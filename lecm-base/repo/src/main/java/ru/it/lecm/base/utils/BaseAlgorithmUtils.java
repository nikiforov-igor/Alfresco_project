package ru.it.lecm.base.utils;

/**
 *
 * @author snovikov
 */
public class BaseAlgorithmUtils implements IBaseAlgorithmUtils {

	@Override
	public <T> T getDocumentsOnStatusAmmount(AtomicWork<T> worker){
		return worker.execute();
	}

}
