package br.com.codeflix.catalog.admin.infrastructure.configuration.usecases;

import br.com.codeflix.catalog.admin.application.video.create.CreateVideoUseCase;
import br.com.codeflix.catalog.admin.application.video.create.DefaultCreateVideoUseCase;
import br.com.codeflix.catalog.admin.application.video.delete.DefaultDeleteVideoUseCase;
import br.com.codeflix.catalog.admin.application.video.delete.DeleteVideoUseCase;
import br.com.codeflix.catalog.admin.application.video.retrieve.get.DefaultGetVideoByIdUseCase;
import br.com.codeflix.catalog.admin.application.video.retrieve.get.GetVideoByIdUseCase;
import br.com.codeflix.catalog.admin.application.video.retrieve.list.DefaultListVideoUseCase;
import br.com.codeflix.catalog.admin.application.video.retrieve.list.ListVideoUseCase;
import br.com.codeflix.catalog.admin.application.video.update.DefaultUpdateVideoUseCase;
import br.com.codeflix.catalog.admin.application.video.update.UpdateVideoUseCase;
import br.com.codeflix.catalog.admin.domain.castmember.CastMemberGateway;
import br.com.codeflix.catalog.admin.domain.category.CategoryGateway;
import br.com.codeflix.catalog.admin.domain.genre.GenreGateway;
import br.com.codeflix.catalog.admin.domain.video.MediaResourceGateway;
import br.com.codeflix.catalog.admin.domain.video.VideoGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class VideoUseCaseConfig {

    private final CategoryGateway categoryGateway;
    private final CastMemberGateway castMemberGateway;
    private final GenreGateway genreGateway;
    private final MediaResourceGateway mediaResourceGateway;
    private final VideoGateway videoGateway;

    public VideoUseCaseConfig(
            final CategoryGateway categoryGateway,
            final CastMemberGateway castMemberGateway,
            final GenreGateway genreGateway,
            final MediaResourceGateway mediaResourceGateway,
            final VideoGateway videoGateway
    ) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
        this.castMemberGateway = Objects.requireNonNull(castMemberGateway);
        this.genreGateway = Objects.requireNonNull(genreGateway);
        this.mediaResourceGateway = Objects.requireNonNull(mediaResourceGateway);
        this.videoGateway = Objects.requireNonNull(videoGateway);
    }

    @Bean
    public CreateVideoUseCase createVideoUseCase() {
        return new DefaultCreateVideoUseCase(categoryGateway, castMemberGateway, genreGateway, mediaResourceGateway, videoGateway);
    }

    @Bean
    public UpdateVideoUseCase updateVideoUseCase() {
        return new DefaultUpdateVideoUseCase(videoGateway, categoryGateway, castMemberGateway, genreGateway, mediaResourceGateway);
    }

    @Bean
    public GetVideoByIdUseCase getVideoByIdUseCase() {
        return new DefaultGetVideoByIdUseCase(videoGateway);
    }

    @Bean
    public DeleteVideoUseCase deleteVideoUseCase() {
        return new DefaultDeleteVideoUseCase(videoGateway);
    }

    @Bean
    public ListVideoUseCase listVideosUseCase() {
        return new DefaultListVideoUseCase(videoGateway);
    }

}
