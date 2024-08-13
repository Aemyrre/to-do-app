package toyprojects.to_do_list.exception;

public class ToDoItemValidationException extends RuntimeException{

    public ToDoItemValidationException() {
        super("Title cannot be null or empty");
    }
}
