package ru.it.lecm.reports.api.model;

import ru.it.lecm.reports.model.impl.ReportFlags;
import ru.it.lecm.reports.model.impl.ReportProviderDescriptor;
import ru.it.lecm.reports.model.impl.ReportTemplate;

import java.util.List;
import java.util.Set;

public interface ReportDescriptor extends Mnemonicable, L18able {

    /**
     * Описатель данных.
     */
    DataSourceDescriptor getDsDescriptor();

    /**
     * Шаблон для построения отчёта (файл) в терминах провайдера.
     * Например jrxml-файл для Jasper-отчёта.
     */
    List<ReportTemplate> getReportTemplates();

    ReportProviderDescriptor getProviderDescriptor();

    ReportFlags getFlags();

    /**
     * @return список подотчётов или null если нет таковых
     */
    List<ReportDescriptor> getSubreports();

    void setSubreports(List<ReportDescriptor> list);

    boolean isSubReport();

    boolean isSQLDataSource();

    public void setDSDescriptor(DataSourceDescriptor dsDescriptor);

    public void setReportTemplates(List<ReportTemplate> reportTemplate);

    public void setProviderDescriptor(ReportProviderDescriptor providerDescriptor);

    public void setFlags(ReportFlags flags);

    public ReportTemplate getDefaultTemplate();

    public Set<String> getBusinessRoles();

    public void setBusinessRoles(Set<String> roles);
}
