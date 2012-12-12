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
import ru.it.lecm.base.statemachine.action.finishstate.FinishStateWithTransitionAction;
import ru.it.lecm.base.statemachine.bean.StateMachineActions;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: PMelnikov
 * Date: 16.11.12
 * Time: 14:45
 */
public class StateMachineStatusPolicy implements NodeServicePolicies.OnUpdatePropertiesPolicy, NodeServicePolicies.OnCreateNodePolicy, NodeServicePolicies.OnDeleteNodePolicy {

	public final static String STATEMACHINE_URI = "http://www.it.ru/logicECM/statemachine/editor/1.0";
	public final static QName TYPE_CONTENT = QName.createQName(STATEMACHINE_URI, "status");
	public final static QName ASSOC_STATUS_FOLDER = QName.createQName(STATEMACHINE_URI, "statusFolder");
	public final static QName PROP_START_STATUS = QName.createQName(STATEMACHINE_URI, "startStatus");
	public final static QName PROP_ACTION_ID = QName.createQName(STATEMACHINE_URI, "actionId");
	public final static QName PROP_ACTION_EXECUTION = QName.createQName(STATEMACHINE_URI, "actionExecution");
	public final static QName PROP_STATUS_UUID = QName.createQName(STATEMACHINE_URI, "statusUUID");

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

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
				TYPE_CONTENT, new JavaBehaviour(this, "onUpdateProperties"));
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				TYPE_CONTENT, new JavaBehaviour(this, "onCreateNode"));
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnDeleteNodePolicy.QNAME,
				TYPE_CONTENT, new JavaBehaviour(this, "onDeleteNode"));

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

		//Проставляем начальный статус, если этот статус первый
		List<ChildAssociationRef> children = nodeService.getChildAssocs(parent);
		if (children.size() == 2) {
			nodeService.setProperty(node, PROP_START_STATUS, true);
		}

		String statusUUID = GUID.generate();
		nodeService.setProperty(node, PROP_STATUS_UUID, statusUUID);

		//Добавляем действия к статусу
		List<String> actions = stateMachineActions.getActions("start");
		createActions(node, actions, "start");
		actions = stateMachineActions.getActions("take");
		createActions(node, actions, "take");
		actions = stateMachineActions.getActions("end");
		createActions(node, actions, "end");

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

	@Override
	public void onDeleteNode(ChildAssociationRef childAssocRef, boolean isNodeArchived) {
	/*
		Заменить на бефоре делит)))
		NodeService nodeService = serviceRegistry.getNodeService();
		Object isStartStatus = nodeService.getProperty(childAssocRef.getChildRef(), PROP_START_STATUS);
		if (isStartStatus != null && (Boolean) isStaStatuson) {
			thr new w IllegalStateException("Can't delete start status");
		*/
	}

	private void createActions(NodeRef status, List<String> actions, String execution) {
		NodeService nodeService = serviceRegistry.getNodeService();

		for (String action : actions) {
			HashMap<QName, Serializable> props = new HashMap<QName, Serializable>(1, 1.0f);
			props.put(PROP_ACTION_ID, action);
			props.put(PROP_ACTION_EXECUTION, execution);
			ChildAssociationRef childAssocRef = nodeService.createNode(
				status,
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
