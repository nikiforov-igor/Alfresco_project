package ru.it.lecm.reporting.processor;

import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ru.it.lecm.reporting.ReportLine;
import ru.it.lecm.reporting.ReportingHelper;
import ru.it.lecm.reporting.ReportingModel;
import ru.it.lecm.reporting.Utils;
import ru.it.lecm.reporting.db.DatabaseHelperBean;

import java.io.Serializable;
import java.util.*;

public class PersonProcessor extends PropertyProcessor {

    private static Log logger = LogFactory.getLog(PersonProcessor.class);

    public PersonProcessor(ServiceRegistry serviceRegistry, ReportingHelper reportingHelper, DatabaseHelperBean dbhb) throws Exception {
        this.setSearchService(serviceRegistry.getSearchService());
        this.setAuthorityService(serviceRegistry.getAuthorityService());
        this.setAuthenticationService(serviceRegistry.getAuthenticationService());
        this.setNodeService(serviceRegistry.getNodeService());
        this.setDictionaryService(serviceRegistry.getDictionaryService());
        this.setFileFolderService(serviceRegistry.getFileFolderService());
        this.setReportingHelper(reportingHelper);
        this.setDbhb(dbhb);
        this.setClassToColumnType(reportingHelper.getClassToColumnType());
        this.setReplacementDataTypes(reportingHelper.getReplacementDataType());
        this.setGlobalProperties(reportingHelper.getGlobalProperties());
        this.setNamespaces(reportingHelper.getNameSpaces());
        this.setBlacklist(reportingHelper.getBlacklist());
    }

    protected ReportLine processNodeToMap(String identifier, String table, ReportLine rl) {
        this.dbhb.fixTableColumnName(table);

        try {
            NodeRef e = new NodeRef(identifier);

            try {
                rl = this.processPropertyValues(rl, e, ",cm_homeFolder,cm_homeFolderProvider" + this.getBlacklist());
            } catch (Exception var12) {
                var12.printStackTrace();
            }

            try {
                rl.setLine("noderef", this.getClassToColumnType().getProperty("noderef"), identifier, this.getReplacementDataType());
            } catch (Exception var11) {
                logger.error("processPerson: That is weird, rl.setLine(noderef) crashed! " + e);
                var11.printStackTrace();
            }

            String account_expires;
            String account_expirydate;
            String account_locked;
            String enabled;
            String username = (String)this.getNodeService().getProperty(e, ContentModel.PROP_USERNAME);
            account_expires = (String)this.getNodeService().getProperty(e, ContentModel.PROP_ACCOUNT_EXPIRES);
            account_expirydate = (String)this.getNodeService().getProperty(e, ContentModel.PROP_ACCOUNT_EXPIRY_DATE);
            account_locked = (String)this.getNodeService().getProperty(e, ContentModel.PROP_ACCOUNT_LOCKED);
            Set zones = this.getAuthorityService().getAuthorityZones(username);
            if(this.getAuthenticationService().getAuthenticationEnabled(username)) {
                enabled = "true";
            } else {
                enabled = "false";
            }

            rl.setLine("account_enabled", this.getClassToColumnType().getProperty("boolean"), enabled, this.getReplacementDataType());
            rl.setLine("account_expires", this.getClassToColumnType().getProperty("boolean"), account_expires, this.getReplacementDataType());
            rl.setLine("account_expirydate", this.getClassToColumnType().getProperty("datetime"), account_expirydate, this.getReplacementDataType());
            rl.setLine("account_locked", this.getClassToColumnType().getProperty("boolean"), account_locked, this.getReplacementDataType());
            rl.setLine("zones", this.getClassToColumnType().getProperty("zones"), Utils.setToString(zones), this.getReplacementDataType());
            rl.setLine("validFrom", this.getClassToColumnType().getProperty("datetime"), this.getSimpleDateFormat().format(new Date()), this.getReplacementDataType());
        } catch (Exception var13) {
            logger.fatal("processPerson: That is weird, rl.setLine(noderef) crashed! " + identifier);
            var13.printStackTrace();
        }

        return rl;
    }

    Properties processQueueDefinition(String table) {
        this.dbhb.fixTableColumnName(table);
        return null;
    }

