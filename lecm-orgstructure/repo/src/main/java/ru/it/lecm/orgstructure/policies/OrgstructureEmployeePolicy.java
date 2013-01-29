package ru.it.lecm.orgstructure.policies;

import java.io.Serializable;
import java.util.HashMap;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.Types;
import ru.it.lecm.security.events.IOrgStructureNotifiers;

/**
 * User: mShafeev
 * Date: 14.12.12
 * Time: 11:33
 */
public class OrgstructureEmployeePolicy
	extends BaseBean
	implements NodeServicePolicies.OnCreateNodePolicy
				, NodeServicePolicies.OnCreateAssociationPolicy
				, NodeServicePolicies.OnDeleteAssociationPolicy
{
	final static protected Logger logger = LoggerFactory.getLogger (OrgstructureEmployeePolicy.class);

	private ServiceRegistry serviceRegistry;
	private PolicyComponent policyComponent;

	private OrgstructureBean orgstructureService;
	private IOrgStructureNotifiers sgNotifier;
	private BusinessJournalService businessJournalService;

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

		PropertyCheck.mandatory(this, "nodeService", nodeService);
		PropertyCheck.mandatory(this, "sgNotifier", sgNotifier);

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				OrgstructureBean.TYPE_EMPLOYEE, new JavaBehaviour(this, "onCreateNode", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				OrgstructureBean.TYPE_EMPLOYEE, OrgstructureBean.ASSOC_EMPLOYEE_PERSON,
				new JavaBehaviour(this, "onCreateAssociation"));
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				OrgstructureBean.TYPE_EMPLOYEE, OrgstructureBean.ASSOC_EMPLOYEE_PERSON,
				new JavaBehaviour(this, "onDeleteAssociation"));
	}

	@Override
	public void onCreateNode(ChildAssociationRef childAssocRef) {
		NodeRef node = childAssocRef.getChildRef();
		NodeService nodeService = serviceRegistry.getNodeService();
		// Получаем папку где сохраняются персональныен данные
		NodeRef personalDirectoryRef = orgstructureService.getPersonalDataDirectory();
		// Создаем пустые персональные данные
		ChildAssociationRef personalDataRef = nodeService.createNode(personalDirectoryRef, ContentModel.ASSOC_CONTAINS,
				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, GUID.generate()),
				OrgstructureBean.TYPE_PERSONAL_DATA, new HashMap<QName, Serializable>());
		// Создаем ассоциацию сотруднику на персональные данные
		nodeService.createAssociation(node, personalDataRef.getChildRef(), OrgstructureBean.ASSOC_EMPLOYEE_PERSON_DATA);

		try {
			businessJournalService.log(serviceRegistry.getAuthenticationService().getCurrentUserName(),
					node, "Добавление", "Добавлен новый сотрудник #mainobject", null);
		} catch (Exception e) {
			logger.error("Не удалось создать запись бизнес-журнала", e);
		}

		// сообщить 1) создание Сотрудника 2) связывание Сотрудника с Person/User.
		notifyEmploeeTie(node);
	}

	@Override
	public void onCreateAssociation(AssociationRef nodeAssocRef) {
		final NodeRef employee = nodeAssocRef.getSourceRef();
		notifyEmploeeTie(employee);
	}

	/**
	 * Нотификация о связывании Сотрудника и пользователя Альфреско.
	 * @param employee
	 */
	private void notifyEmploeeTie(NodeRef employee) {
		final String loginName = orgstructureService.getEmployeeLogin(employee);
		sgNotifier.orgNodeCreated( Types.SGKind.SG_ME.getSGPos( employee.getId(), loginName));
		sgNotifier.orgEmployeeTie( employee.getId(), loginName);
	}

	/**
	 * Нотификация об отвязывании Сотрудника и пользователя Альфреско.
	 * @param employee
	 */
	private void notifyEmploeeDown(NodeRef employee) {
		final String loginName = orgstructureService.getEmployeeLogin(employee);
		sgNotifier.orgNodeDeactivated( Types.SGKind.SG_ME.getSGPos( employee.getId(), loginName));
	}

	@Override
	public void onDeleteAssociation(AssociationRef nodeAssocRef) {
		final NodeRef employee = nodeAssocRef.getSourceRef();
		notifyEmploeeDown(employee);
	}
}
