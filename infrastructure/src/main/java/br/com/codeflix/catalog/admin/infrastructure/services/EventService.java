package br.com.codeflix.catalog.admin.infrastructure.services;

public interface EventService {

    void send(Object event);
}
