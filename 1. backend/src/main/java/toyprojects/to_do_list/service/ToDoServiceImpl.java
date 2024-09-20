package toyprojects.to_do_list.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import toyprojects.to_do_list.constants.TaskStatus;
import toyprojects.to_do_list.entity.ToDoItem;
import toyprojects.to_do_list.entity.ToDoItemRequest;
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
    public ToDoItem saveToDoItem(ToDoItemRequest todo) {
        validateToDoItem(todo);
        ToDoItem toDoItem = new ToDoItem(todo.getTitle(), todo.getDescription(), getCurrentUserSubject());
        return toDoRepository.save(toDoItem);
    }

    @Override
    @Transactional
    public void saveAllToDoItems(List<ToDoItemRequest> toDoList) {
        List<ToDoItem> validatedToDoList = new ArrayList<>();
        for (ToDoItemRequest toDoItem : toDoList) {
            validateToDoItem(toDoItem);
            validatedToDoList.add(new ToDoItem(toDoItem.getTitle(), toDoItem.getDescription(), getCurrentUserSubject()));
        }
        toDoRepository.saveAll(validatedToDoList);
    }

    @Override
    @Transactional(readOnly = true)
    public ToDoItem getToDoItemById(Long id) {
        Optional<ToDoItem> toDoItem = toDoRepository.findByIdAndOwner(id, getCurrentUserSubject());
        return toDoItem.orElseThrow(() -> new ToDoItemNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ToDoItem> getAllToDoItems(Pageable pageable) {
        return toDoRepository.findAll(pageable, getCurrentUserSubject());
    }

    @Override
    @Transactional
    public void deleteToDoItem(Long id) {
        if (toDoRepository.existsByIdAndOwner(id, getCurrentUserSubject())) {
            toDoRepository.deleteById(id);
        } else {
            throw new ToDoItemNotFoundException(id);
        }
    }

    @Override
    @Transactional
    public void deleteAllToDoItems() {
        toDoRepository.deleteAllByOwner(getCurrentUserSubject());       
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
    public ToDoItem updateToDoItem(Long id, ToDoItemRequest toDoItem) {
        validateToDoItem(toDoItem);
        ToDoItem updatedToDoItem = new ToDoItem(id, toDoItem.getTitle(), toDoItem.getDescription(), getCurrentUserSubject());
        
        if (!toDoRepository.existsByIdAndOwner(id, getCurrentUserSubject())) {
            throw new ToDoItemNotFoundException(id);
        }

        return toDoRepository.save(updatedToDoItem);
    }

    /* depracated method */ 
    // private void validateToDoItem(ToDoItem todo) {
    //     if (todo.getTitle() == null || todo.getTitle().isBlank()) {
    //         throw new ToDoItemValidationException();
    //     }
    // }

    private void validateToDoItem(ToDoItemRequest todo) {
        if (todo.getTitle() == null || todo.getTitle().isBlank()) {
            throw new ToDoItemValidationException();
        }
    }

    // Helper method to get the `sub` claim from the current authentication
    @Override
    public String getCurrentUserSubject() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            Jwt jwt = jwtAuthenticationToken.getToken();
            return jwt.getSubject(); // fetches the `sub` claim from the JWT
        }
        throw new IllegalStateException("Expected JwtAuthenticationToken");
    }
}
