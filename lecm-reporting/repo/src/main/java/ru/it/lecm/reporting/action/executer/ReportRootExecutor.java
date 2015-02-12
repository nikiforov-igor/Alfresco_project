package ru.it.lecm.reporting.action.executer;

import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ru.it.lecm.reporting.ReportingHelper;
import ru.it.lecm.reporting.execution.ReportingContainer;
import ru.it.lecm.reporting.execution.ReportingRoot;

import java.util.Iterator;
import java.util.List;

public class ReportRootExecutor extends ActionExecuterAbstractBase {

   public static final String PARAM_FREQUENCY = "executionFrequency";
   public static final String NAME = "report-root-executer";
   private ActionService actionService;
   private NodeService nodeService;
   private SearchService searchService;
   private ReportingHelper reportingHelper;
   private int startDelayMinutes = 0;
   private static Log logger = LogFactory.getLog(ReportRootExecutor.class);


   protected void executeImpl(Action action, NodeRef someRef) {
      if(logger.isDebugEnabled()) {
         logger.debug("enter executeImpl");
      }

      String executionFrequency = (String)action.getParameterValue("executionFrequency");
      String query = "+@reporting\\:executionFrequency:\"" + executionFrequency + "\" " + "+@reporting\\:executionEnabled:true";
      if(logger.isDebugEnabled()) {
         logger.debug("executeImpl query=" + query);
      }

      ResultSet results = this.searchService.query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, "Lucene", query);
      Iterator i$ = results.iterator();

      while(i$.hasNext()) {
         ResultSetRow resultRow = (ResultSetRow)i$.next();
         NodeRef containerRef = resultRow.getChildAssocRef().getChildRef();
         ReportingContainer reportingContainer = new ReportingContainer(containerRef);
         this.reportingHelper.initializeReportingContainer(reportingContainer);
         if(logger.isDebugEnabled()) {
            logger.debug("Found container: " + reportingContainer.getName());
         }

         ReportingRoot reportingRoot = new ReportingRoot(this.reportingHelper.getReportingRoot(containerRef));
         this.reportingHelper.initializeReportingRoot(reportingRoot);
         if(reportingRoot.isGlobalExecutionEnabled()) {
            Action customAction = this.actionService.createAction("report-container-executer");
            this.actionService.executeAction(customAction, containerRef);
         } else {
            logger.warn("Container execution of " + reportingContainer.getName() + " veto\'d by ReportingRoot " + reportingRoot.getName());
         }
      }

      if(logger.isDebugEnabled()) {
         logger.debug("exit executeImpl");
      }

   }

   protected void addParameterDefinitions(List paramList) {
      paramList.add(new ParameterDefinitionImpl("executionFrequency", DataTypeDefinition.TEXT, false, this.getParamDisplayLabel("executionFrequency")));
   }

   public void setNodeService(NodeService nodeService) {
      this.nodeService = nodeService;
   }

   public void setSearchService(SearchService searchService) {
      this.searchService = searchService;
   }

   public void setActionService(ActionService actionService) {
      this.actionService = actionService;
   }

   public void setReportingHelper(ReportingHelper reportingHelper) {
      this.reportingHelper = reportingHelper;
   }

   public void setStartDelayMinutes(String minutes) {
      this.startDelayMinutes = Integer.parseInt(minutes);
   }

}
