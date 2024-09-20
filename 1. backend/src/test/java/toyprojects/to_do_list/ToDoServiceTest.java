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

import toyprojects.to_do_list.entity.ToDoItem;
import toyprojects.to_do_list.entity.ToDoItemRequest;
import toyprojects.to_do_list.exception.ToDoItemNotFoundException;
import toyprojects.to_do_list.exception.ToDoItemValidationException;
import toyprojects.to_do_list.repository.ToDoRepository;
import toyprojects.to_do_list.service.ToDoService;

@SpringBootTest
@DirtiesContext(classMode=DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@WithMockJwt(subject="Ramyr")
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
        ToDoItemRequest item = new ToDoItemRequest("Cook", "Cook Breakfast");
        ToDoItem savedItem = toDoService.saveToDoItem(item);
        ToDoItem getItem = toDoService.getToDoItemById(savedItem.getId());
        assertNotNull(getItem);
        assertEquals(savedItem.getTitle(), getItem.getTitle());
        assertEquals(savedItem.getDescription(), getItem.getDescription());
        assertEquals(savedItem.getStatus(), getItem.getStatus());
        assertEquals(savedItem.getCreatedAt(), LocalDate.now());
        assertNull(savedItem.getCompletedAt());
        assertEquals(savedItem.getOwner(), getItem.getOwner());
    }

    @Test
    public void shouldNotCreateAnInvalidToDoItemWhenSaved() {
        ToDoItemRequest item = new ToDoItemRequest(null, null);
        ToDoItemValidationException ex = assertThrows(ToDoItemValidationException.class, () -> toDoService.saveToDoItem(item));
        assertEquals("Title cannot be null or empty", ex.getMessage());
    }

    @Test
    public void shouldSaveAllToDoItems() {
        List<ToDoItemRequest> toDoList = new ArrayList<>();
        toDoList.add(new ToDoItemRequest("Cook", "Cook Adobo"));
        toDoList.add(new ToDoItemRequest("Eat", "Eat Adobo"));
        toDoList.add(new ToDoItemRequest("DishWash", "Wash Dishes"));     
        toDoService.saveAllToDoItems(toDoList);
        assertEquals(3, toDoRepository.count());
    }

    @Test
    public void shouldNotSaveToDoItemsWithInvalidInput() {
        List<ToDoItemRequest> toDoList = new ArrayList<>();
        toDoList.add(new ToDoItemRequest(" ", "Cook Adobo"));
        toDoList.add(new ToDoItemRequest(null, "Eat Adobo"));
        toDoList.add(new ToDoItemRequest("DishWash", "Wash Dishes"));
        Exception ex = assertThrows(ToDoItemValidationException.class, () -> toDoService.saveAllToDoItems(toDoList));
        assertEquals("Title cannot be null or empty", ex.getMessage());
    }
    
    @Test
    public void shouldDeleteAToDoItemUsingId() {
        ToDoItemRequest item = new ToDoItemRequest("Cook", "Cook Breakfast");
        ToDoItem savedItem = toDoService.saveToDoItem(item);
        toDoService.deleteToDoItem(savedItem.getId());
        ToDoItemNotFoundException ex = assertThrows(ToDoItemNotFoundException.class, () -> toDoService.getToDoItemById(savedItem.getId()));
        assertEquals("Id Not Found", ex.getMessage());
    }  

    @Test
    public void shouldDeleteAllToDoItems() {
        List<ToDoItemRequest> toDoList = new ArrayList<>();
        toDoList.add(new ToDoItemRequest("Cook", "Cook Adobo"));
        toDoList.add(new ToDoItemRequest("Eat", "Eat Adobo"));
        toDoList.add(new ToDoItemRequest("DishWash", "Wash Dishes"));     
        toDoService.saveAllToDoItems(toDoList);
        assertEquals(toDoList.get(0).getTitle(), toDoService.getToDoItemById(1L).getTitle());
        toDoService.deleteAllToDoItems();
        assertThat(toDoRepository.count()).isEqualTo(0);
    }
    
    @Test
    public void shouldThrowErrorWhenAnInvalidIdIsRetrieved() {
        Long nonExistentId = 10000L;
        ToDoItemNotFoundException ex = assertThrows(ToDoItemNotFoundException.class, () -> toDoService.getToDoItemById(nonExistentId));
        assertEquals("Id Not Found", ex.getMessage());
    }

    @Test
    public void shouldUpdateTaskStatusToCompleted() {
        ToDoItemRequest item = new ToDoItemRequest("Cook", "Cook Breakfast");
        ToDoItem savedItem = toDoService.saveToDoItem(item);
        ToDoItem getItem = toDoService.getToDoItemById(savedItem.getId());
        assertEquals(savedItem.getStatus(), getItem.getStatus());
        assertEquals(savedItem.getCreatedAt(), getItem.getCreatedAt());
        assertNull(getItem.getCompletedAt());
        
        ToDoItem updatedItem = toDoService.changeToDoStatus(savedItem.getId());
        ToDoItem getUpdatedItem = toDoService.getToDoItemById(updatedItem.getId());
        assertEquals(updatedItem.getStatus(), getUpdatedItem.getStatus());
        assertEquals(updatedItem.getId(), getUpdatedItem.getId());
        assertEquals(updatedItem.getCompletedAt(), getUpdatedItem.getCompletedAt());
    }

    @Test
    public void shouldReturnTaskStatusToPending() {
        ToDoItemRequest item = new ToDoItemRequest("Cook", "Cook Breakfast");
        ToDoItem savedItem = toDoService.saveToDoItem(item);
        ToDoItem getItem = toDoService.getToDoItemById(savedItem.getId());
        assertEquals(savedItem.getStatus(), getItem.getStatus());
        assertEquals(getItem.getCreatedAt(), getItem.getCreatedAt());
        assertNull(getItem.getCompletedAt());

        ToDoItem updatedItem = toDoService.changeToDoStatus(savedItem.getId());
        ToDoItem getUpdatedItem = toDoService.getToDoItemById(updatedItem.getId());
        assertEquals(updatedItem.getStatus(), getUpdatedItem.getStatus());
        assertEquals(updatedItem.getCompletedAt(), getUpdatedItem.getCompletedAt());
        
        ToDoItem revertItemToPending = toDoService.changeToDoStatus(savedItem.getId());
        ToDoItem getRevertItemToPending = toDoService.getToDoItemById(revertItemToPending.getId());
        assertEquals(revertItemToPending.getStatus(), getRevertItemToPending.getStatus());
        assertNull(revertItemToPending.getCompletedAt());
    }

    @Test
    public void shouldReturnAllToDoListItems() {
        List<ToDoItemRequest> toDoList = new ArrayList<>();
        toDoList.add(new ToDoItemRequest("Cook", "Cook Adobo"));
        toDoList.add(new ToDoItemRequest("Eat", "Eat Adobo"));
        toDoList.add(new ToDoItemRequest("DishWash", "Wash Dishes"));     
        toDoService.saveAllToDoItems(toDoList);

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
        ToDoItemRequest invalidItem = new ToDoItemRequest("  ", null);
        ToDoItemValidationException exception = assertThrows(ToDoItemValidationException.class, () -> toDoService.saveToDoItem(invalidItem));
        assertEquals("Title cannot be null or empty", exception.getMessage());
    }

    @Test
    public void shouldThrowToDoItemValidationExceptionForPassingInvalidInputs() {
        List<ToDoItemRequest> toDoList = new ArrayList<>();
        toDoList.add(new ToDoItemRequest(null, null));
        toDoList.add(new ToDoItemRequest(null, null));
        toDoList.add(new ToDoItemRequest(null, null));
        ToDoItemValidationException exception = assertThrows(ToDoItemValidationException.class, () -> toDoService.saveAllToDoItems(toDoList));
        assertEquals("Title cannot be null or empty", exception.getMessage()); 
    }

    @Test
    public void shouldUpdateAValidToDoItem() {
        ToDoItemRequest todo = new ToDoItemRequest("Read", "Read HeadFirst Java");
        ToDoItem savedToDoItem = toDoService.saveToDoItem(todo);
        
        ToDoItem updatedToDoItem = toDoService.updateToDoItem(savedToDoItem.getId(), new ToDoItemRequest("Read and Code", "Practice while reading HeadFirst Java"));
        ToDoItem getupdatedToDoItem = toDoService.getToDoItemById(updatedToDoItem.getId());
        
        assertEquals(updatedToDoItem.getId(), getupdatedToDoItem.getId());
        assertEquals(updatedToDoItem.getTitle(), getupdatedToDoItem.getTitle());
        assertEquals(updatedToDoItem.getDescription(), getupdatedToDoItem.getDescription());
        assertEquals(updatedToDoItem.getStatus(), getupdatedToDoItem.getStatus());
    }

    @Test
    public void shouldNotUpdateANonExistentId() {
        ToDoItemRequest updatedToDoItem = new ToDoItemRequest("Read and Code", "Practice while reading HeadFirst Java");
        ToDoItemNotFoundException ex = assertThrows(ToDoItemNotFoundException.class, () -> toDoService.updateToDoItem(1000L, updatedToDoItem));
        assertEquals("Id Not Found", ex.getMessage());
    }

    @Test
    public void shouldNotUpdateAnInvalidToDoItem() {
        ToDoItemRequest todo = new ToDoItemRequest("Read", "Read HeadFirst Java");
        toDoService.saveToDoItem(todo);
        ToDoItemValidationException ex = assertThrows(ToDoItemValidationException.class, () -> toDoService.updateToDoItem(1L, new ToDoItemRequest(" ", "Practice while reading HeadFirst Java")));
        assertEquals("Title cannot be null or empty", ex.getMessage());
    }

    @Test
    public void shouldNotUpdateWithAnInvalidUpdateId() {
        ToDoItemRequest todo = new ToDoItemRequest("Read", "Read HeadFirst Java");
        toDoService.saveToDoItem(todo);
        ToDoItemNotFoundException ex = assertThrows(ToDoItemNotFoundException.class, () -> toDoService.updateToDoItem(1000L, new ToDoItemRequest("Read", "Practice while reading HeadFirst Java")));
        assertEquals("Id Not Found", ex.getMessage());
    }
}