package ru.it.lecm.businessjournal.policies;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: pkotelnikova
 * Date: 25.01.13
 * Time: 15:44
 * To change this template use File | Settings | File Templates.
 */
public class BusinessJournalAddHistoryToMainObjectPolicy implements NodeServicePolicies.OnCreateAssociationPolicy {
    private static ServiceRegistry serviceRegistry;
    private static PolicyComponent policyComponent;

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        BusinessJournalAddHistoryToMainObjectPolicy.serviceRegistry = serviceRegistry;
    }

    public void setPolicyComponent(PolicyComponent policyComponent) {
        BusinessJournalAddHistoryToMainObjectPolicy.policyComponent = policyComponent;
    }

    public final void init() {
        PropertyCheck.mandatory(this, "serviceRegistry", serviceRegistry);
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
        NodeService nodeService = serviceRegistry.getNodeService();

        NodeRef mainObject = associationRef.getTargetRef();
        boolean hasHistoryAspect = nodeService.hasAspect(mainObject, BusinessJournalService.TYPE_HISTORY);

        if (!hasHistoryAspect) {
            Map<QName, Serializable> aspectValues = new HashMap<QName, Serializable>();
            aspectValues.put(BusinessJournalService.PROP_HISTORY_LIST, "");

            nodeService.addAspect(mainObject, BusinessJournalService.TYPE_HISTORY, aspectValues);
        }
    }

}
