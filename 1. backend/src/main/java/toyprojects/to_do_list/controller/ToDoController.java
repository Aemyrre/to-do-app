package toyprojects.to_do_list.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

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
    public ResponseEntity<ToDoItem> getMethodName(@PathVariable Long requestedId) {
        ToDoItem getToDoItem = toDoService.getToDoItemById(requestedId);
        if (getToDoItem == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(getToDoItem);   
    }

    @PostMapping
    public ResponseEntity<Void> createToDoItem(@RequestBody ToDoItem todo) {
        ToDoItem savedToDoItem = toDoService.saveToDoItem(todo);
        URI locationofNewToDoItem = UriComponentsBuilder
            .fromPath("/todo/{id}")
            .buildAndExpand(savedToDoItem.getId())
            .toUri();
        return ResponseEntity.created(locationofNewToDoItem).build();
    }
    
    @PutMapping("/{id}/statusUpdate")
    public ResponseEntity<ToDoItem> updateToDoItem(@PathVariable Long id) {
        ToDoItem updatedToDoItem = toDoService.changeToDoStatus(id);      
        return ResponseEntity.ok(updatedToDoItem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteToDoItem(@PathVariable Long id) {
        toDoService.deleteToDoItem(id);
        return ResponseEntity.noContent().build();
    }

}
