package toyprojects.to_do_list.exception;

public class ToDoItemNotFoundException extends RuntimeException {

    public ToDoItemNotFoundException(Long id) {
        super("Id " + id + " Not Found");
    }
    
}
