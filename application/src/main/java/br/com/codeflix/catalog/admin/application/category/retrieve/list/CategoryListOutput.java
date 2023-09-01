package br.com.codeflix.catalog.admin.application.category.retrieve.list;

import br.com.codeflix.catalog.admin.domain.category.Category;
import br.com.codeflix.catalog.admin.domain.category.CategoryID;

import java.time.Instant;

public record CategoryListOutput(CategoryID id, String name, String description, boolean isActive, Instant createdAt,
                                 Instant deletedAt) {

    public static CategoryListOutput from(final Category category) {
        return new CategoryListOutput(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.isActive(),
                category.getCreatedAt(),
                category.getDeletedAt());
    }
}
