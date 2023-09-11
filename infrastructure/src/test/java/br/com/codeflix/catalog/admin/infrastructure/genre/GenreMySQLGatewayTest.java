package br.com.codeflix.catalog.admin.infrastructure.genre;

import br.com.codeflix.catalog.admin.MySQLGatewayTest;
import br.com.codeflix.catalog.admin.infrastructure.category.CategoryMySQLGateway;
import br.com.codeflix.catalog.admin.infrastructure.genre.persistence.GenreRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@MySQLGatewayTest
public class GenreMySQLGatewayTest {

    @Autowired
    private CategoryMySQLGateway categoryGateway;

    @Autowired
    private GenreMySQLGateway genreGateway;

    @Autowired
    private GenreRepository genreRepository;

    @Test
    public void testDependenciesInjected() {
        assertNotNull(categoryGateway);
        assertNotNull(genreGateway);
        assertNotNull(genreRepository);
    }
}
