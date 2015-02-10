package org.alfresco.reporting.processor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import org.alfresco.model.ContentModel;
import org.alfresco.reporting.ReportLine;
import org.alfresco.reporting.ReportingHelper;
import org.alfresco.reporting.ReportingModel;
import org.alfresco.reporting.db.DatabaseHelperBean;
import org.alfresco.reporting.processor.PropertyProcessor;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.CategoryService;
import org.alfresco.service.cmr.search.CategoryService.Depth;
import org.alfresco.service.cmr.search.CategoryService.Mode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CategoryProcessor extends PropertyProcessor {

   protected CategoryService categoryService;
   private static Log logger = LogFactory.getLog(CategoryProcessor.class);


   public CategoryProcessor(DatabaseHelperBean dbhb, ReportingHelper reportingHelper, ServiceRegistry serviceRegistry) throws Exception {
      this.setNodeService(serviceRegistry.getNodeService());
      this.setDictionaryService(serviceRegistry.getDictionaryService());
      this.setFileFolderService(serviceRegistry.getFileFolderService());
      this.setSearchService(serviceRegistry.getSearchService());
      this.categoryService = serviceRegistry.getCategoryService();
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

   public void havestNodes(NodeRef harvestDefinition) {
      if(logger.isDebugEnabled()) {
         logger.debug("enter Categories");
      }

      this.dbhb.openReportingConnection();

      try {
         Collection e = this.categoryService.getRootCategories(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, ContentModel.ASPECT_GEN_CLASSIFIABLE);
         ArrayList carObject = (ArrayList)this.getNodeService().getProperty(harvestDefinition, ReportingModel.PROP_REPORTING_CATEGORIES);
         if(carObject != null && carObject.size() > 0) {
            Iterator cari = carObject.iterator();
            logger.debug("havestNodes: ## Categories=" + e.size());

            NodeRef category;
            String catName;
            String tableName;
            for(; cari.hasNext(); this.processCategoriesAsPath(tableName, category, catName, "value")) {
               category = (NodeRef)cari.next();
               catName = (String)this.getNodeService().getProperty(category, ContentModel.PROP_NAME);
               tableName = this.dbhb.fixTableColumnName(catName);
               if(logger.isDebugEnabled()) {
                  logger.debug("havestNodes: categoryName=" + catName + " tableName=" + tableName);
               }
            }
         }
      } catch (Exception var11) {
         var11.printStackTrace();
         logger.fatal("Exception in harvestNodes() " + var11.getMessage());
      } finally {
         this.dbhb.closeReportingConnection();
      }

      if(logger.isDebugEnabled()) {
         logger.debug("exit Categories");
      }

   }

   private void storeRegionPath(ReportLine rl, NodeRef nodeRef, String regionPath, String columnName, String labelValue) {
      try {
         Properties e = this.reportingHelper.getReplacementDataType();
         String uuid = nodeRef.toString();
         rl.setLine("sys_node_uuid", this.reportingHelper.getClassToColumnType().getProperty("noderef"), uuid.split("SpacesStore/")[1], e);
         rl.setLine(columnName, this.reportingHelper.getClassToColumnType().getProperty("path"), regionPath, e);
         rl.setLine("label", this.reportingHelper.getClassToColumnType().getProperty("label"), labelValue, e);
         this.dbhb.insertIntoTable(rl);
      } catch (Exception var9) {
         var9.printStackTrace();
         logger.fatal(var9.getMessage());
      }

   }

   public void processCategoriesAsPath(String tableName, NodeRef rootCatRef, String categoryName, String columnName) throws Exception {
      if(logger.isDebugEnabled()) {
         logger.debug("Enter processCategoriesAsPath, rootName=" + rootCatRef);
      }

      if(rootCatRef != null) {
         columnName = this.dbhb.fixTableColumnName(columnName);
         ReportLine rl = new ReportLine(tableName, this.getSimpleDateFormat(), this.reportingHelper);
         Properties definition = new Properties();
         definition.setProperty(columnName, this.reportingHelper.getClassToColumnType().getProperty("path", "-"));
         definition.setProperty("label", this.reportingHelper.getClassToColumnType().getProperty("label", "-"));

         try {
            this.setTableDefinition(tableName, definition);
            this.categoryService.getChildren(rootCatRef, Mode.SUB_CATEGORIES, Depth.IMMEDIATE);
            String labelValue = "";
            String regionPath = (String)this.getNodeService().getProperty(rootCatRef, ContentModel.PROP_NAME);
            if(!this.dbhb.tableIsRunning(tableName)) {
               Date theDate = new Date((new Date()).getTime() - 5000L);
               String nowFormattedDate = this.reportingHelper.getSimpleDateFormat().format(theDate);
               this.dbhb.setLastTimestampStatusRunning(tableName);
               this.dbhb.dropTables(tableName);
               this.dbhb.createEmptyTables(tableName);
               this.setTableDefinition(tableName, definition);
               this.storeRegionPath(rl, rootCatRef, regionPath, columnName, regionPath);
               rl.reset();
               Collection ccrs = this.categoryService.getChildren(rootCatRef, Mode.SUB_CATEGORIES, Depth.IMMEDIATE);
               if(ccrs.size() > 0) {
                  Iterator i$ = ccrs.iterator();

                  while(i$.hasNext()) {
                     ChildAssociationRef countryChildRef = (ChildAssociationRef)i$.next();
                     NodeRef countryRef = countryChildRef.getChildRef();
                     labelValue = (String)this.getNodeService().getProperty(countryRef, ContentModel.PROP_NAME);
                     String countryPath = regionPath + "/" + labelValue;
                     this.storeRegionPath(rl, countryRef, countryPath, columnName, labelValue);
                     rl.reset();
                     Collection cdcrs = this.categoryService.getChildren(countryRef, Mode.SUB_CATEGORIES, Depth.IMMEDIATE);
                     if(cdcrs.size() > 0) {
                        Iterator i$1 = cdcrs.iterator();

                        while(i$1.hasNext()) {
                           ChildAssociationRef countryDivChildRef = (ChildAssociationRef)i$1.next();
                           NodeRef countryDivRef = countryDivChildRef.getChildRef();
                           labelValue = (String)this.getNodeService().getProperty(countryDivRef, ContentModel.PROP_NAME);
                           String countryDivPath = countryPath + "/" + labelValue;
                           this.storeRegionPath(rl, countryDivRef, countryDivPath, columnName, labelValue);
                           rl.reset();
                        }
                     }
                  }
               }

               this.dbhb.setLastTimestampAndStatusDone(tableName, nowFormattedDate);
            }
         } catch (Exception var22) {
            var22.printStackTrace();
         }
      }

      if(logger.isDebugEnabled()) {
         logger.debug("Exit processCategoriesAsPath");
      }

   }

}
