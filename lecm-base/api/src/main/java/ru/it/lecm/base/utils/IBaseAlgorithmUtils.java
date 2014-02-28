package ru.it.lecm.base.utils;

/**
 *
 * @author snovikov
 */
public interface IBaseAlgorithmUtils {
	public <T,E> T getDocumentsOnStatusAmmount(AtomicWork<T> worker, E context);
}
