package toyprojects.to_do_list;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import toyprojects.to_do_list.exception.ToDoItemNotFoundException;
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
        ToDoItem updatedItem = toDoService.changeToDoStatus(savedItem.getId());
        assertEquals(TaskStatus.COMPLETED, updatedItem.getStatus());
        assertEquals(updatedItem.getId(), savedItem.getId());
    }

    @Test
    public void shouldReturnTaskStatusToPending() {
        ToDoItem item = new ToDoItem("Cook", "Cook Breakfast");
        ToDoItem savedItem = toDoService.saveToDoItem(item);
        assertEquals(item.getStatus(), savedItem.getStatus());
        ToDoItem updatedItem = toDoService.changeToDoStatus(savedItem.getId());
        assertEquals(TaskStatus.COMPLETED, updatedItem.getStatus());
        ToDoItem revertItemToPending = toDoService.changeToDoStatus(savedItem.getId());
        assertEquals(TaskStatus.PENDING, revertItemToPending.getStatus());
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
    public void shouldThrowIllegalArgumentExceptionForPassingInvalidInput() {
        ToDoItem invalidItem = new ToDoItem("  ", null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> toDoService.saveToDoItem(invalidItem));
        assertEquals("Title cannot be null or empty", exception.getMessage());
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionForPassingInvalidInputs() {
        List<ToDoItem> toDoList = new ArrayList<>();
        toDoList.add(new ToDoItem(null, null));
        toDoList.add(new ToDoItem(null, null));
        toDoList.add(new ToDoItem(null, null));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> toDoService.saveAllToDoItems(toDoList));
        assertEquals("Title cannot be null or empty", exception.getMessage()); 
    }
}