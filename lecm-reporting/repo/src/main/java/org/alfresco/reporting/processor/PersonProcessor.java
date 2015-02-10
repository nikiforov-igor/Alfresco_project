package org.alfresco.reporting.processor;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.alfresco.model.ContentModel;
import org.alfresco.reporting.ReportLine;
import org.alfresco.reporting.ReportingHelper;
import org.alfresco.reporting.ReportingModel;
import org.alfresco.reporting.Utils;
import org.alfresco.reporting.db.DatabaseHelperBean;
import org.alfresco.reporting.processor.PropertyProcessor;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

         String var10000 = (String)this.getNodeService().getProperty(e, ContentModel.PROP_USERNAME);
         String account_expires = null;
         String account_expirydate = null;
         String account_locked = null;
         String enabled = null;
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

         rl.setLine("account_enabled", this.getClassToColumnType().getProperty("boolean"), enabled.toString(), this.getReplacementDataType());
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
      this.dbhb.openReportingConnection();

      try {
         tableName = this.dbhb.fixTableColumnName(tableName);
         this.dbhb.createEmptyTables(tableName);
         ReportLine e = new ReportLine(tableName, this.getSimpleDateFormat(), this.reportingHelper);
         Object stmt = null;
         Properties definition = new Properties();
         long highestDbId = 0L;
         boolean continueSearchCycle = true;
         String query = "+TYPE:\"cm:person\"";
         ResultSet rs = null;

         while(continueSearchCycle) {
            try {
               if(logger.isDebugEnabled()) {
                  logger.debug("processPerson: classToColumnType=" + this.getClassToColumnType());
               }

               SearchParameters e1 = new SearchParameters();
               String fullQuery = query + " +@sys\\:node-dbid:[" + highestDbId + " TO MAX]";
               if(logger.isDebugEnabled()) {
                  logger.debug("processPerson: query=" + fullQuery);
               }

               e1.setLanguage("lucene");
               e1.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
               e1.addSort("@{http://www.alfresco.org/model/system/1.0}node-dbid", true);
               e1.setQuery(fullQuery);
               if(logger.isDebugEnabled()) {
                  logger.debug("processPerson: Before searchService");
               }

               rs = this.getSearchService().query(e1);
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

               if(continueSearchCycle) {
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

                     highestDbId = ((Long)this.getNodeService().getProperty(rsr.getNodeRef(), ReportingModel.PROP_SYSTEM_NODE_DBID)).longValue() + 1L;
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
                     boolean numberOfRows = false;
                     if(this.dbhb.rowExists(e)) {
                        this.dbhb.updateIntoTable(e);
                     } else {
                        this.dbhb.insertIntoTable(e);
                     }
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
      } finally {
         this.dbhb.closeReportingConnection();
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
                  key = this.replaceNameSpaces(key);
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
                        } catch (NullPointerException var12) {
                           ;
                        }
                     }

                     if(type != null && !type.equals("-") && !type.equals("") && key != null && !key.equals("") && !defBacklist.contains("," + key + ",")) {
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
      Map map = this.getNodeService().getProperties(nodeRef);
      Iterator keys = map.keySet().iterator();

      while(keys.hasNext()) {
         String key = "";
         String dtype = "";

         try {
            QName e = (QName)keys.next();
            key = e.toString();
            if(logger.isDebugEnabled()) {
               logger.debug("processPropertyValues: voor: KEY=" + key);
            }

            if(!key.startsWith("{urn:schemas_microsoft_com:}")) {
               key = this.replaceNameSpaces(key);
               if(logger.isDebugEnabled()) {
                  logger.debug("processPropertyValues: na: KEY=" + key);
               }

               dtype = this.getDictionaryService().getProperty(e).getDataType().toString();
               if(logger.isDebugEnabled()) {
                  logger.debug("processPropertyValues: voor: DTYPE=" + dtype);
               }

               dtype = dtype.substring(dtype.indexOf("}") + 1, dtype.length()).trim();
               if(logger.isDebugEnabled()) {
                  logger.debug("processPropertyValues: na: DTYPE=" + dtype);
               }

               String theObject = this.getClassToColumnType().getProperty(dtype, "-");
               String type = theObject.toString();
               if(logger.isDebugEnabled()) {
                  logger.debug("processPropertyValues: na: TYPE=" + type);
               }

               boolean multiValued = false;
               multiValued = this.getDictionaryService().getProperty(e).isMultiValued();
               if(!blacklist.toLowerCase().contains("," + key.toLowerCase() + ",") && !type.equals("-")) {
                  String value = "";

                  try {
                     value = this.getPropertyValue(nodeRef, e, dtype, multiValued);
                     if(value != null) {
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
