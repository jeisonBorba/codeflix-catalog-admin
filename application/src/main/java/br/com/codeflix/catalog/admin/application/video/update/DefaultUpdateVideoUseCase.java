package br.com.codeflix.catalog.admin.application.video.update;

import br.com.codeflix.catalog.admin.domain.Identifier;
import br.com.codeflix.catalog.admin.domain.castmember.CastMemberGateway;
import br.com.codeflix.catalog.admin.domain.castmember.CastMemberID;
import br.com.codeflix.catalog.admin.domain.category.CategoryGateway;
import br.com.codeflix.catalog.admin.domain.category.CategoryID;
import br.com.codeflix.catalog.admin.domain.exceptions.DomainException;
import br.com.codeflix.catalog.admin.domain.exceptions.InternalErrorException;
import br.com.codeflix.catalog.admin.domain.exceptions.NotFoundException;
import br.com.codeflix.catalog.admin.domain.exceptions.NotificationException;
import br.com.codeflix.catalog.admin.domain.genre.GenreGateway;
import br.com.codeflix.catalog.admin.domain.genre.GenreID;
import br.com.codeflix.catalog.admin.domain.validation.Error;
import br.com.codeflix.catalog.admin.domain.validation.ValidationHandler;
import br.com.codeflix.catalog.admin.domain.validation.handler.Notification;
import br.com.codeflix.catalog.admin.domain.video.*;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DefaultUpdateVideoUseCase extends UpdateVideoUseCase {

    private final VideoGateway videoGateway;
    private final CategoryGateway categoryGateway;
    private final CastMemberGateway castMemberGateway;
    private final GenreGateway genreGateway;
    private final MediaResourceGateway mediaResourceGateway;

    public DefaultUpdateVideoUseCase(
            final VideoGateway videoGateway,
            final CategoryGateway categoryGateway,
            final CastMemberGateway castMemberGateway,
            final GenreGateway genreGateway,
            final MediaResourceGateway mediaResourceGateway
    ) {
        this.videoGateway = Objects.requireNonNull(videoGateway);
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
        this.castMemberGateway = Objects.requireNonNull(castMemberGateway);
        this.genreGateway = Objects.requireNonNull(genreGateway);
        this.mediaResourceGateway = Objects.requireNonNull(mediaResourceGateway);
    }

    @Override
    public UpdateVideoOutput execute(final UpdateVideoCommand command) {
        final var id = VideoID.from(command.id());
        final var rating = Rating.of(command.rating()).orElse(null);
        final var launchYear = command.launchedAt() != null ? Year.of(command.launchedAt()) : null;
        final var categories = toIdentifier(command.categories(), CategoryID::from);
        final var genres = toIdentifier(command.genres(), GenreID::from);
        final var members = toIdentifier(command.members(), CastMemberID::from);

        final var video = this.videoGateway.findById(id)
                .orElseThrow(notFoundException(id));

        final var notification = Notification.create();
        notification.append(validateCategories(categories));
        notification.append(validateGenres(genres));
        notification.append(validateMembers(members));

        video.update(
                command.title(),
                command.description(),
                launchYear,
                command.duration(),
                command.opened(),
                command.published(),
                rating,
                categories,
                genres,
                members
        );

        video.validate(notification);

        if (notification.hasError()) {
            throw new NotificationException("Could not update Aggregate Video", notification);
        }

        return UpdateVideoOutput.from(update(command, video));
    }

    private Video update(final UpdateVideoCommand command, final Video video) {
        final var id = video.getId();

        try {
            final var videoMedia = command.getVideo()
                    .map(it -> this.mediaResourceGateway.storeAudioVideo(id, it))
                    .orElse(null);

            final var trailerMedia = command.getTrailer()
                    .map(it -> this.mediaResourceGateway.storeAudioVideo(id, it))
                    .orElse(null);

            final var bannerMedia = command.getBanner()
                    .map(it -> this.mediaResourceGateway.storeImage(id, it))
                    .orElse(null);

            final var thumbnailMedia = command.getThumbnail()
                    .map(it -> this.mediaResourceGateway.storeImage(id, it))
                    .orElse(null);

            final var thumbHalfMedia = command.getThumbnailHalf()
                    .map(it -> this.mediaResourceGateway.storeImage(id, it))
                    .orElse(null);

            return this.videoGateway.update(
                    video
                            .updateVideoMedia(videoMedia)
                            .updateTrailerMedia(trailerMedia)
                            .updateBannerMedia(bannerMedia)
                            .updateThumbnailMedia(thumbnailMedia)
                            .updateThumbnailHalfMedia(thumbHalfMedia)
            );
        } catch (final Throwable t) {
            throw InternalErrorException.with(
                    "An error on create video was observed [videoId:%s]".formatted(id.getValue()),
                    t
            );
        }
    }

    private Supplier<DomainException> notFoundException(final VideoID id) {
        return () -> NotFoundException.with(Video.class, id);
    }

    private ValidationHandler validateCategories(final Set<CategoryID> ids) {
        return validateAggregate("categories", ids, categoryGateway::existsByIds);
    }

    private ValidationHandler validateGenres(final Set<GenreID> ids) {
        return validateAggregate("genres", ids, genreGateway::existsByIds);
    }

    private ValidationHandler validateMembers(final Set<CastMemberID> ids) {
        return validateAggregate("cast members", ids, castMemberGateway::existsByIds);
    }

    private <T extends Identifier> ValidationHandler validateAggregate(
            final String aggregate,
            final Set<T> ids,
            final Function<Iterable<T>, List<T>> existsByIds
    ) {
        final var notification = Notification.create();
        if (ids == null || ids.isEmpty()) {
            return notification;
        }

        final var retrievedIds = existsByIds.apply(ids);

        if (ids.size() != retrievedIds.size()) {
            final var missingIds = new ArrayList<>(ids);
            missingIds.removeAll(retrievedIds);

            final var missingIdsMessage = missingIds.stream()
                    .map(Identifier::getValue)
                    .collect(Collectors.joining(", "));

            notification.append(new Error("Some %s could not be found: %s".formatted(aggregate, missingIdsMessage)));
        }

        return notification;
    }

    private <T> Set<T> toIdentifier(final Set<String> ids, final Function<String, T> mapper) {
        return ids.stream()
                .map(mapper)
                .collect(Collectors.toSet());
    }
}
