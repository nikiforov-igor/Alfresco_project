package ru.it.lecm.reports.api;

import java.io.InputStream;

import ru.it.lecm.reports.api.model.ReportDescriptor;

public interface DsLoader {

	/**
	 * Загрузить модельные данные из указанного потока XML
	 * @param dsXml
	 * @param streamName название потока (для информации при ошибках)
	 * @return
	 */
	ReportDescriptor parseXml(InputStream dsXml, String streamName);

	/**
	 * Получить название ds-xml файла, соот-щее указанному отчёту
	 * @param reportCode код отчёта
	 * @return
	 */
	// String getDsFileName( String reportCode);
}
