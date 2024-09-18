package toyprojects.to_do_list.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import toyprojects.to_do_list.constants.TaskStatus;
import toyprojects.to_do_list.entity.ToDoItem;
import toyprojects.to_do_list.entity.ToDoItemRequest;
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
    public ToDoItem saveToDoItem(ToDoItemRequest todo, String owner) {
        validateToDoItem(todo);
        ToDoItem toDoItem = new ToDoItem(todo.getTitle(), todo.getDescription(), owner);
        return toDoRepository.save(toDoItem);
    }

    @Override
    @Transactional
    public void saveAllToDoItems(List<ToDoItemRequest> toDoList, String owner) {
        List<ToDoItem> validatedToDoList = new ArrayList<>();
        for (ToDoItemRequest toDoItem : toDoList) {
            validateToDoItem(toDoItem);
            validatedToDoList.add(new ToDoItem(toDoItem.getTitle(), toDoItem.getDescription(), owner));
        }
        toDoRepository.saveAll(validatedToDoList);
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
        if (toDoRepository.existsByIdAndOwner(id)) {
            toDoRepository.deleteById(id);
        } else {
            throw new ToDoItemNotFoundException(id);
        }
    }

    @Override
    @Transactional
    public void deleteAllToDoItems() {
        toDoRepository.deleteAllByOwner();       
    }

    @Override
    @Transactional
    public ToDoItem changeToDoStatus(Long id) {
        ToDoItem item = getToDoItemById(id);
        if (item.getStatus() == TaskStatus.PENDING) {
            item.setStatus(TaskStatus.COMPLETED);
            item.setCompletedAt();
        } else {
            item.setStatus(TaskStatus.PENDING);
            item.setCompletedAt();
        }
        return item;
    }

    @Override
    @Transactional
    public ToDoItem updateToDoItem(Long id, ToDoItem toDoItem, String owner) {
        if (!toDoRepository.existsByIdAndOwner(id)) {
            throw new ToDoItemNotFoundException(id);
        }
        
        if (!id.equals(toDoItem.getId()) && toDoItem.getId() != null) {
            throw new ToDoIdValidationException();
        }

        validateToDoItem(toDoItem);
        ToDoItem updatedToDoItem = new ToDoItem(id, toDoItem.getTitle(), toDoItem.getDescription(), owner);
        return toDoRepository.save(updatedToDoItem);
    }

    private void validateToDoItem(ToDoItem todo) {
        if (todo.getTitle() == null || todo.getTitle().isBlank()) {
            throw new ToDoItemValidationException();
        }
    }

    private void validateToDoItem(ToDoItemRequest todo) {
        if (todo.getTitle() == null || todo.getTitle().isBlank()) {
            throw new ToDoItemValidationException();
        }
    }
}
