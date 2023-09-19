package br.com.codeflix.catalog.admin.domain.exceptions;

import br.com.codeflix.catalog.admin.domain.AggregateRoot;
import br.com.codeflix.catalog.admin.domain.Identifier;
import br.com.codeflix.catalog.admin.domain.validation.Error;

import java.util.Collections;
import java.util.List;

public class NotFoundException extends DomainException {

    protected NotFoundException(String message, List<Error> errors) {
        super(message, errors);
    }

    public static NotFoundException with(
            final Class<? extends AggregateRoot<?>> aggregate,
            final Identifier identifier
    ) {
        final var errorMessage = "%s with ID %s was not found".formatted(aggregate.getSimpleName(), identifier.getValue());
        return new NotFoundException(errorMessage, Collections.emptyList());
    }

    public static NotFoundException with(final Error error) {
        return new NotFoundException(error.message(), List.of(error));
    }
}
