package ru.it.lecm.orgstructure.policies;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.LecmBaseException;
import ru.it.lecm.base.beans.LecmBasePropertiesService;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.io.Serializable;
import java.util.Map;

/**
 * @author dbashmakov
 *         Date: 06.02.13
 *         Time: 13:46
 */
public class OrgstructureStaffPositionPolicy
		extends SecurityJournalizedPolicyBase
{
    private LecmBasePropertiesService propertiesService;

    public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

    public void setPropertiesService(LecmBasePropertiesService propertiesService) {
        this.propertiesService = propertiesService;
    }

    public final void init() {
		super.init();

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				OrgstructureBean.TYPE_STAFF_POSITION, new JavaBehaviour(this, "onCreateStaffPosLog", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
				OrgstructureBean.TYPE_STAFF_POSITION, new JavaBehaviour(this, "onUpdateStaffPosLog", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
//		policyComponent.bindClassBehaviour(NodeServicePolicies.OnDeleteNodePolicy.QNAME,
//				OrgstructureBean.TYPE_STAFF_POSITION, new JavaBehaviour(this, "onDeleteStaffPosLog", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
	}

	public void onCreateStaffPosLog(ChildAssociationRef childAssocRef) {
        try {
            Object editorEnabled = propertiesService.getProperty("ru.it.lecm.properties.orgstructure.staff.editor.enabled");
            boolean enabled;
            if (editorEnabled == null) {
                enabled = true;
            } else {
                enabled = Boolean.valueOf((String) editorEnabled);
            }

            if (enabled) {
                final NodeRef staffPos = childAssocRef.getChildRef();
                businessJournalService.log(staffPos, EventCategory.ADD, "#initiator добавил(а) новый элемент в справочник «Должностные позиции» -  #mainobject");
            }
        } catch (LecmBaseException e) {
            throw new IllegalStateException("Cannot read orgstructure properties");
        }
    }

	public void onUpdateStaffPosLog(NodeRef staffPos, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		final Boolean prevActive = (Boolean) before.get(BaseBean.IS_ACTIVE);
		final Boolean curActive = (Boolean) after.get(BaseBean.IS_ACTIVE);
		final boolean changed = !PolicyUtils.safeEquals(prevActive, curActive);

		if (before.size() == after.size() && !changed) {
			businessJournalService.log(staffPos, EventCategory.EDIT, "#initiator внес(ла) изменения в сведения о должностной позиции #mainobject");
		}

		if (changed && !curActive) { // бьыли изменения во флаге и подразделение помечено как неактивное
			businessJournalService.log(staffPos, EventCategory.DELETE, "#initiator удалил(а) сведения о должностной позиции #mainobject");
		}
	}

}
