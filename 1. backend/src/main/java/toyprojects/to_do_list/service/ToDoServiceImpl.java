package toyprojects.to_do_list.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import toyprojects.to_do_list.constants.TaskStatus;
import toyprojects.to_do_list.entity.ToDoItem;
import toyprojects.to_do_list.repository.ToDoRepository;

@Service
public class ToDoServiceImpl implements ToDoService {

    private final ToDoRepository toDoRepository;

    public ToDoServiceImpl(ToDoRepository toDoRepository) {
        this.toDoRepository = toDoRepository;
    }

    @Override
    @Transactional
    public ToDoItem saveToDoItem(ToDoItem todo) {
        validateToDoItem(todo);
        return toDoRepository.save(todo);
    }

    @Override
    @Transactional
    public void saveAllToDoItems(List<ToDoItem> toDoList) {
        for (ToDoItem toDoItem : toDoList) {
            validateToDoItem(toDoItem);
        }
        toDoRepository.saveAll(toDoList);
    }

    @Override
    @Transactional(readOnly = true)
    public ToDoItem getToDoItemById(Long id) {
        Optional<ToDoItem> toDoItem = toDoRepository.findById(id);
        return toDoItem.orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ToDoItem> getAllToDoItems() {
        List<ToDoItem> toDoList = (List<ToDoItem>) toDoRepository.findAll();
        return toDoList;
    }

    @Override
    @Transactional
    public void deleteToDoItem(Long id) {
        toDoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ToDoItem taskCompleted(Long id) {
        ToDoItem item = getToDoItemById(id);
        item.setStatus(TaskStatus.COMPLETED);
        return item;
    }

    private void validateToDoItem(ToDoItem todo) {
        if (todo.getTitle() == null || todo.getTitle().isBlank()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
    }

}
