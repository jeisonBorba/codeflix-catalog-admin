package br.com.codeflix.catalog.admin.infrastructure.category.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateCategoryRequest(
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("is_active") Boolean active
) {
    public static CreateCategoryRequest with(final String name, final String description, final boolean active) {
        return new CreateCategoryRequest(name, description, active);
    }
}
