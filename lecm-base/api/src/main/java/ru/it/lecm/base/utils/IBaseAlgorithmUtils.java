package ru.it.lecm.base.utils;

import ru.it.lecm.base.utils.AtomicWork;
import sun.net.www.content.text.Generic;

/**
 *
 * @author snovikov
 */
public interface IBaseAlgorithmUtils {
	public <T> T getDocumentsOnStatusAmmount(AtomicWork<T> worker);
}
