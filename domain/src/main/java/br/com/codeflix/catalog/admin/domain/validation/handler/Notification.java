package br.com.codeflix.catalog.admin.domain.validation.handler;

import br.com.codeflix.catalog.admin.domain.exceptions.DomainException;
import br.com.codeflix.catalog.admin.domain.validation.Error;
import br.com.codeflix.catalog.admin.domain.validation.ValidationHandler;

import java.util.ArrayList;
import java.util.List;

public class Notification implements ValidationHandler {

    private final List<Error> errors;

    private Notification(final List<Error> errors) {
        this.errors = errors;
    }

    public static Notification create() {
        return new Notification(new ArrayList<>());
    }

    public static Notification create(final Error error) {
        return new Notification(new ArrayList<>()).append(error);
    }

    public static Notification create(final Throwable t) {
        return create(new Error(t.getMessage()));
    }

    @Override
    public Notification append(final Error error) {
        this.errors.add(error);
        return this;
    }

    @Override
    public Notification append(final ValidationHandler handler) {
        this.errors.addAll(handler.getErrors());
        return this;
    }

    @Override
    public <T> T validate(final Validation<T> validation) {
        try {
            return validation.validate();
        } catch (final DomainException ex) {
            this.errors.addAll(ex.getErrors());
        } catch (Throwable t) {
            this.errors.add(new Error(t.getMessage()));
        }
        return null;
    }

    @Override
    public List<Error> getErrors() {
        return this.errors;
    }
}
