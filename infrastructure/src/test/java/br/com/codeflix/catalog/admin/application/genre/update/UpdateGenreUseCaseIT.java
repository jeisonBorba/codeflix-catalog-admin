package br.com.codeflix.catalog.admin.application.genre.update;

import br.com.codeflix.catalog.admin.IntegrationTest;
import br.com.codeflix.catalog.admin.domain.category.Category;
import br.com.codeflix.catalog.admin.domain.category.CategoryGateway;
import br.com.codeflix.catalog.admin.domain.category.CategoryID;
import br.com.codeflix.catalog.admin.domain.exceptions.NotificationException;
import br.com.codeflix.catalog.admin.domain.genre.Genre;
import br.com.codeflix.catalog.admin.domain.genre.GenreGateway;
import br.com.codeflix.catalog.admin.infrastructure.genre.persistence.GenreRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@IntegrationTest
public class UpdateGenreUseCaseIT {

    @Autowired
    private UpdateGenreUseCase useCase;

    @SpyBean
    private CategoryGateway categoryGateway;

    @SpyBean
    private GenreGateway genreGateway;

    @Autowired
    private GenreRepository genreRepository;

    @Test
    public void givenAValidCommand_whenCallsUpdateGenre_shouldReturnGenreId() {
        final var genre = genreGateway.create(Genre.newGenre("acao", true));

        final var expectedId = genre.getId();
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();

        final var command = UpdateGenreCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedIsActive,
                asString(expectedCategories)
        );

        final var actualOutput = useCase.execute(command);

        assertNotNull(actualOutput);
        assertEquals(expectedId.getValue(), actualOutput.id());

        final var actualGenre = genreRepository.findById(genre.getId().getValue()).get();

        assertEquals(expectedName, actualGenre.getName());
        assertEquals(expectedIsActive, actualGenre.isActive());
        assertTrue(
                expectedCategories.size() == actualGenre.getCategoriesIDs().size()
                        && expectedCategories.containsAll(actualGenre.getCategoriesIDs())
        );
        assertEquals(genre.getCreatedAt(), actualGenre.getCreatedAt());
        assertTrue(genre.getUpdatedAt().isBefore(actualGenre.getUpdatedAt()));
        assertNull(actualGenre.getDeletedAt());
    }

    @Test
    public void givenAValidCommandWithCategories_whenCallsUpdateGenre_shouldReturnGenreId() {
        final var filmes = categoryGateway.create(Category.newCategory("Filmes", null, true));
        final var series = categoryGateway.create(Category.newCategory("Séries", null, true));

        final var genre = genreGateway.create(Genre.newGenre("acao", true));

        final var expectedId = genre.getId();
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(filmes.getId(), series.getId());

        final var command = UpdateGenreCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedIsActive,
                asString(expectedCategories)
        );

        final var actualOutput = useCase.execute(command);

        assertNotNull(actualOutput);
        assertEquals(expectedId.getValue(), actualOutput.id());

        final var actualGenre = genreRepository.findById(genre.getId().getValue()).get();

        assertEquals(expectedName, actualGenre.getName());
        assertEquals(expectedIsActive, actualGenre.isActive());
        assertTrue(
                expectedCategories.size() == actualGenre.getCategoriesIDs().size()
                        && expectedCategories.containsAll(actualGenre.getCategoriesIDs())
        );
        assertEquals(genre.getCreatedAt(), actualGenre.getCreatedAt());
        assertTrue(genre.getUpdatedAt().isBefore(actualGenre.getUpdatedAt()));
        assertNull(actualGenre.getDeletedAt());
    }

    @Test
    public void givenAValidCommandWithInactiveGenre_whenCallsUpdateGenre_shouldReturnGenreId() {
        final var genre = genreGateway.create(Genre.newGenre("acao", true));

        final var expectedId = genre.getId();
        final var expectedName = "Ação";
        final var expectedIsActive = false;
        final var expectedCategories = List.<CategoryID>of();

        final var command = UpdateGenreCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedIsActive,
                asString(expectedCategories)
        );

        assertTrue(genre.isActive());
        assertNull(genre.getDeletedAt());

        final var actualOutput = useCase.execute(command);

        assertNotNull(actualOutput);
        assertEquals(expectedId.getValue(), actualOutput.id());

        final var actualGenre = genreRepository.findById(genre.getId().getValue()).get();

        assertEquals(expectedName, actualGenre.getName());
        assertEquals(expectedIsActive, actualGenre.isActive());
        assertTrue(
                expectedCategories.size() == actualGenre.getCategoriesIDs().size()
                        && expectedCategories.containsAll(actualGenre.getCategoriesIDs())
        );
        assertEquals(genre.getCreatedAt(), actualGenre.getCreatedAt());
        assertTrue(genre.getUpdatedAt().isBefore(actualGenre.getUpdatedAt()));
        assertNotNull(actualGenre.getDeletedAt());
    }

    @Test
    public void givenAnInvalidName_whenCallsUpdateGenre_shouldReturnNotificationException() {
        final var genre = genreGateway.create(Genre.newGenre("acao", true));

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
                asString(expectedCategories)
        );

        final var actualException = assertThrows(NotificationException.class, () -> {
            useCase.execute(command);
        });

        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        verify(genreGateway).findById(eq(expectedId));

        verify(categoryGateway, never()).existsByIds(any());

        verify(genreGateway, never()).update(any());
    }

    @Test
    public void givenAnInvalidName_whenCallsUpdateGenreAndSomeCategoriesDoesNotExists_shouldReturnNotificationException() {
        final var filmes = categoryGateway.create(Category.newCategory("Filems", null, true));
        final var series = CategoryID.from("456");
        final var documentarios = CategoryID.from("789");

        final var genre = genreGateway.create(Genre.newGenre("acao", true));

        final var expectedId = genre.getId();
        final String expectedName = null;
        final var expectedIsActive = true;
        final var expectedCategories = List.of(filmes.getId(), series, documentarios);

        final var expectedErrorCount = 2;
        final var expectedErrorMessageOne = "Some categories could not be found: 456, 789";
        final var expectedErrorMessageTwo = "'name' should not be null";

        final var command = UpdateGenreCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedIsActive,
                asString(expectedCategories)
        );

        final var actualException = assertThrows(NotificationException.class, () -> {
            useCase.execute(command);
        });

        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessageOne, actualException.getErrors().get(0).message());
        assertEquals(expectedErrorMessageTwo, actualException.getErrors().get(1).message());

        verify(genreGateway).findById(eq(expectedId));

        verify(categoryGateway).existsByIds(eq(expectedCategories));

        verify(genreGateway, never()).update(any());
    }

    private List<String> asString(final List<CategoryID> ids) {
        return ids.stream()
                .map(CategoryID::getValue)
                .toList();
    }
}