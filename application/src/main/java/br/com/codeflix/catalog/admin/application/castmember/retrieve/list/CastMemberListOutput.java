package br.com.codeflix.catalog.admin.application.castmember.retrieve.list;

import br.com.codeflix.catalog.admin.domain.castmember.CastMember;
import br.com.codeflix.catalog.admin.domain.castmember.CastMemberType;

import java.time.Instant;

public record CastMemberListOutput(
        String id,
        String name,
        CastMemberType type,
        Instant createdAt
) {

    public static CastMemberListOutput from(final CastMember member) {
        return new CastMemberListOutput(
                member.getId().getValue(),
                member.getName(),
                member.getType(),
                member.getCreatedAt()
        );
    }
}