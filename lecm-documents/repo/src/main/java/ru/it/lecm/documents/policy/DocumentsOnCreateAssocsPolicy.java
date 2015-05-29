package ru.it.lecm.documents.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.base.policies.LogicECMAssociationPolicy;
import ru.it.lecm.documents.beans.DocumentService;

import java.io.Serializable;

public class DocumentsOnCreateAssocsPolicy extends LogicECMAssociationPolicy implements NodeServicePolicies.BeforeDeleteAssociationPolicy{
    private SubstitudeBean substitute;

    @Override
    public final void init() {
        super.init();
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
                DocumentService.TYPE_BASE_DOCUMENT, new JavaBehaviour(this, "onDeleteAssociation"));

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                DocumentService.TYPE_BASE_DOCUMENT, new JavaBehaviour(this, "onCreateAssociation"));

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.BeforeDeleteAssociationPolicy.QNAME,
                DocumentService.TYPE_BASE_DOCUMENT, new JavaBehaviour(this, "beforeDeleteAssociation"));
    }

    /**
     * Метод добавляет значение ассоциации в поле объекта с именем 'assoc'-ref и 'assoc'-text-content, если оно существует
     */
    @Override
    public void onCreateAssociation(AssociationRef nodeAssocRef) {
        NodeRef record = nodeAssocRef.getSourceRef();
        String assocQName = nodeAssocRef.getTypeQName().toPrefixString(namespaceService);

        QName propertyRefQName = QName.createQName(assocQName + "-ref", namespaceService);
        PropertyDefinition propertyDefinition = dictionaryService.getProperty(propertyRefQName);
        if (propertyDefinition != null) {
            Serializable oldValue = nodeService.getProperty(record, propertyRefQName);
            String strOldValue = oldValue != null ? oldValue.toString() : "";
            String refValue = nodeAssocRef.getTargetRef().toString();
            if (!strOldValue.contains(refValue)) {
                if (!strOldValue.isEmpty()) {
                    strOldValue += ";";
                }
                strOldValue += refValue;
            }
            nodeService.setProperty(record, propertyRefQName, strOldValue);
        }
        QName propertyTextQName = QName.createQName(assocQName + "-text-content", namespaceService);
        propertyDefinition = dictionaryService.getProperty(propertyTextQName);
        if (propertyDefinition != null) {
            Serializable oldValue = nodeService.getProperty(record, propertyTextQName);
            String strOldValue = oldValue != null ? oldValue.toString() : "";
            Serializable newValue = nodeService.exists(nodeAssocRef.getTargetRef()) ? getSerializable(nodeAssocRef.getTargetRef()) : "";
            String strNewValue = newValue.toString();
            if (!strOldValue.contains(strNewValue)) {
                if (!strOldValue.isEmpty()) {
                    strOldValue += ";";
                }
                strOldValue += strNewValue;
            }
            nodeService.setProperty(record, propertyTextQName, strOldValue);
        }
    }

    /**
     * Метод сбрасывает значение ассоциации в поле объекта с именем 'assoc'-ref и 'assoc'-text-content, если оно существует
     */
    @Override
    public void beforeDeleteAssociation(AssociationRef nodeAssocRef) {
        NodeRef record = nodeAssocRef.getSourceRef();
        updateTextContent(record, nodeAssocRef.getTypeQName());
    }


    @Override
    protected Serializable getSerializable(final NodeRef node){
        return substitute.getObjectDescription(node);
    }

    public void setSubstitute(SubstitudeBean substitute) {
        this.substitute = substitute;
    }
}
