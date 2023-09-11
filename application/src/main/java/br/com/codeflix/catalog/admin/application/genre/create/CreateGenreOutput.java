package br.com.codeflix.catalog.admin.application.genre.create;

import br.com.codeflix.catalog.admin.domain.genre.Genre;

public record CreateGenreOutput(String id) {

    public static CreateGenreOutput from(final Genre genre) {
        return new CreateGenreOutput(genre.getId().getValue());
    }

    public static CreateGenreOutput from(final String id) {
        return new CreateGenreOutput(id);
    }
}
