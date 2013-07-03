package ru.it.lecm.reports.api;

import ru.it.lecm.reports.api.model.ReportDescriptor;

/**
 * Биновый интерфейс для работы с шаблонами зарегистрированных отчётов.
 * @author rabdullin
 */
public interface ReportsManager {

	/**
	 * Получить описатеть отчёта по названию
	 * @param reportMnemoName мнемонический код отчёта (уникальный)
	 * @return
	 */
	ReportDescriptor getReportDescriptor(String reportMnemoName);

}
