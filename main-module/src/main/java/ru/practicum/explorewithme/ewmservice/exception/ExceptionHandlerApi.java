package ru.practicum.explorewithme.ewmservice.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerApi {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(final NotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage(),
                        HttpStatus.NOT_FOUND.name(),
                        "The required object was not found."));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, IllegalArgumentException.class, IllegalStatusException.class})
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(final RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getMessage(),
                        HttpStatus.BAD_REQUEST.name(),
                        "Incorrectly made request."));
    }

    @ExceptionHandler({DataIntegrityViolationException.class, ConflictException.class})
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(final RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(ex.getMessage(),
                        HttpStatus.CONFLICT.name(),
                        "Integrity constraint has been violated."));
    }

    @ExceptionHandler(ConditionsNotMetException.class)
    public ResponseEntity<ErrorResponse> handleConditionsNotMetException(final ConditionsNotMetException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(ex.getMessage(),
                        HttpStatus.FORBIDDEN.name(),
                        "For the requested operation the conditions are not met."));
    }
}
