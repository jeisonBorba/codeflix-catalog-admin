package br.com.codeflix.catalog.admin.infrastructure.api.controllers;

import br.com.codeflix.catalog.admin.application.castmember.create.CreateCastMemberCommand;
import br.com.codeflix.catalog.admin.application.castmember.create.CreateCastMemberUseCase;
import br.com.codeflix.catalog.admin.application.castmember.delete.DeleteCastMemberUseCase;
import br.com.codeflix.catalog.admin.application.castmember.retrieve.get.GetCastMemberByIdUseCase;
import br.com.codeflix.catalog.admin.application.castmember.retrieve.list.ListCastMembersUseCase;
import br.com.codeflix.catalog.admin.application.castmember.update.UpdateCastMemberCommand;
import br.com.codeflix.catalog.admin.application.castmember.update.UpdateCastMemberUseCase;
import br.com.codeflix.catalog.admin.domain.pagination.Pagination;
import br.com.codeflix.catalog.admin.domain.pagination.SearchQuery;
import br.com.codeflix.catalog.admin.infrastructure.api.CastMemberAPI;
import br.com.codeflix.catalog.admin.infrastructure.castmember.models.CastMemberListResponse;
import br.com.codeflix.catalog.admin.infrastructure.castmember.models.CastMemberResponse;
import br.com.codeflix.catalog.admin.infrastructure.castmember.models.CreateCastMemberRequest;
import br.com.codeflix.catalog.admin.infrastructure.castmember.models.UpdateCastMemberRequest;
import br.com.codeflix.catalog.admin.infrastructure.castmember.presenters.CastMemberAPIPresenter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Objects;

@RestController
public class CastMemberController implements CastMemberAPI {

    private final CreateCastMemberUseCase createCastMemberUseCase;
    private final GetCastMemberByIdUseCase getCastMemberByIdUseCase;
    private final UpdateCastMemberUseCase updateCastMemberUseCase;
    private final DeleteCastMemberUseCase deleteCastMemberUseCase;
    private final ListCastMembersUseCase listCastMembersUseCase;

    public CastMemberController(
            final CreateCastMemberUseCase createCastMemberUseCase,
            final GetCastMemberByIdUseCase getCastMemberByIdUseCase,
            final UpdateCastMemberUseCase updateCastMemberUseCase,
            final DeleteCastMemberUseCase deleteCastMemberUseCase,
            final ListCastMembersUseCase listCastMembersUseCase
    ) {
        this.createCastMemberUseCase = Objects.requireNonNull(createCastMemberUseCase);
        this.getCastMemberByIdUseCase = Objects.requireNonNull(getCastMemberByIdUseCase);
        this.updateCastMemberUseCase = Objects.requireNonNull(updateCastMemberUseCase);
        this.deleteCastMemberUseCase = Objects.requireNonNull(deleteCastMemberUseCase);
        this.listCastMembersUseCase = Objects.requireNonNull(listCastMembersUseCase);
    }

    @Override
    public ResponseEntity<?> create(final CreateCastMemberRequest input) {
        final var command = CreateCastMemberCommand.with(input.name(), input.type());

        final var output = this.createCastMemberUseCase.execute(command);

        return ResponseEntity.created(URI.create("/cast_members/" + output.id())).body(output);
    }

    @Override
    public Pagination<CastMemberListResponse> listAll(final String search,
                                                      final int page,
                                                      final int perPage,
                                                      final String sort,
                                                      final String direction) {
        return this.listCastMembersUseCase.execute(SearchQuery.with(page, perPage, search, sort, direction))
                .map(CastMemberAPIPresenter::present);
    }

    @Override
    public CastMemberResponse getById(final String id) {
        return CastMemberAPIPresenter.present
                .compose(this.getCastMemberByIdUseCase::execute)
                .apply(id);
    }

    @Override
    public ResponseEntity<?> updateById(final String id, final UpdateCastMemberRequest input) {
        final var command = UpdateCastMemberCommand.with(id, input.name(), input.type());

        final var output = this.updateCastMemberUseCase.execute(command);

        return ResponseEntity.ok(output);
    }

    @Override
    public void deleteById(final String id) {
        this.deleteCastMemberUseCase.execute(id);
    }
}
