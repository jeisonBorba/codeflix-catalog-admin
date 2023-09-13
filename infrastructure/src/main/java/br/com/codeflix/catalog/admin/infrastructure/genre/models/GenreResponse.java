package br.com.codeflix.catalog.admin.infrastructure.genre.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

public record GenreResponse(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("categories_id") List<String> categories,
        @JsonProperty("is_active") Boolean active,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt,
        @JsonProperty("deleted_at") Instant deletedAt
) {
    public static GenreResponse with(final String id, final String name, final List<String> categories,
                                     final boolean active, final Instant createdAt, final Instant updatedAt,
                                     final Instant deletedAt) {
        return new GenreResponse(
                id,
                name,
                categories,
                active,
                createdAt,
                updatedAt,
                deletedAt
        );
    }
}
