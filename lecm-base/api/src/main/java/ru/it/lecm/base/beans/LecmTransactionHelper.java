
package ru.it.lecm.base.beans;

import org.alfresco.repo.transaction.RetryingTransactionHelper;

/**
 *
 * @author vkuprin
 */
public interface LecmTransactionHelper {

    public void checkTransaction(boolean ro) throws TransactionNeededException, WriteTransactionNeededException;
    public void checkTransaction() throws TransactionNeededException, WriteTransactionNeededException;
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
    public <R> R doInTransaction(RetryingTransactionHelper.RetryingTransactionCallback<R> cb, boolean readOnly);
    
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
     * is RO starts new transaction.
     *
     * @param cb                The callback containing the unit of work.
     * @return                  Returns the result of the unit of work.
     * @throws                  RuntimeException  all checked exceptions are converted
     */
    public <R> R doInRWTransaction(RetryingTransactionHelper.RetryingTransactionCallback<R> cb);
	
	/**
	 * Метод использует RetryingTransactionHelper для выполнения переданного 
	 * callback'а в условной транзакции. Создание транзакции зависит от флага 
	 * createTxIfNeeded - если он установлен в true, то транзакция будет создана
	 * в случае необходимости, иначе - метод вернёт null
	 * 
	 * Данный метод следует использовать только для отладки и выявления мест, 
	 * которые потенциально требуют транзакции, но принудительное её 
	 * использование может повлечь блокировку
	 */
	public <R> R doInNotGuaranteedTransaction(RetryingTransactionHelper.RetryingTransactionCallback<R> cb, boolean readonly);

}
