package ru.it.lecm.reports.api.model.share;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.reports.api.DsLoader;

/**
 * Класс для обеспечения загрузки ds-xml из share-модулей
 *
 * @author rabdullin
 *
 */
public class ModelLoader {

	static final transient Logger logger = LoggerFactory.getLogger(ModelLoader.class);

	public static DsLoader getInstance() {
		if (instance == null) {
			logger.warn( "DsLoader bean not specified");
		}
		return instance;
	}

	// @NOTE: чтобы не выносить в api-share кучу всякого добра из модельных 
	// классов, огранизуем такой статический бин
	public static void setInstance(DsLoader value) { // заполняется в bootstrap нужным бином
		instance = value;
	}

	private static DsLoader instance = null;

}
