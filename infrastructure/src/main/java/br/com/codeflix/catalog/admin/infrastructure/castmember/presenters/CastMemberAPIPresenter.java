package br.com.codeflix.catalog.admin.infrastructure.castmember.presenters;

import br.com.codeflix.catalog.admin.application.castmember.retrieve.get.CastMemberOutput;
import br.com.codeflix.catalog.admin.application.castmember.retrieve.list.CastMemberListOutput;
import br.com.codeflix.catalog.admin.infrastructure.castmember.models.CastMemberListResponse;
import br.com.codeflix.catalog.admin.infrastructure.castmember.models.CastMemberResponse;

import java.util.function.Function;

public interface CastMemberAPIPresenter {

    Function<CastMemberOutput, CastMemberResponse> present = output -> CastMemberResponse.with(
            output.id(),
            output.name(),
            output.type(),
            output.createdAt(),
            output.updatedAt()
    );

    static CastMemberListResponse present(final CastMemberListOutput member) {
        return new CastMemberListResponse(
                member.id(),
                member.name(),
                member.type(),
                member.createdAt()
        );
    }
}