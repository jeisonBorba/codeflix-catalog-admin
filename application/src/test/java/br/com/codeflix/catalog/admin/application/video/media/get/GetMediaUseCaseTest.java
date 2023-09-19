package br.com.codeflix.catalog.admin.application.video.media.get;

import br.com.codeflix.catalog.admin.application.UseCaseTest;
import br.com.codeflix.catalog.admin.domain.exceptions.NotFoundException;
import br.com.codeflix.catalog.admin.domain.video.MediaResourceGateway;
import br.com.codeflix.catalog.admin.domain.video.VideoID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static br.com.codeflix.catalog.admin.domain.category.Fixture.Videos.mediaType;
import static br.com.codeflix.catalog.admin.domain.category.Fixture.Videos.resource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class GetMediaUseCaseTest extends UseCaseTest {
    @InjectMocks
    private DefaultGetMediaUseCase useCase;

    @Mock
    private MediaResourceGateway mediaResourceGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(mediaResourceGateway);
    }

    @Test
    public void givenVideoIdAndType_whenIsValidCmd_shouldReturnResource() {
        final var expectedId = VideoID.unique();
        final var expectedType = mediaType();
        final var expectedResource = resource(expectedType);

        when(mediaResourceGateway.getResource(expectedId, expectedType))
                .thenReturn(Optional.of(expectedResource));

        final var command = GetMediaCommand.with(expectedId.getValue(), expectedType.name());

        final var actualResult = this.useCase.execute(command);

        assertEquals(expectedResource.name(), actualResult.name());
        assertEquals(expectedResource.content(), actualResult.content());
        assertEquals(expectedResource.contentType(), actualResult.contentType());
    }

    @Test
    public void givenVideoIdAndType_whenIsNotFound_shouldReturnNotFoundException() {
        final var expectedId = VideoID.unique();
        final var expectedType = mediaType();

        when(mediaResourceGateway.getResource(expectedId, expectedType))
                .thenReturn(Optional.empty());

        final var command = GetMediaCommand.with(expectedId.getValue(), expectedType.name());

        Assertions.assertThrows(NotFoundException.class, () -> {
            this.useCase.execute(command);
        });
    }

    @Test
    public void givenVideoIdAndType_whenTypeDoesntExists_shouldReturnNotFoundException() {
        final var expectedId = VideoID.unique();
        final var expectedErrorMessage = "Media type QUALQUER doesn't exists";

        final var command = GetMediaCommand.with(expectedId.getValue(), "QUALQUER");

        final var actualException = Assertions.assertThrows(NotFoundException.class, () -> {
            this.useCase.execute(command);
        });

        assertEquals(expectedErrorMessage, actualException.getMessage());
    }
}
