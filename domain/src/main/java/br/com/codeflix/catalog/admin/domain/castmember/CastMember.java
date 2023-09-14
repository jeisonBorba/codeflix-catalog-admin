package br.com.codeflix.catalog.admin.domain.castmember;

import br.com.codeflix.catalog.admin.domain.AggregateRoot;
import br.com.codeflix.catalog.admin.domain.exceptions.NotificationException;
import br.com.codeflix.catalog.admin.domain.utils.InstantUtils;
import br.com.codeflix.catalog.admin.domain.validation.ValidationHandler;
import br.com.codeflix.catalog.admin.domain.validation.handler.Notification;

import java.time.Instant;

public class CastMember extends AggregateRoot<CastMemberID> {

    private String name;
    private CastMemberType type;
    private Instant createdAt;
    private Instant updatedAt;

    protected CastMember(
            final CastMemberID id,
            final String name,
            final CastMemberType type,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        super(id);
        this.name = name;
        this.type = type;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        selfValidate();
    }

    public static CastMember newMember(final String name, final CastMemberType type) {
        final var id = CastMemberID.unique();
        final var now = InstantUtils.now();
        return new CastMember(id, name, type, now, now);
    }

    public static CastMember with(
            final CastMemberID id,
            final String name,
            final CastMemberType type,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        return new CastMember(id, name, type, createdAt, updatedAt);
    }

    public static CastMember with(final CastMember member) {
        return new CastMember(
                member.id,
                member.name,
                member.type,
                member.createdAt,
                member.updatedAt
        );
    }

    public CastMember update(final String name, final CastMemberType type) {
        this.name = name;
        this.type = type;
        this.updatedAt = InstantUtils.now();
        selfValidate();
        return this;
    }

    @Override
    public void validate(final ValidationHandler handler) {
        new CastMemberValidator(this, handler).validate();
    }

    public String getName() {
        return name;
    }

    public CastMemberType getType() {
        return type;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    private void selfValidate() {
        final var notification = Notification.create();
        validate(notification);

        if (notification.hasError()) {
            throw new NotificationException("Failed to create an Aggregate CastMember", notification);
        }
    }
}
