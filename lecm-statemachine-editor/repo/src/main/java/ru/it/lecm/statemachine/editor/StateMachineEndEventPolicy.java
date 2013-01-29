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

	public final static String STATEMACHINE_URI = "http://www.it.ru/logicECM/statemachine/editor/1.0";
	public final static QName TYPE_END_EVENT = QName.createQName(STATEMACHINE_URI, "endEvent");
	public final static QName PROP_ACTION_ID = QName.createQName(STATEMACHINE_URI, "actionId");
	public final static QName PROP_ACTION_EXECUTION = QName.createQName(STATEMACHINE_URI, "actionExecution");
	public final static QName TYPE_ACTIONS = QName.createQName(STATEMACHINE_URI, "actions");

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
				TYPE_END_EVENT, new JavaBehaviour(this, "onCreateNode"));

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
				TYPE_ACTIONS,
				props).getChildRef();


		//Добавляем действия к завершающему событию
		String actionName = "ScriptAction";
		HashMap<QName, Serializable> actionProps = new HashMap<QName, Serializable>(1, 1.0f);
		props.put(PROP_ACTION_ID, actionName);
		props.put(PROP_ACTION_EXECUTION, "end");
		childAssocRef = nodeService.createNode(
				actionsFolder,
				ContentModel.ASSOC_CONTAINS,
				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, actionName),
				QName.createQName(STATEMACHINE_URI, actionName),
				props);
		nodeService.setProperty(node, QName.createQName(STATEMACHINE_URI, "endEvent" + actionName), childAssocRef.getChildRef().toString());
	}

}
