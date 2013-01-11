package ru.it.lecm.orgstructure.policies;

import java.util.List;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 * Created with IntelliJ IDEA.
 * User: AIvkin
 * Date: 11.12.12
 * Time: 15:19
 * To change this template use File | Settings | File Templates.
 */
public class OrgstructureBossPolicy implements NodeServicePolicies.OnCreateNodePolicy {
	private static ServiceRegistry serviceRegistry;
	private static PolicyComponent policyComponent;
	private static OrgstructureBean orgstructureService;

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		OrgstructureBossPolicy.serviceRegistry = serviceRegistry;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		OrgstructureBossPolicy.policyComponent = policyComponent;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		OrgstructureBossPolicy.orgstructureService = orgstructureService;
	}

	public final void init() {
		PropertyCheck.mandatory(this, "serviceRegistry", serviceRegistry);
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);
		PropertyCheck.mandatory(this, "orgstructureService", orgstructureService);

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				OrgstructureBean.TYPE_STAFF_LIST, new JavaBehaviour(this, "onCreateNode"));

	}

	@Override
	public void onCreateNode(ChildAssociationRef childAssocRef) {
		NodeRef node = childAssocRef.getChildRef();
		NodeRef parent = childAssocRef.getParentRef();
		NodeService nodeService = serviceRegistry.getNodeService();

		List<NodeRef> staffList = orgstructureService.getUnitStaffLists(parent);
		nodeService.setProperty(node, OrgstructureBean.PROP_STAFF_LIST_IS_BOSS, staffList.size() == 1 && staffList.get(0).equals(node));
	}
}
