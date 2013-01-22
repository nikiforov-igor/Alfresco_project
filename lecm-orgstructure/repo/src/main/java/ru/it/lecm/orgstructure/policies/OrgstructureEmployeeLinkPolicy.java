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
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
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

	public IOrgStructureNotifiers getSgNotifier() {
		return sgNotifier;
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
	}

	@Override
	public void onCreateAssociation(AssociationRef nodeAssocRef) {
		try {
			// NodeService nodeService = serviceRegistry.getNodeService();

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
				notifyEmploeeSetDP(employee, /*DP*/ object1);
			} else if (orgstructureService.isWorkForce(parent)) {
				eventCategory = "Назначение на роль";
				description = "Сотрудник #mainobject назначен на роль #object1 в рабочей группе #object2";
				object1 = orgstructureService.getRoleByWorkForce(parent);
				object2 = orgstructureService.getWorkGroupByWorkForce(parent);
				notifyEmploeeSetBR(employee, /*brole*/object1);
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
		final NodeService nodeService = super.nodeService; // serviceRegistry.getNodeService();

		NodeRef employeeLink = childAssocRef.getChildRef();
		NodeRef parent = childAssocRef.getParentRef();

		nodeService.createAssociation(parent, employeeLink, OrgstructureBean.ASSOC_ELEMENT_MEMBER_EMPLOYEE);
	}

	@Override
	public void onDeleteAssociation(AssociationRef nodeAssocRef) {
		try {
			// NodeService nodeService = serviceRegistry.getNodeService();

			// генерируем запись в БЖ
			// получаем инициатора
			AuthenticationService authService = serviceRegistry.getAuthenticationService();
			String initiator = authService.getCurrentUserName();

			final NodeRef employeeLink = nodeAssocRef.getSourceRef();
			final NodeRef parent = nodeService.getPrimaryParent(employeeLink).getParentRef();

			// получаем основной объект - сотрудник
			final NodeRef employee = nodeAssocRef.getTargetRef();

			// категория события
			String eventCategory = null;
			// дефолтное описание события (если не будет найдено в справочнике)
			String description = null;
			// дополнительные объекты - 1. должность/роль 2. подразделение/рабочая группа
			NodeRef object1 = null;
			NodeRef object2 = null;
			if (orgstructureService.isStaffList(parent)) {
				eventCategory = "Снятие с должности";
				description = "Сотрудник #mainobject снят с должности #object1 в подразделении #object2";
				object1 = orgstructureService.getPositionByStaff(parent);
				object2 = orgstructureService.getUnitByStaff(parent);
				notifyEmploeeRemoveDP(employee, /*dpId*/ object1);
			} else if (orgstructureService.isWorkForce(parent)) {
				eventCategory = "Отбирание роли";
				description = "Сотрудник #mainobject более не обладает ролью  #object1 в рабочей группе #object2";
				object1 = orgstructureService.getRoleByWorkForce(parent);
				object2 = orgstructureService.getWorkGroupByWorkForce(parent);
				notifyEmploeeRemoveBR(employee, object1);
			}
			List<NodeRef> objects = new ArrayList<NodeRef>(2);
			objects.add(object1);
			objects.add(object2);
			businessJournalService.fire(initiator, employee, eventCategory, description, objects);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// имя (логин) пользователя (cm:person)
	public static final QName PROP_USER_NAME = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "userName");

	// lecm-orgstr:staffPosition
	// название должностной позиции
	public static final QName PROP_DP_NAME = QName.createQName(OrgstructureBean.ORGSTRUCTURE_NAMESPACE_URI, "lecm-orgstr:staffPosition-code");

	String getEmployeeLogin(NodeRef employee) {
		if (employee == null) return null;
		final NodeRef person = orgstructureService.getPersonForEmployee(employee);
		if (person == null) {
			logger.warn( String.format( "Employee '%s' is not linked to system user", employee.toString() ));
			return null;
		}
		final String loginName = ""+ nodeService.getProperty( person, PROP_USER_NAME);
		return loginName;
	}

	/**
	 * Назначение БР для Сотрудника.
	 * @param employee
	 * @param brole
	 */
	private void notifyEmploeeSetBR(NodeRef employee, NodeRef brole) {
		final String loginName = getEmployeeLogin(employee);
		if (loginName == null) return;
		final String broleCode = ""+ nodeService.getProperty(brole, OrgstructureBean.PROP_BUSINESS_ROLE_IDENTIFIER);
		this.sgNotifier.orgBRAssigned( broleCode, Types.SGKind.SG_ME.getSGPos(loginName)); 
	}

	/**
	 * Убрать БР у Сотрудника
	 * @param employee
	 * @param brole
	 */
	private void notifyEmploeeRemoveBR(NodeRef employee, NodeRef brole) {
		final String loginName = getEmployeeLogin(employee);
		if (loginName == null) return;

		// использование специального значения более "человечно" чем brole.getId(), и переносимо между разными базами Альфреско
		final Object broleCode = nodeService.getProperty(brole, OrgstructureBean.PROP_BUSINESS_ROLE_IDENTIFIER);

		this.sgNotifier.orgBRRemoved( broleCode.toString(), Types.SGKind.SG_ME.getSGPos(loginName.toString())); 
	}


	/**
	 * Назначение DP для Сотрудника.
	 * @param employee узел типа "lecm-orgstr:employee-link"
	 * @param dpid узел типа "lecm-orgstr:position"
	 */
	private void notifyEmploeeSetDP(NodeRef employee, NodeRef dpid) {
		final String loginName = getEmployeeLogin(employee);
		if (loginName == null) return;

		// использование специального значения более "человечно" чем dpid.getId(), и переносимо между разными базами Альфреско
		final String dpIdName = ""+ nodeService.getProperty( dpid, PROP_DP_NAME);

		this.sgNotifier.sgInclude( Types.SGKind.SG_ME.getSGPos(loginName), Types.SGKind.getSGDeputyPosition( dpIdName, loginName));
	}

	/**
	 * Убрать БР у Сотрудника
	 * @param employee
	 * @param brole
	 */
	private void notifyEmploeeRemoveDP(NodeRef employee, NodeRef dpid) {
		final String loginName = getEmployeeLogin(employee);
		if (loginName == null) return;

		// использование специального значения более "человечно" чем dpid.getId(), и переносимо между разными базами Альфреско
		final String dpIdName = ""+ nodeService.getProperty( dpid, PROP_DP_NAME);

		this.sgNotifier.sgExclude( Types.SGKind.SG_ME.getSGPos(loginName), Types.SGKind.getSGDeputyPosition( dpIdName, loginName));
	}

}