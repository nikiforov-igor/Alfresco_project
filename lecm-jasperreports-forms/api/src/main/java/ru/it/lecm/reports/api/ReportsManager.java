package ru.it.lecm.reports.api;

import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.reports.api.model.DAO.ReportContentDAO;
import ru.it.lecm.reports.api.model.DAO.ReportEditorDAO;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.api.model.ReportFileData;
import ru.it.lecm.reports.model.impl.ReportDefaultsDesc;
import ru.it.lecm.reports.model.impl.ReportType;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
	 */
	void registerReportDescriptor(ReportDescriptor rdesc);


	/**
	 * Зарегистрировать отчёт, созданный редактором отчётов ("lecm-reports-editor"), указав его id.
	 * Доступ к данным будет выполняться через reportDAO.
	 */
	void registerReportDescriptor(NodeRef rdescId);

	/**
	 * Обратная к registerReportDescriptor.
	 * Если отчёт стандартный из поставки, то он становится недоступен только до 
	 * следующей перезагрузки приложения.
	 */
	void unregisterReportDescriptor(String reportCode);


	/**
	 * Загрузить данные ds-файла указанного шаблонаn
	 */
	byte[] loadDsXmlBytes(String reportCode);


	/**
	 * Сформировать шаблон по-умолчанию для НД указанного описателя отчёта
	 */
	NodeRef produceDefaultTemplate(NodeRef reportRef);

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

	ReportEditorDAO getReportEditorDAO();

	/**
	 * Вернуть хранилище, которое содержит указанный описатель или NULL
	 */
	ReportContentDAO findContentDAO(ReportDescriptor reportDesc);

	/**
	 * Список умочаний для указанного типа отчёта
	 * @return не NULL список [key=ReportType.Mnem -> value={ file_Extension + template_of_template}]
	 */
    ReportDefaultsDesc getReportDefaultsDesc(ReportType rtype);


	/**
	 * Содать отчёт
	 * @param reportCode код отчёта
	 * @param args параметры
	 * @throws IOException 
	 */
	ReportFileData generateReport( final String reportCode, Map<String, String> args) throws IOException;

	/**
	 * Сохранить данные в указанной папке репозитория.
	 * Сохраняет как обычный дочерний "cm:content" c именем и содержанием файла.
	 * Mime-тип будет определяться автоматом по расширению файла, если srcData.mimeType == null.
	 * @return созданный id узла
	 */
	NodeRef storeAsContent(ReportFileData srcData, NodeRef destParentRef);
}
