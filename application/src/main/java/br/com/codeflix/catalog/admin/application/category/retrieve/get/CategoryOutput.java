package br.com.codeflix.catalog.admin.application.category.retrieve.get;

import br.com.codeflix.catalog.admin.domain.category.Category;
import br.com.codeflix.catalog.admin.domain.category.CategoryID;

import java.time.Instant;

public record CategoryOutput(CategoryID id, String name, String description, boolean isActive, Instant createdAt,
                             Instant updatedAt, Instant deletedAt) {

    public static CategoryOutput from(final Category category) {
        return new CategoryOutput(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.isActive(),
                category.getCreatedAt(),
                category.getUpdatedAt(),
                category.getDeletedAt()
        );
    }
}
