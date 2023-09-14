package br.com.codeflix.catalog.admin.domain.category.video;

import br.com.codeflix.catalog.admin.domain.video.AudioVideoMedia;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AudioVideoMediaTest {

    @Test
    public void givenValidParams_whenCallsNewAudioVideo_ShouldReturnInstance() {
        final var expectedChecksum = "abc";
        final var expectedName = "Banner.png";
        final var expectedRawLocation = "/images/ac";

        final var actualVideo = AudioVideoMedia.with(expectedChecksum, expectedName, expectedRawLocation);

        assertNotNull(actualVideo);
        assertEquals(expectedChecksum, actualVideo.checksum());
        assertEquals(expectedName, actualVideo.name());
        assertEquals(expectedRawLocation, actualVideo.rawLocation());
    }

    @Test
    public void givenTwoVideosWithSameChecksumAndLocation_whenCallsEquals_ShouldReturnTrue() {
        final var expectedChecksum = "abc";
        final var expectedRawLocation = "/images/ac";

        final var img1 = AudioVideoMedia.with(expectedChecksum, "Random", expectedRawLocation);

        final var img2 = AudioVideoMedia.with(expectedChecksum, "Simple", expectedRawLocation);

        assertEquals(img1, img2);
        assertNotSame(img1, img2);
    }

    @Test
    public void givenInvalidParams_whenCallsWith_ShouldReturnError() {
        assertThrows(
                NullPointerException.class,
                () -> AudioVideoMedia.with(null, "Random", "/videos")
        );

        assertThrows(
                NullPointerException.class,
                () -> AudioVideoMedia.with("abc", null, "/videos")
        );

        assertThrows(
                NullPointerException.class,
                () -> AudioVideoMedia.with("abc", "Random", null)
        );
    }
}
