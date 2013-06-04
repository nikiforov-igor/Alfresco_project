package ru.it.lecm.reports.api;

import org.alfresco.service.cmr.search.ResultSet;

/**
 * Менеджер отчётов.
 * Поддерживает регистрацию провайдеров отчётов.
 * @author rabdullin
 */
public interface ReportManager {

	/**
	 * Зарегистрировать новый обработчик для построения договоров.
	 * @param handler
	 */
	void registerReportHandler(ReportHandler handler);


	/**
	 * Обработчик для произвольного шаблона	
	 * @author rabdullin
	 */
	interface ReportHandler {

		/**
		 * @return Описание отчёта - название, desc, filler и пр
		 */
		ReportInfo getReportInfo();

		/**
		 * Выполнить поиск и вернуть НД, состоящий из id узлов, каждый из которых
		 * сформирует одну строку в таблице отчёта.
		 * Получение данных для каждого узла будет затем выполняться DataFiller.
		 * @param context
		 * @return
		 */
		ResultSet doSearch(ReportDSContext context);

		/**
		 * @param context контекст построения отчёта
		 * @return объект наполнения, который будет использоваться во время 
		 * формирования списка значений полей
		 */
		ReportDSFiller getDataFiller(ReportDSContext context); 
	}
}
