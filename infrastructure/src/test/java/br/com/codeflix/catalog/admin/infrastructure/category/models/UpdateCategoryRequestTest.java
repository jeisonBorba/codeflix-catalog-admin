package br.com.codeflix.catalog.admin.infrastructure.category.models;

import br.com.codeflix.catalog.admin.JacksonTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JacksonTest
public class UpdateCategoryRequestTest {

    @Autowired
    private JacksonTester<UpdateCategoryRequest> json;

    @Test
    public void testUnmarshall() throws Exception {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;

        final var json = """
        {
            "name": "%s",
            "description": "%s",
            "is_active": %s
        }
        """.formatted(
                expectedName,
                expectedDescription,
                expectedIsActive
        );

        final var actualResponse = this.json.parse(json);

        assertThat(actualResponse)
                .hasFieldOrPropertyWithValue("name", expectedName)
                .hasFieldOrPropertyWithValue("description", expectedDescription)
                .hasFieldOrPropertyWithValue("active", expectedIsActive);
    }
}
