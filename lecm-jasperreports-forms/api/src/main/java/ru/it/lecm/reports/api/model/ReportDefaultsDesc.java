package ru.it.lecm.reports.api.model;

/**
 * Описатеть умолчаний для типа отчёта.
 * [Расширение, Шаблон для гнерации шаблона отчёта]>
 */
public interface ReportDefaultsDesc {

	/**
	 * Расширение шаблона отчёта (с точкой в начале). Например, для Jasper это ".jrxml"
	 */
	public String getFileExtension();

	/**
	 * Расширение шаблона отчёта (с точкой в начале). Например, для Jasper это ".jrxml"
	 */
	public void setFileExtension(String fileExtension);

	/**
	 * Макет для генерации шаблона отчёта ("шаблон шаблона")
	 * @return
	 */
	public String getGenerationTemplate();

	/**
	 * Макет для генерации шаблона отчёта ("шаблон шаблона")
	 */
	public void setGenerationTemplate(String template);
}