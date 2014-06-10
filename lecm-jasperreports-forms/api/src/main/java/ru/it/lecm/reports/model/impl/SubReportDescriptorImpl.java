package ru.it.lecm.reports.model.impl;

import ru.it.lecm.reports.api.model.ReportDescriptor;

import java.util.*;

public class SubReportDescriptorImpl extends ReportDescriptorImpl {
    private static final long serialVersionUID = 1L;

    /** название свойства бина для присвоения строкового номера */
    public static final String BEAN_PROPNAME_COL_ROWNUM = "colRownum";

    private String sourceListExpression, destColumnName;

    private Set<String> sourceTypes = new HashSet<String>();

    private ItemsFormatDescriptor itemsFormat;
    private Map<String, String> subItemsSourceMap;
    private ReportDescriptor ownerReport;

    private ReportTemplate parentTemplate = null;

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("SubReportDescriptorImplImpl [");
        builder.append("ownerReport '").append((ownerReport == null) ? "null" : ownerReport.getMnem()).append("'");
        builder.append("destColumn '").append(destColumnName).append("'");
        builder.append("\n\t, sourceListExpression '").append(sourceListExpression).append("'");
        builder.append("\n\t, itemsFormat ").append(itemsFormat);
        builder.append("\n\t, subItemsSourceMap [").append(subItemsSourceMap).append("]");
        builder.append("\n\t, reportDescriptor ").append(super.toString());
        builder.append("\n]");
        return builder.toString();
    }

    public String getSourceListExpression() {
        return sourceListExpression;
    }

    public void setSourceListExpression(String sourceListExpression) {
        this.sourceListExpression = sourceListExpression;
    }

    public Set<String> getSourceListType() {
        return sourceTypes;
    }

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

    public String getDestColumnName() {
        return destColumnName;
    }

    public void setDestColumnName(String destColumnName) {
        this.destColumnName = destColumnName;
    }

    public ItemsFormatDescriptor getItemsFormat() {
        return itemsFormat;
    }

    public void setItemsFormat(ItemsFormatDescriptor itemsFormat) {
        this.itemsFormat = itemsFormat;
    }

    public Map<String, String> getSubItemsSourceMap() {
        return subItemsSourceMap;
    }

    public void setSubItemsSourceMap(Map<String, String> subItemsSourceMap) {
        this.subItemsSourceMap = subItemsSourceMap;
    }

    public ReportDescriptor getOwnerReport() {
        return ownerReport;
    }

    public void setOwnerReport(ReportDescriptor ownerReport) {
        this.ownerReport = ownerReport;
    }

    public boolean isUsingFormat() {
        return this.itemsFormat != null;
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
    public boolean isSubReport() {
        return true;
    }

    public ReportTemplate getParentTemplate() {
        return parentTemplate;
    }

    public void setParentTemplate(ReportTemplate parentTemplate) {
        this.parentTemplate = parentTemplate;
    }
}
