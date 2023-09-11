package br.com.codeflix.catalog.admin.application.genre.delete;

import br.com.codeflix.catalog.admin.application.UseCaseTest;
import br.com.codeflix.catalog.admin.domain.genre.Genre;
import br.com.codeflix.catalog.admin.domain.genre.GenreGateway;
import br.com.codeflix.catalog.admin.domain.genre.GenreID;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DeleteGenreUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultDeleteGenreUseCase useCase;

    @Mock
    private GenreGateway genreGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(genreGateway);
    }

    @Test
    public void givenAValidGenreId_whenCallDeleteGenre_shouldDeleteGenre() {
        final var genre = Genre.newGenre("Ação", true);
        final var expectedId = genre.getId();

        doNothing().when(genreGateway).deleteById(any());

        assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        verify(genreGateway).deleteById(expectedId);
    }

    @Test
    public void givenAnInvalidGenreId_whenCallDeleteGenre_shouldBeOk() {
        final var expectedId = GenreID.from("1233");

        doNothing().when(genreGateway).deleteById(any());

        assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        verify(genreGateway).deleteById(expectedId);
    }

    @Test
    public void givenAValidGenreId_whenCallDeleteGenreAndGatewayThrowsUnexpectedError_shouldReturnExecption() {
        final var genre = Genre.newGenre("Ação", true);
        final var expectedId = genre.getId();
        final var expectedErrorMessage = "Gateway error";

        doThrow(new IllegalStateException(expectedErrorMessage)).when(genreGateway).deleteById(any());

        final var actualExeption = assertThrows(IllegalStateException.class, () -> useCase.execute(expectedId.getValue()));

        assertEquals(expectedErrorMessage, actualExeption.getMessage());

        verify(genreGateway).deleteById(expectedId);
    }
}
