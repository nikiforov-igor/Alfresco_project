package ru.it.lecm.reporting.web.bean;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.QueryParameterDefinition;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.web.app.Application;
import org.alfresco.web.app.servlet.FacesHelper;
import org.alfresco.web.bean.BrowseBean;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.ui.common.component.UIActionLink;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExecuteScriptHandlerBean {

   private static final String PARAM_ID = "id";
   private static final String PARAM_SCRIPT_REF = "script";
   private static final String PARAM_LABEL_ID = "label-id";
   private static final String PARAM_ACTION_LOCATION = "actionLocation";
   private static Log logger = LogFactory.getLog(ExecuteScriptHandlerBean.class);
   private BrowseBean browseBean;
   private NodeService nodeService;
   private SearchService searchService;
   private NamespaceService namespaceService;
   private ActionService actionService;


   public void execute(ActionEvent event) {
      UIActionLink link = (UIActionLink)event.getComponent();
      Map params = link.getParameterMap();
      String id = (String)params.get("id");
      if(id != null && !id.equals("")) {
         String scriptName = (String)params.get("script");
         if(scriptName != null && !scriptName.equals("")) {
            String labelId = (String)params.get("label-id");
            if(labelId != null && !labelId.equals("")) {
               FacesContext context = FacesContext.getCurrentInstance();
               NodeRef actionedUponNodeRef = new NodeRef(Repository.getStoreRef(), id);
               String filename = (String)this.nodeService.getProperty(actionedUponNodeRef, ContentModel.PROP_NAME);
               NodeRef parent = this.nodeService.getPrimaryParent(actionedUponNodeRef).getParentRef();
               String msg;
               if(this.nodeService.exists(actionedUponNodeRef)) {
                  msg = Application.getRootPath(context) + "/app:dictionary/app:scripts/cm:reporting/cm:" + scriptName;
                  NodeRef fm = this.nodeService.getRootNode(Repository.getStoreRef());
                  List nodes = this.searchService.selectNodes(fm, msg, (QueryParameterDefinition[])null, this.namespaceService, false);
                  if(nodes == null || nodes.size() == 0) {
                     throw new AlfrescoRuntimeException("Unable to locate script with name: " + scriptName);
                  }

                  NodeRef scriptRef = (NodeRef)nodes.get(0);
                  if(logger.isDebugEnabled()) {
                     logger.debug("Script found: " + scriptRef);
                  }

                  HashMap model = new HashMap(2);
                  model.put("document", new ScriptNode(actionedUponNodeRef, Repository.getServiceRegistry(context)));
                  model.put("space", new ScriptNode(parent, Repository.getServiceRegistry(context)));
                  Action action = this.actionService.createAction("script");
                  action.setParameterValue("script-ref", scriptRef);
                  this.actionService.executeAction(action, actionedUponNodeRef);
                  BrowseBean browseBean = (BrowseBean)FacesHelper.getManagedBean(context, "BrowseBean");
                  String actionLocation = (String)params.get("actionLocation");
                  UIComponent comp;
                  if(actionLocation.equals("document-details")) {
                     browseBean.getDocument().reset();
                     comp = context.getViewRoot().findComponent("dialog:dialog-body:document-props");
                     comp.getChildren().clear();
                  } else if(actionLocation.equals("folder-details")) {
                     if(browseBean.getActionSpace() != null) {
                        browseBean.getActionSpace().reset();
                     }

                     comp = context.getViewRoot().findComponent("dialog:dialog-body:space-props");
                     if(comp != null && comp.getChildren() != null) {
                        comp.getChildren().clear();
                     }
                  } else if(actionLocation.equals("folder-browse") && this.nodeService.exists(parent)) {
                     browseBean.clickSpace(parent);
                  }
               }

               msg = Application.getMessage(context, labelId) + " (" + filename + ")";
               FacesMessage fm1 = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, msg);
               context.addMessage(msg, fm1);
            } else {
               throw new AlfrescoRuntimeException("Required parameter \'label-id\' is null or empty");
            }
         } else {
            throw new AlfrescoRuntimeException("Required parameter \'script\' is null or empty");
         }
      } else {
         throw new AlfrescoRuntimeException("Required parameter \'id\' is null or empty");
      }
   }

   public BrowseBean getBrowseBean() {
      return this.browseBean;
   }

   public void setBrowseBean(BrowseBean browseBean) {
      this.browseBean = browseBean;
   }

   public NodeService getNodeService() {
      return this.nodeService;
   }

   public void setNodeService(NodeService nodeService) {
      this.nodeService = nodeService;
   }

   public SearchService getSearchService() {
      return this.searchService;
   }

   public void setSearchService(SearchService searchService) {
      this.searchService = searchService;
   }

   public NamespaceService getNamespaceService() {
      return this.namespaceService;
   }

   public void setNamespaceService(NamespaceService namespaceService) {
      this.namespaceService = namespaceService;
   }

   public ActionService getActionService() {
      return this.actionService;
   }

   public void setActionService(ActionService actionService) {
      this.actionService = actionService;
   }

}
