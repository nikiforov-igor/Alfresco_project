package ru.it.lecm.reports.beans;

import java.io.InputStream;

import ru.it.lecm.reports.api.DsLoader;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.api.model.share.ModelLoader;
import ru.it.lecm.reports.xml.DSXMLProducer;

public class DsLoaderImpl implements DsLoader {

	public DsLoaderImpl() {
		ModelLoader.setInstance(this); // присвоение share-службе
	}

	@Override
	public ReportDescriptor parseXml(InputStream dsXml, String streamName) {
		if (dsXml == null)
			return null;
		return DSXMLProducer.parseDSXML(dsXml, (streamName == null ? "ds-xml" : streamName));
	}
}
