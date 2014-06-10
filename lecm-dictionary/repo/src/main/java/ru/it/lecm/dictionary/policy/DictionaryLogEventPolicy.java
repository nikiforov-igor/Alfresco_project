package ru.it.lecm.dictionary.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.policy.PolicyScope;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.repo.version.VersionServicePolicies;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.dictionary.beans.DictionaryBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: AIvkin
 * Date: 13.02.13
 * Time: 10:33
 */
public class DictionaryLogEventPolicy implements
		NodeServicePolicies.OnCreateNodePolicy,
		NodeServicePolicies.OnUpdatePropertiesPolicy,
		NodeServicePolicies.OnCreateAssociationPolicy,
		NodeServicePolicies.OnDeleteAssociationPolicy,
		VersionServicePolicies.OnCreateVersionPolicy {
	private final static Logger logger = LoggerFactory.getLogger(DictionaryLogEventPolicy.class);

	private PolicyComponent policyComponent;
	private BusinessJournalService businessJournalService;
	private DictionaryBean dictionaryService;
	protected NodeService nodeService;

	private String lastTransactionId = "";

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	public void setDictionaryService(DictionaryBean dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public final void init () {
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				DictionaryBean.TYPE_PLANE_DICTIONARY_VALUE, new JavaBehaviour(this, "onCreateAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				DictionaryBean.TYPE_HIERARCHICAL_DICTIONARY_VALUE, new JavaBehaviour(this, "onCreateAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				DictionaryBean.TYPE_PLANE_DICTIONARY_VALUE, new JavaBehaviour(this, "onDeleteAssociation", Behaviour.NotificationFrequency.FIRST_EVENT));
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				DictionaryBean.TYPE_HIERARCHICAL_DICTIONARY_VALUE, new JavaBehaviour(this, "onDeleteAssociation", Behaviour.NotificationFrequency.FIRST_EVENT));

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				DictionaryBean.TYPE_PLANE_DICTIONARY_VALUE, new JavaBehaviour(this, "onCreateNode", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
				DictionaryBean.TYPE_PLANE_DICTIONARY_VALUE, new JavaBehaviour(this, "onUpdateProperties", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindClassBehaviour(VersionServicePolicies.OnCreateVersionPolicy.QNAME,
				DictionaryBean.TYPE_PLANE_DICTIONARY_VALUE, new JavaBehaviour(this, "onCreateVersion", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				DictionaryBean.TYPE_HIERARCHICAL_DICTIONARY_VALUE, new JavaBehaviour(this, "onCreateNode", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
				DictionaryBean.TYPE_HIERARCHICAL_DICTIONARY_VALUE, new JavaBehaviour(this, "onUpdateProperties", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindClassBehaviour(VersionServicePolicies.OnCreateVersionPolicy.QNAME,
				DictionaryBean.TYPE_HIERARCHICAL_DICTIONARY_VALUE, new JavaBehaviour(this, "onCreateVersion", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
	}

	@Override
	public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		final Boolean prevActive = (Boolean) before.get(BaseBean.IS_ACTIVE);
		final Boolean curActive = (Boolean) after.get(BaseBean.IS_ACTIVE);
		final boolean changed = !safeEquals(prevActive, curActive);

		if (before.size() == after.size()) {
			if (!changed) {
				logEditDictionaryAssoc(nodeRef);
			} else if (!curActive){
				NodeRef dictionary = dictionaryService.getDictionaryByDictionaryValue(nodeRef);
				if (dictionary != null) {
					List<String> objects = new ArrayList<String>();
					objects.add(nodeRef.toString());
					businessJournalService.log(dictionary, EventCategory.DELETE, "#initiator удалил(а) сведения об элементе #object1 справочника #mainobject", objects);
				}
			}
		}
		this.lastTransactionId = AlfrescoTransactionSupport.getTransactionId();
	}

	@Override
	public void onCreateVersion(QName classRef, NodeRef versionableNode, Map<String, Serializable> versionProperties, PolicyScope nodeDetails) {
		NodeRef dictionary = dictionaryService.getDictionaryByDictionaryValue(versionableNode);
		List<String> objects = new ArrayList<String>();
		objects.add(versionableNode.toString());

		Boolean isActive = (Boolean) nodeService.getProperty(versionableNode, BaseBean.IS_ACTIVE);
		if (versionProperties.size() > 0 && (isActive == null || isActive)) {
			try {
				businessJournalService.log(dictionary, EventCategory.ADD_NEW_VERSION, "#initiator создал(а) новую версию элемента #object1 справочника #mainobject", objects);
			} catch (Exception e) {
				logger.error("Could not create the record business-journal", e);
			}
		}
		this.lastTransactionId = AlfrescoTransactionSupport.getTransactionId();
	}

	@Override
	public void onCreateNode(ChildAssociationRef childAssocRef) {
		NodeRef dictionary = dictionaryService.getDictionaryByDictionaryValue(childAssocRef.getChildRef());
		if (dictionary != null) {
			List<String> objects = new ArrayList<String>();
			objects.add(childAssocRef.getChildRef().toString());
			businessJournalService.log(dictionary, EventCategory.ADD, "#initiator добавил(а) новый элемент #object1 в справочник #mainobject", objects);
		}
		this.lastTransactionId = AlfrescoTransactionSupport.getTransactionId();
	}

	@Override
	public void onCreateAssociation(AssociationRef nodeAssocRef) {
		logEditDictionaryAssoc(nodeAssocRef.getSourceRef());
	}

	@Override
	public void onDeleteAssociation(AssociationRef nodeAssocRef) {
		logEditDictionaryAssoc(nodeAssocRef.getSourceRef());
	}

	public void logEditDictionaryAssoc(NodeRef nodeRef) {
		String transactionId = AlfrescoTransactionSupport.getTransactionId();
		if (this.lastTransactionId != transactionId) {
			this.lastTransactionId = transactionId;

			if (this.dictionaryService.isDictionaryValue(nodeRef)) {
				NodeRef dictionary = dictionaryService.getDictionaryByDictionaryValue(nodeRef);
				if (dictionary != null) {
					List<String> objects = new ArrayList<String>();
					objects.add(nodeRef.toString());
					businessJournalService.log(dictionary, EventCategory.EDIT, "#initiator внёс(ла) изменения в элемент #object1 справочника #mainobject", objects);
				}
			}
		}
	}

	/**
	 * Сравнить два объекта по значению
	 * @param o1 объект 1
	 * @param o2 объект 2
	 * @return true, если объекты равны (в том числе когда обоа null), false иначе
	 */
	public static boolean safeEquals(Object o1, Object o2) {
		return (o1 == o2) || (o1 != null && o1.equals(o2));
	}
}
