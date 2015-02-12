package ru.it.lecm.reporting.script;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.audit.AuditService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ru.it.lecm.reporting.ReportingHelper;
import ru.it.lecm.reporting.ReportingModel;
import ru.it.lecm.reporting.db.DatabaseHelperBean;
import ru.it.lecm.reporting.mybatis.SelectFromWhere;

import java.text.SimpleDateFormat;
import java.util.*;

public class AlfrescoReporting extends BaseScopableProcessorExtension {

   private static Log logger = LogFactory.getLog(AlfrescoReporting.class);
   private ServiceRegistry serviceRegistry;
   private SearchService searchService;
   private NodeService nodeService = null;
   private AuthorityService authorityService = null;
   private AuthenticationService authenticationService = null;
   private AuditService auditService = null;
   private SiteService siteService = null;
   private ReportingHelper reportingHelper;
   private DatabaseHelperBean dbhb = null;
   private NodeRef reportingRootRef = null;


   private SimpleDateFormat getSimpleDateFormat() {
      return new SimpleDateFormat();
   }

   public ServiceRegistry getServiceRegistry() {
      return this.serviceRegistry;
   }

   public void setServiceRegistry(ServiceRegistry serviceRegistry) {
      this.serviceRegistry = serviceRegistry;
   }

   public void setNodeService(NodeService nodeService) {
      this.nodeService = nodeService;
   }

   public void setAuditService(AuditService auditService) {
      this.auditService = auditService;
   }

   public void setSearchService(SearchService searchService) {
      this.searchService = searchService;
   }

   public void setAuthorityService(AuthorityService authorityService) {
      this.authorityService = authorityService;
   }

   public void setSiteService(SiteService siteService) {
      this.siteService = siteService;
   }

   public void setDatabaseHelperBean(DatabaseHelperBean databaseHelperBean) {
      this.dbhb = databaseHelperBean;
   }

   public void setReportingHelper(ReportingHelper reportingHelper) {
      this.reportingHelper = reportingHelper;
   }

   public String getStoreList() {
      return this.reportingHelper.getGlobalProperties().getProperty("reporting.harvest.stores", "");
   }

   public boolean isExecutionEnabled() {
      boolean executionEnabled = true;

      try {
         logger.debug("isExecutionEnabled: " + this.nodeService.getProperty(this.getReportingRoot(), ReportingModel.PROP_REPORTING_GLOBAL_EXECUTION_ENABLED));
         executionEnabled = ((Boolean)this.nodeService.getProperty(this.getReportingRoot(), ReportingModel.PROP_REPORTING_GLOBAL_EXECUTION_ENABLED)).booleanValue();
      } catch (Exception var3) {
         logger.debug("isExecutionEnabled() returning exception. Thus returning true;");
         executionEnabled = true;
      }

      return executionEnabled;
   }

   public boolean isHarvestEnabled() {
      boolean harvestEnabled = true;

      try {
         logger.debug("isHarvestEnabled: " + this.nodeService.getProperty(this.getReportingRoot(), ReportingModel.PROP_REPORTING_HARVEST_ENABLED));
         harvestEnabled = ((Boolean)this.nodeService.getProperty(this.getReportingRoot(), ReportingModel.PROP_REPORTING_HARVEST_ENABLED)).booleanValue();
      } catch (Exception var3) {
         logger.debug("isHarvestEnabled() returning exception. Thus returning true;");
         harvestEnabled = true;
      }

      return harvestEnabled;
   }

   public int countSearchResutls(String query) {
      SearchParameters sp = new SearchParameters();
      sp.setLanguage("lucene");
      sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
      sp.setQuery(query);
      ResultSet placeHolderResults = this.searchService.query(sp);
      return placeHolderResults.length();
   }

   private NodeRef getReportingRoot() {
      if(this.reportingRootRef != null) {
         return this.reportingRootRef;
      } else {
         NodeRef thisRootRef = null;
         ResultSet placeHolderResults = null;

         try {
            String e = "TYPE:\"reporting:reportingRoot\"";
            SearchParameters sp = new SearchParameters();
            sp.setLanguage("lucene");
            sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
            sp.setQuery(e);
            placeHolderResults = this.searchService.query(sp);
            Iterator i$ = placeHolderResults.iterator();

            while(i$.hasNext()) {
               ResultSetRow placeHolderRow = (ResultSetRow)i$.next();
               thisRootRef = placeHolderRow.getChildAssocRef().getChildRef();
               logger.debug("Found reporting root: " + this.nodeService.getProperty(thisRootRef, ContentModel.PROP_NAME));
            }
         } catch (Exception var10) {
            var10.printStackTrace();
         } finally {
            if(placeHolderResults != null) {
               placeHolderResults.close();
            }

         }

         this.reportingRootRef = thisRootRef;
         return thisRootRef;
      }
   }

