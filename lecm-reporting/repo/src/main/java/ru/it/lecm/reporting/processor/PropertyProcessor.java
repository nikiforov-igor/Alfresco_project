package ru.it.lecm.reporting.processor;

import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ru.it.lecm.reporting.ReportLine;
import ru.it.lecm.reporting.ReportingHelper;
import ru.it.lecm.reporting.db.DatabaseHelperBean;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

abstract class PropertyProcessor {

    protected ServiceRegistry serviceRegistry;
    private SearchService searchService;
    private AuthorityService authorityService;
    private AuthenticationService authenticationService;
    protected VersionService versionService;
    protected NodeService nodeService;
    protected NamespaceService namespaceService;
    protected DictionaryService dictionaryService;
    protected FileFolderService fileFolderService;
    protected DatabaseHelperBean dbhb;
    protected ReportingHelper reportingHelper;
    protected String method = "SINGLE_INSTANCE";
    private static Log logger = LogFactory.getLog(PropertyProcessor.class);
    protected List<Object> queue = new ArrayList<>();
    Properties dataDictionary;
    Properties replacementDataTypes;
    Properties globalProperties;
    Properties namespaces;
    String blacklist;

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    public void resetQueue() {
        this.queue = new ArrayList<>();
    }

    public SearchService getSearchService() {
        return this.searchService;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public AuthorityService getAuthorityService() {
        return this.authorityService;
    }

    public void setAuthorityService(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }

    public AuthenticationService getAuthenticationService() {
        return this.authenticationService;
    }

    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public ServiceRegistry getServiceRegistry() {
        return this.serviceRegistry;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    public NodeService getNodeService() {
        return this.nodeService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public DictionaryService getDictionaryService() {
        return this.dictionaryService;
    }

    public void setDictionaryService(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    public FileFolderService getFileFolderService() {
        return this.fileFolderService;
    }

    public void setFileFolderService(FileFolderService fileFolderService) {
        this.fileFolderService = fileFolderService;
    }

    public DatabaseHelperBean getDbhb() {
        return this.dbhb;
    }

    public void setDbhb(DatabaseHelperBean dbhb) {
        this.dbhb = dbhb;
    }

    public ReportingHelper getReportingHelper() {
        return this.reportingHelper;
    }

    public void setReportingHelper(ReportingHelper reportingHelper) {
        this.reportingHelper = reportingHelper;
    }

    public Properties getGlobalProperties() {
        return this.globalProperties;
    }

    public void setGlobalProperties(Properties globalProperties) {
        this.globalProperties = globalProperties;
    }

    public Properties getNamespaces() {
        return this.namespaces;
    }

    public void setNamespaces(Properties namespaces) {
        this.namespaces = namespaces;
    }

    public void setBlacklist(String blacklist) {
        this.blacklist = blacklist;
    }

    public List getQueue() {
        return this.queue;
    }

    public Properties getClassToColumnType() {
        return this.dataDictionary;
    }

    void setClassToColumnType(Properties dataDictionary) {
        this.dataDictionary = dataDictionary;
    }

    public Properties getReplacementDataType() {
        return this.replacementDataTypes;
    }

    public String getBlacklist() {
        return this.blacklist;
    }

    public void setReplacementDataTypes(Properties p) {
        this.replacementDataTypes = p;
    }

    public void addToQueue(Object queueItem) {
        this.queue.add(queueItem);
    }

    public SimpleDateFormat getSimpleDateFormat() {
        return this.reportingHelper.getSimpleDateFormat();
    }

    public String replaceNameSpaces(String namespace) {
        Properties p = this.getNamespaces();
        String returnSpace = namespace;

        String into;
        String from;
        for (Enumeration keys = p.keys(); keys.hasMoreElements(); returnSpace = returnSpace.replace(from, into)) {
            into = (String) keys.nextElement();
            from = p.getProperty(into);
        }

        returnSpace = returnSpace.replace("-", "_");
        return returnSpace;
    }

    public static void propertyLogger(String description, Properties p) {
        if (logger.isDebugEnabled()) {
            logger.debug("PropertyLogger: " + description);
        }

        Enumeration keys = p.keys();

        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            String value = p.getProperty(key, "-");
            if (logger.isDebugEnabled()) {
                logger.debug("  entry: " + key + "=" + value);
            }
        }

    }

    public void setTableDefinition(String tableName, Properties props) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Enter setTableDefinition tableName=" + tableName + " with props=" + props);
        }

        try {
            Properties e = this.dbhb.getTableDescription(tableName);
            if (logger.isDebugEnabled()) {
                propertyLogger("## Object properties", props);
                propertyLogger("## Table description", e);
            }

            Enumeration keys = props.keys();

            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                String type = props.getProperty(key, "-");
                if (logger.isDebugEnabled()) {
                    logger.debug("## COMPARE: key=" + key);
                }

                if (!e.containsKey(key) && !e.containsKey(key.toUpperCase()) && !e.containsKey(key.toLowerCase())) {
                    if (!"-".equals(type) && !"".equals(type)) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("DEFINITION Adding column: " + key + "=" + type);
                        }

                        this.dbhb.extendTable(tableName, key, type);
                    } else if (logger.isDebugEnabled()) {
                        logger.debug("DEFINITION Column " + key + " is empty. Type=" + type);
                    }
                } else if (logger.isDebugEnabled()) {
                    logger.debug("DEFINITION Column " + key + " already exists.");
                }
            }
        } catch (Exception var7) {
            logger.fatal("Exception setTableDefinition: " + var7.getMessage());
            throw new Exception(var7);
        }

        logger.debug("Exit setTableDefinition");
    }

    public Properties processPropertyDefinitions(Properties definition, QName objectType) {
        if (logger.isDebugEnabled()) {
            logger.debug("enter processPropertyDefinitions #props=" + definition.size() + " and objectType=" + objectType);
        }

        try {
            TypeDefinition typeDef = this.getDictionaryService().getType(objectType);

            if (typeDef != null) {
                Map<QName, PropertyDefinition> properties = new HashMap<>();
                properties.putAll(typeDef.getProperties());

                List<AspectDefinition> defaultAspects = typeDef.getDefaultAspects(true);
                for (AspectDefinition defaultAspect : defaultAspects) {
                    properties.putAll(defaultAspect.getProperties());
                }

                if (logger.isDebugEnabled()) {
                    logger.debug("processPropertyDefinitions: Size of map=" + properties.size());
                }

                for (QName propName : properties.keySet()) {
                    String key = "";
                    String type = "";

                    try {
                        if (propName != null) {
                            key = this.replaceNameSpaces(propName.toString());
                            if (logger.isDebugEnabled()) {
                                logger.debug("processPropertyDefinitions: Processing key " + key);
                            }

                            if (!key.startsWith("{urn:schemas_microsoft_com:}") && !definition.containsKey(key)) {
                                type = "";
                                if (this.getReplacementDataType().containsKey(key)) {
                                    type = this.getReplacementDataType().getProperty(key, "-").trim();
                                } else {
                                    type = "-";

                                    try {
                                        type = properties.get(propName).getDataType().toString().trim();
                                        type = type.substring(type.indexOf("}") + 1, type.length());
                                        type = this.getClassToColumnType().getProperty(type, "-");
                                    } catch (NullPointerException var9) {
                                        logger.info("Silent drop of NullPointerException against " + key);
                                    }
                                }

                                if (type != null && !type.equals("-") && !type.equals("") && !key.equals("") && !this.getBlacklist().toLowerCase().contains("," + key.toLowerCase() + ",")) {
                                    definition.setProperty(key, type);
                                    if (logger.isDebugEnabled()) {
                                        logger.debug("processPropertyDefinitions: Adding column " + key + "=" + type);
                                    }
                                } else if (logger.isDebugEnabled()) {
                                    logger.debug("Ignoring column " + key + "=" + type);
                                }
                            }
                        }
                    } catch (Exception var10) {
                        logger.error("processPropertyDefinitions: Property not found! Property below...");
                        logger.error("processPropertyDefinitions: type=" + type + ", key=" + key);
                        var10.printStackTrace();
                    }

                    if (logger.isDebugEnabled()) {
                        logger.debug("processPropertyDefinitions: end while");
                    }
                }
            }
        } catch (Exception var11) {
            var11.printStackTrace();
            logger.error("processPropertyDefinitions: Finally an EXCEPTION " + var11.getMessage());
        }

        return definition;
    }

    @SuppressWarnings("unused")
    private String getCategoryDisplayPath(NodeRef category) {
        String returnString = (String) this.getNodeService().getProperty(category, ContentModel.PROP_NAME);
        NodeRef parent = this.getNodeService().getPrimaryParent(category).getParentRef();
        NodeRef parentsParent = this.getNodeService().getPrimaryParent(parent).getParentRef();

        for (int counter = 0; !ContentModel.TYPE_CATEGORYROOT.equals(this.getNodeService().getType(parentsParent)) && counter < 20; ++counter) {
            returnString = this.getNodeService().getProperty(parent, ContentModel.PROP_NAME) + "/" + returnString;
            parent = this.getNodeService().getPrimaryParent(parent).getParentRef();
            parentsParent = this.getNodeService().getPrimaryParent(parent).getParentRef();
        }

        return returnString;
    }

    public String getPropertyValue(Serializable value, String dtype, boolean multiValued) {
        String returnValue = "";

        if (!"category".equals(dtype)) {
            if (multiValued) {
                List propValue = (List) value;
                if (propValue != null && !propValue.isEmpty() && propValue.size() > 0) {
                    int cnt;
                    switch (dtype) {
                        case "date":
                        case "datetime": {
                            SimpleDateFormat formatter = this.getSimpleDateFormat();

                            for (Object aVar11 : propValue) {
                                returnValue = returnValue + formatter.format((Date) aVar11) + ",";
                            }
                            break;
                        }
                        case "id":
                        case "long": {
                            for (cnt = 0; cnt < propValue.size(); ++cnt) {
                                returnValue = returnValue + Long.toString((Long) propValue.get(cnt)) + ",";
                            }
                            break;
                        }
                        case "int": {
                            for (cnt = 0; cnt < propValue.size(); ++cnt) {
                                returnValue = returnValue + Integer.toString((Integer) propValue.get(cnt)) + ",";
                            }
                            break;
                        }
                        case "float":
                        case "double": {
                            for (cnt = 0; cnt < propValue.size(); ++cnt) {
                                returnValue = returnValue + Double.toString((Double) propValue.get(cnt)) + ",";
                            }
                            break;
                        }
                        case "boolean": {
                            for (cnt = 0; cnt < propValue.size(); ++cnt) {
                                returnValue = returnValue + Boolean.toString((Boolean) propValue.get(cnt)) + ",";
                            }
                            break;
                        }
                        case "text": {
                            for (cnt = 0; cnt < propValue.size(); ++cnt) {
                                returnValue = returnValue + propValue.get(cnt) + ",";
                            }
                            break;
                        }
                        case "noderef": {
                            for (cnt = 0; cnt < propValue.size(); ++cnt) {
                                returnValue = returnValue + propValue.get(cnt).toString() + ",";
                            }
                            break;
                        }
                    }

                    if (returnValue.equals("")) {
                        for (cnt = 0; cnt < propValue.size(); ++cnt) {
                            returnValue = returnValue + propValue.get(cnt) + ",";
                        }
                    }
                }
            } else if (value != null) {
                switch (dtype) {
                    case "date":
                    case "datetime": {
                        SimpleDateFormat categories = this.getSimpleDateFormat();
                        Calendar i$ = Calendar.getInstance();
                        i$.setTimeInMillis(((Date) value).getTime());
                        returnValue = categories.format((Date) value);
                        break;
                    }
                    case "id":
                    case "long": {
                        returnValue = Long.toString((Long) value);
                        break;
                    }
                    case "int": {
                        returnValue = Integer.toString((Integer) value);
                        break;
                    }
                    case "float":
                    case "double": {
                        returnValue = Double.toString((Double) value);
                        break;
                    }
                    case "boolean": {
                        returnValue = Boolean.toString((Boolean) value);
                        break;
                    }
                    case "text": {
                        returnValue = value.toString();
                        break;
                    }
                    case "noderef": {
                        returnValue = value.toString();
                        break;
                    }
                }

                if (returnValue.equals("")) {
                    returnValue = String.valueOf(value);
                }
            }
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("I am a category!");
            }

            List categories = (List) value;
            String catName;
            if (categories != null) {
                for (Iterator var16 = categories.iterator(); var16.hasNext(); returnValue = returnValue + catName) {
                    NodeRef var15 = (NodeRef) var16.next();
                    catName = this.nodeService.getProperty(var15, ContentModel.PROP_NAME).toString();
                    //catName = this.getCategoryDisplayPath(var15);
                    if (returnValue.length() > 0) {
                        returnValue = returnValue + ",";
                    }
                }
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Exit getPropertyValue, returning: " + returnValue);
        }

        return returnValue;
    }

    public ReportLine processPropertyValues(ReportLine rl, NodeRef nodeRef) {
        if (logger.isDebugEnabled()) {
            logger.debug("processPropertyValues enter " + nodeRef);
        }

        if (this.dictionaryService.isSubClass(this.nodeService.getType(nodeRef), ContentModel.TYPE_CONTENT)) {
            try {
                rl.setLine("cm_workingcopylink", this.getClassToColumnType().getProperty("noderef", ""), null, this.getReplacementDataType());
                rl.setLine("cm_lockOwner", this.getClassToColumnType().getProperty("noderef", ""), null, this.getReplacementDataType());
                rl.setLine("cm_lockType", this.getClassToColumnType().getProperty("noderef", ""), null, this.getReplacementDataType());
                rl.setLine("cm_expiryDate", this.getClassToColumnType().getProperty("datetime", ""), null, this.getReplacementDataType());
            } catch (Exception ex) {
                logger.error("processPropertyValues Exception " + ex.getMessage());
            }
        }

        QName objectType = getNodeService().getType(nodeRef);
        TypeDefinition typeDef = this.getDictionaryService().getType(objectType);

        if (typeDef != null) {
            Map<QName, Serializable> propsMap = this.nodeService.getProperties(nodeRef);

            Map<QName, PropertyDefinition> properties = new HashMap<>();
            properties.putAll(typeDef.getProperties());

            List<AspectDefinition> defaultAspects = typeDef.getDefaultAspects(true);
            for (AspectDefinition defaultAspect : defaultAspects) {
                properties.putAll(defaultAspect.getProperties());
            }

            for (QName propQName : properties.keySet()) {
                String key = "";
                String dtype = "";

                try {
                    PropertyDefinition propDefinition = properties.get(propQName);
                    key = propQName.toString();
                    if (!key.startsWith("{urn:schemas_microsoft_com:}")) {
                        key = this.replaceNameSpaces(key);
                        dtype = propDefinition.getDataType().toString();
                        dtype = dtype.substring(dtype.indexOf("}") + 1, dtype.length()).trim();
                        String type = this.getClassToColumnType().getProperty(dtype, "-");
                        boolean multiValued = propDefinition.isMultiValued();
                        if (!this.blacklist.toLowerCase().contains("," + key.toLowerCase() + ",") && !type.equals("-")) {
                            String value = this.getPropertyValue(propsMap.get(propQName), dtype, multiValued);
                            rl.setLine(key, type, value, this.getReplacementDataType());
                        }
                    }
                } catch (Exception var12) {
                    logger.debug("processPropertyValues: Error in object, property " + key + " not found! (" + dtype + ")");
                }
            }
        }

        return rl;
    }

    @SuppressWarnings("unused")
    protected abstract ReportLine processNodeToMap(String identifier, String table, ReportLine rl);

    @SuppressWarnings("unused")
    abstract Properties processQueueDefinition(String table);

    @SuppressWarnings("unused")
    abstract void processQueueValues(String table) throws Exception;

    @SuppressWarnings("unused")
    abstract void havestNodes(NodeRef node);
}
