package ru.it.lecm.orgstructure.policies;

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
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: mShafeev
 * Date: 14.12.12
 * Time: 11:33
 */
public class OrgstructureEmployeePolicy
	extends SecurityJournalizedPolicyBase
	implements NodeServicePolicies.OnCreateNodePolicy

				, NodeServicePolicies.OnUpdatePropertiesPolicy
				, NodeServicePolicies.OnDeleteNodePolicy

				, NodeServicePolicies.OnCreateAssociationPolicy
				, NodeServicePolicies.OnDeleteAssociationPolicy
{
	@Override
	public final void init() {
		super.init();

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				OrgstructureBean.TYPE_EMPLOYEE, new JavaBehaviour(this, "onCreateNode"));
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				OrgstructureBean.TYPE_EMPLOYEE, new JavaBehaviour(this, "onCreateEmployeeLog", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnDeleteNodePolicy.QNAME,
				OrgstructureBean.TYPE_EMPLOYEE, new JavaBehaviour(this, "onDeleteNode"));
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
				OrgstructureBean.TYPE_EMPLOYEE, new JavaBehaviour(this, "onUpdateProperties"));
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
				OrgstructureBean.TYPE_EMPLOYEE, new JavaBehaviour(this, "onUpdateEmployeeLog", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				OrgstructureBean.TYPE_EMPLOYEE, OrgstructureBean.ASSOC_EMPLOYEE_PERSON,
				new JavaBehaviour(this, "onCreateAssociation"));
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				OrgstructureBean.TYPE_EMPLOYEE, OrgstructureBean.ASSOC_EMPLOYEE_PERSON,
				new JavaBehaviour(this, "onDeleteAssociation"));
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                OrgstructureBean.TYPE_EMPLOYEE, OrgstructureBean.ASSOC_EMPLOYEE_PHOTO,
                new JavaBehaviour(this, "onCreateAvatarAssociation"));
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				OrgstructureBean.TYPE_EMPLOYEE, OrgstructureBean.ASSOC_EMPLOYEE_PHOTO,
				new JavaBehaviour(this, "onDeleteAvatarAssociation"));
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
        // получаем ссылку на аватар
        if (nodeService.getTargetAssocs(employee, OrgstructureBean.ASSOC_EMPLOYEE_PHOTO).size() != 0) {
            NodeRef avatar = nodeService.getTargetAssocs(employee, OrgstructureBean.ASSOC_EMPLOYEE_PHOTO).get(0).getTargetRef();
            // Соответствует ли сотруднику учетная запись?
            NodeRef person = getOrgstructureService().getPersonForEmployee(employee);
            if (person != null) {
                if (nodeService.getTargetAssocs(person, ContentModel.ASSOC_AVATAR).size() != 0) {
                    // сравнить ссылки на аватарки
                    if (!nodeService.getTargetAssocs(person, ContentModel.ASSOC_AVATAR).get(0).getTargetRef().equals(avatar)) {
                        nodeService.removeAssociation(person, nodeService.getTargetAssocs(person, ContentModel.ASSOC_AVATAR).get(0).getTargetRef(), ContentModel.ASSOC_AVATAR);
                        // далее срабатывает метод onDeleteAvatarAssociation и в нем добавляется person ассоциация на новую аватарку
                        // если мы поменяли аватарки у сотрудника
                    }
                } else {
                    // добавляем учетной записи ассоциацию на аватарку
                    nodeService.createAssociation(person, avatar, ContentModel.ASSOC_AVATAR);
                }
            }
        }
    }

    public void onCreateAvatarAssociation(AssociationRef nodeAssocRef) {
        final NodeRef employee = nodeAssocRef.getSourceRef();
        // Соответствует ли сотруднику учетная запись?
        NodeRef person = getOrgstructureService().getPersonForEmployee(employee);
        if (person != null) {
            // получаем ссылку на аватар
            NodeRef avatar = nodeAssocRef.getTargetRef();
            if (nodeService.getTargetAssocs(person, ContentModel.ASSOC_AVATAR).size() != 0) {
                // сравнить ссылки на аватарки
                if (!nodeService.getTargetAssocs(person, ContentModel.ASSOC_AVATAR).get(0).getTargetRef().equals(avatar)) {
                    nodeService.removeAssociation(person, nodeService.getTargetAssocs(person, ContentModel.ASSOC_AVATAR).get(0).getTargetRef(), ContentModel.ASSOC_AVATAR);
                    // далее срабатывает метод onDeleteAvatarAssociation и в нем добавляется person ассоциация на новую аватарку
                    // если мы поменяли аватарки у сотрудника
                }
            } else {
                // добавляем учетной записи ассоциацию на аватарку
                nodeService.createAssociation(person, avatar, ContentModel.ASSOC_AVATAR);
            }
        }
    }

    public void onDeleteAvatarAssociation(AssociationRef nodeAssocRef) {
        final NodeRef employee = nodeAssocRef.getSourceRef();
        final NodeRef person = getOrgstructureService().getPersonForEmployee(employee);
        if (nodeService.getTargetAssocs(employee, OrgstructureBean.ASSOC_EMPLOYEE_PERSON).size() != 0) {
            if (nodeService.getTargetAssocs(person, ContentModel.ASSOC_AVATAR).size() != 0) {
                nodeService.removeAssociation(person, nodeService.getTargetAssocs(person, ContentModel.ASSOC_AVATAR).get(0).getTargetRef(), ContentModel.ASSOC_AVATAR);
            }

            if (nodeService.getTargetAssocs(employee, OrgstructureBean.ASSOC_EMPLOYEE_PHOTO).size() != 0) {
                nodeService.createAssociation(person, nodeService.getTargetAssocs(employee, OrgstructureBean.ASSOC_EMPLOYEE_PHOTO).get(0).getTargetRef(), ContentModel.ASSOC_AVATAR);
            }
        }

    }

	@Override
    public void onDeleteAssociation(AssociationRef nodeAssocRef) {
        final NodeRef employee = nodeAssocRef.getSourceRef();
        final NodeRef person = nodeAssocRef.getTargetRef();
        notifyEmploeeDown(employee, person);
        // получаем ссылку на аватар
        NodeRef avatar = null;
        List<AssociationRef> avatars = nodeService.getTargetAssocs(employee, OrgstructureBean.ASSOC_EMPLOYEE_PHOTO);
        if (avatars.size() > 0 ) {
            avatar = avatars.get(0).getTargetRef();
        }
        if (avatar != null) {
            if (nodeService.getTargetAssocs(person, ContentModel.ASSOC_AVATAR).size() != 0) {
                // сравнить ссылки на аватарки
                if (!nodeService.getTargetAssocs(person, ContentModel.ASSOC_AVATAR).get(0).getTargetRef().equals(avatar)) {
                    nodeService.removeAssociation(person, nodeService.getTargetAssocs(person, ContentModel.ASSOC_AVATAR).get(0).getTargetRef(), ContentModel.ASSOC_AVATAR);
                    // далее срабатывает метод onDeleteAvatarAssociation и в нем добавляется person ассоциация на новую аватарку
                    // если мы поменяли аватарки у сотрудника
                }
            }
        }
    }

	@Override
	public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		final Boolean nowActive = (Boolean) after.get(IS_ACTIVE);
		final Boolean oldActive = (Boolean) before.get(IS_ACTIVE);
		final boolean changed = !PolicyUtils.safeEquals(oldActive, nowActive);
		if (changed) // произошло переключение активности -> отработать ...
			notifyEmploeeTie(nodeRef, nowActive);
	}

	@Override
	public void onDeleteNode(ChildAssociationRef childAssocRef, boolean isNodeArchived) {
		final NodeRef employee = childAssocRef.getChildRef();
		final NodeRef person = orgstructureService.getPersonForEmployee(employee);
		notifyEmploeeDown(employee, person);
	}

	public void onCreateEmployeeLog(ChildAssociationRef childAssocRef) {
		NodeRef node = childAssocRef.getChildRef();
		businessJournalService.log(node, EventCategory.ADD, "Сотрудник #initiator добавил нового сотрудника - #mainobject");
	}

	public void onUpdateEmployeeLog (NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		final Boolean prevActive = (Boolean) before.get(BaseBean.IS_ACTIVE);
		final Boolean curActive = (Boolean) after.get(BaseBean.IS_ACTIVE);
		final boolean changed = !PolicyUtils.safeEquals(prevActive, curActive);

		if (before.size() == after.size() && !changed) {
			businessJournalService.log(nodeRef, EventCategory.EDIT, "Сотрудник #initiator внес изменения в сведения о сотруднике #mainobject");
		}

		if (changed && !curActive) {
			businessJournalService.log(nodeRef, EventCategory.DELETE, "Сотрудник #initiator удалил сведения о сотруднике #mainobject");
		}
	}

}
