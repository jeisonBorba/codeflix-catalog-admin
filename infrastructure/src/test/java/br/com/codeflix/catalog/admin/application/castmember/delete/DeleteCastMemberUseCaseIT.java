package br.com.codeflix.catalog.admin.application.castmember.delete;

import br.com.codeflix.catalog.admin.IntegrationTest;
import br.com.codeflix.catalog.admin.domain.castmember.CastMember;
import br.com.codeflix.catalog.admin.domain.castmember.CastMemberGateway;
import br.com.codeflix.catalog.admin.domain.castmember.CastMemberID;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@IntegrationTest
public class DeleteCastMemberUseCaseIT {

    @Autowired
    private DeleteCastMemberUseCase useCase;

    @Autowired
    private CastMemberRepository castMemberRepository;

    @SpyBean
    private CastMemberGateway castMemberGateway;

    @Test
    public void givenAValidId_whenCallsDeleteCastMember_shouldDeleteIt() {
        final var member = CastMember.newMember(name(), type());
        final var memberTwo = CastMember.newMember(name(), type());

        final var expectedId = member.getId();

        this.castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(member));
        this.castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(memberTwo));

        assertEquals(2, this.castMemberRepository.count());
        
        assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));
        
        verify(castMemberGateway).deleteById(eq(expectedId));

        assertEquals(1, this.castMemberRepository.count());
        assertFalse(this.castMemberRepository.existsById(expectedId.getValue()));
        assertTrue(this.castMemberRepository.existsById(memberTwo.getId().getValue()));
    }

    @Test
    public void givenAnInvalidId_whenCallsDeleteCastMember_shouldBeOk() {
        this.castMemberRepository.saveAndFlush(
                CastMemberJpaEntity.from(
                        CastMember.newMember(name(), type())
                )
        );

        final var expectedId = CastMemberID.from("123");

        assertEquals(1, this.castMemberRepository.count());

        assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        verify(castMemberGateway).deleteById(eq(expectedId));

        assertEquals(1, this.castMemberRepository.count());
    }

    @Test
    public void givenAValidId_whenCallsDeleteCastMemberAndGatewayThrowsException_shouldReceiveException() {
        final var member = CastMember.newMember(name(), type());

        this.castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(member));

        final var expectedId = member.getId();

        assertEquals(1, this.castMemberRepository.count());

        doThrow(new IllegalStateException("Gateway error")).when(castMemberGateway).deleteById(any());

        assertThrows(IllegalStateException.class, () -> useCase.execute(expectedId.getValue()));

        verify(castMemberGateway).deleteById(eq(expectedId));

        assertEquals(1, this.castMemberRepository.count());
    }
}