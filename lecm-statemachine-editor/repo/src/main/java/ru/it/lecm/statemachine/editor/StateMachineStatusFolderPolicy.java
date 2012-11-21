package ru.it.lecm.statemachine.editor;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
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
	public final static QName ASSOC_STATUS_FOLDER = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "statusFolder");

	private static ServiceRegistry serviceRegistry;
	private static PolicyComponent policyComponent;

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		StateMachineStatusFolderPolicy.serviceRegistry = serviceRegistry;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		StateMachineStatusFolderPolicy.policyComponent = policyComponent;
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
			NodeRef folder = (NodeRef) after.get(ASSOC_STATUS_FOLDER);
			if (folder != null) {
				NodeService nodeService = serviceRegistry.getNodeService();
				nodeService.setProperty(folder, ContentModel.PROP_NAME, curValue);
			}
		}

	}

	@Override
	public void onCreateNode(ChildAssociationRef childAssocRef) {
		NodeRef node = childAssocRef.getChildRef();
		NodeRef parent = childAssocRef.getParentRef();
		NodeService nodeService = serviceRegistry.getNodeService();
		NodeRef folder = (NodeRef) nodeService.getProperty(parent, StateMachineFolderPolicy.PROP_DOCUMENTS_FOLDER);
		String statusFolderName = (String) nodeService.getProperty(node, ContentModel.PROP_NAME);

		if (folder != null) {
			ChildAssociationRef ref = nodeService.createNode(
					folder,
					ContentModel.ASSOC_CONTAINS,
					QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName(statusFolderName)),
					ContentModel.TYPE_FOLDER,
					null);
			nodeService.setProperty(ref.getChildRef(), ContentModel.PROP_NAME, statusFolderName);
			nodeService.setProperty(node, ASSOC_STATUS_FOLDER, ref.getChildRef());
		}

	}
}
