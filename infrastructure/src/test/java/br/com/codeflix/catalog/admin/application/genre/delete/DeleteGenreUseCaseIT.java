package br.com.codeflix.catalog.admin.application.genre.delete;

import br.com.codeflix.catalog.admin.IntegrationTest;
import br.com.codeflix.catalog.admin.domain.genre.Genre;
import br.com.codeflix.catalog.admin.domain.genre.GenreGateway;
import br.com.codeflix.catalog.admin.domain.genre.GenreID;
import br.com.codeflix.catalog.admin.infrastructure.genre.persistence.GenreRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@IntegrationTest
public class DeleteGenreUseCaseIT {

    @Autowired
    private DeleteGenreUseCase useCase;

    @Autowired
    private GenreGateway genreGateway;

    @Autowired
    private GenreRepository genreRepository;

    @Test
    public void givenAValidGenreId_whenCallsDeleteGenre_shouldDeleteGenre() {
        final var aGenre = genreGateway.create(Genre.newGenre("Ação", true));

        final var expectedId = aGenre.getId();

        assertEquals(1, genreRepository.count());

        assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        assertEquals(0, genreRepository.count());
    }

    @Test
    public void givenAnInvalidGenreId_whenCallsDeleteGenre_shouldBeOk() {
        genreGateway.create(Genre.newGenre("Ação", true));

        final var expectedId = GenreID.from("123");

        assertEquals(1, genreRepository.count());

        assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        assertEquals(1, genreRepository.count());
    }
}
