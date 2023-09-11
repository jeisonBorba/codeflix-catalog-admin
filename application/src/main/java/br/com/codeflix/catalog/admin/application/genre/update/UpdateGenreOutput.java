package br.com.codeflix.catalog.admin.application.genre.update;

import br.com.codeflix.catalog.admin.application.category.update.UpdateCategoryOutput;
import br.com.codeflix.catalog.admin.domain.genre.Genre;

public record UpdateGenreOutput(String id) {

    public static UpdateGenreOutput from(final String id) {
        return new UpdateGenreOutput(id);
    }

    public static UpdateGenreOutput from(final Genre genre) {
        return new UpdateGenreOutput(genre.getId().getValue());
    }
}
