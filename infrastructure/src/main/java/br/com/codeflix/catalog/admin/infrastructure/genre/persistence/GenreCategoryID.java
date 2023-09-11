package br.com.codeflix.catalog.admin.infrastructure.genre.persistence;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class GenreCategoryID implements Serializable {

    @Column(name = "genre_id", nullable = false)
    private String genreId;

    @Column(name = "category_id", nullable = false)
    private String categoryId;

    public GenreCategoryID() {}

    private GenreCategoryID(final String genreId, final String categoryId) {
        this.genreId = genreId;
        this.categoryId = categoryId;
    }

    public static GenreCategoryID from(final String genreId, final String categoryId) {
        return new GenreCategoryID(genreId, categoryId);
    }

    public String getGenreId() {
        return genreId;
    }

    public void setGenreId(String genreId) {
        this.genreId = genreId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        GenreCategoryID that = (GenreCategoryID) object;
        return Objects.equals(getGenreId(), that.getGenreId()) && Objects.equals(getCategoryId(), that.getCategoryId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGenreId(), getCategoryId());
    }
}
