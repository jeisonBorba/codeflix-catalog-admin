package br.com.codeflix.catalog.admin.application.category.create;

import br.com.codeflix.catalog.admin.domain.category.Category;
import br.com.codeflix.catalog.admin.domain.category.CategoryID;

public record CreateCategoryOutput(String id) {

    public static CreateCategoryOutput from(final Category category) {
        return new CreateCategoryOutput(category.getId().getValue());
    }

    public static CreateCategoryOutput from(final String id) {
        return new CreateCategoryOutput(id);
    }
}
