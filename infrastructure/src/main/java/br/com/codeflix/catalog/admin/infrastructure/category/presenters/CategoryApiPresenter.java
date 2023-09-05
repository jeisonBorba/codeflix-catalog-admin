package br.com.codeflix.catalog.admin.infrastructure.category.presenters;

import br.com.codeflix.catalog.admin.application.category.retrieve.get.CategoryOutput;
import br.com.codeflix.catalog.admin.application.category.retrieve.list.CategoryListOutput;
import br.com.codeflix.catalog.admin.infrastructure.category.models.CategoryResponse;
import br.com.codeflix.catalog.admin.infrastructure.category.models.CategoryListResponse;

import java.util.function.Function;

public interface CategoryApiPresenter {

    Function<CategoryOutput, CategoryResponse> present = output -> CategoryResponse.with(
            output.id().getValue(),
            output.name(),
            output.description(),
            output.isActive(),
            output.createdAt(),
            output.updatedAt(),
            output.deletedAt()
    );

    static CategoryListResponse present(final CategoryListOutput output) {
        return new CategoryListResponse(
                output.id().getValue(),
                output.name(),
                output.description(),
                output.isActive(),
                output.createdAt(),
                output.deletedAt()
        );
    }
}
