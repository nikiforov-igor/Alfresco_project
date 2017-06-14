package ru.it.lecm.operativestorage.policies;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.operativestorage.beans.OperativeStorageService;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * Created by KKurets on 14.06.2017.
 */
public class UnitSectionPolicy implements NodeServicePolicies.OnUpdatePropertiesPolicy{

    private PolicyComponent policyComponent;
    private NodeService nodeService;

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void init() {
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME, OperativeStorageService.TYPE_NOMENCLATURE_UNIT_SECTION, new JavaBehaviour(this, "onUpdateProperties", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
    }

    @Override
    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        String unitSectionIndexBefore = (String) before.get(OperativeStorageService.PROP_NOMENCLATURE_UNIT_SECTION_INDEX);
        String unitSectionIndexAfter = (String) after.get(OperativeStorageService.PROP_NOMENCLATURE_UNIT_SECTION_INDEX);

        if (!(unitSectionIndexBefore == null && unitSectionIndexAfter == null) && !Objects.equals(unitSectionIndexBefore, unitSectionIndexAfter)) {
            nodeService.setProperty(nodeRef, OperativeStorageService.PROP_NOMENCLATURE_COMMON_INDEX, unitSectionIndexAfter);
        }
    }
}
