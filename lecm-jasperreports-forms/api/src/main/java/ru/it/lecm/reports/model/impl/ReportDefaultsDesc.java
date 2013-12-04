package ru.it.lecm.reports.model.impl;

import java.io.Serializable;

public class ReportDefaultsDesc implements Serializable {
	private static final long serialVersionUID = 1L;

	private String generationTemplate;
    private String subReportGenerationTemplate;
    private String fileExtension;

	public ReportDefaultsDesc() {
		super();
	}

	public ReportDefaultsDesc(String fileExtension, String generationTemplate, String subReportGenerationTemplate) {
		super();
		this.fileExtension = fileExtension;
		this.generationTemplate = generationTemplate;
        this.subReportGenerationTemplate = subReportGenerationTemplate;
	}

	/**
	 * Расширение шаблона отчёта (с точкой в начале). Например, для Jasper это ".jrxml"
	 */
	public String getFileExtension() {
		return fileExtension;
	}

	/**
	 * Расширение шаблона отчёта (с точкой в начале). Например, для Jasper это ".jrxml"
	 */
	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	/**
	 * Шаблон для генерации шаблона отчёта ("шаблон шаблона")
	 */
	public String getGenerationTemplate() {
		return generationTemplate;
	}

	/**
	 * Шаблон для генерации шаблона отчёта ("шаблон шаблона")
	 */
	public void setGenerationTemplate(String template) {
		this.generationTemplate = template;
	}

    public String getSubReportGenerationTemplate() {
        return subReportGenerationTemplate;
    }

    public void setSubReportGenerationTemplate(String subReportGenerationTemplate) {
        this.subReportGenerationTemplate = subReportGenerationTemplate;
    }
}
