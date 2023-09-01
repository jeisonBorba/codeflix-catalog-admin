package br.com.codeflix.catalog.admin.infrastructure.category;

import br.com.codeflix.catalog.admin.domain.category.Category;
import br.com.codeflix.catalog.admin.infrastructure.MySQLGatewayTest;
import br.com.codeflix.catalog.admin.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

@MySQLGatewayTest
class CategoryMySQLGatewayTest {

    @Autowired
    private CategoryMySQLGateway categoryGateway;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void givenAValidCategory_whenCallCreate_shouldReturnNewCategory() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var category = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        assertEquals(0, categoryRepository.count());

        final var actualCategory = categoryGateway.create(category);

        assertEquals(1, categoryRepository.count());
        assertEquals(category.getId(), actualCategory.getId());
        assertEquals(expectedName, actualCategory.getName());
        assertEquals(expectedDescription, actualCategory.getDescription());
        assertEquals(expectedIsActive, actualCategory.isActive());
        assertEquals(category.getCreatedAt(), actualCategory.getCreatedAt());
        assertEquals(category.getUpdatedAt(), actualCategory.getUpdatedAt());
        assertEquals(category.getDeletedAt(), actualCategory.getDeletedAt());
        assertNull(actualCategory.getDeletedAt());

        final var actualEntity = categoryRepository.findById(category.getId().getValue()).get();
        assertEquals(category.getId().getValue(), actualEntity.getId());
        assertEquals(expectedName, actualEntity.getName());
        assertEquals(expectedDescription, actualEntity.getDescription());
        assertEquals(expectedIsActive, actualEntity.isActive());
        assertEquals(category.getCreatedAt(), actualEntity.getCreatedAt());
        assertEquals(category.getUpdatedAt(), actualEntity.getUpdatedAt());
        assertEquals(category.getDeletedAt(), actualEntity.getDeletedAt());
        assertNull(actualEntity.getDeletedAt());
    }

}