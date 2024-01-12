package org.acme.awesomepizza.web;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@ControllerAdvice
public class RestValidationExceptionHandler extends ResponseEntityExceptionHandler {

    // Default codes as examples, in real scenarios we should have a more detailed code

    @ExceptionHandler(value = {ValidationException.class})
    public ResponseEntity<List<GeneralError>> handleExceptions(ValidationException ex) {
        return ResponseEntity.badRequest().body(List.of(new GeneralError("0001", ex.getMessage())));
    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ResponseEntity<List<GeneralError>> handleExceptions(ConstraintViolationException ex) {
        return ResponseEntity.badRequest()
                .body(
                        ex.getConstraintViolations()
                                .stream()
                                .map(violation -> new GeneralError("0002", violation.getMessage()))
                                .toList()
                );
    }
}
