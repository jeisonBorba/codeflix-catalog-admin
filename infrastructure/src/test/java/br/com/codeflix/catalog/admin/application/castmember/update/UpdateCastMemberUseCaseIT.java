package br.com.codeflix.catalog.admin.application.castmember.update;

import br.com.codeflix.catalog.admin.IntegrationTest;
import br.com.codeflix.catalog.admin.domain.castmember.CastMember;
import br.com.codeflix.catalog.admin.domain.castmember.CastMemberGateway;
import br.com.codeflix.catalog.admin.domain.castmember.CastMemberID;
import br.com.codeflix.catalog.admin.domain.castmember.CastMemberType;
import br.com.codeflix.catalog.admin.domain.exceptions.NotFoundException;
import br.com.codeflix.catalog.admin.domain.exceptions.NotificationException;
import br.com.codeflix.catalog.admin.infrastructure.castmember.persistence.CastMemberJpaEntity;
import br.com.codeflix.catalog.admin.infrastructure.castmember.persistence.CastMemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static br.com.codeflix.catalog.admin.Fixture.CastMember.type;
import static br.com.codeflix.catalog.admin.Fixture.name;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@IntegrationTest
public class UpdateCastMemberUseCaseIT {

    @Autowired
    private UpdateCastMemberUseCase useCase;

    @Autowired
    private CastMemberRepository castMemberRepository;

    @SpyBean
    private CastMemberGateway castMemberGateway;

    @Test
    public void givenAValidCommand_whenCallsUpdateCastMember_shouldReturnItsIdentifier() {
        final var member = CastMember.newMember("vin diesel", CastMemberType.DIRECTOR);

        this.castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(member));

        final var expectedId = member.getId();
        final var expectedName = name();
        final var expectedType = CastMemberType.ACTOR;

        final var command = UpdateCastMemberCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedType
        );
        
        final var actualOutput = useCase.execute(command);
        
        assertNotNull(actualOutput);
        assertEquals(expectedId.getValue(), actualOutput.id());

        final var actualPersistedMember = this.castMemberRepository.findById(expectedId.getValue()).get();

        assertEquals(expectedName, actualPersistedMember.getName());
        assertEquals(expectedType, actualPersistedMember.getType());
        assertEquals(member.getCreatedAt(), actualPersistedMember.getCreatedAt());
        assertTrue(member.getUpdatedAt().isBefore(actualPersistedMember.getUpdatedAt()));

        verify(castMemberGateway).findById(any());
        verify(castMemberGateway).update(any());
    }

    @Test
    public void givenAnInvalidName_whenCallsUpdateCastMember_shouldThrowsNotificationException() {
        final var member = CastMember.newMember("vin diesel", CastMemberType.DIRECTOR);

        this.castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(member));

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

        final var actualException = assertThrows(NotificationException.class, () -> {
            useCase.execute(command);
        });

        assertNotNull(actualException);

        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        verify(castMemberGateway).findById(any());
        verify(castMemberGateway, never()).update(any());
    }

    @Test
    public void givenAnInvalidType_whenCallsUpdateCastMember_shouldThrowsNotificationException() {
        final var member = CastMember.newMember("vin diesel", CastMemberType.DIRECTOR);

        this.castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(member));

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

        final var actualException = assertThrows(NotificationException.class, () -> {
            useCase.execute(command);
        });

        assertNotNull(actualException);

        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        verify(castMemberGateway).findById(any());
        verify(castMemberGateway, never()).update(any());
    }

    @Test
    public void givenAnInvalidId_whenCallsUpdateCastMember_shouldThrowsNotFoundException() {
        final var expectedId = CastMemberID.from("123");
        final var expectedName = name();
        final var expectedType = type();

        final var expectedErrorMessage = "CastMember with ID 123 was not found";

        final var command = UpdateCastMemberCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedType
        );

        final var actualException = assertThrows(NotFoundException.class, () -> {
            useCase.execute(command);
        });

        assertNotNull(actualException);

        assertEquals(expectedErrorMessage, actualException.getMessage());

        verify(castMemberGateway).findById(any());
        verify(castMemberGateway, never()).update(any());
    }
}
