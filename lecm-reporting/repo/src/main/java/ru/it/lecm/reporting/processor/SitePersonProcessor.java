package ru.it.lecm.reporting.processor;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ru.it.lecm.reporting.ReportLine;
import ru.it.lecm.reporting.ReportingHelper;
import ru.it.lecm.reporting.db.DatabaseHelperBean;

import java.util.*;

public class SitePersonProcessor extends PropertyProcessor {

   private SiteService siteService;
   private static Log logger = LogFactory.getLog(SitePersonProcessor.class);


   public SiteService getSiteService() {
      return this.siteService;
   }

   public void setSiteService(SiteService siteService) {
      this.siteService = siteService;
   }

   public SitePersonProcessor(ServiceRegistry serviceRegistry, ReportingHelper reportingHelper, DatabaseHelperBean dbhb) throws Exception {
      this.setSiteService(serviceRegistry.getSiteService());
      this.setReportingHelper(reportingHelper);
      this.setDbhb(dbhb);
      this.setClassToColumnType(reportingHelper.getClassToColumnType());
      this.setReplacementDataTypes(reportingHelper.getReplacementDataType());
      this.setGlobalProperties(reportingHelper.getGlobalProperties());
      this.setNamespaces(reportingHelper.getNameSpaces());
      this.setBlacklist(reportingHelper.getBlacklist());
      if(logger.isDebugEnabled()) {
         logger.debug("##this.dataDictionary       =" + this.dataDictionary);
         logger.debug("##this.replacementDataTypes =" + this.replacementDataTypes);
         logger.debug("##this.namespaces           =" + this.namespaces);
      }

   }

   protected ReportLine processNodeToMap(String identifier, String table, ReportLine rl) {
      return null;
   }

   Properties processQueueDefinition(String table) {
      return null;
   }

   void processQueueValues(String table) throws Exception {}

   void havestNodes(NodeRef harvestDefinition) {}

   public void processSitePerson(String tableName) throws Exception {
      logger.debug("enter processSitePerson");
      try {
         tableName = this.reportingHelper.getValidTableName(tableName);
         ReportLine e = new ReportLine(tableName, this.reportingHelper);
         Properties classToColumnType = this.getClassToColumnType();
         Properties replacementTypes = this.getReplacementDataType();
         Properties definition = new Properties();
         definition.setProperty("siteName", classToColumnType.getProperty("name", "-"));
         definition.setProperty("siteRole", classToColumnType.getProperty("name", "-"));
         definition.setProperty("siteRoleGroup", classToColumnType.getProperty("name", "-"));
         definition.setProperty("userName", classToColumnType.getProperty("name", "-"));
         this.setTableDefinition(tableName, definition);
         List roleList = this.siteService.getSiteRoles();
         List siteInfoList = this.siteService.listSites((String)null, (String)null);
         Iterator i$ = siteInfoList.iterator();

         while(i$.hasNext()) {
            SiteInfo siteInfo = (SiteInfo)i$.next();
            Iterator i$1 = roleList.iterator();

            while(i$1.hasNext()) {
               String role = (String)i$1.next();

               try {
                  String e1 = this.siteService.getSiteRoleGroup(siteInfo.getShortName(), role);
                  Map someMap = this.siteService.listMembers(siteInfo.getShortName(), (String)null, role, 0, true);
                  Set keys = someMap.keySet();
                  Iterator i$2 = keys.iterator();

                  while(i$2.hasNext()) {
                     String userName = (String)i$2.next();
                     if(logger.isDebugEnabled()) {
                        logger.debug("processSitePerson: " + siteInfo.getShortName() + " | " + e1 + " | " + userName);
                     }

                     e.reset();

                     try {
                        e.setLine("siteName", classToColumnType.getProperty("name"), siteInfo.getShortName(), replacementTypes);
                        e.setLine("siteRole", classToColumnType.getProperty("name"), role, replacementTypes);
                        e.setLine("siteRoleGroup", classToColumnType.getProperty("name"), e1, replacementTypes);
                        e.setLine("userName", classToColumnType.getProperty("name"), userName, replacementTypes);
                     } catch (Exception var23) {
                        var23.printStackTrace();
                     }

                     boolean numberOfRows = false;
                     this.dbhb.insertIntoTable(e);
                  }
               } catch (Exception var24) {
                  var24.printStackTrace();
                  logger.fatal(var24.getMessage());
               }
            }
         }
      } catch (Exception var25) {
         logger.fatal("Exception selectFromWhere: " + var25.getMessage());
         throw new Exception(var25);
      }

      if(logger.isDebugEnabled()) {
         logger.debug("Exit processSitePerson");
      }

   }

}
