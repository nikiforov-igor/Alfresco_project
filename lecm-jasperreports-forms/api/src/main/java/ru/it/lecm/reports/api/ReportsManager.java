package ru.it.lecm.reports.api;

import java.util.List;
import java.util.Map;

import org.alfresco.service.cmr.repository.NodeRef;

import ru.it.lecm.reports.api.model.ReportDefaultsDesc;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.api.model.ReportType;
import ru.it.lecm.reports.api.model.DAO.ReportContentDAO;
import ru.it.lecm.reports.api.model.DAO.ReportEditorDAO;

/**
 * Биновый интерфейс для работы с шаблонами зарегистрированных отчётов.
 * @author rabdullin
 */
public interface ReportsManager {

	/**
	 * Получить описатеть отчёта по названию.
	 * Просматриваются отчёты по порядку: 
	 *    1) зарегистрированные "свежие"
	 *    2) хранимые в виде ds-xml файлов (поставка)
	 *    3) зарегенные в виде бинов (сконфигурированные spring-beans)
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
	 * Получить список всех зарегистрированных отчётов
	 * @return список зарегеных отчётов
	 */
	List<ReportDescriptor> getRegisteredReports();

	/**
	 * Зарегистрировать указанный описатель отчёта. Создать ds-xml.
	 * @param rdesc
	 */
	void registerReportDescriptor(ReportDescriptor rdesc);


	/**
	 * Зарегистрировать отчёт, созданный редактором отчётов ("lecm-reports-editor"), указав его id.
	 * Доступ к данным будет выполняться через reportDAO.
	 * @param rdescId
	 */
	void registerReportDescriptor(NodeRef rdescId);

	/**
	 * Обратная к registerReportDescriptor.
	 * Если отчёт стандартный из поставки, то он становится недоступен только до 
	 * следующей перезагрузки приложения.
	 * @param reportCode
	 */
	void unregisterReportDescriptor(String reportCode);


	/**
	 * Загрузить данные ds-файла указанного шаблона
	 * @param reportCode
	 * @return
	 */
	byte[] loadDsXmlBytes(String reportCode);


	/**
	 * Сформировать шаблон по-умолчанию для НД указанного описателя отчёта
	 * @param reportDesc
	 * @return
	 */
	byte[] produceDefaultTemplate(ReportDescriptor reportDesc);

	/**
	 * Вернуть каталог, в котором располагается шаблоны отчётов указанного типа
	 * @param reportType
	 * @return
	 */
	// String getReportTemplateFileDir(ReportType reportType);

	/**
	 * Вернуть название ds-xml файла, в котором располагается -описание указанного отчёта
	 * @param reportCode
	 * @return
	 */
	// String getDsRelativeFileName(String reportCode);

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
	 * Получить строковое не NULL название указанного типа отчёта
	 * @param rtype тип отчёта, допустимо NULL
	 * @return при rtype != null воз-ся rtype.code, иначе значение по-умолчанию для данного менеджера
	 */
	String getReportTypeTag(ReportType rtype);

	ReportEditorDAO getReportDAO();

	/**
	 * Вернуть хранилище, которое содержит указанный описатель или NULL
	 * @param reportDesc
	 * @return
	 */
	ReportContentDAO findContentDAO(ReportDescriptor reportDesc);

	/**
	 * Список умочаний для указанного типа отчёта
	 * @return не NULL список [key=ReportType.Mnem -> value={ file_Extension + template_of_template}]
	 */
	ReportDefaultsDesc getReportDefaultsDesc(ReportType rtype);
}
