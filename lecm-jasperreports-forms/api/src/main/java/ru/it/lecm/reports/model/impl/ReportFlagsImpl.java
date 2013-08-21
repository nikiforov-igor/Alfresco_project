package ru.it.lecm.reports.model.impl;

import java.util.Set;

import ru.it.lecm.reports.api.model.NamedValue;
import ru.it.lecm.reports.api.model.QueryDescriptor;
import ru.it.lecm.reports.api.model.ReportFlags;

public class ReportFlagsImpl
		extends FlagsExtendableImpl
		implements ReportFlags
{
	private static final long serialVersionUID = 1L;

	private QueryDescriptor queryDesc;
	private boolean isMultiRow = false;
	private boolean custom = false;

	public ReportFlagsImpl() {
		super();
	}

	public ReportFlagsImpl(Set<NamedValue> flags, boolean isMultiRow) {
		super(flags);
		this.isMultiRow = isMultiRow;
	}

	public ReportFlagsImpl(Set<NamedValue> flags) {
		this(flags, false);
	}

	@Override
	public String getText() {
		return queryDesc().getText();
	}

	@Override
	public void setText(String text) {
		queryDesc().setText(text);
	}

	@Override
	public String getPreferedNodeType() {
		return queryDesc().getPreferedNodeType();
	}

	@Override
	public void setPreferedNodeType(String value) {
		queryDesc().setPreferedNodeType(value);
	}

	@Override
	public int getOffset() {
		return queryDesc().getOffset();
	}


	@Override
	public void setOffset(int value) {
		queryDesc().setOffset(value);
	}

	@Override
	public int getLimit() {
		return queryDesc().getLimit();
	}

	@Override
	public void setLimit(int value) {
		queryDesc().setLimit(value);
	}

	@Override
	public int getPgSize() {
		return queryDesc().getPgSize();
	}

	@Override
	public void setPgSize(int value) {
		queryDesc().setPgSize(value);
	}

	@Override
	public boolean isAllVersions() {
		return queryDesc().isAllVersions();
	}

	@Override
	public void setAllVersions(boolean value) {
		queryDesc().setAllVersions(value);
	}

	@Override
	public String getMnem() {
		return queryDesc().getMnem();
	}

	@Override
	public void setMnem(String mnemo) {
		queryDesc().setMnem(mnemo);
	}

	@Override
	public boolean isTypeSupported(String qname) {
		return queryDesc().isTypeSupported(qname);
	}

	private QueryDescriptor queryDesc() {
		if (this.queryDesc == null)
			this.queryDesc = new QueryDescriptorImpl();
		return this.queryDesc;
	}

	public QueryDescriptor getQueryDesc() {
		return queryDesc();
	}

	public void setQueryDesc(QueryDescriptor queryDesc) {
		this.queryDesc = queryDesc;
	}

	@Override
	public boolean isMultiRow() {
		return this.isMultiRow;
	}

	@Override
	public void setMultiRow(boolean value) {
		this.isMultiRow = value;
	}

	@Override
	public boolean isCustom() {
		return this.custom;
	}

	@Override
	public void setCustom(boolean flag) {
		this.custom = flag;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (isMultiRow ? 1231 : 1237);
		result = prime * result
				+ ((queryDesc == null) ? 0 : queryDesc.hashCode());
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
		final ReportFlagsImpl other = (ReportFlagsImpl) obj;
		if (isMultiRow != other.isMultiRow)
			return false;
		if (queryDesc == null) {
			if (other.queryDesc != null)
				return false;
		} else if (!queryDesc.equals(other.queryDesc))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("reportflags [");
		builder.append("isMultiRow ").append(isMultiRow);
		builder.append(", ").append(super.toString());
		builder.append(", queryDesc ").append(queryDesc);
		builder.append("]");
		return builder.toString();
	}

}
