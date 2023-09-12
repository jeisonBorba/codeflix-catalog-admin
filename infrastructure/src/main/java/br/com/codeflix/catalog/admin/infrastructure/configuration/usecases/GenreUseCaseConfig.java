package br.com.codeflix.catalog.admin.infrastructure.configuration.usecases;

import br.com.codeflix.catalog.admin.application.genre.create.CreateGenreUseCase;
import br.com.codeflix.catalog.admin.application.genre.create.DefaultCreateGenreUseCase;
import br.com.codeflix.catalog.admin.application.genre.delete.DefaultDeleteGenreUseCase;
import br.com.codeflix.catalog.admin.application.genre.delete.DeleteGenreUseCase;
import br.com.codeflix.catalog.admin.application.genre.retrieve.get.DefaultGetGenreByIdUseCase;
import br.com.codeflix.catalog.admin.application.genre.retrieve.get.GetGenreByIdUseCase;
import br.com.codeflix.catalog.admin.application.genre.retrieve.list.DefaultListGenreUseCase;
import br.com.codeflix.catalog.admin.application.genre.retrieve.list.ListGenreUseCase;
import br.com.codeflix.catalog.admin.application.genre.update.DefaultUpdateGenreUseCase;
import br.com.codeflix.catalog.admin.application.genre.update.UpdateGenreUseCase;
import br.com.codeflix.catalog.admin.domain.category.CategoryGateway;
import br.com.codeflix.catalog.admin.domain.genre.GenreGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class GenreUseCaseConfig {

    private final CategoryGateway categoryGateway;
    private final GenreGateway genreGateway;

    public GenreUseCaseConfig(final CategoryGateway categoryGateway, final GenreGateway genreGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
        this.genreGateway = Objects.requireNonNull(genreGateway);
    }

    @Bean
    public CreateGenreUseCase createGenreUseCase() {
        return new DefaultCreateGenreUseCase(categoryGateway, genreGateway);
    }

    @Bean
    public UpdateGenreUseCase updateGenreUseCase() {
        return new DefaultUpdateGenreUseCase(categoryGateway, genreGateway);
    }

    @Bean
    public DeleteGenreUseCase deleteGenreUseCase() {
        return new DefaultDeleteGenreUseCase(genreGateway);
    }

    @Bean
    public GetGenreByIdUseCase getGenreByIdUseCase() {
        return new DefaultGetGenreByIdUseCase(genreGateway);
    }

    @Bean
    public ListGenreUseCase listGenreUseCase() {
        return new DefaultListGenreUseCase(genreGateway);
    }
}
