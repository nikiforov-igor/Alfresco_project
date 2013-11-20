package ru.it.lecm.reports.model.DAO;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.reports.api.model.*;
import ru.it.lecm.reports.api.model.DAO.ReportEditorDAO;
import ru.it.lecm.reports.beans.WKServiceKeeper;
import ru.it.lecm.reports.generators.LucenePreparedQuery;
import ru.it.lecm.reports.model.impl.*;
import ru.it.lecm.reports.model.impl.JavaDataTypeImpl.SupportedTypes;
import ru.it.lecm.reports.utils.Utils;
import ru.it.lecm.utils.LuceneSearchBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class ReportEditorDAOImpl implements ReportEditorDAO {

    private static final transient Logger log = LoggerFactory.getLogger(ReportEditorDAOImpl.class);

    private WKServiceKeeper services;

    public void init() {
        log.info("initialized " + this.getClass());
    }

    public WKServiceKeeper getServices() {
        return services;
    }

    public void setServices(WKServiceKeeper services) {
        this.services = services;
    }

    @Override
    public ReportDescriptor getReportDescriptor(NodeRef id) {
        final Map<QName, Serializable> map;
        try {
            map = getServices().getServiceRegistry().getNodeService().getProperties(id);
        } catch (InvalidNodeRefException ex) {
            log.warn(String.format("ReportDescriptor node not found: '%s'", id), ex);
            return null;
        }

        final ReportDescriptorImpl result = new ReportDescriptorImpl();
        setProps_RD(result, map, id);
        setSubReports(result, id);

        return result;
    }

    protected void setSubReports(ReportDescriptorImpl result, NodeRef id) {
        result.setSubreports(scanSubreports(id));
    }

    @Override
    public ReportDescriptor getReportDescriptor(String mnemo) {
        final LuceneSearchBuilder builder = new LuceneSearchBuilder();
        builder.emmitFieldCond(null, PROP_T_REPORT_CODE, mnemo);

        final ResultSet rs = LucenePreparedQuery.execFindQuery(builder, getServices().getServiceRegistry().getSearchService());
        final List<Map<QName, Serializable>> descs = LucenePreparedQuery.loadNodeProps(rs, mnemo, 0, 1, getServices().getServiceRegistry().getNodeService());
        if (descs == null || descs.isEmpty()) {
            return null;
        }

        final ReportDescriptorImpl result = new ReportDescriptorImpl();
        setProps_RD(result, descs.get(0), rs.getNodeRef(0));

        return result;
    }

    @Override
    public ReportTemplate getReportTemplate(NodeRef id) {
        return createReportTemplate(id);
    }

    @Override
    public ReportTemplate getReportTemplate(String rtMnemo) {
        final NodeService nodeService = getServices().getServiceRegistry().getNodeService();

        final LuceneSearchBuilder builder = new LuceneSearchBuilder();
        builder.emmitFieldCond(null, "cm:name", rtMnemo);

        final ResultSet rs = LucenePreparedQuery.execFindQuery(builder, getServices().getServiceRegistry().getSearchService());
        final List<Map<QName, Serializable>> descs = LucenePreparedQuery.loadNodeProps(rs, rtMnemo, 0, 1, nodeService);

        if (descs == null || descs.isEmpty()) {
            log.warn(String.format("Not found report file template '%s'", rtMnemo));
            return null;
        }

        final NodeRef templateId = rs.getNodeRef(0);
        return createReportTemplate(templateId);
    }

    /**
     * Вспомогательные функции загрузки данных
     */
    protected Object getObj(Map<QName, Serializable> map, final String propName, final Object defaultValue) {
        final QName qname = QName.createQName(propName, getNamespaceService());
        final Object result = map.get(qname);
        return (result != null) ? result : defaultValue;
    }

    protected String getString(Map<QName, Serializable> map, final String propName, final String defaultValue) {
        final Object result = getObj(map, propName, defaultValue);
        return Utils.coalesce(result, null);
    }

    protected String getString(Map<QName, Serializable> map, final String propName) {
        return getString(map, propName, null);
    }

    protected int getInt(Map<QName, Serializable> map, final String propName, int defaultValue) {
        final Integer x = getInteger(map, propName);
        return (x == null) ? defaultValue : x;
    }

    protected Integer getInteger(Map<QName, Serializable> map, final String propName) {
        return (Integer) getObj(map, propName, null);
    }

    protected Date getDate(Map<QName, Serializable> map, final String propName, Date defaultValue) {
        return (Date) getObj(map, propName, defaultValue);
    }

    protected Date getDate(Map<QName, Serializable> map, final String propName) {
        return getDate(map, propName, null);
    }

    /**
     * @param map          Map<QName, Serializable>
     * @param propName     String
     * @param defaultValue Boolean
     * @return Boolean nullable value
     */
    protected Boolean getBoolean(Map<QName, Serializable> map, final String propName, Boolean defaultValue) {
        return (Boolean) getObj(map, propName, defaultValue);
    }

    /**
     * @param map          Map<QName, Serializable>
     * @param propName     String
     * @param defaultValue Boolean
     * @return none-null boolean value
     */
    protected boolean getBool(Map<QName, Serializable> map, final String propName, boolean defaultValue) {
        return (Boolean) getObj(map, propName, defaultValue);
    }

    protected void setL18Name(L18able result, Map<QName, Serializable> map) {
        setL18Name(result, map, "cm:name");
    }

    protected void setL18Name(L18able result, Map<QName, Serializable> map, String propName) {
        result.regItem(getString(map, "sys:locale", "ru"), getString(map, propName));
    }

    protected void setProps_RD(ReportDescriptorImpl result, Map<QName, Serializable> map, NodeRef node) {
        result.setMnem(getString(map, PROP_T_REPORT_CODE));

        setL18Name(result, map);

        setFlags(result.getFlags(), map);

        final NodeService nodeService = getServices().getServiceRegistry().getNodeService();
        final NamespaceService ns = getNamespaceService();

        result.setReportType(createReportType(LucenePreparedQuery.getAssocTarget(node, ASSOC_REPORT_TYPE, nodeService, ns)));
        result.setProviderDescriptor(createReportProvider(LucenePreparedQuery.getAssocTarget(node, ASSOC_REPORT_PROVIDER, nodeService, ns)));
        result.setReportTemplate(createReportTemplate(LucenePreparedQuery.getAssocTarget(node, ASSOC_REPORT_TEMLATE, nodeService, ns)));

        result.setDSDescriptor(createDSDescriptor(LucenePreparedQuery.getAssocChildByType(node, TYPE_REPORT_DATASOURCE, nodeService, ns)));

        result.setSubReport(getBoolean(map, PROP_T_REPORT_IS_SUB, false));
    }

    protected void setFlags(ReportFlags result, Map<QName, Serializable> map) {
        result.setMnem(null);

        result.setText(getString(map, PROP_T_REPORT_QUERY));
        result.setPreferedNodeType(getString(map, PROP_T_REPORT_DOCTYPE));
        result.setMultiRow(getBool(map, PROP_B_REPORT_MULTIPLICITY, true));

        result.setLimit(getInt(map, PROP_I_REPORT_QUERY_LIMIT, LucenePreparedQuery.QUERYROWS_UNLIMITED));
        result.setOffset(getInt(map, PROP_I_REPORT_QUERY_OFFSET, 0));
        result.setPgSize(getInt(map, PROP_I_REPORT_QUERY_PGSIZE, LucenePreparedQuery.QUERYPG_ALL));

        String customFlags = getString(map, PROP_T_REPORT_FLAGS);
        if (customFlags != null) {
            customFlags = customFlags.replaceAll("\\n", "").replaceAll("\\r","");
            String[] cfStr = customFlags.split(";");
            if (cfStr.length > 0) {
                for (String flagStr : cfStr) {
                    String[] flag = flagStr.split("=");
                    if (flag.length == 2) {
                        result.flags().add(new NamedValueImpl(flag[0], flag[1]));
                    }
                }
            }
        }
    }

    protected ReportType createReportType(NodeRef node) {
        if (node == null) {
            return null;
        }

        final Map<QName, Serializable> map = getServices().getServiceRegistry().getNodeService().getProperties(node);

        final ReportTypeImpl result = new ReportTypeImpl();
        if (map != null) {
            result.setMnem(getString(map, PROP_T_RTYPE_CODE));
            setL18Name(result, map);
        }

        return result;
    }

    protected ReportProviderDescriptorImpl createReportProvider(NodeRef node) {
        if (node == null) {
            return null;
        }

        final ReportProviderDescriptorImpl result = new ReportProviderDescriptorImpl();
        final Map<QName, Serializable> map = getServices().getServiceRegistry().getNodeService().getProperties(node);
        if (map != null) {
            result.setMnem(getString(map, PROP_T_RPROVIDER_CODE));
            setL18Name(result, map);
            result.setClassName(getString(map, PROP_T_RPROVIDER_CLASS));
        }
        return result;
    }

    protected ReportTemplateImpl createReportTemplate(NodeRef node) {
        if (node == null) {
            return null;
        }

        final NodeService nodeService = getServices().getServiceRegistry().getNodeService();

        final ReportTemplateImpl result = new ReportTemplateImpl();
        setProps_ReportTemplate(result, nodeService.getProperties(node), node);
        return result;
    }

    protected void setProps_ReportTemplate(ReportTemplate result, final Map<QName, Serializable> map, NodeRef node) {
        final NodeService nodeService = getServices().getServiceRegistry().getNodeService();
        final NamespaceService ns = getNamespaceService();

        result.setMnem(getString(map, "cm:name"));
        setL18Name(result, map);

        final NodeRef nodeCntntFile = LucenePreparedQuery.getAssocTarget(node, ASSOC_RTEMPLATE_FILE, nodeService, ns);
        if (nodeCntntFile != null) {
            final Map<QName, Serializable> mapCntntFile = nodeService.getProperties(nodeCntntFile);
            result.setFileName(getString(mapCntntFile, "cm:name"));

            final ContentReader reader = services.getServiceRegistry().getContentService().getReader(nodeCntntFile, ContentModel.PROP_CONTENT);
            try {
                final byte[] data = (reader != null && reader.getSize() > 0) ? IOUtils.toByteArray(reader.getContentInputStream()) : null;
                result.setData((data == null) ? null : new ByteArrayInputStream(data));
            } catch (IOException ex) {
                final String msg = String.format("Error getting file content of node {%s}", node);
                throw new RuntimeException(msg, ex);
            }
        }
    }

    protected DataSourceDescriptorImpl createDSDescriptor(NodeRef node) {
        if (node == null) {
            return null;
        }
        final DataSourceDescriptorImpl result = new DataSourceDescriptorImpl();
        setProps_DSDescriptor(result, node);
        return result;
    }

    protected void setProps_DSDescriptor(DataSourceDescriptor result, NodeRef node) {
        if (node == null) {
            return;
        }

        final NodeService nodeService = getServices().getServiceRegistry().getNodeService();
        final NamespaceService ns = getNamespaceService();

        final Map<QName, Serializable> map = nodeService.getProperties(node);
        if (map != null) {
            result.setMnem(getString(map, PROP_T_RDS_CODE));
            setL18Name(result, map);

            final List<NodeRef> found = LucenePreparedQuery.getAssocChildrenByType(node, TYPE_RDS_COLUMN, nodeService, ns);
            setProps_ListColumns(result.getColumns(), found);
        }
    }

    public ColumnDescriptor createColumnDescriptor(NodeRef node) {
        if (node == null) {
            return null;
        }
        final ColumnDescriptorImpl result = new ColumnDescriptorImpl();
        setProps_DataColumn(result, node);
        return result;
    }

    protected void setProps_DataColumn(ColumnDescriptor result, NodeRef node) {
        if (node == null) {
            return;
        }

        final NodeService nodeSrv = getServices().getServiceRegistry().getNodeService();
        final NamespaceService ns = getNamespaceService();

        final Map<QName, Serializable> map = nodeSrv.getProperties(node);

        result.setColumnName(getString(map, PROP_T_RDS_COLUMN_CODE));
        setL18Name(result, map);

        result.setExpression(getString(map, PROP_T_RDS_COLUMN_EXPR));
        result.setOrder(getInt(map, PROP_T_RDS_COLUMN_ORDER, 0));
        result.setAlfrescoType(getString(map, PROP_T_RDS_COLUMN_CLASS));

        // TODO: result.setSpecial( getBool(map, PROP_T_RDS_ISSPECIAL, false));

        // тип колонки ...

        final NodeRef nodeColType = LucenePreparedQuery.getAssocTarget(node, ASSOC_RDS_COLUMN_TYPE, nodeSrv, ns);
        final JavaDataType jdt = createColDataType(nodeColType);
        result.setDataType(jdt);

        // тип параметра для колонки ...

        final NodeRef nodeColParType = LucenePreparedQuery.getAssocTarget(node, ASSOC_RDS_COLUMN_PARAMTYPE, nodeSrv, ns);
        final ParameterTypedValue paramValue = createParameterTypeValue(nodeColParType);
        result.setParameterValue(paramValue);
    }

    private JavaDataType createColDataType(NodeRef nodeColType) {
        if (nodeColType == null) {
            return null;
        }

        final Map<QName, Serializable> map = getServices().getServiceRegistry().getNodeService().getProperties(nodeColType);

        final String clazzName = getString(map, PROP_T_RDS_COLTYPE_CLASS);
        final String name = getString(map, "cm:name");
        final String code = getString(map, PROP_T_RDS_COLTYPE_CODE);

        // если есть класс - по классу, потом по имени, потом по по мнемонике
        final String tag = Utils.coalesce(clazzName, name, code);
        try {
            return SupportedTypes.findType(tag).javaDataType();
        } catch (Exception ex) {
            final String msg = String.format("Unsupported column data class '%s' due to error: %s", tag, ex.getMessage());
            log.error(msg, ex);
            throw new RuntimeException(msg, ex);
        }
    }

    private ParameterTypedValue createParameterTypeValue(NodeRef nodeColParType) {
        if (nodeColParType == null) {
            return null;
        }

        final Map<QName, Serializable> map = getServices().getServiceRegistry().getNodeService().getProperties(nodeColParType);

        final String mnem = getString(map, PROP_T_RDS_PARTYPE_CODE);
        final ParameterTypedValueImpl result = new ParameterTypedValueImpl(mnem);

        setL18Name(result, map);
        setL18Name(result.getPrompt1(), map, PROP_T_RDS_PARTYPE_LABEL1);
        setL18Name(result.getPrompt2(), map, PROP_T_RDS_PARTYPE_LABEL2);

        final String tagParType = getString(map, PROP_T_RDS_PARTYPE_CODE);
        if (tagParType != null) {
            final ParameterTypedValue.Type atype = ParameterTypedValue.Type.findType(tagParType);
            if (atype == null) {
                final String msg = String.format("Unsupported column parameter type '%s'", tagParType);
                log.error(msg);
                throw new RuntimeException(msg);
            }
            result.setType(atype);
        }


        // TODO: (tag ALF_TYPES) Получение типа ассоциации альфреско и типа данных Альфреско
        /*
		const : String АТРИБУТ_С_ТИПОМ_АЛЬФРЕСКО = "", АТРИБУТ_С_ТИПОМ_АССОЦИАЦИИ_АЛЬФРЕСКО = "", АТРИБУТ_С_ВИДОМ_АССОЦИАЦИИ_АЛЬФРЕСКО = "";

		result.setAlfrescoType( getString(map, АТРИБУТ_С_ТИПОМ_АЛЬФРЕСКО)); 

		{ // тип ассоциации Альфреско ...
			final String typeOfAssoc = getString(map, АТРИБУТ_С_ТИПОМ_АССОЦИАЦИИ_АЛЬФРЕСКО);
			if (typeOfAssoc != null) {
				final AlfrescoAssocInfoImpl assoc = new AlfrescoAssocInfoImpl();
				assoc.setAssocTypeName( typeOfAssoc );

				final String kindOfAssoc = getString(map, АТРИБУТ_С_ВИДОМ_АССОЦИАЦИИ_АЛЬФРЕСКО); // 11, 1M ...
				assoc.setAssocKind(AssocKind.findAssocKind(kindOfAssoc));
			}
		}
		 */

        return result;
    }

    /**
     * @param result List<ColumnDescriptor>
     * @param list   список узлов типа "lecm-rpeditor:reportDataColumn"
     */
    private void setProps_ListColumns(List<ColumnDescriptor> result, List<NodeRef> list) {
        if (result == null || list == null) {
            return;
        }
        for (NodeRef colRef : list) {
            final ColumnDescriptor coldesc = createColumnDescriptor(colRef);
            if (coldesc != null) {
                result.add(coldesc);
            }
        }
    }

    @Override
    public NodeRef getReportDescriptorNodeByCode(String rtMnemo) {
        LuceneSearchBuilder builder = new LuceneSearchBuilder(getNamespaceService());
        builder.emmitFieldCond(null, PROP_T_REPORT_CODE, rtMnemo);
        builder.emmitTypeCond(TYPE_ReportDescriptor, null);

        ResultSet rs = LucenePreparedQuery.execFindQuery(builder, getServices().getServiceRegistry().getSearchService());
        for (ResultSetRow row : rs) {
            return row.getNodeRef();
        }
        return null;
    }

    public NamespaceService getNamespaceService() {
        return getServices().getServiceRegistry().getNamespaceService();
    }

    public NodeService getNodeService() {
        return getServices().getServiceRegistry().getNodeService();
    }

    public List<SubReportDescriptor> scanSubreports(NodeRef mainReport) {
        if (mainReport == null) {
            return null;
        }

        final Set<SubReportDescriptorImpl> subReports = new LinkedHashSet<SubReportDescriptorImpl>();

        NodeService nodeService = getNodeService();

        Set<QName> descriptors = new HashSet<QName>();
        descriptors.add(QName.createQName(TYPE_ReportDescriptor, getNamespaceService()));

        List<ChildAssociationRef> childDescriptorsList = nodeService.getChildAssocs(mainReport, descriptors);
        for (ChildAssociationRef childAssociationRef : childDescriptorsList) {
            NodeRef subReport = childAssociationRef.getChildRef();
            ReportDescriptor subreportDesc = getReportDescriptor(subReport);

            final SubReportDescriptorImpl sr = transferReportToSubReport(subreportDesc);
            if (sr != null) {
                subReports.add(sr);
            }
        }

        return (subReports.isEmpty()) ? null : new ArrayList<SubReportDescriptor>(subReports);
    }

    private SubReportDescriptorImpl transferReportToSubReport(ReportDescriptor subreportDesc) {
        final String reportName = subreportDesc.getMnem();

        // !) Создание ообъекта подотчёта
        final SubReportDescriptorImpl srResult = new SubReportDescriptorImpl(subreportDesc);
        srResult.setDestColumnName(reportName); // целевая колонка - это главная колонка отчёта

        // источник данных для вложенного списка полей должен быть указан как query
        String sourceLink = subreportDesc.getFlags().getText();
        if (Utils.isStringEmpty(sourceLink)) {
            //TODO точка расширения получения источника для подотчета
        }

        srResult.setSourceListExpression(sourceLink);

        // тип данных для вложенного списка полей должен быть указан в поле Использовать для типов
        List<String> sourceTypes = subreportDesc.getFlags().getSupportedNodeTypes();
        srResult.setSourceListType(new HashSet<String>(sourceTypes));

        // TODO: + beanClass, format, ifEmpty, delimiter
        Map<String, String> customFlags = subreportDesc.getFlags().getFlagsMap();
        if (customFlags != null && customFlags.size() > 0) {
            String beanClass = customFlags.get("beanClass");
            if (beanClass != null) {
                srResult.setBeanClassName(beanClass);
            }

            ItemsFormatDescriptorImpl formatDesc = new ItemsFormatDescriptorImpl();

            String format = customFlags.get("format");
            if (format != null) {
                formatDesc.setFormatString(format);
            }

            String ifEmpty = customFlags.get("ifEmpty");
            if (ifEmpty != null) {
                formatDesc.setIfEmptyTag(ifEmpty);
            }

            String delimiter = customFlags.get("delimiter");
            if (delimiter != null) {
                formatDesc.setItemsDelimiter(delimiter);
            }

            srResult.setItemsFormat(formatDesc);
        }

        for (ColumnDescriptor subreportColumn : srResult.getDsDescriptor().getColumns()) {
            //TODO сюда можно добавить обработку каких-то "особых" столбцов

            /* обновление/формирование sourceMap для subreport */
            if (srResult.getSubItemsSourceMap() == null) {
                srResult.setSubItemsSourceMap(new HashMap<String, String>());
            }
            srResult.getSubItemsSourceMap().put(subreportColumn.getColumnName(), subreportColumn.getExpression());
        }

        return srResult;
    }
}
