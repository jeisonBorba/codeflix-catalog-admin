package br.com.codeflix.catalog.admin.application.castmember.retrieve.get;

import br.com.codeflix.catalog.admin.application.UseCase;

public abstract sealed class GetCastMemberByIdUseCase
        extends UseCase<String, CastMemberOutput>
        permits DefaultGetCastMemberByIdUseCase {
}
