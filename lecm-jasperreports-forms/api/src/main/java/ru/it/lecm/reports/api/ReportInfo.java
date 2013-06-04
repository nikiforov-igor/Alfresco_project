package ru.it.lecm.reports.api;

/**
 * Описатель отчёта. 
 * @author rabdullin
 */
public interface ReportInfo {

	/**
	 * Название отчёта, которое будет использоваться при передаче параметров
	 * для построения.
	 * Например, "approval-list" или "contract-dossier"
	 * Для jasper-отчётов это фактически название jrxml-шаблона без расширения.
	 * @return
	 */
	String getWebName();

	/**
	 * Описание отчёта
	 * @return
	 */
	String getDescription();

}
