package br.com.codeflix.catalog.admin.application.castmember.retrieve.get;

import br.com.codeflix.catalog.admin.application.Fixture;
import br.com.codeflix.catalog.admin.application.UseCaseTest;
import br.com.codeflix.catalog.admin.domain.castmember.CastMember;
import br.com.codeflix.catalog.admin.domain.castmember.CastMemberGateway;
import br.com.codeflix.catalog.admin.domain.castmember.CastMemberID;
import br.com.codeflix.catalog.admin.domain.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetCastMemberByIdUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultGetCastMemberByIdUseCase useCase;

    @Mock
    private CastMemberGateway castMemberGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(castMemberGateway);
    }

    @Test
    public void givenAValidId_whenCallsGetCastMember_shouldReturnIt() {
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMember.type();

        final var member = CastMember.newMember(expectedName, expectedType);

        final var expectedId = member.getId();

        when(castMemberGateway.findById(any())).thenReturn(Optional.of(member));

        final var actualOutput = useCase.execute(expectedId.getValue());

        assertNotNull(actualOutput);
        assertEquals(expectedId.getValue(), actualOutput.id());
        assertEquals(expectedName, actualOutput.name());
        assertEquals(expectedType, actualOutput.type());
        assertEquals(member.getCreatedAt(), actualOutput.createdAt());
        assertEquals(member.getUpdatedAt(), actualOutput.updatedAt());

        verify(castMemberGateway).findById(eq(expectedId));
    }

    @Test
    public void givenAnInvalidId_whenCallsGetCastMemberAndDoesNotExists_shouldReturnNotFoundException() {
        final var expectedId = CastMemberID.from("123");

        final var expectedErrorMessage = "CastMember with ID 123 was not found";

        when(castMemberGateway.findById(any())).thenReturn(Optional.empty());

        final var actualOutput = assertThrows(NotFoundException.class, () -> {
            useCase.execute(expectedId.getValue());
        });

        assertNotNull(actualOutput);
        assertEquals(expectedErrorMessage, actualOutput.getMessage());

        verify(castMemberGateway).findById(eq(expectedId));
    }
}
