package ru.it.lecm.businessjournal.policies;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;

/**
 * Created with IntelliJ IDEA.
 * User: pkotelnikova
 * Date: 25.01.13
 * Time: 15:44
 * To change this template use File | Settings | File Templates.
 */
public class BusinessJournalAddHistoryToMainObjectPolicy extends BaseBean implements NodeServicePolicies.OnCreateAssociationPolicy {
    private static PolicyComponent policyComponent;

    public void setPolicyComponent(PolicyComponent policyComponent) {
        BusinessJournalAddHistoryToMainObjectPolicy.policyComponent = policyComponent;
    }

    public final void init() {
        PropertyCheck.mandatory(this, "nodeService", nodeService);
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                BusinessJournalService.TYPE_BR_RECORD, BusinessJournalService.ASSOC_BR_RECORD_MAIN_OBJ,
                new JavaBehaviour(this, "onCreateAssociation"));
    }

    /**
     * Добавление объекту аспекта Истории Бизнес-Журнала (если нет)
     */
    @Override
    public void onCreateAssociation(AssociationRef associationRef) {
        NodeRef mainObject = associationRef.getTargetRef();
        boolean hasHistoryAspect = nodeService.hasAspect(mainObject, BusinessJournalService.TYPE_HISTORY);

        if (!hasHistoryAspect) {
            Map<QName, Serializable> aspectValues = new HashMap<QName, Serializable>();
            aspectValues.put(BusinessJournalService.PROP_HISTORY_LIST, "");

            nodeService.addAspect(mainObject, BusinessJournalService.TYPE_HISTORY, aspectValues);
        }
    }

}
