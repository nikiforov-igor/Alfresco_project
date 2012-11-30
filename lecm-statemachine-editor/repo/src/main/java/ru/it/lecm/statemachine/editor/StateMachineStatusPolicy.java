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
import java.util.List;
import java.util.Map;

/**
 * User: PMelnikov
 * Date: 16.11.12
 * Time: 14:45
 */
public class StateMachineStatusPolicy implements NodeServicePolicies.OnUpdatePropertiesPolicy, NodeServicePolicies.OnCreateNodePolicy {

	public final static QName TYPE_CONTENT = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "status");
	public final static QName ASSOC_STATUS_FOLDER = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "statusFolder");
	public final static QName PROP_START_STATUS = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "startStatus");

	private static ServiceRegistry serviceRegistry;
	private static PolicyComponent policyComponent;

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		StateMachineStatusPolicy.serviceRegistry = serviceRegistry;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		StateMachineStatusPolicy.policyComponent = policyComponent;
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
		NodeService nodeService = serviceRegistry.getNodeService();
		String prevValue = (String) before.get(ContentModel.PROP_NAME);
		String curValue = (String) after.get(ContentModel.PROP_NAME);
		if (curValue != null && !curValue.equals(prevValue)) {
			NodeRef folder = (NodeRef) after.get(ASSOC_STATUS_FOLDER);
			if (folder != null) {
				nodeService.setProperty(folder, ContentModel.PROP_NAME, curValue);
			}
		}

		if (after.get(PROP_START_STATUS) != null) {
			setStartStatus(nodeRef, (Boolean) after.get(PROP_START_STATUS));
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

		if (nodeService.getProperty(node, PROP_START_STATUS) != null) {
			setStartStatus(node, (Boolean) nodeService.getProperty(node, PROP_START_STATUS));
		}

	}

	private void setStartStatus(NodeRef statusRef, boolean isStart) {
		if (!isStart) {
			return;
		}
		NodeService nodeService = serviceRegistry.getNodeService();
		ChildAssociationRef statemachineRef = nodeService.getPrimaryParent(statusRef);
		List<ChildAssociationRef> statuses = nodeService.getChildAssocs(statemachineRef.getParentRef());
		for (ChildAssociationRef status : statuses) {
			nodeService.setProperty(status.getChildRef(), PROP_START_STATUS, false);
		}
		nodeService.setProperty(statusRef, PROP_START_STATUS, true);
	}

}
