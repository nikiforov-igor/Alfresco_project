package ru.it.lecm.reports.model.impl;

import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.api.model.SubReportDescriptor;
import ru.it.lecm.reports.utils.Utils;

import java.util.Map;

public class SubReportDescriptorImpl extends ReportDescriptorImpl implements SubReportDescriptor {
    private static final long serialVersionUID = 1L;

    private String sourceListExpression,
            destColumnName,
            beanClassName,
            sourceListType;

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

    public SubReportDescriptorImpl(String reportName) {
        super(reportName);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("SubReportDescriptorImpl [");
        builder.append("ownerReport '").append((ownerReport == null) ? "null" : ownerReport.getMnem()).append("'");
        builder.append("destColumn '").append(destColumnName).append("'");
        // builder.append(", tagIfEmpty '").append(tagIfEmpty).append("'");
        builder.append("\n\t, sourceListExpression '").append(sourceListExpression).append("'");
        builder.append("\n\t, beanClassName '").append(beanClassName).append("'");
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
    public String getSourceListType() {
        return sourceListType;
    }

    @Override
    public void setSourceListType(String type) {
        this.sourceListType = type;
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
    public String getBeanClassName() {
        return beanClassName;
    }

    @Override
    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
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
        // класс бина не указан, равен сигнатуре И задан формат
        return (Utils.isStringEmpty(this.beanClassName)
                || VAL_BEANCLASS_BYFORMAT.equalsIgnoreCase(this.beanClassName)) && (this.itemsFormat != null);
    }

    @Override
    public boolean isSubReport() {
        return true;
    }
}
