package ru.it.lecm.reports.model.DAO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.reports.api.model.ColumnDescriptor;
import ru.it.lecm.reports.api.model.DataSourceDescriptor;
import ru.it.lecm.reports.api.model.JavaDataType;
import ru.it.lecm.reports.api.model.L18able;
import ru.it.lecm.reports.api.model.ParameterTypedValue;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.api.model.ReportFlags;
import ru.it.lecm.reports.api.model.ReportTemplate;
import ru.it.lecm.reports.api.model.ReportType;
import ru.it.lecm.reports.api.model.DAO.ReportEditorDAO;
import ru.it.lecm.reports.beans.WKServiceKeeper;
import ru.it.lecm.reports.generators.LucenePreparedQuery;
import ru.it.lecm.reports.model.impl.ColumnDescriptorImpl;
import ru.it.lecm.reports.model.impl.DataSourceDescriptorImpl;
import ru.it.lecm.reports.model.impl.JavaDataTypeImpl.SupportedTypes;
import ru.it.lecm.reports.model.impl.ParameterTypedValueImpl;
import ru.it.lecm.reports.model.impl.ReportDescriptorImpl;
import ru.it.lecm.reports.model.impl.ReportProviderDescriptorImpl;
import ru.it.lecm.reports.model.impl.ReportTemplateImpl;
import ru.it.lecm.reports.model.impl.ReportTypeImpl;
import ru.it.lecm.reports.utils.Utils;
import ru.it.lecm.utils.LuceneSearchBuilder;

public class ReportDAOImpl implements ReportEditorDAO {

	private static final transient Logger log = LoggerFactory.getLogger(ReportDAOImpl.class);

	private WKServiceKeeper services;
	private NamespaceService namespaceService;

	public void init() {
		log.info("initialized "+ this.getClass());
	}

	public WKServiceKeeper getServices() {
		return services;
	}

	public void setServices(WKServiceKeeper services) {
		this.services = services;
	}

	@Override
	public ReportDescriptor getReportDescriptor(NodeRef id) {
		PropertyCheck.mandatory (this, "services", services);

//		final ResultSet rs = LucenePreparedQuery.execFindQueryByNodeRef(id.toString(), getServices().getServiceRegistry().getSearchService());
//		final List<Map<QName, Serializable>> descs = LucenePreparedQuery.loadNodeProps( rs, getServices().getServiceRegistry().getNodeService());
//		if (descs == null || descs.isEmpty())
//			return null;
		final Map<QName, Serializable> map;
		try {
			map = getServices().getServiceRegistry().getNodeService().getProperties(id);
		} catch(InvalidNodeRefException ex) {
			log.warn( String.format( "ReportDescriptor node not found: '%s'", id), ex);
			return null;
		}

		final ReportDescriptorImpl result = new ReportDescriptorImpl();
		setProps_RD( result, map, id);

		return result;
	}

	@Override
	public ReportDescriptor getReportDescriptor(String mnemo) {
		PropertyCheck.mandatory (this, "services", services);

		final LuceneSearchBuilder builder = new LuceneSearchBuilder();
		builder.emmitFieldCond(null, PROP_T_REPORT_CODE, mnemo);

		final ResultSet rs = LucenePreparedQuery.execFindQuery(builder, getServices().getServiceRegistry().getSearchService());
		final List<Map<QName, Serializable>> descs = LucenePreparedQuery.loadNodeProps( rs, mnemo, 0, 1, getServices().getServiceRegistry().getNodeService());
		if (descs == null || descs.isEmpty())
			return null; 

		final ReportDescriptorImpl result = new ReportDescriptorImpl();
		setProps_RD( result, descs.get(0), rs.getNodeRef(0));

		return result;
	}

	@Override
	public ReportTemplate getReportTemplate(NodeRef id) {
		return createReportTemplate(id);
	}

