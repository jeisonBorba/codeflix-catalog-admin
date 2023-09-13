package br.com.codeflix.catalog.admin.infrastructure.api.controllers;

import br.com.codeflix.catalog.admin.application.genre.create.CreateGenreCommand;
import br.com.codeflix.catalog.admin.application.genre.create.CreateGenreUseCase;
import br.com.codeflix.catalog.admin.application.genre.delete.DeleteGenreUseCase;
import br.com.codeflix.catalog.admin.application.genre.retrieve.get.GetGenreByIdUseCase;
import br.com.codeflix.catalog.admin.application.genre.retrieve.list.ListGenreUseCase;
import br.com.codeflix.catalog.admin.application.genre.update.UpdateGenreCommand;
import br.com.codeflix.catalog.admin.application.genre.update.UpdateGenreUseCase;
import br.com.codeflix.catalog.admin.domain.pagination.Pagination;
import br.com.codeflix.catalog.admin.domain.pagination.SearchQuery;
import br.com.codeflix.catalog.admin.infrastructure.api.GenreAPI;
import br.com.codeflix.catalog.admin.infrastructure.genre.models.CreateGenreRequest;
import br.com.codeflix.catalog.admin.infrastructure.genre.models.GenreListResponse;
import br.com.codeflix.catalog.admin.infrastructure.genre.models.GenreResponse;
import br.com.codeflix.catalog.admin.infrastructure.genre.models.UpdateGenreRequest;
import br.com.codeflix.catalog.admin.infrastructure.genre.presenters.GenreApiPresenter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Objects;

@RestController
public class GenreController implements GenreAPI {

    private final CreateGenreUseCase createGenreUseCase;
    private final DeleteGenreUseCase deleteGenreUseCase;
    private final GetGenreByIdUseCase getGenreByIdUseCase;
    private final ListGenreUseCase listGenreUseCase;
    private final UpdateGenreUseCase updateGenreUseCase;

    public GenreController(
            final CreateGenreUseCase createGenreUseCase,
            final DeleteGenreUseCase deleteGenreUseCase,
            final GetGenreByIdUseCase getGenreByIdUseCase,
            final ListGenreUseCase listGenreUseCase,
            final UpdateGenreUseCase updateGenreUseCase
    ) {
        this.createGenreUseCase = Objects.requireNonNull(createGenreUseCase);
        this.deleteGenreUseCase = Objects.requireNonNull(deleteGenreUseCase);
        this.getGenreByIdUseCase = Objects.requireNonNull(getGenreByIdUseCase);
        this.listGenreUseCase = Objects.requireNonNull(listGenreUseCase);
        this.updateGenreUseCase = Objects.requireNonNull(updateGenreUseCase);
    }

    @Override
    public ResponseEntity<?> create(final CreateGenreRequest input) {
        final var command = CreateGenreCommand.with(
                input.name(),
                input.isActive(),
                input.categories()
        );

        final var output = this.createGenreUseCase.execute(command);

        return ResponseEntity.created(URI.create("/genres/" + output.id())).body(output);
    }

    @Override
    public Pagination<GenreListResponse> list(
            final String search,
            final int page,
            final int perPage,
            final String sort,
            final String direction
    ) {
        return this.listGenreUseCase.execute(new SearchQuery(page, perPage, search, sort, direction))
                .map(GenreApiPresenter::present);
    }

    @Override
    public GenreResponse getById(final String id) {
        return GenreApiPresenter.present
                .compose(this.getGenreByIdUseCase::execute)
                .apply(id);
    }

    @Override
    public ResponseEntity<?> updateById(final String id, final UpdateGenreRequest input) {
        final var command = UpdateGenreCommand.with(
                id,
                input.name(),
                input.isActive(),
                input.categories()
        );

        final var output = this.updateGenreUseCase.execute(command);

        return ResponseEntity.ok(output);
    }

    @Override
    public void deleteById(final String id) {
        this.deleteGenreUseCase.execute(id);
    }
}
