package br.com.codeflix.catalog.admin.application.category.retrieve.list;

import br.com.codeflix.catalog.admin.IntegrationTest;
import br.com.codeflix.catalog.admin.domain.category.Category;
import br.com.codeflix.catalog.admin.domain.category.CategorySearchQuery;
import br.com.codeflix.catalog.admin.infrastructure.category.persistence.CategoryJpaEntity;
import br.com.codeflix.catalog.admin.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@IntegrationTest
public class ListCategoryUseCaseIT {

    @Autowired
    private ListCategoryUseCase useCase;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void mockUp() {
        final var categories = Stream.of(
                    Category.newCategory("Filmes", "", true),
                    Category.newCategory("Netflix Originals", "Títulos de autoria da Netflix", true),
                    Category.newCategory("Amazon Originals", "Títulos de autoria da Amazon Prime", true),
                    Category.newCategory("Documentarios", "", true),
                    Category.newCategory("Kids", "Categoria para crianças", true),
                    Category.newCategory("Sports", "", true),
                    Category.newCategory("Series", "", true)
                )
                .map(CategoryJpaEntity::from)
                .toList();

        categoryRepository.saveAllAndFlush(categories);
    }

    @Test
    public void givenAValidTerm_whenTermDoesntMatchPrePersisted_shouldReturnEmptyPage() {
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "not found";
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var expectedItemsCount = 0;
        final var expectedTotal = 0;

        final var query = CategorySearchQuery.with(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var actualResult = useCase.execute(query);

        assertEquals(expectedItemsCount, actualResult.items().size());
        assertEquals(expectedPage, actualResult.currentPage());
        assertEquals(expectedPerPage, actualResult.perPage());
        assertEquals(expectedTotal, actualResult.total());
    }

    @ParameterizedTest
    @CsvSource({
        "fil, 0, 10, 1, 1, Filmes",
        "net, 0, 10, 1, 1, Netflix Originals",
        "ZON, 0, 10, 1, 1, Amazon Originals",
        "kid, 0, 10, 1, 1, Kids",
        "criança, 0, 10, 1, 1, Kids",
    })
    public void givenAValidTerm_whenCallListCategories_shouldReturnCategoriesFiltered(
            final String expectedTerms,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedCategoryName
    ) {
        final var expectedSort = "name";
        final var expectedDirection = "asc";

        final var query = CategorySearchQuery.with(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var actualResult = useCase.execute(query);

        assertEquals(expectedItemsCount, actualResult.items().size());
        assertEquals(expectedPage, actualResult.currentPage());
        assertEquals(expectedPerPage, actualResult.perPage());
        assertEquals(expectedTotal, actualResult.total());
        assertEquals(expectedCategoryName, actualResult.items().get(0).name());
    }

    @ParameterizedTest
    @CsvSource({
            "name, asc, 0, 10, 7, 7, Amazon Originals",
            "name, desc, 0, 10, 7, 7, Sports",
            "createdAt, asc, 0, 10, 7, 7, Filmes",
            "createdAt, desc, 0, 10, 7, 7, Series",
    })
    public void givenAValidSortAndDirection_whenCallListCategories_shouldReturnCategoriesOrdered(
            final String expectedSort,
            final String expectedDirection,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedCategoryName
    ) {
        final var query = CategorySearchQuery.with(expectedPage, expectedPerPage, "", expectedSort, expectedDirection);

        final var actualResult = useCase.execute(query);

        assertEquals(expectedItemsCount, actualResult.items().size());
        assertEquals(expectedPage, actualResult.currentPage());
        assertEquals(expectedPerPage, actualResult.perPage());
        assertEquals(expectedTotal, actualResult.total());
        assertEquals(expectedCategoryName, actualResult.items().get(0).name());
    }

    @ParameterizedTest
    @CsvSource({
            "0, 2, 2, 7, Amazon Originals;Documentarios",
            "1, 2, 2, 7, Filmes;Kids",
            "2, 2, 2, 7, Netflix Originals;Series",
            "3, 2, 1, 7, Sports"
    })
    public void givenAValidPage_whenCallListCategories_shouldReturnCategoriesPaginated(
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedCategoriesName
    ) {
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var query = CategorySearchQuery.with(expectedPage, expectedPerPage, "", expectedSort, expectedDirection);

        final var actualResult = useCase.execute(query);

        assertEquals(expectedItemsCount, actualResult.items().size());
        assertEquals(expectedPage, actualResult.currentPage());
        assertEquals(expectedPerPage, actualResult.perPage());
        assertEquals(expectedTotal, actualResult.total());

        int index = 0;
        for (final String expectedName : expectedCategoriesName.split(";")) {
            final var actualName = actualResult.items().get(index).name();
            assertEquals(expectedName, actualName);
            index++;
        }
    }
}
