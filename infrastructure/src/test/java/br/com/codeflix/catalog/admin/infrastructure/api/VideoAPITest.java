package br.com.codeflix.catalog.admin.infrastructure.api;

import br.com.codeflix.catalog.admin.ControllerTest;
import br.com.codeflix.catalog.admin.application.video.create.CreateVideoCommand;
import br.com.codeflix.catalog.admin.application.video.create.CreateVideoOutput;
import br.com.codeflix.catalog.admin.application.video.create.CreateVideoUseCase;
import br.com.codeflix.catalog.admin.application.video.delete.DeleteVideoUseCase;
import br.com.codeflix.catalog.admin.application.video.media.get.GetMediaCommand;
import br.com.codeflix.catalog.admin.application.video.media.get.GetMediaUseCase;
import br.com.codeflix.catalog.admin.application.video.media.get.MediaOutput;
import br.com.codeflix.catalog.admin.application.video.media.upload.UploadMediaCommand;
import br.com.codeflix.catalog.admin.application.video.media.upload.UploadMediaOutput;
import br.com.codeflix.catalog.admin.application.video.media.upload.UploadMediaUseCase;
import br.com.codeflix.catalog.admin.application.video.retrieve.get.GetVideoByIdUseCase;
import br.com.codeflix.catalog.admin.application.video.retrieve.get.VideoOutput;
import br.com.codeflix.catalog.admin.application.video.retrieve.list.ListVideoUseCase;
import br.com.codeflix.catalog.admin.application.video.retrieve.list.VideoListOutput;
import br.com.codeflix.catalog.admin.application.video.update.UpdateVideoCommand;
import br.com.codeflix.catalog.admin.application.video.update.UpdateVideoOutput;
import br.com.codeflix.catalog.admin.application.video.update.UpdateVideoUseCase;
import br.com.codeflix.catalog.admin.domain.castmember.CastMemberID;
import br.com.codeflix.catalog.admin.domain.category.CategoryID;
import br.com.codeflix.catalog.admin.domain.category.Fixture;
import br.com.codeflix.catalog.admin.domain.exceptions.NotFoundException;
import br.com.codeflix.catalog.admin.domain.exceptions.NotificationException;
import br.com.codeflix.catalog.admin.domain.genre.GenreID;
import br.com.codeflix.catalog.admin.domain.pagination.Pagination;
import br.com.codeflix.catalog.admin.domain.validation.Error;
import br.com.codeflix.catalog.admin.domain.video.*;
import br.com.codeflix.catalog.admin.infrastructure.video.models.CreateVideoRequest;
import br.com.codeflix.catalog.admin.infrastructure.video.models.UpdateVideoRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static br.com.codeflix.catalog.admin.domain.category.Fixture.CastMembers.wesley;
import static br.com.codeflix.catalog.admin.domain.category.Fixture.Categories.aulas;
import static br.com.codeflix.catalog.admin.domain.category.Fixture.Genres.tech;
import static br.com.codeflix.catalog.admin.domain.category.Fixture.Videos.description;
import static br.com.codeflix.catalog.admin.domain.category.Fixture.Videos.*;
import static br.com.codeflix.catalog.admin.domain.category.Fixture.*;
import static br.com.codeflix.catalog.admin.domain.utils.CollectionUtils.mapTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ControllerTest(controllers = VideoAPI.class)
public class VideoAPITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CreateVideoUseCase createVideoUseCase;

    @MockBean
    private GetVideoByIdUseCase getVideoByIdUseCase;

    @MockBean
    private UpdateVideoUseCase updateVideoUseCase;

    @MockBean
    private DeleteVideoUseCase deleteVideoUseCase;

    @MockBean
    private ListVideoUseCase listVideosUseCase;

    @MockBean
    private GetMediaUseCase getMediaUseCase;

    @MockBean
    private UploadMediaUseCase uploadMediaUseCase;

    @Test
    public void givenAValidCommand_whenCallsCreateFull_shouldReturnAnId() throws Exception {
        final var wesley = wesley();
        final var aulas = aulas();
        final var tech = tech();

        final var expectedId = VideoID.unique();
        final var expectedTitle = title();
        final var expectedDescription = description();
        final var expectedLaunchYear = Year.of(year());
        final var expectedDuration = duration();
        final var expectedOpened = bool();
        final var expectedPublished = bool();
        final var expectedRating = rating();
        final var expectedCategories = Set.of(aulas.getId().getValue());
        final var expectedGenres = Set.of(tech.getId().getValue());
        final var expectedMembers = Set.of(wesley.getId().getValue());

        final var expectedVideo =
                new MockMultipartFile("video_file", "video.mp4", "video/mp4", "VIDEO".getBytes());

        final var expectedTrailer =
                new MockMultipartFile("trailer_file", "trailer.mp4", "video/mp4", "TRAILER".getBytes());

        final var expectedBanner =
                new MockMultipartFile("banner_file", "banner.jpg", "image/jpg", "BANNER".getBytes());

        final var expectedThumb =
                new MockMultipartFile("thumb_file", "thumbnail.jpg", "image/jpg", "THUMB".getBytes());

        final var expectedThumbHalf =
                new MockMultipartFile("thumb_half_file", "thumbnailHalf.jpg", "image/jpg", "THUMBHALF".getBytes());

        when(createVideoUseCase.execute(any())).thenReturn(new CreateVideoOutput(expectedId.getValue()));

        final var request = multipart("/videos")
                .file(expectedVideo)
                .file(expectedTrailer)
                .file(expectedBanner)
                .file(expectedThumb)
                .file(expectedThumbHalf)
                .param("title", expectedTitle)
                .param("description", expectedDescription)
                .param("year_launched", String.valueOf(expectedLaunchYear.getValue()))
                .param("duration", expectedDuration.toString())
                .param("opened", String.valueOf(expectedOpened))
                .param("published", String.valueOf(expectedPublished))
                .param("rating", expectedRating.getName())
                .param("cast_members_id", wesley.getId().getValue())
                .param("categories_id", aulas.getId().getValue())
                .param("genres_id", tech.getId().getValue())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        this.mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/videos/" + expectedId.getValue()))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo(expectedId.getValue())));

        final var commandCaptor = ArgumentCaptor.forClass(CreateVideoCommand.class);

        verify(createVideoUseCase).execute(commandCaptor.capture());

        final var actualCommand = commandCaptor.getValue();

        assertEquals(expectedTitle, actualCommand.title());
        assertEquals(expectedDescription, actualCommand.description());
        assertEquals(expectedLaunchYear.getValue(), actualCommand.launchedAt());
        assertEquals(expectedDuration, actualCommand.duration());
        assertEquals(expectedOpened, actualCommand.opened());
        assertEquals(expectedPublished, actualCommand.published());
        assertEquals(expectedRating.getName(), actualCommand.rating());
        assertEquals(expectedCategories, actualCommand.categories());
        assertEquals(expectedGenres, actualCommand.genres());
        assertEquals(expectedMembers, actualCommand.members());
        assertEquals(expectedVideo.getOriginalFilename(), actualCommand.getVideo().get().name());
        assertEquals(expectedTrailer.getOriginalFilename(), actualCommand.getTrailer().get().name());
        assertEquals(expectedBanner.getOriginalFilename(), actualCommand.getBanner().get().name());
        assertEquals(expectedThumb.getOriginalFilename(), actualCommand.getThumbnail().get().name());
        assertEquals(expectedThumbHalf.getOriginalFilename(), actualCommand.getThumbnailHalf().get().name());
    }

    @Test
    public void givenAnInvalidCommand_whenCallsCreateFull_shouldReturnError() throws Exception {
        final var expectedErrorMessage = "title is required";

        when(createVideoUseCase.execute(any())).thenThrow(NotificationException.with(new Error(expectedErrorMessage)));

        final var request = multipart("/videos")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        final var response = this.mvc.perform(request);

        response.andExpect(status().isUnprocessableEntity())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)));
    }

    @Test
    public void givenAValidCommand_whenCallsCreatePartial_shouldReturnId() throws Exception {
        final var wesley = wesley();
        final var aulas = aulas();
        final var tech = tech();

        final var expectedId = VideoID.unique();
        final var expectedTitle = title();
        final var expectedDescription = description();
        final var expectedLaunchYear = Year.of(year());
        final var expectedDuration = duration();
        final var expectedOpened = bool();
        final var expectedPublished = bool();
        final var expectedRating = rating();
        final var expectedCategories = Set.of(aulas.getId().getValue());
        final var expectedGenres = Set.of(tech.getId().getValue());
        final var expectedMembers = Set.of(wesley.getId().getValue());

        final var command = new CreateVideoRequest(
                expectedTitle,
                expectedDescription,
                expectedDuration,
                expectedLaunchYear.getValue(),
                expectedOpened,
                expectedPublished,
                expectedRating.getName(),
                expectedMembers,
                expectedCategories,
                expectedGenres
        );

        when(createVideoUseCase.execute(any())).thenReturn(new CreateVideoOutput(expectedId.getValue()));

        final var request = post("/videos")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(command));

        this.mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/videos/" + expectedId.getValue()))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo(expectedId.getValue())));

        final var commandCaptor = ArgumentCaptor.forClass(CreateVideoCommand.class);

        verify(createVideoUseCase).execute(commandCaptor.capture());

        final var actualCommand = commandCaptor.getValue();

        assertEquals(expectedTitle, actualCommand.title());
        assertEquals(expectedDescription, actualCommand.description());
        assertEquals(expectedLaunchYear.getValue(), actualCommand.launchedAt());
        assertEquals(expectedDuration, actualCommand.duration());
        assertEquals(expectedOpened, actualCommand.opened());
        assertEquals(expectedPublished, actualCommand.published());
        assertEquals(expectedRating.getName(), actualCommand.rating());
        assertEquals(expectedCategories, actualCommand.categories());
        assertEquals(expectedGenres, actualCommand.genres());
        assertEquals(expectedMembers, actualCommand.members());
        Assertions.assertTrue(actualCommand.getVideo().isEmpty());
        Assertions.assertTrue(actualCommand.getTrailer().isEmpty());
        Assertions.assertTrue(actualCommand.getBanner().isEmpty());
        Assertions.assertTrue(actualCommand.getThumbnail().isEmpty());
        Assertions.assertTrue(actualCommand.getThumbnailHalf().isEmpty());
    }

    @Test
    public void givenAnEmptyBody_whenCallsCreatePartial_shouldReturnError() throws Exception {
        final var request = post("/videos")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(request);

        response.andExpect(status().isBadRequest());
    }

    @Test
    public void givenAnInvalidCommand_whenCallsCreatePartial_shouldReturnError() throws Exception {
        final var expectedErrorMessage = "title is required";

        when(createVideoUseCase.execute(any()))
                .thenThrow(NotificationException.with(new Error(expectedErrorMessage)));

        final var request = post("/videos")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "title": "Olá Mundo!"
                        }
                        """);

        final var response = this.mvc.perform(request);

        response.andExpect(status().isUnprocessableEntity())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)));
    }

    @Test
    public void givenAValidId_whenCallsGetById_shouldReturnVideo() throws Exception {
        final var wesley = wesley();
        final var aulas = aulas();
        final var tech = tech();

        final var expectedTitle = title();
        final var expectedDescription = description();
        final var expectedLaunchYear = Year.of(year());
        final var expectedDuration = duration();
        final var expectedOpened = bool();
        final var expectedPublished = bool();
        final var expectedRating = rating();
        final var expectedCategories = Set.of(aulas.getId().getValue());
        final var expectedGenres = Set.of(tech.getId().getValue());
        final var expectedMembers = Set.of(wesley.getId().getValue());

        final var expectedVideo = audioVideo(VideoMediaType.VIDEO);
        final var expectedTrailer = audioVideo(VideoMediaType.TRAILER);
        final var expectedBanner = imageMedia(VideoMediaType.BANNER);
        final var expectedThumb = imageMedia(VideoMediaType.THUMBNAIL);
        final var expectedThumbHalf = imageMedia(VideoMediaType.THUMBNAIL_HALF);

        final var video = Video.newVideo(
                        expectedTitle,
                        expectedDescription,
                        expectedLaunchYear,
                        expectedDuration,
                        expectedOpened,
                        expectedPublished,
                        expectedRating,
                        mapTo(expectedCategories, CategoryID::from),
                        mapTo(expectedGenres, GenreID::from),
                        mapTo(expectedMembers, CastMemberID::from)
                )
                .updateVideoMedia(expectedVideo)
                .updateTrailerMedia(expectedTrailer)
                .updateBannerMedia(expectedBanner)
                .updateThumbnailMedia(expectedThumb)
                .updateThumbnailHalfMedia(expectedThumbHalf);

        final var expectedId = video.getId().getValue();

        when(getVideoByIdUseCase.execute(any()))
                .thenReturn(VideoOutput.from(video));

        final var request = get("/videos/{id}", expectedId)
                .accept(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(request);

        response.andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo(expectedId)))
                .andExpect(jsonPath("$.title", equalTo(expectedTitle)))
                .andExpect(jsonPath("$.description", equalTo(expectedDescription)))
                .andExpect(jsonPath("$.year_launched", equalTo(expectedLaunchYear.getValue())))
                .andExpect(jsonPath("$.duration", equalTo(expectedDuration)))
                .andExpect(jsonPath("$.opened", equalTo(expectedOpened)))
                .andExpect(jsonPath("$.published", equalTo(expectedPublished)))
                .andExpect(jsonPath("$.rating", equalTo(expectedRating.getName())))
                .andExpect(jsonPath("$.created_at", equalTo(video.getCreatedAt().toString())))
                .andExpect(jsonPath("$.updated_at", equalTo(video.getUpdatedAt().toString())))
                .andExpect(jsonPath("$.banner.id", equalTo(expectedBanner.id())))
                .andExpect(jsonPath("$.banner.name", equalTo(expectedBanner.name())))
                .andExpect(jsonPath("$.banner.location", equalTo(expectedBanner.location())))
                .andExpect(jsonPath("$.banner.checksum", equalTo(expectedBanner.checksum())))
                .andExpect(jsonPath("$.thumbnail.id", equalTo(expectedThumb.id())))
                .andExpect(jsonPath("$.thumbnail.name", equalTo(expectedThumb.name())))
                .andExpect(jsonPath("$.thumbnail.location", equalTo(expectedThumb.location())))
                .andExpect(jsonPath("$.thumbnail.checksum", equalTo(expectedThumb.checksum())))
                .andExpect(jsonPath("$.thumbnail_half.id", equalTo(expectedThumbHalf.id())))
                .andExpect(jsonPath("$.thumbnail_half.name", equalTo(expectedThumbHalf.name())))
                .andExpect(jsonPath("$.thumbnail_half.location", equalTo(expectedThumbHalf.location())))
                .andExpect(jsonPath("$.thumbnail_half.checksum", equalTo(expectedThumbHalf.checksum())))
                .andExpect(jsonPath("$.video.id", equalTo(expectedVideo.id())))
                .andExpect(jsonPath("$.video.name", equalTo(expectedVideo.name())))
                .andExpect(jsonPath("$.video.checksum", equalTo(expectedVideo.checksum())))
                .andExpect(jsonPath("$.video.location", equalTo(expectedVideo.rawLocation())))
                .andExpect(jsonPath("$.video.encoded_location", equalTo(expectedVideo.encodedLocation())))
                .andExpect(jsonPath("$.video.status", equalTo(expectedVideo.status().name())))
                .andExpect(jsonPath("$.trailer.id", equalTo(expectedTrailer.id())))
                .andExpect(jsonPath("$.trailer.name", equalTo(expectedTrailer.name())))
                .andExpect(jsonPath("$.trailer.checksum", equalTo(expectedTrailer.checksum())))
                .andExpect(jsonPath("$.trailer.location", equalTo(expectedTrailer.rawLocation())))
                .andExpect(jsonPath("$.trailer.encoded_location", equalTo(expectedTrailer.encodedLocation())))
                .andExpect(jsonPath("$.trailer.status", equalTo(expectedTrailer.status().name())))
                .andExpect(jsonPath("$.categories_id", equalTo(new ArrayList(expectedCategories))))
                .andExpect(jsonPath("$.genres_id", equalTo(new ArrayList(expectedGenres))))
                .andExpect(jsonPath("$.cast_members_id", equalTo(new ArrayList(expectedMembers))));
    }

    @Test
    public void givenAnInvalidId_whenCallsGetById_shouldReturnNotFound() throws Exception {
        final var expectedId = VideoID.unique();
        final var expectedErrorMessage = "Video with ID %s was not found".formatted(expectedId.getValue());

        when(getVideoByIdUseCase.execute(any())).thenThrow(NotFoundException.with(Video.class, expectedId));

        final var request = get("/videos/{id}", expectedId)
                .accept(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(request);

        response.andExpect(status().isNotFound())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)));
    }

    @Test
    public void givenAValidCommand_whenCallsUpdateVideo_shouldReturnVideoId() throws Exception {
        final var wesley = wesley();
        final var aulas = aulas();
        final var tech = tech();

        final var expectedId = VideoID.unique();
        final var expectedTitle = title();
        final var expectedDescription = description();
        final var expectedLaunchYear = Year.of(year());
        final var expectedDuration = duration();
        final var expectedOpened = bool();
        final var expectedPublished = bool();
        final var expectedRating = rating();
        final var expectedCategories = Set.of(aulas.getId().getValue());
        final var expectedGenres = Set.of(tech.getId().getValue());
        final var expectedMembers = Set.of(wesley.getId().getValue());

        final var command = new UpdateVideoRequest(
                expectedTitle,
                expectedDescription,
                expectedDuration,
                expectedLaunchYear.getValue(),
                expectedOpened,
                expectedPublished,
                expectedRating.getName(),
                expectedMembers,
                expectedCategories,
                expectedGenres
        );

        when(updateVideoUseCase.execute(any()))
                .thenReturn(new UpdateVideoOutput(expectedId.getValue()));

        final var request = put("/videos/{id}", expectedId.getValue())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(command));

        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(header().string("Location", "/videos/" + expectedId.getValue()))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo(expectedId.getValue())));

        final var cmdCaptor = ArgumentCaptor.forClass(UpdateVideoCommand.class);

        verify(updateVideoUseCase).execute(cmdCaptor.capture());

        final var actualCommand = cmdCaptor.getValue();

        assertEquals(expectedTitle, actualCommand.title());
        assertEquals(expectedDescription, actualCommand.description());
        assertEquals(expectedLaunchYear.getValue(), actualCommand.launchedAt());
        assertEquals(expectedDuration, actualCommand.duration());
        assertEquals(expectedOpened, actualCommand.opened());
        assertEquals(expectedPublished, actualCommand.published());
        assertEquals(expectedRating.getName(), actualCommand.rating());
        assertEquals(expectedCategories, actualCommand.categories());
        assertEquals(expectedGenres, actualCommand.genres());
        assertEquals(expectedMembers, actualCommand.members());
        Assertions.assertTrue(actualCommand.getVideo().isEmpty());
        Assertions.assertTrue(actualCommand.getTrailer().isEmpty());
        Assertions.assertTrue(actualCommand.getBanner().isEmpty());
        Assertions.assertTrue(actualCommand.getThumbnail().isEmpty());
        Assertions.assertTrue(actualCommand.getThumbnailHalf().isEmpty());
    }

    @Test
    public void givenAnInvalidCommand_whenCallsUpdateVideo_shouldReturnNotification() throws Exception {
        final var wesley = wesley();
        final var aulas = aulas();
        final var tech = tech();

        final var expectedId = VideoID.unique();
        final var expectedErrorMessage = "'title' should not be empty";
        final var expectedErrorCount = 1;

        final var expectedTitle = "";
        final var expectedDescription = description();
        final var expectedLaunchYear = Year.of(year());
        final var expectedDuration = duration();
        final var expectedOpened = bool();
        final var expectedPublished = bool();
        final var expectedRating = rating();
        final var expectedCategories = Set.of(aulas.getId().getValue());
        final var expectedGenres = Set.of(tech.getId().getValue());
        final var expectedMembers = Set.of(wesley.getId().getValue());

        final var command = new UpdateVideoRequest(
                expectedTitle,
                expectedDescription,
                expectedDuration,
                expectedLaunchYear.getValue(),
                expectedOpened,
                expectedPublished,
                expectedRating.getName(),
                expectedMembers,
                expectedCategories,
                expectedGenres
        );

        when(updateVideoUseCase.execute(any()))
                .thenThrow(NotificationException.with(new Error(expectedErrorMessage)));

        final var request = put("/videos/{id}", expectedId.getValue())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(command));

        final var response = this.mvc.perform(request);

        response.andExpect(status().isUnprocessableEntity())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)))
                .andExpect(jsonPath("$.errors", hasSize(expectedErrorCount)))
                .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        verify(updateVideoUseCase).execute(any());
    }

    @Test
    public void givenAValidId_whenCallsDeleteById_shouldDeleteIt() throws Exception {
        final var expectedId = VideoID.unique();

        doNothing().when(deleteVideoUseCase).execute(any());

        final var request = delete("/videos/{id}", expectedId.getValue());

        final var response = this.mvc.perform(request);

        response.andExpect(status().isNoContent());

        verify(deleteVideoUseCase).execute(eq(expectedId.getValue()));
    }

    @Test
    public void givenValidParams_whenCallsListVideos_shouldReturnPagination() throws Exception {
        final var video = new VideoPreview(Fixture.video());

        final var expectedPage = 50;
        final var expectedPerPage = 50;
        final var expectedTerms = "Algo";
        final var expectedSort = "title";
        final var expectedDirection = "asc";
        final var expectedCastMembers = "cast1";
        final var expectedGenres = "gen1";
        final var expectedCategories = "cat1";

        final var expectedItemsCount = 1;
        final var expectedTotal = 1;

        final var expectedItems = List.of(VideoListOutput.from(video));

        when(listVideosUseCase.execute(any()))
                .thenReturn(new Pagination<>(expectedPage, expectedPerPage, expectedTotal, expectedItems));

        final var request = get("/videos")
                .queryParam("page", String.valueOf(expectedPage))
                .queryParam("perPage", String.valueOf(expectedPerPage))
                .queryParam("sort", expectedSort)
                .queryParam("dir", expectedDirection)
                .queryParam("search", expectedTerms)
                .queryParam("cast_members_ids", expectedCastMembers)
                .queryParam("categories_ids", expectedCategories)
                .queryParam("genres_ids", expectedGenres)
                .accept(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(request);

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(expectedPage)))
                .andExpect(jsonPath("$.per_page", equalTo(expectedPerPage)))
                .andExpect(jsonPath("$.total", equalTo(expectedTotal)))
                .andExpect(jsonPath("$.items", hasSize(expectedItemsCount)))
                .andExpect(jsonPath("$.items[0].id", equalTo(video.id())))
                .andExpect(jsonPath("$.items[0].title", equalTo(video.title())))
                .andExpect(jsonPath("$.items[0].description", equalTo(video.description())))
                .andExpect(jsonPath("$.items[0].created_at", equalTo(video.createdAt().toString())))
                .andExpect(jsonPath("$.items[0].updated_at", equalTo(video.updatedAt().toString())));

        final var captor = ArgumentCaptor.forClass(VideoSearchQuery.class);

        verify(listVideosUseCase).execute(captor.capture());

        final var actualQuery = captor.getValue();
        assertEquals(expectedPage, actualQuery.page());
        assertEquals(expectedPerPage, actualQuery.perPage());
        assertEquals(expectedDirection, actualQuery.direction());
        assertEquals(expectedSort, actualQuery.sort());
        assertEquals(expectedTerms, actualQuery.terms());
        assertEquals(Set.of(CategoryID.from(expectedCategories)), actualQuery.categories());
        assertEquals(Set.of(CastMemberID.from(expectedCastMembers)), actualQuery.castMembers());
        assertEquals(Set.of(GenreID.from(expectedGenres)), actualQuery.genres());
    }

    @Test
    public void givenEmptyParams_whenCallsListVideosWithDefaultValues_shouldReturnPagination() throws Exception {
        final var video = new VideoPreview(Fixture.video());

        final var expectedPage = 0;
        final var expectedPerPage = 25;
        final var expectedTerms = "";
        final var expectedSort = "title";
        final var expectedDirection = "asc";

        final var expectedItemsCount = 1;
        final var expectedTotal = 1;

        final var expectedItems = List.of(VideoListOutput.from(video));

        when(listVideosUseCase.execute(any()))
                .thenReturn(new Pagination<>(expectedPage, expectedPerPage, expectedTotal, expectedItems));

        final var request = get("/videos")
                .accept(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(request);

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(expectedPage)))
                .andExpect(jsonPath("$.per_page", equalTo(expectedPerPage)))
                .andExpect(jsonPath("$.total", equalTo(expectedTotal)))
                .andExpect(jsonPath("$.items", hasSize(expectedItemsCount)))
                .andExpect(jsonPath("$.items[0].id", equalTo(video.id())))
                .andExpect(jsonPath("$.items[0].title", equalTo(video.title())))
                .andExpect(jsonPath("$.items[0].description", equalTo(video.description())))
                .andExpect(jsonPath("$.items[0].created_at", equalTo(video.createdAt().toString())))
                .andExpect(jsonPath("$.items[0].updated_at", equalTo(video.updatedAt().toString())));

        final var captor = ArgumentCaptor.forClass(VideoSearchQuery.class);

        verify(listVideosUseCase).execute(captor.capture());

        final var actualQuery = captor.getValue();
        assertEquals(expectedPage, actualQuery.page());
        assertEquals(expectedPerPage, actualQuery.perPage());
        assertEquals(expectedDirection, actualQuery.direction());
        assertEquals(expectedSort, actualQuery.sort());
        assertEquals(expectedTerms, actualQuery.terms());
        Assertions.assertTrue(actualQuery.categories().isEmpty());
        Assertions.assertTrue(actualQuery.castMembers().isEmpty());
        Assertions.assertTrue(actualQuery.genres().isEmpty());
    }

    @Test
    public void givenAValidVideoIdAndFileType_whenCallsGetMediaById_shouldReturnContent() throws Exception {
        final var expectedId = VideoID.unique();

        final var expectedMediaType = VideoMediaType.VIDEO;
        final var expectedResource = Fixture.Videos.resource(expectedMediaType);

        final var expectedMedia = new MediaOutput(expectedResource.content(), expectedResource.contentType(), expectedResource.name());

        when(getMediaUseCase.execute(any())).thenReturn(expectedMedia);

        final var request = get("/videos/{id}/medias/{type}", expectedId.getValue(), expectedMediaType.name());

        final var response = this.mvc.perform(request);

        response.andExpect(status().isOk())
                .andExpect(header().string(CONTENT_TYPE, expectedMedia.contentType()))
                .andExpect(header().string(CONTENT_LENGTH, String.valueOf(expectedMedia.content().length)))
                .andExpect(header().string(CONTENT_DISPOSITION, "attachment; filename=%s".formatted(expectedMedia.name())))
                .andExpect(content().bytes(expectedMedia.content()));

        final var captor = ArgumentCaptor.forClass(GetMediaCommand.class);

        verify(this.getMediaUseCase).execute(captor.capture());

        final var actualCommand = captor.getValue();
        assertEquals(expectedId.getValue(), actualCommand.videoId());
        assertEquals(expectedMediaType.name(), actualCommand.mediaType());
    }

    @Test
    public void givenAValidVideoIdAndFile_whenCallsUploadMedia_shouldStoreIt() throws Exception {
        final var expectedId = VideoID.unique();
        final var expectedType = VideoMediaType.VIDEO;
        final var expectedResource = Fixture.Videos.resource(expectedType);

        final var expectedVideo =
                new MockMultipartFile("media_file", expectedResource.name(), expectedResource.contentType(), expectedResource.content());

        when(uploadMediaUseCase.execute(any())).thenReturn(new UploadMediaOutput(expectedId.getValue(), expectedType));

        final var request = multipart("/videos/{id}/medias/{type}", expectedId.getValue(), expectedType.name())
                .file(expectedVideo)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        final var response = this.mvc.perform(request);

        response.andExpect(status().isCreated())
                .andExpect(header().string(LOCATION, "/videos/%s/medias/%s".formatted(expectedId.getValue(), expectedType.name())))
                .andExpect(header().string(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.video_id", equalTo(expectedId.getValue())))
                .andExpect(jsonPath("$.media_type", equalTo(expectedType.name())));

        final var captor = ArgumentCaptor.forClass(UploadMediaCommand.class);

        verify(this.uploadMediaUseCase).execute(captor.capture());

        final var actualCommand = captor.getValue();
        assertEquals(expectedId.getValue(), actualCommand.videoId());
        assertEquals(expectedResource.content(), actualCommand.videoResource().resource().content());
        assertEquals(expectedResource.name(), actualCommand.videoResource().resource().name());
        assertEquals(expectedResource.contentType(), actualCommand.videoResource().resource().contentType());
        assertEquals(expectedType, actualCommand.videoResource().type());
    }

    @Test
    public void givenAnInvalidMediaType_whenCallsUploadMedia_shouldReturnError() throws Exception {
        final var expectedId = VideoID.unique();
        final var expectedResource = Fixture.Videos.resource(VideoMediaType.VIDEO);

        final var expectedVideo =
                new MockMultipartFile("media_file", expectedResource.name(), expectedResource.contentType(), expectedResource.content());

        final var request = multipart("/videos/{id}/medias/INVALID", expectedId.getValue())
                .file(expectedVideo)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        final var response = this.mvc.perform(request);

        response.andExpect(status().isUnprocessableEntity())
                .andExpect(header().string(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message", equalTo("Invalid INVALID for VideoMediaType")));
    }
}