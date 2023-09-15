package br.com.codeflix.catalog.admin.application.video.update;

import br.com.codeflix.catalog.admin.domain.video.Video;

public record UpdateVideoOutput(String id) {

    public static UpdateVideoOutput from(final Video video) {
        return new UpdateVideoOutput(video.getId().getValue());
    }
}
