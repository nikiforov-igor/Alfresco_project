
package ru.it.lecm.orgstructure.policies;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.site.SiteModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.orgstructure.beans.OrgstructureAspectsModel;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 *
 * @author snovikov
 */
public class OrgstructureSitePolicy extends BaseBean {

	private PolicyComponent policyComponent;
	private OrgstructureBean orgstructureService;
	
	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}
	
	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}
	
	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}
	
	public void init() {
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
						SiteModel.TYPE_SITE,
						new JavaBehaviour(this, "onCreateSite", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
	}
	
	public void onCreateSite(ChildAssociationRef childAssocRef) {
		final NodeRef site = childAssocRef.getChildRef();
		
		NodeRef organization = orgstructureService.getEmployeeOrganization(orgstructureService.getCurrentEmployee());
        if (null != organization) {
            nodeService.addAspect(site, OrgstructureAspectsModel.ASPECT_HAS_LINKED_ORGANIZATION, null);
            nodeService.createAssociation(site, organization, OrgstructureAspectsModel.ASSOC_LINKED_ORGANIZATION);
        }
	}
	
}
