package toyprojects.to_do_list.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import toyprojects.to_do_list.entity.ToDoItem;
import toyprojects.to_do_list.entity.ToDoItemRequest;

public interface ToDoService {
    ToDoItem getToDoItemById(Long id);
    Page<ToDoItem> getAllToDoItems(Pageable pageable);
    ToDoItem saveToDoItem(ToDoItemRequest todo);
    void saveAllToDoItems(List<ToDoItemRequest> toDoList);
    void deleteToDoItem(Long id);
    void deleteAllToDoItems();
    ToDoItem changeToDoStatus(Long id);
    ToDoItem updateToDoItem(Long id, ToDoItemRequest toDoItem);
    String getCurrentUserSubject();
}
