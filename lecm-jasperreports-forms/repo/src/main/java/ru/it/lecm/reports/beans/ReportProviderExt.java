package ru.it.lecm.reports.beans;

import ru.it.lecm.reports.api.model.ReportDescriptor;

/**
 * Доп интерфейс классов провайдеров отчётов
 * @author rabdullin
 *
 */
public interface ReportProviderExt {

	void setServices(WKServiceKeeper services);

	void setReportDescriptor(ReportDescriptor reportDescriptor);

}
