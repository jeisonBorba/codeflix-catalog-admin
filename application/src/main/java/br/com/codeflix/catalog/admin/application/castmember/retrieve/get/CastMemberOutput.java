package br.com.codeflix.catalog.admin.application.castmember.retrieve.get;

import br.com.codeflix.catalog.admin.domain.castmember.CastMember;
import br.com.codeflix.catalog.admin.domain.castmember.CastMemberType;

import java.time.Instant;

public record CastMemberOutput(
        String id,
        String name,
        CastMemberType type,
        Instant createdAt,
        Instant updatedAt
) {

    public static CastMemberOutput from(final CastMember member) {
        return new CastMemberOutput(
                member.getId().getValue(),
                member.getName(),
                member.getType(),
                member.getCreatedAt(),
                member.getUpdatedAt()
        );
    }
}
