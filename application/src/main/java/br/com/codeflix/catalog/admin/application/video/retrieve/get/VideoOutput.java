package br.com.codeflix.catalog.admin.application.video.retrieve.get;

import br.com.codeflix.catalog.admin.domain.Identifier;
import br.com.codeflix.catalog.admin.domain.video.AudioVideoMedia;
import br.com.codeflix.catalog.admin.domain.video.ImageMedia;
import br.com.codeflix.catalog.admin.domain.video.Rating;
import br.com.codeflix.catalog.admin.domain.video.Video;

import java.time.Instant;
import java.util.Set;

import static br.com.codeflix.catalog.admin.domain.utils.CollectionUtils.mapTo;

public record VideoOutput(
        String id,
        Instant createdAt,
        Instant updatedAt,
        String title,
        String description,
        int launchedAt,
        double duration,
        boolean opened,
        boolean published,
        Rating rating,
        Set<String> categories,
        Set<String> genres,
        Set<String> castMembers,
        ImageMedia banner,
        ImageMedia thumbnail,
        ImageMedia thumbnailHalf,
        AudioVideoMedia video,
        AudioVideoMedia trailer
) {

    public static VideoOutput from(final Video video) {
        return new VideoOutput(
                video.getId().getValue(),
                video.getCreatedAt(),
                video.getUpdatedAt(),
                video.getTitle(),
                video.getDescription(),
                video.getLaunchedAt().getValue(),
                video.getDuration(),
                video.getOpened(),
                video.getPublished(),
                video.getRating(),
                mapTo(video.getCategories(), Identifier::getValue),
                mapTo(video.getGenres(), Identifier::getValue),
                mapTo(video.getCastMembers(), Identifier::getValue),
                video.getBanner().orElse(null),
                video.getThumbnail().orElse(null),
                video.getThumbnailHalf().orElse(null),
                video.getVideo().orElse(null),
                video.getTrailer().orElse(null)
        );
    }
}
