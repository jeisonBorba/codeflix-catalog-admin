package br.com.codeflix.catalog.admin.e2e.category;

import br.com.codeflix.catalog.admin.E2ETest;
import br.com.codeflix.catalog.admin.domain.category.CategoryID;
import br.com.codeflix.catalog.admin.infrastructure.category.models.CategoryResponse;
import br.com.codeflix.catalog.admin.infrastructure.category.models.CreateCategoryRequest;
import br.com.codeflix.catalog.admin.infrastructure.category.models.UpdateCategoryRequest;
import br.com.codeflix.catalog.admin.infrastructure.category.persistence.CategoryRepository;
import br.com.codeflix.catalog.admin.infrastructure.configuration.json.Json;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@E2ETest
@Testcontainers
public class CategoryE2eTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Container
    private static final MySQLContainer MY_SQL_CONTAINER = new MySQLContainer("mysql:latest")
                .withUsername("root")
                .withPassword("123456")
                .withDatabaseName("adm_videos");

    @DynamicPropertySource
    public static void setDataSourceProperties(final DynamicPropertyRegistry registry) {
        registry.add("mysql.port", () -> MY_SQL_CONTAINER.getMappedPort(3306));
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToCreateNewCategoryWithValidValues() throws Exception {
        assertTrue(MY_SQL_CONTAINER.isRunning());
        assertEquals(0, categoryRepository.count());

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var actualCategoryId = givenACategory(expectedName, expectedDescription, expectedIsActive);

        final var actualCategory = retrieveACategoryById(actualCategoryId.getValue());

        assertEquals(expectedName, actualCategory.name());
        assertEquals(expectedDescription, actualCategory.description());
        assertEquals(expectedIsActive, actualCategory.active());
        assertNotNull(actualCategory.createdAt());
        assertNotNull(actualCategory.updatedAt());
        assertNull(actualCategory.deletedAt());
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToNavigateToAllCategories() throws Exception {
        assertTrue(MY_SQL_CONTAINER.isRunning());
        assertEquals(0, categoryRepository.count());

        givenACategory("Filmes", null, true);
        givenACategory("Documentários", null, true);
        givenACategory("Séries", null, true);

        listCategories(0, 1)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.current_page", equalTo(0)),
                        jsonPath("$.per_page", equalTo(1)),
                        jsonPath("$.total", equalTo(3)),
                        jsonPath("$.items", hasSize(1)),
                        jsonPath("$.items[0].name", equalTo("Documentários"))
                );

        listCategories(1, 1)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.current_page", equalTo(1)),
                        jsonPath("$.per_page", equalTo(1)),
                        jsonPath("$.total", equalTo(3)),
                        jsonPath("$.items", hasSize(1)),
                        jsonPath("$.items[0].name", equalTo("Filmes"))
                );

        listCategories(2, 1)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.current_page", equalTo(2)),
                        jsonPath("$.per_page", equalTo(1)),
                        jsonPath("$.total", equalTo(3)),
                        jsonPath("$.items", hasSize(1)),
                        jsonPath("$.items[0].name", equalTo("Séries"))
                );

        listCategories(3, 1)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.current_page", equalTo(3)),
                        jsonPath("$.per_page", equalTo(1)),
                        jsonPath("$.total", equalTo(3)),
                        jsonPath("$.items", hasSize(0))
                );
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToSearchBetweenAllCategories() throws Exception {
        assertTrue(MY_SQL_CONTAINER.isRunning());
        assertEquals(0, categoryRepository.count());

        givenACategory("Filmes", null, true);
        givenACategory("Documentários", null, true);
        givenACategory("Séries", null, true);

        listCategories(0, 1, "fil")
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.current_page", equalTo(0)),
                        jsonPath("$.per_page", equalTo(1)),
                        jsonPath("$.total", equalTo(1)),
                        jsonPath("$.items", hasSize(1)),
                        jsonPath("$.items[0].name", equalTo("Filmes"))
                );
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToSortAllCategoriesByDescriptionDesc() throws Exception {
        assertTrue(MY_SQL_CONTAINER.isRunning());
        assertEquals(0, categoryRepository.count());

        givenACategory("Filmes", "C", true);
        givenACategory("Documentários", "Z", true);
        givenACategory("Séries", "A", true);

        listCategories(0, 3, "", "description", "desc")
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.current_page", equalTo(0)),
                        jsonPath("$.per_page", equalTo(3)),
                        jsonPath("$.total", equalTo(3)),
                        jsonPath("$.items", hasSize(3)),
                        jsonPath("$.items[0].name", equalTo("Documentários")),
                        jsonPath("$.items[1].name", equalTo("Filmes")),
                        jsonPath("$.items[2].name", equalTo("Séries"))
                );
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToRetrieveACategoryByItsIdentifier() throws Exception {
        assertTrue(MY_SQL_CONTAINER.isRunning());
        assertEquals(0, categoryRepository.count());

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var actualCategoryId = givenACategory(expectedName, expectedDescription, expectedIsActive);

        final var actualCategory = retrieveACategoryById(actualCategoryId.getValue());

        assertEquals(expectedName, actualCategory.name());
        assertEquals(expectedDescription, actualCategory.description());
        assertEquals(expectedIsActive, actualCategory.active());
        assertNotNull(actualCategory.createdAt());
        assertNotNull(actualCategory.updatedAt());
        assertNull(actualCategory.deletedAt());
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToSeeATreatedErrorByRetrievingANotFoundCategory() throws Exception {
        assertTrue(MY_SQL_CONTAINER.isRunning());
        assertEquals(0, categoryRepository.count());

        final var request = get("/categories/" + "123")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        final var json = this.mvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", equalTo("Category with ID 123 was not found")));
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToUpdateACategoryByItsIdentifier() throws Exception {
        assertTrue(MY_SQL_CONTAINER.isRunning());
        assertEquals(0, categoryRepository.count());

        final var actualCategoryId = givenACategory("Movies", null, true);

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var requestBody = UpdateCategoryRequest.with(expectedName, expectedDescription, expectedIsActive);

        final var request = put("/categories/" + actualCategoryId.getValue())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(requestBody));

        this.mvc.perform(request)
                .andExpect(status().isOk());

        final var actualCategory = this.categoryRepository.findById(actualCategoryId.getValue()).get();

        assertEquals(expectedName, actualCategory.getName());
        assertEquals(expectedDescription, actualCategory.getDescription());
        assertEquals(expectedIsActive, actualCategory.isActive());
        assertNotNull(actualCategory.getCreatedAt());
        assertNotNull(actualCategory.getUpdatedAt());
        assertNull(actualCategory.getDeletedAt());
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToInactivateACategoryByItsIdentifier() throws Exception {
        assertTrue(MY_SQL_CONTAINER.isRunning());
        assertEquals(0, categoryRepository.count());

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;

        final var actualCategoryId = givenACategory(expectedName, expectedDescription, true);

        final var requestBody = UpdateCategoryRequest.with(expectedName, expectedDescription, expectedIsActive);

        final var request = put("/categories/" + actualCategoryId.getValue())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(requestBody));

        this.mvc.perform(request)
                .andExpect(status().isOk());

        final var actualCategory = this.categoryRepository.findById(actualCategoryId.getValue()).get();

        assertEquals(expectedName, actualCategory.getName());
        assertEquals(expectedDescription, actualCategory.getDescription());
        assertEquals(expectedIsActive, actualCategory.isActive());
        assertNotNull(actualCategory.getCreatedAt());
        assertNotNull(actualCategory.getUpdatedAt());
        assertNotNull(actualCategory.getDeletedAt());
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToActivateACategoryByItsIdentifier() throws Exception {
        assertTrue(MY_SQL_CONTAINER.isRunning());
        assertEquals(0, categoryRepository.count());

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var actualCategoryId = givenACategory(expectedName, expectedDescription, false);

        final var requestBody = UpdateCategoryRequest.with(expectedName, expectedDescription, expectedIsActive);

        final var request = put("/categories/" + actualCategoryId.getValue())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(requestBody));

        this.mvc.perform(request)
                .andExpect(status().isOk());

        final var actualCategory = this.categoryRepository.findById(actualCategoryId.getValue()).get();

        assertEquals(expectedName, actualCategory.getName());
        assertEquals(expectedDescription, actualCategory.getDescription());
        assertEquals(expectedIsActive, actualCategory.isActive());
        assertNotNull(actualCategory.getCreatedAt());
        assertNotNull(actualCategory.getUpdatedAt());
        assertNull(actualCategory.getDeletedAt());
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToDeleteACategoryByItsIdentifier() throws Exception {
        assertTrue(MY_SQL_CONTAINER.isRunning());
        assertEquals(0, categoryRepository.count());

        final var actualCategoryId = givenACategory("Filmes", null, true);

        this.mvc.perform(
                    delete("/categories/" + actualCategoryId.getValue())
                            .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent());

        assertFalse(this.categoryRepository.existsById(actualCategoryId.getValue()));
    }

    private ResultActions listCategories(final int page, final int perPage, final String search) throws Exception {
        return listCategories(page, perPage, search, "", "");
    }

    private ResultActions listCategories(final int page, final int perPage) throws Exception {
        return listCategories(page, perPage, "", "", "");
    }

    private ResultActions listCategories(final int page, final int perPage, final String search,
                                         final String sort, final String direction) throws Exception {
        final var request = get("/categories")
                .queryParam("page", String.valueOf(page))
                .queryParam("perPage", String.valueOf(perPage))
                .queryParam("search", search)
                .queryParam("sort", sort)
                .queryParam("dir", direction)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        return this.mvc.perform(request);
    }

    private CategoryResponse retrieveACategoryById(final String id) throws Exception {
        final var request = get("/categories/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        final var json = this.mvc.perform(request)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        return Json.readValue(json, CategoryResponse.class);
    }

    private CategoryID givenACategory(final String expectedName, final String expectedDescription, final boolean expectedIsActive) throws Exception {
        final var requestBody = CreateCategoryRequest.with(expectedName, expectedDescription, expectedIsActive);

        final var request = post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(requestBody));

        final var actualId = this.mvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getHeader("Location")
                .replace("/categories/", "");

        return CategoryID.from(actualId);
    }
}
