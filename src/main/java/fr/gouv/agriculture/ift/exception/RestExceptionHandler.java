package fr.gouv.agriculture.ift.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(value = {Exception.class, RuntimeException.class})
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error(ex.getMessage(), ex);
        Throwable cause = getRoot(ex).getCause();
        return new ResponseEntity<>(new ErrorResponse(cause != null ? cause.getMessage() : ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {ServerException.class})
    public ResponseEntity<ErrorResponse> handleServerException() {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle special binding exception raised should a @Valid parsed entity has error.
     *
     * @param ex exception which hold fieldBindingErrors to be returned
     * @return JSON formatted error message
     */
    @ExceptionHandler(value = {InvalidBindingEntityException.class})
    public ResponseEntity<ErrorResponse> handleInvalidRequestException(InvalidBindingEntityException ex) {

        ErrorResponse errorResponse = new ErrorResponse("ERR_UNPROCESSABLE_ENTITY", "Error during binding");

        BindingResult result = ex.getErrors();
        log.error(result.toString());

        List<FieldError> fieldErrors = result.getFieldErrors();
        fieldErrors.forEach((error) ->
                errorResponse.addFieldBindingError(error.getField(), error.getCode(), error.getDefaultMessage())
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    /**
     * @param t given throwable instance
     * @return the given Throwable root cause.
     */
    private Throwable getRoot(Throwable t) {
        Throwable result = t;

        while (result.getCause() != null) {
            result = result.getCause();
        }

        return result;
    }
}
