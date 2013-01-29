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
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.Types;
import ru.it.lecm.security.events.IOrgStructureNotifiers;

/**
 * @author dbashmakov
 *         Date: 10.01.13
 *         Time: 16:21
 */
public class OrgstructureEmployeeLinkPolicy
	extends BaseBean
	implements 
		  NodeServicePolicies.OnCreateNodePolicy
		, NodeServicePolicies.OnCreateAssociationPolicy
		, NodeServicePolicies.OnDeleteAssociationPolicy
{
	final static protected Logger logger = LoggerFactory.getLogger (OrgstructureEmployeeLinkPolicy.class);

	private ServiceRegistry serviceRegistry;
	private PolicyComponent policyComponent;
	private OrgstructureBean orgstructureService;

	private BusinessJournalService businessJournalService;
	private IOrgStructureNotifiers sgNotifier;

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setSgNotifier(IOrgStructureNotifiers sgNotifier) {
		this.sgNotifier = sgNotifier;
	}

	public final void init() {
		PropertyCheck.mandatory(this, "serviceRegistry", serviceRegistry);
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);
		PropertyCheck.mandatory(this, "orgstructureService", orgstructureService);
		PropertyCheck.mandatory(this, "businessJournalService", businessJournalService);

		PropertyCheck.mandatory(this, "nodeService", nodeService);
		PropertyCheck.mandatory(this, "sgNotifier", sgNotifier);

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
			AuthenticationService authService = serviceRegistry.getAuthenticationService();
			String initiator = authService.getCurrentUserName();

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
				businessJournalService.log(initiator, employee, BusinessJournalService.EventCategories.TAKE_JOB_POSITION.toString(), defaultDescription, objects);

				if ((Boolean) nodeService.getProperty(staff, OrgstructureBean.PROP_STAFF_LIST_IS_BOSS)) {
					// Назначение на должность
					defaultDescription = "Сотрудник #mainobject назначен руководителем в подразделении #object1";
					objects = new ArrayList<String>(1);
					objects.add(unit != null ? unit.toString() : "");
					businessJournalService.log(initiator, employee, BusinessJournalService.EventCategories.TAKE_BOSS_POSITION.toString(), defaultDescription, objects);
				}
				//назначение
				notifyEmploeeSetDP(employee, position);
			} else {
				NodeRef role = orgstructureService.getRoleByWorkForce(staff);
				notifyEmploeeSetBR(employee, role);
			}
		} catch (Exception e) {
			logger.error("", e);
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
				AuthenticationService authService = serviceRegistry.getAuthenticationService();
				String initiator = authService.getCurrentUserName();

				String defaultDescription = "Сотрудник #mainobject освобожден от должности \"#object1\" в подразделении #object2";
				NodeRef position = orgstructureService.getPositionByStaff(parent);
				NodeRef unit = orgstructureService.getUnitByStaff(parent);
				List<String> objects = new ArrayList<String>(2);
				objects.add(position != null ? position.toString() : "");
				objects.add(unit != null ? unit.toString() : "");

				businessJournalService.log(initiator, employee, BusinessJournalService.EventCategories.RELEASE_JOB_POSITION.toString(), defaultDescription, objects);

				if ((Boolean) nodeService.getProperty(staff, OrgstructureBean.PROP_STAFF_LIST_IS_BOSS)) {
					// Назначение на должность
					defaultDescription = "Сотрудник #mainobject снят с руководящей позиции в подразделении #object1";
					objects = new ArrayList<String>(1);
					objects.add(unit != null ? unit.toString() : "");
					businessJournalService.log(initiator, employee, BusinessJournalService.EventCategories.RELEASE_BOSS_POSITION.toString(), defaultDescription, objects);
				}
				notifyEmploeeRemoveDP(employee, position);
			} else if (orgstructureService.isWorkForce(parent)) {
				NodeRef role = orgstructureService.getRoleByWorkForce(parent);
				notifyEmploeeRemoveBR(employee, role);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	/**
	 * Назначение БР для Сотрудника.
	 * @param employee
	 * @param brole
	 */
	private void notifyEmploeeSetBR(NodeRef employee, NodeRef brole) {
		final String loginName = orgstructureService.getEmployeeLogin(employee);
		final String broleCode = (String) nodeService.getProperty(brole, OrgstructureBean.PROP_BUSINESS_ROLE_IDENTIFIER);
		this.sgNotifier.orgBRAssigned( broleCode, Types.SGKind.SG_ME.getSGPos( employee.getId(), loginName)); 
	}

	/**
	 * Убрать БР у Сотрудника
	 * @param employee
	 * @param brole
	 */
	private void notifyEmploeeRemoveBR(NodeRef employee, NodeRef brole) {
		final String loginName = orgstructureService.getEmployeeLogin(employee);
		// использование специального значения более "человечно" чем brole.getId(), и переносимо между разными базами Альфреско
		final Object broleCode = nodeService.getProperty(brole, OrgstructureBean.PROP_BUSINESS_ROLE_IDENTIFIER);

		this.sgNotifier.orgBRRemoved( broleCode.toString(), Types.SGKind.SG_ME.getSGPos( employee.getId(), loginName));
	}


	/**
	 * Назначение DP для Сотрудника.
	 * @param employee узел типа "lecm-orgstr:employee-link"
	 * @param dpid узел типа "lecm-orgstr:position"
	 */
	private void notifyEmploeeSetDP(NodeRef employee, NodeRef dpid) {
		final String loginName = orgstructureService.getEmployeeLogin(employee);

		// использование специального значения более "человечно" чем dpid.getId(), и переносимо между разными базами Альфреско
		final String dpIdName = (String) nodeService.getProperty( dpid, OrgstructureBean.PROP_STAFF_POSITION_CODE);

		this.sgNotifier.sgInclude( Types.SGKind.SG_ME.getSGPos(employee.getId(), loginName), Types.SGKind.getSGDeputyPosition( dpIdName, employee.getId(), loginName));
	}

	/**
	 * Убрать БР у Сотрудника
	 * @param employee
	 */
	private void notifyEmploeeRemoveDP(NodeRef employee, NodeRef dpid) {
		final String loginName = orgstructureService.getEmployeeLogin(employee);

		// использование специального значения более "человечно" чем dpid.getId(), и переносимо между разными базами Альфреско
		final String dpIdName = (String) nodeService.getProperty( dpid, OrgstructureBean.PROP_STAFF_POSITION_CODE);

		this.sgNotifier.sgExclude( Types.SGKind.SG_ME.getSGPos(employee.getId(), loginName), Types.SGKind.getSGDeputyPosition( dpIdName, employee.getId(), loginName));
	}

}