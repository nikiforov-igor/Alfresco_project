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
	public String reportName; // название отчёта

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

	public ReportType getRtype() {
		return rtype;
	}

	public void setRtype(ReportType rtype) {
		this.rtype = rtype;
	}

	public String getReportCode() {
		return reportCode;
	}

	public void setReportCode(String reportCode) {
		this.reportCode = reportCode;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

}