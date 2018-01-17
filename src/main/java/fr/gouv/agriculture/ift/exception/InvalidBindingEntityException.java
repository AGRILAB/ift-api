package fr.gouv.agriculture.ift.exception;

import org.springframework.validation.BindingResult;

public class InvalidBindingEntityException extends RuntimeException {

    private BindingResult errors;

    public InvalidBindingEntityException(BindingResult errors) {
        this.errors = errors;
    }

    public BindingResult getErrors() {
        return errors;
    }

}