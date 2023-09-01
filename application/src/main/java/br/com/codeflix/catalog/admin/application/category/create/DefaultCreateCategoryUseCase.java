package br.com.codeflix.catalog.admin.application.category.create;

import br.com.codeflix.catalog.admin.domain.category.Category;
import br.com.codeflix.catalog.admin.domain.category.CategoryGateway;
import br.com.codeflix.catalog.admin.domain.validation.handler.Notification;
import br.com.codeflix.catalog.admin.domain.validation.handler.ThrowsValidationHandler;
import io.vavr.API;
import io.vavr.control.Either;

import java.util.Objects;

import static io.vavr.API.Left;
import static io.vavr.API.Try;

public class DefaultCreateCategoryUseCase extends CreateCategoryUseCase {

    private final CategoryGateway categoryGateway;

    public DefaultCreateCategoryUseCase(final CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public Either<Notification, CreateCategoryOutput> execute(final CreateCategoryCommand command) {
        final var name = command.name();
        final var description = command.description();
        final var isActive = command.isActive();

        final var notification = Notification.create();

        final var category = Category.newCategory(name, description, isActive);
        category.validate(notification);

        return notification.hasError() ? Left(notification) : create(category);
    }

    private Either<Notification, CreateCategoryOutput> create(final Category category) {
        return Try(() -> this.categoryGateway.create(category))
                .toEither()
                .bimap(Notification::create, CreateCategoryOutput::from);
    }
}
