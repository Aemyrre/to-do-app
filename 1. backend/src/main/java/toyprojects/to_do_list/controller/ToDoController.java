package toyprojects.to_do_list.controller;

import java.net.URI;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
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
import toyprojects.to_do_list.entity.CurrentOwner;
import toyprojects.to_do_list.entity.ToDoItem;
import toyprojects.to_do_list.entity.ToDoItemRequest;
import toyprojects.to_do_list.service.ToDoService;



@RestController
@RequestMapping("/todo")
public class ToDoController {
    
    private final ToDoService toDoService;

    public ToDoController(ToDoService toDoService) {
        this.toDoService = toDoService;
    }

    @PostAuthorize("returnObject.body.owner == principal.attributes['sub']")
    @GetMapping("/{requestedId}")
    public ResponseEntity<ToDoItem> getToDoItem(@PathVariable Long requestedId) {
        ToDoItem getToDoItem = toDoService.getToDoItemById(requestedId);
        if (getToDoItem == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(getToDoItem);   
    }

    @PostAuthorize("returnObject.body.owner == principal.attributes['sub']")
    @GetMapping("/all")
    public ResponseEntity<Page<ToDoItem>> getAllToDoItem(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "id,asc") String[] sort) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(sort[0]).ascending());
        Page<ToDoItem> toDoItemsPage = toDoService.getAllToDoItems(pageable);
        return ResponseEntity.ok(toDoItemsPage);
    }

    @PostMapping
    public ResponseEntity<Void> createToDoItem(@Valid @RequestBody ToDoItemRequest todo, @CurrentOwner String owner) {
        ToDoItem savedToDoItem = toDoService.saveToDoItem(todo, owner);
        URI locationofNewToDoItem = UriComponentsBuilder
            .fromPath("/todo/{id}")
            .buildAndExpand(savedToDoItem.getId())
            .toUri();
        return ResponseEntity.created(locationofNewToDoItem).build();
    }

    @PostMapping("/saveAll")
    public ResponseEntity<Void> saveAllToDoItems(@Valid @RequestBody List<ToDoItemRequest> toDoItems, @CurrentOwner String owner) {
        toDoService.saveAllToDoItems(toDoItems, owner);
        return ResponseEntity.ok().build();
    }
    
    
    @PutMapping("/{id}/statusUpdate")
    public ResponseEntity<Void> updateToDoItem(@PathVariable Long id) {
        toDoService.changeToDoStatus(id);      
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateToDoItem(@PathVariable Long id, @Valid @RequestBody ToDoItem toDoItem, @CurrentOwner String owner) {
        toDoService.updateToDoItem(id, toDoItem, owner);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteToDoItem(@PathVariable Long id) {
        toDoService.deleteToDoItem(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/all") 
    public ResponseEntity<Void> deleteAllToDoItems() {
        toDoService.deleteAllToDoItems();
        return ResponseEntity.noContent().build();
    }

}
