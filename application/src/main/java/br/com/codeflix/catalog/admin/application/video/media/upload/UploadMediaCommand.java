package br.com.codeflix.catalog.admin.application.video.media.upload;

import br.com.codeflix.catalog.admin.domain.video.VideoResource;

public record UploadMediaCommand(
        String videoId,
        VideoResource videoResource
) {

    public static UploadMediaCommand with(final String id, final VideoResource resource) {
        return new UploadMediaCommand(id, resource);
    }
}
