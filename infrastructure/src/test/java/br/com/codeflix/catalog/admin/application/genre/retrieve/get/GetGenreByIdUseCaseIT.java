package br.com.codeflix.catalog.admin.application.genre.retrieve.get;

import br.com.codeflix.catalog.admin.IntegrationTest;
import br.com.codeflix.catalog.admin.domain.category.Category;
import br.com.codeflix.catalog.admin.domain.category.CategoryGateway;
import br.com.codeflix.catalog.admin.domain.category.CategoryID;
import br.com.codeflix.catalog.admin.domain.exceptions.NotFoundException;
import br.com.codeflix.catalog.admin.domain.genre.Genre;
import br.com.codeflix.catalog.admin.domain.genre.GenreGateway;
import br.com.codeflix.catalog.admin.domain.genre.GenreID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@IntegrationTest
public class GetGenreByIdUseCaseIT {

    @Autowired
    private GetGenreByIdUseCase useCase;

    @Autowired
    private CategoryGateway categoryGateway;

    @Autowired
    private GenreGateway genreGateway;

    @Test
    public void givenAValidId_whenCallsGetGenre_shouldReturnGenre() {
        final var series = categoryGateway.create(Category.newCategory("Séries", null, true));
        final var filmes = categoryGateway.create(Category.newCategory("Filmes", null, true));

        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(series.getId(), filmes.getId());

        final var aGenre = genreGateway.create(
                Genre.newGenre(expectedName, expectedIsActive)
                        .addCategories(expectedCategories)
        );

        final var expectedId = aGenre.getId();

        final var actualGenre = useCase.execute(expectedId.getValue());

        assertEquals(expectedId.getValue(), actualGenre.id());
        assertEquals(expectedName, actualGenre.name());
        assertEquals(expectedIsActive, actualGenre.isActive());
        assertTrue(
                expectedCategories.size() == actualGenre.categories().size()
                        && asString(expectedCategories).containsAll(actualGenre.categories())
        );
        assertEquals(aGenre.getCreatedAt(), actualGenre.createdAt());
        assertEquals(aGenre.getUpdatedAt(), actualGenre.updatedAt());
        assertEquals(aGenre.getDeletedAt(), actualGenre.deletedAt());
    }

    @Test
    public void givenAValidId_whenCallsGetGenreAndDoesNotExists_shouldReturnNotFound() {
        final var expectedErrorMessage = "Genre with ID 123 was not found";

        final var expectedId = GenreID.from("123");

        final var actualException = assertThrows(NotFoundException.class, () -> {
            useCase.execute(expectedId.getValue());
        });

        assertEquals(expectedErrorMessage, actualException.getMessage());
    }

    private List<String> asString(final List<CategoryID> ids) {
        return ids.stream()
                .map(CategoryID::getValue)
                .toList();
    }
}
