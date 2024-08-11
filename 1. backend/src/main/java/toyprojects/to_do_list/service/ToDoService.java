package toyprojects.to_do_list.service;

import java.util.List;

import toyprojects.to_do_list.entity.ToDoItem;

public interface ToDoService {
    ToDoItem getToDoItemById(Long id);
    ToDoItem saveToDoItem(ToDoItem todo);
    void deleteToDoItem(Long id);
    ToDoItem changeToDoStatus(Long id);
    List<ToDoItem> getAllToDoItems();
    void saveAllToDoItems(List<ToDoItem> toDoList);
}
