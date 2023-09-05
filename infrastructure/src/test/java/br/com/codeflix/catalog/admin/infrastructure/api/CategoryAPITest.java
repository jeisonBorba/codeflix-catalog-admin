package br.com.codeflix.catalog.admin.infrastructure.api;

import br.com.codeflix.catalog.admin.ControllerTest;
import br.com.codeflix.catalog.admin.application.category.create.CreateCategoryOutput;
import br.com.codeflix.catalog.admin.application.category.create.CreateCategoryUseCase;
import br.com.codeflix.catalog.admin.application.category.delete.DeleteCategoryUseCase;
import br.com.codeflix.catalog.admin.application.category.retrieve.get.CategoryOutput;
import br.com.codeflix.catalog.admin.application.category.retrieve.get.GetCategoryByIdUseCase;
import br.com.codeflix.catalog.admin.application.category.retrieve.list.CategoryListOutput;
import br.com.codeflix.catalog.admin.application.category.retrieve.list.ListCategoryUseCase;
import br.com.codeflix.catalog.admin.application.category.update.UpdateCategoryOutput;
import br.com.codeflix.catalog.admin.application.category.update.UpdateCategoryUseCase;
import br.com.codeflix.catalog.admin.domain.category.Category;
import br.com.codeflix.catalog.admin.domain.category.CategoryID;
import br.com.codeflix.catalog.admin.domain.exceptions.DomainException;
import br.com.codeflix.catalog.admin.domain.exceptions.NotFoundException;
import br.com.codeflix.catalog.admin.domain.pagination.Pagination;
import br.com.codeflix.catalog.admin.domain.validation.Error;
import br.com.codeflix.catalog.admin.domain.validation.handler.Notification;
import br.com.codeflix.catalog.admin.infrastructure.category.models.CreateCategoryRequest;
import br.com.codeflix.catalog.admin.infrastructure.category.models.UpdateCategoryRequest;
import br.com.codeflix.catalog.admin.infrastructure.category.models.UpdateCategoryRequestTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Objects;

