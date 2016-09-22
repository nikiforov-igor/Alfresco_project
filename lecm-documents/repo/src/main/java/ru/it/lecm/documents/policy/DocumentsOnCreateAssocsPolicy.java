package ru.it.lecm.documents.policy;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.MLPropertyInterceptor;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.MLText;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.base.policies.LogicECMAssociationPolicy;
import ru.it.lecm.documents.beans.DocumentService;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

public class DocumentsOnCreateAssocsPolicy extends LogicECMAssociationPolicy implements NodeServicePolicies.BeforeDeleteAssociationPolicy {
	
	final static protected Logger logger = LoggerFactory.getLogger(DocumentsOnCreateAssocsPolicy.class);
	
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
    	logger.debug("ДОКУМЕНТ. onCreateAssociation");
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

        List<Locale> locales = lecmMessageService.getMlLocales();
        if (lecmMessageService.isMlSupported()) {
            QName propertyMlTextQName = QName.createQName(assocQName + "-ml-text-content", namespaceService);
            PropertyDefinition propertyDefinitionMlText = dictionaryService.getProperty(propertyMlTextQName);
            if (propertyDefinitionMlText != null && nodeService.exists(nodeAssocRef.getTargetRef())) { // ml-text-content
                MLPropertyInterceptor.setMLAware(true);
                MLText title = null;
                if (nodeService.hasAspect(nodeAssocRef.getTargetRef(), ContentModel.ASPECT_TITLED)) {
                    title = (MLText) nodeService.getProperty(nodeAssocRef.getTargetRef(), ContentModel.PROP_TITLE);
                }
                MLText oldMlText = (MLText) nodeService.getProperty(record, propertyMlTextQName);
                if (oldMlText == null) {
                    oldMlText = new MLText();
                }
                for (Locale locale : locales) {
                    String localeValue;
                    if (title != null) {
                        localeValue = title.get(locale);
                    } else {
                        localeValue = getSerializable(nodeAssocRef.getTargetRef()).toString();
                    }
                    if (localeValue != null && !localeValue.isEmpty()) {

                        String mlTextValue = oldMlText.get(locale);
                        if (mlTextValue != null) {
                            if (!mlTextValue.isEmpty()) {
                                mlTextValue += ";";
                            }
                            mlTextValue += localeValue;
                        } else {
                            mlTextValue = localeValue;
                        }
                        oldMlText.addValue(locale, mlTextValue);
                    }
                }

                nodeService.setProperty(record, propertyMlTextQName, oldMlText);
                MLPropertyInterceptor.setMLAware(false);
            }
        }
    }

    /**
     * Метод сбрасывает значение ассоциации в поле объекта с именем 'assoc'-ref и 'assoc'-text-content, если оно существует
     */
    @Override
    public void beforeDeleteAssociation(AssociationRef nodeAssocRef) {
    	logger.debug("ДОКУМЕНТ. beforeDeleteAssociation");
        NodeRef record = nodeAssocRef.getSourceRef();
        updateTextContent(record, nodeAssocRef.getTypeQName());
    }


    @Override
    protected Serializable getSerializable(final NodeRef node) {
        return substitute.getObjectDescription(node);
    }

    public void setSubstitute(SubstitudeBean substitute) {
        this.substitute = substitute;
    }
}
