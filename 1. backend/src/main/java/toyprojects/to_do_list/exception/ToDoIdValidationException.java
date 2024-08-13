package toyprojects.to_do_list.exception;

public class ToDoIdValidationException extends RuntimeException {
    

    public ToDoIdValidationException() {
        super("Ids' are not the same.");
    }

}
