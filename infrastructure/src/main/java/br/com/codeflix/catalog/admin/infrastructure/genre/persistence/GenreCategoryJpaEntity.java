package br.com.codeflix.catalog.admin.infrastructure.genre.persistence;

import br.com.codeflix.catalog.admin.domain.category.CategoryID;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "genres_categories")
public class GenreCategoryJpaEntity {

    @EmbeddedId
    private GenreCategoryID id;

    @ManyToOne
    @MapsId("genreId")
    private GenreJpaEntity genre;

    public GenreCategoryJpaEntity() {}

    private GenreCategoryJpaEntity(final GenreJpaEntity genre, final CategoryID categoryId) {
        this.id = GenreCategoryID.from(genre.getId(), categoryId.getValue());
        this.genre = genre;
    }

    public static GenreCategoryJpaEntity from(final GenreJpaEntity genre, final CategoryID categoryId) {
        return new GenreCategoryJpaEntity(genre, categoryId);
    }

    public GenreCategoryID getId() {
        return id;
    }

    public void setId(GenreCategoryID id) {
        this.id = id;
    }

    public GenreJpaEntity getGenre() {
        return genre;
    }

    public void setGenre(GenreJpaEntity genre) {
        this.genre = genre;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        GenreCategoryJpaEntity that = (GenreCategoryJpaEntity) object;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
