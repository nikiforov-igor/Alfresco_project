package ru.it.lecm.statemachine.editor;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;

import java.io.Serializable;
import java.util.Map;

/**
 * User: PMelnikov
 * Date: 16.11.12
 * Time: 14:45
 */
public class StateMachineFolderPolicy implements NodeServicePolicies.OnUpdatePropertiesPolicy {

	public final static QName TYPE_CONTENT = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "statemachine");
	public final static QName PROP_STATEMACHINE_FOLDER = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "machineFolder");

	private static ServiceRegistry serviceRegistry;
	private static PolicyComponent policyComponent;

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public final void init() {
		PropertyCheck.mandatory(this, "serviceRegistry", serviceRegistry);
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
				TYPE_CONTENT, new JavaBehaviour(this, "onUpdateProperties"));

	}

	@Override
	public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		String prevValue = (String) before.get(PROP_STATEMACHINE_FOLDER);
		String curValue = (String) after.get(PROP_STATEMACHINE_FOLDER);
		if (curValue != null && !curValue.equals(prevValue)) {
			SearchParameters sp = new SearchParameters();
			sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
			sp.setLanguage(SearchService.LANGUAGE_XPATH);
			sp.setQuery("/app:company_home/cm:documents/cm:" + prevValue);

			SearchService searchService = serviceRegistry.getSearchService();
			ResultSet result = searchService.query(sp);
			if (result.length() > 0) {
				NodeRef folder = result.getNodeRef(0);
				NodeService nodeService = serviceRegistry.getNodeService();
				nodeService.setProperty(folder, ContentModel.PROP_NAME, curValue);
			}
		}

	}
}