    void processQueueValues(String table) throws Exception {
        this.dbhb.fixTableColumnName(table);
    }

    void havestNodes(NodeRef harvestDefinition) {}

    public void processPersons(String tableName) throws Exception {
        logger.debug("Enter processPerson");

        try {
            tableName = this.dbhb.fixTableColumnName(tableName);
            this.dbhb.createEmptyTables(tableName);
            ReportLine e = new ReportLine(tableName, this.reportingHelper);
            Properties definition = new Properties();
            long highestDbId = 0L;
            boolean continueSearchCycle = true;
            ResultSet rs = null;

            while(continueSearchCycle) {
                try {
                    if(logger.isDebugEnabled()) {
                        logger.debug("processPerson: classToColumnType=" + this.getClassToColumnType());
                    }

                    SearchParameters sp = new SearchParameters();
                    String fullQuery = "TYPE:\"cm:person\" AND @sys\\:node\\-dbid:[" + highestDbId + " TO MAX]";
                    if(logger.isDebugEnabled()) {
                        logger.debug("processPerson: query=" + fullQuery);
                    }

                    sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
                    sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
                    sp.addSort("@{http://www.alfresco.org/model/system/1.0}node-dbid", true);
                    sp.setQuery(fullQuery);
                    if(logger.isDebugEnabled()) {
                        logger.debug("processPerson: Before searchService");
                    }

                    rs = this.getSearchService().query(sp);
                    if(logger.isDebugEnabled()) {
                        logger.debug("processPerson: Found results=" + rs.length());
                    }

                    if(rs.length() == 0) {
                        continueSearchCycle = false;
                        if(logger.isDebugEnabled()) {
                            logger.debug("processPerson: Break fired!");
                        }
                        break;
                    }

                    Iterator rsi = rs.iterator();

                    ResultSetRow rsr;
                    while(rsi.hasNext()) {
                        rsr = (ResultSetRow)rsi.next();
                        definition = this.processPropertyDefinitions(definition, rsr.getNodeRef(), ",cm_homeFolder,cm_homeFolderProvider" + this.getBlacklist());
                        definition.setProperty("noderef", this.getClassToColumnType().getProperty("noderef", "-"));
                        definition.setProperty("account_enabled", this.getClassToColumnType().getProperty("boolean", "-"));
                        definition.setProperty("account_expires", this.getClassToColumnType().getProperty("boolean", "-"));
                        definition.setProperty("account_expirydate", this.getClassToColumnType().getProperty("datetime", "-"));
                        definition.setProperty("account_locked", this.getClassToColumnType().getProperty("boolean", "-"));
                        definition.setProperty("zones", this.getClassToColumnType().getProperty("zones", "-"));
                        if(logger.isDebugEnabled()) {
                            logger.debug("Processing person with dbid=" + this.getNodeService().getProperty(rsr.getNodeRef(), ReportingModel.PROP_SYSTEM_NODE_DBID));
                        }

                        highestDbId = (Long) this.getNodeService().getProperty(rsr.getNodeRef(), ReportingModel.PROP_SYSTEM_NODE_DBID) + 1L;
                        if(logger.isDebugEnabled()) {
                            logger.debug("## Table def = " + definition);
                        }
                    }

                    if(logger.isDebugEnabled()) {
                        logger.debug("processPerson: Before setTableDefinition size=" + definition.size());
                    }

                    this.setTableDefinition(tableName, definition);
                    rsi = rs.iterator();

                    while(rsi.hasNext()) {
                        rsr = (ResultSetRow)rsi.next();
                        e.reset();
                        e = this.processNodeToMap(rsr.getNodeRef().toString(), tableName, e);
                        if(this.dbhb.rowExists(e)) {
                            this.dbhb.updateIntoTable(e);
                        } else {
                            this.dbhb.insertIntoTable(e);
                        }
                    }
                } catch (Exception var25) {
                    var25.printStackTrace();
                } finally {
                    if(rs != null) {
                        rs.close();
                    }

                }
            }
        } catch (Exception var27) {
            logger.fatal("Exception processPersson: " + var27.getMessage());
            throw new Exception(var27);
        }

        if(logger.isDebugEnabled()) {
            logger.debug("Exit processPerson");
        }

    }

