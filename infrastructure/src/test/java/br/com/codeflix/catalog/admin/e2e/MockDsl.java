package br.com.codeflix.catalog.admin.e2e;

import br.com.codeflix.catalog.admin.domain.Identifier;
import br.com.codeflix.catalog.admin.domain.castmember.CastMemberID;
import br.com.codeflix.catalog.admin.domain.castmember.CastMemberType;
import br.com.codeflix.catalog.admin.domain.category.CategoryID;
import br.com.codeflix.catalog.admin.domain.genre.GenreID;
import br.com.codeflix.catalog.admin.infrastructure.castmember.models.CastMemberResponse;
import br.com.codeflix.catalog.admin.infrastructure.castmember.models.CreateCastMemberRequest;
import br.com.codeflix.catalog.admin.infrastructure.castmember.models.UpdateCastMemberRequest;
import br.com.codeflix.catalog.admin.infrastructure.category.models.CategoryResponse;
import br.com.codeflix.catalog.admin.infrastructure.category.models.CreateCategoryRequest;
import br.com.codeflix.catalog.admin.infrastructure.category.models.UpdateCategoryRequest;
import br.com.codeflix.catalog.admin.infrastructure.configuration.json.Json;
import br.com.codeflix.catalog.admin.infrastructure.genre.models.CreateGenreRequest;
import br.com.codeflix.catalog.admin.infrastructure.genre.models.GenreResponse;
import br.com.codeflix.catalog.admin.infrastructure.genre.models.UpdateGenreRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.function.Function;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public interface MockDsl {

    MockMvc mvc();

    /**
     * Cast Member
     */
    default ResultActions deleteACastMember(final CastMemberID id) throws Exception {
        return this.delete("/cast_members/", id);
    }

    default CastMemberID givenACastMember(final String name, final CastMemberType type) throws Exception {
        final var requestBody = new CreateCastMemberRequest(name, type);
        final var actualId = this.given("/cast_members", requestBody);
        return CastMemberID.from(actualId);
    }

    default ResultActions givenACastMemberResult(final String name, final CastMemberType type) throws Exception {
        final var requestBody = new CreateCastMemberRequest(name, type);
        return this.givenResult("/cast_members", requestBody);
    }

    default ResultActions listCastMembers(final int page, final int perPage) throws Exception {
        return listCastMembers(page, perPage, "", "", "");
    }

    default ResultActions listCastMembers(final int page, final int perPage, final String search) throws Exception {
        return listCastMembers(page, perPage, search, "", "");
    }

    default ResultActions listCastMembers(final int page, final int perPage, final String search, final String sort, final String direction) throws Exception {
        return this.list("/cast_members", page, perPage, search, sort, direction);
    }

    default CastMemberResponse retrieveACastMember(final CastMemberID id) throws Exception {
        return this.retrieve("/cast_members/", id, CastMemberResponse.class);
    }

    default ResultActions retrieveACastMemberResult(final CastMemberID id) throws Exception {
        return this.retrieveResult("/cast_members/", id);
    }

    default ResultActions updateACastMember(final CastMemberID id, final String name, final CastMemberType type) throws Exception {
        return this.update("/cast_members/", id, new UpdateCastMemberRequest(name, type));
    }

    /**
     * Category
     */

    default ResultActions deleteACategory(final CategoryID id) throws Exception {
        return this.delete("/categories/", id);
    }

    default CategoryID givenACategory(final String name, final String aDescription, final boolean isActive) throws Exception {
        final var requestBody = new CreateCategoryRequest(name, aDescription, isActive);
        final var actualId = this.given("/categories", requestBody);
        return CategoryID.from(actualId);
    }

    default ResultActions listCategories(final int page, final int perPage) throws Exception {
        return listCategories(page, perPage, "", "", "");
    }

    default ResultActions listCategories(final int page, final int perPage, final String search) throws Exception {
        return listCategories(page, perPage, search, "", "");
    }

    default ResultActions listCategories(final int page, final int perPage, final String search, final String sort, final String direction) throws Exception {
        return this.list("/categories", page, perPage, search, sort, direction);
    }

    default CategoryResponse retrieveACategory(final CategoryID id) throws Exception {
        return this.retrieve("/categories/", id, CategoryResponse.class);
    }

    default ResultActions updateACategory(final CategoryID id, final UpdateCategoryRequest request) throws Exception {
        return this.update("/categories/", id, request);
    }

    /**
     * Genre
     */

    default ResultActions deleteAGenre(final GenreID id) throws Exception {
        return this.delete("/genres/", id);
    }

    default GenreID givenAGenre(final String name, final boolean isActive, final List<CategoryID> categories) throws Exception {
        final var requestBody = new CreateGenreRequest(name, mapTo(categories, CategoryID::getValue), isActive);
        final var actualId = this.given("/genres", requestBody);
        return GenreID.from(actualId);
    }

    default ResultActions listGenres(final int page, final int perPage) throws Exception {
        return listGenres(page, perPage, "", "", "");
    }

    default ResultActions listGenres(final int page, final int perPage, final String search) throws Exception {
        return listGenres(page, perPage, search, "", "");
    }

    default ResultActions listGenres(final int page, final int perPage, final String search, final String sort, final String direction) throws Exception {
        return this.list("/genres", page, perPage, search, sort, direction);
    }

    default GenreResponse retrieveAGenre(final GenreID id) throws Exception {
        return this.retrieve("/genres/", id, GenreResponse.class);
    }

    default ResultActions updateAGenre(final GenreID id, final UpdateGenreRequest request) throws Exception {
        return this.update("/genres/", id, request);
    }

    default <A, D> List<D> mapTo(final List<A> actual, final Function<A, D> mapper) {
        return actual.stream()
                .map(mapper)
                .toList();
    }

    private String given(final String url, final Object body) throws Exception {
        final var request = post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(body));

        final var actualId = this.mvc().perform(request)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse().getHeader("Location")
                .replace("%s/".formatted(url), "");

        return actualId;
    }

    private ResultActions givenResult(final String url, final Object body) throws Exception {
        final var request = post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(body));

        return this.mvc().perform(request);
    }

    private ResultActions list(final String url, final int page, final int perPage, final String search, final String sort, final String direction) throws Exception {
        final var request = get(url)
                .queryParam("page", String.valueOf(page))
                .queryParam("perPage", String.valueOf(perPage))
                .queryParam("search", search)
                .queryParam("sort", sort)
                .queryParam("dir", direction)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        return this.mvc().perform(request);
    }

    private <T> T retrieve(final String url, final Identifier id, final Class<T> clazz) throws Exception {
        final var request = get(url + id.getValue())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8);

        final var json = this.mvc().perform(request)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        return Json.readValue(json, clazz);
    }

    private ResultActions retrieveResult(final String url, final Identifier id) throws Exception {
        final var request = get(url + id.getValue())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8);

        return this.mvc().perform(request);
    }

    private ResultActions delete(final String url, final Identifier id) throws Exception {
        final var request = MockMvcRequestBuilders.delete(url + id.getValue())
                .contentType(MediaType.APPLICATION_JSON);

        return this.mvc().perform(request);
    }

    private ResultActions update(final String url, final Identifier id, final Object requestBody) throws Exception {
        final var request = put(url + id.getValue())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(requestBody));

        return this.mvc().perform(request);
    }
}