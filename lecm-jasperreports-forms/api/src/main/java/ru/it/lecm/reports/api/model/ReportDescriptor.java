package ru.it.lecm.reports.api.model;

import java.util.List;

public interface ReportDescriptor extends Mnemonicable, L18able {

    /**
     * Описатель данных.
     */
    DataSourceDescriptor getDsDescriptor();

    /**
     * Тип отчёта (и провайдера). Ключ уникальности - обычная мнемоника.
     * Список доуступных отчётов расширяем, но требует разворачивания соот-щих
     * jar-ок дл яновых провайдеров.
     */
    ReportType getReportType();

    /**
     * Шаблон для построения отчёта (файл) в терминах провайдера.
     * Например jrxml-файл для Jasper-отчёта.
     */
    ReportTemplate getReportTemplate();

    ReportProviderDescriptor getProviderDescriptor();

    ReportFlags getFlags();

    /**
     * @return список подотчётов или null если нет таковых
     */
    List<ReportDescriptor> getSubreports();

    void setSubreports(List<ReportDescriptor> list);

    boolean isSubReport();

    public void setSubReport(boolean isSubReport);

    boolean isSQLDataSource();

    public void setDSDescriptor(DataSourceDescriptor dsDescriptor);

    public void setReportType(ReportType reportType);

    public void setReportTemplate(ReportTemplate reportTemplate);

    public void setProviderDescriptor(ReportProviderDescriptor providerDescriptor);

    public void setFlags(ReportFlags flags);
}
