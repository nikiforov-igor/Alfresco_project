package ru.it.lecm.orgstructure.policies;

import java.io.Serializable;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.orgstructure.beans.OrgstructureAspectsModel;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.alfresco.model.ContentModel;

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
		, NodeServicePolicies.BeforeDeleteNodePolicy,
        NodeServicePolicies.OnDeleteNodePolicy{

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
/*        policyComponent.bindClassBehaviour(NodeServicePolicies.OnDeleteNodePolicy.QNAME,
				OrgstructureBean.TYPE_EMPLOYEE_LINK, new JavaBehaviour(this, "onDeleteNode"));*/
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
            NodeRef unitContractor = orgstructureService.getOrganization(unit);
            if (unitContractor != null) {
                if (!nodeService.hasAspect(employee, OrgstructureAspectsModel.ASPECT_HAS_LINKED_ORGANIZATION)) {
                    // Включаем сотрудника в Организацию
                    nodeService.addAspect(employee, OrgstructureAspectsModel.ASPECT_HAS_LINKED_ORGANIZATION, null);
                    nodeService.createAssociation(employee, unitContractor, OrgstructureAspectsModel.ASSOC_LINKED_ORGANIZATION);
                } else {
                    // проверяем соответствие организаций
                    NodeRef employeeContractor = orgstructureService.getEmployeeOrganization(employee);
                    if (!employeeContractor.equals(unitContractor)) {
                        throw new IllegalStateException("Невозможно добавить сотрудника больше чем в одну организацию!");
                    }
                }
            }
            try {
                NodeRef position = orgstructureService.getPositionByStaff(staff);
				if (position != null) {
					String positionName = (String)nodeService.getProperty(position, ContentModel.PROP_NAME);

					Serializable ePositions = nodeService.getProperty(employee, OrgstructureBean.PROP_EMPLOYEE_POSITIONS);
					HashSet<String> employeePositions = (ePositions != null) ? new HashSet<>((Collection<String>)ePositions) : new HashSet<String>();
					employeePositions.add(positionName);
					nodeService.setProperty(employee, OrgstructureBean.PROP_EMPLOYEE_POSITIONS, employeePositions);
				}

                String defaultDescription = "#initiator внес(ла) сведения о назначении Сотрудника #mainobject на должность #object1 в подразделение #object2";
                List<String> objects = new ArrayList<>(2);
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
				//notifyEmploeeSetBR(employee, role);
				notifyEmployeeSetWG(employee, role, group);
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
	public void beforeDeleteNode(NodeRef employeeLink) {
		try {
			final NodeRef parent = nodeService.getPrimaryParent(employeeLink).getParentRef();
			final NodeRef employee = orgstructureService.getEmployeeByLink(employeeLink);

			if (orgstructureService.isStaffList(parent)) {
                if (!hasStaffWithContractor(employee)) {
                    // удаляется последняя позиция -> сценарий "Исключение сотрудника из организации"
                    if (nodeService.hasAspect(employee, OrgstructureAspectsModel.ASPECT_HAS_LINKED_ORGANIZATION)) {
                        NodeRef organization = orgstructureService.getEmployeeOrganization(employee);
                        nodeService.removeAssociation(employee, organization, OrgstructureAspectsModel.ASSOC_LINKED_ORGANIZATION);
                        nodeService.removeAspect(employee, OrgstructureAspectsModel.ASPECT_HAS_LINKED_ORGANIZATION);
                    }
                }

				NodeRef position = orgstructureService.getPositionByStaff(parent);
				NodeRef unit = orgstructureService.getUnitByStaff(parent);

				if (position != null) {
					String positionName = (String)nodeService.getProperty(position, ContentModel.PROP_NAME);

					Serializable ePositions = nodeService.getProperty(employee, OrgstructureBean.PROP_EMPLOYEE_POSITIONS);
					if (ePositions != null) {
						HashSet<String> employeePositions = new HashSet<String>((Collection<String>)ePositions);
						employeePositions.remove(positionName);
						nodeService.setProperty(employee, OrgstructureBean.PROP_EMPLOYEE_POSITIONS, employeePositions);
					}
				}

                // -> запись в БЖ
                String defaultDescription = "#initiator внес(ла) сведения о снятии Сотрудника #mainobject с должности #object1 в подразделении #object2";
				List<String> objects = new ArrayList<>(2);
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

				notifyEmployeeRemoveWG(employee, role, group);
			}
		} catch (Exception e) {
			logger.error("Exception at association post processing onDeleteAssociation:", e);
		}
	}

    private boolean hasStaffWithContractor(NodeRef employee) {
        List<NodeRef> staffs = orgstructureService.getEmployeeStaffs(employee);
        if (staffs.size() > 0) {
            NodeRef rootUnit = orgstructureService.getRootUnit();
            for (NodeRef staff : staffs) {
                NodeRef unit = orgstructureService.getUnitByStaff(staff);
                if (!unit.equals(rootUnit) && nodeService.hasAspect(unit, OrgstructureAspectsModel.ASPECT_HAS_LINKED_ORGANIZATION)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onDeleteNode(ChildAssociationRef childAssocRef, boolean isNodeArchived) {
        /*final NodeRef parent = childAssocRef.getParentRef();
        final NodeRef employee = orgstructureService.getEmployeeByLink(childAssocRef.getChildRef());
        if (orgstructureService.isStaffList(parent)) {
            if (!hasStaffWithContractor(employee)) {
                // удаляется последняя позиция -> сценарий "Исключение сотрудника из организации"
                if (nodeService.hasAspect(employee, OrgstructureAspectsModel.ASPECT_HAS_LINKED_ORGANIZATION)) {
                    nodeService.removeAspect(employee, OrgstructureAspectsModel.ASPECT_HAS_LINKED_ORGANIZATION);
                }
            }
        }*/
    }
}
