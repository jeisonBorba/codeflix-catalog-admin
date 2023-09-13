package br.com.codeflix.catalog.admin.infrastructure.genre.presenters;

import br.com.codeflix.catalog.admin.application.genre.retrieve.get.GenreOutput;
import br.com.codeflix.catalog.admin.application.genre.retrieve.list.GenreListOutput;
import br.com.codeflix.catalog.admin.infrastructure.genre.models.GenreListResponse;
import br.com.codeflix.catalog.admin.infrastructure.genre.models.GenreResponse;

import java.util.function.Function;

public interface GenreApiPresenter {

    Function<GenreOutput, GenreResponse> present = output -> GenreResponse.with(
            output.id(),
            output.name(),
            output.categories(),
            output.isActive(),
            output.createdAt(),
            output.updatedAt(),
            output.deletedAt()
    );

    static GenreListResponse present(final GenreListOutput output) {
        return new GenreListResponse(
                output.id(),
                output.name(),
                output.isActive(),
                output.createdAt(),
                output.deletedAt()
        );
    }
}