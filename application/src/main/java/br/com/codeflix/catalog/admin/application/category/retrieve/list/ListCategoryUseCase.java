package br.com.codeflix.catalog.admin.application.category.retrieve.list;

import br.com.codeflix.catalog.admin.application.UseCase;
import br.com.codeflix.catalog.admin.domain.category.CategorySearchQuery;
import br.com.codeflix.catalog.admin.domain.pagination.Pagination;

public abstract class ListCategoryUseCase extends UseCase<CategorySearchQuery, Pagination<CategoryListOutput>> {
}
