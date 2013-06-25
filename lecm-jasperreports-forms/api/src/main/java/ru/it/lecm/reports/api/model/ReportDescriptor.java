package ru.it.lecm.reports.api.model;

public interface ReportDescriptor extends Mnemonicable, L18able {

	DataSourceDescriptor getDsDescriptor();

	ReportType getReportType();

	ReportTemplate getReportTemplate();

	ReportProviderDescriptor getProviderDescriptor();

	ReportFlags getFlags();
}
