package toyprojects.to_do_list.exception;

public class InvalidInputException extends RuntimeException {
    
    public InvalidInputException () {
        super("Invalid input");
    }
}
