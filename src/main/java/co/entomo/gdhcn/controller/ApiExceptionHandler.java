package co.entomo.gdhcn.controller;

import java.util.HashMap;

import co.entomo.gdhcn.exceptions.GdhcnIPSAlreadyAccessedException;
import co.entomo.gdhcn.exceptions.GdhcnQRCodeExpiredException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import co.entomo.gdhcn.exceptions.GdhcnValidationException;
import co.entomo.gdhcn.vo.ApiFailureResponse;
/**
 *
 *  @author Uday Matta
 *  @organization entomo Labs
 * Global exception handler for API-related exceptions.
 * This class handles specific exceptions and formats them into appropriate API responses.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    /**
     * Handles {@link GdhcnValidationException} thrown by the application.
     *
     * @param ex the exception to handle.
     * @return a {@link ResponseEntity} containing an {@link ApiFailureResponse} with error details.
     */
    @ExceptionHandler(GdhcnValidationException.class)
    public final ResponseEntity<ApiFailureResponse> handleApiException(GdhcnValidationException ex) {
        var errorDetails = ApiFailureResponse.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(ex.getMessage())
                .errorMessage("Unable to process request")
                .status(false).build();
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(GdhcnQRCodeExpiredException.class)
    public final ResponseEntity<ApiFailureResponse> handleApiQrExpiredException(GdhcnQRCodeExpiredException ex) {
        var errorDetails = ApiFailureResponse.builder()
                .code(HttpStatus.UNAUTHORIZED.value())
                .error(ex.getMessage())
                .errorMessage("Unable to process request")
                .status(false).build();
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(GdhcnIPSAlreadyAccessedException.class)
    public final ResponseEntity<ApiFailureResponse> handleApiIpsExpiredException(GdhcnIPSAlreadyAccessedException ex) {
        var errorDetails = ApiFailureResponse.builder()
                .code(HttpStatus.NOT_FOUND.value())
                .error(ex.getMessage())
                .errorMessage("Unable to process request")
                .status(false).build();
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles {@link MethodArgumentNotValidException} thrown during request validation.
     *
     * @param ex the exception to handle.
     * @return a {@link ResponseEntity} containing an {@link ApiFailureResponse} with validation errors.
     */
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
