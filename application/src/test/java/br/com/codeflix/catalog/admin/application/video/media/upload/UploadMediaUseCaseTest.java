package br.com.codeflix.catalog.admin.application.video.media.upload;

import br.com.codeflix.catalog.admin.application.UseCaseTest;
import br.com.codeflix.catalog.admin.domain.category.Fixture;
import br.com.codeflix.catalog.admin.domain.exceptions.NotFoundException;
import br.com.codeflix.catalog.admin.domain.video.MediaResourceGateway;
import br.com.codeflix.catalog.admin.domain.video.VideoGateway;
import br.com.codeflix.catalog.admin.domain.video.VideoMediaType;
import br.com.codeflix.catalog.admin.domain.video.VideoResource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static br.com.codeflix.catalog.admin.domain.category.Fixture.Videos.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UploadMediaUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultUploadMediaUseCase useCase;

    @Mock
    private MediaResourceGateway mediaResourceGateway;

    @Mock
    private VideoGateway videoGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(mediaResourceGateway, videoGateway);
    }

    @Test
    public void givenCmdToUpload_whenIsValid_shouldUpdateVideoMediaAndPersistIt() {
        final var video = systemDesign();
        final var expectedId = video.getId();
        final var expectedType = VideoMediaType.VIDEO;
        final var expectedResource = resource(expectedType);
        final var expectedVideoResource = VideoResource.with(expectedResource, expectedType);
        final var expectedMedia = Fixture.Videos.audioVideo(expectedType);

        when(videoGateway.findById(any())).thenReturn(Optional.of(video));

        when(mediaResourceGateway.storeAudioVideo(any(), any())).thenReturn(expectedMedia);

        when(videoGateway.update(any())).thenAnswer(returnsFirstArg());

        final var command = UploadMediaCommand.with(expectedId.getValue(), expectedVideoResource);

        final var actualOutput = useCase.execute(command);

        assertEquals(expectedType, actualOutput.mediaType());
        assertEquals(expectedId.getValue(), actualOutput.videoId());

        verify(videoGateway).findById(eq(expectedId));

        verify(mediaResourceGateway).storeAudioVideo(eq(expectedId), eq(expectedVideoResource));

        verify(videoGateway).update(argThat(actualVideo ->
                Objects.equals(expectedMedia, actualVideo.getVideo().get())
                        && actualVideo.getTrailer().isEmpty()
                        && actualVideo.getBanner().isEmpty()
                        && actualVideo.getThumbnail().isEmpty()
                        && actualVideo.getThumbnailHalf().isEmpty()
        ));
    }

    @Test
    public void givenCmdToUpload_whenIsValid_shouldUpdateTrailerMediaAndPersistIt() {
        final var video = systemDesign();
        final var expectedId = video.getId();
        final var expectedType = VideoMediaType.TRAILER;
        final var expectedResource = resource(expectedType);
        final var expectedVideoResource = VideoResource.with(expectedResource, expectedType);
        final var expectedMedia = Fixture.Videos.audioVideo(expectedType);

        when(videoGateway.findById(any())).thenReturn(Optional.of(video));

        when(mediaResourceGateway.storeAudioVideo(any(), any())).thenReturn(expectedMedia);

        when(videoGateway.update(any())).thenAnswer(returnsFirstArg());

        final var command = UploadMediaCommand.with(expectedId.getValue(), expectedVideoResource);

        final var actualOutput = useCase.execute(command);

        assertEquals(expectedType, actualOutput.mediaType());
        assertEquals(expectedId.getValue(), actualOutput.videoId());

        verify(videoGateway).findById(eq(expectedId));

        verify(mediaResourceGateway).storeAudioVideo(eq(expectedId), eq(expectedVideoResource));

        verify(videoGateway).update(argThat(actualVideo ->
                actualVideo.getVideo().isEmpty()
                        && Objects.equals(expectedMedia, actualVideo.getTrailer().get())
                        && actualVideo.getBanner().isEmpty()
                        && actualVideo.getThumbnail().isEmpty()
                        && actualVideo.getThumbnailHalf().isEmpty()
        ));
    }

    @Test
    public void givenCmdToUpload_whenIsValid_shouldUpdateBannerMediaAndPersistIt() {
        final var video = systemDesign();
        final var expectedId = video.getId();
        final var expectedType = VideoMediaType.BANNER;
        final var expectedResource = resource(expectedType);
        final var expectedVideoResource = VideoResource.with(expectedResource, expectedType);
        final var expectedMedia = imageMedia(expectedType);

        when(videoGateway.findById(any())).thenReturn(Optional.of(video));

        when(mediaResourceGateway.storeImage(any(), any())).thenReturn(expectedMedia);

        when(videoGateway.update(any())).thenAnswer(returnsFirstArg());

        final var command = UploadMediaCommand.with(expectedId.getValue(), expectedVideoResource);

        final var actualOutput = useCase.execute(command);

        assertEquals(expectedType, actualOutput.mediaType());
        assertEquals(expectedId.getValue(), actualOutput.videoId());

        verify(videoGateway).findById(eq(expectedId));

        verify(mediaResourceGateway).storeImage(eq(expectedId), eq(expectedVideoResource));

        verify(videoGateway).update(argThat(actualVideo ->
                actualVideo.getVideo().isEmpty()
                        && actualVideo.getTrailer().isEmpty()
                        && Objects.equals(expectedMedia, actualVideo.getBanner().get())
                        && actualVideo.getThumbnail().isEmpty()
                        && actualVideo.getThumbnailHalf().isEmpty()
        ));
    }

    @Test
    public void givenCmdToUpload_whenIsValid_shouldUpdateThumbnailMediaAndPersistIt() {
        final var aVideo = systemDesign();
        final var expectedId = aVideo.getId();
        final var expectedType = VideoMediaType.THUMBNAIL;
        final var expectedResource = resource(expectedType);
        final var expectedVideoResource = VideoResource.with(expectedResource, expectedType);
        final var expectedMedia = imageMedia(expectedType);

        when(videoGateway.findById(any())).thenReturn(Optional.of(aVideo));

        when(mediaResourceGateway.storeImage(any(), any())).thenReturn(expectedMedia);

        when(videoGateway.update(any())).thenAnswer(returnsFirstArg());

        final var command = UploadMediaCommand.with(expectedId.getValue(), expectedVideoResource);

        final var actualOutput = useCase.execute(command);

        assertEquals(expectedType, actualOutput.mediaType());
        assertEquals(expectedId.getValue(), actualOutput.videoId());

        verify(videoGateway).findById(eq(expectedId));

        verify(mediaResourceGateway).storeImage(eq(expectedId), eq(expectedVideoResource));

        verify(videoGateway).update(argThat(actualVideo ->
                actualVideo.getVideo().isEmpty()
                        && actualVideo.getTrailer().isEmpty()
                        && actualVideo.getBanner().isEmpty()
                        && Objects.equals(expectedMedia, actualVideo.getThumbnail().get())
                        && actualVideo.getThumbnailHalf().isEmpty()
        ));
    }

    @Test
    public void givenCmdToUpload_whenIsValid_shouldUpdateThumbnailHalfMediaAndPersistIt() {
        final var video = systemDesign();
        final var expectedId = video.getId();
        final var expectedType = VideoMediaType.THUMBNAIL_HALF;
        final var expectedResource = resource(expectedType);
        final var expectedVideoResource = VideoResource.with(expectedResource, expectedType);
        final var expectedMedia = imageMedia(expectedType);

        when(videoGateway.findById(any())).thenReturn(Optional.of(video));

        when(mediaResourceGateway.storeImage(any(), any())).thenReturn(expectedMedia);

        when(videoGateway.update(any())).thenAnswer(returnsFirstArg());

        final var command = UploadMediaCommand.with(expectedId.getValue(), expectedVideoResource);

        final var actualOutput = useCase.execute(command);

        assertEquals(expectedType, actualOutput.mediaType());
        assertEquals(expectedId.getValue(), actualOutput.videoId());

        verify(videoGateway).findById(eq(expectedId));

        verify(mediaResourceGateway).storeImage(eq(expectedId), eq(expectedVideoResource));

        verify(videoGateway).update(argThat(actualVideo ->
                actualVideo.getVideo().isEmpty()
                        && actualVideo.getTrailer().isEmpty()
                        && actualVideo.getBanner().isEmpty()
                        && actualVideo.getThumbnail().isEmpty()
                        && Objects.equals(expectedMedia, actualVideo.getThumbnailHalf().get())
        ));
    }

    @Test
    public void givenCmdToUpload_whenVideoIsInvalid_shouldReturnNotFound() {
        final var video = systemDesign();
        final var expectedId = video.getId();
        final var expectedType = VideoMediaType.THUMBNAIL_HALF;
        final var expectedResource = resource(expectedType);
        final var expectedVideoResource = VideoResource.with(expectedResource, expectedType);

        final var expectedErrorMessage = "Video with ID %s was not found".formatted(expectedId.getValue());

        when(videoGateway.findById(any())).thenReturn(Optional.empty());

        final var command = UploadMediaCommand.with(expectedId.getValue(), expectedVideoResource);

        final var actualException = Assertions.assertThrows(
                NotFoundException.class,
                () -> useCase.execute(command)
        );

        assertEquals(expectedErrorMessage, actualException.getMessage());
    }
}
