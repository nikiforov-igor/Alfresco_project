package ru.it.lecm.base.beans;

import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport.TxnReadState;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.transaction.TransactionService;

/**
 *
 * @author vkuprin
 */
public class LecmTransactionHelperImpl implements LecmTransactionHelper {

    private TransactionService transactionService;

    public TransactionService getTransactionService() {
        return transactionService;
    }

    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Check if rw transaction exists
     */
    @Override
    public void checkTransaction() throws TransactionNeededException, WriteTransactionNeededException {
        checkTransaction(false);
    }

    /**
     * Check if transaction exists
     * @param ro - type of transaction
     * @throws TransactionNeededException
     */
    @Override
    public void checkTransaction(boolean ro) throws TransactionNeededException, WriteTransactionNeededException {
        TxnReadState readState = AlfrescoTransactionSupport.getTransactionReadState();
        switch (readState) {
            case TXN_NONE:
                throw new TransactionNeededException();
            case TXN_READ_ONLY:
                if (!ro) {
                    throw new WriteTransactionNeededException();
                }
                break;
            case TXN_READ_WRITE:
                break;
            default:
                throw new RuntimeException("Unknown transaction state: " + readState);
        }
    }


    /**
     *
     * Execute a callback in a transaction until it succeeds, fails
     * because of an error not the result of an optimistic locking failure,
     * or a deadlock loser failure, or until a maximum number of retries have
     * been attempted.
     * <p>
     *
     * Uses RetryingTransactionHelper.
     * Try execute in existing transaction. If it is not exists or current transaction
     * is RO but RW transaction needed starts new transaction.
     *
     * @param cb                The callback containing the unit of work.
     * @param readOnly          Whether this is a read only transaction.

     * @return                  Returns the result of the unit of work.
     * @throws                  RuntimeException  all checked exceptions are converted
     */
    @Override
    public <R> R doInTransaction(RetryingTransactionHelper.RetryingTransactionCallback<R> cb, boolean readOnly) {
//        try {
//            checkTransaction(readOnly);
//        } catch (WriteTransactionNeededException ex) {
//            return transactionService.getRetryingTransactionHelper().doInTransaction(cb, false, true);
//        } catch (TransactionNeededException ex1) {
            return transactionService.getRetryingTransactionHelper().doInTransaction(cb, readOnly);
//        }
//        try {
//            return cb.execute();
//        } catch (Throwable ex) {
//            throw new RuntimeException(ex);
//        }
    }

    @Override
    public <R> R doInRWTransaction(RetryingTransactionHelper.RetryingTransactionCallback<R> cb) {
        return doInTransaction(cb, false);
    }

    

}
