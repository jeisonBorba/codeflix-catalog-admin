package br.com.codeflix.catalog.admin.infrastructure.api;

import br.com.codeflix.catalog.admin.ControllerTest;
import br.com.codeflix.catalog.admin.application.genre.create.CreateGenreOutput;
import br.com.codeflix.catalog.admin.application.genre.create.CreateGenreUseCase;
import br.com.codeflix.catalog.admin.application.genre.delete.DeleteGenreUseCase;
import br.com.codeflix.catalog.admin.application.genre.retrieve.get.GenreOutput;
import br.com.codeflix.catalog.admin.application.genre.retrieve.get.GetGenreByIdUseCase;
import br.com.codeflix.catalog.admin.application.genre.retrieve.list.GenreListOutput;
import br.com.codeflix.catalog.admin.application.genre.retrieve.list.ListGenreUseCase;
import br.com.codeflix.catalog.admin.application.genre.update.UpdateGenreOutput;
import br.com.codeflix.catalog.admin.application.genre.update.UpdateGenreUseCase;
import br.com.codeflix.catalog.admin.domain.category.CategoryID;
import br.com.codeflix.catalog.admin.domain.exceptions.NotFoundException;
import br.com.codeflix.catalog.admin.domain.exceptions.NotificationException;
import br.com.codeflix.catalog.admin.domain.genre.Genre;
import br.com.codeflix.catalog.admin.domain.genre.GenreID;
import br.com.codeflix.catalog.admin.domain.pagination.Pagination;
import br.com.codeflix.catalog.admin.domain.validation.handler.Notification;
import br.com.codeflix.catalog.admin.infrastructure.genre.models.CreateGenreRequest;
import br.com.codeflix.catalog.admin.infrastructure.genre.models.UpdateGenreRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ControllerTest(controllers = GenreAPI.class)
public class GenreAPITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CreateGenreUseCase createGenreUseCase;

    @MockBean
    private GetGenreByIdUseCase getGenreByIdUseCase;

    @MockBean
    private UpdateGenreUseCase updateGenreUseCase;

    @MockBean
    private DeleteGenreUseCase deleteGenreUseCase;

    @MockBean
    private ListGenreUseCase listGenreUseCase;

    @Test
    public void givenAValidCommand_whenCallCreateGenre_shouldReturnGenreId() throws Exception {
        final var expectedName = "Ação";
        final var expectedCategories = List.of("123", "456");
        final var expectedIsActive = true;
        final var expectedId = "123";

        final var command = new CreateGenreRequest(expectedName, expectedCategories, expectedIsActive);

        when(createGenreUseCase.execute(any())).thenReturn(CreateGenreOutput.from(expectedId));

        final var request = post("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(command));

        final var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().isCreated())
                .andExpect(header().string("Location", "/genres/" + expectedId))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo(expectedId)));

        verify(createGenreUseCase).execute(argThat(cmd ->
                Objects.equals(expectedName, cmd.name())
                        && Objects.equals(expectedCategories, cmd.categories())
                        && Objects.equals(expectedIsActive, cmd.isActive())
        ));
    }

    @Test
    public void givenAnInvalidName_whenCallCreateGenre_shouldReturnNotification() throws Exception {
        final String expectedName = null;
        final var expectedCategories = List.of("123", "456");
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be null";

        final var command = new CreateGenreRequest(expectedName, expectedCategories, expectedIsActive);

        when(createGenreUseCase.execute(any()))
                .thenThrow(new NotificationException("Error", Notification.create(new Error(expectedErrorMessage))));

        final var request = post("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(command));

        final var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().isUnprocessableEntity())
                .andExpect(header().string("Location", nullValue()))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        verify(createGenreUseCase).execute(argThat(cmd ->
                Objects.equals(expectedName, cmd.name())
                        && Objects.equals(expectedCategories, cmd.categories())
                        && Objects.equals(expectedIsActive, cmd.isActive())
        ));
    }

    @Test
    public void givenAValidId_whenCallGetGenreById_shouldReturnGenre() throws Exception {
        final var expectedName = "Ação";
        final var expectedCategories = List.of("123", "456");
        final var expectedIsActive = false;

        final var genre = Genre.newGenre(expectedName, expectedIsActive)
                .addCategories(
                        expectedCategories.stream()
                                .map(CategoryID::from)
                                .toList()
                );

        final var expectedId = genre.getId().getValue();

        when(getGenreByIdUseCase.execute(any())).thenReturn(GenreOutput.from(genre));

        final var request = get("/genres/{id}", expectedId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo(expectedId)))
                .andExpect(jsonPath("$.name", equalTo(expectedName)))
                .andExpect(jsonPath("$.categories_id", equalTo(expectedCategories)))
                .andExpect(jsonPath("$.is_active", equalTo(expectedIsActive)))
                .andExpect(jsonPath("$.created_at", equalTo(genre.getCreatedAt().toString())))
                .andExpect(jsonPath("$.updated_at", equalTo(genre.getUpdatedAt().toString())))
                .andExpect(jsonPath("$.deleted_at", equalTo(genre.getDeletedAt().toString())));

        verify(getGenreByIdUseCase).execute(eq(expectedId));
    }

    @Test
    public void givenAnInvalidId_whenCallGetGenreById_shouldReturnNotFound() throws Exception {
        final var expectedErrorMessage = "Genre with ID 123 was not found";
        final var expectedId = GenreID.from("123");

        when(getGenreByIdUseCase.execute(any())).thenThrow(NotFoundException.with(Genre.class, expectedId));

        final var request = get("/genres/{id}", expectedId.getValue())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(request);

        response.andExpect(status().isNotFound())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)));

        verify(getGenreByIdUseCase).execute(eq(expectedId.getValue()));
    }

    @Test
    public void givenAValidCommand_whenCallUpdateGenre_shouldReturnGenreId() throws Exception {
        final var expectedName = "Ação";
        final var expectedCategories = List.of("123", "456");
        final var expectedIsActive = true;

        final var genre = Genre.newGenre(expectedName, expectedIsActive);
        final var expectedId = genre.getId().getValue();

        final var command = new UpdateGenreRequest(expectedName, expectedCategories, expectedIsActive);

        when(updateGenreUseCase.execute(any())).thenReturn(UpdateGenreOutput.from(genre));

        final var request = put("/genres/{id}", expectedId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(command));

        final var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo(expectedId)));

        verify(updateGenreUseCase).execute(argThat(cmd ->
                Objects.equals(expectedName, cmd.name())
                        && Objects.equals(expectedCategories, cmd.categories())
                        && Objects.equals(expectedIsActive, cmd.isActive())
        ));
    }

    @Test
    public void givenAnInvalidName_whenCallUpdateGenre_shouldReturnNotification() throws Exception {
        final String expectedName = null;
        final var expectedCategories = List.of("123", "456");
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be null";

        final var genre = Genre.newGenre("Ação", expectedIsActive);
        final var expectedId = genre.getId().getValue();

        final var command = new UpdateGenreRequest(expectedName, expectedCategories, expectedIsActive);

        when(updateGenreUseCase.execute(any()))
                .thenThrow(new NotificationException("Error", Notification.create(new Error(expectedErrorMessage))));

        final var request = put("/genres/{id}", expectedId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(command));

        final var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().isUnprocessableEntity())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        verify(updateGenreUseCase).execute(argThat(cmd ->
                Objects.equals(expectedName, cmd.name())
                        && Objects.equals(expectedCategories, cmd.categories())
                        && Objects.equals(expectedIsActive, cmd.isActive())
        ));
    }

    @Test
    public void givenAValidId_whenCallDeleteGenre_shouldBeOK() throws Exception {
        final var expectedId = "123";

        doNothing()
                .when(deleteGenreUseCase).execute(any());

        final var request = delete("/genres/{id}", expectedId)
                .accept(MediaType.APPLICATION_JSON);

        final var result = this.mvc.perform(request);

        result.andExpect(status().isNoContent());

        verify(deleteGenreUseCase).execute(eq(expectedId));
    }

    @Test
    public void givenValidParams_whenCallListGenres_shouldReturnGenres() throws Exception {
        final var genre = Genre.newGenre("Ação", false);

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "ac";
        final var expectedSort = "name";
        final var expectedDirection = "asc";

        final var expectedItemsCount = 1;
        final var expectedTotal = 1;

        final var expectedItems = List.of(GenreListOutput.from(genre));

        when(listGenreUseCase.execute(any()))
                .thenReturn(new Pagination<>(expectedPage, expectedPerPage, expectedTotal, expectedItems));

        final var request = get("/genres")
                .queryParam("page", String.valueOf(expectedPage))
                .queryParam("perPage", String.valueOf(expectedPerPage))
                .queryParam("sort", expectedSort)
                .queryParam("dir", expectedDirection)
                .queryParam("search", expectedTerms)
                .accept(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(request);

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(expectedPage)))
                .andExpect(jsonPath("$.per_page", equalTo(expectedPerPage)))
                .andExpect(jsonPath("$.total", equalTo(expectedTotal)))
                .andExpect(jsonPath("$.items", hasSize(expectedItemsCount)))
                .andExpect(jsonPath("$.items[0].id", equalTo(genre.getId().getValue())))
                .andExpect(jsonPath("$.items[0].name", equalTo(genre.getName())))
                .andExpect(jsonPath("$.items[0].is_active", equalTo(genre.isActive())))
                .andExpect(jsonPath("$.items[0].created_at", equalTo(genre.getCreatedAt().toString())))
                .andExpect(jsonPath("$.items[0].deleted_at", equalTo(genre.getDeletedAt().toString())));

        verify(listGenreUseCase).execute(argThat(query ->
                Objects.equals(expectedPage, query.page())
                        && Objects.equals(expectedPerPage, query.perPage())
                        && Objects.equals(expectedDirection, query.direction())
                        && Objects.equals(expectedSort, query.sort())
                        && Objects.equals(expectedTerms, query.terms())
        ));
    }
}