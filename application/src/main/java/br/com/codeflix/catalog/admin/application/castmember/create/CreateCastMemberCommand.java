package br.com.codeflix.catalog.admin.application.castmember.create;

import br.com.codeflix.catalog.admin.domain.castmember.CastMemberType;

public record CreateCastMemberCommand(
        String name,
        CastMemberType type
) {

    public static CreateCastMemberCommand with(final String name, final CastMemberType type) {
        return new CreateCastMemberCommand(name, type);
    }
}
