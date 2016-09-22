package ru.it.lecm.base.dbviews;

import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: AZinovin
 * Date: 28.11.13
 * Time: 12:44
 */
public class DBViewBootstrap {

    private static final transient Logger log = LoggerFactory.getLogger(DBViewBootstrap.class);

    public static final String LECM_VIEW_PREFIX = "lecm_v_";

    private DataSource dataSource;

    private DictionaryService dictionaryService;

    private List<String> models;
    private List<String> columns;
    private NamespaceService namespaceService;

    public void bootstrapViews() {
        for (String model : models) {
            try {
                bootstrapView(model);
            } catch (Exception e) {
                log.warn("View for '{}' was not created", model);
            }
        }
    }

    private void bootstrapView(String model) throws SQLException {
//        String viewName = LECM_VIEW_PREFIX + model.toLowerCase().replace(":", "_");
//        QName typeQName = QName.createQName(model, namespaceService);
//        TypeDefinition typeDefinition = dictionaryService.getType(typeQName);
//        Map<QName,PropertyDefinition> properties = new HashMap<QName, PropertyDefinition>();
//        properties.putAll(typeDefinition.getProperties());
//
//        List<AspectDefinition> defaultAspects = typeDefinition.getDefaultAspects(true);
//        for (AspectDefinition defaultAspect : defaultAspects) {
//            properties.putAll(defaultAspect.getProperties());
//        }
//
//        StringBuilder allPropsSql = new StringBuilder();
//        allPropsSql.append("DROP VIEW IF EXISTS \"");
//        allPropsSql.append(viewName);
//        allPropsSql.append("\";\n");
//        allPropsSql.append("CREATE OR REPLACE VIEW \"");
//        allPropsSql.append(viewName);
//        allPropsSql.append("\" AS ");
//
//
//        StringBuilder propsSql = new StringBuilder();
//        StringBuilder columnsSql = new StringBuilder();
//        for (Map.Entry<QName, PropertyDefinition> entry : properties.entrySet()) {
//            String prefixedName = entry.getKey().toPrefixString();
//            if (prefixedName.startsWith("sys:") || (
//                    columns != null && !columns.isEmpty() && !columns.contains(prefixedName)
//            )) {
//                continue;
//            }
//            String javaClassName = entry.getValue().getDataType().getJavaClassName();
//            String valueColumn = null;
//            if ("java.lang.String".equals(javaClassName)) {
//                valueColumn = "string_value";
//            } else if ("java.util.Date".equals(javaClassName)) {
//                valueColumn = "string_value";
//            } else if ("java.lang.Double".equals(javaClassName)) {
//                valueColumn = "double_value";
//            } else if ("java.lang.Float".equals(javaClassName)) {
//                valueColumn = "float_value";
//            } else if ("java.lang.Long".equals(javaClassName)) {
//                valueColumn = "long_value";
//            } else if ("java.lang.Integer".equals(javaClassName)) {
//                valueColumn = "long_value";
//            } else if ("java.lang.Boolean".equals(javaClassName)) {
//                valueColumn = "boolean_value";
//            } else {
//                log.warn("Value column not defined for '{}' - '{}'", prefixedName, javaClassName);
//            }
//
//            if (valueColumn != null) {
//                prefixedName = prefixedName.replace(":", "_").replace("-", "_");
//                columnsSql.append(", ");
//                if ("boolean_value".equals(valueColumn)) {
//                    columnsSql.append("bool_or");
//                } else {
//                    columnsSql.append("max");
//                }
//                columnsSql.append("(");
//                columnsSql.append(prefixedName);
//                columnsSql.append(") AS ");
//                columnsSql.append(prefixedName);
//                propsSql.append(",\n");
//                propsSql.append("CASE WHEN uri='");
//                propsSql.append(entry.getKey().getNamespaceURI());
//                propsSql.append("' AND local_name='");
//                propsSql.append(entry.getKey().getLocalName());
//                propsSql.append("' THEN ");
//                propsSql.append(valueColumn);
//                propsSql.append(" ELSE null END AS ");
//                propsSql.append(prefixedName);
//            }
//
//        }
//
//        allPropsSql.append("SELECT node_id, uuid");
//        allPropsSql.append(columnsSql);
//
//        allPropsSql.append(" FROM (\n" +
//                "SELECT node_id, uuid");
//        allPropsSql.append(propsSql);
//
//        allPropsSql.append(" FROM (SELECT \n" +
//                "  props.node_id, \n" +
//                "  props.persisted_type_n, \n" +
//                "  props.boolean_value, \n" +
//                "  props.long_value, \n" +
//                "  props.float_value, \n" +
//                "  props.double_value, \n" +
//                "  props.string_value, \n" +
//                "  props.serializable_value, \n" +
//                "  props.list_index, \n" +
//                "  node.uuid, \n" +
//                "  prop_qname.local_name, \n" +
//                "  prop_ns.uri\n" +
//                "FROM \n" +
//                "  public.alf_node node, \n" +
//                "  public.alf_store store, \n" +
//                "  public.alf_node_properties props, \n" +
//                "  public.alf_qname prop_qname, \n" +
//                "  public.alf_namespace prop_ns, \n" +
//                "  public.alf_qname type_qname, \n" +
//                "  public.alf_namespace type_ns\n" +
//                "WHERE \n" +
//                "  node.store_id = store.id AND\n" +
//                "  node.id = props.node_id AND\n" +
//                "  prop_qname.id = props.qname_id AND\n" +
//                "  prop_ns.id = prop_qname.ns_id AND\n" +
//                "  type_qname.id = node.type_qname_id AND\n" +
//                "  type_ns.id = type_qname.ns_id AND\n" +
//                "  store.identifier = 'SpacesStore' AND \n" +
//                "  store.protocol = 'workspace' AND \n" +
//                "  type_ns.uri = '");
//        allPropsSql.append(typeQName.getNamespaceURI());
//                allPropsSql.append("' AND \n" +
//                "  type_qname.local_name = '");
//        allPropsSql.append(typeQName.getLocalName());
//        allPropsSql.append("') all_props) needed_prop_list\n" +
//                "GROUP BY node_id, uuid;");
//
//
//        Connection connection = null;
//        PreparedStatement statement = null;
//        try {
//            connection = dataSource.getConnection();
//            statement = connection.prepareStatement(allPropsSql.toString());
//            statement.execute();
//            connection.commit();
//            log.debug("View created:\n{}", allPropsSql.toString());
//        } catch (SQLException e) {
//            log.warn("Can not create view:\n{}", allPropsSql.toString());
//        } finally {
//            if (statement != null) {
//                statement.close();
//            }
//            if (connection != null) {
//                connection.close();
//            }
//        }
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setDictionaryService(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    public void setModels(List<String> models) {
        this.models = models;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }
}
