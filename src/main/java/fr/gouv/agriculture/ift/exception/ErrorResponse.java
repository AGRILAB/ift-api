package fr.gouv.agriculture.ift.exception;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean used to return errors to REST clients
 */
@Data
class ErrorResponse {

    private String message;
    private String code;
    private List<FieldBindingError> fieldBindingErrors;

    ErrorResponse(String message) {
        this(null, message);
    }

    ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    void addFieldBindingError(String field, String code, String message) {
        if (fieldBindingErrors == null) {
            fieldBindingErrors = new ArrayList<>();
        }
        fieldBindingErrors.add(new FieldBindingError(field, code, message));
    }

    /**
     * Bean used to store Field binding error detected by Bean Validation
     */
    @Data
    private class FieldBindingError {

        private String field;
        private String code;
        private String message;

        private FieldBindingError(String field, String code, String message) {
            this.field = field;
            this.code = code;
            this.message = message;
        }
    }
}