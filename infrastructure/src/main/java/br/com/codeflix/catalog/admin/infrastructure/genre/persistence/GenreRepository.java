package br.com.codeflix.catalog.admin.infrastructure.genre.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<GenreJpaEntity, String> {
}
