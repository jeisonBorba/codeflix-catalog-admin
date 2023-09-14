package br.com.codeflix.catalog.admin.infrastructure.configuration.usecases;

import br.com.codeflix.catalog.admin.application.castmember.create.CreateCastMemberUseCase;
import br.com.codeflix.catalog.admin.application.castmember.create.DefaultCreateCastMemberUseCase;
import br.com.codeflix.catalog.admin.application.castmember.delete.DefaultDeleteCastMemberUseCase;
import br.com.codeflix.catalog.admin.application.castmember.delete.DeleteCastMemberUseCase;
import br.com.codeflix.catalog.admin.application.castmember.retrieve.get.DefaultGetCastMemberByIdUseCase;
import br.com.codeflix.catalog.admin.application.castmember.retrieve.get.GetCastMemberByIdUseCase;
import br.com.codeflix.catalog.admin.application.castmember.retrieve.list.DefaultListCastMembersUseCase;
import br.com.codeflix.catalog.admin.application.castmember.retrieve.list.ListCastMembersUseCase;
import br.com.codeflix.catalog.admin.application.castmember.update.DefaultUpdateCastMemberUseCase;
import br.com.codeflix.catalog.admin.application.castmember.update.UpdateCastMemberUseCase;
import br.com.codeflix.catalog.admin.domain.castmember.CastMemberGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class CastMemberUseCaseConfig {

    private final CastMemberGateway castMemberGateway;

    public CastMemberUseCaseConfig(final CastMemberGateway castMemberGateway) {
        this.castMemberGateway = Objects.requireNonNull(castMemberGateway);
    }

    @Bean
    public CreateCastMemberUseCase createCastMemberUseCase() {
        return new DefaultCreateCastMemberUseCase(castMemberGateway);
    }

    @Bean
    public DeleteCastMemberUseCase deleteCastMemberUseCase() {
        return new DefaultDeleteCastMemberUseCase(castMemberGateway);
    }

    @Bean
    public GetCastMemberByIdUseCase getCastMemberByIdUseCase() {
        return new DefaultGetCastMemberByIdUseCase(castMemberGateway);
    }

    @Bean
    public ListCastMembersUseCase listCastMembersUseCase() {
        return new DefaultListCastMembersUseCase(castMemberGateway);
    }

    @Bean
    public UpdateCastMemberUseCase updateCastMemberUseCase() {
        return new DefaultUpdateCastMemberUseCase(castMemberGateway);
    }
}
