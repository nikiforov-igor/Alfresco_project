package ru.it.lecm.orgstructure.policies;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;

import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.orgstructure.beans.OrgstructureBeanImpl;

/**
 * @author dbashmakov
 *         Date: 28.01.13
 *         Time: 10:37
 */
public class OrgstructureUnitPolicy
		extends SecurityJournalizedPolicyBase
		implements NodeServicePolicies.OnCreateNodePolicy
				, NodeServicePolicies.OnUpdatePropertiesPolicy
				, NodeServicePolicies.OnDeleteNodePolicy
{

	@Override
	public void init() {
		PropertyCheck.mandatory(this, CHKNAME_AUTH_SERVICE, CHKNAME_AUTH_SERVICE);

		super.init();

		// TYPE_ORGANIZATION_UNIT : "lecm-orgstr:organization-unit"
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				OrgstructureBean.TYPE_ORGANIZATION_UNIT, new JavaBehaviour(this, "onCreateNode", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
				OrgstructureBean.TYPE_ORGANIZATION_UNIT, new JavaBehaviour(this, "onUpdateProperties", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnDeleteNodePolicy.QNAME,
				OrgstructureBean.TYPE_ORGANIZATION_UNIT, new JavaBehaviour(this, "onDeleteNode"));
	}

	@Override
	public void onCreateNode(ChildAssociationRef childAssocRef) {
		NodeRef unit = childAssocRef.getChildRef();
		NodeRef parent = orgstructureService.getParentUnit(unit);

		final List<String> objects = new ArrayList<String>(1);
		if (parent != null) {
			objects.add(parent.toString());
		} else { // корневое подразделение - берем Организацию
			objects.add(orgstructureService.getOrganizationRootRef().toString());
		}

		final String initiator = authService.getCurrentUserName();
		try {
			businessJournalService.log(initiator, unit, BusinessJournalService.EventCategories.ADD.toString(), "Созданo новое подразделение #mainobject в подразделении #object1", objects);
		} catch (Exception e) {
			logger.error("Не удалось создать запись бизнес-журнала", e);
		}

		// оповещение securityService по Должностной Позиции ...
		notifyChangedOU( unit, parent);
	}

	@Override
	public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		final Boolean prevActive = (Boolean) before.get(OrgstructureBeanImpl.IS_ACTIVE);
		final Boolean curActive = (Boolean) after.get(OrgstructureBeanImpl.IS_ACTIVE);

		final boolean changed = !PolicyUtils.safeEquals(prevActive, curActive);

		if (changed && !curActive) { // бьыли изменения во флаге
			final NodeRef parent = orgstructureService.getParentUnit(nodeRef);

			final List<String> objects = new ArrayList<String>(1);
			if (parent != null) {
				objects.add(parent.toString());
			} else { // корневое подразделение - берем Организацию
				objects.add(orgstructureService.getOrganizationRootRef().toString());
			}

			final String initiator = authService.getCurrentUserName();
			try {
				businessJournalService.log(initiator, nodeRef, BusinessJournalService.EventCategories.DELETE.toString(), "Подразделение \"#mainobject\" в подразделении #object1 расформировано", objects);
			} catch (Exception e) {
				logger.error("Не удалось создать запись бизнес-журнала", e);
			}
		}
	}

	@Override
	public void onDeleteNode(ChildAssociationRef childAssocRef, boolean isNodeArchived) {
		final NodeRef nodeOU = childAssocRef.getChildRef();
		final NodeRef parentOU = childAssocRef.getParentRef();
		notifyDeleteOU( nodeOU, parentOU);
	}

}
