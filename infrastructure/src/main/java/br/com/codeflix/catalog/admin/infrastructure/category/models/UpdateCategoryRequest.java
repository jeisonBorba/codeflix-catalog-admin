package br.com.codeflix.catalog.admin.infrastructure.category.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateCategoryRequest(
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("is_active") Boolean active
) {

    public static UpdateCategoryRequest with(final String name, final String description, final Boolean active) {
        return new UpdateCategoryRequest(name, description, active);
    }
}
