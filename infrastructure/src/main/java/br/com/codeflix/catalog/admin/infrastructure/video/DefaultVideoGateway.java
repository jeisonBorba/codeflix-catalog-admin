package br.com.codeflix.catalog.admin.infrastructure.video;

import br.com.codeflix.catalog.admin.domain.Identifier;
import br.com.codeflix.catalog.admin.domain.pagination.Pagination;
import br.com.codeflix.catalog.admin.domain.video.*;
import br.com.codeflix.catalog.admin.infrastructure.configuration.annotations.VideoCreatedQueue;
import br.com.codeflix.catalog.admin.infrastructure.services.EventService;
import br.com.codeflix.catalog.admin.infrastructure.video.persistence.VideoJpaEntity;
import br.com.codeflix.catalog.admin.infrastructure.video.persistence.VideoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

import static br.com.codeflix.catalog.admin.domain.utils.CollectionUtils.mapTo;
import static br.com.codeflix.catalog.admin.domain.utils.CollectionUtils.nullIfEmpty;
import static br.com.codeflix.catalog.admin.infrastructure.utils.SqlUtils.like;
import static br.com.codeflix.catalog.admin.infrastructure.utils.SqlUtils.upper;

@Component
public class DefaultVideoGateway implements VideoGateway {

    private final VideoRepository videoRepository;
    private final EventService eventService;

    public DefaultVideoGateway(
            final VideoRepository videoRepository,
            @VideoCreatedQueue final EventService eventService) {
        this.videoRepository = Objects.requireNonNull(videoRepository);
        this.eventService = Objects.requireNonNull(eventService);
    }

    @Override
    @Transactional
    public Video create(final Video video) {
        return save(video);
    }

    @Override
    public void deleteById(final VideoID id) {
        final var videoId = id.getValue();
        if (this.videoRepository.existsById(videoId)) {
            this.videoRepository.deleteById(videoId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Video> findById(final VideoID id) {
        return this.videoRepository.findById(id.getValue())
                .map(VideoJpaEntity::toAggregate);
    }

    @Override
    @Transactional
    public Video update(final Video video) {
        return save(video);
    }

    @Override
    public Pagination<VideoPreview> findAll(final VideoSearchQuery query) {
        final var page = PageRequest.of(
                query.page(),
                query.perPage(),
                Sort.by(Sort.Direction.fromString(query.direction()), query.sort())
        );
        final var actualPage = this.videoRepository.findAll(
                like(upper(query.terms())),
                nullIfEmpty(mapTo(query.castMembers(), Identifier::getValue)),
                nullIfEmpty(mapTo(query.categories(), Identifier::getValue)),
                nullIfEmpty(mapTo(query.genres(), Identifier::getValue)),
                page
        );

        return new Pagination<>(
                actualPage.getNumber(),
                actualPage.getSize(),
                actualPage.getTotalElements(),
                actualPage.toList()
        );
    }

    private Video save(final Video video) {
        final var result = this.videoRepository.save(VideoJpaEntity.from(video))
                .toAggregate();

        video.publishDomainEvents(this.eventService::send);

        return result;
    }
}