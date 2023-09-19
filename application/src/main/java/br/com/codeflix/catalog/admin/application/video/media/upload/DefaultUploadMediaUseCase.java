package br.com.codeflix.catalog.admin.application.video.media.upload;

import br.com.codeflix.catalog.admin.domain.exceptions.NotFoundException;
import br.com.codeflix.catalog.admin.domain.video.MediaResourceGateway;
import br.com.codeflix.catalog.admin.domain.video.Video;
import br.com.codeflix.catalog.admin.domain.video.VideoGateway;
import br.com.codeflix.catalog.admin.domain.video.VideoID;

import java.util.Objects;

public class DefaultUploadMediaUseCase extends UploadMediaUseCase {

    private final MediaResourceGateway mediaResourceGateway;
    private final VideoGateway videoGateway;

    public DefaultUploadMediaUseCase(
            final MediaResourceGateway mediaResourceGateway,
            final VideoGateway videoGateway
    ) {
        this.mediaResourceGateway = Objects.requireNonNull(mediaResourceGateway);
        this.videoGateway = Objects.requireNonNull(videoGateway);
    }

    @Override
    public UploadMediaOutput execute(final UploadMediaCommand command) {
        final var id = VideoID.from(command.videoId());
        final var resource = command.videoResource();

        final var video = this.videoGateway.findById(id)
                .orElseThrow(() -> notFound(id));

        switch (resource.type()) {
            case VIDEO -> video.updateVideoMedia(mediaResourceGateway.storeAudioVideo(id, resource));
            case TRAILER -> video.updateTrailerMedia(mediaResourceGateway.storeAudioVideo(id, resource));
            case BANNER -> video.updateBannerMedia(mediaResourceGateway.storeImage(id, resource));
            case THUMBNAIL -> video.updateThumbnailMedia(mediaResourceGateway.storeImage(id, resource));
            case THUMBNAIL_HALF -> video.updateThumbnailHalfMedia(mediaResourceGateway.storeImage(id, resource));
        }

        return UploadMediaOutput.with(videoGateway.update(video), resource.type());
    }

    private NotFoundException notFound(final VideoID id) {
        return NotFoundException.with(Video.class, id);
    }
}
