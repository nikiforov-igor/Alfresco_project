package ru.it.lecm.reports.model.impl;

import java.util.List;

import ru.it.lecm.reports.api.model.*;
import ru.it.lecm.reports.utils.Utils;

public class ReportDescriptorImpl
		extends MnemonicNamedItem
		implements ReportDescriptor
{
	private static final long serialVersionUID = 1L;

	private DataSourceDescriptor dsDescriptor;
	private ReportType reportType;
	private ReportTemplate reportTemplate;

	private ReportProviderDescriptor providerDescriptor;
	private ReportFlags flags;
	private List<SubReportDescriptor> subreports;

    private boolean subReport = false;

	public ReportDescriptorImpl() {
		super();
	}

	public ReportDescriptorImpl(String mnem) {
		super(mnem);
	}

	@Override
	public ReportType getReportType() {
		if (this.reportType == null)
			this.reportType = new ReportTypeImpl();
		return this.reportType;
	}

	@Override
	public ReportFlags getFlags() {
		if (this.flags == null)
			this.flags = new ReportFlagsImpl();
		return this.flags;
	}

	@Override
	public DataSourceDescriptor getDsDescriptor() {
		if (this.dsDescriptor == null)
			this.dsDescriptor = new DataSourceDescriptorImpl();
		return this.dsDescriptor;
	}

	@Override
	public ReportTemplate getReportTemplate() {
		if (this.reportTemplate == null)
			this.reportTemplate = new ReportTemplateImpl();
		return this.reportTemplate;
	}

	@Override
	public ReportProviderDescriptor getProviderDescriptor() {
		if (this.providerDescriptor == null)
			this.providerDescriptor = new ReportProviderDescriptorImpl();
		return this.providerDescriptor;
	}

	public void setDSDescriptor(DataSourceDescriptor dsDescriptor) {
		this.dsDescriptor = dsDescriptor;
	}

	public void setReportType(ReportType reportType) {
		this.reportType = reportType;
	}

	public void setReportTemplate(ReportTemplate reportTemplate) {
		this.reportTemplate = reportTemplate;
	}

	public void setProviderDescriptor(ReportProviderDescriptor providerDescriptor) {
		this.providerDescriptor = providerDescriptor;
	}

	public void setFlags(ReportFlags flags) {
		this.flags = flags;
	}

	@Override
	public List<SubReportDescriptor> getSubreports() {
		return subreports;
	}

	@Override
    public void setSubreports(List<SubReportDescriptor> list) {
        this.subreports = list;
        // пропишем подотчётам владельца ...
        if (this.subreports != null) {
            for (SubReportDescriptor item : this.subreports) {
                String destColumn = item.getDestColumnName();
                ColumnDescriptor cd = dsDescriptor.findColumnByName(destColumn);
                if (cd != null) {
                    // тип колонки в основном отчёте, которая соот-вет подотчёту:
                    //    String, если используется форматирование
                    //    List, иначе
                    final Class<?> classOfMainReportColumn = (item.isUsingFormat()) ? String.class : List.class;
                    cd.setClassName(classOfMainReportColumn.getName());
                }
                item.setOwnerReport(this);
            }
        }
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((reportType == null) ? 0 : reportType.hashCode());
		result = prime * result + ((providerDescriptor == null) ? 0 : providerDescriptor.hashCode());
		result = prime * result + ((reportTemplate == null) ? 0 : reportTemplate.hashCode());
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
		if (reportType == null) {
			if (other.reportType != null)
				return false;
		} else if (!reportType.equals(other.reportType))
			return false;

		if (providerDescriptor == null) {
			if (other.providerDescriptor != null)
				return false;
		} else if (!providerDescriptor.equals(other.providerDescriptor))
			return false;

		if (reportTemplate == null) {
			if (other.reportTemplate != null)
				return false;
		} else if (!reportTemplate.equals(other.reportTemplate))
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
		builder.append( String.format( "ReportDescriptorImpl [ mnem '%s'", getMnem()) );
		builder.append("\n\t, reportType ").append(reportType);
		builder.append("\n\t, reportTemplate '").append(reportTemplate).append("'");
		builder.append("\n\t, providerDescriptor '").append(providerDescriptor).append("'");
		builder.append("\n\t, flags ").append(flags);
		builder.append("\n\t, dsDescriptor ").append(dsDescriptor);
		builder.append("\n\t, subreports: \n\t\t").append(Utils.getAsString( this.subreports, "\n\t\t"));
		builder.append("\n\t]");
		return builder.toString();
	}

    public boolean isSubReport() {
        return subReport;
    }

    @Override
    public boolean isSQLDataSource() {
        return getProviderDescriptor() != null && getProviderDescriptor().getMnem().equals("SQL_PROVIDER");
    }

    public void setSubReport(boolean subReport) {
        this.subReport = subReport;
    }
}
