package br.com.codeflix.catalog.admin.application.castmember.create;

import br.com.codeflix.catalog.admin.application.UseCase;

public abstract sealed class CreateCastMemberUseCase
        extends UseCase<CreateCastMemberCommand, CreateCastMemberOutput>
        permits DefaultCreateCastMemberUseCase {
}
