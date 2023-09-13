package br.com.codeflix.catalog.admin.application.genre.retrieve.list;

import br.com.codeflix.catalog.admin.domain.category.CategoryID;
import br.com.codeflix.catalog.admin.domain.genre.Genre;

import java.time.Instant;
import java.util.List;

public record GenreListOutput(
        String id,
        String name,
        boolean isActive,
        List<String> categories,
        Instant createdAt,
        Instant deletedAt
) {

    public static GenreListOutput from(final Genre genre) {
        return new GenreListOutput(
                genre.getId().getValue(),
               genre.getName(),
                genre.isActive(),
                genre.getCategories().stream()
                        .map(CategoryID::getValue)
                        .toList(),
                genre.getCreatedAt(),
                genre.getDeletedAt()
        );
    }
}
