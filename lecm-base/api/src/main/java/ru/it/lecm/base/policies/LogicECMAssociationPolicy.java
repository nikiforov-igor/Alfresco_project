package ru.it.lecm.base.policies;

import java.io.Serializable;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;

/**
 * @author dbashmakov
 *         Date: 05.02.13
 *         Time: 11:09
 */
public abstract class LogicECMAssociationPolicy implements NodeServicePolicies.OnCreateAssociationPolicy, NodeServicePolicies.OnDeleteAssociationPolicy {
	protected  PolicyComponent policyComponent;
	protected  NamespaceService namespaceService;
	protected  NodeService nodeService;
	protected  DictionaryService dictionaryService;

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

	public void init() {
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);
		PropertyCheck.mandatory(this, "namespaceService", namespaceService);
		PropertyCheck.mandatory(this, "nodeService", nodeService);
		PropertyCheck.mandatory(this, "dictionaryService", dictionaryService);
	}
	/**
	 * Метод добавляет значение ассоциации в поле объекта с именем 'assoc'-ref, если оно существует
	 * @param nodeAssocRef
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
			Serializable newValue = getSerializable(nodeAssocRef.getTargetRef());
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
	 * Метод сбрасывает значение ассоциации в поле объекта с именем 'assoc'-ref, если оно существует
	 * @param nodeAssocRef
	 */
	@Override
	public void onDeleteAssociation(AssociationRef nodeAssocRef) {

		NodeRef record = nodeAssocRef.getSourceRef();
		String assocQName = nodeAssocRef.getTypeQName().toPrefixString(namespaceService);
		QName propertyQName = QName.createQName(assocQName + "-ref", namespaceService);

		PropertyDefinition propertyDefinition = dictionaryService.getProperty(propertyQName);
		if (propertyDefinition != null) {
			Serializable oldValue = nodeService.getProperty(record, propertyQName);
			String strOldValue = oldValue != null ? oldValue.toString() : "";
			String refValue = nodeAssocRef.getTargetRef().toString();
			strOldValue = strOldValue.replace(";" + refValue, "");
			strOldValue = strOldValue.replace(refValue, "");
			nodeService.setProperty(record, propertyQName, strOldValue);
		}

		QName propertyTextQName = QName.createQName(assocQName + "-text-content", namespaceService);
		propertyDefinition = dictionaryService.getProperty(propertyTextQName);
		if (propertyDefinition != null) {
			Serializable oldValue = nodeService.getProperty(record, propertyTextQName);
			String strOldValue = oldValue != null ? oldValue.toString() : "";
			Serializable newValue = getSerializable(nodeAssocRef.getTargetRef());
			String strNewValue = newValue.toString();

			strOldValue = strOldValue.replace(";" + strNewValue, "");
			strOldValue = strOldValue.replace(strNewValue, "");
			nodeService.setProperty(record, propertyTextQName, strOldValue);
		}
	}

	protected Serializable getSerializable(final NodeRef node){
		return nodeService.getProperty(node, ContentModel.PROP_NAME);
	}
}
