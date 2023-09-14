package br.com.codeflix.catalog.admin.application.castmember.update;

import br.com.codeflix.catalog.admin.domain.Identifier;
import br.com.codeflix.catalog.admin.domain.castmember.CastMember;
import br.com.codeflix.catalog.admin.domain.castmember.CastMemberGateway;
import br.com.codeflix.catalog.admin.domain.castmember.CastMemberID;
import br.com.codeflix.catalog.admin.domain.exceptions.NotFoundException;
import br.com.codeflix.catalog.admin.domain.exceptions.NotificationException;
import br.com.codeflix.catalog.admin.domain.validation.handler.Notification;

import java.util.Objects;
import java.util.function.Supplier;

public non-sealed class DefaultUpdateCastMemberUseCase extends UpdateCastMemberUseCase {

    private final CastMemberGateway castMemberGateway;

    public DefaultUpdateCastMemberUseCase(final CastMemberGateway castMemberGateway) {
        this.castMemberGateway = Objects.requireNonNull(castMemberGateway);
    }

    @Override
    public UpdateCastMemberOutput execute(final UpdateCastMemberCommand command) {
        final var id = CastMemberID.from(command.id());
        final var name = command.name();
        final var type = command.type();

        final var member = this.castMemberGateway.findById(id)
                .orElseThrow(notFound(id));

        final var notification = Notification.create();
        notification.validate(() -> member.update(name, type));

        if (notification.hasError()) {
            notify(id, notification);
        }

        return UpdateCastMemberOutput.from(this.castMemberGateway.update(member));
    }

    private void notify(final Identifier id, final Notification notification) {
        throw new NotificationException("Could not update Aggregate CastMember %s".formatted(id.getValue()), notification);
    }

    private Supplier<NotFoundException> notFound(final CastMemberID id) {
        return () -> NotFoundException.with(CastMember.class, id);
    }
}
