package br.com.codeflix.catalog.admin.infrastructure.api;

import br.com.codeflix.catalog.admin.ApiTest;
import br.com.codeflix.catalog.admin.ControllerTest;
import br.com.codeflix.catalog.admin.application.castmember.create.CreateCastMemberOutput;
import br.com.codeflix.catalog.admin.application.castmember.create.DefaultCreateCastMemberUseCase;
import br.com.codeflix.catalog.admin.application.castmember.delete.DefaultDeleteCastMemberUseCase;
import br.com.codeflix.catalog.admin.application.castmember.retrieve.get.CastMemberOutput;
import br.com.codeflix.catalog.admin.application.castmember.retrieve.get.DefaultGetCastMemberByIdUseCase;
import br.com.codeflix.catalog.admin.application.castmember.retrieve.list.CastMemberListOutput;
import br.com.codeflix.catalog.admin.application.castmember.retrieve.list.DefaultListCastMembersUseCase;
import br.com.codeflix.catalog.admin.application.castmember.update.DefaultUpdateCastMemberUseCase;
import br.com.codeflix.catalog.admin.application.castmember.update.UpdateCastMemberOutput;
import br.com.codeflix.catalog.admin.domain.castmember.CastMember;
import br.com.codeflix.catalog.admin.domain.castmember.CastMemberID;
import br.com.codeflix.catalog.admin.domain.castmember.CastMemberType;
import br.com.codeflix.catalog.admin.domain.exceptions.NotFoundException;
import br.com.codeflix.catalog.admin.domain.exceptions.NotificationException;
import br.com.codeflix.catalog.admin.domain.pagination.Pagination;
import br.com.codeflix.catalog.admin.domain.validation.Error;
import br.com.codeflix.catalog.admin.infrastructure.castmember.models.CreateCastMemberRequest;
import br.com.codeflix.catalog.admin.infrastructure.castmember.models.UpdateCastMemberRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Objects;

