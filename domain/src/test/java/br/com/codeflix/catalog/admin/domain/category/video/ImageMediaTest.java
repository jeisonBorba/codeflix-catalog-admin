package br.com.codeflix.catalog.admin.domain.category.video;

import br.com.codeflix.catalog.admin.domain.video.ImageMedia;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ImageMediaTest {

    @Test
    public void givenValidParams_whenCallsNewImage_ShouldReturnInstance() {
        final var expectedChecksum = "abc";
        final var expectedName = "Banner.png";
        final var expectedLocation = "/images/ac";

        final var actualImage = ImageMedia.with(expectedChecksum, expectedName, expectedLocation);

        assertNotNull(actualImage);
        assertEquals(expectedChecksum, actualImage.checksum());
        assertEquals(expectedName, actualImage.name());
        assertEquals(expectedLocation, actualImage.location());
    }

    @Test
    public void givenTwoImagesWithSameChecksumAndLocation_whenCallsEquals_ShouldReturnTrue() {
        final var expectedChecksum = "abc";
        final var expectedLocation = "/images/ac";

        final var img1 = ImageMedia.with(expectedChecksum, "Random", expectedLocation);

        final var img2 = ImageMedia.with(expectedChecksum, "Simple", expectedLocation);

        assertEquals(img1, img2);
        assertNotSame(img1, img2);
    }

    @Test
    public void givenInvalidParams_whenCallsWith_ShouldReturnError() {
        assertThrows(
                NullPointerException.class,
                () -> ImageMedia.with(null, "Random", "/images")
        );

        assertThrows(
                NullPointerException.class,
                () -> ImageMedia.with("abc", null, "/images")
        );

        assertThrows(
                NullPointerException.class,
                () -> ImageMedia.with("abc", "Random", null)
        );
    }
}
