package ru.it.lecm.orgstructure.policies;

import java.util.List;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 * Created with IntelliJ IDEA.
 * User: AIvkin
 * Date: 12.12.12
 * Time: 16:06
 * To change this template use File | Settings | File Templates.
 */
public class OrgstructurePrimaryPositionPolicy implements NodeServicePolicies.OnCreateAssociationPolicy {
	private static ServiceRegistry serviceRegistry;
	private static PolicyComponent policyComponent;
	private static OrgstructureBean orgstructureService;

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		OrgstructurePrimaryPositionPolicy.serviceRegistry = serviceRegistry;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		OrgstructurePrimaryPositionPolicy.policyComponent = policyComponent;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		OrgstructurePrimaryPositionPolicy.orgstructureService = orgstructureService;
	}

	public final void init() {
		PropertyCheck.mandatory(this, "serviceRegistry", serviceRegistry);
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);
		PropertyCheck.mandatory(this, "orgstructureService", orgstructureService);

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				OrgstructureBean.TYPE_EMPLOYEE_LINK, OrgstructureBean.ASSOC_EMPLOYEE_LINK_EMPLOYEE,
				new JavaBehaviour(this, "onCreateAssociation"));
	}

	@Override
	public void onCreateAssociation(AssociationRef associationRef) {
		NodeRef emplyoeeLink = associationRef.getSourceRef();
		NodeRef emplyoee = associationRef.getTargetRef();
		NodeService nodeService = serviceRegistry.getNodeService();
		ChildAssociationRef parent = nodeService.getPrimaryParent(emplyoeeLink);
		if (orgstructureService.isStaffList(parent.getParentRef())) {
			List<NodeRef> staffs = orgstructureService.getEmployeeStaffs(emplyoee);
			nodeService.setProperty(emplyoeeLink, OrgstructureBean.PROP_EMP_LINK_IS_PRIMARY, staffs.size() == 1);
		}
	}
}