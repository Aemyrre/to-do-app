package toyprojects.to_do_list.validation;

import toyprojects.to_do_list.entity.ToDoItem;
import toyprojects.to_do_list.exception.ToDoItemValidationException;

public abstract class ToDoItemValidation {

    public static void validateToDoItem(ToDoItem todo) {
        if (todo.getTitle() == null || todo.getTitle().isBlank()) {
            throw new ToDoItemValidationException();
        }
    }
}
