package ru.it.lecm.base.utils;

/**
 *
 * @author snovikov
 * @param <T> 
 */
public interface AtomicWork<T> {
	public <E> T execute(E Context);
}
