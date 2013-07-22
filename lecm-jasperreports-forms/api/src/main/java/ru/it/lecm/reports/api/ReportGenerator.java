package ru.it.lecm.reports.api;

import java.io.IOException;
import java.util.Map;

import org.springframework.extensions.webscripts.WebScriptResponse;

import ru.it.lecm.reports.api.model.ReportDescriptor;

public interface ReportGenerator {

	/**
	 * Построить отчёт по его мнемоническому названию и параметрам
	 * @param webScriptResponse выходной ответ
	 * @param reportName мнемоническое название отчёта
	 * @param parameters параметры (обычно это request-параметры).
	 * подразумевается что названия параметров в этом списке совпадают с мнемоникой
	 * соот-щих колонок набора данных, который соот-ет шаблону reportName.
	 * Если это не так, тогда провайдер "сам" должен разбираться "что и куда"
	 * надо назначить.
	 * @param reportDesc описатель отчёта (null, если нет описателя - "hardcoded report")
	 * @throws IOException
	 */
	void produceReport(WebScriptResponse webScriptResponse
			, String reportName
			, Map<String, String[]> parameters
			, ReportDescriptor reportDesc
			) throws IOException;

	/**
	 * Вызывается менеджером при получении нового шаблона, чтобы провайдер среагировал 
	 * (например, успел построить .jasper для .jrxml)
	 * @param templateFileFullName полное название шаблона
	 * @param desc зарегеный описатель
	 */
	void onRegister(String templateFileFullName, ReportDescriptor desc);
}
