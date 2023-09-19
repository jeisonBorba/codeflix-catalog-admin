package br.com.codeflix.catalog.admin.domain.category;

import br.com.codeflix.catalog.admin.domain.Entity;
import br.com.codeflix.catalog.admin.domain.Identifier;
import br.com.codeflix.catalog.admin.domain.event.DomainEvent;
import br.com.codeflix.catalog.admin.domain.utils.IdUtils;
import br.com.codeflix.catalog.admin.domain.utils.InstantUtils;
import br.com.codeflix.catalog.admin.domain.validation.ValidationHandler;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class EntityTest extends UnitTest {

    @Test
    public void givenNullAsEvents_whenInstantiate_shouldBeOk() {
        final List<DomainEvent> events = null;

        final var entity = new DummyEntity(new DummyID(), events);

        assertNotNull(entity.getDomainEvents());
        assertTrue(entity.getDomainEvents().isEmpty());
    }

    @Test
    public void givenDomainEvents_whenPassInConstructor_shouldCreateADefensiveClone() {
        final List<DomainEvent> events = new ArrayList<>();
        events.add(new DummyEvent());

        final var entity = new DummyEntity(new DummyID(), events);

        assertNotNull(entity.getDomainEvents());
        assertEquals(1, entity.getDomainEvents().size());

        assertThrows(RuntimeException.class, () -> {
            final var actualEvents = entity.getDomainEvents();
            actualEvents.add(new DummyEvent());
        });
    }

    @Test
    public void givenEmptyDomainEvents_whenCallsRegisterEvent_shouldAddEventToList() {
        final var expectedEvents = 1;
        final var entity = new DummyEntity(new DummyID(), new ArrayList<>());

        entity.registerEvent(new DummyEvent());

        assertNotNull(entity.getDomainEvents());
        assertEquals(expectedEvents, entity.getDomainEvents().size());
    }

    @Test
    public void givenAFewDomainEvents_whenCallsPublishEvents_shouldCallPublisherAndClearTheList() {
        final var expectedEvents = 0;
        final var expectedSentEvents = 2;
        final var counter = new AtomicInteger(0);
        final var entity = new DummyEntity(new DummyID(), new ArrayList<>());

        entity.registerEvent(new DummyEvent());
        entity.registerEvent(new DummyEvent());

        assertEquals(2, entity.getDomainEvents().size());

        entity.publishDomainEvents(event -> {
            counter.incrementAndGet();
        });

        assertNotNull(entity.getDomainEvents());
        assertEquals(expectedEvents, entity.getDomainEvents().size());
        assertEquals(expectedSentEvents, counter.get());
    }

    public static class DummyEvent implements DomainEvent {

        @Override
        public Instant occurredOn() {
            return InstantUtils.now();
        }
    }

    public static class DummyID extends Identifier {

        private final String id;

        public DummyID() {
            this.id = IdUtils.uuid();
        }

        @Override
        public String getValue() {
            return this.id;
        }
    }

    public static class DummyEntity extends Entity<DummyID> {

        public DummyEntity(DummyID dummyID, List<DomainEvent> domainEvents) {
            super(dummyID, domainEvents);
        }

        @Override
        public void validate(ValidationHandler handler) {

        }
    }
}