	@Override
	public ReportTemplate getReportTemplate(String rtMnemo) {
		PropertyCheck.mandatory (this, "services", services);

		final NodeService nodeService =  getServices().getServiceRegistry().getNodeService();

		// final String dsFileName = "ds-"+ reportName + ".xml";

		final LuceneSearchBuilder builder = new LuceneSearchBuilder();
		builder.emmitFieldCond(null, "cm:name", rtMnemo);

		final ResultSet rs = LucenePreparedQuery.execFindQuery(builder, getServices().getServiceRegistry().getSearchService());
		final List<Map<QName, Serializable>> descs = LucenePreparedQuery.loadNodeProps( rs, rtMnemo, 0, 1, nodeService);

		if (descs == null || descs.isEmpty()) {
			log.warn( String.format( "Not found report file template '%s'", rtMnemo));
			return null; 
		}

		final NodeRef templateId = rs.getNodeRef(0);

		/* search by template file name (like *.jrxml):
		// если найден только файл шаблона, переходим к его родителю - описателю шаблона ...
		final NodeRef fileNodeId = rs.getNodeRef(0);
		final ChildAssociationRef parent = nodeService.getPrimaryParent(fileNodeId);
		if (parent == null)
			throw new RuntimeException( String.format( "Not found parent for ds template file '%s'", dsFileName));
		final NodeRef templateId = parent.getParentRef();
		 */

		return createReportTemplate( templateId);
	}

	/**
	 * Вспомогательные функции загрузки данных
	 */
	protected Object getObj( Map<QName, Serializable> map, final String propName, final Object defaultValue) {
		final QName qname = QName.createQName(propName, getServices().getServiceRegistry().getNamespaceService());
		final Object result = map.get(qname);
		return (result != null) ? result : defaultValue;
	}

	protected String getString(Map<QName, Serializable> map, final String propName, final String defaultValue) {
		final Object result = getObj( map, propName, defaultValue);
		return Utils.coalesce( result, null) ;
	}

	protected String getString(Map<QName, Serializable> map, final String propName) {
		return getString( map, propName, null);
	}

