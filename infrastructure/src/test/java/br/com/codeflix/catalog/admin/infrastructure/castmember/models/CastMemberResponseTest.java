package br.com.codeflix.catalog.admin.infrastructure.castmember.models;

import br.com.codeflix.catalog.admin.JacksonTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import java.time.Instant;

import static br.com.codeflix.catalog.admin.domain.category.Fixture.CastMembers.type;
import static br.com.codeflix.catalog.admin.domain.category.Fixture.name;
import static org.assertj.core.api.Assertions.assertThat;

@JacksonTest
class CastMemberResponseTest {

    @Autowired
    private JacksonTester<CastMemberResponse> json;

    @Test
    public void testMarshall() throws Exception {
        final var expectedId = "123";
        final var expectedName = name();
        final var expectedType = type();
        final var expectedCreatedAt = Instant.now();
        final var expectedUpdatedAt = Instant.now();

        final var response = new CastMemberResponse(
                expectedId,
                expectedName,
                expectedType,
                expectedCreatedAt,
                expectedUpdatedAt
        );

        final var actualJson = this.json.write(response);

        assertThat(actualJson)
                .hasJsonPathValue("$.id", expectedId)
                .hasJsonPathValue("$.name", expectedName)
                .hasJsonPathValue("$.type", expectedType)
                .hasJsonPathValue("$.created_at", expectedCreatedAt)
                .hasJsonPathValue("$.updated_at", expectedUpdatedAt);
    }
}