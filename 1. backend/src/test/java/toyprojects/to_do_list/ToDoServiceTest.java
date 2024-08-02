package toyprojects.to_do_list;

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
import org.springframework.test.annotation.DirtiesContext;

import toyprojects.to_do_list.constants.TaskStatus;
import toyprojects.to_do_list.entity.ToDoItem;
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
    public void shouldReturnANullWhenAnInvalidItemIsRetrieved() {
        ToDoItem item = new ToDoItem("Cook", "Cook Breakfast");
        toDoService.saveToDoItem(item);
        assertNull(toDoService.getToDoItemById(10L));
    }

    @Test
    public void shouldDeleteAToDoItemUsingId() {
        ToDoItem item = new ToDoItem("Cook", "Cook Breakfast");
        ToDoItem savedItem = toDoService.saveToDoItem(item);
        ToDoItem getItem = toDoService.getToDoItemById(savedItem.getId());
        assertNotNull(getItem);
        toDoService.deleteToDoItem(getItem.getId());
        ToDoItem getDeletedItem = toDoService.getToDoItemById(savedItem.getId());
        assertNull(getDeletedItem);
    }   

    @Test
    public void shouldUpdateTaskStatusToCompleted() {
        ToDoItem item = new ToDoItem("Cook", "Cook Breakfast");
        ToDoItem savedItem = toDoService.saveToDoItem(item);
        assertEquals(item.getStatus(), savedItem.getStatus());
        ToDoItem updatedItem = toDoService.taskCompleted(savedItem.getId());
        assertEquals(TaskStatus.COMPLETED, updatedItem.getStatus());
        assertEquals(updatedItem.getId(), savedItem.getId());
    }

    @Test
    public void shouldReturnAllToDoListItems() {
        List<ToDoItem> toDoList = new ArrayList<>();
        toDoList.add(new ToDoItem("Cook", "Cook Adobo"));
        toDoList.add(new ToDoItem("Eat", "Eat Adobo"));
        toDoList.add(new ToDoItem("Wash", "Wash Dishes"));     
        
        toDoRepository.saveAll(toDoList);
        List<ToDoItem> getAllList = toDoService.getAllToDoItems();
        
        assertEquals(3, getAllList.size());
        assertEquals(toDoList.get(0).getTitle(), getAllList.get(0).getTitle());
        assertEquals(toDoList.get(2).getDescription(), getAllList.get(2).getDescription());
    }

    @Test
    public void shouldNotReturnAnInvalidToDoListItems() {
        List<ToDoItem> nullList = toDoService.getAllToDoItems();
        System.out.println("See List: " + nullList);
        assertThat(nullList).isEmpty();
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionForPassingInvalidInput() {
        ToDoItem invalidItem = new ToDoItem("  ", null);
        assertThrows(IllegalArgumentException.class, () -> toDoService.saveToDoItem(invalidItem));
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionForPassingInvalidInputs() {
        List<ToDoItem> toDoList = new ArrayList<>();
        toDoList.add(new ToDoItem(null, null));
        toDoList.add(new ToDoItem(null, null));
        toDoList.add(new ToDoItem(null, null));
        assertThrows(IllegalArgumentException.class, () -> toDoService.saveAllToDoItems(toDoList));
    }
}