package org.alfresco.reporting.behaviour;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies.OnCreateChildAssociationPolicy;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.reporting.ReportingModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OnCreateDocument_Specialize implements OnCreateChildAssociationPolicy {

   private PolicyComponent policyComponent;
   private NodeService nodeService;
   private static Log logger = LogFactory.getLog(OnCreateDocument_Specialize.class);


   public void initialise() {
      this.policyComponent.bindAssociationBehaviour(OnCreateChildAssociationPolicy.QNAME, ReportingModel.TYPE_REPORTING_ROOT, ContentModel.ASSOC_CONTAINS, new JavaBehaviour(this, "onCreateChildAssociation", NotificationFrequency.FIRST_EVENT));
      this.policyComponent.bindAssociationBehaviour(OnCreateChildAssociationPolicy.QNAME, ReportingModel.TYPE_REPORTING_CONTAINER, ContentModel.ASSOC_CONTAINS, new JavaBehaviour(this, "onCreateChildAssociation", NotificationFrequency.FIRST_EVENT));
   }

   public void onCreateChildAssociation(ChildAssociationRef car, boolean arg1) {
      NodeRef parent = car.getParentRef();
      NodeRef child = car.getChildRef();

      try {
         if(this.nodeService.exists(child) && this.nodeService.exists(parent) && this.nodeService.getType(child).equals(ContentModel.TYPE_CONTENT) && !this.nodeService.hasAspect(child, ContentModel.ASPECT_TEMPORARY) && !this.nodeService.getType(child).equals(ContentModel.TYPE_THUMBNAIL) && !this.nodeService.getType(child).equals(ContentModel.TYPE_FAILED_THUMBNAIL)) {
            if(this.nodeService.hasAspect(parent, ReportingModel.ASPECT_REPORTING_CONTAINERABLE)) {
               this.nodeService.setType(child, ReportingModel.TYPE_REPORTING_REPORTTEMPLATE);
            }

            if(this.nodeService.hasAspect(parent, ReportingModel.ASPECT_REPORTING_REPORTING_ROOTABLE)) {
               this.nodeService.setType(child, ReportingModel.TYPE_REPORTING_HARVEST_DEFINITION);
            }
         }
      } catch (InvalidNodeRefException var6) {
         logger.info("The specialization into a REPORTING_REPORT or HARVEST_DFEFINITION failed, throwing a InvalidNodeRefException against document: " + child.toString() + " or against folder: " + parent);
      } catch (Exception var7) {
         logger.error("The specialization into a REPORTING_REPORT or HARVEST_DFEFINITION failed... Bad luck! (Weird though)");
         logger.error(var7.getMessage());
      }

   }

   public void setPolicyComponent(PolicyComponent policyComponent) {
      this.policyComponent = policyComponent;
   }

   public void setNodeService(NodeService nodeService) {
      this.nodeService = nodeService;
   }

}
