package org.alfresco.reporting.behaviour;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies.OnCreateChildAssociationPolicy;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.reporting.ReportingModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OnCreateFolder_Specialize implements OnCreateChildAssociationPolicy {

   private PolicyComponent policyComponent;
   private NodeService nodeService;
   private static Log logger = LogFactory.getLog(OnCreateFolder_Specialize.class);


   public void initialise() {
      this.policyComponent.bindAssociationBehaviour(OnCreateChildAssociationPolicy.QNAME, ReportingModel.TYPE_REPORTING_ROOT, ContentModel.ASSOC_CONTAINS, new JavaBehaviour(this, "onCreateChildAssociation", NotificationFrequency.FIRST_EVENT));
   }

   public void onCreateChildAssociation(ChildAssociationRef car, boolean arg1) {
      NodeRef parent = car.getParentRef();
      NodeRef child = car.getChildRef();

      try {
         if(this.nodeService.getType(child).equals(ContentModel.TYPE_FOLDER) && !this.nodeService.getType(child).equals(ReportingModel.TYPE_REPORTING_CONTAINER)) {
            this.nodeService.setType(child, ReportingModel.TYPE_REPORTING_CONTAINER);
         }
      } catch (Exception var6) {
         logger.fatal("The specialization into a REPORTING_CONTAINER failed... Bad luck! (Weird though)");
         logger.fatal(var6.getMessage());
      }

   }

   public void setPolicyComponent(PolicyComponent policyComponent) {
      this.policyComponent = policyComponent;
   }

   public void setNodeService(NodeService nodeService) {
      this.nodeService = nodeService;
   }

}
