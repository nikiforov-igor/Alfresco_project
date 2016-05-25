package ru.it.lecm.notifications.beans;

/**
 * Created by dkuchurkin on 25.05.2016.
 */

public class TemplateNotFoundException extends Exception {

    public TemplateNotFoundException() {
    }

    public TemplateNotFoundException(String message) {
        super(message);
    }

    public TemplateNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public TemplateNotFoundException(Throwable cause) {
        super(cause);
    }
}

