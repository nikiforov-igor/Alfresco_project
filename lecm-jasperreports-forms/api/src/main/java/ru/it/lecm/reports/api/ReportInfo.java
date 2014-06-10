package ru.it.lecm.reports.api;

/**
 * Краткая информация по отчёту
 */
public class ReportInfo {
	public String reportCode; // код отчёта
	public String documentType; // тип документов, поддерживаемых отчётом
	public String reportName; // название отчёта

    public ReportInfo(String reportCode) {
        this(reportCode, null);
	}

    public ReportInfo(String reportCode, String documentType) {
		super();
		this.reportCode = reportCode;
		this.documentType = documentType;
	}

	public String getReportCode() {
		return reportCode;
	}

	public void setReportCode(String reportCode) {
		this.reportCode = reportCode;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

    public String getReportName() {
        return reportName;
    }
}