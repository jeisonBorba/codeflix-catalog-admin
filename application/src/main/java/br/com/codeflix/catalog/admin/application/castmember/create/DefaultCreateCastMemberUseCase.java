package br.com.codeflix.catalog.admin.application.castmember.create;

import br.com.codeflix.catalog.admin.domain.castmember.CastMember;
import br.com.codeflix.catalog.admin.domain.castmember.CastMemberGateway;
import br.com.codeflix.catalog.admin.domain.exceptions.NotificationException;
import br.com.codeflix.catalog.admin.domain.validation.handler.Notification;

import java.util.Objects;

public non-sealed class DefaultCreateCastMemberUseCase extends CreateCastMemberUseCase {

    private final CastMemberGateway castMemberGateway;

    public DefaultCreateCastMemberUseCase(final CastMemberGateway castMemberGateway) {
        this.castMemberGateway = Objects.requireNonNull(castMemberGateway);
    }

    @Override
    public CreateCastMemberOutput execute(final CreateCastMemberCommand command) {
        final var name = command.name();
        final var type = command.type();

        final var notification = Notification.create();

        final var member = notification.validate(() -> CastMember.newMember(name, type));

        if (notification.hasError()) {
            notify(notification);
        }

        return CreateCastMemberOutput.from(this.castMemberGateway.create(member));
    }

    private void notify(Notification notification) {
        throw new NotificationException("Could not create Aggregate CastMember", notification);
    }
}
