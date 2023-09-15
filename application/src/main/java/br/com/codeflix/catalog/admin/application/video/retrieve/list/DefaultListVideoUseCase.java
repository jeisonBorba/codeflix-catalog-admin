package br.com.codeflix.catalog.admin.application.video.retrieve.list;

import br.com.codeflix.catalog.admin.domain.pagination.Pagination;
import br.com.codeflix.catalog.admin.domain.video.VideoGateway;
import br.com.codeflix.catalog.admin.domain.video.VideoSearchQuery;

import java.util.Objects;

public class DefaultListVideoUseCase extends ListVideoUseCase {

    private final VideoGateway videoGateway;

    public DefaultListVideoUseCase(final VideoGateway videoGateway) {
        this.videoGateway = Objects.requireNonNull(videoGateway);
    }

    @Override
    public Pagination<VideoListOutput> execute(final VideoSearchQuery query) {
        return this.videoGateway.findAll(query)
                .map(VideoListOutput::from);
    }
}
