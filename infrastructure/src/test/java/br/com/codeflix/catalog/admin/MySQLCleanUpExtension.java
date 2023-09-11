package br.com.codeflix.catalog.admin;

import br.com.codeflix.catalog.admin.infrastructure.category.persistence.CategoryRepository;
import br.com.codeflix.catalog.admin.infrastructure.genre.persistence.GenreRepository;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collection;
import java.util.List;

public class MySQLCleanUpExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) {
        final var appContext = SpringExtension.getApplicationContext(context);

        cleanUp(List.of(
                appContext.getBean(GenreRepository.class),
                appContext.getBean(CategoryRepository.class)
        ));

        final var entityManager = appContext.getBean(TestEntityManager.class);
        entityManager.flush();
        entityManager.clear();
    }

    private void cleanUp(final Collection<CrudRepository> repositories) {
        repositories.forEach(CrudRepository::deleteAll);
    }

}
