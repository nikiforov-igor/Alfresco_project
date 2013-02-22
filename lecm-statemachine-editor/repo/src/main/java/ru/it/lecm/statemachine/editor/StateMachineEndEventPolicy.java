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
import ru.it.lecm.statemachine.bean.StateMachineActions;

import java.io.Serializable;
import java.util.HashMap;

/**
 * User: PMelnikov
 * Date: 16.11.12
 * Time: 14:45
 */
public class StateMachineEndEventPolicy implements NodeServicePolicies.OnCreateNodePolicy {

	private static ServiceRegistry serviceRegistry;
	private static PolicyComponent policyComponent;
	private static StateMachineActions stateMachineActions;

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		StateMachineEndEventPolicy.serviceRegistry = serviceRegistry;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		StateMachineEndEventPolicy.policyComponent = policyComponent;
	}

	public void setStateMachineActions(StateMachineActions stateMachineActions) {
		StateMachineEndEventPolicy.stateMachineActions = stateMachineActions;
	}

	public final void init() {
		PropertyCheck.mandatory(this, "serviceRegistry", serviceRegistry);
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);
		PropertyCheck.mandatory(this, "stateMachineActions", stateMachineActions);

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				StatemachineEditorModel.TYPE_END_EVENT, new JavaBehaviour(this, "onCreateNode"));

	}

	@Override
	public void onCreateNode(ChildAssociationRef childAssocRef) {
		NodeRef node = childAssocRef.getChildRef();
		NodeService nodeService = serviceRegistry.getNodeService();

		HashMap<QName, Serializable> props = new HashMap<QName, Serializable>(1, 1.0f);
		props.put(ContentModel.PROP_NAME, "actions");

		NodeRef actionsFolder = nodeService.createNode(
				node,
				ContentModel.ASSOC_CONTAINS,
				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "actions"),
				StatemachineEditorModel.TYPE_ACTIONS,
				props).getChildRef();


		//Добавляем действия к завершающему событию
		String actionName = "ScriptAction";
		HashMap<QName, Serializable> actionProps = new HashMap<QName, Serializable>(1, 1.0f);
		actionProps.put(StatemachineEditorModel.PROP_ACTION_ID, actionName);
		actionProps.put(StatemachineEditorModel.PROP_ACTION_EXECUTION, "end");
		childAssocRef = nodeService.createNode(
				actionsFolder,
				ContentModel.ASSOC_CONTAINS,
				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, actionName),
				QName.createQName(StatemachineEditorModel.STATEMACHINE_EDITOR_URI, actionName),
				actionProps);
		nodeService.setProperty(node, QName.createQName(StatemachineEditorModel.STATEMACHINE_EDITOR_URI, "endEvent" + actionName), childAssocRef.getChildRef().toString());
	}

}
