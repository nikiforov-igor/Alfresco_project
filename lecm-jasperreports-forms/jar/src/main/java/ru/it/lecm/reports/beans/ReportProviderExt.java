package ru.it.lecm.reports.beans;

import ru.it.lecm.reports.api.ReportsManager;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.generators.ReportGeneratorBase;

/**
 * Доп интерфейс классов провайдеров отчётов
 * @author rabdullin
 *
 */
public interface ReportProviderExt {

	void setReportDescriptor(ReportDescriptor reportDescriptor);

	void setReportsManager(ReportsManager reportsManager);

    void initializeFromGenerator(ReportGeneratorBase baseGenerator);
}
