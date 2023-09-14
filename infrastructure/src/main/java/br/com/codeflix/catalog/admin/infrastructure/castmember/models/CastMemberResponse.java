package br.com.codeflix.catalog.admin.infrastructure.castmember.models;

import br.com.codeflix.catalog.admin.domain.castmember.CastMemberType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record CastMemberResponse(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("type") CastMemberType type,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt
) {
    public static CastMemberResponse with(final String id, final String name, final CastMemberType type,
                                          final Instant createdAt, final Instant updatedAt) {
        return new CastMemberResponse(
                id,
                name,
                type,
                createdAt,
                updatedAt
        );
    }
}