	protected int getInt(Map<QName, Serializable> map, final String propName, int defaultValue) {
		final Integer x = getInteger(map, propName);
		return (x == null) ? defaultValue : x.intValue(); 
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
	 * @param map
	 * @param propName
	 * @param defaultValue
	 * @return Boolean nullable value
	 */
	protected Boolean getBoolean(Map<QName, Serializable> map, final String propName, Boolean defaultValue) {
		return (Boolean) getObj(map, propName, defaultValue);
	}

	/**
	 * @param map
	 * @param propName
	 * @param defaultValue
	 * @return none-null boolean value
	 */
	protected boolean getBool(Map<QName, Serializable> map, final String propName, boolean defaultValue) {
		return (Boolean) getObj(map, propName, defaultValue);
	}

	protected void setL18Name( L18able result, Map<QName, Serializable> map) {
		setL18Name(result, map, "cm:name");
	}

	protected void setL18Name( L18able result, Map<QName, Serializable> map, String propName) {
		result.regItem( getString(map, "sys:locale", "ru"), getString( map, propName));
	}

	protected void setProps_RD( ReportDescriptorImpl result, Map<QName, Serializable> map, NodeRef node) {
		result.setMnem( getString( map, PROP_T_REPORT_CODE));

		setL18Name(result, map);

		setProps_Query( result.getFlags(), map);

		final NodeService nodeService = getServices().getServiceRegistry().getNodeService();
		final NamespaceService ns = getServices().getServiceRegistry().getNamespaceService();

		result.setReportType( createReportType( LucenePreparedQuery.getAssocTarget( node, ASSOC_REPORT_TYPE, nodeService, ns)));
		result.setProviderDescriptor( createReportProvider( LucenePreparedQuery.getAssocTarget( node, ASSOC_REPORT_PROVIDER, nodeService, ns)) );
		result.setReportTemplate( createReportTemplate( LucenePreparedQuery.getAssocTarget( node, ASSOC_REPORT_TEMLATE, nodeService, ns)) );

		result.setDSDescriptor( createDSDescriptor( LucenePreparedQuery.getAssocChildByType( node, TYPE_REPORT_DATASOURCE, nodeService, ns)) );
	}

	protected void setProps_Query(ReportFlags result, Map<QName, Serializable> map) {
		result.setMnem( null);

		result.setText( getString(map, PROP_T_REPORT_QUERY));
		result.setPreferedNodeType( getString(map, PROP_T_REPORT_DOCTYPE));
		result.setMultiRow( getBool(map, PROP_B_REPORT_MULTIPLICITY, true));

		result.setLimit( getInt(map, PROP_I_REPORT_QUERY_LIMIT, LucenePreparedQuery.QUERYROWS_UNLIMITED));
		result.setOffset( getInt(map, PROP_I_REPORT_QUERY_OFFSET, 0));
		result.setPgSize( getInt(map, PROP_I_REPORT_QUERY_PGSIZE, LucenePreparedQuery.QUERYPG_ALL));
	}

//	protected void setProps_Mnemo( MnemonicNamedItem result, final Map<QName, Serializable> map, String propMnemo) {
//		if (map == null)
//			return;
//		result.setMnem( getString( map, Utils.coalesce( propMnemo, "cm:name") ));
//		setL18Name(result, map);
//	}

	protected ReportType createReportType( NodeRef node) {
		if (node == null)
			return null;

		PropertyCheck.mandatory (this, "services", services);

		final Map<QName, Serializable> map = getServices().getServiceRegistry().getNodeService().getProperties(node);

		final ReportTypeImpl result = new ReportTypeImpl();
		if (map != null) {
			result.setMnem( getString( map, PROP_T_RTYPE_CODE));
			setL18Name(result, map);
		}

		return result;
	}

	protected ReportProviderDescriptorImpl createReportProvider(NodeRef node) {
		if (node == null)
			return null;
		PropertyCheck.mandatory (this, "services", services);

		final ReportProviderDescriptorImpl result = new ReportProviderDescriptorImpl(); 
		final Map<QName, Serializable> map = getServices().getServiceRegistry().getNodeService().getProperties(node);
		if (map != null) {
			result.setMnem( getString( map, PROP_T_RPROVIDER_CODE));
			setL18Name(result, map);
			result.setClassName( getString( map, PROP_T_RPROVIDER_CLASS));
		}
		return result;
	}

	protected ReportTemplateImpl createReportTemplate(NodeRef node) {
		if (node == null)
			return null;
		PropertyCheck.mandatory (this, "services", services);
		final NodeService nodeService =  getServices().getServiceRegistry().getNodeService();

		final ReportTemplateImpl result = new ReportTemplateImpl();
		setProps_ReportTemplate(result, nodeService.getProperties(node), node);
		return result;
	}

	protected void setProps_ReportTemplate(ReportTemplate result, final Map<QName, Serializable> map, NodeRef node) {
		PropertyCheck.mandatory (this, "services", services);
		final NodeService nodeService =  getServices().getServiceRegistry().getNodeService();
		final NamespaceService ns =  getServices().getServiceRegistry().getNamespaceService();

		result.setMnem( getString( map, "cm:name"));
		setL18Name(result, map);

		final NodeRef nodeCntntFile = LucenePreparedQuery.getAssocTarget( node, ASSOC_RTEMPLATE_FILE, nodeService, ns);
		if (nodeCntntFile != null) {
			final Map<QName, Serializable> mapCntntFile = nodeService.getProperties(nodeCntntFile);
			result.setFileName( getString(mapCntntFile, "cm:name"));

			final ContentReader reader = services.getServiceRegistry().getContentService().getReader(nodeCntntFile, ContentModel.PROP_CONTENT);
			try {
				final byte[] data = (reader != null && reader.getSize() > 0) ? IOUtils.toByteArray(reader.getContentInputStream()) : null;
				result.setData( (data == null) ? null : new ByteArrayInputStream(data) );
			} catch(IOException ex) {
				final String msg = String.format( "Error getting file content of node {%s}", node);
				throw new RuntimeException( msg, ex);
			}
		}
	}

	protected DataSourceDescriptorImpl createDSDescriptor(NodeRef node) {
		if (node == null)
			return null;
		final DataSourceDescriptorImpl result = new DataSourceDescriptorImpl();
		setProps_DSDescriptor(result, node);
		return result;
	}

	protected void setProps_DSDescriptor(DataSourceDescriptor result, NodeRef node) {
		if (node == null)
			return;

		final NodeService nodeService =  getServices().getServiceRegistry().getNodeService();
		final NamespaceService ns =  getServices().getServiceRegistry().getNamespaceService();

		final Map<QName, Serializable> map = nodeService.getProperties(node);
		if (map != null) {
			result.setMnem( getString( map, PROP_T_RDS_CODE));
			setL18Name(result, map);

			final List<NodeRef> found = LucenePreparedQuery.getAssocChildrenByType( node, TYPE_RDS_COLUMN, nodeService, ns);
			setProps_ListColumns( result.getColumns(), found);
		}
	}

	protected ColumnDescriptor createColumnDescriptor(NodeRef node) {
		if (node == null)
			return null;
		final ColumnDescriptorImpl result = new ColumnDescriptorImpl();
		setProps_DataColumn(result, node);
		return result;
	}

    protected void setProps_DataColumn(ColumnDescriptor result, NodeRef node) {
        if (node == null)
            return;

        PropertyCheck.mandatory(this, "services", services);
        final NodeService nodeSrv = getServices().getServiceRegistry().getNodeService();
        final NamespaceService ns = getServices().getServiceRegistry().getNamespaceService();

        final Map<QName, Serializable> map = nodeSrv.getProperties(node);

        if (map == null)
            return;

        result.setColumnName(getString(map, PROP_T_RDS_COLUMN_CODE));
        setL18Name(result, map);

        result.setExpression(getString(map, PROP_T_RDS_COLUMN_EXPR));

        result.setOrder(getInt(map, PROP_T_RDS_COLUMN_ORDER, 0));

        result.setAlfrescoType(getString(map, PROP_T_RDS_COLUMN_CLASS));
        // TODO: result.setSpecial( getBool(map, PROP_T_RDS_ISSPECIAL, false));

        // тип колонки ...
        {
            final NodeRef nodeColType = LucenePreparedQuery.getAssocTarget(node, ASSOC_RDS_COLUMN_TYPE, nodeSrv, ns);
            final JavaDataType jdt = createColDataType(nodeColType);
            result.setDataType(jdt);
        }

        // тип параметра для колонки ...
        {
            final NodeRef nodeColParType = LucenePreparedQuery.getAssocTarget(node, ASSOC_RDS_COLUMN_PARAMTYPE, nodeSrv, ns);
            final ParameterTypedValue paramValue = createParameterTypeValue(nodeColParType);
            result.setParameterValue(paramValue);
        }

    }

	private JavaDataType createColDataType(NodeRef nodeColType) {
		if (nodeColType == null)
			return null;

		PropertyCheck.mandatory (this, "services", services);

		final Map<QName, Serializable> map = getServices().getServiceRegistry().getNodeService().getProperties(nodeColType);

		final String clazzName = getString( map, PROP_T_RDS_COLTYPE_CLASS);
		final String name =  getString( map, "cm:name");
		final String code = getString( map, PROP_T_RDS_COLTYPE_CODE);

		// если есть класс - по классу, потом по имени, потом по по мнемонике
		final String tag =  Utils.coalesce( clazzName, name, code);
		try {
			final JavaDataType result = SupportedTypes.findType( tag).javaDataType();
			return result;
		} catch (Exception ex) {
			final String msg = String.format( "Unsupported column data class '%s' due to error: %s", tag, ex.getMessage());
			log.error(msg, ex);
			throw new RuntimeException( msg, ex);
		}
	}

	private ParameterTypedValue createParameterTypeValue(NodeRef nodeColParType) {
		if (nodeColParType == null)
			return null;

		PropertyCheck.mandatory (this, "services", services);

		final Map<QName, Serializable> map = getServices().getServiceRegistry().getNodeService().getProperties(nodeColParType);

		final String mnem = getString( map, PROP_T_RDS_PARTYPE_CODE);
		final ParameterTypedValueImpl result = new ParameterTypedValueImpl(mnem);

		setL18Name(result, map);
		setL18Name(result.getPrompt1(), map, PROP_T_RDS_PARTYPE_LABEL1);
		setL18Name(result.getPrompt2(), map, PROP_T_RDS_PARTYPE_LABEL2);

		final String tagParType = getString( map, PROP_T_RDS_PARTYPE_CODE);
		if (tagParType != null) {
			final ParameterTypedValue.Type atype = ParameterTypedValue.Type.findType(tagParType);
			if (atype == null) {
				final String msg = String.format( "Unsupported column parameter type '%s'", tagParType);
				log.error(msg);
				throw new RuntimeException( msg);
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
	 * 
	 * @param result
	 * @param list список узлов типа "lecm-rpeditor:reportDataColumn"
	 */
	private void setProps_ListColumns( List<ColumnDescriptor> result, List<NodeRef> list) {
		if (result == null || list == null)
			return;
		for (NodeRef colRef: list) {
			final ColumnDescriptor coldesc = createColumnDescriptor(colRef);
			if (coldesc != null)
				result.add(coldesc);
		}
	}

    @Override
    public NodeRef getReportDescriptorNodeByCode(String rtMnemo) {
        LuceneSearchBuilder builder = new LuceneSearchBuilder(namespaceService);
        builder.emmitFieldCond(null, PROP_T_REPORT_CODE, rtMnemo);
        builder.emmitTypeCond(TYPE_ReportDescriptor, null);

        ResultSet rs = LucenePreparedQuery.execFindQuery(builder, getServices().getServiceRegistry().getSearchService());
        for (ResultSetRow row : rs) {
            NodeRef nodeId = row.getNodeRef();
            return nodeId;
        }
        return null;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }
}
