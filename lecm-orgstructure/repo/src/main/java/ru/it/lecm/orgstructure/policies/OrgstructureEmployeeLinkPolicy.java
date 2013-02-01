package ru.it.lecm.orgstructure.policies;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 * @author dbashmakov
 *         Date: 10.01.13
 *         Time: 16:21
 */
public class OrgstructureEmployeeLinkPolicy
	extends SecurityJournalizedPolicyBase
	implements
		  NodeServicePolicies.OnCreateNodePolicy
		, NodeServicePolicies.OnCreateAssociationPolicy
		, NodeServicePolicies.OnDeleteAssociationPolicy
{

	@Override
	public void init() {
		PropertyCheck.mandatory(this, "authService", authService);
		super.init();

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				OrgstructureBean.TYPE_EMPLOYEE_LINK, new JavaBehaviour(this, "onCreateNode"));
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				OrgstructureBean.TYPE_EMPLOYEE_LINK, OrgstructureBean.ASSOC_EMPLOYEE_LINK_EMPLOYEE,
				new JavaBehaviour(this, "onCreateAssociation"));
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				OrgstructureBean.TYPE_EMPLOYEE_LINK, OrgstructureBean.ASSOC_EMPLOYEE_LINK_EMPLOYEE,
				new JavaBehaviour(this, "onDeleteAssociation"));
	}

	@Override
	public void onCreateAssociation(AssociationRef nodeAssocRef) {
		try {
			NodeRef employeeLink = nodeAssocRef.getSourceRef();
			NodeRef staff = nodeService.getPrimaryParent(employeeLink).getParentRef();

			// получаем инициатора
			final String initiator = authService.getCurrentUserName();

			// получаем основной объект - сотрудник
			NodeRef employee = nodeAssocRef.getTargetRef();

			if (orgstructureService.isStaffList(staff)) {
				// Назначение на должность
				String defaultDescription = "Сотрудник #mainobject назначен на должность \"#object1\" в подразделении #object2";
				NodeRef position = orgstructureService.getPositionByStaff(staff);
				NodeRef unit = orgstructureService.getUnitByStaff(staff);
				List<String> objects = new ArrayList<String>(2);
				objects.add(position != null ? position.toString() : "");
				objects.add(unit != null ? unit.toString() : "");
				businessJournalService.log(initiator, employee, EventCategory.TAKE_JOB_POSITION, defaultDescription, objects);

				if ((Boolean) nodeService.getProperty(staff, OrgstructureBean.PROP_STAFF_LIST_IS_BOSS)) {
					// Назначение на должность
					defaultDescription = "Сотрудник #mainobject назначен руководителем в подразделении #object1";
					objects = new ArrayList<String>(1);
					objects.add(unit != null ? unit.toString() : "");
					businessJournalService.log(initiator, employee, EventCategory.TAKE_BOSS_POSITION, defaultDescription, objects);
				}
				// назначение СОтрудника на должность
				notifyEmploeeSetDP(employee, position);
			} else {
				NodeRef role = orgstructureService.getRoleByWorkForce(staff);
				notifyEmploeeSetBR(employee, role);
			}
		} catch (Exception e) {
			logger.error( "Exception at association post processing onCreateAssociation:", e);
		}
	}

	@Override
	public void onCreateNode(ChildAssociationRef childAssocRef) {
		NodeRef employeeLink = childAssocRef.getChildRef();
		NodeRef parent = childAssocRef.getParentRef();

		nodeService.createAssociation(parent, employeeLink, OrgstructureBean.ASSOC_ELEMENT_MEMBER_EMPLOYEE);
	}

	@Override
	public void onDeleteAssociation(AssociationRef nodeAssocRef) {
		try {
			final NodeRef employeeLink = nodeAssocRef.getSourceRef();
			final NodeRef parent = nodeService.getPrimaryParent(employeeLink).getParentRef();
			final NodeRef staff = nodeService.getPrimaryParent(employeeLink).getParentRef();
			final NodeRef employee = nodeAssocRef.getTargetRef();

			if (orgstructureService.isStaffList(parent)) { // -> запись в БЖ
				// получаем инициатора
				final String initiator = authService.getCurrentUserName();

				String defaultDescription = "Сотрудник #mainobject освобожден от должности \"#object1\" в подразделении #object2";
				NodeRef position = orgstructureService.getPositionByStaff(parent);
				NodeRef unit = orgstructureService.getUnitByStaff(parent);
				List<String> objects = new ArrayList<String>(2);
				objects.add(position != null ? position.toString() : "");
				objects.add(unit != null ? unit.toString() : "");

				businessJournalService.log(initiator, employee, EventCategory.RELEASE_JOB_POSITION, defaultDescription, objects);

				if ((Boolean) nodeService.getProperty(staff, OrgstructureBean.PROP_STAFF_LIST_IS_BOSS)) {
					// Назначение на должность
					defaultDescription = "Сотрудник #mainobject снят с руководящей позиции в подразделении #object1";
					objects = new ArrayList<String>(1);
					objects.add(unit != null ? unit.toString() : "");
					businessJournalService.log(initiator, employee, EventCategory.RELEASE_BOSS_POSITION, defaultDescription, objects);
				}
				notifyEmploeeRemoveDP(employee, position);
			} else if (orgstructureService.isWorkForce(parent)) {
				NodeRef role = orgstructureService.getRoleByWorkForce(parent);
				notifyEmploeeRemoveBR(employee, role);
			}
		} catch (Exception e) {
			logger.error( "Exception at association post processing onDeleteAssociation:", e);
		}
	}

}
