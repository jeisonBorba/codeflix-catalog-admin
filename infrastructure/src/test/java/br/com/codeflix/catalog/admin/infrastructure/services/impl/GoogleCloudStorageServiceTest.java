package br.com.codeflix.catalog.admin.infrastructure.services.impl;

import br.com.codeflix.catalog.admin.domain.resource.Resource;
import br.com.codeflix.catalog.admin.domain.video.VideoMediaType;
import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static br.com.codeflix.catalog.admin.domain.category.Fixture.Videos.resource;
import static br.com.codeflix.catalog.admin.domain.video.VideoMediaType.THUMBNAIL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class GoogleCloudStorageServiceTest {

    private GoogleCloudStorageService googleCloudStorageService;

    private Storage storage;

    private String bucket = "test";

    @BeforeEach
    public void setUp() {
        this.storage = mock(Storage.class);
        this.googleCloudStorageService = new GoogleCloudStorageService(bucket, storage);
    }

    @Test
    public void givenValidResource_whenCallsStore_shouldStoreIt() {
        final var expectedResource = resource(THUMBNAIL);
        final var expectedId = expectedResource.name();

        final Blob blob = mockBlob(expectedResource);
        doReturn(blob).when(storage).get(eq(bucket), eq(expectedId));

        this.googleCloudStorageService.store(expectedId, expectedResource);

        final var captured = ArgumentCaptor.forClass(BlobInfo.class);

        verify(storage, times(1)).create(captured.capture(), eq(expectedResource.content()));

        final var actualBlob = captured.getValue();
        assertEquals(this.bucket, actualBlob.getBlobId().getBucket());
        assertEquals(expectedId, actualBlob.getBlobId().getName());
        assertEquals(expectedResource.contentType(), actualBlob.getContentType());
        assertEquals(expectedResource.checksum(), actualBlob.getCrc32cToHexString());
    }

    @Test
    public void givenResource_whenCallsGet_shouldRetrieveIt() {
        final var expectedResource = resource(THUMBNAIL);
        final var expectedId = expectedResource.name();

        final Blob blob = mockBlob(expectedResource);
        doReturn(blob).when(storage).get(eq(bucket), eq(expectedId));

        final var actualContent = googleCloudStorageService.get(expectedId).get();

        assertEquals(expectedResource.checksum(), actualContent.checksum());
        assertEquals(expectedResource.name(), actualContent.name());
        assertEquals(expectedResource.content(), actualContent.content());
        assertEquals(expectedResource.contentType(), actualContent.contentType());
    }

    @Test
    public void givenInvalidResource_whenCallsGet_shouldRetrieveEmpty() {
        final var expectedResource = resource(THUMBNAIL);
        final var expectedId = expectedResource.name();

        doReturn(null).when(storage).get(eq(bucket), eq(expectedId));

        final var actualContent = googleCloudStorageService.get(expectedId);

        assertTrue(actualContent.isEmpty());
    }

    @Test
    public void givenPrefix_whenCallsList_shouldRetrieveAll() {
        final var video = resource(VideoMediaType.VIDEO);
        final var banner = resource(VideoMediaType.BANNER);
        final var expectedIds = List.of(video.name(), banner.name());

        final var page = mock(Page.class);

        final Blob blob1 = mockBlob(video);
        final Blob blob2 = mockBlob(banner);

        doReturn(List.of(blob1, blob2)).when(page).iterateAll();
        doReturn(page).when(storage).list(eq(bucket), eq(Storage.BlobListOption.prefix("it")));

        final var actualContent = googleCloudStorageService.list("it");

        assertTrue(
                expectedIds.size() == actualContent.size()
                        && expectedIds.containsAll(actualContent)
        );
    }

    @Test
    public void givenResource_whenCallsDeleteAll_shouldEmptyStorage() {
        final var expectedIds = List.of("item1", "item2");

        googleCloudStorageService.deleteAll(expectedIds);

        final var captured = ArgumentCaptor.forClass(List.class);

        verify(storage).delete(captured.capture());

        final var actualIds = ((List<BlobId>) captured.getValue()).stream()
                .map(BlobId::getName)
                .toList();

        assertTrue(expectedIds.size() == actualIds.size() && actualIds.containsAll(expectedIds));
    }

    private Blob mockBlob(final Resource resource) {
        final var blob1 = mock(Blob.class);
        when(blob1.getBlobId()).thenReturn(BlobId.of(bucket, resource.name()));
        when(blob1.getCrc32cToHexString()).thenReturn(resource.checksum());
        when(blob1.getContent()).thenReturn(resource.content());
        when(blob1.getContentType()).thenReturn(resource.contentType());
        when(blob1.getName()).thenReturn(resource.name());
        return blob1;
    }

}