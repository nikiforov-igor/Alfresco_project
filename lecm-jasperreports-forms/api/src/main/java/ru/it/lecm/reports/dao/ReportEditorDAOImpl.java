package ru.it.lecm.reports.dao;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.reports.api.model.DAO.ReportEditorDAO;
import ru.it.lecm.reports.api.model.L18able;
import ru.it.lecm.reports.api.model.ParameterTypedValue;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.beans.WKServiceKeeper;
import ru.it.lecm.reports.generators.LucenePreparedQueryHelper;
import ru.it.lecm.reports.model.impl.*;
import ru.it.lecm.reports.model.impl.JavaDataType.SupportedTypes;
import ru.it.lecm.reports.utils.Utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.springframework.context.ApplicationEvent;

public class ReportEditorDAOImpl extends BaseBean implements ReportEditorDAO {

    private static final transient Logger log = LoggerFactory.getLogger(ReportEditorDAOImpl.class);

    private WKServiceKeeper services;

    public WKServiceKeeper getServices() {
        return services;
    }

    public void setServices(WKServiceKeeper services) {
        this.services = services;
    }

    @Override
    public ReportDescriptor getReportDescriptor(NodeRef id) {
        return getReportDescriptor(id, false);
    }

    public NodeRef getReportsRootFolder() {
        return getFolder("RE_REPORTS_ROOT_ID");
    }

    @Override
    public ReportDescriptor getReportDescriptor(NodeRef node, boolean withoutSubs) {
        boolean isSubReport = getNodeService().getType(node).equals(TYPE_SUB_REPORT_DESCRIPTOR);
        ReportDescriptor result = !isSubReport ? new ReportDescriptorImpl() : new SubReportDescriptorImpl();

        final NodeService nodeService = getNodeService();

        result.setMnem((String) nodeService.getProperty(node, PROP_REPORT_CODE));
        setL18Name(result, node);
        setFlags(result.getFlags(), node);

        // провайдер
        List<AssociationRef> provideRefs = nodeService.getTargetAssocs(node, ASSOC_REPORT_PROVIDER);
        if (!provideRefs.isEmpty()) {
            NodeRef provider = provideRefs.get(0).getTargetRef();
            result.setProviderDescriptor(createReportProvider(provider));
        }

        //шаблон(ы)
        List<AssociationRef> templatesRefs = nodeService.getTargetAssocs(node, ASSOC_REPORT_TEMLATE);
        List<ReportTemplate> templates = new ArrayList<ReportTemplate>();
        for (AssociationRef template : templatesRefs) {
            templates.add(createReportTemplate(template.getTargetRef()));
        }
        result.setReportTemplates(templates);

        //бизнес-роли
        List<AssociationRef> rolesRefs = nodeService.getTargetAssocs(node, ASSOC_REPORT_ROLES);
        Set<String> rolesSet = new HashSet<String>();
        for (AssociationRef role : rolesRefs) {
            String id = (String) nodeService.getProperty(role.getTargetRef(), OrgstructureBean.PROP_BUSINESS_ROLE_IDENTIFIER);
            rolesSet.add(id);
        }
        result.setBusinessRoles(rolesSet);

        //источник данных
        Set<QName> ds = new HashSet<QName>();
        ds.add(TYPE_REPORT_DATASOURCE);
        List<ChildAssociationRef> dsChilds = nodeService.getChildAssocs(node, ds);
        if (!dsChilds.isEmpty()) {
            result.setDSDescriptor(createDSDescriptor(dsChilds.get(0).getChildRef()));
        }

        //кастомные свойства для подотчета
        if (result.isSubReport()) {
            SubReportDescriptorImpl subResult = (SubReportDescriptorImpl) result;
            final String reportName = result.getMnem();
            subResult.setDestColumnName(reportName); // целевая колонка - это главная колонка отчёта

            // источник данных для вложенного списка полей должен быть указан как query
            subResult.setSourceListExpression(result.getFlags().getText());

            List<AssociationRef> parentTempRefAssoc = getNodeService().getTargetAssocs(node, ASSOC_PARENT_TEMPLATE_ASSOC);
            if (!parentTempRefAssoc.isEmpty()) {
                NodeRef parentTemplate = parentTempRefAssoc.get(0).getTargetRef();
                subResult.setParentTemplate(createReportTemplate(parentTemplate));
            }
            //установим родителя
            NodeRef parentReport = getNodeService().getPrimaryParent(node).getParentRef();
            ReportDescriptor parent = getReportDescriptor(parentReport, true); // не включаем подочтеты сюда! нужен только "макет основного отчета"
            subResult.setOwnerReport(parent);
        }

        if (!withoutSubs) {
            setSubReports(result, node);
        }

        return result;
    }

