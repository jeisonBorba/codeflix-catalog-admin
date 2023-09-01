package br.com.codeflix.catalog.admin.infrastructure.category;

import br.com.codeflix.catalog.admin.domain.category.Category;
import br.com.codeflix.catalog.admin.domain.category.CategoryGateway;
import br.com.codeflix.catalog.admin.domain.category.CategoryID;
import br.com.codeflix.catalog.admin.domain.category.CategorySearchQuery;
import br.com.codeflix.catalog.admin.domain.pagination.Pagination;
import br.com.codeflix.catalog.admin.infrastructure.category.persistence.CategoryJpaEntity;
import br.com.codeflix.catalog.admin.infrastructure.category.persistence.CategoryRepository;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
public class CategoryMySQLGateway implements CategoryGateway {

    private final CategoryRepository categoryRepository;

    public CategoryMySQLGateway(CategoryRepository categoryRepository) {
        this.categoryRepository = Objects.requireNonNull(categoryRepository);
    }

    @Override
    public Category create(final Category category) {
        return this.categoryRepository.save(CategoryJpaEntity.from(category)).toAggregate();
    }

    @Override
    public void deleteById(CategoryID id) {

    }

    @Override
    public Optional<Category> findById(CategoryID id) {
        return Optional.empty();
    }

    @Override
    public Category update(Category category) {
        return null;
    }

    @Override
    public Pagination<Category> findAll(CategorySearchQuery query) {
        return null;
    }
}
