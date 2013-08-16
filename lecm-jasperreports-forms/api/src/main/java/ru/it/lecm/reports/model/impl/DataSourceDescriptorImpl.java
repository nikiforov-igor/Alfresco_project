package ru.it.lecm.reports.model.impl;

import java.util.ArrayList;
import java.util.List;

import ru.it.lecm.reports.api.model.ColumnDescriptor;
import ru.it.lecm.reports.api.model.DataSourceDescriptor;
import ru.it.lecm.reports.api.model.L18able;
import ru.it.lecm.reports.utils.ParameterMapper;
import ru.it.lecm.reports.utils.Utils;

public class DataSourceDescriptorImpl
		extends MnemonicNamedItem
		implements DataSourceDescriptor
{
	private static final long serialVersionUID = 1L;

	private List<ColumnDescriptor> columns;

	public DataSourceDescriptorImpl() {
		super();
	}

	public DataSourceDescriptorImpl(String mnem, L18able name) {
		super(mnem, name);
	}

	public DataSourceDescriptorImpl(String mnem) {
		super(mnem);
	}

	@Override
	public List<ColumnDescriptor> getColumns() {
		if (this.columns == null)
			this.columns = new ArrayList<ColumnDescriptor>();
		return this.columns;
	}

	public void setColumns(List<ColumnDescriptor> columns) {
		this.columns = columns;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((columns == null) ? 0 : columns.hashCode());
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
		if (!super.equals(obj))
				return false;
		final DataSourceDescriptorImpl other = (DataSourceDescriptorImpl) obj;
		if (columns == null) {
			if (other.columns != null)
				return false;
		} else if (!columns.equals(other.columns))
			return false;
		return true;
	}

	@Override
	public String toString() {
		final int maxLen = 10;
		final StringBuilder builder = new StringBuilder();
		builder.append("DataSourceDescriptorImpl [");
		builder.append( String.format( " mnem '%s'", getMnem()) );
		builder.append( String.format( ", columns: %s \n", (columns == null ? "null" : "count "+ columns.size())));
		if (columns != null) {
			int i = 0;
			for (ColumnDescriptor c: columns.subList(0, Math.min(columns.size(), maxLen)) ) {
				++i;
				builder.append( String.format( "\t\t[col#%d]\t %s \n", i, (c == null ? "NULL" : c.toString()) ));
			}
		}
		builder.append("]");
		return builder.toString();
	}

	@Override
	public ColumnDescriptor findColumnByName(String colName) {
		if (!Utils.isStringEmpty(colName)) {
			for (ColumnDescriptor d: this.getColumns()) {
				if (d != null && colName.equalsIgnoreCase(d.getColumnName()) )
					return d; // FOUND
			}
		}
		return null; // NOT FOUND
	}

	@Override
	public ColumnDescriptor findColumnByParameter(String paramName) {
		if (!Utils.isStringEmpty(paramName)) {
			for (ColumnDescriptor d: this.getColumns()) {
				final String argName = ParameterMapper.getArgRootName(d);
				if (paramName.equalsIgnoreCase(argName))
					return d; // FOUND
			}
		}
		return null; // NOT FOUND
	}

}
