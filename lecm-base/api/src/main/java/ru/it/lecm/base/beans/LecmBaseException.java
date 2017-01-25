package ru.it.lecm.base.beans;

/**
 * User: pmelnikov
 * Date: 03.04.14
 * Time: 11:15
 */
public class LecmBaseException extends RuntimeException {

    public LecmBaseException() {
        super();
    }

    public LecmBaseException(String message) {
        super(message);
    }

    public LecmBaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public LecmBaseException(Throwable cause) {
        super(cause);
    }
    
}