import static io.vavr.API.Left;
import static io.vavr.API.Right;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ControllerTest(controllers = CategoryAPI.class)
public class CategoryAPITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CreateCategoryUseCase createCategoryUseCase;

    @MockBean
    private GetCategoryByIdUseCase getCategoryByIdUseCase;

    @MockBean
    private UpdateCategoryUseCase updateCategoryUseCase;

    @MockBean
    private DeleteCategoryUseCase deleteCategoryUseCase;

    @MockBean
    private ListCategoryUseCase listCategoryUseCase;

    @Test
    public void givenAValidCommand_whenCallCreateCategory_shouldReturnCategoryId() throws Exception {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var expectedCategoryId = CategoryID.from("12345");
        final var expectedOutput = CreateCategoryOutput.from(expectedCategoryId.getValue());

        when(createCategoryUseCase.execute(any())).thenReturn(Right(expectedOutput));

        final var input = new CreateCategoryRequest(expectedName, expectedDescription, expectedIsActive);

        final var request = post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(input));

        mvc.perform(request)
                .andDo(print())
                .andExpectAll(
                        status().isCreated(),
                        header().string("Location", "/categories/12345"),
                        header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE),
                        jsonPath("$.id", equalTo("12345"))
                );

        verify(createCategoryUseCase).execute(argThat(cmd ->
                Objects.equals(expectedName, cmd.name())
                && Objects.equals(expectedDescription, cmd.description())
                && Objects.equals(expectedIsActive, cmd.isActive())
        ));
    }

    @Test
    public void givenAnInvalidName_whenCallCreateCategory_shouldReturnNotification() throws Exception {
        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be null";

        final var expectedError = Notification.create(new Error(expectedErrorMessage));

        when(createCategoryUseCase.execute(any())).thenReturn(Left(expectedError));

        final var input = new CreateCategoryRequest(expectedName, expectedDescription, expectedIsActive);

        final var request = post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(input));

        mvc.perform(request)
                .andDo(print())
                .andExpectAll(
                        status().isUnprocessableEntity(),
                        header().string("Location", nullValue()),
                        header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE),
                        jsonPath("$.errors", hasSize(1)),
                        jsonPath("$.errors[0].message", equalTo(expectedErrorMessage))
                );

        verify(createCategoryUseCase).execute(argThat(cmd ->
                Objects.equals(expectedName, cmd.name())
                        && Objects.equals(expectedDescription, cmd.description())
                        && Objects.equals(expectedIsActive, cmd.isActive())
        ));
    }

    @Test
    public void givenAnInvalidCommand_whenCallCreateCategory_shouldReturnDomainException() throws Exception {
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be null";
        final var ExpectedErrorCount = 1;

        when(createCategoryUseCase.execute(any())).thenThrow(DomainException.with(new Error(expectedErrorMessage)));

        final var input = new CreateCategoryRequest(null, expectedDescription, expectedIsActive);

        final var request = post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(input));

        mvc.perform(request)
                .andDo(print())
                .andExpectAll(
                        status().isUnprocessableEntity(),
                        header().string("Location", nullValue()),
                        header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE),
                        jsonPath("$.errors", hasSize(ExpectedErrorCount)),
                        jsonPath("$.errors[0].message", equalTo(expectedErrorMessage))
                );

        verify(createCategoryUseCase).execute(argThat(cmd ->
                Objects.isNull(cmd.name())
                        && Objects.equals(expectedDescription, cmd.description())
                        && Objects.equals(expectedIsActive, cmd.isActive())
        ));
    }

    @Test
    public void givenAValidId_whenCallGetCategory_shouldBeOk() throws Exception {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var category = Category.newCategory(expectedName, expectedDescription, expectedIsActive);
        final var expectedId = category.getId();

        when(getCategoryByIdUseCase.execute(any())).thenReturn(CategoryOutput.from(category));

        final var request = get("/categories/{id}", expectedId.getValue())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", equalTo(expectedId.getValue())),
                        jsonPath("$.name", equalTo(expectedName)),
                        jsonPath("$.description", equalTo(expectedDescription)),
                        jsonPath("$.is_active", equalTo(expectedIsActive)),
                        jsonPath("$.created_at", equalTo(category.getCreatedAt().toString())),
                        jsonPath("$.updated_at", equalTo(category.getUpdatedAt().toString())),
                        jsonPath("$.deleted_at", equalTo(category.getDeletedAt()))
                );

        verify(getCategoryByIdUseCase).execute(eq(expectedId.getValue()));
    }

    @Test
    public void givenAInvalidId_whenCallGetCategory_shouldReturnNotFound() throws Exception {
        final var expectedErrorMessage = "Category with ID 12345 was not found";
        final var expectedId = CategoryID.from("12345");

        when(getCategoryByIdUseCase.execute(any())).thenThrow(NotFoundException.with(Category.class, expectedId));

        final var request = get("/categories/{id}", expectedId.getValue())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.message", equalTo(expectedErrorMessage))
                );
    }

    @Test
    public void givenAValidCommand_whenCallUpdateCategory_shouldReturnCategoryId() throws Exception {
        final var expectedId = "12345";
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        when(updateCategoryUseCase.execute(any())).thenReturn(Right(UpdateCategoryOutput.from(expectedId)));

        final var command = new UpdateCategoryRequest(expectedName, expectedDescription, expectedIsActive);
        final var request = put("/categories/{id}", expectedId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(command));

        mvc.perform(request)
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", equalTo(expectedId))
                );

        verify(updateCategoryUseCase).execute(argThat(cmd ->
                Objects.equals(expectedName, cmd.name())
                    && Objects.equals(expectedDescription, cmd.description())
                    && Objects.equals(expectedIsActive, cmd.isActive())
        ));
    }

    @Test
    public void givenACommandWithInvalidID_whenCallUpdateCategory_shouldReturnNotFoundException() throws Exception {
        final var expectedId = "not-found";
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "Category with ID not-found was not found";

        when(updateCategoryUseCase.execute(any())).thenThrow(NotFoundException.with(Category.class, CategoryID.from(expectedId)));

        final var command = new UpdateCategoryRequest(expectedName, expectedDescription, expectedIsActive);
        final var request = put("/categories/{id}", expectedId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(command));

        mvc.perform(request)
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.message", equalTo(expectedErrorMessage))
                );

        verify(updateCategoryUseCase).execute(argThat(cmd ->
                Objects.equals(expectedName, cmd.name())
                        && Objects.equals(expectedDescription, cmd.description())
                        && Objects.equals(expectedIsActive, cmd.isActive())
        ));
    }

    @Test
    public void givenAnInvalidName_whenCallUpdateCategory_shouldReturnDomainException() throws Exception {
        final var expectedId = "12345";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be null";
        final var ExpectedErrorCount = 1;

        final var expectedError = Notification.create(new Error(expectedErrorMessage));

        when(updateCategoryUseCase.execute(any())).thenReturn(Left(expectedError));

        final var command = new UpdateCategoryRequest(null, expectedDescription, expectedIsActive);
        final var request = put("/categories/{id}", expectedId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(command));

        mvc.perform(request)
                .andDo(print())
                .andExpectAll(
                        status().isUnprocessableEntity(),
                        jsonPath("$.errors", hasSize(ExpectedErrorCount)),
                        jsonPath("$.errors[0].message", equalTo(expectedErrorMessage))
                );

        verify(updateCategoryUseCase).execute(argThat(cmd ->
                Objects.isNull(cmd.name())
                        && Objects.equals(expectedDescription, cmd.description())
                        && Objects.equals(expectedIsActive, cmd.isActive())
        ));
    }

    @Test
    public void givenAValidId_whenCallDeleteCategory_shouldReturnNoContent() throws Exception {
        final var expectedId = "12345";

        doNothing().when(deleteCategoryUseCase).execute(any());

        final var request = delete("/categories/{id}", expectedId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(deleteCategoryUseCase).execute(eq(expectedId));
    }

    @Test
    public void givenValidParams_whenCallListCategories_shouldReturnCategories() throws Exception {
        final var category = Category.newCategory("Movies", null, true);
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "movies";
        final var expectedSort = "description";
        final var expectedDirection = "desc";
        final var expectedItemsCount = 1;
        final var expectedTotal = 1;
        final var expectedItems = List.of(CategoryListOutput.from(category));

        when(listCategoryUseCase.execute(any())).thenReturn(new Pagination<>(expectedPage, expectedPerPage, expectedTotal, expectedItems));

        final var request = get("/categories")
                .queryParam("page", String.valueOf(expectedPage))
                .queryParam("perPage", String.valueOf(expectedPerPage))
                .queryParam("sort", expectedSort)
                .queryParam("dir", expectedDirection)
                .queryParam("search", expectedTerms)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.current_page", equalTo(expectedPage)),
                        jsonPath("$.per_page", equalTo(expectedPerPage)),
                        jsonPath("$.total", equalTo(expectedTotal)),
                        jsonPath("$.items", hasSize(expectedItemsCount)),
                        jsonPath("$.items[0].id", equalTo(category.getId().getValue())),
                        jsonPath("$.items[0].name", equalTo(category.getName())),
                        jsonPath("$.items[0].description", equalTo(category.getDescription())),
                        jsonPath("$.items[0].is_active", equalTo(category.isActive())),
                        jsonPath("$.items[0].created_at", equalTo(category.getCreatedAt().toString())),
                        jsonPath("$.items[0].deleted_at", equalTo(category.getDeletedAt()))
                );

        verify(listCategoryUseCase).execute(argThat(query ->
                Objects.equals(expectedPage, query.page())
                        && Objects.equals(expectedPerPage, query.perPage())
                        && Objects.equals(expectedSort, query.sort())
                        && Objects.equals(expectedDirection, query.direction())
                        && Objects.equals(expectedTerms, query.terms())
        ));
    }
}
