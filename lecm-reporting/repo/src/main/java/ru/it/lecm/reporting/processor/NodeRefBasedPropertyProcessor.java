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
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.jms.core.JmsTemplate;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.reporting.Constants;
import ru.it.lecm.reporting.ReportLine;
import ru.it.lecm.reporting.ReportingHelper;
import ru.it.lecm.reporting.mybatis.UpdateWhere;

import javax.jms.*;
import javax.jms.Queue;
import java.util.*;

public class NodeRefBasedPropertyProcessor extends PropertyProcessor {
    private String COLUMN_SIZE = "";
    private static Log logger = LogFactory.getLog(NodeRefBasedPropertyProcessor.class);

    private ActiveMQConnectionFactory connectionFactory;
    private JmsTemplate templateProducer;

    private Map<QName, Properties> typePropsDefinitions = new HashMap<>();
    private Map<QName, Properties> typeAssocsDefinitions = new HashMap<>();

    public NodeRefBasedPropertyProcessor(ServiceRegistry serviceRegistry, ReportingHelper helper) throws Exception {
        this.setNodeService(serviceRegistry.getNodeService());
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

    public void setConnectionFactory(ActiveMQConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void setTemplateProducer(JmsTemplate templateProducer) {
        this.templateProducer = templateProducer;
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

    private Properties processAssociationDefinitions(Properties definition, NodeRef nodeRef, QName objectType) throws Exception {
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
                blockNameSpaces = this.getClassToColumnType().getProperty(Constants.COLUMN_NODEREF, "-");
                if (this.getReplacementDataType().containsKey(Constants.COLUMN_PARENT_NODEREF)) {
                    blockNameSpaces = this.getReplacementDataType().getProperty(Constants.COLUMN_PARENT_NODEREF, "-").trim();
                }

                definition.setProperty(Constants.COLUMN_PARENT_NODEREF, blockNameSpaces);
            }
        } catch (Exception var18) {
            logger.error("processAssociationDefinitions: parent_noderef ERROR!");
        }

        TypeDefinition typeDef = this.getDictionaryService().getType(objectType);
        blockNameSpaces = this.globalProperties.getProperty("reporting.harvest.blockNameSpaces", "");
        String[] startValues = blockNameSpaces.split(",");

        if (typeAssocsDefinitions.containsKey(objectType)) {
            definition.putAll(typeAssocsDefinitions.get(objectType));
        } else {
            Properties assocsFromDefinition = new Properties();
            try {
                if (typeDef != null) {
                    Map<QName,AssociationDefinition> associations = new HashMap<>();
                    associations.putAll(typeDef.getAssociations());

                    List<AspectDefinition> defaultAspects = typeDef.getDefaultAspects(true);
                    for (AspectDefinition defaultAspect : defaultAspects) {
                        associations.putAll(defaultAspect.getAssociations());
                    }

                    boolean stop;
                    String assocValue;

                    for (QName assocName : associations.keySet()) {
                        String shortName = this.replaceNameSpaces(assocName.toString());
                        if (definition.getProperty(shortName) == null) {
                            for (String startValue : startValues) {
                                stop = shortName.startsWith(startValue.trim());
                                if (stop) {
                                    break;
                                }
                            }
                            if (!this.getBlacklist().toLowerCase().contains("," + shortName.toLowerCase() + ",") && !shortName.equals("-")) {
                                assocValue = this.getClassToColumnType().getProperty(Constants.COLUMN_NODEREFS, "-");
                                if (this.getReplacementDataType().containsKey(shortName)) {
                                    assocValue = this.getReplacementDataType().getProperty(shortName, "-").trim();
                                }

                                if (logger.isDebugEnabled()) {
                                    logger.debug("Target: Setting " + shortName + "=" + assocValue);
                                }

                                assocsFromDefinition.setProperty(shortName, assocValue);
                                definition.setProperty(shortName, assocValue);
                            }
                        }
                    }
                }
            } catch (Exception var20) {
                logger.warn("processAssociationDefinitions: source-target ERROR!");
                var20.printStackTrace();
            }
            typeAssocsDefinitions.put(objectType, assocsFromDefinition);
        }

        if (typeDef != null) {
            try {
                List<AssociationRef> sourceAssocs = this.getNodeService().getSourceAssocs(nodeRef, RegexQNamePattern.MATCH_ALL);
                processAssocs(definition, startValues, sourceAssocs);
            } catch (Exception var16) {
                logger.error("processAssociationDefinitions: Source_Association ERROR!");
            }
        }

        return definition;
    }

   private void processAssocs(Properties definition, String[] startValues, List<AssociationRef> assocsList) {
        boolean stop;
        String assocValue;
        for (AssociationRef targetAssoc : assocsList) {
            String shortName = this.replaceNameSpaces(targetAssoc.getTypeQName().toString());
            if (definition.getProperty(shortName) == null) {
                for (String startValue : startValues) {
                    stop = shortName.startsWith(startValue.trim());
                    if (stop) {
                        break;
                    }
                }
                if (!this.getBlacklist().toLowerCase().contains("," + shortName.toLowerCase() + ",") && !shortName.equals("-")) {
                    assocValue = this.getClassToColumnType().getProperty(Constants.COLUMN_NODEREFS, "-");
                    if (this.getReplacementDataType().containsKey(shortName)) {
                        assocValue = this.getReplacementDataType().getProperty(shortName, "-").trim();
                    }

                    if (logger.isDebugEnabled()) {
                        logger.debug("Target: Setting " + shortName + "=" + assocValue);
                    }

                    definition.setProperty(shortName, assocValue);
                }
            }
        }
    }

    private ReportLine processAssociationValues(ReportLine rl, NodeRef nodeRef) throws Exception {
        try {
            List childAssocs = this.getNodeService().getChildAssocs(nodeRef);
            long min = (long) Math.min(childAssocs.size(),
                    Integer.parseInt(this.getGlobalProperties().getProperty("reporting.harvest.treshold.child.assocs", "20")));
            long shortName = childAssocs.size();
            if (shortName > 0L && shortName <= min) {
                String maxChildCount = "";

                ChildAssociationRef numberOfChildCars;
                for (Iterator iterator = childAssocs.iterator(); iterator.hasNext(); maxChildCount = maxChildCount + numberOfChildCars.getChildRef()) {
                    numberOfChildCars = (ChildAssociationRef) iterator.next();
                    if (maxChildCount.length() > 0) {
                        maxChildCount = maxChildCount + ",";
                    }
                }

                rl.setLine(Constants.COLUMN_CHILD_NODEREF, this.getClassToColumnType().getProperty(Constants.COLUMN_NODEREFS, "-"), maxChildCount, this.getReplacementDataType());
            }
        } catch (Exception var19) {
            logger.warn("Error in processing processAssociationValues");
            var19.printStackTrace();
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

        QName objectType = getNodeService().getType(nodeRef);
        TypeDefinition typeDef = this.getDictionaryService().getType(objectType);

        if (typeDef != null) {
            Map<QName, AssociationDefinition> associations = new HashMap<>();
            associations.putAll(typeDef.getAssociations());

            List<AspectDefinition> defaultAspects = typeDef.getDefaultAspects(true);
            for (AspectDefinition defaultAspect : defaultAspects) {
                associations.putAll(defaultAspect.getAssociations());
            }

            int maxAssocsCount = Integer.parseInt(this.getGlobalProperties().getProperty("reporting.harvest.treshold.sourcetarget.assocs", "20"));
            List<AssociationRef> assocValues;
            String key;
            String value;
            Iterator it;
            AssociationRef ar;
            long maxChildCount;
            long numberOfChildCars;

            for (QName assocQName : associations.keySet()) {
                if (!associations.get(assocQName).isChild()) {
                    String assocShortName = this.replaceNameSpaces(assocQName.toString());
                    if (!assocShortName.startsWith("trx")
                            && !assocShortName.startsWith("act")
                            && !assocShortName.startsWith("wca")) {
                        try {
                            assocValues = this.getNodeService().getTargetAssocs(nodeRef, assocQName);
                            maxChildCount = Math.min(assocValues.size(), maxAssocsCount);
                            numberOfChildCars = assocValues.size();
                            if (numberOfChildCars <= maxChildCount) {
                                key = this.replaceNameSpaces(assocQName.toString());
                                if (!this.getBlacklist().toLowerCase().contains("," + key.toLowerCase() + ",") && !key.equals("-")) {
                                    value = "";

                                    for (it = assocValues.iterator(); it.hasNext(); value = value + ar.getTargetRef().toString()) {
                                        ar = (AssociationRef) it.next();
                                        if (value.length() > 0) {
                                            value = value + ",";
                                        }
                                    }

                                    rl.setLine(key, this.getClassToColumnType().getProperty(Constants.COLUMN_NODEREFS, "-"), value, this.getReplacementDataType());
                                }
                            }
                        } catch (Exception ex) {
                            //if (logger.isDebugEnabled()) {
                            logger.error(ex.getMessage(), ex);
                            //}
                        }
                    }
                }
            }

            try {
                assocValues = this.getNodeService().getSourceAssocs(nodeRef, RegexQNamePattern.MATCH_ALL);
                Map<String, String> sourceValues = new HashMap<>();
                for (AssociationRef assocValue : assocValues) {
                    key = assocValue.getTypeQName().toString();
                    key = this.replaceNameSpaces(key);
                    if (!this.getBlacklist().toLowerCase().contains("," + key.toLowerCase() + ",") && !key.equals("-")) {
                        value = sourceValues.get(key);
                        if (value == null) {
                            value = "";
                        }
                        NodeRef sourceValue = assocValue.getSourceRef();
                        if (value.length() > 0) {
                            value = value + ",";
                        }
                       sourceValues.put(key, value + sourceValue.toString());
                    }
                }
                for (String sourceKey : sourceValues.keySet()) {
                    rl.setLine(sourceKey, this.getClassToColumnType().getProperty(Constants.COLUMN_NODEREFS, "-"),
                            sourceValues.get(sourceKey), this.getReplacementDataType());
                }
            } catch (Exception ignored) {
                if (logger.isDebugEnabled()) {
                    logger.error(ignored.getMessage(), ignored);
                }
            }
        }

        return rl;
    }

    protected ReportLine processNodeToMap(String identifier, String table, ReportLine rl) {
        this.dbhb.fixTableColumnName(table);

        if (logger.isDebugEnabled()) {
            logger.debug("Enter processNodeToMap nodeRef=" + identifier);
        }

        NodeRef nodeRef = new NodeRef(identifier);

        try {
            rl = this.processPropertyValues(rl, nodeRef);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        try {
            rl = this.processAssociationValues(rl, nodeRef);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        try {
            rl.setLine(Constants.COLUMN_NODEREF, this.getClassToColumnType().getProperty(Constants.COLUMN_NODEREF), nodeRef.toString(), this.getReplacementDataType());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        String valueString;
        try {
            valueString = this.nodeService.getType(nodeRef).toPrefixString(namespaceService);
            rl.setLine(Constants.COLUMN_OBJECT_TYPE, this.getClassToColumnType().getProperty(Constants.COLUMN_OBJECT_TYPE, ""), valueString, this.getReplacementDataType());
        } catch (Exception ex) {
            logger.debug("EXCEPTION: // it does not have a Type. Bad luck. Don\'t crash (versionStore?!)");
        }

        valueString = "";

        try {
            Set<QName> path = this.nodeService.getAspects(nodeRef);

            QName site;
            for (Iterator displayPath = path.iterator(); displayPath.hasNext(); valueString = valueString + site.getLocalName()) {
                site = (QName) displayPath.next();
                if (valueString.length() > 0) {
                    valueString = valueString + ",";
                }
            }

            rl.setLine(Constants.COLUMN_ASPECTS, this.getClassToColumnType().getProperty(Constants.COLUMN_ASPECTS, ""), valueString, this.getReplacementDataType());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        String displayPath1;

        try {
            Path path1 = this.getNodeService().getPath(nodeRef);
            displayPath1 = this.toDisplayPath(path1);
            rl.setLine(Constants.COLUMN_PATH, this.getClassToColumnType().getProperty(Constants.COLUMN_PATH), displayPath1, this.getReplacementDataType());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
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
            } catch (Exception ex) {
                logger.fatal("Exception getting orig_noderef" + ex.getMessage());
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
                logger.warn("Exception getting orig_noderef" + Arrays.toString(var18.getStackTrace()));
            }

            try {
                if (nodeRef.toString().startsWith("version")) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Setting nodeRef to orig_noderef - VERSION!!!");
                        //logger.debug("Master says: " + (masterRef != null ? masterRef.toString() : null));
                    }

                    //rl.setLine(Constants.COLUMN_ORIG_NODEREF, this.getClassToColumnType().getProperty(Constants.COLUMN_NODEREF), masterRef != null ? masterRef.toString() : null, this.getReplacementDataType());
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

        return rl;
    }

    public Properties processQueueDefinition(String table) {
        Properties definition = new Properties();
        for (Object aQueue : this.queue) {
            NodeRef nodeRef = new NodeRef(aQueue.toString().split(",")[0]);
            QName objectType = this.getNodeService().getType(nodeRef);
            if (typePropsDefinitions.containsKey(objectType)) {
                definition = typePropsDefinitions.get(objectType);
            } else {
                try {
                    definition = this.processPropertyDefinitions(definition, objectType);
                    definition.setProperty(Constants.COLUMN_ORIG_NODEREF, this.getClassToColumnType().getProperty(Constants.COLUMN_NODEREF, "-"));
                } catch (Exception var9) {
                    logger.warn("processQueueDefinition: ERROR: versionNodes.containsKey or before " + var9.getMessage());
                    var9.printStackTrace();
                }

                try {
                    definition.setProperty(Constants.COLUMN_ORIG_NODEREF, this.getClassToColumnType().getProperty(Constants.COLUMN_NODEREF, "-"));
                    definition.setProperty(Constants.COLUMN_PATH, this.getClassToColumnType().getProperty(Constants.COLUMN_PATH, "-"));
                    definition.setProperty(Constants.COLUMN_NODEREF, this.getClassToColumnType().getProperty(Constants.COLUMN_NODEREF, "-"));
                    definition.setProperty(Constants.COLUMN_OBJECT_TYPE, this.getClassToColumnType().getProperty(Constants.COLUMN_OBJECT_TYPE, "-"));
                    definition.setProperty(Constants.COLUMN_ASPECTS, this.getClassToColumnType().getProperty(Constants.COLUMN_ASPECTS, "-"));

                    if (logger.isDebugEnabled()) {
                        logger.debug("processQueueDefinition: qname=" + objectType);
                    }

                    if (!this.getDictionaryService().isSubClass(objectType, ContentModel.TYPE_CONTENT)
                            && !this.getDictionaryService().getType(objectType).toString().equalsIgnoreCase(ContentModel.TYPE_CONTENT.toString())) {
                        logger.debug("processQueueDefinition: NOOOOO! We are NOT a subtype of Content!");
                    } else {
                        if (logger.isDebugEnabled()) {
                            logger.debug("processQueueDefinition: YEAH! We are a subtype of Content! " + ContentModel.TYPE_CONTENT);
                        }

                        definition.setProperty("mimetype", this.getClassToColumnType().getProperty("mimetype", "-"));
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

                    if (this.getDictionaryService().isSubClass(objectType, ContentModel.TYPE_PERSON)) {
                        definition.setProperty("enabled", this.getClassToColumnType().getProperty("boolean", "-"));
                    }
                } catch (Exception var10) {
                    logger.info("unexpeted error in node " + nodeRef.toString());
                    logger.info("unexpeted error in node " + var10.getMessage());
                }

                typePropsDefinitions.put(objectType, definition);
            }

            try {
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
            } catch (Exception ex) {
                logger.info("unexpeted error in node " + nodeRef.toString());
                logger.info("unexpeted error in node " + ex.getMessage());
            }

            try {
                definition = this.processAssociationDefinitions(definition, nodeRef, objectType);
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

        ReportLine rl = new ReportLine(table, this.reportingHelper);
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

                rl = this.processNodeToMap(nodeRef.toString(), table, rl);

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
                    logger.error("processQueueValues Exception2: " + Arrays.toString(var19.getStackTrace()));
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

    public void havestNodes(NodeRef harvestDefinition) {
        try {
            List<StoreRef> stores = this.getStoreRefList();

            Properties queries = this.getReportingHelper().getTableQueries();
            Enumeration keys = queries.keys();

            long shiftFromDB = getTimestampFromDBShift() * 60000;

            this.dbhb.createEmptyTypeTablesTable();

            Date lastSolrIndexDate = null;
            long solrTimestamp = this.reportingHelper.getSolrLastTimestamp();
            if (solrTimestamp > 0) {
                long solrShift = getSolrTimestampShift() * 60000;
                lastSolrIndexDate = new Date(solrTimestamp - solrShift);
            }

            List<Message> messagesAboutDelete = getMessagesAboutDelete();//получим до старта синхронизации

            while (keys.hasMoreElements()) {
                String tableName = (String) keys.nextElement();
                String query = (String) queries.get(tableName);
                tableName = this.dbhb.fixTableColumnName(tableName);
                Iterator storeIterator = stores.iterator();
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
                            String fullQuery = query + this.queryClauseTimestamp(timestampStart, storeRef.getProtocol())
                                    + this.queryClauseOrderBy(startDbId);
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
                                        logger.info("   " + Arrays.toString(var35.getStackTrace()));
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
                                    logger.error("Message has no type!!! Message:" + messageText + "\n");
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
        }
    }

    private Integer getTimestampFromDBShift() {
        String shift = this.reportingHelper.getGlobalProperties().getProperty("reporting.harvest.timestamp.fromDB.shift", "30");
        return Integer.parseInt(shift);
    }

    private Integer getSolrTimestampShift() {
        String shift = this.reportingHelper.getGlobalProperties().getProperty("reporting.harvest.timestamp.solr.shift", "10");
        return Integer.parseInt(shift);
    }

    private List<StoreRef> getStoreRefList() {
        String[] stores = this.reportingHelper.getGlobalProperties().getProperty("reporting.harvest.stores", "").split(",");
        List<StoreRef> storeRefArray = new ArrayList<>();

        for (String store : stores) {
            logger.debug("Adding store: " + store);
            StoreRef s = new StoreRef(store);
            storeRefArray.add(s);
        }

        return storeRefArray;
    }

    private String queryClauseTimestamp(String timestamp1, String protocol) {
        String dateQuery = " ";
        String myTimestamp1 = timestamp1.replaceAll(" ", "T");
        if (protocol.equalsIgnoreCase("workspace")) {
            dateQuery = dateQuery + "AND @cm\\:modified:['" + myTimestamp1 + "' TO NOW]";
        } else if (protocol.equalsIgnoreCase("archive")) {
            dateQuery = dateQuery + "AND @sys\\:archivedDate:['" + myTimestamp1 + "' TO NOW]";
        }

        return dateQuery;
    }

    private String queryClauseOrderBy(long dbid) {
        String orderBy = "";
        if (dbid > 0L) {
            orderBy = "AND @sys\\:node\\-dbid:[" + (dbid + 1L) + " TO MAX]";
        }

        return orderBy;
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
}
