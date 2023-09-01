package br.com.codeflix.catalog.admin.infrastructure.category.persistence;

import br.com.codeflix.catalog.admin.domain.category.Category;
import br.com.codeflix.catalog.admin.infrastructure.MySQLGatewayTest;
import org.hibernate.PropertyValueException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;

@MySQLGatewayTest
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void givenAInvalidNullName_whenCallSave_shouldReturnError() {
        final var expectedPropertyNameError = "name";
        final var expectedErrorMessage = "not-null property references a null or transient value : br.com.codeflix.catalog.admin.infrastructure.category.persistence.CategoryJpaEntity.name";
        final var category = Category.newCategory("Filmes", "A categoria mais assistida", true);

        final var entity = CategoryJpaEntity.from(category);
        entity.setName(null);

        final var actualException = assertThrows(DataIntegrityViolationException.class, () -> categoryRepository.save(entity));

        final var actualCause = assertInstanceOf(PropertyValueException.class, actualException.getCause());

        assertEquals(expectedPropertyNameError, actualCause.getPropertyName());
        assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    public void givenAInvalidNullCreatedAt_whenCallSave_shouldReturnError() {
        final var expectedPropertyNameError = "createdAt";
        final var expectedErrorMessage = "not-null property references a null or transient value : br.com.codeflix.catalog.admin.infrastructure.category.persistence.CategoryJpaEntity.createdAt";
        final var category = Category.newCategory("Filmes", "A categoria mais assistida", true);

        final var entity = CategoryJpaEntity.from(category);
        entity.setCreatedAt(null);

        final var actualException = assertThrows(DataIntegrityViolationException.class, () -> categoryRepository.save(entity));

        final var actualCause = assertInstanceOf(PropertyValueException.class, actualException.getCause());

        assertEquals(expectedPropertyNameError, actualCause.getPropertyName());
        assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    public void givenAInvalidNullUpdatedAt_whenCallSave_shouldReturnError() {
        final var expectedPropertyNameError = "updatedAt";
        final var expectedErrorMessage = "not-null property references a null or transient value : br.com.codeflix.catalog.admin.infrastructure.category.persistence.CategoryJpaEntity.updatedAt";
        final var category = Category.newCategory("Filmes", "A categoria mais assistida", true);

        final var entity = CategoryJpaEntity.from(category);
        entity.setUpdatedAt(null);

        final var actualException = assertThrows(DataIntegrityViolationException.class, () -> categoryRepository.save(entity));

        final var actualCause = assertInstanceOf(PropertyValueException.class, actualException.getCause());

        assertEquals(expectedPropertyNameError, actualCause.getPropertyName());
        assertEquals(expectedErrorMessage, actualCause.getMessage());
    }
}
