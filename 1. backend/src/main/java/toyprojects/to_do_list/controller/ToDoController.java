package toyprojects.to_do_list.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        return ResponseEntity.ok(getToDoItem);   
    }

}