import static br.com.codeflix.catalog.admin.domain.category.Fixture.CastMembers.type;
import static br.com.codeflix.catalog.admin.domain.category.Fixture.name;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ControllerTest(controllers = CastMemberAPI.class)
public class CastMemberAPITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private DefaultCreateCastMemberUseCase createCastMemberUseCase;

    @MockBean
    private DefaultDeleteCastMemberUseCase deleteCastMemberUseCase;

    @MockBean
    private DefaultGetCastMemberByIdUseCase getCastMemberByIdUseCase;

    @MockBean
    private DefaultListCastMembersUseCase listCastMembersUseCase;

    @MockBean
    private DefaultUpdateCastMemberUseCase updateCastMemberUseCase;

    @Test
    public void givenAValidCommand_whenCallsCreateCastMember_shouldReturnItsIdentifier() throws Exception {
        final var expectedName = name();
        final var expectedType = type();
        final var expectedId = CastMemberID.from("o1i2u3i1o");

        final var command = new CreateCastMemberRequest(expectedName, expectedType);

        when(createCastMemberUseCase.execute(any())).thenReturn(CreateCastMemberOutput.from(expectedId));

        final var request = post("/cast_members")
                .with(ApiTest.CAST_MEMBERS_JWT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(command));

        final var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().isCreated())
                .andExpect(header().string("Location", "/cast_members/" + expectedId.getValue()))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo(expectedId.getValue())));

        verify(createCastMemberUseCase).execute(argThat(actualCmd ->
                Objects.equals(expectedName, actualCmd.name())
                        && Objects.equals(expectedType, actualCmd.type())
        ));
    }

    @Test
    public void givenAnInvalidName_whenCallsCreateCastMember_shouldReturnNotification() throws Exception {
        final String expectedName = null;
        final var expectedType = type();

        final var expectedErrorMessage = "'name' should not be null";

        final var command = new CreateCastMemberRequest(expectedName, expectedType);

        when(createCastMemberUseCase.execute(any())).thenThrow(NotificationException.with(new Error(expectedErrorMessage)));

        final var request = post("/cast_members")
                .with(ApiTest.CAST_MEMBERS_JWT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(command));

        final var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().isUnprocessableEntity())
                .andExpect(header().string("Location", nullValue()))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        verify(createCastMemberUseCase).execute(argThat(actualCmd ->
                Objects.equals(expectedName, actualCmd.name())
                        && Objects.equals(expectedType, actualCmd.type())
        ));
    }

    @Test
    public void givenAValidId_whenCallsGetById_shouldReturnIt() throws Exception {
        final var expectedName = name();
        final var expectedType = type();

        final var member = CastMember.newMember(expectedName, expectedType);
        final var expectedId = member.getId().getValue();

        when(getCastMemberByIdUseCase.execute(any()))
                .thenReturn(CastMemberOutput.from(member));

        final var request = get("/cast_members/{id}", expectedId)
                .with(ApiTest.CAST_MEMBERS_JWT)
                .accept(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(request);

        response.andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo(expectedId)))
                .andExpect(jsonPath("$.name", equalTo(expectedName)))
                .andExpect(jsonPath("$.type", equalTo(expectedType.name())))
                .andExpect(jsonPath("$.created_at", equalTo(member.getCreatedAt().toString())))
                .andExpect(jsonPath("$.updated_at", equalTo(member.getUpdatedAt().toString())));

        verify(getCastMemberByIdUseCase).execute(eq(expectedId));
    }

    @Test
    public void givenAInvalidId_whenCallsGetByIdAndCastMemberDoesntExists_shouldReturnNotFound() throws Exception {
        final var expectedErrorMessage = "CastMember with ID 123 was not found";
        final var expectedId = CastMemberID.from("123");

        when(getCastMemberByIdUseCase.execute(any())).thenThrow(NotFoundException.with(CastMember.class, expectedId));

        final var request = get("/cast_members/{id}", expectedId.getValue())
                .with(ApiTest.CAST_MEMBERS_JWT)
                .accept(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(request);

        response.andExpect(status().isNotFound())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)));

        verify(getCastMemberByIdUseCase).execute(eq(expectedId.getValue()));
    }

    @Test
    public void givenAValidCommand_whenCallsUpdateCastMember_shouldReturnItsIdentifier() throws Exception {
        final var expectedName = name();
        final var expectedType = type();

        final var member = CastMember.newMember(expectedName, expectedType);
        final var expectedId = member.getId();

        final var command = new UpdateCastMemberRequest(expectedName, expectedType);

        when(updateCastMemberUseCase.execute(any())).thenReturn(UpdateCastMemberOutput.from(expectedId));

        final var request = put("/cast_members/{id}", expectedId.getValue())
                .with(ApiTest.CAST_MEMBERS_JWT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(command));

        final var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo(expectedId.getValue())));

        verify(updateCastMemberUseCase).execute(argThat(actualCmd ->
                Objects.equals(expectedId.getValue(), actualCmd.id())
                        && Objects.equals(expectedName, actualCmd.name())
                        && Objects.equals(expectedType, actualCmd.type())
        ));
    }

    @Test
    public void givenAnInvalidName_whenCallsUpdateCastMember_shouldReturnNotification() throws Exception {
        final var member = CastMember.newMember("Vin Di", CastMemberType.DIRECTOR);
        final var expectedId = member.getId();

        final String expectedName = null;
        final var expectedType = type();

        final var expectedErrorMessage = "'name' should not be null";

        final var command = new UpdateCastMemberRequest(expectedName, expectedType);

        when(updateCastMemberUseCase.execute(any())).thenThrow(NotificationException.with(new Error(expectedErrorMessage)));

        final var request = put("/cast_members/{id}", expectedId.getValue())
                .with(ApiTest.CAST_MEMBERS_JWT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(command));

        final var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().isUnprocessableEntity())
                .andExpect(header().string("Location", nullValue()))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        verify(updateCastMemberUseCase).execute(argThat(actualCmd ->
                Objects.equals(expectedId.getValue(), actualCmd.id())
                        && Objects.equals(expectedName, actualCmd.name())
                        && Objects.equals(expectedType, actualCmd.type())
        ));
    }

    @Test
    public void givenAnInvalidId_whenCallsUpdateCastMember_shouldReturnNotFound() throws Exception {
        final var expectedId = CastMemberID.from("123");

        final var expectedName = name();
        final var expectedType = type();

        final var expectedErrorMessage = "CastMember with ID 123 was not found";

        final var command = new UpdateCastMemberRequest(expectedName, expectedType);

        when(updateCastMemberUseCase.execute(any())).thenThrow(NotFoundException.with(CastMember.class, expectedId));

        final var request = put("/cast_members/{id}", expectedId.getValue())
                .with(ApiTest.CAST_MEMBERS_JWT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(command));

        final var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().isNotFound())
                .andExpect(header().string("Location", nullValue()))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)));

        verify(updateCastMemberUseCase).execute(argThat(actualCmd ->
                Objects.equals(expectedId.getValue(), actualCmd.id())
                        && Objects.equals(expectedName, actualCmd.name())
                        && Objects.equals(expectedType, actualCmd.type())
        ));
    }

    @Test
    public void givenAValidId_whenCallsDeleteById_shouldDeleteIt() throws Exception {
        final var expectedId = "123";

        doNothing().when(deleteCastMemberUseCase).execute(any());

        final var request = delete("/cast_members/{id}", expectedId)
                .with(ApiTest.CAST_MEMBERS_JWT);

        final var response = this.mvc.perform(request);

        response.andExpect(status().isNoContent());

        verify(deleteCastMemberUseCase).execute(eq(expectedId));
    }

    @Test
    public void givenValidParams_whenCallListCastMembers_shouldReturnIt() throws Exception {
        final var member = CastMember.newMember(name(), type());

        final var expectedPage = 1;
        final var expectedPerPage = 20;
        final var expectedTerms = "Alg";
        final var expectedSort = "type";
        final var expectedDirection = "desc";

        final var expectedItemsCount = 1;
        final var expectedTotal = 1;

        final var expectedItems = List.of(CastMemberListOutput.from(member));

        when(listCastMembersUseCase.execute(any()))
                .thenReturn(new Pagination<>(expectedPage, expectedPerPage, expectedTotal, expectedItems));

        final var request = get("/cast_members")
                .with(ApiTest.CAST_MEMBERS_JWT)
                .queryParam("page", String.valueOf(expectedPage))
                .queryParam("perPage", String.valueOf(expectedPerPage))
                .queryParam("search", expectedTerms)
                .queryParam("sort", expectedSort)
                .queryParam("dir", expectedDirection)
                .accept(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(request);

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(expectedPage)))
                .andExpect(jsonPath("$.per_page", equalTo(expectedPerPage)))
                .andExpect(jsonPath("$.total", equalTo(expectedTotal)))
                .andExpect(jsonPath("$.items", hasSize(expectedItemsCount)))
                .andExpect(jsonPath("$.items[0].id", equalTo(member.getId().getValue())))
                .andExpect(jsonPath("$.items[0].name", equalTo(member.getName())))
                .andExpect(jsonPath("$.items[0].type", equalTo(member.getType().name())))
                .andExpect(jsonPath("$.items[0].created_at", equalTo(member.getCreatedAt().toString())));

        verify(listCastMembersUseCase).execute(argThat(aQuery ->
                Objects.equals(expectedPage, aQuery.page())
                        && Objects.equals(expectedPerPage, aQuery.perPage())
                        && Objects.equals(expectedTerms, aQuery.terms())
                        && Objects.equals(expectedSort, aQuery.sort())
                        && Objects.equals(expectedDirection, aQuery.direction())
        ));
    }

    @Test
    public void givenEmptyParams_whenCallListCastMembers_shouldUseDefaultsAndReturnIt() throws Exception {
        final var member = CastMember.newMember(name(), type());

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "name";
        final var expectedDirection = "asc";

        final var expectedItemsCount = 1;
        final var expectedTotal = 1;

        final var expectedItems = List.of(CastMemberListOutput.from(member));

        when(listCastMembersUseCase.execute(any()))
                .thenReturn(new Pagination<>(expectedPage, expectedPerPage, expectedTotal, expectedItems));

        final var request = get("/cast_members")
                .with(ApiTest.CAST_MEMBERS_JWT)
                .accept(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(request);

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(expectedPage)))
                .andExpect(jsonPath("$.per_page", equalTo(expectedPerPage)))
                .andExpect(jsonPath("$.total", equalTo(expectedTotal)))
                .andExpect(jsonPath("$.items", hasSize(expectedItemsCount)))
                .andExpect(jsonPath("$.items[0].id", equalTo(member.getId().getValue())))
                .andExpect(jsonPath("$.items[0].name", equalTo(member.getName())))
                .andExpect(jsonPath("$.items[0].type", equalTo(member.getType().name())))
                .andExpect(jsonPath("$.items[0].created_at", equalTo(member.getCreatedAt().toString())));

        verify(listCastMembersUseCase).execute(argThat(aQuery ->
                Objects.equals(expectedPage, aQuery.page())
                        && Objects.equals(expectedPerPage, aQuery.perPage())
                        && Objects.equals(expectedTerms, aQuery.terms())
                        && Objects.equals(expectedSort, aQuery.sort())
                        && Objects.equals(expectedDirection, aQuery.direction())
        ));
    }
}