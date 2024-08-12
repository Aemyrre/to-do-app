package toyprojects.to_do_list.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import toyprojects.to_do_list.constants.TaskStatus;
import toyprojects.to_do_list.entity.ToDoItem;
import toyprojects.to_do_list.exception.ToDoItemNotFoundException;
import toyprojects.to_do_list.repository.ToDoRepository;
import toyprojects.to_do_list.validation.ToDoItemValidation;

@Service
public class ToDoServiceImpl extends ToDoItemValidation implements ToDoService {

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
        return toDoItem.orElseThrow(() -> new ToDoItemNotFoundException(id));
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
        if (toDoRepository.findById(id).isPresent()) {
            toDoRepository.deleteById(id);
        } else {
            throw new ToDoItemNotFoundException(id);
        }
    }

    @Override
    @Transactional
    public ToDoItem changeToDoStatus(Long id) {
        ToDoItem item = getToDoItemById(id);
        if (item.getStatus() == TaskStatus.PENDING) {
            item.setStatus(TaskStatus.COMPLETED);
        } else {
            item.setStatus(TaskStatus.PENDING);
        }
        return item;
    }
}