    public Properties processPropertyDefinitions(Properties definition, NodeRef nodeRef, String defBacklist) {
        if(logger.isDebugEnabled()) {
            logger.debug("enter processPropertyDefinitions def  : " + definition);
            logger.debug("enter processPropertyDefinitions node : " + nodeRef);
            logger.debug("enter processPropertyDefinitions black: " + defBacklist);
        }

        try {
            Map e = this.getNodeService().getProperties(nodeRef);
            Iterator keys = e.keySet().iterator();
            Properties classToColumnType = this.getClassToColumnType();
            Properties replacementDataType = this.getReplacementDataType();

            while(keys.hasNext()) {
                String key = "";
                String type = "";

                try {
                    QName e1 = (QName)keys.next();
                    if(e1 != null) {
                        key = e1.toString();
                        key = this.replaceNameSpaces(e1);
                        if(!key.startsWith("{urn:schemas_microsoft_com:}") && !definition.containsKey(key)) {
                            type = "";
                            if(replacementDataType.containsKey(key)) {
                                type = replacementDataType.getProperty(key, "-").trim();
                            } else {
                                type = "-";

                                try {
                                    type = this.getDictionaryService().getProperty(e1).getDataType().toString().trim();
                                    type = type.substring(type.indexOf("}") + 1, type.length());
                                    type = classToColumnType.getProperty(type, "-");
                                } catch (NullPointerException ignored) {
                                }
                            }

                            if(type != null && !type.equals("-") && !type.equals("") && !key.equals("") && !defBacklist.contains("," + key + ",")) {
                                definition.setProperty(key, type);
                            }
                        }
                    }
                } catch (Exception var13) {
                    logger.error("processPropertyDefinitions: Property not found! Property below...");
                    logger.error("processPropertyDefinitions: type=" + type + ", key=" + key);
                    var13.printStackTrace();
                }
            }
        } catch (Exception var14) {
            var14.printStackTrace();
        }

        return definition;
    }

    public ReportLine processPropertyValues(ReportLine rl, NodeRef nodeRef, String blacklist) {
        Map<QName, Serializable> propertiesMap = this.getNodeService().getProperties(nodeRef);

        for (QName propName : propertiesMap.keySet()) {
            String key = "";
            String dtype = "";

            try {
                PropertyDefinition propertyDefinition = this.getDictionaryService().getProperty(propName);
                key = propName.toString();
                if (logger.isDebugEnabled()) {
                    logger.debug("processPropertyValues: voor: KEY=" + key);
                }

                if (!key.startsWith("{urn:schemas_microsoft_com:}")) {
                    key = this.replaceNameSpaces(propName);
                    if (logger.isDebugEnabled()) {
                        logger.debug("processPropertyValues: na: KEY=" + key);
                    }

                    dtype = propertyDefinition.getDataType().toString();
                    if (logger.isDebugEnabled()) {
                        logger.debug("processPropertyValues: voor: DTYPE=" + dtype);
                    }

                    dtype = dtype.substring(dtype.indexOf("}") + 1, dtype.length()).trim();
                    if (logger.isDebugEnabled()) {
                        logger.debug("processPropertyValues: na: DTYPE=" + dtype);
                    }

                    String type = this.getClassToColumnType().getProperty(dtype, "-");
                    if (logger.isDebugEnabled()) {
                        logger.debug("processPropertyValues: na: TYPE=" + type);
                    }

                    boolean multiValued = propertyDefinition.isMultiValued();
                    if (!blacklist.toLowerCase().contains("," + key.toLowerCase() + ",") && !type.equals("-")) {
                        String value = "";

                        try {
                            value = this.getPropertyValue(nodeRef, propName, dtype, multiValued, propertiesMap.get(propName));
                            if (value != null) {
                                rl.setLine(key, type, value, this.getReplacementDataType());
                            }
                        } catch (Exception var14) {
                            logger.error("Error setting ReportLine " + key + "=" + value);
                            logger.error(var14.getMessage());
                        }
                    }
                }
            } catch (Exception var15) {
                logger.error("processPropertyValues: Error in object, property " + key + " not found! (" + dtype + ")");
            }
        }

        return rl;
    }
}
