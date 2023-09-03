package br.com.codeflix.catalog.admin.infrastructure.api.controllers;

import br.com.codeflix.catalog.admin.application.category.create.CreateCategoryCommand;
import br.com.codeflix.catalog.admin.application.category.create.CreateCategoryOutput;
import br.com.codeflix.catalog.admin.application.category.create.CreateCategoryUseCase;
import br.com.codeflix.catalog.admin.application.category.retrieve.get.GetCategoryByIdUseCase;
import br.com.codeflix.catalog.admin.domain.pagination.Pagination;
import br.com.codeflix.catalog.admin.domain.validation.handler.Notification;
import br.com.codeflix.catalog.admin.infrastructure.api.CategoryAPI;
import br.com.codeflix.catalog.admin.infrastructure.category.models.CategoryApiOutput;
import br.com.codeflix.catalog.admin.infrastructure.category.models.CreateCategoryApiInput;
import br.com.codeflix.catalog.admin.infrastructure.category.presenters.CategoryApiPresenter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Objects;
import java.util.function.Function;

@RestController
public class CategoryController implements CategoryAPI {

    private final CreateCategoryUseCase createCategoryUseCase;
    private final GetCategoryByIdUseCase getCategoryByIdUseCase;

    public CategoryController(final CreateCategoryUseCase createCategoryUseCase,
                              final GetCategoryByIdUseCase getCategoryByIdUseCase) {
        this.createCategoryUseCase = Objects.requireNonNull(createCategoryUseCase);
        this.getCategoryByIdUseCase = Objects.requireNonNull(getCategoryByIdUseCase);
    }

    @Override
    public ResponseEntity<?> createCategory(final CreateCategoryApiInput input) {
        final var command = CreateCategoryCommand.with(
                input.name(),
                input.description(),
                input.active() != null ? input.active() : true
        );

        final Function<Notification, ResponseEntity<?>> onError = notification ->
                ResponseEntity.unprocessableEntity().body(notification);

        final Function<CreateCategoryOutput, ResponseEntity<?>> onSuccess = output ->
                ResponseEntity.created(URI.create("/categories/" + output.id())).body(output);

        return this.createCategoryUseCase.execute(command)
                .fold(onError, onSuccess);
    }

    @Override
    public Pagination<?> listCategories(String search, int page, int perPage, String sort, String direction) {
        return null;
    }

    @Override
    public CategoryApiOutput getCategoryById(final String id) {
        return CategoryApiPresenter.present
                .compose(this.getCategoryByIdUseCase::execute)
                .apply(id);
    }
}
