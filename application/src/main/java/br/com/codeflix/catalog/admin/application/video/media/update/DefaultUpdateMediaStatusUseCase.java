package br.com.codeflix.catalog.admin.application.video.media.update;

import br.com.codeflix.catalog.admin.domain.exceptions.NotFoundException;
import br.com.codeflix.catalog.admin.domain.video.*;

import java.util.Objects;

import static br.com.codeflix.catalog.admin.domain.video.VideoMediaType.TRAILER;
import static br.com.codeflix.catalog.admin.domain.video.VideoMediaType.VIDEO;

public class DefaultUpdateMediaStatusUseCase extends UpdateMediaStatusUseCase {

    private final VideoGateway videoGateway;

    public DefaultUpdateMediaStatusUseCase(final VideoGateway videoGateway) {
        this.videoGateway = Objects.requireNonNull(videoGateway);
    }

    @Override
    public void execute(final UpdateMediaStatusCommand command) {
        final var id = VideoID.from(command.videoId());
        final var resourceId = command.resourceId();
        final var folder = command.folder();
        final var filename = command.filename();

        final var video = this.videoGateway.findById(id)
                .orElseThrow(() -> notFound(id));

        final var encodedPath = "%s/%s".formatted(folder, filename);

        if (matches(resourceId, video.getVideo().orElse(null))) {
            updateVideo(VIDEO, command.status(), video, encodedPath);
        } else if (matches(resourceId, video.getTrailer().orElse(null))) {
            updateVideo(TRAILER, command.status(), video, encodedPath);
        }
    }

    private void updateVideo(final VideoMediaType type, final MediaStatus status, final Video video, final String encodedPath) {
        switch (status) {
            case PENDING -> {}
            case PROCESSING -> video.processing(type);
            case COMPLETED -> video.completed(type, encodedPath);
        }

        this.videoGateway.update(video);
    }

    private boolean matches(final String id, final AudioVideoMedia media) {
        if (media == null) {
            return false;
        }

        return media.id().equals(id);
    }

    private NotFoundException notFound(final VideoID id) {
        return NotFoundException.with(Video.class, id);
    }
}
