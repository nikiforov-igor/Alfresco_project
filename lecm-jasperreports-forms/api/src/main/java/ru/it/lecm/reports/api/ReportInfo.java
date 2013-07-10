package ru.it.lecm.reports.api;

import ru.it.lecm.reports.api.model.ReportType;

/**
 * Краткая информация по отчёту
 */
public class ReportInfo 
{
	public ReportType rtype; // тип отчёта (Jasper, OOffice ...)
	public String reportCode; // код отчёта
	public String documentType; // тип документов, поддерживаемых отчётом

	public ReportInfo() {
		super();
	}

	public ReportInfo( ReportType rtype, String reportCode) {
		this(rtype, reportCode, null);
	}

	public ReportInfo( ReportType rtype, String reportCode, String documentType) {
		super();
		this.rtype = rtype;
		this.reportCode = reportCode;
		this.documentType = documentType;
	}
}