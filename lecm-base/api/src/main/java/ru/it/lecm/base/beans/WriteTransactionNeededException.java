
package ru.it.lecm.base.beans;

/**
 *
 * @author vkuprin
 */
public class WriteTransactionNeededException extends TransactionNeededException {

    public WriteTransactionNeededException() {
    }

    public WriteTransactionNeededException(String message) {
        super(message);
    }

    public WriteTransactionNeededException(String message, Throwable cause) {
        super(message, cause);
    }

    public WriteTransactionNeededException(Throwable cause) {
        super(cause);
    }
    
}
