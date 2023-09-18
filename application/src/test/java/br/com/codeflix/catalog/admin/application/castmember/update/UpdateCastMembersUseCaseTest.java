package br.com.codeflix.catalog.admin.application.castmember.update;

import br.com.codeflix.catalog.admin.application.UseCaseTest;
import br.com.codeflix.catalog.admin.domain.castmember.CastMember;
import br.com.codeflix.catalog.admin.domain.castmember.CastMemberGateway;
import br.com.codeflix.catalog.admin.domain.castmember.CastMemberID;
import br.com.codeflix.catalog.admin.domain.castmember.CastMemberType;
import br.com.codeflix.catalog.admin.domain.category.Fixture;
import br.com.codeflix.catalog.admin.domain.category.Fixture.CastMembers;
import br.com.codeflix.catalog.admin.domain.exceptions.NotFoundException;
import br.com.codeflix.catalog.admin.domain.exceptions.NotificationException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static br.com.codeflix.catalog.admin.domain.category.Fixture.name;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class UpdateCastMembersUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultUpdateCastMemberUseCase useCase;

    @Mock
    private CastMemberGateway castMemberGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(castMemberGateway);
    }

    @Test
    public void givenAValidCommand_whenCallsUpdateCastMember_shouldReturnItsIdentifier() {
        final var member = CastMember.newMember("vin diesel", CastMemberType.DIRECTOR);

        final var expectedId = member.getId();
        final var expectedName = name();
        final var expectedType = CastMemberType.ACTOR;

        final var command = UpdateCastMemberCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedType
        );

        when(castMemberGateway.findById(any())).thenReturn(Optional.of(CastMember.with(member)));

        when(castMemberGateway.update(any())).thenAnswer(returnsFirstArg());

        final var actualOutput = useCase.execute(command);

        assertNotNull(actualOutput);
        assertEquals(expectedId.getValue(), actualOutput.id());

        verify(castMemberGateway).findById(eq(expectedId));

        verify(castMemberGateway).update(argThat(aUpdatedMember ->
                Objects.equals(expectedId, aUpdatedMember.getId())
                        && Objects.equals(expectedName, aUpdatedMember.getName())
                        && Objects.equals(expectedType, aUpdatedMember.getType())
                        && Objects.equals(member.getCreatedAt(), aUpdatedMember.getCreatedAt())
                        && member.getUpdatedAt().isBefore(aUpdatedMember.getUpdatedAt())
        ));
    }

    @Test
    public void givenAInvalidName_whenCallsUpdateCastMember_shouldThrowsNotificationException() {
        final var member = CastMember.newMember("vin diesel", CastMemberType.DIRECTOR);

        final var expectedId = member.getId();
        final String expectedName = null;
        final var expectedType = CastMemberType.ACTOR;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        final var command = UpdateCastMemberCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedType
        );

        when(castMemberGateway.findById(any())).thenReturn(Optional.of(member));

        final var actualException = assertThrows(NotificationException.class, () -> {
            useCase.execute(command);
        });

        assertNotNull(actualException);

        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        verify(castMemberGateway).findById(eq(expectedId));
        verify(castMemberGateway, never()).update(any());
    }

    @Test
    public void givenAnInvalidType_whenCallsUpdateCastMember_shouldThrowsNotificationException() {
        final var member = CastMember.newMember("vin diesel", CastMemberType.DIRECTOR);

        final var expectedId = member.getId();
        final var expectedName = name();
        final CastMemberType expectedType = null;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'type' should not be null";

        final var command = UpdateCastMemberCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedType
        );

        when(castMemberGateway.findById(any())).thenReturn(Optional.of(member));

        final var actualException = assertThrows(NotificationException.class, () -> {
            useCase.execute(command);
        });

        assertNotNull(actualException);

        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        verify(castMemberGateway).findById(eq(expectedId));
        verify(castMemberGateway, times(0)).update(any());
    }

    @Test
    public void givenAnInvalidId_whenCallsUpdateCastMember_shouldThrowsNotFoundException() {
        final var member = CastMember.newMember("vin diesel", CastMemberType.DIRECTOR);

        final var expectedId = CastMemberID.from("123");
        final var expectedName = name();
        final var expectedType = CastMembers.type();

        final var expectedErrorMessage = "CastMember with ID 123 was not found";

        final var command = UpdateCastMemberCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedType
        );

        when(castMemberGateway.findById(any())).thenReturn(Optional.empty());

        final var actualException = assertThrows(NotFoundException.class, () -> {
            useCase.execute(command);
        });
        
        assertNotNull(actualException);

        assertEquals(expectedErrorMessage, actualException.getMessage());

        verify(castMemberGateway).findById(eq(expectedId));
        verify(castMemberGateway, times(0)).update(any());
    }
}
