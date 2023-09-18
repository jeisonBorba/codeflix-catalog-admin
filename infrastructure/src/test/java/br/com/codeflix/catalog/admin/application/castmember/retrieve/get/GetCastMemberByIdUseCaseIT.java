package br.com.codeflix.catalog.admin.application.castmember.retrieve.get;

import br.com.codeflix.catalog.admin.IntegrationTest;
import br.com.codeflix.catalog.admin.domain.castmember.CastMember;
import br.com.codeflix.catalog.admin.domain.castmember.CastMemberGateway;
import br.com.codeflix.catalog.admin.domain.castmember.CastMemberID;
import br.com.codeflix.catalog.admin.domain.exceptions.NotFoundException;
import br.com.codeflix.catalog.admin.infrastructure.castmember.persistence.CastMemberJpaEntity;
import br.com.codeflix.catalog.admin.infrastructure.castmember.persistence.CastMemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static br.com.codeflix.catalog.admin.domain.category.Fixture.CastMembers.type;
import static br.com.codeflix.catalog.admin.domain.category.Fixture.name;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@IntegrationTest
public class GetCastMemberByIdUseCaseIT {

    @Autowired
    private GetCastMemberByIdUseCase useCase;

    @Autowired
    private CastMemberRepository castMemberRepository;

    @SpyBean
    private CastMemberGateway castMemberGateway;

    @Test
    public void givenAValidId_whenCallsGetCastMember_shouldReturnIt() {
        final var expectedName = name();
        final var expectedType = type();

        final var member = CastMember.newMember(expectedName, expectedType);

        final var expectedId = member.getId();

        this.castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(member));

        assertEquals(1, this.castMemberRepository.count());

        final var actualOutput = useCase.execute(expectedId.getValue());

        assertNotNull(actualOutput);
        assertEquals(expectedId.getValue(), actualOutput.id());
        assertEquals(expectedName, actualOutput.name());
        assertEquals(expectedType, actualOutput.type());
        assertEquals(member.getCreatedAt(), actualOutput.createdAt());
        assertEquals(member.getUpdatedAt(), actualOutput.updatedAt());

        verify(castMemberGateway).findById(any());
    }

    @Test
    public void givenAInvalidId_whenCallsGetCastMemberAndDoesNotExists_shouldReturnNotFoundException() {
        final var expectedId = CastMemberID.from("123");

        final var expectedErrorMessage = "CastMember with ID 123 was not found";

        final var actualOutput = assertThrows(NotFoundException.class, () -> {
            useCase.execute(expectedId.getValue());
        });

        assertNotNull(actualOutput);
        assertEquals(expectedErrorMessage, actualOutput.getMessage());

        verify(castMemberGateway).findById(eq(expectedId));
    }
}
