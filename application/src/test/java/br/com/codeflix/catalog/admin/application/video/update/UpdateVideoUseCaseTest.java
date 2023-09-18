package br.com.codeflix.catalog.admin.application.video.update;

import br.com.codeflix.catalog.admin.application.UseCaseTest;
import br.com.codeflix.catalog.admin.domain.castmember.CastMemberGateway;
import br.com.codeflix.catalog.admin.domain.castmember.CastMemberID;
import br.com.codeflix.catalog.admin.domain.category.CategoryGateway;
import br.com.codeflix.catalog.admin.domain.category.CategoryID;
import br.com.codeflix.catalog.admin.domain.exceptions.DomainException;
import br.com.codeflix.catalog.admin.domain.exceptions.InternalErrorException;
import br.com.codeflix.catalog.admin.domain.exceptions.NotificationException;
import br.com.codeflix.catalog.admin.domain.genre.GenreGateway;
import br.com.codeflix.catalog.admin.domain.genre.GenreID;
import br.com.codeflix.catalog.admin.domain.video.*;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.Year;
import java.util.*;

import static br.com.codeflix.catalog.admin.domain.category.Fixture.*;
import static br.com.codeflix.catalog.admin.domain.category.Fixture.CastMembers.gabriel;
import static br.com.codeflix.catalog.admin.domain.category.Fixture.CastMembers.wesley;
import static br.com.codeflix.catalog.admin.domain.category.Fixture.Categories.aulas;
import static br.com.codeflix.catalog.admin.domain.category.Fixture.Genres.tech;
import static br.com.codeflix.catalog.admin.domain.category.Fixture.Videos.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class UpdateVideoUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultUpdateVideoUseCase useCase;

    @Mock
    private VideoGateway videoGateway;

    @Mock
    private CategoryGateway categoryGateway;

    @Mock
    private GenreGateway genreGateway;

    @Mock
    private CastMemberGateway castMemberGateway;

    @Mock
    private MediaResourceGateway mediaResourceGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(videoGateway, categoryGateway, genreGateway, castMemberGateway, mediaResourceGateway);
    }

    @Test
    public void givenAValidCommand_whenCallsUpdateVideo_shouldReturnVideoId() {
        final var video = systemDesign();

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
        final Resource expectedVideo = resource(VideoMediaType.VIDEO);
        final Resource expectedTrailer = resource(VideoMediaType.TRAILER);
        final Resource expectedBanner = resource(VideoMediaType.BANNER);
        final Resource expectedThumb = resource(VideoMediaType.THUMBNAIL);
        final Resource expectedThumbHalf = resource(VideoMediaType.THUMBNAIL_HALF);

        final var command = UpdateVideoCommand.with(
                video.getId().getValue(),
                expectedTitle,
                expectedDescription,
                expectedLaunchYear.getValue(),
                expectedDuration,
                expectedOpened,
                expectedPublished,
                expectedRating.getName(),
                asString(expectedCategories),
                asString(expectedGenres),
                asString(expectedMembers),
                expectedVideo,
                expectedTrailer,
                expectedBanner,
                expectedThumb,
                expectedThumbHalf
        );

        when(videoGateway.findById(any())).thenReturn(Optional.of(Video.with(video)));

        when(categoryGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedCategories));

        when(castMemberGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedMembers));

        when(genreGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedGenres));

        mockImageMedia();
        mockAudioVideoMedia();

        when(videoGateway.update(any())).thenAnswer(returnsFirstArg());

        final var actualResult = useCase.execute(command);

        assertNotNull(actualResult);
        assertNotNull(actualResult.id());

        verify(videoGateway).findById(eq(video.getId()));

        verify(videoGateway).update(argThat(actualVideo ->
                Objects.equals(expectedTitle, actualVideo.getTitle())
                        && Objects.equals(expectedDescription, actualVideo.getDescription())
                        && Objects.equals(expectedLaunchYear, actualVideo.getLaunchedAt())
                        && Objects.equals(expectedDuration, actualVideo.getDuration())
                        && Objects.equals(expectedOpened, actualVideo.getOpened())
                        && Objects.equals(expectedPublished, actualVideo.getPublished())
                        && Objects.equals(expectedRating, actualVideo.getRating())
                        && Objects.equals(expectedCategories, actualVideo.getCategories())
                        && Objects.equals(expectedGenres, actualVideo.getGenres())
                        && Objects.equals(expectedMembers, actualVideo.getCastMembers())
                        && Objects.equals(expectedVideo.name(), actualVideo.getVideo().get().name())
                        && Objects.equals(expectedTrailer.name(), actualVideo.getTrailer().get().name())
                        && Objects.equals(expectedBanner.name(), actualVideo.getBanner().get().name())
                        && Objects.equals(expectedThumb.name(), actualVideo.getThumbnail().get().name())
                        && Objects.equals(expectedThumbHalf.name(), actualVideo.getThumbnailHalf().get().name())
                        && Objects.equals(video.getCreatedAt(), actualVideo.getCreatedAt())
                        && video.getUpdatedAt().isBefore(actualVideo.getUpdatedAt())
        ));
    }

    @Test
    public void givenAValidCommandWithoutCategories_whenCallsUpdateVideo_shouldReturnVideoId() {
        final var video = systemDesign();

        final var expectedTitle = title();
        final var expectedDescription = description();
        final var expectedLaunchYear = Year.of(year());
        final var expectedDuration = duration();
        final var expectedOpened = bool();
        final var expectedPublished = bool();
        final var expectedRating = rating();
        final var expectedCategories = Set.<CategoryID>of();
        final var expectedGenres = Set.of(tech().getId());
        final var expectedMembers = Set.of(
                wesley().getId(),
                gabriel().getId()
        );
        final Resource expectedVideo = resource(VideoMediaType.VIDEO);
        final Resource expectedTrailer = resource(VideoMediaType.TRAILER);
        final Resource expectedBanner = resource(VideoMediaType.BANNER);
        final Resource expectedThumb = resource(VideoMediaType.THUMBNAIL);
        final Resource expectedThumbHalf = resource(VideoMediaType.THUMBNAIL_HALF);

        final var command = UpdateVideoCommand.with(
                video.getId().getValue(),
                expectedTitle,
                expectedDescription,
                expectedLaunchYear.getValue(),
                expectedDuration,
                expectedOpened,
                expectedPublished,
                expectedRating.getName(),
                asString(expectedCategories),
                asString(expectedGenres),
                asString(expectedMembers),
                expectedVideo,
                expectedTrailer,
                expectedBanner,
                expectedThumb,
                expectedThumbHalf
        );

        when(videoGateway.findById(any())).thenReturn(Optional.of(Video.with(video)));

        when(castMemberGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedMembers));

        when(genreGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedGenres));

        mockImageMedia();
        mockAudioVideoMedia();

        when(videoGateway.update(any())).thenAnswer(returnsFirstArg());

        final var actualResult = useCase.execute(command);

        assertNotNull(actualResult);
        assertNotNull(actualResult.id());

        verify(videoGateway).findById(eq(video.getId()));

        verify(videoGateway).update(argThat(actualVideo ->
                Objects.equals(expectedTitle, actualVideo.getTitle())
                        && Objects.equals(expectedDescription, actualVideo.getDescription())
                        && Objects.equals(expectedLaunchYear, actualVideo.getLaunchedAt())
                        && Objects.equals(expectedDuration, actualVideo.getDuration())
                        && Objects.equals(expectedOpened, actualVideo.getOpened())
                        && Objects.equals(expectedPublished, actualVideo.getPublished())
                        && Objects.equals(expectedRating, actualVideo.getRating())
                        && Objects.equals(expectedCategories, actualVideo.getCategories())
                        && Objects.equals(expectedGenres, actualVideo.getGenres())
                        && Objects.equals(expectedMembers, actualVideo.getCastMembers())
                        && Objects.equals(expectedVideo.name(), actualVideo.getVideo().get().name())
                        && Objects.equals(expectedTrailer.name(), actualVideo.getTrailer().get().name())
                        && Objects.equals(expectedBanner.name(), actualVideo.getBanner().get().name())
                        && Objects.equals(expectedThumb.name(), actualVideo.getThumbnail().get().name())
                        && Objects.equals(expectedThumbHalf.name(), actualVideo.getThumbnailHalf().get().name())
                        && Objects.equals(video.getCreatedAt(), actualVideo.getCreatedAt())
                        && video.getUpdatedAt().isBefore(actualVideo.getUpdatedAt())
        ));
    }

    @Test
    public void givenAValidCommandWithoutGenres_whenCallsUpdateVideo_shouldReturnVideoId() {
        final var video = systemDesign();

        final var expectedTitle = title();
        final var expectedDescription = description();
        final var expectedLaunchYear = Year.of(year());
        final var expectedDuration = duration();
        final var expectedOpened = bool();
        final var expectedPublished = bool();
        final var expectedRating = rating();
        final var expectedCategories = Set.of(aulas().getId());
        final var expectedGenres = Set.<GenreID>of();
        final var expectedMembers = Set.of(
                wesley().getId(),
                gabriel().getId()
        );
        final Resource expectedVideo = resource(VideoMediaType.VIDEO);
        final Resource expectedTrailer = resource(VideoMediaType.TRAILER);
        final Resource expectedBanner = resource(VideoMediaType.BANNER);
        final Resource expectedThumb = resource(VideoMediaType.THUMBNAIL);
        final Resource expectedThumbHalf = resource(VideoMediaType.THUMBNAIL_HALF);

        final var command = UpdateVideoCommand.with(
                video.getId().getValue(),
                expectedTitle,
                expectedDescription,
                expectedLaunchYear.getValue(),
                expectedDuration,
                expectedOpened,
                expectedPublished,
                expectedRating.getName(),
                asString(expectedCategories),
                asString(expectedGenres),
                asString(expectedMembers),
                expectedVideo,
                expectedTrailer,
                expectedBanner,
                expectedThumb,
                expectedThumbHalf
        );

        when(videoGateway.findById(any())).thenReturn(Optional.of(Video.with(video)));

        when(categoryGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedCategories));

        when(castMemberGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedMembers));

        mockImageMedia();
        mockAudioVideoMedia();

        when(videoGateway.update(any())).thenAnswer(returnsFirstArg());

        final var actualResult = useCase.execute(command);

        assertNotNull(actualResult);
        assertNotNull(actualResult.id());

        verify(videoGateway).findById(eq(video.getId()));

        verify(videoGateway).update(argThat(actualVideo ->
                Objects.equals(expectedTitle, actualVideo.getTitle())
                        && Objects.equals(expectedDescription, actualVideo.getDescription())
                        && Objects.equals(expectedLaunchYear, actualVideo.getLaunchedAt())
                        && Objects.equals(expectedDuration, actualVideo.getDuration())
                        && Objects.equals(expectedOpened, actualVideo.getOpened())
                        && Objects.equals(expectedPublished, actualVideo.getPublished())
                        && Objects.equals(expectedRating, actualVideo.getRating())
                        && Objects.equals(expectedCategories, actualVideo.getCategories())
                        && Objects.equals(expectedGenres, actualVideo.getGenres())
                        && Objects.equals(expectedMembers, actualVideo.getCastMembers())
                        && Objects.equals(expectedVideo.name(), actualVideo.getVideo().get().name())
                        && Objects.equals(expectedTrailer.name(), actualVideo.getTrailer().get().name())
                        && Objects.equals(expectedBanner.name(), actualVideo.getBanner().get().name())
                        && Objects.equals(expectedThumb.name(), actualVideo.getThumbnail().get().name())
                        && Objects.equals(expectedThumbHalf.name(), actualVideo.getThumbnailHalf().get().name())
                        && Objects.equals(video.getCreatedAt(), actualVideo.getCreatedAt())
                        && video.getUpdatedAt().isBefore(actualVideo.getUpdatedAt())
        ));
    }

    @Test
    public void givenAValidCommandWithoutCastMembers_whenCallsUpdateVideo_shouldReturnVideoId() {
        final var video = systemDesign();

        final var expectedTitle = title();
        final var expectedDescription = description();
        final var expectedLaunchYear = Year.of(year());
        final var expectedDuration = duration();
        final var expectedOpened = bool();
        final var expectedPublished = bool();
        final var expectedRating = rating();
        final var expectedCategories = Set.of(aulas().getId());
        final var expectedGenres = Set.of(tech().getId());
        final var expectedMembers = Set.<CastMemberID>of();
        final Resource expectedVideo = resource(VideoMediaType.VIDEO);
        final Resource expectedTrailer = resource(VideoMediaType.TRAILER);
        final Resource expectedBanner = resource(VideoMediaType.BANNER);
        final Resource expectedThumb = resource(VideoMediaType.THUMBNAIL);
        final Resource expectedThumbHalf = resource(VideoMediaType.THUMBNAIL_HALF);

        final var command = UpdateVideoCommand.with(
                video.getId().getValue(),
                expectedTitle,
                expectedDescription,
                expectedLaunchYear.getValue(),
                expectedDuration,
                expectedOpened,
                expectedPublished,
                expectedRating.getName(),
                asString(expectedCategories),
                asString(expectedGenres),
                asString(expectedMembers),
                expectedVideo,
                expectedTrailer,
                expectedBanner,
                expectedThumb,
                expectedThumbHalf
        );

        when(videoGateway.findById(any())).thenReturn(Optional.of(Video.with(video)));

        when(categoryGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedCategories));

        when(genreGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedGenres));

        mockImageMedia();
        mockAudioVideoMedia();

        when(videoGateway.update(any())).thenAnswer(returnsFirstArg());

        final var actualResult = useCase.execute(command);

        assertNotNull(actualResult);
        assertNotNull(actualResult.id());

        verify(videoGateway).findById(eq(video.getId()));

        verify(videoGateway).update(argThat(actualVideo ->
                Objects.equals(expectedTitle, actualVideo.getTitle())
                        && Objects.equals(expectedDescription, actualVideo.getDescription())
                        && Objects.equals(expectedLaunchYear, actualVideo.getLaunchedAt())
                        && Objects.equals(expectedDuration, actualVideo.getDuration())
                        && Objects.equals(expectedOpened, actualVideo.getOpened())
                        && Objects.equals(expectedPublished, actualVideo.getPublished())
                        && Objects.equals(expectedRating, actualVideo.getRating())
                        && Objects.equals(expectedCategories, actualVideo.getCategories())
                        && Objects.equals(expectedGenres, actualVideo.getGenres())
                        && Objects.equals(expectedMembers, actualVideo.getCastMembers())
                        && Objects.equals(expectedVideo.name(), actualVideo.getVideo().get().name())
                        && Objects.equals(expectedTrailer.name(), actualVideo.getTrailer().get().name())
                        && Objects.equals(expectedBanner.name(), actualVideo.getBanner().get().name())
                        && Objects.equals(expectedThumb.name(), actualVideo.getThumbnail().get().name())
                        && Objects.equals(expectedThumbHalf.name(), actualVideo.getThumbnailHalf().get().name())
                        && Objects.equals(video.getCreatedAt(), actualVideo.getCreatedAt())
                        && video.getUpdatedAt().isBefore(actualVideo.getUpdatedAt())
        ));
    }

    @Test
    public void givenAValidCommandWithoutResources_whenCallsUpdateVideo_shouldReturnVideoId() {
        final var video = systemDesign();

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
        final Resource expectedVideo = null;
        final Resource expectedTrailer = null;
        final Resource expectedBanner = null;
        final Resource expectedThumb = null;
        final Resource expectedThumbHalf = null;

        final var command = UpdateVideoCommand.with(
                video.getId().getValue(),
                expectedTitle,
                expectedDescription,
                expectedLaunchYear.getValue(),
                expectedDuration,
                expectedOpened,
                expectedPublished,
                expectedRating.getName(),
                asString(expectedCategories),
                asString(expectedGenres),
                asString(expectedMembers),
                expectedVideo,
                expectedTrailer,
                expectedBanner,
                expectedThumb,
                expectedThumbHalf
        );

        when(videoGateway.findById(any())).thenReturn(Optional.of(Video.with(video)));

        when(categoryGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedCategories));

        when(castMemberGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedMembers));

        when(genreGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedGenres));

        when(videoGateway.update(any())).thenAnswer(returnsFirstArg());

        final var actualResult = useCase.execute(command);

        assertNotNull(actualResult);
        assertNotNull(actualResult.id());

        verify(videoGateway).findById(eq(video.getId()));

        verify(videoGateway).update(argThat(actualVideo ->
                Objects.equals(expectedTitle, actualVideo.getTitle())
                        && Objects.equals(expectedDescription, actualVideo.getDescription())
                        && Objects.equals(expectedLaunchYear, actualVideo.getLaunchedAt())
                        && Objects.equals(expectedDuration, actualVideo.getDuration())
                        && Objects.equals(expectedOpened, actualVideo.getOpened())
                        && Objects.equals(expectedPublished, actualVideo.getPublished())
                        && Objects.equals(expectedRating, actualVideo.getRating())
                        && Objects.equals(expectedCategories, actualVideo.getCategories())
                        && Objects.equals(expectedGenres, actualVideo.getGenres())
                        && Objects.equals(expectedMembers, actualVideo.getCastMembers())
                        && actualVideo.getVideo().isEmpty()
                        && actualVideo.getTrailer().isEmpty()
                        && actualVideo.getBanner().isEmpty()
                        && actualVideo.getThumbnail().isEmpty()
                        && actualVideo.getThumbnailHalf().isEmpty()
                        && Objects.equals(video.getCreatedAt(), actualVideo.getCreatedAt())
                        && video.getUpdatedAt().isBefore(actualVideo.getUpdatedAt())
        ));
    }

    @Test
    public void givenANullTitle_whenCallsUpdateVideo_shouldReturnDomainException() {
        final var video = systemDesign();

        final var expectedErrorMessage = "'title' should not be null";
        final var expectedErrorCount = 1;

        final String expectedTitle = null;
        final var expectedDescription = description();
        final var expectedLaunchYear = Year.of(year());
        final var expectedDuration = duration();
        final var expectedOpened = bool();
        final var expectedPublished = bool();
        final var expectedRating = rating();
        final var expectedCategories = Set.<CategoryID>of();
        final var expectedGenres = Set.<GenreID>of();
        final var expectedMembers = Set.<CastMemberID>of();
        final Resource expectedVideo = null;
        final Resource expectedTrailer = null;
        final Resource expectedBanner = null;
        final Resource expectedThumb = null;
        final Resource expectedThumbHalf = null;

        final var command = UpdateVideoCommand.with(
                video.getId().getValue(),
                expectedTitle,
                expectedDescription,
                expectedLaunchYear.getValue(),
                expectedDuration,
                expectedOpened,
                expectedPublished,
                expectedRating.getName(),
                asString(expectedCategories),
                asString(expectedGenres),
                asString(expectedMembers),
                expectedVideo,
                expectedTrailer,
                expectedBanner,
                expectedThumb,
                expectedThumbHalf
        );

        when(videoGateway.findById(any())).thenReturn(Optional.of(Video.with(video)));

        final var actualException = assertThrows(DomainException.class, () -> {
            useCase.execute(command);
        });

        assertNotNull(actualException);
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        verify(videoGateway).findById(eq(video.getId()));

        verify(categoryGateway, never()).existsByIds(any());
        verify(castMemberGateway, never()).existsByIds(any());
        verify(genreGateway, never()).existsByIds(any());
        verify(mediaResourceGateway, never()).storeAudioVideo(any(), any());
        verify(videoGateway, never()).update(any());
    }

    @Test
    public void givenAEmptyTitle_whenCallsUpdateVideo_shouldReturnDomainException() {
        final var video = systemDesign();

        final var expectedErrorMessage = "'title' should not be empty";
        final var expectedErrorCount = 1;

        final String expectedTitle = " ";
        final var expectedDescription = description();
        final var expectedLaunchYear = Year.of(year());
        final var expectedDuration = duration();
        final var expectedOpened = bool();
        final var expectedPublished = bool();
        final var expectedRating = rating();
        final var expectedCategories = Set.<CategoryID>of();
        final var expectedGenres = Set.<GenreID>of();
        final var expectedMembers = Set.<CastMemberID>of();
        final Resource expectedVideo = null;
        final Resource expectedTrailer = null;
        final Resource expectedBanner = null;
        final Resource expectedThumb = null;
        final Resource expectedThumbHalf = null;

        final var command = UpdateVideoCommand.with(
                video.getId().getValue(),
                expectedTitle,
                expectedDescription,
                expectedLaunchYear.getValue(),
                expectedDuration,
                expectedOpened,
                expectedPublished,
                expectedRating.getName(),
                asString(expectedCategories),
                asString(expectedGenres),
                asString(expectedMembers),
                expectedVideo,
                expectedTrailer,
                expectedBanner,
                expectedThumb,
                expectedThumbHalf
        );

        when(videoGateway.findById(any())).thenReturn(Optional.of(Video.with(video)));

        final var actualException = assertThrows(DomainException.class, () -> {
            useCase.execute(command);
        });

        assertNotNull(actualException);
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        verify(videoGateway).findById(eq(video.getId()));

        verify(categoryGateway, never()).existsByIds(any());
        verify(castMemberGateway, never()).existsByIds(any());
        verify(genreGateway, never()).existsByIds(any());
        verify(mediaResourceGateway, never()).storeAudioVideo(any(), any());
        verify(videoGateway, never()).update(any());
    }

    @Test
    public void givenANullRating_whenCallsUpdateVideo_shouldReturnDomainException() {
        final var video = systemDesign();

        final var expectedErrorMessage = "'rating' should not be null";
        final var expectedErrorCount = 1;

        final var expectedTitle = title();
        final var expectedDescription = description();
        final var expectedLaunchYear = Year.of(year());
        final var expectedDuration = duration();
        final var expectedOpened = bool();
        final var expectedPublished = bool();
        final String expectedRating = null;
        final var expectedCategories = Set.<CategoryID>of();
        final var expectedGenres = Set.<GenreID>of();
        final var expectedMembers = Set.<CastMemberID>of();
        final Resource expectedVideo = null;
        final Resource expectedTrailer = null;
        final Resource expectedBanner = null;
        final Resource expectedThumb = null;
        final Resource expectedThumbHalf = null;

        final var command = UpdateVideoCommand.with(
                video.getId().getValue(),
                expectedTitle,
                expectedDescription,
                expectedLaunchYear.getValue(),
                expectedDuration,
                expectedOpened,
                expectedPublished,
                expectedRating,
                asString(expectedCategories),
                asString(expectedGenres),
                asString(expectedMembers),
                expectedVideo,
                expectedTrailer,
                expectedBanner,
                expectedThumb,
                expectedThumbHalf
        );

        when(videoGateway.findById(any())).thenReturn(Optional.of(Video.with(video)));

        final var actualException = assertThrows(DomainException.class, () -> {
            useCase.execute(command);
        });

        assertNotNull(actualException);
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        verify(videoGateway).findById(eq(video.getId()));

        verify(categoryGateway, never()).existsByIds(any());
        verify(castMemberGateway, never()).existsByIds(any());
        verify(genreGateway, never()).existsByIds(any());
        verify(mediaResourceGateway, never()).storeAudioVideo(any(), any());
        verify(videoGateway, never()).update(any());
    }

    @Test
    public void givenAnInvalidRating_whenCallsUpdateVideo_shouldReturnDomainException() {
        final var video = systemDesign();

        final var expectedErrorMessage = "'rating' should not be null";
        final var expectedErrorCount = 1;

        final var expectedTitle = title();
        final var expectedDescription = description();
        final var expectedLaunchYear = Year.of(year());
        final var expectedDuration = duration();
        final var expectedOpened = bool();
        final var expectedPublished = bool();
        final String expectedRating = "ADASDA";
        final var expectedCategories = Set.<CategoryID>of();
        final var expectedGenres = Set.<GenreID>of();
        final var expectedMembers = Set.<CastMemberID>of();
        final Resource expectedVideo = null;
        final Resource expectedTrailer = null;
        final Resource expectedBanner = null;
        final Resource expectedThumb = null;
        final Resource expectedThumbHalf = null;

        final var command = UpdateVideoCommand.with(
                video.getId().getValue(),
                expectedTitle,
                expectedDescription,
                expectedLaunchYear.getValue(),
                expectedDuration,
                expectedOpened,
                expectedPublished,
                expectedRating,
                asString(expectedCategories),
                asString(expectedGenres),
                asString(expectedMembers),
                expectedVideo,
                expectedTrailer,
                expectedBanner,
                expectedThumb,
                expectedThumbHalf
        );

        when(videoGateway.findById(any())).thenReturn(Optional.of(Video.with(video)));

        final var actualException = assertThrows(DomainException.class, () -> {
            useCase.execute(command);
        });

        assertNotNull(actualException);
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        verify(videoGateway).findById(eq(video.getId()));

        verify(categoryGateway, never()).existsByIds(any());
        verify(castMemberGateway, never()).existsByIds(any());
        verify(genreGateway, never()).existsByIds(any());
        verify(mediaResourceGateway, never()).storeAudioVideo(any(), any());
        verify(videoGateway, never()).update(any());
    }

    @Test
    public void givenANullLaunchedAt_whenCallsUpdateVideo_shouldReturnDomainException() {
        final var video = systemDesign();

        final var expectedErrorMessage = "'launchedAt' should not be null";
        final var expectedErrorCount = 1;

        final var expectedTitle = title();
        final var expectedDescription = description();
        final Integer expectedLaunchYear = null;
        final var expectedDuration = duration();
        final var expectedOpened = bool();
        final var expectedPublished = bool();
        final var expectedRating = rating();
        final var expectedCategories = Set.<CategoryID>of();
        final var expectedGenres = Set.<GenreID>of();
        final var expectedMembers = Set.<CastMemberID>of();
        final Resource expectedVideo = null;
        final Resource expectedTrailer = null;
        final Resource expectedBanner = null;
        final Resource expectedThumb = null;
        final Resource expectedThumbHalf = null;

        final var command = UpdateVideoCommand.with(
                video.getId().getValue(),
                expectedTitle,
                expectedDescription,
                expectedLaunchYear,
                expectedDuration,
                expectedOpened,
                expectedPublished,
                expectedRating.getName(),
                asString(expectedCategories),
                asString(expectedGenres),
                asString(expectedMembers),
                expectedVideo,
                expectedTrailer,
                expectedBanner,
                expectedThumb,
                expectedThumbHalf
        );

        when(videoGateway.findById(any())).thenReturn(Optional.of(Video.with(video)));

        final var actualException = assertThrows(DomainException.class, () -> {
            useCase.execute(command);
        });

        assertNotNull(actualException);
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        verify(videoGateway).findById(eq(video.getId()));

        verify(categoryGateway, never()).existsByIds(any());
        verify(castMemberGateway, never()).existsByIds(any());
        verify(genreGateway, never()).existsByIds(any());
        verify(mediaResourceGateway, never()).storeAudioVideo(any(), any());
        verify(videoGateway, never()).update(any());
    }

    @Test
    public void givenAValidCommand_whenCallsUpdateVideoAndSomeCategoriesDoesNotExists_shouldReturnDomainException() {
        final var video = systemDesign();
        final var aulasId = aulas().getId();

        final var expectedErrorMessage = "Some categories could not be found: %s".formatted(aulasId.getValue());
        final var expectedErrorCount = 1;

        final var expectedTitle = title();
        final var expectedDescription = description();
        final var expectedLaunchYear = year();
        final var expectedDuration = duration();
        final var expectedOpened = bool();
        final var expectedPublished = bool();
        final var expectedRating = rating();
        final var expectedCategories = Set.of(aulasId);
        final var expectedGenres = Set.of(tech().getId());
        final var expectedMembers = Set.of(wesley().getId());
        final Resource expectedVideo = null;
        final Resource expectedTrailer = null;
        final Resource expectedBanner = null;
        final Resource expectedThumb = null;
        final Resource expectedThumbHalf = null;

        final var command = UpdateVideoCommand.with(
                video.getId().getValue(),
                expectedTitle,
                expectedDescription,
                expectedLaunchYear,
                expectedDuration,
                expectedOpened,
                expectedPublished,
                expectedRating.getName(),
                asString(expectedCategories),
                asString(expectedGenres),
                asString(expectedMembers),
                expectedVideo,
                expectedTrailer,
                expectedBanner,
                expectedThumb,
                expectedThumbHalf
        );

        when(videoGateway.findById(any())).thenReturn(Optional.of(Video.with(video)));

        when(categoryGateway.existsByIds(any())).thenReturn(new ArrayList<>());

        when(castMemberGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedMembers));

        when(genreGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedGenres));

        final var actualException = assertThrows(NotificationException.class, () -> {
            useCase.execute(command);
        });

        assertNotNull(actualException);
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        verify(categoryGateway).existsByIds(eq(expectedCategories));
        verify(castMemberGateway).existsByIds(eq(expectedMembers));
        verify(genreGateway).existsByIds(eq(expectedGenres));
        verify(mediaResourceGateway, never()).storeImage(any(), any());
        verify(videoGateway, never()).update(any());
    }

    @Test
    public void givenAValidCommand_whenCallsUpdateVideoAndSomeGenresDoesNotExists_shouldReturnDomainException() {
        final var video = systemDesign();
        final var techId = tech().getId();

        final var expectedErrorMessage = "Some genres could not be found: %s".formatted(techId.getValue());
        final var expectedErrorCount = 1;

        final var expectedTitle = title();
        final var expectedDescription = description();
        final var expectedLaunchYear = year();
        final var expectedDuration = duration();
        final var expectedOpened = bool();
        final var expectedPublished = bool();
        final var expectedRating = rating();
        final var expectedCategories = Set.of(aulas().getId());
        final var expectedGenres = Set.of(techId);
        final var expectedMembers = Set.of(wesley().getId());
        final Resource expectedVideo = null;
        final Resource expectedTrailer = null;
        final Resource expectedBanner = null;
        final Resource expectedThumb = null;
        final Resource expectedThumbHalf = null;

        final var command = UpdateVideoCommand.with(
                video.getId().getValue(),
                expectedTitle,
                expectedDescription,
                expectedLaunchYear,
                expectedDuration,
                expectedOpened,
                expectedPublished,
                expectedRating.getName(),
                asString(expectedCategories),
                asString(expectedGenres),
                asString(expectedMembers),
                expectedVideo,
                expectedTrailer,
                expectedBanner,
                expectedThumb,
                expectedThumbHalf
        );

        when(videoGateway.findById(any())).thenReturn(Optional.of(Video.with(video)));

        when(categoryGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedCategories));

        when(castMemberGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedMembers));

        when(genreGateway.existsByIds(any())).thenReturn(new ArrayList<>());

        final var actualException = assertThrows(NotificationException.class, () -> {
            useCase.execute(command);
        });

        assertNotNull(actualException);
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        verify(categoryGateway).existsByIds(eq(expectedCategories));
        verify(castMemberGateway).existsByIds(eq(expectedMembers));
        verify(genreGateway).existsByIds(eq(expectedGenres));
        verify(mediaResourceGateway, never()).storeImage(any(), any());
        verify(videoGateway, never()).create(any());
    }

    @Test
    public void givenAValidCommand_whenCallsUpdateVideoAndSomeCastMembersDoesNotExists_shouldReturnDomainException() {
        final var video = systemDesign();
        final var wesleyId = wesley().getId();

        final var expectedErrorMessage = "Some cast members could not be found: %s".formatted(wesleyId.getValue());
        final var expectedErrorCount = 1;

        final var expectedTitle = title();
        final var expectedDescription = description();
        final var expectedLaunchYear = year();
        final var expectedDuration = duration();
        final var expectedOpened = bool();
        final var expectedPublished = bool();
        final var expectedRating = rating();
        final var expectedCategories = Set.of(aulas().getId());
        final var expectedGenres = Set.of(tech().getId());
        final var expectedMembers = Set.of(wesleyId);
        final Resource expectedVideo = null;
        final Resource expectedTrailer = null;
        final Resource expectedBanner = null;
        final Resource expectedThumb = null;
        final Resource expectedThumbHalf = null;

        final var command = UpdateVideoCommand.with(
                video.getId().getValue(),
                expectedTitle,
                expectedDescription,
                expectedLaunchYear,
                expectedDuration,
                expectedOpened,
                expectedPublished,
                expectedRating.getName(),
                asString(expectedCategories),
                asString(expectedGenres),
                asString(expectedMembers),
                expectedVideo,
                expectedTrailer,
                expectedBanner,
                expectedThumb,
                expectedThumbHalf
        );

        when(videoGateway.findById(any())).thenReturn(Optional.of(Video.with(video)));

        when(categoryGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedCategories));

        when(castMemberGateway.existsByIds(any())).thenReturn(new ArrayList<>());

        when(genreGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedGenres));

        final var actualException = assertThrows(NotificationException.class, () -> {
            useCase.execute(command);
        });

        assertNotNull(actualException);
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        verify(categoryGateway).existsByIds(eq(expectedCategories));
        verify(castMemberGateway).existsByIds(eq(expectedMembers));
        verify(genreGateway).existsByIds(eq(expectedGenres));
        verify(mediaResourceGateway, never()).storeImage(any(), any());
        verify(videoGateway, never()).create(any());
    }

    @Test
    public void givenAValidCommand_whenCallsCreateVideoThrowsException_shouldCallClearResources() {
        final var video = systemDesign();
        final var expectedErrorMessage = "An error on create video was observed [videoId:";

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
        final Resource expectedVideo = resource(VideoMediaType.VIDEO);
        final Resource expectedTrailer = resource(VideoMediaType.TRAILER);
        final Resource expectedBanner = resource(VideoMediaType.BANNER);
        final Resource expectedThumb = resource(VideoMediaType.THUMBNAIL);
        final Resource expectedThumbHalf = resource(VideoMediaType.THUMBNAIL_HALF);

        final var command = UpdateVideoCommand.with(
                video.getId().getValue(),
                expectedTitle,
                expectedDescription,
                expectedLaunchYear.getValue(),
                expectedDuration,
                expectedOpened,
                expectedPublished,
                expectedRating.getName(),
                asString(expectedCategories),
                asString(expectedGenres),
                asString(expectedMembers),
                expectedVideo,
                expectedTrailer,
                expectedBanner,
                expectedThumb,
                expectedThumbHalf
        );

        when(videoGateway.findById(any())).thenReturn(Optional.of(Video.with(video)));

        when(categoryGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedCategories));

        when(castMemberGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedMembers));

        when(genreGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedGenres));

        mockImageMedia();
        mockAudioVideoMedia();

        when(videoGateway.update(any())).thenThrow(new RuntimeException("Internal Server Error"));

        final var actualResult = assertThrows(InternalErrorException.class, () -> {
            useCase.execute(command);
        });

        assertNotNull(actualResult);
        assertTrue(actualResult.getMessage().startsWith(expectedErrorMessage));

        verify(mediaResourceGateway, never()).clearResources(any());
    }

    private void mockImageMedia() {
        when(mediaResourceGateway.storeImage(any(), any())).thenAnswer(t -> {
            final var resource = t.getArgument(1, Resource.class);
            return ImageMedia.with(resource.checksum(), resource.name(), "/img");
        });
    }

    private void mockAudioVideoMedia() {
        when(mediaResourceGateway.storeAudioVideo(any(), any())).thenAnswer(t -> {
            final var resource = t.getArgument(1, Resource.class);
            return AudioVideoMedia.with(
                    resource.checksum(),
                    resource.name(),
                    "/img"
            );
        });
    }
}
