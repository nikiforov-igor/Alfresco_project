package ru.it.lecm.reports.api;



public interface ScriptApiReportManager {

	/**
	 * Выполнить регистрацию отчёта
	 * @param reportDescNode id узла описателя отчёта
	 * @return true, если выполнено успешно и false иначе
	 */
	boolean deployReport(final String reportDescNode);

	/**
	 * Получить данные ds-xml файла, который соответствует указанному отчёту 
	 * @param reportCode код (мнемоника) отчёта
	 * @return
	 */
	public byte[] getDsXmlBytes(final String reportCode);

	/**
	 * Получить список зарешистрированных отчётов указанного типа
	 * @param docType тип документов или null, если для всех типов
	 * @param reportType тип отчёта или null, если для любого типа
	 * @return список зарегеных отчётов (если отчёт не зависит от типа документа
	 * (т.е. у этого отчёта doctype=null), то он воз-ся при любом значении 
	 * параметра docType)
	 */
	public ReportInfo[] getRegisteredReports(final String docType
			, final String reportType);
}
