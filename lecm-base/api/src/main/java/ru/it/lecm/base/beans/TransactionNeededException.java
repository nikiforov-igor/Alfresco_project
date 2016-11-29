
package ru.it.lecm.base.beans;

/**
 *
 * @author vkuprin
 */
public class TransactionNeededException extends RuntimeException {

    public TransactionNeededException() {
    }

    public TransactionNeededException(String message) {
        super(message);
    }

    public TransactionNeededException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransactionNeededException(Throwable cause) {
        super(cause);
    }
    
}
