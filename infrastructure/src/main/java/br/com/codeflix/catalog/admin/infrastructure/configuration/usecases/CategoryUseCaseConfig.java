package br.com.codeflix.catalog.admin.infrastructure.configuration.usecases;

import br.com.codeflix.catalog.admin.application.category.create.CreateCategoryUseCase;
import br.com.codeflix.catalog.admin.application.category.create.DefaultCreateCategoryUseCase;
import br.com.codeflix.catalog.admin.application.category.delete.DefaultDeleteCategoryUseCase;
import br.com.codeflix.catalog.admin.application.category.delete.DeleteCategoryUseCase;
import br.com.codeflix.catalog.admin.application.category.retrieve.get.DefaultGetCategoryByIdUseCase;
import br.com.codeflix.catalog.admin.application.category.retrieve.get.GetCategoryByIdUseCase;
import br.com.codeflix.catalog.admin.application.category.retrieve.list.DefaultListCategoryUseCase;
import br.com.codeflix.catalog.admin.application.category.retrieve.list.ListCategoryUseCase;
import br.com.codeflix.catalog.admin.application.category.update.DefaultUpdateCategoryUseCase;
import br.com.codeflix.catalog.admin.application.category.update.UpdateCategoryUseCase;
import br.com.codeflix.catalog.admin.domain.category.CategoryGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CategoryUseCaseConfig {

    private final CategoryGateway categoryGateway;

    public CategoryUseCaseConfig(CategoryGateway categoryGateway) {
        this.categoryGateway = categoryGateway;
    }

    @Bean
    public CreateCategoryUseCase createCategoryUseCase() {
        return new DefaultCreateCategoryUseCase(categoryGateway);
    }

    @Bean
    public UpdateCategoryUseCase updateCategoryUseCase() {
        return new DefaultUpdateCategoryUseCase(categoryGateway);
    }

    @Bean
    public DeleteCategoryUseCase deleteCategoryUseCase() {
        return new DefaultDeleteCategoryUseCase(categoryGateway);
    }

    @Bean
    public GetCategoryByIdUseCase getCategoryByIdUseCase() {
        return new DefaultGetCategoryByIdUseCase(categoryGateway);
    }

    @Bean
    public ListCategoryUseCase listCategoryUseCase() {
        return new DefaultListCategoryUseCase(categoryGateway);
    }
}
