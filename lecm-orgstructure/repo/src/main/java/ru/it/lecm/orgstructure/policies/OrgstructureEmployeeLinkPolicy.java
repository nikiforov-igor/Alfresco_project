package ru.it.lecm.orgstructure.policies;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 * @author dbashmakov
 *         Date: 10.01.13
 *         Time: 16:21
 */
public class OrgstructureEmployeeLinkPolicy implements  NodeServicePolicies.OnCreateNodePolicy, NodeServicePolicies.OnCreateAssociationPolicy {
	private static ServiceRegistry serviceRegistry;
	private static PolicyComponent policyComponent;
	private static OrgstructureBean orgstructureService;
	private static BusinessJournalService businessJournalService;

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		OrgstructureEmployeeLinkPolicy.businessJournalService = businessJournalService;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		OrgstructureEmployeeLinkPolicy.serviceRegistry = serviceRegistry;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		OrgstructureEmployeeLinkPolicy.policyComponent = policyComponent;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		OrgstructureEmployeeLinkPolicy.orgstructureService = orgstructureService;
	}

	public final void init() {
		PropertyCheck.mandatory(this, "serviceRegistry", serviceRegistry);
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);
		PropertyCheck.mandatory(this, "orgstructureService", orgstructureService);
		PropertyCheck.mandatory(this, "businessJournalService", businessJournalService);

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				OrgstructureBean.TYPE_EMPLOYEE_LINK, new JavaBehaviour(this, "onCreateNode"));
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				OrgstructureBean.TYPE_EMPLOYEE_LINK, OrgstructureBean.ASSOC_EMPLOYEE_LINK_EMPLOYEE,
				new JavaBehaviour(this, "onCreateAssociation"));
	}

	@Override
	public void onCreateAssociation(AssociationRef nodeAssocRef) {
		try {
			NodeService nodeService = serviceRegistry.getNodeService();

			NodeRef employeeLink = nodeAssocRef.getSourceRef();
			NodeRef parent = nodeService.getPrimaryParent(employeeLink).getParentRef();

			// генерируем запись в БЖ
			// получаем инициатора
			AuthenticationService authService = serviceRegistry.getAuthenticationService();
			String initiator = authService.getCurrentUserName();

			// получаем основной объект - сотрудник
			NodeRef employee = nodeAssocRef.getTargetRef();

			// категория события
			String eventCategory = null;
			// дефолтное описание события (если не будет найдено в справочнике)
			String description = null;
			// дополнительные объекты - 1. должность/роль 2. подразделение/рабочая группа
			NodeRef object1 = null;
			NodeRef object2 = null;
			if (orgstructureService.isStaffList(parent)) {
				eventCategory = "Назначение на должность";
				description = "Сотрудник #mainobject назначен на должность #object1 в подразделении #object2";
				object1 = orgstructureService.getPositionByStaff(parent);
				object2 = orgstructureService.getUnitByStaff(parent);
			} else if (orgstructureService.isWorkForce(parent)) {
				eventCategory = "Назначение на роль";
				description = "Сотрудник #mainobject назначен на роль #object1 в рабочей группе #object2";
				object1 = orgstructureService.getRoleByWorkForce(parent);
				object2 = orgstructureService.getWorkGroupByWorkForce(parent);
			}
			List<NodeRef> objects = new ArrayList<NodeRef>(2);
			objects.add(object1);
			objects.add(object2);


			businessJournalService.fire(initiator, employee, eventCategory, description, objects);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onCreateNode(ChildAssociationRef childAssocRef) {
		// создаем ассоциацию
		NodeService nodeService = serviceRegistry.getNodeService();

		NodeRef employeeLink = childAssocRef.getChildRef();
		NodeRef parent = childAssocRef.getParentRef();

		nodeService.createAssociation(parent, employeeLink, OrgstructureBean.ASSOC_ELEMENT_MEMBER_EMPLOYEE);
	}
}
