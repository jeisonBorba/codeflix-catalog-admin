package br.com.codeflix.catalog.admin.infrastructure.category;

import br.com.codeflix.catalog.admin.domain.category.Category;
import br.com.codeflix.catalog.admin.domain.category.CategoryGateway;
import br.com.codeflix.catalog.admin.domain.category.CategoryID;
import br.com.codeflix.catalog.admin.domain.category.CategorySearchQuery;
import br.com.codeflix.catalog.admin.domain.pagination.Pagination;
import br.com.codeflix.catalog.admin.infrastructure.category.persistence.CategoryJpaEntity;
import br.com.codeflix.catalog.admin.infrastructure.category.persistence.CategoryRepository;
import br.com.codeflix.catalog.admin.infrastructure.utils.SpecificationUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Objects;
import java.util.Optional;

import static br.com.codeflix.catalog.admin.infrastructure.utils.SpecificationUtils.like;
import static org.springframework.data.jpa.domain.Specification.where;

@Component
public class CategoryMySQLGateway implements CategoryGateway {

    private final CategoryRepository categoryRepository;

    public CategoryMySQLGateway(CategoryRepository categoryRepository) {
        this.categoryRepository = Objects.requireNonNull(categoryRepository);
    }

    @Override
    public Category create(final Category category) {
        return this.save(category);
    }

    @Override
    public void deleteById(final CategoryID id) {
        final var idValue = id.getValue();
        if (this.categoryRepository.existsById(idValue)) {
            this.categoryRepository.deleteById(idValue);
        }
    }

    @Override
    public Optional<Category> findById(final CategoryID id) {
        return this.categoryRepository.findById(id.getValue())
                .map(CategoryJpaEntity::toAggregate);
    }

    @Override
    public Category update(final Category category) {
        return this.save(category);
    }

    @Override
    public Pagination<Category> findAll(final CategorySearchQuery query) {
        final var page = PageRequest.of(
                query.page(),
                query.perPage(),
                Sort.by(Sort.Direction.fromString(query.direction()), query.sort())
        );

        final var specifications = Optional.ofNullable(query.terms())
                .filter(str -> !str.isBlank())
                .map(str -> {
                    final Specification<CategoryJpaEntity> nameLike = like("name", str);
                    final Specification<CategoryJpaEntity> descriptionLike = like("description", str);

                    return nameLike.or(descriptionLike);
                })
                .orElse(null);

        final var pageResult = this.categoryRepository.findAll(where(specifications), page);
        return new Pagination<>(
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.map(CategoryJpaEntity::toAggregate).toList()
        );
    }

    private Category save(final Category category) {
        return this.categoryRepository.save(CategoryJpaEntity.from(category)).toAggregate();
    }
}
