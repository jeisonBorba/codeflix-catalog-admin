package br.com.codeflix.catalog.admin.infrastructure.api;

import br.com.codeflix.catalog.admin.ControllerTest;
import br.com.codeflix.catalog.admin.application.category.create.CreateCategoryCommand;
import br.com.codeflix.catalog.admin.application.category.create.CreateCategoryOutput;
import br.com.codeflix.catalog.admin.application.category.create.CreateCategoryUseCase;
import br.com.codeflix.catalog.admin.domain.category.CategoryID;
import br.com.codeflix.catalog.admin.domain.exceptions.DomainException;
import br.com.codeflix.catalog.admin.domain.validation.Error;
import br.com.codeflix.catalog.admin.domain.validation.handler.Notification;
import br.com.codeflix.catalog.admin.infrastructure.category.models.CreateCategoryApiInput;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.API;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Objects;

import static io.vavr.API.Left;
import static io.vavr.API.Right;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @Test
    public void givenAValidCommand_whenCallCreateCategory_shouldReturnCategoryId() throws Exception {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var expectedCategoryId = CategoryID.from("12345");
        final var expectedOutput = CreateCategoryOutput.from(expectedCategoryId.getValue());

        when(createCategoryUseCase.execute(any())).thenReturn(Right(expectedOutput));

        final var input = new CreateCategoryApiInput(expectedName, expectedDescription, expectedIsActive);

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

        final var input = new CreateCategoryApiInput(expectedName, expectedDescription, expectedIsActive);

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
        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be null";

        when(createCategoryUseCase.execute(any())).thenThrow(DomainException.with(new Error(expectedErrorMessage)));

        final var input = new CreateCategoryApiInput(expectedName, expectedDescription, expectedIsActive);

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
}
