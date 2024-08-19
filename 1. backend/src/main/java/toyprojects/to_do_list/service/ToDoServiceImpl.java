package toyprojects.to_do_list.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import toyprojects.to_do_list.constants.TaskStatus;
import toyprojects.to_do_list.entity.ToDoItem;
import toyprojects.to_do_list.exception.ToDoIdValidationException;
import toyprojects.to_do_list.exception.ToDoItemNotFoundException;
import toyprojects.to_do_list.exception.ToDoItemValidationException;
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
        return toDoItem.orElseThrow(() -> new ToDoItemNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ToDoItem> getAllToDoItems(Pageable pageable) {
        return toDoRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public void deleteToDoItem(Long id) {
        if (toDoRepository.existsById(id)) {
            toDoRepository.deleteById(id);
        } else {
            throw new ToDoItemNotFoundException(id);
        }
    }

    @Override
    @Transactional
    public void deleteAllToDoItems() {
        toDoRepository.deleteAll();       
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

    @Override
    @Transactional
    public ToDoItem updateToDoItem(Long id, ToDoItem toDoItem) {
        if (!toDoRepository.existsById(id)) {
            throw new ToDoItemNotFoundException(id);
        }
        
        if (!id.equals(toDoItem.getId()) && toDoItem.getId() != null) {
            throw new ToDoIdValidationException();
        }

        validateToDoItem(toDoItem);
        ToDoItem updatedToDoItem = new ToDoItem(id, toDoItem.getTitle(), toDoItem.getDescription());
        return toDoRepository.save(updatedToDoItem);
    }

    private void validateToDoItem(ToDoItem todo) {
        if (todo.getTitle() == null || todo.getTitle().isBlank()) {
            throw new ToDoItemValidationException();
        }
    }
}
