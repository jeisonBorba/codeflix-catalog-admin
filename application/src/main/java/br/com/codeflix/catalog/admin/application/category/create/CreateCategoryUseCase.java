package br.com.codeflix.catalog.admin.application.category.create;

import br.com.codeflix.catalog.admin.application.UseCase;
import br.com.codeflix.catalog.admin.domain.validation.handler.Notification;
import io.vavr.control.Either;

public abstract class CreateCategoryUseCase extends UseCase<CreateCategoryCommand, Either<Notification, CreateCategoryOutput>> {
}
