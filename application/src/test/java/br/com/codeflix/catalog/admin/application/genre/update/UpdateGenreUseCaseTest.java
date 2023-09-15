package br.com.codeflix.catalog.admin.application.genre.update;

import br.com.codeflix.catalog.admin.application.UseCaseTest;
import br.com.codeflix.catalog.admin.domain.category.CategoryGateway;
import br.com.codeflix.catalog.admin.domain.category.CategoryID;
import br.com.codeflix.catalog.admin.domain.exceptions.NotificationException;
import br.com.codeflix.catalog.admin.domain.genre.Genre;
import br.com.codeflix.catalog.admin.domain.genre.GenreGateway;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UpdateGenreUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultUpdateGenreUseCase useCase;

    @Mock
    private CategoryGateway categoryGateway;

    @Mock
    private GenreGateway genreGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(categoryGateway, genreGateway);
    }

    @Test
    public void givenAValidCommand_whenCallUpdateGenre_shouldReturnGenreId() {
        final var genre = Genre.newGenre("acao", true);

        final var expectedId = genre.getId();
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();

        final var command = UpdateGenreCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedIsActive,
                asString(expectedCategories));

        when(genreGateway.findById(any())).thenReturn(Optional.of(genre.clone()));
        when(genreGateway.update(any())).thenAnswer(returnsFirstArg());

        final var actualOutput = useCase.execute(command);

        assertNotNull(actualOutput);
        assertEquals(expectedId.getValue(), actualOutput.id());

        verify(genreGateway).findById(eq(expectedId));
        verify(genreGateway).update(argThat(updatedGenre ->
                Objects.equals(expectedId, updatedGenre.getId())
                        && Objects.equals(expectedName, updatedGenre.getName())
                        && Objects.equals(expectedCategories, updatedGenre.getCategories())
                        && Objects.equals(genre.getCreatedAt(), updatedGenre.getCreatedAt())
                        && genre.getUpdatedAt().isBefore(updatedGenre.getUpdatedAt())
                        && Objects.isNull(updatedGenre.getDeletedAt())
        ));
    }

    @Test
    public void givenAValidCommandWithCategories_whenCallUpdateGenre_shouldReturnGenreId() {
        final var genre = Genre.newGenre("acao", true);
        final var filmes = CategoryID.from("123");
        final var series = CategoryID.from("456");

        final var expectedId = genre.getId();
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(
                filmes,
                series
        );

        final var command = UpdateGenreCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedIsActive,
                asString(expectedCategories));

        when(genreGateway.findById(any())).thenReturn(Optional.of(genre.clone()));
        when(categoryGateway.existsByIds(any())).thenReturn(expectedCategories);
        when(genreGateway.update(any())).thenAnswer(returnsFirstArg());

        final var actualOutput = useCase.execute(command);

        assertNotNull(actualOutput);
        assertEquals(expectedId.getValue(), actualOutput.id());

        verify(genreGateway).findById(eq(expectedId));
        verify(categoryGateway).existsByIds(eq(List.of(filmes, series)));
        verify(genreGateway).update(argThat(updatedGenre ->
                Objects.equals(expectedId, updatedGenre.getId())
                        && Objects.equals(expectedName, updatedGenre.getName())
                        && Objects.equals(expectedCategories, updatedGenre.getCategories())
                        && Objects.equals(genre.getCreatedAt(), updatedGenre.getCreatedAt())
                        && genre.getUpdatedAt().isBefore(updatedGenre.getUpdatedAt())
                        && Objects.isNull(updatedGenre.getDeletedAt())
        ));
    }

    @Test
    public void givenAnInvalidName_whenCallUpdateGenre_shouldReturnNotificationException() {
        final var genre = Genre.newGenre("acao", true);

        final var expectedId = genre.getId();
        final String expectedName = null;
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        final var command = UpdateGenreCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedIsActive,
                asString(expectedCategories));

        when(genreGateway.findById(any())).thenReturn(Optional.of(genre.clone()));

        final var actualException = assertThrows(NotificationException.class, () -> useCase.execute(command));

        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        verify(genreGateway).findById(eq(expectedId));
    }

    @Test
    public void givenAnInvalidName_whenCallUpdateGenreAndSomeCategoriesDoesNotExists_shouldReturnNotificationException() {
        final var genre = Genre.newGenre("acao", true);
        final var filmes = CategoryID.from("123");
        final var series = CategoryID.from("456");
        final var documentarios = CategoryID.from("789");

        final var expectedId = genre.getId();
        final var expectedName = "";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(
                filmes,
                series,
                documentarios
        );
        final var expectedErrorCount = 2;
        final var expectedErrorMessageOne = "Some categories could not be found: 456, 789";
        final var expectedErrorMessageTwo = "'name' should not be empty";

        final var command = UpdateGenreCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedIsActive,
                asString(expectedCategories));

        when(genreGateway.findById(any())).thenReturn(Optional.of(genre.clone()));
        when(categoryGateway.existsByIds(any())).thenReturn(List.of(filmes));

        final var actualException = assertThrows(NotificationException.class, () -> useCase.execute(command));

        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessageOne, actualException.getErrors().get(0).message());
        assertEquals(expectedErrorMessageTwo, actualException.getErrors().get(1).message());

        verify(genreGateway).findById(eq(expectedId));
        verify(categoryGateway).existsByIds(eq(List.of(filmes, series, documentarios)));
    }

    @Test
    public void givenAValidCommandWithInactiveGenre_whenCallUpdateGenre_shouldReturnGenreId() {
        final var genre = Genre.newGenre("acao", true);

        final var expectedId = genre.getId();
        final var expectedName = "Ação";
        final var expectedIsActive = false;
        final var expectedCategories = List.<CategoryID>of();

        final var command = UpdateGenreCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedIsActive,
                asString(expectedCategories));

        when(genreGateway.findById(any())).thenReturn(Optional.of(genre.clone()));
        when(genreGateway.update(any())).thenAnswer(returnsFirstArg());

        assertNull(genre.getDeletedAt());
        assertTrue(genre.isActive());

        final var actualOutput = useCase.execute(command);

        assertNotNull(actualOutput);
        assertEquals(expectedId.getValue(), actualOutput.id());

        verify(genreGateway).findById(eq(expectedId));
        verify(genreGateway).update(argThat(updatedGenre ->
                Objects.equals(expectedId, updatedGenre.getId())
                        && Objects.equals(expectedName, updatedGenre.getName())
                        && Objects.equals(expectedCategories, updatedGenre.getCategories())
                        && Objects.equals(genre.getCreatedAt(), updatedGenre.getCreatedAt())
                        && genre.getUpdatedAt().isBefore(updatedGenre.getUpdatedAt())
                        && Objects.nonNull(updatedGenre.getDeletedAt())
        ));
    }

}
