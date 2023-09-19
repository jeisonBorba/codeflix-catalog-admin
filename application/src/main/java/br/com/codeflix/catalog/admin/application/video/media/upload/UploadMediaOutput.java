package br.com.codeflix.catalog.admin.application.video.media.upload;

import br.com.codeflix.catalog.admin.domain.video.Video;
import br.com.codeflix.catalog.admin.domain.video.VideoMediaType;

public record UploadMediaOutput(
        String videoId,
        VideoMediaType mediaType
) {

    public static UploadMediaOutput with(final Video video, final VideoMediaType type) {
        return new UploadMediaOutput(video.getId().getValue(), type);
    }
}
