package br.com.codeflix.catalog.admin.application.video.retrieve.get;

import br.com.codeflix.catalog.admin.application.UseCaseTest;
import br.com.codeflix.catalog.admin.domain.exceptions.NotFoundException;
import br.com.codeflix.catalog.admin.domain.video.Video;
import br.com.codeflix.catalog.admin.domain.video.VideoGateway;
import br.com.codeflix.catalog.admin.domain.video.VideoID;
import br.com.codeflix.catalog.admin.domain.video.VideoMediaType;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static br.com.codeflix.catalog.admin.application.Fixture.CastMembers.gabriel;
import static br.com.codeflix.catalog.admin.application.Fixture.CastMembers.wesley;
import static br.com.codeflix.catalog.admin.application.Fixture.Categories.aulas;
import static br.com.codeflix.catalog.admin.application.Fixture.Genres.tech;
import static br.com.codeflix.catalog.admin.application.Fixture.*;
import static br.com.codeflix.catalog.admin.application.Fixture.Videos.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class GetVideoByIdUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultGetVideoByIdUseCase useCase;

    @Mock
    private VideoGateway videoGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(videoGateway);
    }

    @Test
    public void givenAValidId_whenCallsGetVideo_shouldReturnIt() {
        final var expectedTitle = title();
        final var expectedDescription = description();
        final var expectedLaunchYear = Year.of(year());
        final var expectedDuration = duration();
        final var expectedOpened = bool();
        final var expectedPublished = bool();
        final var expectedRating = rating();
        final var expectedCategories = Set.of(aulas().getId());
        final var expectedGenres = Set.of(tech().getId());
        final var expectedMembers = Set.of(
                wesley().getId(),
                gabriel().getId()
        );
        final var expectedVideo = audioVideo(VideoMediaType.VIDEO);
        final var expectedTrailer = audioVideo(VideoMediaType.TRAILER);
        final var expectedBanner = image(VideoMediaType.BANNER);
        final var expectedThumb = image(VideoMediaType.THUMBNAIL);
        final var expectedThumbHalf = image(VideoMediaType.THUMBNAIL_HALF);

        final var aVideo = Video.newVideo(
                        expectedTitle,
                        expectedDescription,
                        expectedLaunchYear,
                        expectedDuration,
                        expectedOpened,
                        expectedPublished,
                        expectedRating,
                        expectedCategories,
                        expectedGenres,
                        expectedMembers
                )
                .updateVideoMedia(expectedVideo)
                .updateTrailerMedia(expectedTrailer)
                .updateBannerMedia(expectedBanner)
                .updateThumbnailMedia(expectedThumb)
                .updateThumbnailHalfMedia(expectedThumbHalf);

        final var expectedId = aVideo.getId();

        when(videoGateway.findById(any())).thenReturn(Optional.of(Video.with(aVideo)));

        final var actualVideo = this.useCase.execute(expectedId.getValue());

        assertEquals(expectedId.getValue(), actualVideo.id());
        assertEquals(expectedTitle, actualVideo.title());
        assertEquals(expectedDescription, actualVideo.description());
        assertEquals(expectedLaunchYear.getValue(), actualVideo.launchedAt());
        assertEquals(expectedDuration, actualVideo.duration());
        assertEquals(expectedOpened, actualVideo.opened());
        assertEquals(expectedPublished, actualVideo.published());
        assertEquals(expectedRating, actualVideo.rating());
        assertEquals(asString(expectedCategories), actualVideo.categories());
        assertEquals(asString(expectedGenres), actualVideo.genres());
        assertEquals(asString(expectedMembers), actualVideo.castMembers());
        assertEquals(expectedVideo, actualVideo.video());
        assertEquals(expectedTrailer, actualVideo.trailer());
        assertEquals(expectedBanner, actualVideo.banner());
        assertEquals(expectedThumb, actualVideo.thumbnail());
        assertEquals(expectedThumbHalf, actualVideo.thumbnailHalf());
        assertEquals(aVideo.getCreatedAt(), actualVideo.createdAt());
        assertEquals(aVideo.getUpdatedAt(), actualVideo.updatedAt());
    }

    @Test
    public void givenInvalidId_whenCallsGetVideo_shouldReturnNotFound() {
        final var expectedErrorMessage = "Video with ID 123 was not found";

        final var expectedId = VideoID.from("123");

        when(videoGateway.findById(any())).thenReturn(Optional.empty());

        final var actualError = assertThrows(
                NotFoundException.class,
                () -> this.useCase.execute(expectedId.getValue())
        );

        assertEquals(expectedErrorMessage, actualError.getMessage());
    }
}
