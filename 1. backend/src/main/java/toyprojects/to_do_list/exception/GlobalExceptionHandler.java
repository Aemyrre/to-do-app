package toyprojects.to_do_list.exception;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ToDoItemValidationException.class)
    public ResponseEntity<ErrorResponse> handleToDoItemValidationException(ToDoItemValidationException ex) {
        ErrorResponse errorResponse = new ErrorResponse("Title cannot be null or empty", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<String> handleTransactionSystemException(TransactionSystemException ex) {
        Throwable cause = ex.getRootCause();
        if (cause instanceof ConstraintViolationException) {
            return new ResponseEntity<>(cause.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Transaction error", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ToDoItemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleToDoItemNotFoundException(ToDoItemNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse("Id Not Found", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ErrorResponse> handleInvalidInputException(InvalidInputException ex) {
        ErrorResponse errorResponse = new ErrorResponse("Input is not valid", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /* Deprecated Method */
    // @ExceptionHandler(ToDoIdValidationException.class)
    // public ResponseEntity<ErrorResponse> handleToDoIdValidationException(ToDoIdValidationException ex) {
    //     ErrorResponse errorResponse = new ErrorResponse("Id not the same", ex.getMessage());
    //     return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    // }
}
