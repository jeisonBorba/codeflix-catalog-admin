package br.com.codeflix.catalog.admin.application.video.delete;

import br.com.codeflix.catalog.admin.domain.video.VideoGateway;
import br.com.codeflix.catalog.admin.domain.video.VideoID;

import java.util.Objects;

public class DefaultDeleteVideoUseCase extends DeleteVideoUseCase {

    private final VideoGateway videoGateway;

    public DefaultDeleteVideoUseCase(final VideoGateway videoGateway) {
        this.videoGateway = Objects.requireNonNull(videoGateway);
    }

    @Override
    public void execute(final String id) {
        this.videoGateway.deleteById(VideoID.from(id));
    }
}
