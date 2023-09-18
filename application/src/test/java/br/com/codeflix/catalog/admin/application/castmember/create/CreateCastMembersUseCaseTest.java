package br.com.codeflix.catalog.admin.application.castmember.create;

import br.com.codeflix.catalog.admin.application.UseCaseTest;
import br.com.codeflix.catalog.admin.domain.castmember.CastMemberGateway;
import br.com.codeflix.catalog.admin.domain.castmember.CastMemberType;
import br.com.codeflix.catalog.admin.domain.exceptions.NotificationException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Objects;

import static br.com.codeflix.catalog.admin.domain.category.Fixture.CastMembers.type;
import static br.com.codeflix.catalog.admin.domain.category.Fixture.name;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

public class CreateCastMembersUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultCreateCastMemberUseCase useCase;

    @Mock
    private CastMemberGateway castMemberGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(castMemberGateway);
    }

    @Test
    public void givenAValidCommand_whenCallsCreateCastMember_shouldReturnIt() {
        final var expectedName = name();
        final var expectedType = type();

        final var command = CreateCastMemberCommand.with(expectedName, expectedType);

        when(castMemberGateway.create(any())).thenAnswer(returnsFirstArg());

        final var actualOutput = useCase.execute(command);

        assertNotNull(actualOutput);
        assertNotNull(actualOutput.id());

        verify(castMemberGateway).create(argThat(aMember ->
                Objects.nonNull(aMember.getId())
                        && Objects.equals(expectedName, aMember.getName())
                        && Objects.equals(expectedType, aMember.getType())
                        && Objects.nonNull(aMember.getCreatedAt())
                        && Objects.nonNull(aMember.getUpdatedAt())
        ));
    }

    @Test
    public void givenAInvalidName_whenCallsCreateCastMember_shouldThrowsNotificationException() {
        final String expectedName = null;
        final var expectedType = type();

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        final var command = CreateCastMemberCommand.with(expectedName, expectedType);

        final var actualException = assertThrows(NotificationException.class, () -> {
            useCase.execute(command);
        });

        assertNotNull(actualException);
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        verify(castMemberGateway, never()).create(any());
    }

    @Test
    public void givenAInvalidType_whenCallsCreateCastMember_shouldThrowsNotificationException() {
        final var expectedName = name();
        final CastMemberType expectedType = null;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'type' should not be null";

        final var command = CreateCastMemberCommand.with(expectedName, expectedType);

        final var actualException = assertThrows(NotificationException.class, () -> {
            useCase.execute(command);
        });

        assertNotNull(actualException);
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        verify(castMemberGateway, times(0)).create(any());
    }
}