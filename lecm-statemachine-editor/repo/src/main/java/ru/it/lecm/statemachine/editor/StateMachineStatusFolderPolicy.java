package ru.it.lecm.statemachine.editor;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;

import java.io.Serializable;
import java.util.Map;

/**
 * User: PMelnikov
 * Date: 16.11.12
 * Time: 14:45
 */
public class StateMachineStatusFolderPolicy implements NodeServicePolicies.OnUpdatePropertiesPolicy, NodeServicePolicies.OnCreateNodePolicy {

	public final static QName TYPE_CONTENT = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "status");

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
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				TYPE_CONTENT, new JavaBehaviour(this, "onCreateNode"));

	}

	@Override
	public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		String prevValue = (String) before.get(ContentModel.PROP_NAME);
		String curValue = (String) after.get(ContentModel.PROP_NAME);
		if (curValue != null && !curValue.equals(prevValue)) {
			NodeService nodeService = serviceRegistry.getNodeService();
			ChildAssociationRef parentAssoc = nodeService.getPrimaryParent(nodeRef);
			NodeRef parent = parentAssoc.getParentRef();
			String parentFolder = (String) nodeService.getProperty(parent, ContentModel.PROP_NAME);

			SearchParameters sp = new SearchParameters();
			sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
			sp.setLanguage(SearchService.LANGUAGE_XPATH);
			sp.setQuery("/app:company_home/cm:documents/cm:" + parentFolder + "/cm:" + prevValue);

			SearchService searchService = serviceRegistry.getSearchService();
			ResultSet result = searchService.query(sp);
			if (result.length() > 0) {
				NodeRef folder = result.getNodeRef(0);
				nodeService.setProperty(folder, QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "name"), curValue);
			}
		}

	}

	@Override
	public void onCreateNode(ChildAssociationRef childAssocRef) {
		NodeRef node = childAssocRef.getChildRef();
		NodeRef parent = childAssocRef.getParentRef();
		NodeService nodeService = serviceRegistry.getNodeService();
		String parentFolderName = (String) nodeService.getProperty(parent, StateMachineFolderPolicy.PROP_STATEMACHINE_FOLDER);
		String statusFolderName = (String) nodeService.getProperty(node, ContentModel.PROP_NAME);

		SearchParameters sp = new SearchParameters();
		sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		sp.setLanguage(SearchService.LANGUAGE_XPATH);
		sp.setQuery("/app:company_home/cm:documents/cm:" + parentFolderName);

		SearchService searchService = serviceRegistry.getSearchService();
		ResultSet result = searchService.query(sp);
		if (result.length() > 0) {
			NodeRef parentFolder = result.getNodeRef(0);
			nodeService.createNode(
					parentFolder,
					ContentModel.ASSOC_CONTAINS,
					QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName(statusFolderName)),
					ContentModel.TYPE_FOLDER,
					null);
		}

	}
}
