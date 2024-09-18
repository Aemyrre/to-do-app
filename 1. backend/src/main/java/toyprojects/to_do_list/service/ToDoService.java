package toyprojects.to_do_list.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import toyprojects.to_do_list.entity.ToDoItem;
import toyprojects.to_do_list.entity.ToDoItemRequest;

public interface ToDoService {
    ToDoItem getToDoItemById(Long id);
    Page<ToDoItem> getAllToDoItems(Pageable pageable);
    ToDoItem saveToDoItem(ToDoItemRequest todo, String owner);
    void saveAllToDoItems(List<ToDoItemRequest> toDoList, String owner);
    void deleteToDoItem(Long id);
    void deleteAllToDoItems();
    ToDoItem changeToDoStatus(Long id);
    ToDoItem updateToDoItem(Long id, ToDoItem toDoItem, String owner);
}
