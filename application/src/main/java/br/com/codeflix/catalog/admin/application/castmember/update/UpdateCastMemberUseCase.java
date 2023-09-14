package br.com.codeflix.catalog.admin.application.castmember.update;

import br.com.codeflix.catalog.admin.application.UseCase;

public abstract sealed class UpdateCastMemberUseCase
        extends UseCase<UpdateCastMemberCommand, UpdateCastMemberOutput>
        permits DefaultUpdateCastMemberUseCase {
}
