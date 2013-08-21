package ru.it.lecm.reports.model.impl;

import java.io.Serializable;

import ru.it.lecm.reports.api.model.ReportDefaultsDesc;

public class ReportDefaultsDescImpl 
		implements ReportDefaultsDesc, Serializable
{
	private static final long serialVersionUID = 1L;

	private String generationTemplate, fileExtension;

	public ReportDefaultsDescImpl() {
		super();
	}

	public ReportDefaultsDescImpl(String fileExtension, String generationTemplate) {
		super();
		this.fileExtension = fileExtension;
		this.generationTemplate = generationTemplate;
	}

	/**
	 * Расширение шаблона отчёта (с точкой в начале). Например, для Jasper это ".jrxml"
	 */
	@Override
	public String getFileExtension() {
		return fileExtension;
	}

	/**
	 * Расширение шаблона отчёта (с точкой в начале). Например, для Jasper это ".jrxml"
	 */
	@Override
	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	/**
	 * Шаблон для генерации шаблона отчёта ("шаблон шаблона")
	 * @return
	 */
	@Override
	public String getGenerationTemplate() {
		return generationTemplate;
	}

	/**
	 * Шаблон для генерации шаблона отчёта ("шаблон шаблона")
	 */
	@Override
	public void setGenerationTemplate(String template) {
		this.generationTemplate = template;
	}

}
