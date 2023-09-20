package br.com.codeflix.catalog.admin.infrastructure.video.models;

import br.com.codeflix.catalog.admin.domain.video.VideoMediaType;
import com.fasterxml.jackson.annotation.JsonProperty;

public record UploadMediaResponse(
        @JsonProperty("video_id") String videoId,
        @JsonProperty("media_type") VideoMediaType mediaType
) {
}
