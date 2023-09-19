package br.com.codeflix.catalog.admin.application.video.media.get;

import br.com.codeflix.catalog.admin.domain.resource.Resource;

public record MediaOutput(
        byte[] content,
        String contentType,
        String name
) {
    public static MediaOutput with(final Resource resource) {
        return new MediaOutput(
                resource.content(),
                resource.contentType(),
                resource.name()
        );
    }
}
