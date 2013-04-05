package ru.it.lecm.dictionary.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.version.VersionServicePolicies;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.version.Version;
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
//		NodeServicePolicies.OnCreateAssociationPolicy,
//		NodeServicePolicies.OnDeleteAssociationPolicy,
		VersionServicePolicies.AfterCreateVersionPolicy {
	private final static Logger logger = LoggerFactory.getLogger(DictionaryLogEventPolicy.class);

	private PolicyComponent policyComponent;
	private BusinessJournalService businessJournalService;
	private DictionaryBean dictionaryService;

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	public void setDictionaryService(DictionaryBean dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	public final void init () {
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);

//		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
//				DictionaryBean.TYPE_PLANE_DICTIONARY_VALUE, new JavaBehaviour(this, "onCreateAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
//		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
//				DictionaryBean.TYPE_HIERARCHICAL_DICTIONARY_VALUE, new JavaBehaviour(this, "onCreateAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
//
//		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
//				DictionaryBean.TYPE_PLANE_DICTIONARY_VALUE, new JavaBehaviour(this, "onDeleteAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
//		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
//				DictionaryBean.TYPE_HIERARCHICAL_DICTIONARY_VALUE, new JavaBehaviour(this, "onDeleteAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				DictionaryBean.TYPE_PLANE_DICTIONARY_VALUE, new JavaBehaviour(this, "onCreateNode", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
				DictionaryBean.TYPE_PLANE_DICTIONARY_VALUE, new JavaBehaviour(this, "onUpdateProperties", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindClassBehaviour(VersionServicePolicies.AfterCreateVersionPolicy.QNAME,
				DictionaryBean.TYPE_PLANE_DICTIONARY_VALUE, new JavaBehaviour(this, "afterCreateVersion"));

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
				DictionaryBean.TYPE_HIERARCHICAL_DICTIONARY_VALUE, new JavaBehaviour(this, "onUpdateProperties", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindClassBehaviour(VersionServicePolicies.AfterCreateVersionPolicy.QNAME,
				DictionaryBean.TYPE_HIERARCHICAL_DICTIONARY_VALUE, new JavaBehaviour(this, "afterCreateVersion"));
	}

	@Override
	public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		NodeRef dictionary = dictionaryService.getDictionaryByDictionaryValue(nodeRef);
		List<String> objects = new ArrayList<String>();
		objects.add(nodeRef.toString());

		final Boolean prevActive = (Boolean) before.get(BaseBean.IS_ACTIVE);
		final Boolean curActive = (Boolean) after.get(BaseBean.IS_ACTIVE);
		final boolean changed = !safeEquals(prevActive, curActive);

		if (before.size() == after.size()) {
			if (!changed) {
				businessJournalService.log(dictionary, EventCategory.EDIT, "Сотрудник #initiator внёс изменения в элемент #object1 справочника #mainobject", objects);
			} else if (!curActive){
				businessJournalService.log(dictionary, EventCategory.DELETE, "Сотрудник #initiator удалил сведения об элементе #object1 справочника #mainobject", objects);
			}
		}
	}

	@Override
	public void afterCreateVersion(NodeRef versionableNode, Version version) {
		NodeRef dictionary = dictionaryService.getDictionaryByDictionaryValue(versionableNode);
		List<String> objects = new ArrayList<String>();
		objects.add(versionableNode.toString());

		try {
			businessJournalService.log(dictionary, EventCategory.ADD_NEW_VERSION, "Сотрудник #initiator создал новую версию элемента #object1 справочника #mainobject", objects);
		} catch (Exception e) {
			logger.error("Could not create the record business-journal", e);
		}
	}

	@Override
	public void onCreateNode(ChildAssociationRef childAssocRef) {
		NodeRef dictionary = dictionaryService.getDictionaryByDictionaryValue(childAssocRef.getChildRef());

		List<String> objects = new ArrayList<String>();
		objects.add(childAssocRef.getChildRef().toString());

		businessJournalService.log(dictionary, EventCategory.ADD, "Сотрудник #initiator добавил новый элемент #object1 в справочник #mainobject", objects);
	}

//	@Override
//	public void onCreateAssociation(AssociationRef nodeAssocRef) {
//		logEditDictionaryAssoc(nodeAssocRef);
//	}
//
//	@Override
//	public void onDeleteAssociation(AssociationRef nodeAssocRef) {
//		logEditDictionaryAssoc(nodeAssocRef);
//	}
//
//	public void logEditDictionaryAssoc(AssociationRef nodeAssocRef) {
//		NodeRef dictionaryValue = nodeAssocRef.getSourceRef();
//		if (this.dictionaryService.isDictionaryValue(dictionaryValue)) {
//			NodeRef dictionary = dictionaryService.getDictionaryByDictionaryValue(dictionaryValue);
//			if (dictionary != null) {
//				List<String> objects = new ArrayList<String>();
//				objects.add(dictionaryValue.toString());
//				businessJournalService.log(dictionary, EventCategory.EDIT, "Сотрудник #initiator внёс изменения в элемент #object1 справочника #mainobject", objects);
//			}
//		}
//	}

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
