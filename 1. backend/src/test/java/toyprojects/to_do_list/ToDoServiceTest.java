package toyprojects.to_do_list;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;

import toyprojects.to_do_list.constants.TaskStatus;
import toyprojects.to_do_list.entity.ToDoItem;
import toyprojects.to_do_list.exception.ToDoIdValidationException;
import toyprojects.to_do_list.exception.ToDoItemNotFoundException;
import toyprojects.to_do_list.exception.ToDoItemValidationException;
import toyprojects.to_do_list.repository.ToDoRepository;
import toyprojects.to_do_list.service.ToDoService;

@SpringBootTest
@DirtiesContext(classMode=DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ToDoServiceTest {

    @Autowired
    private ToDoRepository toDoRepository;
    
    @Autowired
    private ToDoService toDoService;

    @Test
    void contextLoads() {
        assertNotNull(toDoRepository);
        assertNotNull(toDoService);
    }

    @Test
    public void shouldReturnAToDoItemWhenDataIsSaved() {
        ToDoItem item = new ToDoItem("Cook", "Cook Breakfast");
        ToDoItem savedItem = toDoService.saveToDoItem(item);
        ToDoItem getItem = toDoService.getToDoItemById(savedItem.getId());
        assertNotNull(getItem);
        assertEquals(item.getTitle(), getItem.getTitle());
        assertEquals(item.getDescription(), getItem.getDescription());
        assertEquals(item.getStatus(), getItem.getStatus());
        assertEquals(item.getCreatedAt(), LocalDate.now());
        assertNull(item.getCompletedAt());
    }

    @Test
    public void shouldNotCreateAnInvalidToDoItemWhenSaved() {
        ToDoItem item = new ToDoItem(null, null, null);
        ToDoItemValidationException ex = assertThrows(ToDoItemValidationException.class, () -> toDoService.saveToDoItem(item));
        assertEquals("Title cannot be null or empty", ex.getMessage());
    }

    @Test
    public void shouldSaveAllToDoItems() {
        List<ToDoItem> toDoList = new ArrayList<>();
        toDoList.add(new ToDoItem("Cook", "Cook Adobo"));
        toDoList.add(new ToDoItem("Eat", "Eat Adobo"));
        toDoList.add(new ToDoItem("DishWash", "Wash Dishes"));     
        toDoService.saveAllToDoItems(toDoList);
        assertEquals(3, toDoRepository.count());
    }

    @Test
    public void shouldNotSaveToDoItemsWithInvalidInput() {
        List<ToDoItem> toDoList = new ArrayList<>();
        toDoList.add(new ToDoItem(" ", "Cook Adobo"));
        toDoList.add(new ToDoItem(null, "Eat Adobo"));
        toDoList.add(new ToDoItem("DishWash", "Wash Dishes"));
        Exception ex = assertThrows(ToDoItemValidationException.class, () -> toDoService.saveAllToDoItems(toDoList));
        assertEquals("Title cannot be null or empty", ex.getMessage());
    }
    
    @Test
    public void shouldDeleteAToDoItemUsingId() {
        ToDoItem item = new ToDoItem("Cook", "Cook Breakfast");
        ToDoItem savedItem = toDoService.saveToDoItem(item);
        ToDoItem getItem = toDoService.getToDoItemById(savedItem.getId());
        toDoService.deleteToDoItem(getItem.getId());
        ToDoItemNotFoundException ex = assertThrows(ToDoItemNotFoundException.class, () -> toDoService.getToDoItemById(getItem.getId()));
        assertEquals("Id " + getItem.getId() + " Not Found", ex.getMessage());
    }  

    @Test
    public void shouldDeleteAllToDoItems() {
        List<ToDoItem> toDoList = new ArrayList<>();
        toDoList.add(new ToDoItem("Cook", "Cook Adobo"));
        toDoList.add(new ToDoItem("Eat", "Eat Adobo"));
        toDoList.add(new ToDoItem("DishWash", "Wash Dishes"));     
        toDoRepository.saveAll(toDoList);
        assertEquals(toDoList.get(0).getTitle(), toDoService.getToDoItemById(1L).getTitle());
        toDoService.deleteAllToDoItems();
        assertThat(toDoRepository.count()).isEqualTo(0);
    }
    
    @Test
    public void shouldThrowErrorWhenAnInvalidIdIsRetrieved() {
        Long nonExistentId = 10000L;
        ToDoItemNotFoundException ex = assertThrows(ToDoItemNotFoundException.class, () -> toDoService.getToDoItemById(nonExistentId));
        assertEquals("Id " + nonExistentId + " Not Found", ex.getMessage());
    }

    @Test
    public void shouldUpdateTaskStatusToCompleted() {
        ToDoItem item = new ToDoItem("Cook", "Cook Breakfast");
        ToDoItem savedItem = toDoService.saveToDoItem(item);
        assertEquals(item.getStatus(), savedItem.getStatus());
        assertEquals(savedItem.getCreatedAt(), savedItem.getCreatedAt());
        assertNull(savedItem.getCompletedAt());
        
        ToDoItem updatedItem = toDoService.changeToDoStatus(savedItem.getId());
        assertEquals(TaskStatus.COMPLETED, updatedItem.getStatus());
        assertEquals(savedItem.getId(), updatedItem.getId());
        assertEquals(LocalDate.now(), updatedItem.getCompletedAt());
    }

    @Test
    public void shouldReturnTaskStatusToPending() {
        ToDoItem item = new ToDoItem("Cook", "Cook Breakfast");
        ToDoItem savedItem = toDoService.saveToDoItem(item);
        assertEquals(item.getStatus(), savedItem.getStatus());
        assertEquals(LocalDate.now(), savedItem.getCreatedAt());
        assertNull(savedItem.getCompletedAt());

        ToDoItem updatedItem = toDoService.changeToDoStatus(savedItem.getId());
        assertEquals(TaskStatus.COMPLETED, updatedItem.getStatus());
        assertEquals(LocalDate.now(), updatedItem.getCompletedAt());
        
        ToDoItem revertItemToPending = toDoService.changeToDoStatus(savedItem.getId());
        assertEquals(TaskStatus.PENDING, revertItemToPending.getStatus());
        assertNull(revertItemToPending.getCompletedAt());
    }

    @Test
    public void shouldReturnAllToDoListItems() {
        List<ToDoItem> toDoList = new ArrayList<>();
        toDoList.add(new ToDoItem("Cook", "Cook Adobo"));
        toDoList.add(new ToDoItem("Eat", "Eat Adobo"));
        toDoList.add(new ToDoItem("DishWash", "Wash Dishes"));     
        toDoRepository.saveAll(toDoList);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<ToDoItem> getList = toDoService.getAllToDoItems(pageable);
        List<ToDoItem> allItems = getList.getContent();

        System.out.println("these are the list items:");
        for (ToDoItem toDoItem : allItems) {
            System.out.printf("id:%s, title:%s, description:%s, status:%s%n", toDoItem.getId(), toDoItem.getTitle(), toDoItem.getDescription(), toDoItem.getStatus());
        }

        assertEquals(3, allItems.size());
        assertEquals("Cook", allItems.get(0).getTitle());
        assertEquals(3, allItems.get(2).getId());
        assertEquals("Eat Adobo", allItems.get(1).getDescription());
        assertEquals(1, getList.getTotalPages());
    }

    @Test
    public void shouldNotReturnAnInvalidToDoListItems() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<ToDoItem> nullList = toDoService.getAllToDoItems(pageable);
        System.out.println("See List: " + nullList.getContent());
        assertThat(nullList).isEmpty();
    }

    @Test
    public void shouldThrowToDoItemValidationExceptionForPassingInvalidInput() {
        ToDoItem invalidItem = new ToDoItem("  ", null);
        ToDoItemValidationException exception = assertThrows(ToDoItemValidationException.class, () -> toDoService.saveToDoItem(invalidItem));
        assertEquals("Title cannot be null or empty", exception.getMessage());
    }

    @Test
    public void shouldThrowToDoItemValidationExceptionForPassingInvalidInputs() {
        List<ToDoItem> toDoList = new ArrayList<>();
        toDoList.add(new ToDoItem(null, null));
        toDoList.add(new ToDoItem(null, null));
        toDoList.add(new ToDoItem(null, null));
        ToDoItemValidationException exception = assertThrows(ToDoItemValidationException.class, () -> toDoService.saveAllToDoItems(toDoList));
        assertEquals("Title cannot be null or empty", exception.getMessage()); 
    }

    @Test
    public void shouldUpdateAValidToDoItem() {
        ToDoItem todo = new ToDoItem("Read", "Read HeadFirst Java");
        ToDoItem savedToDoItem = toDoRepository.save(todo);

        ToDoItem updatedToDoItem = new ToDoItem(1L, "Read and Code", "Practice while reading HeadFirst Java");
        ToDoItem newSavedToDoItem = toDoService.updateToDoItem(savedToDoItem.getId(), updatedToDoItem);
        
        assertEquals(savedToDoItem.getId(), newSavedToDoItem.getId());
        assertEquals(updatedToDoItem.getTitle(), newSavedToDoItem.getTitle());
        assertEquals(updatedToDoItem.getDescription(), newSavedToDoItem.getDescription());
        assertEquals(updatedToDoItem.getStatus(), newSavedToDoItem.getStatus());
    }

    @Test
    public void shouldNotUpdateANonExistentId() {
        ToDoItem updatedToDoItem = new ToDoItem(null,"Read and Code", "Practice while reading HeadFirst Java");
        ToDoItemNotFoundException ex = assertThrows(ToDoItemNotFoundException.class, () -> toDoService.updateToDoItem(1000L, updatedToDoItem));
        assertEquals("Id 1000 Not Found", ex.getMessage());
    }

    @Test
    public void shouldNotUpdateAnInvalidToDoItem() {
        ToDoItem todo = new ToDoItem("Read", "Read HeadFirst Java");
        toDoRepository.save(todo);
        ToDoItem updatedToDoItem = new ToDoItem(1L, " ", "Practice while reading HeadFirst Java");
        ToDoItemValidationException ex = assertThrows(ToDoItemValidationException.class, () -> toDoService.updateToDoItem(1L, updatedToDoItem));
        assertEquals("Title cannot be null or empty", ex.getMessage());
    }

    @Test
    public void shouldNotUpdateWithAnInvalidUpdateId() {
        ToDoItem todo = new ToDoItem("Read", "Read HeadFirst Java");
        toDoRepository.save(todo);
        ToDoItem updatedToDoItem = new ToDoItem(1000L, " ", "Practice while reading HeadFirst Java");
        ToDoIdValidationException ex = assertThrows(ToDoIdValidationException.class, () -> toDoService.updateToDoItem(1L, updatedToDoItem));
        assertEquals("Ids' are not the same.", ex.getMessage());
    }
}