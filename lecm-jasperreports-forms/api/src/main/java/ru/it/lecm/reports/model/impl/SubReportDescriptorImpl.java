package ru.it.lecm.reports.model.impl;

import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.api.model.SubReportDescriptor;

import java.util.*;

public class SubReportDescriptorImpl extends ReportDescriptorImpl implements SubReportDescriptor {
    private static final long serialVersionUID = 1L;

    private String sourceListExpression,
            destColumnName;

    private Set<String> sourceTypes = new HashSet<String>();

    private ItemsFormatDescriptor itemsFormat;
    private Map<String, String> subItemsSourceMap;
    private ReportDescriptor ownerReport;

	public SubReportDescriptorImpl() {
	}

    public SubReportDescriptorImpl(ReportDescriptor descriptor) {
        this.setL18Name(((ReportDescriptorImpl) descriptor).getL18Name());
        this.setMnem(descriptor.getMnem());
        this.setProviderDescriptor(descriptor.getProviderDescriptor());
        this.setDSDescriptor(descriptor.getDsDescriptor());
        this.setFlags(descriptor.getFlags());
        this.setReportTemplate(descriptor.getReportTemplate());
        this.setReportType(descriptor.getReportType());
        this.setSubReport(true);
        this.setSubreports(descriptor.getSubreports());
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("SubReportDescriptorImpl [");
        builder.append("ownerReport '").append((ownerReport == null) ? "null" : ownerReport.getMnem()).append("'");
        builder.append("destColumn '").append(destColumnName).append("'");
        builder.append("\n\t, sourceListExpression '").append(sourceListExpression).append("'");
        builder.append("\n\t, itemsFormat ").append(itemsFormat);
        builder.append("\n\t, subItemsSourceMap [").append(subItemsSourceMap).append("]");
        builder.append("\n\t, reportDescriptor ").append(super.toString());
        builder.append("\n]");
        return builder.toString();
    }

    @Override
    public String getSourceListExpression() {
        return sourceListExpression;
    }

    @Override
    public void setSourceListExpression(String sourceListExpression) {
        this.sourceListExpression = sourceListExpression;
    }

    @Override
    public Set<String> getSourceListType() {
        return sourceTypes;
    }

    @Override
    public void setSourceListType(Set<String> types) {
        for (String type : types) {
            if (type != null && !type.isEmpty()) {
                this.sourceTypes.add(type);
            }
        }
    }

    public void setSourceListType(String types) {
        if (types != null && !types.isEmpty()) {
            String[] typesArray = types.split(",");
            List<String> typesList = Arrays.asList(typesArray);
            setSourceListType(new HashSet<String>(typesList));
        }
    }

    @Override
    public String getDestColumnName() {
        return destColumnName;
    }

    @Override
    public void setDestColumnName(String destColumnName) {
        this.destColumnName = destColumnName;
    }

    @Override
    public ItemsFormatDescriptor getItemsFormat() {
        return itemsFormat;
    }

    @Override
    public void setItemsFormat(ItemsFormatDescriptor itemsFormat) {
        this.itemsFormat = itemsFormat;
    }

    @Override
    public Map<String, String> getSubItemsSourceMap() {
        return subItemsSourceMap;
    }

    @Override
    public void setSubItemsSourceMap(Map<String, String> subItemsSourceMap) {
        this.subItemsSourceMap = subItemsSourceMap;
    }

    @Override
    public ReportDescriptor getOwnerReport() {
        return ownerReport;
    }

    @Override
    public void setOwnerReport(ReportDescriptor ownerReport) {
        this.ownerReport = ownerReport;
    }

    @Override
    public boolean isUsingFormat() {
        return this.itemsFormat != null;
    }

    @Override
    public boolean isSubReport() {
        return true;
    }
}