    protected void setSubReports(ReportDescriptor result, NodeRef id) {
        result.setSubreports(scanSubreports(id));
    }

    @Override
    public ReportTemplate getReportTemplate(NodeRef id) {
        return createReportTemplate(id);
    }

    /**
     * Вспомогательные функции загрузки данных
     */
    protected Object getObj(Map<QName, Serializable> map, final String propName, final Object defaultValue) {
        final QName qname = QName.createQName(propName, getNamespaceService());
        final Object result = map.get(qname);
        return (result != null) ? result : defaultValue;
    }

    protected Date getDate(Map<QName, Serializable> map, final String propName, Date defaultValue) {
        return (Date) getObj(map, propName, defaultValue);
    }

    protected Date getDate(Map<QName, Serializable> map, final String propName) {
        return getDate(map, propName, null);
    }

    protected Boolean getBoolean(NodeRef node, final QName propName, Boolean defaultValue) {
        Object value = getNodeService().getProperty(node, propName);
        if (value != null && value instanceof Boolean) {
            return (Boolean) value;
        } else {
            return defaultValue;
        }
    }

    protected String getString(NodeRef node, final QName propName, String defaultValue) {
        Object value = getNodeService().getProperty(node, propName);
        if (value != null && value instanceof String) {
            return (String) value;
        } else {
            return defaultValue;
        }
    }

    protected String getString(NodeRef node, final QName propName) {
        return getString(node, propName, null);
    }

    protected Integer getInteger(NodeRef node, final QName propName, Integer defaultValue) {
        Object value = getNodeService().getProperty(node, propName);
        if (value != null && value instanceof Integer) {
            return (Integer) value;
        } else {
            return defaultValue;
        }
    }

    protected Integer getInteger(NodeRef node, final QName propName) {
        return getInteger(node, propName, 0);
    }

    protected void setL18Name(L18able result, NodeRef node) {
        setL18Name(result, node, ContentModel.PROP_NAME);
    }

    protected void setL18Name(L18able result, NodeRef node, QName propName) {
        NodeService nodeService = getNodeService();
        result.regItem(nodeService.getProperty(node, ContentModel.PROP_LOCALE).toString(), getString(node, propName, null));
    }

    protected void setFlags(ReportFlags result, NodeRef node) {
        result.setMnem(null);

        result.setText(getString(node, PROP_REPORT_QUERY));

        result.setSort(getString(node, PROP_REPORT_QUERY_SORT));

        result.setPreferedNodeType(getString(node, PROP_REPORT_DOCTYPE));
        result.setMultiRow(getBoolean(node, PROP_REPORT_MULTIPLICITY, true));
        result.setRunAsSystem(getBoolean(node, PROP_REPORT_RUN_AS_SYSTEM, true));
        result.setIncludeAllOrganizations(getBoolean(node, PROP_REPORT_INCLUDE_ALL_ORGS, false));
        result.setLoadColumnsFromSQLQuery(getBoolean(node, PROP_REPORT_LOAD_COLUMNS_FROM_SQL, false));

        result.setLimit(getInteger(node, PROP_REPORT_QUERY_LIMIT));
        result.setOffset(getInteger(node, PROP_REPORT_QUERY_OFFSET, 0));
        result.setPgSize(getInteger(node, PROP_REPORT_QUERY_PGSIZE, LucenePreparedQueryHelper.QUERYPG_ALL));

        String customFlags = getString(node, PROP_T_REPORT_FLAGS);
        if (customFlags != null) {
            customFlags = customFlags.replaceAll("\\n", "").replaceAll("\\r", "");
            String[] cfStr = customFlags.split(";");
            if (cfStr.length > 0) {
                for (String flagStr : cfStr) {
                    String[] flag = flagStr.split("=");
                    if (flag.length == 2) {
                        result.flags().add(new NamedValue(flag[0], flag[1]));
                    }
                }
            }
        }
    }

    protected ReportType createReportType(NodeRef reportTypeNode) {
        if (reportTypeNode == null) {
            return null;
        }
        NodeService nodeService = getNodeService();
        ReportType result = new ReportType();

        result.setMnem((String) nodeService.getProperty(reportTypeNode, PROP_RTYPE_CODE));
        setL18Name(result, reportTypeNode);

        return result;
    }

