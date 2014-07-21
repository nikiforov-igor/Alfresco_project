package ru.it.lecm.orgstructure.policies;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.orgstructure.beans.OrgstructureAspectsModel;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.util.ArrayList;
import java.util.List;

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
		, NodeServicePolicies.BeforeDeleteNodePolicy {

	@Override
	public void init() {
		super.init();

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				OrgstructureBean.TYPE_EMPLOYEE_LINK, new JavaBehaviour(this, "onCreateNode"));
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				OrgstructureBean.TYPE_EMPLOYEE_LINK, OrgstructureBean.ASSOC_EMPLOYEE_LINK_EMPLOYEE,
				new JavaBehaviour(this, "onCreateAssociation"));
		policyComponent.bindClassBehaviour(NodeServicePolicies.BeforeDeleteNodePolicy.QNAME,
				OrgstructureBean.TYPE_EMPLOYEE_LINK, new JavaBehaviour(this, "beforeDeleteNode"));
	}

	@Override
    public void onCreateAssociation(AssociationRef nodeAssocRef) {
        NodeRef employeeLink = nodeAssocRef.getSourceRef();
        NodeRef staff = nodeService.getPrimaryParent(employeeLink).getParentRef();

        // получаем основной объект - сотрудник
        NodeRef employee = nodeAssocRef.getTargetRef();

        if (orgstructureService.isStaffList(staff)) {
            // Назначение на должность
            NodeRef unit = orgstructureService.getUnitByStaff(staff);
            if (nodeService.hasAspect(unit, OrgstructureAspectsModel.ASPECT_HAS_LINKED_CONTRACTOR)
                    && nodeService.getTargetAssocs(unit, OrgstructureAspectsModel.ASSOC_LINKED_CONTRACTOR).size() > 0) {
                NodeRef unitContractor = nodeService.getTargetAssocs(unit, OrgstructureAspectsModel.ASSOC_LINKED_CONTRACTOR).get(0).getTargetRef();

                if (!nodeService.hasAspect(employee, OrgstructureAspectsModel.ASPECT_HAS_LINKED_CONTRACTOR)) {
                    // Включаем сотрудника в Организацию
                    nodeService.addAspect(employee, OrgstructureAspectsModel.ASPECT_HAS_LINKED_CONTRACTOR, null);
                    nodeService.createAssociation(employee, unitContractor, OrgstructureAspectsModel.ASSOC_LINKED_CONTRACTOR);
                } else {
                    // проверяем соответствие организаций
                    NodeRef employeeContractor = nodeService.getTargetAssocs(employee, OrgstructureAspectsModel.ASSOC_LINKED_CONTRACTOR).get(0).getTargetRef();
                    if (!employeeContractor.equals(unitContractor)) {
                        throw new IllegalStateException("Невозможно добавить сотрудника больше чем в одну организацию!");
                    }
                }
            }
            try {
                String defaultDescription = "#initiator внес(ла) сведения о назначении Сотрудника #mainobject на должность #object1 в подразделение #object2";
                NodeRef position = orgstructureService.getPositionByStaff(staff);
                List<String> objects = new ArrayList<String>(2);
                objects.add(position != null ? position.toString() : "");
                objects.add(unit != null ? unit.toString() : "");
                businessJournalService.log(employee, EventCategory.TAKE_JOB_POSITION, defaultDescription, objects);

                if ((Boolean) nodeService.getProperty(staff, OrgstructureBean.PROP_STAFF_LIST_IS_BOSS)) {
                    // Назначение на руководящую должность
                    defaultDescription = "#initiator внес(ла) сведения о назначении Сотрудника #mainobject руководителем в подразделении #object1";
                    objects = new ArrayList<String>(1);
                    objects.add(unit != null ? unit.toString() : "");
                    businessJournalService.log(employee, EventCategory.TAKE_BOSS_POSITION, defaultDescription, objects);
                }
                // назначение Сотрудника на должность
                notifyEmploeeSetDP(employee, staff);
            } catch (Exception e) {
                logger.error("Exception at association post processing onCreateAssociation:", e);
            }
        } else {
            try {
                // Назначение на роль
                String defaultDescription = "#initiator внес(ла) сведения о назначении Сотрудника #mainobject на роль #object1 в рабочей группе #object2";
                NodeRef role = orgstructureService.getRoleByWorkForce(staff);
                NodeRef group = orgstructureService.getWorkGroupByWorkForce(staff);
                List<String> objects = new ArrayList<String>(2);
                objects.add(role != null ? role.toString() : "");
                objects.add(group != null ? group.toString() : "");
                businessJournalService.log(employee, EventCategory.TAKE_GROUP_ROLE, defaultDescription, objects);
                // уведомление
                notifyEmploeeSetBR(employee, role);
            } catch (Exception e) {
                logger.error("Exception at association post processing onCreateAssociation:", e);
            }
        }
    }

	@Override
	public void onCreateNode(ChildAssociationRef childAssocRef) {
		NodeRef employeeLink = childAssocRef.getChildRef();
		NodeRef parent = childAssocRef.getParentRef();

		nodeService.createAssociation(parent, employeeLink, OrgstructureBean.ASSOC_ELEMENT_MEMBER_EMPLOYEE);
	}

	@Override
	public void beforeDeleteNode(NodeRef nodeRef) {
		try {
			final NodeRef employeeLink = nodeRef;
			final NodeRef parent = nodeService.getPrimaryParent(employeeLink).getParentRef();
			final NodeRef employee = orgstructureService.getEmployeeByLink(employeeLink);

			if (orgstructureService.isStaffList(parent)) { // -> запись в БЖ
				String defaultDescription = "#initiator внес(ла) сведения о снятии Сотрудника #mainobject с должности #object1 в подразделении #object2";
				NodeRef position = orgstructureService.getPositionByStaff(parent);
				NodeRef unit = orgstructureService.getUnitByStaff(parent);
				List<String> objects = new ArrayList<String>(2);
				objects.add(position != null ? position.toString() : "");
				objects.add(unit != null ? unit.toString() : "");

				businessJournalService.log(employee, EventCategory.RELEASE_JOB_POSITION, defaultDescription, objects);

				if ((Boolean) nodeService.getProperty(parent, OrgstructureBean.PROP_STAFF_LIST_IS_BOSS)) {
					// Назначение на должность
					defaultDescription = "#initiator внес(ла) сведения о снятии Сотрудника #mainobject с руководящей должности в подразделении #object1\"";
					objects = new ArrayList<String>(1);
					objects.add(unit != null ? unit.toString() : "");
					businessJournalService.log(employee, EventCategory.RELEASE_BOSS_POSITION, defaultDescription, objects);
				}
				notifyEmploeeRemoveDP(employee, parent);
			} else if (orgstructureService.isWorkForce(parent)) {
				String defaultDescription = "#initiator внес(ла) сведения о снятии Сотрудника #mainobject с роли #object1 в рабочей группе #object2";
				NodeRef role = orgstructureService.getRoleByWorkForce(parent);
				NodeRef group = orgstructureService.getWorkGroupByWorkForce(parent);
				List<String> objects = new ArrayList<String>(2);
				objects.add(role != null ? role.toString() : "");
				objects.add(group != null ? group.toString() : "");

				businessJournalService.log(employee, EventCategory.RELEASE_GROUP_ROLE, defaultDescription, objects);

				notifyEmploeeRemoveBR(employee, role);
			}
		} catch (Exception e) {
			logger.error("Exception at association post processing onDeleteAssociation:", e);
		}
	}
}
