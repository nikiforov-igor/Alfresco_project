package ru.it.lecm.orgstructure.policies;

import java.util.List;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;

import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 * Created with IntelliJ IDEA.
 * User: AIvkin
 * Date: 12.12.12
 * Time: 16:06
 */
public class OrgstructurePrimaryPositionPolicy
		extends SecurityNotificationsPolicyBase
		implements NodeServicePolicies.OnCreateAssociationPolicy
{
	@Override
	public final void init() {
		super.init();

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				OrgstructureBean.TYPE_EMPLOYEE_LINK, OrgstructureBean.ASSOC_EMPLOYEE_LINK_EMPLOYEE,
				new JavaBehaviour(this, "onCreateAssociation"));
	}

	/**
	 * Назначение Сотрудника на Должность
	 */
	@Override
	public void onCreateAssociation(AssociationRef associationRef) {
		NodeRef emplyoeeLink = associationRef.getSourceRef();
		NodeRef emplyoee = associationRef.getTargetRef();
		ChildAssociationRef parent = nodeService.getPrimaryParent(emplyoeeLink);
		if (orgstructureService.isStaffList(parent.getParentRef())) {
			List<NodeRef> staffs = orgstructureService.getEmployeeStaffs(emplyoee);
			nodeService.setProperty(emplyoeeLink, OrgstructureBean.PROP_EMP_LINK_IS_PRIMARY, staffs.size() == 1);
		}
	}

}
