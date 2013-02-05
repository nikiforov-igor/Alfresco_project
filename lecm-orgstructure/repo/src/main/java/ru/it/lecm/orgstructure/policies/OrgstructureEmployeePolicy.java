package ru.it.lecm.orgstructure.policies;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 * User: mShafeev
 * Date: 14.12.12
 * Time: 11:33
 */
public class OrgstructureEmployeePolicy
	extends SecurityJournalizedPolicyBase
	implements NodeServicePolicies.OnCreateNodePolicy
				, NodeServicePolicies.OnUpdatePropertiesPolicy
				// , NodeServicePolicies.OnDeleteNodePolicy
				, NodeServicePolicies.OnCreateAssociationPolicy
				, NodeServicePolicies.OnDeleteAssociationPolicy
{
	@Override
	public final void init() {
		super.init();

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				OrgstructureBean.TYPE_EMPLOYEE, new JavaBehaviour(this, "onCreateNode", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				OrgstructureBean.TYPE_EMPLOYEE, OrgstructureBean.ASSOC_EMPLOYEE_PERSON,
				new JavaBehaviour(this, "onCreateAssociation"));
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				OrgstructureBean.ASSOC_EMPLOYEE_PERSON, OrgstructureBean.ASSOC_EMPLOYEE_LINK_EMPLOYEE,
				new JavaBehaviour(this, "onDeleteAssociation"));
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
				OrgstructureBean.ASSOC_EMPLOYEE_PERSON, OrgstructureBean.ASSOC_EMPLOYEE_LINK_EMPLOYEE,
				new JavaBehaviour(this, "onUpdateProperties"));
	}

	@Override
	public void onCreateNode(ChildAssociationRef childAssocRef) {
		NodeRef node = childAssocRef.getChildRef();
		// Получаем папку где сохраняются персональныен данные
		NodeRef personalDirectoryRef = orgstructureService.getPersonalDataDirectory();
		final Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
		// Создаем пустые персональные данные
		ChildAssociationRef personalDataRef = nodeService.createNode(personalDirectoryRef, ContentModel.ASSOC_CONTAINS,
				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, GUID.generate()),
				OrgstructureBean.TYPE_PERSONAL_DATA, properties);
		// Создаем ассоциацию сотруднику на персональные данные
		nodeService.createAssociation(node, personalDataRef.getChildRef(), OrgstructureBean.ASSOC_EMPLOYEE_PERSON_DATA);

		try {
			businessJournalService.log(getAuthService().getCurrentUserName(),
					node, EventCategory.ADD, "Добавлен новый сотрудник #mainobject", null);
		} catch (Exception e) {
			logger.error("Не удалось создать запись бизнес-журнала", e);
		}
		// сообщить 1) создание Сотрудника 2) связывание Сотрудника с Person/User.
		{
			final NodeRef employee = node;
			notifyEmploeeTie(employee);
		}
	}

	@Override
	public void onCreateAssociation(AssociationRef nodeAssocRef) {
		final NodeRef employee = nodeAssocRef.getSourceRef();
		notifyEmploeeTie(employee);
	}

	@Override
	public void onDeleteAssociation(AssociationRef nodeAssocRef) {
		final NodeRef employee = nodeAssocRef.getSourceRef();
		notifyEmploeeDown(employee);
	}

	@Override
	public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		final Boolean nowActive = (Boolean) after.get(IS_ACTIVE);
		final Boolean oldActive = (Boolean) before.get(IS_ACTIVE);
		final boolean changed = !PolicyUtils.safeEquals(oldActive, nowActive);
		if (changed) // произошло переключение активности -> отработать ...
			notifyEmploeeTie(nodeRef, nowActive);
	}

}
