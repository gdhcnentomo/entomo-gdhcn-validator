package co.entomo.gdhcn.controller;

import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import co.entomo.gdhcn.exceptions.GdhcnValidationException;
import co.entomo.gdhcn.vo.ApiFailureResponse;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(GdhcnValidationException.class)
    public final ResponseEntity<ApiFailureResponse> handleApiException(GdhcnValidationException ex) {
        var errorDetails = ApiFailureResponse.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(ex.getMessage())
                .errorMessage("Unable to process request")
                .status(false).build();
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public final ResponseEntity<ApiFailureResponse> handleValidationException(MethodArgumentNotValidException ex) {
        var errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        var errorDetails = ApiFailureResponse.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .errorMessage("Unable to process request")
                .data(errors)
                .status(false).build();
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}