    protected ReportProviderDescriptor createReportProvider(NodeRef providerNode) {
        if (providerNode == null) {
            return null;
        }

        NodeService nodeService = getNodeService();

        final ReportProviderDescriptor result = new ReportProviderDescriptor();

        result.setMnem((String) nodeService.getProperty(providerNode, PROP_RPROVIDER_CODE));
        setL18Name(result, providerNode);
        result.setClassName((String) nodeService.getProperty(providerNode, PROP_RPROVIDER_CLASS));

        return result;
    }

    protected ReportTemplate createReportTemplate(NodeRef node) {
        if (node == null) {
            return null;
        }

        final ReportTemplate result = new ReportTemplate();

        NodeService nodeService = getNodeService();

        Object code = nodeService.getProperty(node, PROP_RTEMPLATE_CODE);
        result.setMnem(code != null ? (String) code : "NOT_CODE");

        setL18Name(result, node);

        List<AssociationRef> filesRef = nodeService.getTargetAssocs(node, ASSOC_RTEMPLATE_FILE);
        if (!filesRef.isEmpty()) {
            NodeRef templateFile = filesRef.get(0).getTargetRef();
            if (templateFile != null) {
                result.setFileName((String) nodeService.getProperty(templateFile, ContentModel.PROP_NAME));

                final ContentReader reader = services.getServiceRegistry().getContentService().getReader(templateFile, ContentModel.PROP_CONTENT);
                try {
                    final byte[] data = (reader != null && reader.getSize() > 0) ? IOUtils.toByteArray(reader.getContentInputStream()) : null;
                    result.setData((data == null) ? null : new ByteArrayInputStream(data));
                } catch (IOException ex) {
                    throw new RuntimeException("Error getting file content of node" + node, ex);
                }
            }
        }

        List<AssociationRef> typesRef = nodeService.getTargetAssocs(node, ASSOC_RTEMPLATE_TYPE);
        if (!typesRef.isEmpty()) {
            NodeRef templateType = typesRef.get(0).getTargetRef();
            if (templateType != null) {
                result.setReportType(createReportType(templateType));
            }
        }

        return result;
    }

    protected DataSourceDescriptorImpl createDSDescriptor(NodeRef dsNode) {
        if (dsNode == null) {
            return null;
        }
        final DataSourceDescriptorImpl result = new DataSourceDescriptorImpl();


        result.setMnem((String) getNodeService().getProperty(dsNode, PROP_REPORT_DATASOURSE_CODE));
        setL18Name(result, dsNode);

        Set<QName> rdsColumn = new HashSet<QName>();
        rdsColumn.add(TYPE_RDS_COLUMN);

        List<ChildAssociationRef> columns = getNodeService().getChildAssocs(dsNode, rdsColumn);
        if (!columns.isEmpty()) {
            for (ChildAssociationRef column : columns) {
                final ColumnDescriptor coldesc = createColumnDescriptor(column.getChildRef());
                result.getColumns().add(coldesc);
            }
        }
        return result;
    }

    public ColumnDescriptor createColumnDescriptor(NodeRef node) {
        if (node == null) {
            return null;
        }
        final ColumnDescriptor result = new ColumnDescriptor();

        result.setColumnName((String) getNodeService().getProperty(node, PROP_RDS_COLUMN_CODE));
        setL18Name(result, node);

        Object exprValue = getNodeService().getProperty(node, PROP_RDS_COLUMN_EXPR);
        if (exprValue != null) {
            result.setExpression(exprValue.toString());
        }

        Object orderValue = getNodeService().getProperty(node, PROP_RDS_COLUMN_ORDER);
        if (orderValue != null) {
            result.setOrder(Integer.parseInt(orderValue.toString()));
        } else {
            result.setOrder(0);
        }

        Object alfTypeValue = getNodeService().getProperty(node, PROP_RDS_COLUMN_CLASS);
        if (alfTypeValue != null) {
            result.setAlfrescoType(alfTypeValue.toString());
        }

        Object columnParams = getNodeService().getProperty(node, PROP_RDS_COLUMN_CONTROL_PARAMS);
        if (columnParams != null) {
            result.setControlParams(columnParams.toString());
        }

        // тип колонки ...
        List<AssociationRef> colTypesRefs = getNodeService().getTargetAssocs(node, ASSOC_RDS_COLUMN_TYPE);
        if (!colTypesRefs.isEmpty()) {
            NodeRef colType = colTypesRefs.get(0).getTargetRef();
            result.setDataType(createColDataType(colType));
        }

        // тип параметра для колонки ...
        List<AssociationRef> colParsRefs = getNodeService().getTargetAssocs(node, ASSOC_RDS_COLUMN_PARAMTYPE);
        if (!colParsRefs.isEmpty()) {
            NodeRef parType = colParsRefs.get(0).getTargetRef();
            result.setParameterValue(createParameterTypeValue(node, parType));
        }

        return result;
    }

