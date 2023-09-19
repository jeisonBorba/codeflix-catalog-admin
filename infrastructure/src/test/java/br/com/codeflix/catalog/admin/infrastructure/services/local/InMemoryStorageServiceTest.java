package br.com.codeflix.catalog.admin.infrastructure.services.local;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static br.com.codeflix.catalog.admin.domain.category.Fixture.Videos.resource;
import static br.com.codeflix.catalog.admin.domain.video.VideoMediaType.THUMBNAIL;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryStorageServiceTest {

    private InMemoryStorageService inMemoryStorageService = new InMemoryStorageService();

    @BeforeEach
    public void setUp() {
        inMemoryStorageService.clear();
    }

    @Test
    public void givenValidResource_whenCallsStore_shouldStoreIt() {
        final var expectedResource = resource(THUMBNAIL);
        final var expectedId = "item";

        inMemoryStorageService.store(expectedId, expectedResource);

        final var actualContent = this.inMemoryStorageService.storage().get(expectedId);

        assertEquals(expectedResource, actualContent);
    }

    @Test
    public void givenResource_whenCallsGet_shouldRetrieveIt() {
        final var expectedResource = resource(THUMBNAIL);
        final var expectedId = "item";

        this.inMemoryStorageService.storage().put(expectedId, expectedResource);

        final var actualContent = inMemoryStorageService.get(expectedId).get();

        assertEquals(expectedResource, actualContent);
    }

    @Test
    public void givenInvalidResource_whenCallsGet_shouldRetrieveEmpty() {
        final var expectedResource = resource(THUMBNAIL);
        final var expectedId = "11122";

        this.inMemoryStorageService.storage().put("item", expectedResource);

        final var actualContent = inMemoryStorageService.get(expectedId);

        assertTrue(actualContent.isEmpty());
    }

    @Test
    public void givenPrefix_whenCallsList_shouldRetrieveAll() {
        final var expectedResource = resource(THUMBNAIL);

        final var expectedIds = List.of("item1", "item2");

        this.inMemoryStorageService.storage().put("item1", expectedResource);
        this.inMemoryStorageService.storage().put("item2", expectedResource);

        final var actualContent = inMemoryStorageService.list("it");

        assertTrue(
                expectedIds.size() == actualContent.size()
                        && expectedIds.containsAll(actualContent)
        );
    }

    @Test
    public void givenResource_whenCallsDeleteAll_shouldEmptyStorage() {
        final var expectedResource = resource(THUMBNAIL);

        final var expectedIds = List.of("item1", "item2");

        this.inMemoryStorageService.storage().put("item1", expectedResource);
        this.inMemoryStorageService.storage().put("item2", expectedResource);

        inMemoryStorageService.deleteAll(expectedIds);

        assertTrue(this.inMemoryStorageService.storage().isEmpty());
    }
}