package ru.it.lecm.reports.api.model.share;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.reports.api.DsLoader;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.xml.DSXMLProducer;

/**
 * Класс для обеспечения загрузки ds-xml из share-модулей
 *
 * @author rabdullin
 *
 */
// singletone
public class ModelLoader implements DsLoader {

	static final transient Logger logger = LoggerFactory.getLogger(ModelLoader.class);

	private static DsLoader instance = null;

	// singletone
	private ModelLoader() {
	}

	public static DsLoader getInstance() {
		if (instance == null) {
			// logger.warn( "DsLoader bean not specified");
			instance = new ModelLoader();
		}
		return instance;
	}

	@Override
	public ReportDescriptor parseXml(InputStream dsXml, String streamName) {
		if (dsXml == null)
			return null;
		return DSXMLProducer.parseDSXML(dsXml, (streamName == null ? "ds-xml" : streamName));
	}

}
