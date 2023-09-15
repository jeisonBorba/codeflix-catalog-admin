package br.com.codeflix.catalog.admin.application.video.create;

import br.com.codeflix.catalog.admin.application.Fixture;
import br.com.codeflix.catalog.admin.application.UseCaseTest;
import br.com.codeflix.catalog.admin.domain.castmember.CastMemberGateway;
import br.com.codeflix.catalog.admin.domain.category.CategoryGateway;
import br.com.codeflix.catalog.admin.domain.genre.GenreGateway;
import br.com.codeflix.catalog.admin.domain.video.Resource;
import br.com.codeflix.catalog.admin.domain.video.VideoGateway;
import br.com.codeflix.catalog.admin.domain.video.VideoMediaType;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static br.com.codeflix.catalog.admin.application.Fixture.*;
import static br.com.codeflix.catalog.admin.application.Fixture.CastMembers.gabriel;
import static br.com.codeflix.catalog.admin.application.Fixture.CastMembers.wesley;
import static br.com.codeflix.catalog.admin.application.Fixture.Categories.aulas;
import static br.com.codeflix.catalog.admin.application.Fixture.Genres.tech;
import static br.com.codeflix.catalog.admin.application.Fixture.Videos.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CreateVideoUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultCreateVideoUseCase useCase;

    @Mock
    private VideoGateway videoGateway;

    @Mock
    private CategoryGateway categoryGateway;

    @Mock
    private CastMemberGateway castMemberGateway;

    @Mock
    private GenreGateway genreGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(videoGateway, categoryGateway, castMemberGateway, genreGateway);
    }

    @Test
    public void givenAValidCommand_whenCallsCreateVideo_shouldReturnVideoId() {
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

        final var command = CreateVideoCommand.with(
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

        when(categoryGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedCategories));

        when(castMemberGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedMembers));

        when(genreGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedGenres));

//        mockImageMedia();
//        mockAudioVideoMedia();

        when(videoGateway.create(any())).thenAnswer(returnsFirstArg());

        final var actualResult = useCase.execute(command);

        assertNotNull(actualResult);
        assertNotNull(actualResult.id());

        verify(videoGateway).create(argThat(actualVideo ->
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
//                        && Objects.equals(expectedVideo.name(), actualVideo.getVideo().get().name())
//                        && Objects.equals(expectedTrailer.name(), actualVideo.getTrailer().get().name())
//                        && Objects.equals(expectedBanner.name(), actualVideo.getBanner().get().name())
//                        && Objects.equals(expectedThumb.name(), actualVideo.getThumbnail().get().name())
//                        && Objects.equals(expectedThumbHalf.name(), actualVideo.getThumbnailHalf().get().name())
        ));
    }
}
