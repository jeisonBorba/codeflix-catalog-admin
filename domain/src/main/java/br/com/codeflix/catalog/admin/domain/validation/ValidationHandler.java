package br.com.codeflix.catalog.admin.domain.validation;

import java.util.List;

public interface ValidationHandler {

    ValidationHandler append(Error error);
    ValidationHandler append(ValidationHandler handler);
    ValidationHandler validate(Validation validation);
    List<Error> getErrors();

    default boolean hasError() {
        return getErrors() != null && !getErrors().isEmpty();
    }

    default Error firsError() {
        if (getErrors() != null && !getErrors().isEmpty()) {
            return getErrors().get(0);
        }
        return null;
    }

    interface Validation {
        void validate();
    }
}
