package ru.it.lecm.reporting.processor;

import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.dictionary.DictionaryService;
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
import ru.it.lecm.reporting.mybatis.AssocDefinition;

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
    protected List queue = new ArrayList();
    Properties dataDictionary;
    Properties replacementDataTypes;
    Properties globalProperties;
    Map<String, String> namespaces;
    String blacklist;

    private ArrayList<AssocDefinition> assocs = new ArrayList<>();

    public void resetQueue() {
        this.queue = new ArrayList();
        this.assocs = new ArrayList<>();
    }

    public ArrayList<AssocDefinition> getAssocs() {
        return assocs;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
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

    public Map<String, String> getNamespaces() {
        return this.namespaces;
    }

    public void setNamespaces(Map<String, String> namespaces) {
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

    public String replaceNameSpaces(QName qName) {
        Map<String, String> p = this.getNamespaces();

        String from = qName.getNamespaceURI();

        return p.get(from) + qName.getLocalName().replace("-", "_");
    }

    public static void propertyLogger(String description, Properties p) {
        if(logger.isDebugEnabled()) {
            logger.debug("PropertyLogger: " + description);
        }

        Enumeration keys = p.keys();

        while(keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            String value = p.getProperty(key, "-");
            if(logger.isDebugEnabled()) {
                logger.debug("  entry: " + key + "=" + value);
            }
        }
    }

    public void setTableDefinition(String tableName, HashMap<String, String> props) throws Exception {
        if(logger.isDebugEnabled()) {
            logger.debug("Enter setTableDefinition tableName=" + tableName + " with props=" + props);
        }

        try {
            Properties e = this.dbhb.getTableDescription(tableName);

            for (String key : props.keySet()) {
                String type = props.get(key);
                if(logger.isDebugEnabled()) {
                    logger.debug("## COMPARE: key=" + key);
                }

                if(!e.containsKey(key) && !e.containsKey(key.toUpperCase()) && !e.containsKey(key.toLowerCase())) {
                    if(!"-".equals(type) && !"".equals(type)) {
                        if(logger.isDebugEnabled()) {
                            logger.debug("DEFINITION Adding column: " + key + "=" + type);
                        }

                        this.dbhb.extendTable(tableName, key, type);
                    } else if(logger.isDebugEnabled()) {
                        logger.debug("DEFINITION Column " + key + " is empty. Type=" + type);
                    }
                } else if(logger.isDebugEnabled()) {
                    logger.debug("DEFINITION Column " + key + " already exists.");
                }
            }
        } catch (Exception var7) {
            logger.fatal("Exception setTableDefinition: " + var7.getMessage());
            throw new Exception(var7);
        }

        logger.debug("Exit setTableDefinition");
    }

    public void setTableDefinition(String tableName, Properties props) throws Exception {
        if(logger.isDebugEnabled()) {
            logger.debug("Enter setTableDefinition tableName=" + tableName + " with props=" + props);
        }

        try {
            Properties e = this.dbhb.getTableDescription(tableName);
            if(logger.isDebugEnabled()) {
                propertyLogger("## Object properties", props);
                propertyLogger("## Table description", e);
            }

            Enumeration keys = props.keys();

            while(keys.hasMoreElements()) {
                String key = (String)keys.nextElement();
                String type = props.getProperty(key, "-");
                if(logger.isDebugEnabled()) {
                    logger.debug("## COMPARE: key=" + key);
                }

                if(!e.containsKey(key) && !e.containsKey(key.toUpperCase()) && !e.containsKey(key.toLowerCase())) {
                    if(!"-".equals(type) && !"".equals(type)) {
                        if(logger.isDebugEnabled()) {
                            logger.debug("DEFINITION Adding column: " + key + "=" + type);
                        }

                        this.dbhb.extendTable(tableName, key, type);
                    } else if(logger.isDebugEnabled()) {
                        logger.debug("DEFINITION Column " + key + " is empty. Type=" + type);
                    }
                } else if(logger.isDebugEnabled()) {
                    logger.debug("DEFINITION Column " + key + " already exists.");
                }
            }
        } catch (Exception var7) {
            logger.fatal("Exception setTableDefinition: " + var7.getMessage());
            throw new Exception(var7);
        }

        logger.debug("Exit setTableDefinition");
    }

    public Properties processPropertyDefinitions(Properties definition, NodeRef nodeRef) {
        if(logger.isDebugEnabled()) {
            logger.debug("enter processPropertyDefinitions #props=" + definition.size() + " and nodeRef " + nodeRef);
        }

        try {
            Map e = this.nodeService.getProperties(nodeRef);
            if(logger.isDebugEnabled()) {
                logger.debug("processPropertyDefinitions: Size of map=" + e.size());
            }

            for (Object o : e.keySet()) {
                String key = "";
                String type = "";

                try {
                    QName e1 = (QName) o;
                    if (e1 != null) {
                        key = this.replaceNameSpaces(e1);
                        if (logger.isTraceEnabled()) {
                            logger.trace("processPropertyDefinitions: Processing key " + key);
                        }

                        if (!key.startsWith("{urn:schemas_microsoft_com:}") && !definition.containsKey(key)) {
                            type = "";
                            if (this.getReplacementDataType().containsKey(key)) {
                                type = this.getReplacementDataType().getProperty(key, "-").trim();
                            } else {
                                type = "-";

                                try {
                                    type = this.dictionaryService.getProperty(e1).getDataType().toString().trim();
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

                if (logger.isTraceEnabled()) {
                    logger.trace("processPropertyDefinitions: end while");
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
        String returnString = (String)this.getNodeService().getProperty(category, ContentModel.PROP_NAME);
        NodeRef parent = this.getNodeService().getPrimaryParent(category).getParentRef();
        NodeRef parentsParent = this.getNodeService().getPrimaryParent(parent).getParentRef();

        for(int counter = 0; !ContentModel.TYPE_CATEGORYROOT.equals(this.getNodeService().getType(parentsParent)) && counter < 20; ++counter) {
            returnString = this.getNodeService().getProperty(parent, ContentModel.PROP_NAME) + "/" + returnString;
            parent = this.getNodeService().getPrimaryParent(parent).getParentRef();
            parentsParent = this.getNodeService().getPrimaryParent(parent).getParentRef();
        }

        return returnString;
    }

    public String getPropertyValue(NodeRef nodeRef, QName qname, String dtype, boolean multiValued, Serializable sValue) {
        if(logger.isDebugEnabled()) {
            logger.debug("Enter getPropertyValue (4 params), qname=" + qname + ", noderef=" + nodeRef + ", dtype=" + dtype);
        }

        StringBuilder returnValue = new StringBuilder();
        if(logger.isDebugEnabled()) {
            logger.debug("getPropertyType Serialized=" + sValue);
        }

        if(multiValued && !"category".equals(dtype)) {
            new ArrayList();
            ArrayList var11 = (ArrayList) sValue;
            if(var11 != null && !var11.isEmpty() && var11.size() > 0) {
                if(dtype.equals("date") || dtype.equals("datetime")) {
                    SimpleDateFormat var13 = this.getSimpleDateFormat();

                    for (Object aVar11 : var11) {
                        returnValue.append(var13.format((Date) aVar11)).append(",");
                    }
                }

                int var14;
                switch (dtype) {
                    case "id":
                    case "long":
                        for (var14 = 0; var14 < var11.size(); ++var14) {
                            returnValue.append(Long.toString(((Long) var11.get(var14)).longValue())).append(",");
                        }
                        break;
                    case "int":
                        for (var14 = 0; var14 < var11.size(); ++var14) {
                            returnValue.append(Integer.toString(((Integer) var11.get(var14)).intValue())).append(",");
                        }
                        break;
                    case "float":
                    case "double":
                        for (var14 = 0; var14 < var11.size(); ++var14) {
                            returnValue.append(Double.toString(((Double) var11.get(var14)).doubleValue())).append(",");
                        }
                        break;
                    case "boolean":
                        for (var14 = 0; var14 < var11.size(); ++var14) {
                            returnValue.append(Boolean.toString(((Boolean) var11.get(var14)).booleanValue())).append(",");
                        }
                        break;
                    case "text":
                        for (var14 = 0; var14 < var11.size(); ++var14) {
                            returnValue.append(var11.get(var14)).append(",");
                        }
                        break;
                    case "noderef":
                        for (var14 = 0; var14 < var11.size(); ++var14) {
                            returnValue.append(var11.get(var14).toString()).append(",");
                        }
                        break;
                }

                if(returnValue.length() == 0) {
                    for(var14 = 0; var14 < var11.size(); ++var14) {
                        returnValue.append(var11.get(var14)).append(",");
                    }
                }
            }
        } else if(sValue != null && !"category".equals(dtype)) {
            if(dtype.equals("date") || dtype.equals("datetime")) {
                SimpleDateFormat categories = this.getSimpleDateFormat();
                Calendar i$ = Calendar.getInstance();
                i$.setTimeInMillis(((Date) sValue).getTime());
                returnValue = new StringBuilder(categories.format((Date) sValue));
            }

            switch (dtype) {
                case "id":
                case "long":
                    returnValue = new StringBuilder(Long.toString(((Long) sValue).longValue()));
                    break;
                case "int":
                    returnValue = new StringBuilder(Integer.toString(((Integer) sValue).intValue()));
                    break;
                case "float":
                case "double":
                    returnValue = new StringBuilder(Double.toString(((Double) sValue).doubleValue()));
                    break;
                case "boolean":
                    returnValue = new StringBuilder(Boolean.toString(((Boolean) sValue).booleanValue()));
                    break;
                case "text":
                    returnValue = new StringBuilder(sValue.toString());
                    break;
                case "noderef":
                    returnValue = new StringBuilder(sValue.toString());
                    break;
            }

            if(returnValue.length() == 0) {
                returnValue = new StringBuilder(String.valueOf(sValue));
            }
        }

        if(dtype.equals("category")) {
            if(logger.isDebugEnabled()) {
                logger.debug("I am a category!");
            }

            List var12 = (ArrayList) sValue;
            String catName;
            if(var12 != null) {
                for(Iterator var16 = var12.iterator(); var16.hasNext(); returnValue.append(catName)) {
                    NodeRef var15 = (NodeRef)var16.next();
                    catName = this.getCategoryDisplayPath(var15);
                    if(returnValue.length() > 0) {
                        returnValue.append(",");
                    }
                }
            }
        }

        if(logger.isDebugEnabled()) {
            logger.debug("Exit getPropertyValue, returning: " + returnValue.toString());
        }

        return returnValue.toString();
    }

    public ReportLine processPropertyValues(ReportLine rl, NodeRef nodeRef, boolean includeMeta) {
        Map<QName, Serializable> propertiesMap = this.nodeService.getProperties(nodeRef);
        if (logger.isDebugEnabled()) {
            logger.debug("processPropertyValues enter " + nodeRef);
        }

        if (includeMeta && this.dictionaryService.isSubClass(this.nodeService.getType(nodeRef), ContentModel.TYPE_CONTENT)) {
            try {
                rl.setLine("cm_workingcopylink", this.getClassToColumnType().getProperty("noderef", ""), null, this.getReplacementDataType());
                rl.setLine("cm_lockOwner", this.getClassToColumnType().getProperty("noderef", ""), null, this.getReplacementDataType());
                rl.setLine("cm_lockType", this.getClassToColumnType().getProperty("noderef", ""), null, this.getReplacementDataType());
                rl.setLine("cm_expiryDate", this.getClassToColumnType().getProperty("datetime", ""), null, this.getReplacementDataType());
            } catch (Exception ex) {
                logger.error("processPropertyValues Exception " + ex.getMessage());
            }
        }

        for (QName propQName : propertiesMap.keySet()) {
            String key = "";
            String dtype = "";

            try {
                key = propQName.toString();
                if (!key.startsWith("{urn:schemas_microsoft_com:}")) {
                    key = this.replaceNameSpaces(propQName);
                    if (key.length() > 60) {
                        key = key.substring(0, 60);
                    }
                    dtype = this.dictionaryService.getProperty(propQName).getDataType().toString();
                    dtype = dtype.substring(dtype.indexOf("}") + 1, dtype.length()).trim();
                    String type = this.getClassToColumnType().getProperty(dtype, "-");
                    boolean multiValued = this.dictionaryService.getProperty(propQName).isMultiValued();
                    if (!this.blacklist.toLowerCase().contains("," + key.toLowerCase() + ",") && !type.equals("-")) {
                        Serializable sVal = propertiesMap.get(propQName);
                        String value = this.getPropertyValue(nodeRef, propQName, dtype, multiValued, sVal);
                        rl.setLine(key, type, value, this.getReplacementDataType());
                    }
                }
            } catch (Exception var12) {
                logger.debug("processPropertyValues: Error in object, property " + key + " not found! (" + dtype + ")");
            }
        }

        return rl;
    }

    @SuppressWarnings("unused")
    public ReportLine processPropertyValues(ReportLine rl, NodeRef nodeRef) {
        return processPropertyValues(rl, nodeRef, true);
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
