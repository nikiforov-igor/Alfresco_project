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
import org.alfresco.util.GUID;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.statemachine.action.finishstate.FinishStateWithTransitionAction;
import ru.it.lecm.statemachine.bean.StateMachineActions;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * User: PMelnikov
 * Date: 16.11.12
 * Time: 14:45
 */
public class StateMachineStatusPolicy implements NodeServicePolicies.OnCreateNodePolicy {

	public final static String STATEMACHINE_URI = "http://www.it.ru/logicECM/statemachine/editor/1.0";
	public final static QName TYPE_STATUS = QName.createQName(STATEMACHINE_URI, "taskStatus");
	public final static QName PROP_ACTION_ID = QName.createQName(STATEMACHINE_URI, "actionId");
	public final static QName PROP_ACTION_EXECUTION = QName.createQName(STATEMACHINE_URI, "actionExecution");
	public final static QName PROP_STATUS_UUID = QName.createQName(STATEMACHINE_URI, "statusUUID");
	public final static QName TYPE_ROLES = QName.createQName(STATEMACHINE_URI, "roles");
	public final static QName TYPE_ACTIONS = QName.createQName(STATEMACHINE_URI, "actions");
	public final static QName PROP_STATIC_ROLES = QName.createQName(STATEMACHINE_URI, "staticRoles");
	public final static QName PROP_DYNAMIC_ROLES = QName.createQName(STATEMACHINE_URI, "dynamicRoles");

	private static ServiceRegistry serviceRegistry;
	private static PolicyComponent policyComponent;
	private static StateMachineActions stateMachineActions;

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		StateMachineStatusPolicy.serviceRegistry = serviceRegistry;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		StateMachineStatusPolicy.policyComponent = policyComponent;
	}

	public void setStateMachineActions(StateMachineActions stateMachineActions) {
		StateMachineStatusPolicy.stateMachineActions = stateMachineActions;
	}

	public final void init() {
		PropertyCheck.mandatory(this, "serviceRegistry", serviceRegistry);
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);
		PropertyCheck.mandatory(this, "stateMachineActions", stateMachineActions);

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				TYPE_STATUS, new JavaBehaviour(this, "onCreateNode"));

	}

	@Override
	public void onCreateNode(ChildAssociationRef childAssocRef) {
		NodeRef node = childAssocRef.getChildRef();
		NodeService nodeService = serviceRegistry.getNodeService();

		String statusUUID = GUID.generate();
		nodeService.setProperty(node, PROP_STATUS_UUID, statusUUID);

		HashMap<QName, Serializable> props = new HashMap<QName, Serializable>(1, 1.0f);
		props.put(ContentModel.PROP_NAME, "actions");

		NodeRef statusFolder = nodeService.createNode(
				node,
				ContentModel.ASSOC_CONTAINS,
				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "actions"),
				TYPE_ACTIONS,
				props).getChildRef();

		props.put(ContentModel.PROP_NAME, "roles");
		NodeRef roles = nodeService.createNode(
				node,
				ContentModel.ASSOC_CONTAINS,
				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "roles"),
				TYPE_ROLES,
				props).getChildRef();

		props.put(ContentModel.PROP_NAME, "static");
		NodeRef staticRoles = nodeService.createNode(
				roles,
				ContentModel.ASSOC_CONTAINS,
				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "static"),
				TYPE_ROLES,
				props).getChildRef();

		props.put(ContentModel.PROP_NAME, "dynamic");
		NodeRef dynamicRoles = nodeService.createNode(
				roles,
				ContentModel.ASSOC_CONTAINS,
				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "dynamic"),
				TYPE_ROLES,
				props).getChildRef();

		nodeService.setProperty(node, PROP_STATIC_ROLES, staticRoles.toString());
		nodeService.setProperty(node, PROP_DYNAMIC_ROLES, dynamicRoles.toString());

		//Добавляем действия к статусу
		List<String> actions = stateMachineActions.getActions("start");
		createActions(node, statusFolder, actions, "start");
		actions = stateMachineActions.getActions("take");
		createActions(node, statusFolder, actions, "take");
		actions = stateMachineActions.getActions("end");
		createActions(node, statusFolder, actions, "end");
	}

	private void createActions(NodeRef status, NodeRef statusesFolder, List<String> actions, String execution) {
		NodeService nodeService = serviceRegistry.getNodeService();

		for (String action : actions) {
			HashMap<QName, Serializable> props = new HashMap<QName, Serializable>(1, 1.0f);
			props.put(PROP_ACTION_ID, action);
			props.put(PROP_ACTION_EXECUTION, execution);
			ChildAssociationRef childAssocRef = nodeService.createNode(
				statusesFolder,
				ContentModel.ASSOC_CONTAINS,
				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, action),
				QName.createQName(STATEMACHINE_URI, action),
				props);
			if (action.equals(StateMachineActions.getActionName(FinishStateWithTransitionAction.class))) {
				nodeService.setProperty(status, QName.createQName(STATEMACHINE_URI, execution + action + "User"), childAssocRef.getChildRef().toString());
				nodeService.setProperty(status, QName.createQName(STATEMACHINE_URI, execution + action + "Workflow"), childAssocRef.getChildRef().toString());
			} else {
				nodeService.setProperty(status, QName.createQName(STATEMACHINE_URI, execution + action), childAssocRef.getChildRef().toString());
			}
		}
	}

}
