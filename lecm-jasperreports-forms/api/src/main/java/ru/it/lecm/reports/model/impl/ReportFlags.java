package ru.it.lecm.reports.model.impl;

import ru.it.lecm.reports.api.model.FlagsExtendable;
import ru.it.lecm.reports.api.model.QueryDescriptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ReportFlags extends FlagsExtendableImpl implements FlagsExtendable, QueryDescriptor {
    private static final long serialVersionUID = 1L;

    private QueryDescriptor queryDesc;
    private boolean isMultiRow = false;
    private boolean custom = false;
    private boolean runAsSystem = true;
    private boolean includeAllOrganizations = false;

    public ReportFlags() {
        super();
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
    public String getSort() {
        return queryDesc().getSort();
    }

    @Override
    public void setSort(String sort) {
        queryDesc().setSort(sort);
    }

    @Override
    public void setPreferedNodeType(String value) {
        queryDesc().setPreferedNodeType(value);
    }

    @Override
    public List<String> getSupportedNodeTypes() {
        return queryDesc().getSupportedNodeTypes();
    }

    @Override
    public void setSupportedNodeTypes(List<String> values) {
        queryDesc().setSupportedNodeTypes(values);
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

    public boolean isMultiRow() {
        return this.isMultiRow;
    }

    public void setMultiRow(boolean value) {
        this.isMultiRow = value;
    }

    public boolean isCustom() {
        return this.custom;
    }

    public void setCustom(boolean flag) {
        this.custom = flag;
    }

    public boolean isRunAsSystem() {
        return runAsSystem;
    }

    public void setRunAsSystem(boolean runAsSystem) {
        this.runAsSystem = runAsSystem;
    }

    public boolean isIncludeAllOrganizations() {
        return includeAllOrganizations;
    }

    public void setIncludeAllOrganizations(boolean includeAllOrganizations) {
        this.includeAllOrganizations = includeAllOrganizations;
    }

    public Map<String, String> getFlagsMap() {
        Map<String, String> flagsMap = new HashMap<String, String>();
        Set<NamedValue> flags = flags();

        for (NamedValue flag : flags) {
            flagsMap.put(flag.getMnem(), flag.getValue());
        }

        return flagsMap;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (isMultiRow ? 1231 : 1237);
        result = prime * result + (runAsSystem ? 1231 : 1237);
        result = prime * result + (includeAllOrganizations ? 1231 : 1237);
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
        final ReportFlags other = (ReportFlags) obj;
        if (isMultiRow != other.isMultiRow)
            return false;
        if (runAsSystem != other.runAsSystem)
            return false;
        if (includeAllOrganizations != other.includeAllOrganizations)
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
        builder.append("runAsSystem ").append(runAsSystem);
        builder.append("includeAllOrganizations ").append(includeAllOrganizations);
        builder.append(", ").append(super.toString());
        builder.append(", queryDesc ").append(queryDesc);
        builder.append("]");
        return builder.toString();
    }
}
