package ru.it.lecm.reports.model.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ru.it.lecm.reports.api.model.*;
import ru.it.lecm.reports.utils.Utils;

public class ReportDescriptorImpl extends MnemonicNamedItem implements ReportDescriptor {
    private static final long serialVersionUID = 1L;

    protected DataSourceDescriptor dsDescriptor;
    protected List<ReportTemplate> reportTemplates;

    protected ReportProviderDescriptor providerDescriptor;
    protected ReportFlags flags;
    protected List<ReportDescriptor> subreports;

    protected Set<String> businessRoles;

    public ReportDescriptorImpl() {
        super();
    }

    @Override
    public ReportFlags getFlags() {
        if (this.flags == null)
            this.flags = new ReportFlags();
        return this.flags;
    }

    @Override
    public DataSourceDescriptor getDsDescriptor() {
        if (this.dsDescriptor == null)
            this.dsDescriptor = new DataSourceDescriptorImpl();
        return this.dsDescriptor;
    }

    @Override
    public List<ReportTemplate> getReportTemplates() {
        return this.reportTemplates;
    }

    @Override
    public ReportProviderDescriptor getProviderDescriptor() {
        return this.providerDescriptor;
    }

    @Override
    public void setDSDescriptor(DataSourceDescriptor dsDescriptor) {
        this.dsDescriptor = dsDescriptor;
    }

    @Override
    public void setReportTemplates(List<ReportTemplate> reportTemplates) {
        this.reportTemplates = reportTemplates;
    }

    @Override
    public void setProviderDescriptor(ReportProviderDescriptor providerDescriptor) {
        this.providerDescriptor = providerDescriptor;
    }

    @Override
    public void setFlags(ReportFlags flags) {
        this.flags = flags;
    }

    @Override
    public ReportTemplate getDefaultTemplate() {
        if (getReportTemplates() == null || getReportTemplates().isEmpty()) {
            return null;
        }
        return getReportTemplates().get(0);
    }

    @Override
    public Set<String> getBusinessRoles() {
        if (businessRoles == null) {
            return new HashSet<String>();
        }
        return businessRoles;
    }

    @Override
    public void setBusinessRoles(Set<String> roles) {
        this.businessRoles = roles;
    }

    @Override
    public List<ReportDescriptor> getSubreports() {
        return subreports;
    }

    @Override
    public void setSubreports(List<ReportDescriptor> list) {
        this.subreports = list;
        // пропишем подотчётам владельца ...
        if (this.subreports != null) {
            for (ReportDescriptor item : this.subreports) {
                if (item.isSubReport()) {
                    SubReportDescriptorImpl subItem = (SubReportDescriptorImpl)item;
                    String destColumn = subItem.getDestColumnName();
                    ColumnDescriptor cd = dsDescriptor.findColumnByName(destColumn);
                    if (cd != null) {
                        // тип колонки в основном отчёте, которая соот-вет подотчёту:
                        //    String, если используется форматирование
                        //    List, иначе
                        final Class<?> classOfMainReportColumn = (subItem.isUsingFormat()) ? String.class : List.class;
                        cd.setClassName(classOfMainReportColumn.getName());
                    }
                    subItem.setOwnerReport(this);
                }
            }
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((providerDescriptor == null) ? 0 : providerDescriptor.hashCode());
        result = prime * result + ((reportTemplates == null) ? 0 : reportTemplates.hashCode());
        result = prime * result + ((flags == null) ? 0 : flags.hashCode());
        result = prime * result + ((dsDescriptor == null) ? 0 : dsDescriptor.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ReportDescriptorImpl other = (ReportDescriptorImpl) obj;

        if (providerDescriptor == null) {
            if (other.providerDescriptor != null)
                return false;
        } else if (!providerDescriptor.equals(other.providerDescriptor))
            return false;

        if (reportTemplates == null) {
            if (other.reportTemplates != null)
                return false;
        } else if (!reportTemplates.equals(other.reportTemplates))
            return false;

        if (dsDescriptor == null) {
            if (other.dsDescriptor != null)
                return false;
        } else if (!dsDescriptor.equals(other.dsDescriptor))
            return false;

        if (flags == null) {
            if (other.flags != null)
                return false;
        } else if (!flags.equals(other.flags))
            return false;

        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("ReportDescriptorImpl [ mnem '%s'", getMnem()));
        builder.append("\n\t, reportTemplates '").append(reportTemplates).append("'");
        builder.append("\n\t, providerDescriptor '").append(providerDescriptor).append("'");
        builder.append("\n\t, flags ").append(flags);
        builder.append("\n\t, dsDescriptor ").append(dsDescriptor);
        builder.append("\n\t, subreports: \n\t\t").append(Utils.getAsString(this.subreports, "\n\t\t"));
        builder.append("\n\t]");
        return builder.toString();
    }

    @Override
    public boolean isSubReport() {
        return false;
    }

    @Override
    public boolean isSQLDataSource() {
        return getProviderDescriptor() != null && getProviderDescriptor().getMnem().equals("SQL_PROVIDER");
    }
}
