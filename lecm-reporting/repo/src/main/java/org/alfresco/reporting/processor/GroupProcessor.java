package org.alfresco.reporting.processor;

import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import org.alfresco.reporting.ReportLine;
import org.alfresco.reporting.ReportingHelper;
import org.alfresco.reporting.Utils;
import org.alfresco.reporting.db.DatabaseHelperBean;
import org.alfresco.reporting.processor.PropertyProcessor;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AuthorityType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GroupProcessor extends PropertyProcessor {

   private static Log logger = LogFactory.getLog(GroupProcessor.class);


   public GroupProcessor(ServiceRegistry serviceRegistry, ReportingHelper reportingHelper, DatabaseHelperBean dbhb) throws Exception {
      this.setAuthorityService(serviceRegistry.getAuthorityService());
      this.setReportingHelper(reportingHelper);
      this.setDbhb(dbhb);
      this.setClassToColumnType(reportingHelper.getClassToColumnType());
      this.setReplacementDataTypes(reportingHelper.getReplacementDataType());
      this.setGlobalProperties(reportingHelper.getGlobalProperties());
      this.setNamespaces(reportingHelper.getNameSpaces());
      this.setBlacklist(reportingHelper.getBlacklist());
   }

   protected ReportLine processNodeToMap(String identifier, String table, ReportLine rl) {
      return null;
   }

   Properties processQueueDefinition(String table) {
      return null;
   }

   void processQueueValues(String table) throws Exception {}

   void havestNodes(NodeRef harvestDefinition) {}

   public void processGroups(String tableName) {
      logger.debug("enter processGroups");
      ReportLine rl = new ReportLine(tableName, this.getSimpleDateFormat(), this.reportingHelper);
      this.dbhb.openReportingConnection();

      try {
         Properties e = new Properties();
         Properties classToColumnType = this.getClassToColumnType();
         e.setProperty("sys_store_protocol", classToColumnType.getProperty("sys_store_protocol", "-"));
         e.setProperty("groupName", classToColumnType.getProperty("name", "-"));
         e.setProperty("groupDisplayName", classToColumnType.getProperty("name", "-"));
         e.setProperty("userName", classToColumnType.getProperty("name", "-"));
         e.setProperty("zones", classToColumnType.getProperty("zones", "-"));
         this.setTableDefinition(tableName, e);
         Set groupNames = this.getAuthorityService().getAllAuthorities(AuthorityType.GROUP);
         Iterator i$ = groupNames.iterator();

         while(i$.hasNext()) {
            String groupName = (String)i$.next();
            String groupDisplayName = this.getAuthorityService().getAuthorityDisplayName(groupName);
            Set zones = this.getAuthorityService().getAuthorityZones(groupName);
            Set userNames = this.getAuthorityService().getContainedAuthorities(AuthorityType.USER, groupName, false);
            Iterator i$1 = userNames.iterator();

            while(i$1.hasNext()) {
               String userName = (String)i$1.next();
               rl.reset();

               try {
                  rl.setLine("groupName", classToColumnType.getProperty("name"), groupName, this.getReplacementDataType());
                  rl.setLine("groupDisplayName", classToColumnType.getProperty("name"), groupDisplayName, this.getReplacementDataType());
                  rl.setLine("userName", classToColumnType.getProperty("name"), userName, this.getReplacementDataType());
                  rl.setLine("zones", classToColumnType.getProperty("zones"), Utils.setToString(zones), this.getReplacementDataType());
                  rl.setLine("cm_created", this.getClassToColumnType().getProperty("validFrom"), this.getSimpleDateFormat().format(new Date()), this.getReplacementDataType());
               } catch (Exception var18) {
                  logger.error("processUpdate: That is weird");
                  var18.printStackTrace();
               }

               boolean numberOfRows = false;
               this.dbhb.insertIntoTable(rl);
            }
         }
      } catch (Exception var19) {
         logger.fatal("processGroups - terrible error:");
         var19.printStackTrace();
      } finally {
         this.dbhb.closeReportingConnection();
         rl.reset();
      }

      logger.debug("Exit processGroups");
   }

}
