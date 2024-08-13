package toyprojects.to_do_list.controller;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.validation.Valid;
import toyprojects.to_do_list.entity.ToDoItem;
import toyprojects.to_do_list.service.ToDoService;



@RestController
@RequestMapping("/todo")
public class ToDoController {
    
    private final ToDoService toDoService;

    public ToDoController(ToDoService toDoService) {
        this.toDoService = toDoService;
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<ToDoItem> getToDoItem(@PathVariable Long requestedId) {
        ToDoItem getToDoItem = toDoService.getToDoItemById(requestedId);
        if (getToDoItem == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(getToDoItem);   
    }

    @GetMapping("/all")
    public ResponseEntity<Page<ToDoItem>> getAllToDoItem(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "id,asc") String[] sort) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(sort[0]).ascending());
        Page<ToDoItem> toDoItemsPage = toDoService.getAllToDoItems(pageable);
        return ResponseEntity.ok(toDoItemsPage);
    }

    @PostMapping
    public ResponseEntity<Void> createToDoItem(@Valid @RequestBody ToDoItem todo) {
        ToDoItem savedToDoItem = toDoService.saveToDoItem(todo);
        URI locationofNewToDoItem = UriComponentsBuilder
            .fromPath("/todo/{id}")
            .buildAndExpand(savedToDoItem.getId())
            .toUri();
        return ResponseEntity.created(locationofNewToDoItem).build();
    }
    
    @PutMapping("/{id}/statusUpdate")
    public ResponseEntity<Void> updateToDoItem(@PathVariable Long id) {
        toDoService.changeToDoStatus(id);      
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateToDoItem(@PathVariable Long id, @Valid @RequestBody ToDoItem toDoItem) {
        toDoService.updateToDoItem(id, toDoItem);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteToDoItem(@PathVariable Long id) {
        toDoService.deleteToDoItem(id);
        return ResponseEntity.noContent().build();
    }

}
