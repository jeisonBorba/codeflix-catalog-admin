package br.com.codeflix.catalog.admin.application.castmember.create;

import br.com.codeflix.catalog.admin.domain.castmember.CastMember;
import br.com.codeflix.catalog.admin.domain.castmember.CastMemberID;

public record CreateCastMemberOutput(
        String id
) {

    public static CreateCastMemberOutput from(final CastMemberID id) {
        return new CreateCastMemberOutput(id.getValue());
    }

    public static CreateCastMemberOutput from(final CastMember member) {
        return from(member.getId());
    }
}
