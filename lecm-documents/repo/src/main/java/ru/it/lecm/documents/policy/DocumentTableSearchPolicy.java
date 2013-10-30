package ru.it.lecm.documents.policy;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.base.policies.LogicECMAssociationPolicy;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.documents.beans.DocumentTableService;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * User: AZinovin
 * Date: 28.10.13
 * Time: 12:00
 */
public class DocumentTableSearchPolicy extends LogicECMAssociationPolicy implements NodeServicePolicies.OnUpdatePropertiesPolicy {

	final static protected Logger logger = LoggerFactory.getLogger(DocumentTableSearchPolicy.class);

    private SubstitudeBean substitudeService;

    public void setSubstitudeService(SubstitudeBean substitudeService) {
        this.substitudeService = substitudeService;
    }

	final public void init() {
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				DocumentService.TYPE_BASE_DOCUMENT, new JavaBehaviour(this, "onCreateAssociation"));

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				DocumentService.TYPE_BASE_DOCUMENT, new JavaBehaviour(this, "onDeleteAssociation"));

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
                DocumentTableService.TYPE_TABLE_DATA_ROW, new JavaBehaviour(this, "onUpdateProperties", JavaBehaviour.NotificationFrequency.TRANSACTION_COMMIT));
	}

    @Override
    protected Serializable getSerializable(NodeRef node) {
        if (!dictionaryService.isSubClass(nodeService.getType(node), DocumentTableService.TYPE_TABLE_DATA_ROW)) {
            return super.getSerializable(node);
        }
        String templateStringForObject = substitudeService.getTemplateStringForObject(node, false, false);
//        если задано форматирование для объекта, то используем его
        if (templateStringForObject != null) {
            return substitudeService.formatNodeTitle(node, templateStringForObject);
        }
//        если форматирование не задано - собираем все индексируемые атрибуты
        StringBuilder stringBuilder = new StringBuilder();
        Map<QName, Serializable> properties = nodeService.getProperties(node);
        for (Map.Entry<QName, Serializable> propertyEntry : properties.entrySet()) {
            QName qName = propertyEntry.getKey();
            if (qName.getNamespaceURI().equals(NamespaceService.SYSTEM_MODEL_1_0_URI)
                    || qName.equals(ContentModel.PROP_CREATOR)
                    || qName.equals(ContentModel.PROP_CREATED)
                    || qName.equals(ContentModel.PROP_MODIFIER)
                    || qName.equals(ContentModel.PROP_MODIFIED)
                    || qName.getLocalName().endsWith("-text-content")
                    || qName.getLocalName().endsWith("-ref")) {
                continue;
            }
            PropertyDefinition propertyDefinition = dictionaryService.getProperty(qName);
            if (propertyDefinition.isIndexed()) {
                Serializable value = propertyEntry.getValue();
                if (value != null)
                stringBuilder.append(value).append(" ");
            }
        }

        return stringBuilder.length() > 0 ? stringBuilder.substring(0, stringBuilder.length() - 1) : "";
    }

    @Override
    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        List<AssociationRef> sourceAssocs = nodeService.getSourceAssocs(nodeRef, RegexQNamePattern.MATCH_ALL);
        for (AssociationRef sourceAssoc : sourceAssocs) {
            NodeRef sourceRef = sourceAssoc.getSourceRef();
            if (dictionaryService.isSubClass(nodeService.getType(sourceRef), DocumentService.TYPE_BASE_DOCUMENT)) {
                updateTextContent(sourceRef, sourceAssoc.getTypeQName());
            }
        }
    }
}
