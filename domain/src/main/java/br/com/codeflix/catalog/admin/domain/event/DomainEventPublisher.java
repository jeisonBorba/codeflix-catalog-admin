package br.com.codeflix.catalog.admin.domain.event;

@FunctionalInterface
public interface DomainEventPublisher {

     void publishEvent(DomainEvent event);
}
