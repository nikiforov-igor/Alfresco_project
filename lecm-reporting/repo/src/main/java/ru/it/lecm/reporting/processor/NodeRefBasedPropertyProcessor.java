package ru.it.lecm.reporting.processor;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.site.SiteModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.cmr.repository.Path.ChildAssocElement;
import org.alfresco.service.cmr.repository.Path.Element;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.namespace.QName;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.jms.core.JmsTemplate;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.reporting.ReportLine;
import ru.it.lecm.reporting.ReportingHelper;
import ru.it.lecm.reporting.mybatis.UpdateWhere;

import javax.jms.*;
import javax.jms.Queue;
import java.io.IOException;
import java.util.*;

public class NodeRefBasedPropertyProcessor extends PropertyProcessor {
    private VersionService versionService;
    private ContentService contentService;
    private String COLUMN_SIZE = "";
    private static Log logger = LogFactory.getLog(NodeRefBasedPropertyProcessor.class);
    private Integer minutesShift = 30; //
    private Integer solrMinutesShift = 10; //
    private ActiveMQConnectionFactory connectionFactory;
    private JmsTemplate templateProducer;

    public ContentService getContentService() {
        return this.contentService;
    }

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }

    public NodeRefBasedPropertyProcessor(ServiceRegistry serviceRegistry, ReportingHelper helper) throws Exception {
        this.setNodeService(serviceRegistry.getNodeService());
        this.setNamespaceService(serviceRegistry.getNamespaceService());
        this.setDictionaryService(serviceRegistry.getDictionaryService());
        this.setFileFolderService(serviceRegistry.getFileFolderService());
        this.setContentService(serviceRegistry.getContentService());
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

    private String getSiteName(NodeRef currentRef) {
        String siteName = "";
        if (currentRef != null) {
            NodeRef rootNode = this.getNodeService().getRootNode(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
            NodeRef siteRef = null;
            boolean siteTypeFound = this.getNodeService().getType(currentRef).equals(SiteModel.TYPE_SITE);
            if (siteTypeFound) {
                siteRef = currentRef;
            }

            while (!currentRef.equals(rootNode) && !siteTypeFound) {
                currentRef = this.getNodeService().getPrimaryParent(currentRef).getParentRef();
                siteTypeFound = this.getNodeService().getType(currentRef).equals(SiteModel.TYPE_SITE);
                if (siteTypeFound) {
                    siteRef = currentRef;
                }
            }

            if (siteRef != null) {
                siteName = (String) this.getNodeService().getProperty(siteRef, ContentModel.PROP_NAME);
            }
        }

        return siteName;
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
            List e = this.getNodeService().getChildAssocs(nodeRef);
            if (e.size() > 0) {
                blockNameSpaces = this.getClassToColumnType().getProperty("noderefs", "-");
                if (this.getReplacementDataType().containsKey("child_noderef")) {
                    blockNameSpaces = this.getReplacementDataType().getProperty("child_noderef", "-").trim();
                }

                definition.setProperty("child_noderef", blockNameSpaces);
            }
        } catch (Exception var19) {
            logger.error("processAssociationDefinitions: child_noderef ERROR! " + var19.getMessage());
        }

        try {
            ChildAssociationRef var21 = this.getNodeService().getPrimaryParent(nodeRef);
            if (var21 != null) {
                blockNameSpaces = this.getClassToColumnType().getProperty("noderef", "-");
                if (this.getReplacementDataType().containsKey("parent_noderef")) {
                    blockNameSpaces = this.getReplacementDataType().getProperty("parent_noderef", "-").trim();
                }

                definition.setProperty("parent_noderef", blockNameSpaces);
            }
        } catch (Exception var18) {
            logger.error("processAssociationDefinitions: parent_noderef ERROR!");
        }

        try {
            Collection var22 = this.getDictionaryService().getAllAssociations();
            blockNameSpaces = this.globalProperties.getProperty("reporting.harvest.blockNameSpaces", "");
            String[] startValues = blockNameSpaces.split(",");

            for (Object aVar22 : var22) {
                QName type = (QName) aVar22;
                String key = "";
                String shortName = this.replaceNameSpaces(type.toString());
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
                            key = type.toString();
                            key = this.replaceNameSpaces(key);
                            if (!this.getBlacklist().toLowerCase().contains("," + key.toLowerCase() + ",") && !key.equals("-")) {
                                var24 = this.getClassToColumnType().getProperty("noderefs", "-");
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

                    try {
                        var23 = this.getNodeService().getSourceAssocs(nodeRef, type);
                        if (var23.size() > 0) {
                            logger.debug("Found a Source association! " + type.toString());
                            key = type.toString();
                            key = this.replaceNameSpaces(key);
                            if (!this.getBlacklist().toLowerCase().contains("," + key.toLowerCase() + ",") && !key.equals("-")) {
                                var24 = this.getClassToColumnType().getProperty("noderefs", "-");
                                if (this.getReplacementDataType().containsKey(key)) {
                                    var24 = this.getReplacementDataType().getProperty(key, "-").trim();
                                }

                                definition.setProperty(key, var24);
                            }
                        }
                    } catch (Exception var16) {
                        logger.warn("processAssociationDefinitions: Source_Association ERROR! key=" + key);
                        logger.warn(" Messg: " + var16.getMessage());
                        logger.warn(" Cause: " + var16.getCause());
                        logger.warn(" Error: " + var16.toString());
                    }
                }
            }
        } catch (Exception var20) {
            logger.warn("processAssociationDefinitions: source-target ERROR!");
            var20.printStackTrace();
        }

        return definition;
    }

    private ReportLine processAssociationValues(ReportLine rl, NodeRef nodeRef) throws Exception {
        try {
            List assocTypes = this.getNodeService().getChildAssocs(nodeRef);
            long i$ = (long) Math.min(assocTypes.size(), Integer.parseInt(this.getGlobalProperties().getProperty("reporting.harvest.treshold.child.assocs", "20")));
            long shortName = (long) assocTypes.size();
            if (shortName > 0L && shortName <= i$) {
                String maxChildCount = "";

                ChildAssociationRef numberOfChildCars;
                for (Iterator i$1 = assocTypes.iterator(); i$1.hasNext(); maxChildCount = maxChildCount + numberOfChildCars.getChildRef()) {
                    numberOfChildCars = (ChildAssociationRef) i$1.next();
                    if (maxChildCount.length() > 0) {
                        maxChildCount = maxChildCount + ",";
                    }
                }

                rl.setLine("child_noderef", this.getClassToColumnType().getProperty("noderefs", "-"), maxChildCount, this.getReplacementDataType());
            }
        } catch (Exception var19) {
            logger.warn("Error in processing processAssociationValues");
            var19.printStackTrace();
        }

        try {
            ChildAssociationRef assocTypes1 = this.getNodeService().getPrimaryParent(nodeRef);
            if (assocTypes1 != null) {
                String i$3 = assocTypes1.getParentRef().toString();
                rl.setLine("parent_noderef", this.getClassToColumnType().getProperty("noderef", "-"), i$3, this.getReplacementDataType());
            }
        } catch (Exception var16) {
            logger.warn("Exception in getting primary Parent noderef: " + var16.getMessage());
        }

        Collection assocTypes2 = this.getDictionaryService().getAllAssociations();

        for (Object anAssocTypes2 : assocTypes2) {
            QName type = (QName) anAssocTypes2;
            String shortName1 = this.replaceNameSpaces(type.toString());
            if (!shortName1.startsWith("trx") && !shortName1.startsWith("act") && !shortName1.startsWith("wca")) {
                List e;
                String key;
                String value;
                Iterator i$2;
                AssociationRef ar;
                long maxChildCount1;
                long numberOfChildCars1;
                try {
                    e = this.getNodeService().getTargetAssocs(nodeRef, type);
                    maxChildCount1 = (long) Math.min(e.size(), Integer.parseInt(this.getGlobalProperties().getProperty("reporting.harvest.treshold.sourcetarget.assocs", "20")));
                    numberOfChildCars1 = (long) e.size();
                    if (numberOfChildCars1 > 0L && numberOfChildCars1 <= maxChildCount1) {
                        key = type.toString();
                        key = this.replaceNameSpaces(key);
                        if (!this.getBlacklist().toLowerCase().contains("," + key.toLowerCase() + ",") && !key.equals("-") && e.size() > 0) {
                            value = "";

                            for (i$2 = e.iterator(); i$2.hasNext(); value = value + ar.getTargetRef().toString()) {
                                ar = (AssociationRef) i$2.next();
                                if (value.length() > 0) {
                                    value = value + ",";
                                }
                            }

                            rl.setLine(key, this.getClassToColumnType().getProperty("noderefs", "-"), value, this.getReplacementDataType());
                        }
                    }
                } catch (Exception ignored) {
                    if (logger.isDebugEnabled()){
                        logger.error(ignored.getMessage(), ignored);
                    }
                }

                try {
                    e = this.getNodeService().getSourceAssocs(nodeRef, type);
                    maxChildCount1 = (long) Math.min(e.size(), Integer.parseInt(this.getGlobalProperties().getProperty("reporting.harvest.treshold.sourcetarget.assocs", "20")));
                    numberOfChildCars1 = (long) e.size();
                    if (numberOfChildCars1 > 0L && numberOfChildCars1 <= maxChildCount1) {
                        key = type.toString();
                        key = this.replaceNameSpaces(key);
                        if (!this.getBlacklist().toLowerCase().contains("," + key.toLowerCase() + ",") && !key.equals("-") && e != null && e.size() > 0) {
                            value = "";

                            for (i$2 = e.iterator(); i$2.hasNext(); value = value + ar.getSourceRef().toString()) {
                                ar = (AssociationRef) i$2.next();
                                if (value.length() > 0) {
                                    value = value + ",";
                                }
                            }

                            rl.setLine(key, this.getClassToColumnType().getProperty("noderefs", "-"), value, this.getReplacementDataType());
                        }
                    }
                } catch (Exception ignored) {
                    if (logger.isDebugEnabled()){
                        logger.error(ignored.getMessage(), ignored);
                    }
                }
            }
        }

        return rl;
    }

    protected ReportLine processNodeToMap(String identifier, String table, ReportLine rl) {
        this.dbhb.fixTableColumnName(table);
        if (logger.isDebugEnabled()) {
            logger.debug("processNodeToMap, identifier=" + identifier);
        }

        NodeRef masterRef = null;
        NodeRef nodeRef = new NodeRef(identifier.split(",")[0]);
        if (identifier.contains(",")) {
            masterRef = new NodeRef(identifier.split(",")[1]);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Enter processNodeToMap nodeRef=" + nodeRef);
        }

        try {
            rl = this.processPropertyValues(rl, nodeRef);
        } catch (Exception var28) {
            var28.printStackTrace();
        }

        try {
            rl = this.processAssociationValues(rl, nodeRef);
        } catch (Exception var27) {
            var27.printStackTrace();
        }

        try {
            rl.setLine("noderef", this.getClassToColumnType().getProperty("noderef"), nodeRef.toString(), this.getReplacementDataType());
        } catch (Exception var26) {
            var26.printStackTrace();
        }

        String aspectString;
        try {
            aspectString = this.nodeService.getType(nodeRef).toPrefixString(namespaceService);
            rl.setLine("object_type", this.getClassToColumnType().getProperty("object_type", ""), aspectString, this.getReplacementDataType());
        } catch (Exception var25) {
            logger.debug("EXCEPTION: // it does not have a Type. Bad luck. Don\'t crash (versionStore?!)");
        }

        aspectString = "";

        try {
            Set path = this.nodeService.getAspects(nodeRef);

            QName site;
            for (Iterator displayPath = path.iterator(); displayPath.hasNext(); aspectString = aspectString + site.getLocalName()) {
                site = (QName) displayPath.next();
                if (aspectString.length() > 0) {
                    aspectString = aspectString + ",";
                }
            }

            rl.setLine("aspects", this.getClassToColumnType().getProperty("aspects", ""), aspectString, this.getReplacementDataType());
        } catch (Exception var29) {
            logger.error(var29.getMessage(), var29);
        }

        String displayPath1;

        try {
            Path path1 = this.getNodeService().getPath(nodeRef);
            displayPath1 = this.toDisplayPath(path1);
            rl.setLine("path", this.getClassToColumnType().getProperty("path"), displayPath1, this.getReplacementDataType());
        } catch (Exception var24) {
            logger.error(var24.getMessage(), var24);
        }

        String site1;

        try {
            site1 = this.getSiteName(nodeRef);
            rl.setLine("site", this.getClassToColumnType().getProperty("site"), site1, this.getReplacementDataType());
        } catch (Exception ignored) {
            if (logger.isDebugEnabled()) {
                logger.error(ignored.getMessage(), ignored);
            }
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

                rl.setLine("orig_noderef", this.getClassToColumnType().getProperty("noderef"), size.toString(), this.getReplacementDataType());
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("Setting currentRef to orig_noderef!!!");
                }

                rl.setLine("orig_noderef", this.getClassToColumnType().getProperty("noderef"), nodeRef.toString(), this.getReplacementDataType());
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

                rl.setLine("mimetype", this.getClassToColumnType().getProperty("mimetype"), origNodeRef, this.getReplacementDataType());
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
                        logger.debug("Master says: " + (masterRef != null ? masterRef.toString() : null));
                    }

                    rl.setLine("orig_noderef", this.getClassToColumnType().getProperty("noderef"), masterRef != null ? masterRef.toString() : null, this.getReplacementDataType());
                } else if (origNodeRef1 != null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Setting Ref from archive to orig_noderef!!!");
                    }

                    rl.setLine("orig_noderef", this.getClassToColumnType().getProperty("noderef"), origNodeRef1.toString(), this.getReplacementDataType());
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Setting currentRef to orig_noderef!!!");
                    }

                    rl.setLine("orig_noderef", this.getClassToColumnType().getProperty("noderef"), nodeRef.toString(), this.getReplacementDataType());
                }
            } catch (Exception ignored) {
                if (logger.isDebugEnabled()) {
                    logger.error(ignored.getMessage(), ignored);
                }
            }
        }

        return rl;
    }

    public Properties processQueueDefinition(String table) {
        Properties definition = new Properties();
        for (Object aQueue : this.queue) {
            NodeRef nodeRef = new NodeRef(aQueue.toString().split(",")[0]);

            try {
                definition = this.processPropertyDefinitions(definition, nodeRef);
                definition.setProperty("orig_noderef", this.getClassToColumnType().getProperty("noderef", "-"));
            } catch (Exception var9) {
                logger.warn("processQueueDefinition: ERROR: versionNodes.containsKey or before " + var9.getMessage());
                var9.printStackTrace();
            }

            try {
                definition.setProperty("orig_noderef", this.getClassToColumnType().getProperty("noderef", "-"));
                definition.setProperty("site", this.getClassToColumnType().getProperty("site", "-"));
                definition.setProperty("path", this.getClassToColumnType().getProperty("path", "-"));
                definition.setProperty("noderef", this.getClassToColumnType().getProperty("noderef", "-"));
                definition.setProperty("object_type", this.getClassToColumnType().getProperty("object_type", "-"));
                definition.setProperty("aspects", this.getClassToColumnType().getProperty("aspects", "-"));
                QName var11 = this.getNodeService().getType(nodeRef);
                if (logger.isDebugEnabled()) {
                    logger.debug("processQueueDefinition: qname=" + var11);
                }

                if (!this.getDictionaryService().isSubClass(var11, ContentModel.TYPE_CONTENT) && !this.getDictionaryService().getType(var11).toString().equalsIgnoreCase(ContentModel.TYPE_CONTENT.toString())) {
                    logger.debug("processQueueDefinition: NOOOOO! We are NOT a subtype of Content!");
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("processQueueDefinition: YEAH! We are a subtype of Content! " + ContentModel.TYPE_CONTENT);
                    }

                    definition.setProperty("mimetype", this.getClassToColumnType().getProperty("mimetype", "-"));
                    definition.setProperty(this.COLUMN_SIZE, this.getClassToColumnType().getProperty(this.COLUMN_SIZE, "-"));
                    definition.setProperty("cm_workingCopyLlink", this.getClassToColumnType().getProperty("noderef", "-"));
                    definition.setProperty("cm_lockOwner", this.getClassToColumnType().getProperty("noderef", "-"));
                    definition.setProperty("cm_lockType", this.getClassToColumnType().getProperty("noderef", "-"));
                    definition.setProperty("cm_expiryDate", this.getClassToColumnType().getProperty("datetime", "-"));
                    definition.setProperty("sys_archivedDate", this.getClassToColumnType().getProperty("datetime", "-"));
                    definition.setProperty("sys_archivedBy", this.getClassToColumnType().getProperty("noderef", "-"));
                    definition.setProperty("sys_archivedOriginalOwner", this.getClassToColumnType().getProperty("noderef", "-"));
                    definition.setProperty("versioned", this.getClassToColumnType().getProperty("noderef", "-"));
                }

                if (this.getDictionaryService().isSubClass(var11, ContentModel.TYPE_PERSON)) {
                    definition.setProperty("enabled", this.getClassToColumnType().getProperty("boolean", "-"));
                }

                if (this.getNodeService().hasAspect(nodeRef, ContentModel.ASPECT_VERSIONABLE)) {
                    String type = this.getClassToColumnType().getProperty("text", "-");
                    if (this.getReplacementDataType().containsKey("cm_versionLabel")) {
                        type = this.getReplacementDataType().getProperty("cm_versionLabel", "-").trim();
                    }

                    definition.setProperty("cm_versionLabel", type);
                    type = this.getClassToColumnType().getProperty("text", "-");
                    if (this.getReplacementDataType().containsKey("cm_versionType")) {
                        type = this.getReplacementDataType().getProperty("cm_versionType", "-").trim();
                    }

                    definition.setProperty("cm_versionType", type);
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

    public void processQueueValues(String table) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Enter processQueueValues table=" + table);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("************ Found " + this.queue.size() + " entries in " + table + " **************** " + this.method);
        }

        ReportLine rl = new ReportLine(table, this.getSimpleDateFormat(), this.reportingHelper);
        int queuesize = this.queue.size();

        Set<String> types = getTypesPerTable(table);

        for (int q = 0; q < this.queue.size(); ++q) {
            String identifier = this.queue.get(q).toString();

            try {
                NodeRef nodeRef = new NodeRef(identifier.split(",")[0]);

                if (logger.isDebugEnabled()) {
                    String numberOfRows = (String) this.getNodeService().getProperty(nodeRef, ContentModel.PROP_NAME);
                    logger.debug("processQueueValues: " + q + "/" + queuesize + ": " + numberOfRows);
                }

                rl = this.processNodeToMap(identifier, table, rl);

                String shortTypeName = rl.getValue("object_type");
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
                            this.dbhb.insertIntoTable(rl);
                        }

                        if (this.method.equals("SINGLE_INSTANCE")) {
                            if (this.dbhb.rowExists(rl)) {
                                this.dbhb.updateIntoTable(rl);
                            } else {
                                this.dbhb.insertIntoTable(rl);
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

                                    var23 = this.dbhb.insertIntoTable(rl);
                                    if (logger.isDebugEnabled()) {
                                        logger.debug(var23 + " rows inserted");
                                    }
                                }
                            } catch (RecoverableDataAccessException var16) {
                                throw new AlfrescoRuntimeException("processQueueValues1: " + var16.getMessage());
                            } catch (Exception var17) {
                                var17.printStackTrace();
                                logger.fatal("processQueueValues Exception1: " + var17.getMessage());
                            }
                        }
                    }
                } catch (RecoverableDataAccessException var18) {
                    throw new AlfrescoRuntimeException("processQueueValues2: " + var18.getMessage());
                } catch (Exception var19) {
                    logger.fatal("processQueueValues Exception2: " + var19.getStackTrace());
                } finally {
                    rl.reset();
                }
            } catch (Exception var21) {
                logger.info("Bad node detected; ignoring... " + identifier);
            }
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
                if ("tablename".equals(keyString)) {
                    tablesSet.add(row.get(keyString).toString());
                }
            }
        }

        return tablesSet;
    }

    public void addToQueue(Object nodeRef, NodeRef masterRef) {
        this.addToQueue(nodeRef.toString() + "," + masterRef.toString());
    }

    public void havestNodes(NodeRef harvestDefinition) {
        try {
            List e = this.getStoreRefList();
            Properties queries = this.getTableQueries(harvestDefinition);
            String language = this.reportingHelper.getSearchLanguage(harvestDefinition);
            this.dbhb.openReportingConnection();
            Enumeration keys = queries.keys();

            long shiftFromDB = getTimestampFromDBShift() * 60000;

            this.dbhb.createEmptyTypeTablesTable();

            Date lastSolrIndexDate = null;
            long solrTimestamp = this.reportingHelper.getSolrLastTimestamp();
            if (solrTimestamp > 0) {
                long solrShift = getSolrTimestampShift() * 60000;
                lastSolrIndexDate = new Date(solrTimestamp - solrShift);
            }

            List<Message> messagesAboutDelete = getMessagesAboutDelete();

            while (keys.hasMoreElements()) {
                String tableName = (String) keys.nextElement();
                String query = (String) queries.get(tableName);
                tableName = this.dbhb.fixTableColumnName(tableName);
                Iterator storeIterator = e.iterator();
                if (!this.dbhb.tableIsRunning(tableName)) {
                    this.dbhb.createEmptyTables(tableName);

                    Date theDate = new Date();
                    String nowFormattedDate = this.reportingHelper.getSimpleDateFormat().format(theDate);

                    String timestampStart = this.dbhb.getLastTimestamp(tableName);
                    Date lastSyncDate = this.reportingHelper.getSimpleDateFormat().parse(timestampStart.replace("T", " "));
                    lastSyncDate = new Date(lastSyncDate.getTime() - shiftFromDB);

                    if (lastSolrIndexDate != null) {
                        if (lastSolrIndexDate.before(lastSyncDate)) {
                            lastSyncDate = lastSolrIndexDate;
                        }
                    }
                    timestampStart = this.reportingHelper.getSimpleDateFormat().format(lastSyncDate);

                    this.dbhb.setLastTimestampStatusRunning(tableName);
                    while (storeIterator.hasNext()) {
                        StoreRef storeRef = (StoreRef) storeIterator.next();
                        if (logger.isDebugEnabled()) {
                            logger.debug("harvest: StoreRef=" + storeRef.getProtocol());
                        }

                        long startDbId = 0L;
                        long loopcount = 0L;
                        boolean letsContinue = true;

                        while (letsContinue) {
                            ++loopcount;
                            String fullQuery = query + this.queryClauseTimestamp(language, timestampStart, storeRef.getProtocol()) + this.queryClauseOrderBy(language, startDbId);
                            if (logger.isInfoEnabled()) {
                                logger.info("harvest: StoreProtocol = " + storeRef.getProtocol() + " fullQuery = " + fullQuery);
                            }

                            SearchParameters sp = new SearchParameters();
                            sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
                            sp.addStore(storeRef);
                            sp.setQuery(fullQuery);
                            sp.addSort("@{http://www.alfresco.org/model/system/1.0}node-dbid", true);
                            ResultSet results = this.getSearchService().query(sp);
                            letsContinue = results.length() > 0;
                            logger.info("harvest: loopCount = " + loopcount + " letsContinue = " + letsContinue);
                            if (letsContinue) {
                                for (ResultSetRow result : results) {
                                    try {
                                        NodeRef e1 = result.getNodeRef();
                                        logger.debug("harvest nodeRef " + e1);
                                        if (!e1.toString().startsWith("version")) {
                                            if (logger.isInfoEnabled()) {
                                                logger.info("harvest:  adding NodeRef " + e1);
                                            }

                                            this.addToQueue(e1);
                                        }
                                    } catch (Exception var35) {
                                        logger.info("NodeRef appears broken: " + var35.getMessage());
                                        logger.info("   " + var35.getStackTrace());
                                    }
                                }

                                try {
                                    Properties var38 = this.processQueueDefinition(tableName);
                                    if (logger.isInfoEnabled()) {
                                        logger.info("harvest: queueDef done, setting tableDefinition");
                                    }

                                    this.setTableDefinition(tableName, var38);
                                    if (logger.isInfoEnabled()) {
                                        logger.info("harvest: tableDef done. Processing queue Values");
                                    }

                                    this.processQueueValues(tableName);
                                    this.resetQueue();
                                    startDbId = Long.parseLong(String.valueOf(this.getNodeService().getProperty(results.getNodeRef(results.length() - 1), ContentModel.PROP_NODE_DBID)));
                                    if (logger.isDebugEnabled()) {
                                        logger.debug("harvest: StoreProtocol = " + storeRef.getProtocol());
                                        logger.debug("harvest: New start DBID=" + startDbId);
                                    }
                                } catch (Exception var34) {
                                    logger.info("harvest: something wrong with the noderef, skipping");
                                }
                            }
                        }
                    }
                    this.dbhb.setLastTimestampAndStatusDone(tableName, nowFormattedDate);

                }
            }
            if (!this.dbhb.tableIsRunning("refresh_deleted_rows")) {
                Map<String, Set> typeTablesMap = new HashMap<>();
                List<String> processedMessages = new ArrayList<>();
                for (Message deleteMessage : messagesAboutDelete) {
                    if (deleteMessage instanceof TextMessage) {
                        String messageText = ((TextMessage) deleteMessage).getText();
                        if (messageText != null) {
                            JSONObject messagesObject = new JSONObject(messageText);
                            String mainObjectRef = messagesObject.getString("mainObject");
                            if (NodeRef.isNodeRef(mainObjectRef)) {
                                NodeRef mainObject = new NodeRef(mainObjectRef);
                                String shortTypeName;
                                String objectType = messagesObject.getString("objectType");
                                if (NodeRef.isNodeRef(objectType)) {
                                    NodeRef type = new NodeRef(objectType);
                                    shortTypeName = (String) nodeService.getProperty(type, BusinessJournalService.PROP_OBJ_TYPE_CLASS);

                                    Set<String> tables;
                                    if (typeTablesMap.containsKey(shortTypeName)) {
                                        tables = typeTablesMap.get(shortTypeName);
                                    } else {
                                        tables = getTablesPerType(shortTypeName);
                                        typeTablesMap.put(shortTypeName, tables);
                                    }
                                    for (String table : tables) {
                                        UpdateWhere updateWhere =
                                                new UpdateWhere(table,
                                                        "",
                                                        "sys_node_uuid LIKE \'" + mainObject.getId() + "\'");
                                        dbhb.getReportingDAO().deleteFromTable(updateWhere);
                                    }
                                    processedMessages.add(deleteMessage.getJMSMessageID());
                                } else {
                                    logger.error("Message has no type!!! Message:" +  messageText +"\n");
                                    processedMessages.add(deleteMessage.getJMSMessageID());
                                }
                            }
                        }
                    }
                }
                this.consumeMessagesAboutDelete(processedMessages);
            }
        } catch (Exception var36) {
            logger.info("Fatality: " + var36.getMessage());
        } finally {
            this.dbhb.closeReportingConnection();
        }
    }

    private Integer getTimestampFromDBShift() {
        String shift = this.reportingHelper.getGlobalProperties().getProperty("reporting.harvest.timestamp.fromDB.shift", minutesShift.toString());
        return Integer.parseInt(shift);
    }
    private Integer getSolrTimestampShift() {
        String shift = this.reportingHelper.getGlobalProperties().getProperty("reporting.harvest.timestamp.solr.shift", solrMinutesShift.toString());
        return Integer.parseInt(shift);
    }

    private ArrayList getStoreRefList() {
        String[] stores = this.reportingHelper.getGlobalProperties().getProperty("reporting.harvest.stores", "").split(",");
        ArrayList storeRefArray = new ArrayList();

        for (String store : stores) {
            logger.debug("Adding store: " + store);
            StoreRef s = new StoreRef(store);
            storeRefArray.add(s);
        }

        return storeRefArray;
    }

    public String queryClauseTimestamp(String language, String timestamp1, String protocol) {
        String dateQuery = " ";
        String myTimestamp1 = timestamp1.replaceAll(" ", "T");
        if ("lucene".equalsIgnoreCase(language)) {
            if (protocol.equalsIgnoreCase("workspace")) {
                dateQuery = dateQuery + "AND @cm\\:modified:['" + myTimestamp1 + "' TO NOW]";
            }

            if (protocol.equalsIgnoreCase("archive")) {
                dateQuery = dateQuery + "AND @sys\\:archivedDate:['" + myTimestamp1 + "' TO NOW]";
            }
        }

        return dateQuery;
    }

    public String queryClauseOrderBy(String language, long dbid) {
        String orderBy = "";
        if ("lucene".equalsIgnoreCase(language) && dbid > 0L) {
            orderBy = orderBy + "AND @sys\\:node\\-dbid:[" + (dbid + 1L) + " TO MAX]";
        }

        return orderBy;
    }

    public String getOrderBy(String language) {
        String orderBy = " ";
        if ("lucene".equalsIgnoreCase(language)) {
            orderBy = orderBy + ContentModel.PROP_NODE_DBID.toString();
        }

        return orderBy;
    }

    private Properties getTableQueries(NodeRef nodeRef) {
        Properties p = new Properties();
        if (logger.isDebugEnabled()) {
            logger.debug("getTableQueries, nodeRef=" + nodeRef);
        }

        try {
            ContentService e = this.getContentService();
            ContentReader contentReader = e.getReader(nodeRef, ContentModel.PROP_CONTENT);
            p.load(contentReader.getContentInputStream());
        } catch (ContentIOException var5) {
            logger.error(var5.getMessage());
        } catch (IOException var6) {
            var6.printStackTrace();
            logger.error(var6.getMessage());
        }

        return p;
    }

    protected List<Message> getMessagesAboutDelete() throws JMSException {
        List<Message> messages = new ArrayList<>();

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
                    messages.add(tempMsg);

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
        return messages;
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
                        MessageConsumer consumer = session.createConsumer(destination, "JMSMessageID=\'" + tempMsg.getJMSMessageID() +"\'");
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

    public void setConnectionFactory(ActiveMQConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void setTemplateProducer(JmsTemplate templateProducer) {
        this.templateProducer = templateProducer;
    }
}
