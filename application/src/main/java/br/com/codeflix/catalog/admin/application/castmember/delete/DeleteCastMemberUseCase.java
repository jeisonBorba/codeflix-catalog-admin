package br.com.codeflix.catalog.admin.application.castmember.delete;

import br.com.codeflix.catalog.admin.application.UnitUseCase;

public abstract sealed class DeleteCastMemberUseCase
        extends UnitUseCase<String>
        permits DefaultDeleteCastMemberUseCase {
}
