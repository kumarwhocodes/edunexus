package dev.kumar.edunexus.exception;

import dev.kumar.edunexus.dto.CustomResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    

    @ExceptionHandler(FirebaseOperationException.class)
    public ResponseEntity<CustomResponse<String>> handleFirebaseOperationException(FirebaseOperationException e) {
        return new ResponseEntity<>(
                new CustomResponse<>(
                        HttpStatus.UNAUTHORIZED,
                        e.getMessage(),
                        "Error deleting user from firebase."
                ),
                HttpStatus.UNAUTHORIZED
        );
    }
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CustomResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new CustomResponse<>(HttpStatus.NOT_FOUND, ex.getMessage(), null));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomResponse<Map<String, String>>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new CustomResponse<>(HttpStatus.BAD_REQUEST, "Validation Failed", errors));
    }
    
    //MethodArgumentTypeMismatchException -- UUID
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<CustomResponse<String>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        return new ResponseEntity<>(
                new CustomResponse<>(HttpStatus.BAD_REQUEST, "Invalid UUID format", "ERROR"),
                HttpStatus.BAD_REQUEST
        );
    }
    
    //Conflict Exception
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<CustomResponse<Void>> handleConflictException(ConflictException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new CustomResponse<>(HttpStatus.CONFLICT, ex.getMessage(), null));
    }
    
    // IllegalStateException handler
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<CustomResponse<String>> handleIllegalStateException(IllegalStateException ex) {
        return new ResponseEntity<>(
                new CustomResponse<>(HttpStatus.BAD_REQUEST, ex.getMessage(), "ERROR"),
                HttpStatus.BAD_REQUEST
        );
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CustomResponse<String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(
                new CustomResponse<>(HttpStatus.BAD_REQUEST, ex.getMessage(), "ERROR"),
                HttpStatus.BAD_REQUEST
        );
    }
    
    // Handle NullPointerException
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<CustomResponse<String>> handleNullPointerException(NullPointerException ex) {
        return new ResponseEntity<>(
                new CustomResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, "Null pointer exception occurred", "ERROR"),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
    
    // Handle DataIntegrityViolationException for database issues
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<CustomResponse<String>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        return new ResponseEntity<>(
                new CustomResponse<>(HttpStatus.CONFLICT, "Data integrity violation", "ERROR"),
                HttpStatus.CONFLICT
        );
    }
    
    // Generic Exception handler for unforeseen errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomResponse<String>> handleGenericException(Exception ex) {
        return new ResponseEntity<>(
                new CustomResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong.", "ERROR"),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
