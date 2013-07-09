package ru.it.lecm.reports.api;

import java.net.URL;

import org.alfresco.service.cmr.repository.NodeRef;

import ru.it.lecm.reports.api.model.ReportDescriptor;

/**
 * Биновый интерфейс для работы с шаблонами зарегистрированных отчётов.
 * @author rabdullin
 */
public interface ReportsManager {

	/**
	 * Получить описатеть отчёта по названию.
	 * Просматриваютс отчёты по порядку: 
	 *    1) зарегистрированные "свежие"
	 *    2) хранимые в виде ds-xml файлов
	 *    3) зарегенные в виде бинов
	 * @param reportMnemoName мнемонический код отчёта (уникальный)
	 * @return
	 */
	ReportDescriptor getRegisteredReportDescriptor(String reportMnemoName);


	/**
	 * Зарегистрировать указанный описатель отчёта. Создать ds-xml.
	 * @param rdesc
	 */
	void registerReportDescriptor(ReportDescriptor rdesc);


	/**
	 * Зарегистрировать отчёт, указав его id
	 * @param rdescId
	 */
	void registerReportDescriptor(NodeRef rdescId);


	/**
	 * Получить адрес ресурса с ds-xml файлом, который соот-ет отчёту
	 * @param reportCode
	 * @return
	 */
	URL getDsXmlResourceUrl(String reportCode);


	/**
	 * Загрузить данные ds-файла указанного шаблона
	 * @param reportCode
	 * @return
	 */
	byte[] loadDsXmlBytes(String reportCode);


	/**
	 * Вернуть каталог, в котором располагается шаблон указанного отчёта
	 * @param reportCode
	 * @return
	 */
	String getReportTemplateFileDir(String reportCode);


	/**
	 * Вернуть название ds-xml файла, в котором располагается -описание указанного отчёта
	 * @param reportCode
	 * @return
	 */
	String getDsRelativeFileName(String reportCode);
}
