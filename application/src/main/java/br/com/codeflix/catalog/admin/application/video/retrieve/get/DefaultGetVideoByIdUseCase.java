package br.com.codeflix.catalog.admin.application.video.retrieve.get;

import br.com.codeflix.catalog.admin.domain.exceptions.NotFoundException;
import br.com.codeflix.catalog.admin.domain.video.Video;
import br.com.codeflix.catalog.admin.domain.video.VideoGateway;
import br.com.codeflix.catalog.admin.domain.video.VideoID;

import java.util.Objects;

public class DefaultGetVideoByIdUseCase extends GetVideoByIdUseCase {

    private final VideoGateway videoGateway;

    public DefaultGetVideoByIdUseCase(final VideoGateway videoGateway) {
        this.videoGateway = Objects.requireNonNull(videoGateway);
    }

    @Override
    public VideoOutput execute(final String id) {
        final var videoID = VideoID.from(id);
        return this.videoGateway.findById(videoID)
                .map(VideoOutput::from)
                .orElseThrow(() -> NotFoundException.with(Video.class, videoID));
    }
}
