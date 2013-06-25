package ru.it.lecm.reports.api.model;

public interface ReportFlags extends FlagsExtendable, QueryDescriptor {

	/**
	 * @return true, если отчёт может содержать более одной строки.
	 */
	boolean isMultiRow();
	void setMultiRow(boolean flag);

}
