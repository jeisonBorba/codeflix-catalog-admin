package br.com.codeflix.catalog.admin.infrastructure.genre;

import br.com.codeflix.catalog.admin.domain.genre.Genre;
import br.com.codeflix.catalog.admin.domain.genre.GenreGateway;
import br.com.codeflix.catalog.admin.domain.genre.GenreID;
import br.com.codeflix.catalog.admin.domain.pagination.Pagination;
import br.com.codeflix.catalog.admin.domain.pagination.SearchQuery;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GenreMySQLGateway implements GenreGateway {

    @Override
    public Genre create(Genre genre) {
        return null;
    }

    @Override
    public void deleteById(GenreID id) {

    }

    @Override
    public Optional<Genre> findById(GenreID id) {
        return Optional.empty();
    }

    @Override
    public Genre update(Genre genre) {
        return null;
    }

    @Override
    public Pagination<Genre> findAll(SearchQuery query) {
        return null;
    }
}
