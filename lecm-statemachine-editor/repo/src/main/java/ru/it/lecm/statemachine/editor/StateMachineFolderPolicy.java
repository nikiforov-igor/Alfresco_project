package ru.it.lecm.statemachine.editor;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
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
	public final static QName PROP_DOCUMENTS_FOLDER = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "documentsFolder");

	private static ServiceRegistry serviceRegistry;
	private static PolicyComponent policyComponent;

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		StateMachineFolderPolicy.serviceRegistry = serviceRegistry;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		StateMachineFolderPolicy.policyComponent = policyComponent;
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
			NodeRef folder = (NodeRef) after.get(PROP_DOCUMENTS_FOLDER);
			if (folder != null) {
				NodeService nodeService = serviceRegistry.getNodeService();
				nodeService.setProperty(folder, ContentModel.PROP_NAME, curValue);
			}
		}

	}
}
