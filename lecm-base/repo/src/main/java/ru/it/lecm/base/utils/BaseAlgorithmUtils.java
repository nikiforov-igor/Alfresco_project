package ru.it.lecm.base.utils;

import java.util.List;
import sun.net.www.content.text.Generic;

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
