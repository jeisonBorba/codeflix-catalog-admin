package br.com.codeflix.catalog.admin.domain.exceptions;

import br.com.codeflix.catalog.admin.domain.validation.handler.Notification;


public class NotificationException extends DomainException {

    public NotificationException(final String message, final Notification notification) {
        super(message, notification.getErrors());
    }
}
