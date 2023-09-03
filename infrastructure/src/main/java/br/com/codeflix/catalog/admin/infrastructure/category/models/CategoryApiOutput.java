package br.com.codeflix.catalog.admin.infrastructure.category.models;

import br.com.codeflix.catalog.admin.domain.category.CategoryID;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record CategoryApiOutput(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("is_active") Boolean active,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt,
        @JsonProperty("deleted_at") Instant deletedAt
) {
    public static CategoryApiOutput with(final String id, final String name, final String description,
                                         final boolean isActive, final Instant createdAt, final Instant updatedAt,
                                         final Instant deletedAt) {
        return new CategoryApiOutput(id, name, description, isActive, createdAt, updatedAt, deletedAt);
    }
}
