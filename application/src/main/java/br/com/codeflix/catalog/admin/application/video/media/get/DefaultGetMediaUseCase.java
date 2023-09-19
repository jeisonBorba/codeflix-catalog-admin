package br.com.codeflix.catalog.admin.application.video.media.get;

import br.com.codeflix.catalog.admin.domain.exceptions.NotFoundException;
import br.com.codeflix.catalog.admin.domain.validation.Error;
import br.com.codeflix.catalog.admin.domain.video.MediaResourceGateway;
import br.com.codeflix.catalog.admin.domain.video.VideoID;
import br.com.codeflix.catalog.admin.domain.video.VideoMediaType;

import java.util.Objects;

public class DefaultGetMediaUseCase extends GetMediaUseCase {

    private final MediaResourceGateway mediaResourceGateway;

    public DefaultGetMediaUseCase(final MediaResourceGateway mediaResourceGateway) {
        this.mediaResourceGateway = Objects.requireNonNull(mediaResourceGateway);
    }

    @Override
    public MediaOutput execute(final GetMediaCommand command) {
        final var id = VideoID.from(command.videoId());
        final var type = VideoMediaType.of(command.mediaType())
                .orElseThrow(() -> typeNotFound(command.mediaType()));

        final var resource = this.mediaResourceGateway.getResource(id, type)
                .orElseThrow(() -> notFound(command.videoId(), command.mediaType()));

        return MediaOutput.with(resource);
    }

    private NotFoundException notFound(final String id, final String type) {
        return NotFoundException.with(new Error("Resource %s not found for video %s".formatted(type, id)));
    }

    private NotFoundException typeNotFound(final String type) {
        return NotFoundException.with(new Error("Media type %s doesn't exists".formatted(type)));
    }
}
