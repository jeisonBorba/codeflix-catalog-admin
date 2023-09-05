package br.com.codeflix.catalog.admin.infrastructure.api.controllers;

import br.com.codeflix.catalog.admin.application.category.create.CreateCategoryCommand;
import br.com.codeflix.catalog.admin.application.category.create.CreateCategoryOutput;
import br.com.codeflix.catalog.admin.application.category.create.CreateCategoryUseCase;
import br.com.codeflix.catalog.admin.application.category.delete.DeleteCategoryUseCase;
import br.com.codeflix.catalog.admin.application.category.retrieve.get.GetCategoryByIdUseCase;
import br.com.codeflix.catalog.admin.application.category.retrieve.list.ListCategoryUseCase;
import br.com.codeflix.catalog.admin.application.category.update.UpdateCategoryCommand;
import br.com.codeflix.catalog.admin.application.category.update.UpdateCategoryOutput;
import br.com.codeflix.catalog.admin.application.category.update.UpdateCategoryUseCase;
import br.com.codeflix.catalog.admin.domain.category.CategorySearchQuery;
import br.com.codeflix.catalog.admin.domain.pagination.Pagination;
import br.com.codeflix.catalog.admin.domain.validation.handler.Notification;
import br.com.codeflix.catalog.admin.infrastructure.api.CategoryAPI;
import br.com.codeflix.catalog.admin.infrastructure.category.models.CategoryListResponse;
import br.com.codeflix.catalog.admin.infrastructure.category.models.CategoryResponse;
import br.com.codeflix.catalog.admin.infrastructure.category.models.CreateCategoryRequest;
import br.com.codeflix.catalog.admin.infrastructure.category.models.UpdateCategoryRequest;
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

    private final UpdateCategoryUseCase updateCategoryUseCase;

    private final DeleteCategoryUseCase deleteCategoryUseCase;
    private final ListCategoryUseCase listCategoryUseCase;

    public CategoryController(
            final CreateCategoryUseCase createCategoryUseCase,
            final GetCategoryByIdUseCase getCategoryByIdUseCase,
            final UpdateCategoryUseCase updateCategoryUseCase,
            final DeleteCategoryUseCase deleteCategoryUseCase,
            final ListCategoryUseCase listCategoryUseCase
    ) {
        this.createCategoryUseCase = Objects.requireNonNull(createCategoryUseCase);
        this.getCategoryByIdUseCase = Objects.requireNonNull(getCategoryByIdUseCase);
        this.updateCategoryUseCase = Objects.requireNonNull(updateCategoryUseCase);
        this.deleteCategoryUseCase = Objects.requireNonNull(deleteCategoryUseCase);
        this.listCategoryUseCase = Objects.requireNonNull(listCategoryUseCase);
    }

    @Override
    public ResponseEntity<?> createCategory(final CreateCategoryRequest input) {
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
    public Pagination<CategoryListResponse> listCategories(final String search, final int page, final int perPage, final String sort, final String direction) {
        return this.listCategoryUseCase
                .execute(CategorySearchQuery.with(page, perPage, search, sort, direction))
                .map(CategoryApiPresenter::present);
    }

    @Override
    public CategoryResponse getCategoryById(final String id) {
        return CategoryApiPresenter.present
                .compose(this.getCategoryByIdUseCase::execute)
                .apply(id);
    }

    @Override
    public ResponseEntity<?> updateCategoryById(final String id, final UpdateCategoryRequest input) {
        final var command = UpdateCategoryCommand.with(
                id,
                input.name(),
                input.description(),
                input.active() != null ? input.active() : true
        );

        final Function<Notification, ResponseEntity<?>> onError = notification ->
                ResponseEntity.unprocessableEntity().body(notification);

        final Function<UpdateCategoryOutput, ResponseEntity<?>> onSuccess = ResponseEntity::ok;

        return this.updateCategoryUseCase.execute(command)
                .fold(onError, onSuccess);
    }

    @Override
    public void deleteCategoryById(final String id) {
        this.deleteCategoryUseCase.execute(id);
    }
}