   public ScriptNode getReportingRootNode() {
      return new ScriptNode(this.getReportingRoot(), this.serviceRegistry);
   }

   public void testLog(String logString) {
      logger.debug("## " + logString);
   }

   public void testLogFailed(String logString) {
      logger.fatal("## !! " + logString);
   }

   public String selectFromWhere(String select, String table, String where) {
      try {
         logger.debug("enter selectFromWhere: select=" + select + " from=" + table + " where=" + where);
         new SelectFromWhere(select, table, where);
         return this.dbhb.selectFromWhere(select, table, where);
      } catch (Exception var5) {
         logger.fatal("Exception selectFromWhere: select=" + select + " from=" + table + " where=" + where);
         logger.fatal(var5.getMessage());
         throw new AlfrescoRuntimeException(var5.getMessage());
      }
   }

   public void resetLastTimestampTable(String tablename) {
      this.dbhb.resetLastTimestampTable(tablename);
   }

   public void clearLastTimestampTable(String tablename) {
      this.dbhb.clearLastTimestampTable(tablename);
   }

   public String getLastTimestampStatus(String tablename) {
      return this.dbhb.getLastTimestampStatus(tablename);
   }

   public String getLastTimestamp(String tablename) {
      return this.dbhb.getLastTimestamp(tablename);
   }

   public void setLastTimestampAndStatusDone(String tablename, String timestamp) {
      this.dbhb.setLastTimestampAndStatusDone(tablename, timestamp);
   }

   public void setLastTimestampStatusRunning(String tablename) {
      this.dbhb.setLastTimestampStatusRunning(tablename);
   }

   public void createLastTimestampTable(String tablename) {
      this.dbhb.createLastTimestampTableRow(tablename);
   }

   public void dropLastTimestampTable() {
      this.dbhb.dropLastTimestampTable();
   }

   public Map getShowTables() {
      return this.dbhb.getShowTables();
   }

   public AlfrescoReporting() {
      logger.info("Starting AlfrescoReporting module (Constructor)");
      int numberOfHours = TimeZone.getDefault().getRawOffset() / 3600000;
      String sign = "+";
      if(numberOfHours < 0) {
         sign = "-";
      }

      logger.info("Using timezone (for right hour in timestamp): " + TimeZone.getDefault().getDisplayName() + " (" + sign + numberOfHours + ")");
   }

   public void dropTables(String tablesToDrop) {
      logger.debug("Starting dropTables: " + tablesToDrop);

      try {
         this.dbhb.dropTables(tablesToDrop);
      } catch (Exception var3) {
         logger.fatal("Exception dropTables: " + var3.getMessage());
         throw new AlfrescoRuntimeException(var3.getMessage());
      }
   }

   public void setAllStatusesDoneForTable() {
      this.dbhb.setAllStatusesDoneForTable();
   }

   public String getDatabaseVendor() {
      return this.reportingHelper.getDatabaseProvider();
   }

   public void logAllPropertyTypes() {
      if(logger.isDebugEnabled()) {
         Collection dts = this.serviceRegistry.getDictionaryService().getAllDataTypes();
         Iterator myIterator = dts.iterator();

         while(myIterator.hasNext()) {
            QName q = (QName)myIterator.next();
            String returnType = q.toString();
            returnType = returnType.substring(returnType.indexOf("}") + 1, returnType.length());
            logger.debug(returnType);
         }
      }

   }

   public void logAllGlobalProperties() {
      if(logger.isDebugEnabled()) {
         Enumeration keys = this.reportingHelper.getGlobalProperties().keys();

         while(keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            if(key.contains("reporting.")) {
               logger.debug(key + "=" + this.reportingHelper.getGlobalProperties().getProperty(key));
            }
         }
      }

   }

}
