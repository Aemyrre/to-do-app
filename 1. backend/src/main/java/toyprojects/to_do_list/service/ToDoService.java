package toyprojects.to_do_list.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import toyprojects.to_do_list.entity.ToDoItem;

public interface ToDoService {
    ToDoItem getToDoItemById(Long id);
    ToDoItem saveToDoItem(ToDoItem todo);
    void deleteToDoItem(Long id);
    ToDoItem changeToDoStatus(Long id);
    Page<ToDoItem> getAllToDoItems(Pageable pageable);
    void saveAllToDoItems(List<ToDoItem> toDoList);
}
