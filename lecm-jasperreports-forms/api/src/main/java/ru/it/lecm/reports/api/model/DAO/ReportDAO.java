package ru.it.lecm.reports.api.model.DAO;

import org.alfresco.service.cmr.repository.NodeRef;

import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.api.model.ReportTemplate;

/**
 * Служба получения дексрипторов отчётов из Альфреско
 * 
 * @author rabdullin
 *
 */
public interface ReportDAO {


	/**
	 * Получить "Описатеть отчёта" по id узла типа "lecm-rpeditor:reportDescriptor"
	 * @param id узел Альфреско (типа "lecm-rpeditor:reportDescriptor")
	 * @return
	 */
	ReportDescriptor getReportDescriptor(NodeRef id);

	/**
	 * Получить описатеть отчёта по названию
	 * @param mnemo мнемонический код отчёта (уникальный)
	 * @return
	 */
	ReportDescriptor getReportDescriptor(String mnemo);

	/*
	 * Получить "Шаблон отчета" (файл)
	 * @param id узел типа <type name="lecm-rpeditor:reportTemplate">
	 */
	ReportTemplate getReportTemplate(NodeRef id);

	/*
	 * Получить "Шаблон отчета" (файл) по названию
	 * @param fileName узел типа <type name="lecm-rpeditor:reportTemplate">
	 */
	ReportTemplate getReportTemplate(String fileName);

	/*
	 * Набор данных
	 * "lecm-rpeditor:reportDataSource"
	 */

	/*
	 * Колонка данных
	 * <type name="lecm-rpeditor:reportDataColumn">
	 */

	/*
	 * Справочники: Тип отчета
	 * <type name="lecm-rpeditor:reportType">
	 */

	/*
	 * Справочники: Тип провайдера
	 * <type name="lecm-rpeditor:reportProvider">
	 */

	/*
	 * Справочники: Тип колонок в отчете
	 * <type name="lecm-rpeditor:reportColumnType">
	 */

	/*
	 * Справочники: Тип параметра
	 * <type name="lecm-rpeditor:reportParameterType">
	 */

	/*
	 */
}
