package ru.it.lecm.documents.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.dictionary.ConstraintDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.documents.constraints.PresentStringConstraint;

import java.io.Serializable;
import java.util.Map;

/**
 * User: AIvkin
 * Date: 04.03.13
 * Time: 17:05
 */
public class DocumentPresentStringPolicy implements NodeServicePolicies.OnUpdatePropertiesPolicy {

    private static PolicyComponent policyComponent;
    private static DictionaryService dictionaryService;
    private static NodeService nodeService;
    private SubstitudeBean substituteService;

    public void setPolicyComponent(PolicyComponent policyComponent) {
        DocumentPresentStringPolicy.policyComponent = policyComponent;
    }

    public void setNodeService(NodeService nodeService) {
        DocumentPresentStringPolicy.nodeService = nodeService;
    }

    public void setDictionaryService(DictionaryService dictionaryService) {
        DocumentPresentStringPolicy.dictionaryService = dictionaryService;
    }

    public void setSubstituteService(SubstitudeBean substituteService) {
        this.substituteService = substituteService;
    }

    public final void init() {
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);
        PropertyCheck.mandatory(this, "nodeService", nodeService);

        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
                DocumentService.TYPE_BASE_DOCUMENT, new JavaBehaviour(this, "onUpdateProperties", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
    }

    @Override
    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        String presentString = "{cm:name}";

        QName type = nodeService.getType(nodeRef);
        ConstraintDefinition constraint = dictionaryService.getConstraint(QName.createQName(type.getNamespaceURI(), DocumentService.CONSTRAINT_PRESENT_STRING));
        if (constraint != null && constraint.getConstraint() != null && (constraint.getConstraint() instanceof PresentStringConstraint)) {
            PresentStringConstraint psConstraint = (PresentStringConstraint) constraint.getConstraint();
            if (psConstraint.getPresentString() != null) {
                presentString = psConstraint.getPresentString();
            }
        }

        String presentStringValue = substituteService.formatNodeTitle(nodeRef, presentString);
        if (presentStringValue != null) {
            nodeService.setProperty(nodeRef, DocumentService.PROP_PRESENT_STRING, presentStringValue);
        }
        String listPresentString = substituteService.getTemplateStringForObject(nodeRef, true);

        String listPresentStringValue = substituteService.formatNodeTitle(nodeRef, listPresentString);
        if (listPresentStringValue != null) {
            nodeService.setProperty(nodeRef, DocumentService.PROP_LIST_PRESENT_STRING, listPresentStringValue);
        }
    }
}