    @Override
    public void markAsDeployed(NodeRef reportId) {
        serviceRegistry.getNodeService().setProperty(reportId, ReportEditorDAO.PROP_REPORT_DESCRIPTOR_IS_DEPLOYED, true);
    }

    private JavaDataType createColDataType(NodeRef nodeColType) {
        if (nodeColType == null) {
            return null;
        }

        final String clazzName = (String) getNodeService().getProperty(nodeColType, PROP_RDS_COLTYPE_CLASS);
        final String name = (String) getNodeService().getProperty(nodeColType, ContentModel.PROP_NAME);
        final String code = (String) getNodeService().getProperty(nodeColType, PROP_RDS_COLTYPE_CODE);

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

    private ParameterTypedValue createParameterTypeValue(NodeRef columnRef, NodeRef nodeColParType) {
        if (nodeColParType == null) {
            return null;
        }

        final String mnem = (String) getNodeService().getProperty(nodeColParType, PROP_T_RDS_PARTYPE_CODE);
        final ParameterTypedValueImpl result = new ParameterTypedValueImpl(mnem);

        setL18Name(result, nodeColParType);
        setL18Name(result.getPrompt1(), nodeColParType, PROP_T_RDS_PARTYPE_LABEL1);
        setL18Name(result.getPrompt2(), nodeColParType, PROP_T_RDS_PARTYPE_LABEL2);

        final String tagParType = getString(nodeColParType, PROP_T_RDS_PARTYPE_CODE);
        if (tagParType != null) {
            final ParameterTypedValue.Type atype = ParameterTypedValue.Type.findType(tagParType);
            if (atype == null) {
                final String msg = String.format("Unsupported column parameter type '%s'", tagParType);
                log.error(msg);
                throw new RuntimeException(msg);
            }
            result.setType(atype);
        }


        Object mandatoryValue = getNodeService().getProperty(columnRef, PROP_RDS_COLUMN_MANDATORY);
        if (mandatoryValue != null) {
            result.setRequired(Boolean.valueOf(mandatoryValue.toString()));
        } else {
            result.setRequired(false);
        }
        return result;
    }

    @Override
    public NodeRef getReportDescriptorNodeByCode(String rtMnemo) {
        if (rtMnemo != null && !rtMnemo.isEmpty()) {
            Set<QName> types = new HashSet<QName>();
            types.add(TYPE_REPORT_DESCRIPTOR);
            List<ChildAssociationRef> reports = getNodeService().getChildAssocs(getReportsRootFolder(), types);
            for (ChildAssociationRef report : reports) {
                String reportCode = (String) getNodeService().getProperty(report.getChildRef(), PROP_REPORT_CODE);
                if (reportCode.equals(rtMnemo)) {
                    return report.getChildRef();
                }
            }
        }
        return null;
    }

    public NamespaceService getNamespaceService() {
        return getServices().getServiceRegistry().getNamespaceService();
    }

    public NodeService getNodeService() {
        return getServices().getServiceRegistry().getNodeService();
    }

    @Override
    public NodeRef getServiceRootFolder() {
        return getFolder("RE_ROOT_ID");
    }

    public List<ReportDescriptor> scanSubreports(NodeRef mainReport) {
        if (mainReport == null) {
            return null;
        }

        final Set<ReportDescriptor> subReports = new LinkedHashSet<ReportDescriptor>();

        NodeService nodeService = getNodeService();

        Set<QName> descriptors = new HashSet<QName>();
        descriptors.add(TYPE_SUB_REPORT_DESCRIPTOR);

        List<ChildAssociationRef> childDescriptorsList = nodeService.getChildAssocs(mainReport, descriptors);
        for (ChildAssociationRef childAssociationRef : childDescriptorsList) {
            NodeRef subReport = childAssociationRef.getChildRef();
            ReportDescriptor subreportDesc = getReportDescriptor(subReport);
            if (subreportDesc != null) {
                subReports.add(subreportDesc);
            }
        }
        return (subReports.isEmpty()) ? null : new ArrayList<ReportDescriptor>(subReports);
    }

}
