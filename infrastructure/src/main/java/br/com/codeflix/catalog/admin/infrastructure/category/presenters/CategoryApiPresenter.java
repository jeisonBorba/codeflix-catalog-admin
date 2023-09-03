package br.com.codeflix.catalog.admin.infrastructure.category.presenters;

import br.com.codeflix.catalog.admin.application.category.retrieve.get.CategoryOutput;
import br.com.codeflix.catalog.admin.infrastructure.category.models.CategoryApiOutput;

import java.util.function.Function;

public interface CategoryApiPresenter {

    Function<CategoryOutput, CategoryApiOutput> present = output -> CategoryApiOutput.with(
            output.id().getValue(),
            output.name(),
            output.description(),
            output.isActive(),
            output.createdAt(),
            output.updatedAt(),
            output.deletedAt()
    );
}
