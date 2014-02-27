package ru.it.lecm.base.utils;

import sun.net.www.content.text.Generic;

/**
 *
 * @author snovikov
 */
public interface AtomicWork<T> {
	public T execute();
}
