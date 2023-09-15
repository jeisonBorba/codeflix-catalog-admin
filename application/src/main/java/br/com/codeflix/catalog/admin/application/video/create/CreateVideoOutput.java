package br.com.codeflix.catalog.admin.application.video.create;

import br.com.codeflix.catalog.admin.domain.video.Video;

public record CreateVideoOutput(String id) {

    public static CreateVideoOutput from(final Video video) {
        return new CreateVideoOutput(video.getId().getValue());
    }
}