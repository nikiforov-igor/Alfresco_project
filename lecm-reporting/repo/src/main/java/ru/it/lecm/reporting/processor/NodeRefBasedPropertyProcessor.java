package ru.it.lecm.reporting.processor;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.cmr.repository.Path.ChildAssocElement;
import org.alfresco.service.cmr.repository.Path.Element;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.jms.core.JmsTemplate;
import ru.it.lecm.reporting.Constants;
import ru.it.lecm.reporting.ReportLine;
import ru.it.lecm.reporting.ReportingHelper;
import ru.it.lecm.reporting.mybatis.AssocDefinition;
import ru.it.lecm.reporting.mybatis.InsertInto;
import ru.it.lecm.reporting.mybatis.ReportingDAO;
import ru.it.lecm.reporting.mybatis.UpdateWhere;
import ru.it.lecm.reporting.mybatis.impl.ReportingDAOImpl;

import javax.jms.*;
import javax.jms.Queue;
import java.io.File;
import java.io.FileInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NodeRefBasedPropertyProcessor extends PropertyProcessor {
    private static Log logger = LogFactory.getLog(NodeRefBasedPropertyProcessor.class);
    private String COLUMN_SIZE = "";
    private ActiveMQConnectionFactory connectionFactory;
    private JmsTemplate templateProducer;
    private Integer batchSize = 100;
    final long batchForDeleteSize = 500;

    private Map<QName, Set<String>> typeAssocsDefinitions = new HashMap<>();

    public void setConnectionFactory(ActiveMQConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void setTemplateProducer(JmsTemplate templateProducer) {
        this.templateProducer = templateProducer;
    }

    public NodeRefBasedPropertyProcessor(ServiceRegistry serviceRegistry, ReportingHelper helper) throws Exception {
        this.setNodeService(helper.getNodeService());
        this.setNamespaceService(serviceRegistry.getNamespaceService());
        this.setDictionaryService(serviceRegistry.getDictionaryService());
        this.setFileFolderService(serviceRegistry.getFileFolderService());
        this.setSearchService(serviceRegistry.getSearchService());
        this.versionService = serviceRegistry.getVersionService();
        super.versionService = serviceRegistry.getVersionService();

        this.setReportingHelper(helper);
        this.setClassToColumnType(reportingHelper.getClassToColumnType());
        this.setReplacementDataTypes(reportingHelper.getReplacementDataType());
        this.setGlobalProperties(reportingHelper.getGlobalProperties());
        this.setNamespaces(reportingHelper.getNameSpaces());
        this.setBlacklist(reportingHelper.getBlacklist());
        String vendor = reportingHelper.getDatabaseProvider();
        if ("Oracle".equals(vendor)) {
            this.COLUMN_SIZE = "docsize";
        } else {
            this.COLUMN_SIZE = "size";
        }
    }

    public void havestNodes(NodeRef harvestDefinition) {
        try {
            HashMap<String, HashSet<String>> tableDefinitions = new HashMap<>();
            typeAssocsDefinitions = new HashMap<>();

            Properties queries = this.getReportingHelper().getTableQueries();
            Enumeration keys = queries.keys();

            this.getDbhb().createEmptyTypeTablesTable();
            this.getDbhb().createEmptyAssocsTable();

            // до сбора - удаление. Если удалять после - возможно проблемы с восстановление данных из корзины
            deleteRecordsFromDB(queries);

            while (keys.hasMoreElements()) {
                String tableName = (String) keys.nextElement();
                String query = (String) queries.get(tableName);
                logger.info("Harvesting table '" + tableName + "' with query: " + query);
                tableName = this.dbhb.fixTableColumnName(tableName);
                Pattern pattern = Pattern.compile("TYPE: *\"(.*?)\".*?");
                Matcher matcher = pattern.matcher(query);
                QName type;
                Collection<QName> types;
                if (matcher.find()) {
                    String typeStr = matcher.group(1);
                    type = QName.createQName(typeStr, namespaceService);
                    types = dictionaryService.getSubTypes(type, true);
                    types.add(type);
                } else {
                    throw new RuntimeException("Unknown type for query " + query);
                }
                String sTypes = null;
                ResultSet res = null;
                PreparedStatement stmt = null;
                java.sql.Connection conn = null;
                try {
                    StringBuilder typesQuery = new StringBuilder("SELECT " +
                            "  alf_qname.id " +
                            "FROM " +
                            "  alf_qname, " +
                            "  alf_namespace " +
                            "WHERE " +
                            "  alf_qname.ns_id = alf_namespace.id AND (");

                    boolean first = true;
                    for (QName t : types) {
                        if (!first) {
                            typesQuery.append(" OR ");
                        }
                        typesQuery.append("(alf_namespace.uri = '").append(t.getNamespaceURI()).append("' AND alf_qname.local_name = '").append(t.getLocalName()).append("')");
                        first = false;
                    }

                    typesQuery.append(");");

                    conn = getReportingHelper().getAlfrescoDataSource().getConnection();
                    stmt = conn.prepareStatement(typesQuery.toString());
                    res = stmt.executeQuery();
                    List<Long> lTypes = new ArrayList<>();
                    while (res.next()) {
                        lTypes.add(res.getLong(1));
                    }
                    if (lTypes.isEmpty()){
                        lTypes.add((long) -1);
                    }
                    sTypes = StringUtils.join(lTypes, ',');
                    if (logger.isInfoEnabled()) {
                        logger.info("Type IDs: " + sTypes);
                    }
                } finally {
                    if (res != null && !res.isClosed()) {
                        res.close();
                    }
                    if (stmt != null && !stmt.isClosed()) {
                        stmt.close();
                    }
                    if (conn != null && !conn.isClosed()) {
                        conn.close();
                    }
                }

                if (!this.dbhb.tableIsRunning(tableName)) {
                    this.dbhb.createEmptyTables(tableName);

                    // метка о запуске для таблицы
                    this.dbhb.setLastTimestampStatusRunning(tableName);

                    final Properties tableColumns = this.dbhb.getTableDescription(tableName);

                    long txnFrom = 0;
                    long nodeFrom = 0;

                    final StoreRef storeRef = StoreRef.STORE_REF_WORKSPACE_SPACESSTORE;
                    if (logger.isDebugEnabled()) {
                        logger.debug("harvest: StoreRef=" + storeRef.getProtocol());
                    }

                    long startDbId = 0L;
                    long loopcount = 0L;
                    boolean letsContinue = true;
                    try {
                        while (letsContinue) {
                            //Получаем дату последней синхронизации
                            String timestampStart = this.dbhb.getLastTimestamp(tableName);

                            String[] lastSyncDate = null;
                            if (timestampStart != null) {
                                lastSyncDate = timestampStart.split("-");
                            }
                            if (lastSyncDate == null || lastSyncDate.length != 2) {
                                lastSyncDate = new String[]{"0", "0"};
                            }

                            txnFrom = Long.parseLong(lastSyncDate[0]);
                            nodeFrom = Long.parseLong(lastSyncDate[1]);

                            Properties configs;

                            while (letsContinue) { // цикл по нодам в выбранном диапозоне
                                // выход из цикла - если это указано в конфиге
                                configs = this.getReportingConfigs();
                                if (isReportingDisabled(configs) || isReportingMustStop(configs)) {
                                    logger.info("Check config file. Reporting stopped!");
                                    this.dbhb.setLastTimestampAndStatus(tableName, "Done", "" + txnFrom + "-" + nodeFrom);
                                    return;
                                } else {
                                    this.dbhb.setLastTimestampAndStatus(tableName, "Running", "" + txnFrom + "-" + nodeFrom);
                                }

                                ++loopcount;

                                letsContinue = false;

                                java.sql.ResultSet results = null;
                                java.sql.Connection connection = null;
                                PreparedStatement preparedStatement = null;
                                try {
                                    String fullQuery = "SELECT node.uuid, node.transaction_id, node.id FROM alf_node node " +
                                            "INNER JOIN alf_store store ON store.id = node.store_id " +
                                            "WHERE " +
                                            "store.protocol = ? AND " +
                                            "store.identifier = ? AND " +
                                            "node.type_qname_id IN (" + sTypes + ") " +
                                            "AND (node.transaction_id > ? " +
                                            "OR (node.transaction_id = ? " +
                                            "AND node.id > ?))" +
                                            "ORDER BY node.transaction_id, node.id LIMIT 500";


                                    connection = getReportingHelper().getAlfrescoDataSource().getConnection();
                                    preparedStatement = connection.prepareStatement(fullQuery);
                                    preparedStatement.setString(1, storeRef.getProtocol());
                                    preparedStatement.setString(2, storeRef.getIdentifier());
                                    preparedStatement.setLong(3, txnFrom);
                                    preparedStatement.setLong(4, txnFrom);
                                    preparedStatement.setLong(5, nodeFrom);

                                    if (logger.isInfoEnabled()) {
                                        logger.info("harvest: StoreProtocol = " + storeRef.getProtocol() + " fullQuery = " + preparedStatement);
                                    }

                                    results = preparedStatement.executeQuery();

                                    while (results.next()) {
                                        letsContinue = true;
                                        NodeRef e1 = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, results.getString(1));
                                        logger.debug("harvest nodeRef " + e1);
                                        if (!e1.toString().startsWith("version")) {
                                            if (logger.isDebugEnabled()) {
                                                logger.debug("harvest:  adding NodeRef " + e1);
                                            }
                                            this.addToQueue(e1);
                                            txnFrom = results.getLong(2);
                                            nodeFrom = results.getLong(3);
                                        }
                                    }
                                } catch (SQLException e1) {
                                    logger.error("Error while database request processed for type " + type + "\r\n" + preparedStatement + "\r\n" + e1.getMessage());
                                } finally {
                                    if (results != null && !results.isClosed()) {
                                        results.close();
                                    }
                                    if (preparedStatement != null && !preparedStatement.isClosed()) {
                                        preparedStatement.close();
                                    }
                                    if (connection != null && !connection.isClosed()) {
                                        connection.close();
                                    }
                                }
                                if (logger.isInfoEnabled()) {
                                    logger.info("harvest: loopCount = " + loopcount + " letsContinue = " + letsContinue);
                                }

                                if (letsContinue) { // нашли - обрабатываем
                                    HashMap<String, String> definitions = new HashMap<>();
                                    List<ReportLine> lines = new ArrayList<>();
                                    for (Object node : this.queue) {
                                        NodeRef nodeRef = (NodeRef) node;
                                        ReportLine rl = new ReportLine(tableName, this.reportingHelper);
                                        ReportLine line =
                                                this.processNodeToMap(nodeRef, tableName, rl, tableColumns);
                                        if (line.getValue("sys_node_uuid") != null && line.getValue("sys_node_uuid").length() > 0) {
                                            lines.add(line);
                                            Properties p = line.getTypes();
                                            for (Object key : p.keySet()) {
                                                definitions.put((String) key, p.getProperty((String) key));
                                            }
                                        }
                                    }

                                    HashSet<String> tableDefinition = tableDefinitions.get(tableName);
                                    boolean doNotCheck = tableDefinition != null;
                                    if (tableDefinition != null) {
                                        for (String fieldName : definitions.keySet()) {
                                            doNotCheck &= tableDefinition.contains(fieldName);
                                        }
                                    }

                                    if (!doNotCheck) {
                                        this.setTableDefinition(tableName, definitions);
                                        if (tableDefinition == null) {
                                            tableDefinition = new HashSet<>();
                                            tableDefinitions.put(tableName, tableDefinition);
                                        }
                                        tableDefinition.addAll(definitions.keySet());
                                        if (logger.isInfoEnabled()) {
                                            logger.info("harvest: tableDef done. Processing queue Values");
                                        }
                                    }

                                    this.processQueueValues(tableName, lines);
                                    this.resetQueue();
                                    if (logger.isDebugEnabled()) {
                                        logger.debug("harvest: StoreProtocol = " + storeRef.getProtocol());
                                        logger.debug("harvest: New start DBID=" + startDbId);
                                    }
                                }
                            }

                        }
                        this.dbhb.setLastTimestampAndStatus(tableName, "Done", "" + txnFrom + "-" + nodeFrom);
                    } catch (Exception ex) {
                        logger.error("Error while harvest", ex);
                    }
                }
            }

        } catch (Exception var36) {
            logger.error("Fatality: " + var36.getMessage());
        }
    }

    private long getMaxTransactionId() throws SQLException {
        long transactionId = 0L;

        java.sql.ResultSet results = null;
        java.sql.Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            String fullQuery = "select MAX(transaction_id) FROM alf_node";

            connection = getReportingHelper().getAlfrescoDataSource().getConnection();
            preparedStatement = connection.prepareStatement(fullQuery);

            results = preparedStatement.executeQuery();

            while (results.next()) {
                transactionId = results.getLong(1);
            }
        } catch (SQLException e1) {
            logger.error("Error while database request " + "\r\n" + preparedStatement + "\r\n" + e1.getMessage());
        } finally {
            if (results != null && !results.isClosed()) {
                results.close();
            }
            if (preparedStatement != null && !preparedStatement.isClosed()) {
                preparedStatement.close();
            }
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        }
        return transactionId;
    }


    private void deleteRecordsFromDB(Properties tables) throws Exception {
        final String deletedTimestamp = this.dbhb.getLastTimestamp("deleted"); // для отсчета удаления

        String[] lastSyncDate = null;
        if (deletedTimestamp != null) {
            lastSyncDate = deletedTimestamp.split("-");
        }
        if (lastSyncDate == null || lastSyncDate.length != 2) {
            lastSyncDate = new String[]{"0", "0"};
        }

        final long minTransactionId = Long.parseLong(lastSyncDate[0]);
        final long maxTransactionId = getMaxTransactionId();

        if (!this.dbhb.tableIsRunning("deleted") && isRecordDeletionEnabled(getReportingConfigs())) {
            this.dbhb.setLastTimestampStatusRunning("deleted");

            Map<Long, NodeRef> toDelete = getRecordsToDelete(minTransactionId);
            logger.debug("Found records to delete: " + toDelete.size());
            final long max = toDelete.size();

            try {
                SqlSession sqlSession = ((SqlSessionTemplate) ((ReportingDAOImpl) this.dbhb.getReportingDAO()).getTemplate()).getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
                sqlSession.getConnection().setAutoCommit(false);
                try {
                    int cnt = 0;

                    final StringBuilder nodeRefBuilder = new StringBuilder();
                    final StringBuilder idsBuilder = new StringBuilder();
                    for (Long dbId : toDelete.keySet()) {
                        cnt++;
                        nodeRefBuilder.append("\'").append(toDelete.get(dbId).toString()).append("\',");
                        idsBuilder.append(dbId).append(",");

                        if (cnt == batchForDeleteSize || cnt >= max) {
                            final String nodesQuery = nodeRefBuilder.delete(nodeRefBuilder.length() - 1, nodeRefBuilder.length()).toString();
                            final String idsQuery = idsBuilder.delete(idsBuilder.length() - 1, idsBuilder.length()).toString();

                            // 1. удаление из таблиц
                            Enumeration keys = tables.keys();
                            while (keys.hasMoreElements()) {
                                String tableName = (String) keys.nextElement();
                                tableName = this.dbhb.fixTableColumnName(tableName);
                                UpdateWhere updateWhere =
                                        new UpdateWhere(tableName,
                                                "",
                                                "sys_node_dbid IN (" + idsQuery + ")");
                                sqlSession.delete("reporting-delete-from-table", updateWhere);
                            }
                            // 2. удаление из таблицы associations
                            UpdateWhere updateSources =
                                    new UpdateWhere(ReportingDAO.ASSOCS, "", "source_ref IN (" + nodesQuery + ")");
                            sqlSession.delete("reporting-delete-from-table", updateSources);

                            UpdateWhere updateTargets =
                                    new UpdateWhere(ReportingDAO.ASSOCS, "", "target_ref IN (" + nodesQuery + ")");
                            sqlSession.delete("reporting-delete-from-table", updateTargets);

                            sqlSession.commit();
                            sqlSession.close();
                            sqlSession = ((SqlSessionTemplate) ((ReportingDAOImpl) this.dbhb.getReportingDAO()).getTemplate()).getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
                            sqlSession.getConnection().setAutoCommit(false);

                            cnt = 0;
                            nodeRefBuilder.delete(0, nodeRefBuilder.length());
                            idsBuilder.delete(0, idsBuilder.length());
                        }
                    }
                    sqlSession.commit();
                } finally {
                    sqlSession.close();
                }
                this.dbhb.setLastTimestampAndStatus("deleted", "Done", "" + maxTransactionId + "-0");
            } catch (Exception ex) {
                logger.error("Fatality: " + ex.getMessage());
                this.dbhb.setLastTimestampAndStatus("deleted", "Done", "" + minTransactionId + "-0"); // не обновляем
            }
        }
    }

    protected ReportLine processNodeToMap(NodeRef nodeRef, String table, ReportLine rl, Properties tableColumns) throws Exception {
        this.dbhb.fixTableColumnName(table);

        if (logger.isDebugEnabled()) {
            logger.debug("Enter processNodeToMap nodeRef=" + nodeRef);
        }

        boolean isIncludeMetadata = isIncludeMetadata();

        try {
            rl = this.processPropertyValues(rl, nodeRef, isIncludeMetadata);

            rl = this.processAssociationValues(rl, nodeRef, tableColumns);

            String objectType = this.nodeService.getType(nodeRef).toPrefixString(namespaceService);
            rl.setLine(Constants.COLUMN_OBJECT_TYPE, this.getClassToColumnType().getProperty(Constants.COLUMN_OBJECT_TYPE, ""), objectType, this.getReplacementDataType());

            rl.setLine(Constants.COLUMN_NODEREF, this.getClassToColumnType().getProperty(Constants.COLUMN_NODEREF), nodeRef.toString(), this.getReplacementDataType());

            if (isIncludeMetadata) {
                String displayPath;

                try {
                    Path path1 = this.getNodeService().getPath(nodeRef);
                    displayPath = this.toDisplayPath(path1);
                    rl.setLine(Constants.COLUMN_PATH, this.getClassToColumnType().getProperty(Constants.COLUMN_PATH), displayPath, this.getReplacementDataType());
                } catch (Exception var24) {
                    logger.error(var24.getMessage(), var24);
                }

                QName myType = this.getNodeService().getType(nodeRef);
                if (this.getDictionaryService().isSubClass(myType, ContentModel.TYPE_FOLDER)) {
                    NodeRef size = null;

                    try {
                        if (nodeRef.toString().startsWith("archive")) {
                            ChildAssociationRef e = (ChildAssociationRef) this.nodeService.getProperty(nodeRef, QName.createQName("http://www.alfresco.org/model/system/1.0", "archivedOriginalParentAssoc"));
                            logger.debug("ORIGIN: child:" + e.getChildRef() + " parent: " + e.getParentRef());
                            size = e.getChildRef();
                        }
                    } catch (Exception var22) {
                        logger.fatal("Exception getting orig_noderef" + Arrays.toString(var22.getStackTrace()));
                    }

                    if (size != null) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Setting Ref from archive to orig_noderef!!!");
                        }

                        rl.setLine(Constants.COLUMN_ORIG_NODEREF, this.getClassToColumnType().getProperty(Constants.COLUMN_NODEREF), size.toString(), this.getReplacementDataType());
                    } else {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Setting currentRef to orig_noderef!!!");
                        }

                        rl.setLine(Constants.COLUMN_ORIG_NODEREF, this.getClassToColumnType().getProperty(Constants.COLUMN_NODEREF), nodeRef.toString(), this.getReplacementDataType());
                    }
                }

                if (!this.getDictionaryService().isSubClass(myType, ContentModel.TYPE_CONTENT) && !this.getDictionaryService().getType(myType).toString().equalsIgnoreCase(ContentModel.TYPE_CONTENT.toString())) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(myType.toString() + " is no content subclass!");
                    }
                } else {
                    long size1;
                    String sizeString;

                    try {
                        size1 = this.getFileFolderService().getFileInfo(nodeRef).getContentData().getSize();
                        if (size1 == 0L) {
                            sizeString = "0";
                        } else {
                            sizeString = Long.toString(size1);
                        }

                        rl.setLine(this.COLUMN_SIZE, this.getClassToColumnType().getProperty(this.COLUMN_SIZE), sizeString, this.getReplacementDataType());
                    } catch (Exception var21) {
                        logger.info("processNodeToMap: Huh, no size?");
                    }

                    boolean versioned;

                    try {
                        versioned = this.getNodeService().hasAspect(nodeRef, ContentModel.ASPECT_VERSIONABLE);
                        rl.setLine("versioned", this.getClassToColumnType().getProperty("boolean"), String.valueOf(versioned), this.getReplacementDataType());
                    } catch (Exception var20) {
                        logger.info("processNodeToMap: Huh, no versioned info?");
                    }

                    try {
                        String origNodeRef = this.getFileFolderService().getFileInfo(nodeRef).getContentData().getMimetype();
                        if (origNodeRef == null) {
                            origNodeRef = "NULL";
                        }

                        rl.setLine(Constants.COLUMN_MIMETYPE, this.getClassToColumnType().getProperty(Constants.COLUMN_MIMETYPE), origNodeRef, this.getReplacementDataType());
                    } catch (Exception var19) {
                        logger.info("processNodeToMap: Huh, no mimetype?");
                    }

                    NodeRef origNodeRef1 = null;

                    try {
                        if (nodeRef.toString().startsWith("archive")) {
                            ChildAssociationRef e1 = (ChildAssociationRef) this.nodeService.getProperty(nodeRef, QName.createQName("http://www.alfresco.org/model/system/1.0", "archivedOriginalParentAssoc"));
                            logger.debug("ORIGIN: child:" + e1.getChildRef() + " parent: " + e1.getParentRef());
                            origNodeRef1 = e1.getChildRef();
                        }
                    } catch (Exception var18) {
                        logger.warn("Exception getting orig_noderef" + var18.getStackTrace());
                    }

                    try {
                        if (nodeRef.toString().startsWith("version")) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("Setting nodeRef to orig_noderef - VERSION!!!");
                            }

                            rl.setLine(Constants.COLUMN_ORIG_NODEREF, this.getClassToColumnType().getProperty(Constants.COLUMN_NODEREF), null, this.getReplacementDataType());
                        } else if (origNodeRef1 != null) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("Setting Ref from archive to orig_noderef!!!");
                            }

                            rl.setLine(Constants.COLUMN_ORIG_NODEREF, this.getClassToColumnType().getProperty(Constants.COLUMN_NODEREF), origNodeRef1.toString(), this.getReplacementDataType());
                        } else {
                            if (logger.isDebugEnabled()) {
                                logger.debug("Setting currentRef to orig_noderef!!!");
                            }

                            rl.setLine(Constants.COLUMN_ORIG_NODEREF, this.getClassToColumnType().getProperty(Constants.COLUMN_NODEREF), nodeRef.toString(), this.getReplacementDataType());
                        }
                    } catch (Exception ignored) {
                        if (logger.isDebugEnabled()) {
                            logger.error(ignored.getMessage(), ignored);
                        }
                    }
                }
            }
        } catch (DataIntegrityViolationException ex) {
            logger.error(ex.getMessage(), ex);
        }

        return rl;
    }

    @Override
    protected ReportLine processNodeToMap(String var1, String var2, ReportLine var3) {
        return null;
    }

    public Properties processQueueDefinition(String table) {
        Properties definition = new Properties();
        boolean isIncludeMeta = isIncludeMetadata();

        for (Object aQueue : this.queue) {
            NodeRef nodeRef = new NodeRef(aQueue.toString().split(",")[0]);

            try {
                definition = this.processPropertyDefinitions(definition, nodeRef);
            } catch (Exception var9) {
                logger.warn("processQueueDefinition: ERROR: versionNodes.containsKey or before " + var9.getMessage());
                var9.printStackTrace();
            }

            try {
                definition.setProperty("noderef", this.getClassToColumnType().getProperty(Constants.COLUMN_NODEREF, "-"));
                definition.setProperty(Constants.COLUMN_OBJECT_TYPE, this.getClassToColumnType().getProperty("object_type", "-"));

                QName type = this.getNodeService().getType(nodeRef);
                if (logger.isDebugEnabled()) {
                    logger.debug("processQueueDefinition: qname=" + type);
                }

                if (isIncludeMeta) {
                    definition.setProperty(Constants.COLUMN_ORIG_NODEREF, this.getClassToColumnType().getProperty(Constants.COLUMN_NODEREF, "-"));
                    definition.setProperty(Constants.COLUMN_PATH, this.getClassToColumnType().getProperty(Constants.COLUMN_PATH, "-"));

                    if (!this.getDictionaryService().isSubClass(type, ContentModel.TYPE_CONTENT) && !this.getDictionaryService().getType(type).toString().equalsIgnoreCase(ContentModel.TYPE_CONTENT.toString())) {
                        logger.debug("processQueueDefinition: NOOOOO! We are NOT a subtype of Content!");
                    } else {
                        if (logger.isDebugEnabled()) {
                            logger.debug("processQueueDefinition: YEAH! We are a subtype of Content! " + ContentModel.TYPE_CONTENT);
                        }

                        definition.setProperty(Constants.COLUMN_MIMETYPE, this.getClassToColumnType().getProperty(Constants.COLUMN_MIMETYPE, "-"));
                        definition.setProperty(this.COLUMN_SIZE, this.getClassToColumnType().getProperty(this.COLUMN_SIZE, "-"));
                        definition.setProperty("cm_workingCopyLlink", this.getClassToColumnType().getProperty(Constants.COLUMN_NODEREF, "-"));
                        definition.setProperty("cm_lockOwner", this.getClassToColumnType().getProperty(Constants.COLUMN_NODEREF, "-"));
                        definition.setProperty("cm_lockType", this.getClassToColumnType().getProperty(Constants.COLUMN_NODEREF, "-"));
                        definition.setProperty("cm_expiryDate", this.getClassToColumnType().getProperty("datetime", "-"));
                        definition.setProperty("sys_archivedDate", this.getClassToColumnType().getProperty("datetime", "-"));
                        definition.setProperty("sys_archivedBy", this.getClassToColumnType().getProperty(Constants.COLUMN_NODEREF, "-"));
                        definition.setProperty("sys_archivedOriginalOwner", this.getClassToColumnType().getProperty(Constants.COLUMN_NODEREF, "-"));
                        definition.setProperty("versioned", this.getClassToColumnType().getProperty(Constants.COLUMN_NODEREF, "-"));
                    }

                    if (this.getDictionaryService().isSubClass(type, ContentModel.TYPE_PERSON)) {
                        definition.setProperty("enabled", this.getClassToColumnType().getProperty("boolean", "-"));
                    }

                    if (this.getNodeService().hasAspect(nodeRef, ContentModel.ASPECT_VERSIONABLE)) {
                        String typeStr = this.getClassToColumnType().getProperty("text", "-");
                        if (this.getReplacementDataType().containsKey("cm_versionLabel")) {
                            typeStr = this.getReplacementDataType().getProperty("cm_versionLabel", "-").trim();
                        }

                        definition.setProperty("cm_versionLabel", typeStr);
                        typeStr = this.getClassToColumnType().getProperty("text", "-");
                        if (this.getReplacementDataType().containsKey("cm_versionType")) {
                            typeStr = this.getReplacementDataType().getProperty("cm_versionType", "-").trim();
                        }

                        definition.setProperty("cm_versionType", typeStr);
                    }
                }
            } catch (Exception var10) {
                logger.info("unexpeted error in node " + nodeRef.toString());
                logger.info("unexpeted error in node " + var10.getMessage());
            }

            try {
                definition = this.processAssociationDefinitions(definition, nodeRef);
            } catch (Exception var8) {
                logger.warn("Error getting assoc definitions" + var8.getMessage());
                var8.printStackTrace();
            }
        }

        return definition;
    }

    @Override
    void processQueueValues(String var1) throws Exception {

    }

    public void processQueueValues(String table, List<ReportLine> lines) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Enter processQueueValues table=" + table);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("************ Found " + this.queue.size() + " entries in " + table + " **************** " + this.method);
        }

        int queuesize = this.queue.size();

        Set<String> types = getTypesPerTable(table);

        ArrayList<InsertInto> inserts = new ArrayList<>();
        ArrayList<UpdateWhere> updates = new ArrayList<>();

        for (ReportLine rl : lines) {
            try {
                if (logger.isDebugEnabled()) {
                    String numberOfRows = (String) this.getNodeService().getProperty(new NodeRef(rl.getValue(Constants.COLUMN_NODEREF)), ContentModel.PROP_NAME);
                    logger.debug("processQueueValues: " + rl.getValue(Constants.COLUMN_NODEREF) + "/" + queuesize + ": " + numberOfRows);
                }

                String shortTypeName = rl.getValue(Constants.COLUMN_OBJECT_TYPE).toLowerCase();

                if (!types.contains(shortTypeName)) {
                    this.dbhb.createLastTypesTableRow(table, shortTypeName);
                    types.add(shortTypeName);
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("Current method=" + this.method);
                }

                try {
                    if (rl.size() > 0) {
                        if (this.method.equals("INSERT_ONLY")) {
                            inserts.add(insertIntoTableRecord(rl));
                        }

                        if (this.method.equals("SINGLE_INSTANCE")) {
                            if (this.dbhb.rowExists(rl)) {
                                updates.add(updateIntoTableRecord(rl));
                            } else {
                                inserts.add(insertIntoTableRecord(rl));
                            }
                        }

                        if (this.method.equals("UPDATE_VERSIONED")) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("Going UPDATE_VERSIONED");
                            }

                            try {
                                int var23;
                                if (this.dbhb.rowExists(rl)) {
                                    var23 = this.dbhb.updateVersionedIntoTable(rl);
                                    if (logger.isDebugEnabled()) {
                                        logger.debug(var23 + " rows updated");
                                    }
                                } else {
                                    if (logger.isDebugEnabled()) {
                                        logger.debug("No rows exist");
                                        logger.debug("## Set " + rl.getInsertListOfKeys());
                                        logger.debug("## Values " + rl.getInsertListOfValues());
                                    }

                                    inserts.add(insertIntoTableRecord(rl));
                                }
                            } catch (RecoverableDataAccessException var16) {
                                throw new AlfrescoRuntimeException("processQueueValues1: " + var16.getMessage());
                            } catch (Exception var17) {
                                var17.printStackTrace();
                                logger.fatal("processQueueValues Exception1: " + var17.getMessage());
                            }
                        }
                        this.getDbhb().getReportingDAO().deleteFromAssocsTable(new AssocDefinition(rl.getValue(Constants.COLUMN_NODEREF), null, null));
                    }
                } catch (RecoverableDataAccessException var18) {
                    throw new AlfrescoRuntimeException("processQueueValues2: " + var18.getMessage());
                } catch (Exception var19) {
                    logger.fatal("processQueueValues Exception2: " + Arrays.toString(var19.getStackTrace()));
                } finally {
                    rl.reset();
                }
            } catch (Exception var21) {
                logger.info("Bad node detected; ignoring... " + rl.getValue(Constants.COLUMN_NODEREF));
            }
        }

        String size = this.reportingHelper.getGlobalProperties().getProperty("reporting.harvest.batch.size", batchSize.toString());
        Integer batchSize = Integer.valueOf(size);

        SqlSession sqlSession = ((SqlSessionTemplate) ((ReportingDAOImpl) this.dbhb.getReportingDAO()).getTemplate()).getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
        sqlSession.getConnection().setAutoCommit(false);
        try {
            int count = 0;
            for (final InsertInto record : inserts) {
                count++;
                sqlSession.insert("reporting-insert-into-table", record);
                if (count % batchSize == 0) {
                    sqlSession.commit();
                    sqlSession.close();
                    sqlSession = ((SqlSessionTemplate) ((ReportingDAOImpl) this.dbhb.getReportingDAO()).getTemplate()).getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
                    sqlSession.getConnection().setAutoCommit(false);
                }
            }
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }

        sqlSession = ((SqlSessionTemplate) ((ReportingDAOImpl) this.dbhb.getReportingDAO()).getTemplate()).getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
        sqlSession.getConnection().setAutoCommit(false);
        try {
            int count = 0;
            for (final UpdateWhere record : updates) {
                count++;
                record.setTablename(record.getTablename().toLowerCase());
                sqlSession.update("reporting-update-into-table", record);
                if (count % batchSize == 0) {
                    sqlSession.commit();
                    sqlSession.close();
                    sqlSession = ((SqlSessionTemplate) ((ReportingDAOImpl) this.dbhb.getReportingDAO()).getTemplate()).getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
                    sqlSession.getConnection().setAutoCommit(false);
                }
            }
            sqlSession.commit();

        } finally {
            sqlSession.close();
        }

        sqlSession = ((SqlSessionTemplate) ((ReportingDAOImpl) this.dbhb.getReportingDAO()).getTemplate()).getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
        sqlSession.getConnection().setAutoCommit(false);
        try {
            int count = 0;
            for (AssocDefinition definition: this.getAssocs()) {
                count++;

                sqlSession.insert("assocs-insertValue", definition);
                if (count % batchSize == 0) {
                    sqlSession.commit();
                    sqlSession.close();
                    sqlSession = ((SqlSessionTemplate) ((ReportingDAOImpl) this.dbhb.getReportingDAO()).getTemplate()).getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
                    sqlSession.getConnection().setAutoCommit(false);
                }
            }
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Exit processQueueValues");
        }

    }

    private Set<String> getTypesPerTable(String table) throws Exception {
        List typesPerTable = this.dbhb.getTypesPerTable(table);
        Set<String> typesSet = new HashSet<>();
        Iterator rowIterator = typesPerTable.iterator();
        Iterator columnIterator;
        while (rowIterator.hasNext()) {
            Map row = (Map) rowIterator.next();
            columnIterator = row.keySet().iterator();
            while (columnIterator.hasNext()) {
                String keyString = (String) columnIterator.next();
                if ("typename".equals(keyString)) {
                    typesSet.add(row.get(keyString).toString());
                }
            }
        }

        return typesSet;
    }

    private Set<String> getTablesPerType(String type) throws Exception {
        List typesPerTable = this.dbhb.getTablesPerType(type);
        Set<String> tablesSet = new HashSet<>();
        Iterator rowIterator = typesPerTable.iterator();
        Iterator columnIterator;
        while (rowIterator.hasNext()) {
            Map row = (Map) rowIterator.next();
            columnIterator = row.keySet().iterator();
            while (columnIterator.hasNext()) {
                String keyString = (String) columnIterator.next();
                if (Constants.COLUMN_TABLENAME.equals(keyString)) {
                    tablesSet.add(row.get(keyString).toString());
                }
            }
        }

        return tablesSet;
    }

    private boolean isReportingDisabled(Properties configs) {
        boolean result = false;
        if (configs.containsKey("reporting.disable")) {
            String disabled = configs.getProperty("reporting.disable");
            if ("true".equalsIgnoreCase(disabled)) {
                logger.info("Reporting is disabled!");
                result = true;
            }
        }
        return result;
    }

    private boolean isRecordDeletionEnabled(Properties configs) {
        boolean result = false;
        if (configs.containsKey("reporting.records-delete.enabled")) {
            String enabled = configs.getProperty("reporting.records-delete.enabled");
            if ("true".equalsIgnoreCase(enabled)) {
                logger.info("Reporting Delete Records is enabled!");
                result = true;
            }
        } else {
            result = true; // если нет в конфиге - сбор включен, как и работало до
        }
        return result;
    }

    private boolean isReportingMustStop(Properties configs) {
        if (configs.containsKey("reporting.startHour") && configs.containsKey("reporting.endHour")) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            try {
                String start = configs.getProperty("reporting.startHour");
                String end = configs.getProperty("reporting.endHour");
                if (start.length() > 0 && end.length() > 0) {
                    int startHour = Integer.valueOf(start);
                    int endHour = Integer.valueOf(end);

                    boolean isMidnight = endHour < startHour;

                    if (isMidnight) {
                        return !(startHour <= hour || hour <= endHour);
                    } else {
                        return !(startHour <= hour && hour <= endHour);
                    }
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
        return false;
    }

    private Boolean isIncludeMetadata() {
        String include = this.reportingHelper.getGlobalProperties().getProperty("reporting.harvest.include.metadata", "false");
        return Boolean.parseBoolean(include);
    }

    private Properties getReportingConfigs() {
        final Properties p = new Properties();
        if (logger.isDebugEnabled()) {
            logger.debug("getReportingConfigs");
        }
        FileInputStream fis = null;
        try {
            String filePath = getGlobalProperties().getProperty("reporting.harvest.configFile.path", "-");
            if (!filePath.equals("-")) {
                File configFile = new File(filePath);
                if (configFile.exists() && configFile.length() > 0) {
                    fis = new FileInputStream(configFile);
                    p.load(fis);
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            IOUtils.closeQuietly(fis);
        }

        return p;
    }

    protected Map<Long,NodeRef> getRecordsToDelete(long minTxn) throws SQLException {
        Map<Long,NodeRef> records = new HashMap<>();

        java.sql.ResultSet results = null;
        java.sql.Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            String fullQuery = "SELECT np.long_value, node.uuid FROM alf_transaction txn " +
                    "JOIN alf_node node  ON (txn.id = node.transaction_id) " +
                    "JOIN alf_qname on (node.type_qname_id = alf_qname.id) " +
                    "LEFT OUTER JOIN alf_node_properties np on (np.node_id = node.id and np.qname_id = (SELECT id from alf_qname WHERE local_name = 'originalId')) " +
                    "WHERE alf_qname.local_name = 'deleted' AND " +
                    "node.transaction_id > ? ";

            connection = getReportingHelper().getAlfrescoDataSource().getConnection();
            preparedStatement = connection.prepareStatement(fullQuery);
            preparedStatement.setLong(1, minTxn);

            results = preparedStatement.executeQuery();

            while (results.next()) {
                Long e1 = results.getLong(1);
                String e2 = results.getString(2);
                logger.debug("Delete node with id= " + e1);
                records.put(e1, new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, e2));
            }
        } catch (SQLException e1) {
            logger.error("Error while database request " + "\r\n" + preparedStatement + "\r\n" + e1.getMessage());
        } finally {
            if (results != null && !results.isClosed()) {
                results.close();
            }
            if (preparedStatement != null && !preparedStatement.isClosed()) {
                preparedStatement.close();
            }
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        }
        return records;
    }

    protected void consumeMessagesAboutDelete(List<String> messagesIds) throws JMSException {
        // Create a Connection
        Connection connection = null;
        Session session = null;
        try {
            connection = connectionFactory.createConnection();
            connection.start();

            // Create a Session
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Get a destination
            Destination destination = templateProducer.getDefaultDestination();

            QueueBrowser browser = session.createBrowser((javax.jms.Queue) destination);
            Enumeration msgs = browser.getEnumeration();
            if (msgs.hasMoreElements()) {
                while (msgs.hasMoreElements()) {
                    Message tempMsg = (Message) msgs.nextElement();
                    if (messagesIds.contains(tempMsg.getJMSMessageID())) {
                        MessageConsumer consumer = session.createConsumer(destination, "JMSMessageID=\'" + tempMsg.getJMSMessageID() + "\'");
                        consumer.receive(1000);
                        consumer.close();
                    }
                    if (logger.isDebugEnabled()) {
                        logger.debug("Get message. Message:" + tempMsg);
                    }
                }
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("No messages in queue query! Query:" + ((Queue) destination).getQueueName());
                }
            }
        } catch (JMSException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (session != null) {
                session.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    private InsertInto insertIntoTableRecord(ReportLine rl) throws Exception {
        logger.debug("enter insertIntoTable");
        try {
            logger.debug("### sys_store_protocol=" + rl.getValue("sys_store_protocol"));
            Properties e = this.getReportingHelper().getClassToColumnType();
            Properties replacementTypes = this.getReportingHelper().getReplacementDataType();
            String insertInto;
            String validUntil;
            if ("archive".equals(rl.getValue("sys_store_protocol"))) {
                insertInto = rl.getValue("cm_created");
                validUntil = rl.getValue("sys_archiveddate");
                rl.setLine("isLatest", e.getProperty("boolean", "-"), "false", replacementTypes);
                rl.setLine("validFrom", e.getProperty("datetime", "-"), insertInto, replacementTypes);
                rl.setLine("validUntil", e.getProperty("datetime", "-"), validUntil, replacementTypes);
            } else if (rl.getValue(Constants.COLUMN_NODEREF) != null && rl.getValue(Constants.COLUMN_NODEREF).startsWith("version")) {
                insertInto = rl.getValue("cm_created");
                validUntil = rl.getValue("cm_modified");
                rl.setLine("isLatest", e.getProperty("boolean", "-"), "false", replacementTypes);
                rl.setLine("validFrom", e.getProperty("datetime", "-"), insertInto, replacementTypes);
                rl.setLine("validUntil", e.getProperty("datetime", "-"), validUntil, replacementTypes);
            } else {
                insertInto = rl.getValue("cm_modified");
                rl.setLine("validFrom", e.getProperty("datetime", "-"), insertInto, replacementTypes);
            }

            if (logger.isDebugEnabled()) {
                logger.debug("insertIntoTable table=" + rl.getTable());
                logger.debug("insertIntoTable keys=" + rl.getInsertListOfKeys());
                logger.debug("insertIntoTable values=" + rl.getInsertListOfValues());
            }

            return new InsertInto(rl.getTable(), rl.getInsertListOfKeys(), rl.getInsertListOfValues());
        } catch (Exception var7) {
            logger.fatal("Exception insertIntoTable: " + var7.getMessage());
            throw new Exception(var7);
        }
    }

    private UpdateWhere updateIntoTableRecord(ReportLine rl) throws Exception {
        logger.debug("enter updateIntoTable");
        try {
            return new UpdateWhere(rl.getTable(), rl.getUpdateSet(),
                    "sys_node_uuid=\'" + rl.getValue("sys_node_uuid") + "\' OR noderef=\'" + rl.getValue(Constants.COLUMN_NODEREF) + "\'");
        } catch (Exception var4) {
            logger.fatal("Exception updateIntoTable: " + var4.getMessage());
            throw new Exception(var4);
        }
    }

    private String toDisplayPath(Path path) {
        StringBuilder displayPath = new StringBuilder();
        if (path.size() == 1) {
            displayPath.append("/");
        } else {
            for (int i = 1; i < path.size(); ++i) {
                Element element = path.get(i);
                if (element instanceof ChildAssocElement) {
                    ChildAssociationRef assocRef = ((ChildAssocElement) element).getRef();
                    NodeRef node = assocRef.getChildRef();
                    displayPath.append("/");
                    displayPath.append(this.getNodeService().getProperty(node, ContentModel.PROP_NAME));
                }
            }
        }

        return displayPath.toString();
    }


    private Properties processAssociationDefinitions(Properties definition, NodeRef nodeRef) throws Exception {
        String blockNameSpaces;
        try {
            List childAssocs = this.getNodeService().getChildAssocs(nodeRef);
            if (childAssocs.size() > 0) {
                blockNameSpaces = this.getClassToColumnType().getProperty(Constants.COLUMN_NODEREFS, "-");
                if (this.getReplacementDataType().containsKey(Constants.COLUMN_CHILD_NODEREF)) {
                    blockNameSpaces = this.getReplacementDataType().getProperty(Constants.COLUMN_CHILD_NODEREF, "-").trim();
                }

                definition.setProperty(Constants.COLUMN_CHILD_NODEREF, blockNameSpaces);
            }
        } catch (Exception var19) {
            logger.error("processAssociationDefinitions: child_noderef ERROR! " + var19.getMessage());
        }

        try {
            ChildAssociationRef parentRef = this.getNodeService().getPrimaryParent(nodeRef);
            if (parentRef != null) {
                blockNameSpaces = this.getClassToColumnType().getProperty(Constants.COLUMN_NODEREFS, "-");
                if (this.getReplacementDataType().containsKey(Constants.COLUMN_PARENT_NODEREF)) {
                    blockNameSpaces = this.getReplacementDataType().getProperty(Constants.COLUMN_PARENT_NODEREF, "-").trim();
                }

                definition.setProperty(Constants.COLUMN_PARENT_NODEREF, blockNameSpaces);
            }
        } catch (Exception var18) {
            logger.error("processAssociationDefinitions: parent_noderef ERROR!");
        }

        try {
            Collection<QName> allAssocs = this.getDictionaryService().getAllAssociations();
            blockNameSpaces = this.globalProperties.getProperty("reporting.harvest.blockNameSpaces", "");
            String[] startValues = blockNameSpaces.split(",");

            for (QName type : allAssocs) {
                String key = "";
                String shortName = this.replaceNameSpaces(type);
                boolean stop = nodeRef.toString().startsWith("versionStore") || nodeRef.toString().startsWith("archive");

                for (String startValue : startValues) {
                    stop = shortName.startsWith(startValue.trim());
                    if (stop) {
                        break;
                    }
                }

                if (!stop) {
                    List var23;
                    String var24;
                    try {
                        var23 = this.getNodeService().getTargetAssocs(nodeRef, type);
                        if (var23.size() > 0) {
                            key = this.replaceNameSpaces(type);
                            if (!this.getBlacklist().toLowerCase().contains("," + key.toLowerCase() + ",") && !key.equals("-")) {
                                var24 = this.getClassToColumnType().getProperty(Constants.COLUMN_NODEREFS, "-");
                                if (this.getReplacementDataType().containsKey(key)) {
                                    var24 = this.getReplacementDataType().getProperty(key, "-").trim();
                                }

                                if (logger.isDebugEnabled()) {
                                    logger.debug("Target: Setting " + key + "=" + var24);
                                }

                                definition.setProperty(key, var24);
                            }
                        }
                    } catch (Exception var17) {
                        logger.error("processAssociationDefinitions: Target_Association ERROR! key=" + key);
                    }
                }
            }
        } catch (Exception var20) {
            logger.error("processAssociationDefinitions: source-target ERROR!");
        }

        return definition;
    }

    private ReportLine processAssociationValues(ReportLine rl, NodeRef nodeRef, Properties tableColumns)  throws Exception {
        try {
            List childAssocs = this.getNodeService().getChildAssocs(nodeRef);
            long min = (long) Math.min(childAssocs.size(),
                    Integer.parseInt(this.getGlobalProperties().getProperty("reporting.harvest.treshold.child.assocs", "20")));
            long shortName = childAssocs.size();
            if (shortName > 0L && shortName <= min) {
                StringBuilder maxChildCount = new StringBuilder();

                ChildAssociationRef numberOfChildCars;
                for (Iterator iterator = childAssocs.iterator(); iterator.hasNext(); maxChildCount.append(numberOfChildCars.getChildRef())) {
                    numberOfChildCars = (ChildAssociationRef) iterator.next();
                    if (maxChildCount.length() > 0) {
                        maxChildCount.append(",");
                    }
                }

                rl.setLine(Constants.COLUMN_CHILD_NODEREF, this.getClassToColumnType().getProperty(Constants.COLUMN_NODEREFS, "-"), maxChildCount.toString(), this.getReplacementDataType());
            } else if (shortName > 0L) { // достигли лимита - сообщение
                logger.warn("Max limit reached for node:" + nodeRef + " ," +
                        "reporting.harvest.treshold.child.assocs = " + min + ", number assocs=" + shortName);
            }
        } catch (Exception var19) {
            logger.warn("Error in processing processAssociationValues");
        }

        try {
            ChildAssociationRef primaryParent = this.getNodeService().getPrimaryParent(nodeRef);
            if (primaryParent != null) {
                String parentRef = primaryParent.getParentRef().toString();
                rl.setLine(Constants.COLUMN_PARENT_NODEREF, this.getClassToColumnType().getProperty(Constants.COLUMN_NODEREF, "-"), parentRef, this.getReplacementDataType());
            }
        } catch (Exception var16) {
            logger.warn("Exception in getting primary Parent noderef: " + var16.getMessage());
        }

        List<AssociationRef> targets = nodeService.getTargetAssocs(nodeRef, RegexQNamePattern.MATCH_ALL);
        HashMap<QName, List<NodeRef>> targetsByQName = new HashMap<>();
        for (AssociationRef assoc : targets) {
            List<NodeRef> list = targetsByQName.get(assoc.getTypeQName());
            if (list == null) {
                list = new ArrayList<>();
                targetsByQName.put(assoc.getTypeQName(), list);
            }
            list.add(assoc.getTargetRef());
        }

        for (QName type : targetsByQName.keySet()) {
            String key;
            StringBuilder value;
            long maxChildCount1;
            long numberOfChildCars1;
            try {
                List<NodeRef> e = targetsByQName.get(type);
                maxChildCount1 = (long) Math.min(e.size(), Integer.parseInt(this.getGlobalProperties().getProperty("reporting.harvest.treshold.sourcetarget.assocs", "20")));
                numberOfChildCars1 = (long) e.size();
                if (numberOfChildCars1 > 0L && numberOfChildCars1 <= maxChildCount1) {
                    key = this.replaceNameSpaces(type);
                    if (key.length() > 60) {
                        key = key.substring(0, 60);
                    }
                    if (!this.getBlacklist().toLowerCase().contains("," + key.toLowerCase() + ",") && !key.equals("-") && e.size() > 0) {
                        value = new StringBuilder();
                        for (NodeRef node : e) {
                            value.append(node.toString()).append(",");
                            AssocDefinition definition = new AssocDefinition(nodeRef.toString(), node.toString(), key);
                            this.getAssocs().add(definition);
                        }
                        String strValue = value.substring(0, value.length() - 1);
                        rl.setLine(key, this.getClassToColumnType().getProperty(Constants.COLUMN_NODEREFS, "-"), strValue, this.getReplacementDataType());
                    }
                } else if (numberOfChildCars1 > 0L) { //достигли лимита - сообщение
                    logger.warn("Max limit reached for node:" + nodeRef + " ,reporting.harvest.treshold.sourcetarget.assocs=" + maxChildCount1 + ", number assocs=" + numberOfChildCars1);
                }
            } catch (Exception ignored) {
                if (logger.isDebugEnabled()) {
                    logger.error(ignored.getMessage(), ignored);
                }
            }
        }

        QName objectType = nodeService.getType(nodeRef);
        Set<String> typeAssocs = null;
        if (typeAssocsDefinitions.containsKey(objectType)) {
            typeAssocs = typeAssocsDefinitions.get(objectType);
        } else {
            TypeDefinition typeDef = this.getDictionaryService().getType(objectType);
            if (typeDef != null) {
                typeAssocs = new HashSet<>();
                Map<QName, AssociationDefinition> associations = new HashMap<>();
                associations.putAll(typeDef.getAssociations());

                List<AspectDefinition> defaultAspects = typeDef.getDefaultAspects(true);
                for (AspectDefinition defaultAspect : defaultAspects) {
                    associations.putAll(defaultAspect.getAssociations());
                }

                for (QName assocName : associations.keySet()) {
                    String shortName = this.replaceNameSpaces(assocName);
                    typeAssocs.add(shortName);
                }
                typeAssocsDefinitions.put(objectType, typeAssocs);
            }
        }
        if (typeAssocs != null && tableColumns != null) {
            for (String typeAssoc : typeAssocs) {
                if (rl.getType(typeAssoc) == null && tableColumns.containsKey(typeAssoc)) {
                    rl.setLine(typeAssoc, this.getClassToColumnType().getProperty(Constants.COLUMN_NODEREFS, "-"), "", this.getReplacementDataType());
                }
            }
        }
        return rl;
    }
}
