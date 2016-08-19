package ru.it.lecm.base.policies;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.MLPropertyInterceptor;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.MLText;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.alfresco.util.PropertyMap;
import org.apache.commons.lang.StringEscapeUtils;
import ru.it.lecm.base.beans.LecmMessageService;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

/**
 * Policy, содержащее логику сохранения текстовых описаний (для последующего поиска по ним) объектов,
 * на которые добавляется ассоциация. Наследуется другими, чтобы включить сохранение описаний
 * для конкретных типов в соответствующих модулях
 * @author dbashmakov
 *         Date: 05.02.13
 *         Time: 11:09
 */
public abstract class LogicECMAssociationPolicy implements NodeServicePolicies.OnCreateAssociationPolicy, NodeServicePolicies.OnDeleteAssociationPolicy {
	protected  PolicyComponent policyComponent;
	protected  NamespaceService namespaceService;
	protected  NodeService nodeService;
	protected  DictionaryService dictionaryService;
    protected LecmMessageService lecmMessageService;

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

    public void setLecmMessageService(LecmMessageService lecmMessageService) {
        this.lecmMessageService = lecmMessageService;
    }

    public void init() {
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);
        PropertyCheck.mandatory(this, "namespaceService", namespaceService);
        PropertyCheck.mandatory(this, "nodeService", nodeService);
        PropertyCheck.mandatory(this, "dictionaryService", dictionaryService);
    }
    /**
     * Метод добавляет значение ассоциации в поле объекта с именем 'assoc'-ref и 'assoc'-text-content, если оно существует
     */
    @Override
    public void onCreateAssociation(AssociationRef nodeAssocRef) {
        NodeRef record = nodeAssocRef.getSourceRef();
        updateTextContent(record, nodeAssocRef.getTypeQName());
    }

    /**
     * Метод сбрасывает значение ассоциации в поле объекта с именем 'assoc'-ref и 'assoc'-text-content, если оно существует
     */
    @Override
    public void onDeleteAssociation(AssociationRef nodeAssocRef) {
        NodeRef record = nodeAssocRef.getSourceRef();
        updateTextContent(record, nodeAssocRef.getTypeQName());
    }

    protected void updateTextContent(NodeRef nodeRef, QName assocQName) {
        StringBuilder builderText = new StringBuilder();
        StringBuilder builderRef = new StringBuilder();

        QName propertyTextQName = QName.createQName(assocQName.toPrefixString(namespaceService) + "-text-content", namespaceService);
        QName propertyMlTextQName = QName.createQName(assocQName.toPrefixString(namespaceService) + "-ml-text-content", namespaceService);
        QName propertyRefQName = QName.createQName(assocQName.toPrefixString(namespaceService) + "-ref", namespaceService);

        PropertyDefinition propertyDefinitionText = dictionaryService.getProperty(propertyTextQName);
        PropertyDefinition propertyDefinitionMlText = dictionaryService.getProperty(propertyMlTextQName);
        PropertyDefinition propertyDefinitionRef = dictionaryService.getProperty(propertyRefQName);
        if (propertyDefinitionText != null || propertyDefinitionRef != null) {
            List<AssociationRef> assocs = nodeService.getTargetAssocs(nodeRef, assocQName);
            for (AssociationRef assoc : assocs) {
                NodeRef targetRef = assoc.getTargetRef();
                if (targetRef != null) {
                    builderText.append(getSerializable(targetRef)).append(";");
                    builderRef.append(targetRef.toString()).append(";");
                }
            }
            if (propertyDefinitionText != null) { // text-content
                String textValue = "";
                if (builderText.length() > 0)  {
                    textValue = builderText.substring(0, builderText.length() - 1);
                }
                nodeService.setProperty(nodeRef, propertyTextQName, textValue);
            }
            if (propertyDefinitionMlText != null) { // ml-text-content
//                List<Locale> locales = lecmMessageService.getAvailableLocales();
//                List<Locale> fallback = lecmMessageService.getFallbackLocales();
//                MLPropertyInterceptor.setMLAware(true);
//                MLText mlText = new MLText();
//                for (Locale locale : fallback) {
//                    mlText.addValue(locale, names[0]);
//                }
//                for (Locale locale : locales) {
//                    String categoryTitle = StringEscapeUtils.unescapeJava(messageService.getMessage(messageKey, locale));
//                    if (categoryTitle != null) {
//                        mlText.addValue(locale, categoryTitle);
//                    }
//                }
//                PropertyMap props = new PropertyMap();
//                props.put(ContentModel.PROP_TITLE, mlText);
//                nodeService.addAspect(categoryRef, ContentModel.ASPECT_TITLED, props);
//                MLPropertyInterceptor.setMLAware(false);
//
//                String textValue = "";
//                if (builderText.length() > 0)  {
//                    textValue = builderText.substring(0, builderText.length() - 1);
//                }
//                nodeService.setProperty(nodeRef, propertyTextQName, textValue);
            }
            if (propertyDefinitionRef != null) { // -ref values
                String textValue = "";
                if (builderRef.length() > 0)  {
                    textValue = builderRef.substring(0, builderRef.length() - 1);
                }
                nodeService.setProperty(nodeRef, propertyRefQName,textValue);
            }
        }
    }

	protected Serializable getSerializable(final NodeRef node){
		return nodeService.getProperty(node, ContentModel.PROP_NAME);
	}
}
