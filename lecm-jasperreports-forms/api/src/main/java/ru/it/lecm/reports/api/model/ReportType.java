package ru.it.lecm.reports.api.model;

public interface ReportType extends L18able, Mnemonicable {

	/**
	 * Мнемоника для Jasper-отчётов
	 */
	final public static String RTYPE_MNEMO_JASPER = "JASPER";

	/**
	 * Мнемоника для OpenOffice-отчётов
	 */
	final public static String RTYPE_MNEMO_OOFFICE = "OOFFICE";

}
