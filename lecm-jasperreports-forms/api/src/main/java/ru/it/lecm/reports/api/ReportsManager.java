package ru.it.lecm.reports.api;

import java.net.URL;
import java.util.List;
import java.util.Map;

import org.alfresco.service.cmr.repository.NodeRef;

import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.api.model.ReportType;
import ru.it.lecm.reports.api.model.DAO.ReportDAO;

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
	 * Получить список зарегистрированных редакторов отчётов для указанного типа
	 * документов и тип отчёта
	 * @param docType тип документов или null, если для любых типов док-ов
	 * @param reportType тип отчёта (Jasper, OOffice и т.п.) или null, если для всех типов
	 * @return список зарегеных отчётов (отчёты с типом документов null, воз-ся
	 * при любом состоянии параметра docType)
	 */
	List<ReportDescriptor> getRegisteredReports(String docType, String reportType);

	/**
	 * Получить список зарегистрированных редакторов отчётов для указанного типа
	 * документов и тип отчёта
	 * @param docTypes массив типов документов  или null, если для любых типов
	 * @param forCollection возвращать отчеты для коллекции или нет?
	 * @return список зарегеных отчётов
	 */
	List<ReportDescriptor> getRegisteredReports(String[] docTypes, boolean forCollection);

	/**
	 * Получить список зарегистрированных  отчётов
	 * @return список зарегеных отчётов
	 */
	List<ReportDescriptor> getRegisteredReports();

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
	 * Обратная к registerReportDescriptor
	 * @param reportCode
	 */
	void unregisterReportDescriptor(String reportCode);


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
	 * Сформировать jrxml по-умолчанию для НД указанного описания отчёта
	 * @param reportDesc
	 * @return
	 */
	byte[] produceDefaultTemplate(ReportDescriptor reportDesc);

	/**
	 * Вернуть каталог, в котором располагается шаблоны отчётов указанного типа
	 * @param reportType
	 * @return
	 */
	String getReportTemplateFileDir(ReportType reportType);

	/**
	 * Вернуть название ds-xml файла, в котором располагается -описание указанного отчёта
	 * @param reportCode
	 * @return
	 */
	String getDsRelativeFileName(String reportCode);

	/**
	 * @return не NULL список [ReportTypeMnemonic -> ReportGenerator]
	 */
	Map</*ReportType.Code*/String, ReportGenerator> getReportGenerators();

	/**
	 * Задать соот-вие типов отчётов и их провайдеров
	 * @param map список [ReportTypeMnemonic -> ReportGenerator]
	 */
	void setReportGenerators(Map<String, ReportGenerator> map);

	/**
	 * Получить строковое не NULL название
	 * @param rtype
	 * @return при rtype != null воз-ся rtype.code, иначе значение по-умолчанию данного менеджера
	 */
	String getReportTypeTag(ReportType rtype);

	ReportDAO getReportDAO();
}
