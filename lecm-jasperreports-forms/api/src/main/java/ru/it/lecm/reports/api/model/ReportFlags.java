package ru.it.lecm.reports.api.model;

import java.io.Serializable;
import java.util.Map;

public interface ReportFlags extends FlagsExtendable, QueryDescriptor {

	/**
	 * @return true, если отчёт может содержать более одной строки.
	 */
	boolean isMultiRow();
	void setMultiRow(boolean flag);

	/**
	 * @return true, если отчёт яваляется специальным и его не требуется 
	 * сопровождать редактором отчётов и отображать среди созданных им отчётов
	 */
	boolean isCustom();
	void setCustom(boolean flag);

    Map<String, String> getFlagsMap();
}
