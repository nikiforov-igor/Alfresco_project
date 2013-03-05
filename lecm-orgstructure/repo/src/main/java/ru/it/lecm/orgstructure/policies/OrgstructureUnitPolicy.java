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
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;

import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 * @author dbashmakov
 *         Date: 28.01.13
 *         Time: 10:37
 */
public class OrgstructureUnitPolicy
		extends SecurityJournalizedPolicyBase
		implements NodeServicePolicies.OnCreateNodePolicy
					, NodeServicePolicies.OnDeleteNodePolicy 
{

	final static String CHKNAME_AUTH_SERVICE = "authService";

	private AuthenticationService authService; // optional

	@Override
	public void init() {
		PropertyCheck.mandatory(this, CHKNAME_AUTH_SERVICE, authService);

		super.init();

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				OrgstructureBean.TYPE_ORGANIZATION_UNIT, new JavaBehaviour(this, "onCreateNode"));
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				OrgstructureBean.TYPE_ORGANIZATION_UNIT, new JavaBehaviour(this, "onCreateUnitLog", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
				OrgstructureBean.TYPE_ORGANIZATION_UNIT, new JavaBehaviour(this, "onUpdateUnitLog", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnDeleteNodePolicy.QNAME,
				OrgstructureBean.TYPE_ORGANIZATION_UNIT, new JavaBehaviour(this, "onDeleteNode"));
	}

	public AuthenticationService getAuthService() {
		return authService;
	}

	public void setAuthService(AuthenticationService authService) {
		this.authService = authService;
	}

	@Override
	public void onCreateNode(ChildAssociationRef childAssocRef) {
		NodeRef unit = childAssocRef.getChildRef();
		NodeRef parent = orgstructureService.getParentUnit(unit);

		// оповещение securityService по Департаменту ...
		notifyChangedOU( unit, parent);
	}

	public void onUpdateUnitLog(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		final Boolean prevActive = (Boolean) before.get(BaseBean.IS_ACTIVE);
		final Boolean curActive = (Boolean) after.get(BaseBean.IS_ACTIVE);
		final boolean changed = !PolicyUtils.safeEquals(prevActive, curActive);

		if (before.size() == after.size() && !changed) {
			businessJournalService.log(nodeRef, EventCategory.EDIT, "Сотрудник #initiator внес изменения в сведения о подразделении #mainobject");
		}

		if (changed && !curActive) { // бьыли изменения во флаге и подразделение помечено как неактивное
			businessJournalService.log(nodeRef, EventCategory.DELETE, "Сотрудник #initiator удалил сведения о подразделении #mainobject");
		}

		// отслеживаем котороткое название для SG-обозначений
		{
			final Object oldValue = before.get(PolicyUtils.PROP_ORGUNIT_NAME);
			final Object newValue = after.get(PolicyUtils.PROP_ORGUNIT_NAME);
			final boolean flagChanged = ! PolicyUtils.safeEquals( newValue, oldValue);
			if (flagChanged) {
				logger.debug( String.format( "updating details for OU '%s'\n\t from '%s'\n\t to '%s'", nodeRef, oldValue, newValue));
				notifyNodeCreated( PolicyUtils.makeOrgUnitPos(nodeRef, nodeService));
				/* 
				notifyNodeCreated( PolicyUtils.makeOrgUnitSVPos(nodeRef, nodeService));
				(!) для OUSV явный вызов не потребуется, т.к. notifyOU автоматом создаёт sv-часть
				*/
			}
		}
	}

	@Override
	public void onDeleteNode(ChildAssociationRef childAssocRef, boolean isNodeArchived) {
		final NodeRef nodeOU = childAssocRef.getChildRef();
		final NodeRef parentOU = childAssocRef.getParentRef();
		notifyDeleteOU( nodeOU, parentOU);
	}

	public void onCreateUnitLog(ChildAssociationRef childAssocRef) {
		NodeRef unit = childAssocRef.getChildRef();
		NodeRef parent = orgstructureService.getParentUnit(unit);

		final List<String> objects = new ArrayList<String>(1);
		if (parent != null) {
			objects.add(parent.toString());
		} else { // корневое подразделение - берем Организацию
			objects.add(orgstructureService.getOrganizationRootRef().toString());
		}

		final String initiator = authService.getCurrentUserName();
		businessJournalService.log(initiator, unit, EventCategory.ADD, "Сотрудник #initiator создал новое подразделение #mainobject в подразделении #object1", objects);
	}
}
