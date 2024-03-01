package kb.persondata.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionDto> handleMethodArgumentNotValidException(ConstraintViolationException ex) {
        ValidationExceptionDto exDto = new ValidationExceptionDto();
        ex.getConstraintViolations().forEach(fieldError ->
                exDto.addViolation(fieldError.getPropertyPath().toString(), fieldError.getMessageTemplate()));
        return new ResponseEntity<>(exDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionDto> handleSQLIntegrityViolation(DataIntegrityViolationException ex) {
        ExceptionDto exceptionDto = new ExceptionDto(ex.getMessage());
        return new ResponseEntity<>(exceptionDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionDto> handleNotFoundException(EntityNotFoundException ex) {
        ExceptionDto exceptionDto = new ExceptionDto(ex.getMessage());
        return new ResponseEntity<>(exceptionDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionDto> handleNotFoundException(IllegalArgumentException ex) {
        ExceptionDto exceptionDto = new ExceptionDto(ex.getMessage());
        return new ResponseEntity<>(exceptionDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ExceptionDto> handleIllegalStateException(IllegalStateException ex) {
        ExceptionDto exceptionDto = new ExceptionDto(ex.getMessage());
        return new ResponseEntity<>(exceptionDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ExceptionDto> handleOptimisticLockingFailure(ObjectOptimisticLockingFailureException ex) {
        ExceptionDto exceptionDto = new ExceptionDto("An error occurred while processing your request. " +
                "Another user has modified the data you were attempting to update. Please try again. " + ex.getMessage());
        return new ResponseEntity<>(exceptionDto, HttpStatus.CONFLICT);
    }
}
