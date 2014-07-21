package ru.it.lecm.orgstructure.policies;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.LecmBaseException;
import ru.it.lecm.base.beans.LecmBasePropertiesService;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 * @author dbashmakov
 *         Date: 07.02.13
 *         Time: 09:37
 */
public class OrgstructureWorkGroupPolicy
		extends SecurityJournalizedPolicyBase
		implements NodeServicePolicies.OnCreateNodePolicy
					, NodeServicePolicies.OnUpdatePropertiesPolicy
					, NodeServicePolicies.OnDeleteNodePolicy
{

	private LecmBasePropertiesService propertiesService;

	public void setPropertiesService(LecmBasePropertiesService propertiesService) {
        this.propertiesService = propertiesService;
    }

	@Override
	public void init() {
		super.init();
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				OrgstructureBean.TYPE_WORK_GROUP, new JavaBehaviour(this, "onCreateNode", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
				OrgstructureBean.TYPE_WORK_GROUP, new JavaBehaviour(this, "onUpdateProperties", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnDeleteNodePolicy.QNAME,
				OrgstructureBean.TYPE_WORK_GROUP, new JavaBehaviour(this, "onDeleteNode"));
	}

	@Override
	public void onCreateNode(ChildAssociationRef childAssocRef) {
		try {
            Object editorEnabled = propertiesService.getProperty("ru.it.lecm.properties.orgstructure.editor.enabled");
            boolean enabled;
            if (editorEnabled == null) {
                enabled = true;
            } else {
                enabled = Boolean.valueOf((String) editorEnabled);
            }
			if (enabled) {
				NodeRef group = childAssocRef.getChildRef();
				businessJournalService.log(group, EventCategory.ADD, "#initiator добавил(а) новую рабочую группу #mainobject");

				notifyChangedWG(group);
			}
		} catch (LecmBaseException e) {
            throw new IllegalStateException("Cannot read orgstructure properties");
        }
	}

	@Override
	public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		final Boolean prevActive = (Boolean) before.get(BaseBean.IS_ACTIVE);
		final Boolean curActive = (Boolean) after.get(BaseBean.IS_ACTIVE);
		final boolean changed = !PolicyUtils.safeEquals(prevActive, curActive);

		if (before.size() == after.size() && curActive) {
			businessJournalService.log(nodeRef, EventCategory.EDIT, "#initiator внес(ла) изменения в сведения о Рабочей группе #mainobject");
		}

		if (changed && !curActive) { // бьыли изменения во флаге и группа помечена как неактивное
			businessJournalService.log(nodeRef, EventCategory.DELETE, "#initiator удалил(а) сведения о Рабочей группе #mainobject");
		}
	}

	@Override
	public void onDeleteNode(ChildAssociationRef childAssocRef, boolean isNodeArchived) {
		try {
            Object editorEnabled = propertiesService.getProperty("ru.it.lecm.properties.orgstructure.editor.enabled");
            boolean enabled;
            if (editorEnabled == null) {
                enabled = true;
            } else {
                enabled = Boolean.valueOf((String) editorEnabled);
            }
			if (enabled) {
				NodeRef groupRef = childAssocRef.getChildRef();
				businessJournalService.log(groupRef, EventCategory.ADD, "#initiator удалил(а) рабочую группу #mainobject");

				notifyDeleteWG(groupRef);
			}
		} catch (LecmBaseException e) {
            throw new IllegalStateException("Cannot read orgstructure properties");
        }
	}
}
