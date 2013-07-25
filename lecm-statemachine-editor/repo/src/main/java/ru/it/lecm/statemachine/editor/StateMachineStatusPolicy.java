package ru.it.lecm.statemachine.editor;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.dictionary.beans.DictionaryBean;
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
public class StateMachineStatusPolicy implements NodeServicePolicies.OnCreateNodePolicy, NodeServicePolicies.BeforeDeleteNodePolicy {

	private static ServiceRegistry serviceRegistry;
	private static PolicyComponent policyComponent;
	private static StateMachineActions stateMachineActions;
	private static DictionaryBean serviceDictionary;

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		StateMachineStatusPolicy.serviceRegistry = serviceRegistry;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		StateMachineStatusPolicy.policyComponent = policyComponent;
	}

	public void setStateMachineActions(StateMachineActions stateMachineActions) {
		StateMachineStatusPolicy.stateMachineActions = stateMachineActions;
	}

	public void setServiceDictionary(DictionaryBean serviceDictionary) {
		StateMachineStatusPolicy.serviceDictionary = serviceDictionary;
	}

	public final void init() {
		PropertyCheck.mandatory(this, "serviceRegistry", serviceRegistry);
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);
		PropertyCheck.mandatory(this, "stateMachineActions", stateMachineActions);

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				StatemachineEditorModel.TYPE_TASK_STATUS, new JavaBehaviour(this, "onCreateNode"));

        policyComponent.bindClassBehaviour(NodeServicePolicies.BeforeDeleteNodePolicy.QNAME,
                StatemachineEditorModel.TYPE_STATUS, new JavaBehaviour(this, "beforeDeleteNode"));

    }

	@Override
	public void onCreateNode(ChildAssociationRef childAssocRef) {
		NodeRef node = childAssocRef.getChildRef();
		NodeService nodeService = serviceRegistry.getNodeService();

		String statusUUID = GUID.generate();
		nodeService.setProperty(node, StatemachineEditorModel.PROP_STATUS_UUID, statusUUID);

		HashMap<QName, Serializable> props = new HashMap<QName, Serializable>(1, 1.0f);
		props.put(ContentModel.PROP_NAME, "actions");

		NodeRef statusFolder = nodeService.createNode(
				node,
				ContentModel.ASSOC_CONTAINS,
				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "actions"),
				StatemachineEditorModel.TYPE_ACTIONS,
				props).getChildRef();

		props.put(ContentModel.PROP_NAME, "roles");
		NodeRef roles = nodeService.createNode(
				node,
				ContentModel.ASSOC_CONTAINS,
				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "roles"),
				StatemachineEditorModel.TYPE_ROLES,
				props).getChildRef();

		props.put(ContentModel.PROP_NAME, "static");
		NodeRef staticRoles = nodeService.createNode(
				roles,
				ContentModel.ASSOC_CONTAINS,
				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "static"),
				StatemachineEditorModel.TYPE_ROLES,
				props).getChildRef();

		props.put(ContentModel.PROP_NAME, "dynamic");
		NodeRef dynamicRolesFolder = nodeService.createNode(
				roles,
				ContentModel.ASSOC_CONTAINS,
				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "dynamic"),
				StatemachineEditorModel.TYPE_ROLES,
				props).getChildRef();

		nodeService.setProperty(node, StatemachineEditorModel.PROP_STATIC_ROLES, staticRoles.toString());
		nodeService.setProperty(node, StatemachineEditorModel.PROP_DYNAMIC_ROLES, dynamicRolesFolder.toString());

		//Добавляем действия к статусу
		List<String> actions = stateMachineActions.getActions("start");
		createActions(node, statusFolder, actions, "start");
		actions = stateMachineActions.getActions("take");
		createActions(node, statusFolder, actions, "take");
		actions = stateMachineActions.getActions("end");
		createActions(node, statusFolder, actions, "end");
	}

    @Override
    public void beforeDeleteNode(NodeRef nodeRef) {
        NodeService nodeService = serviceRegistry.getNodeService();
        NodeRef statuses = nodeService.getPrimaryParent(nodeRef).getParentRef();
        List<ChildAssociationRef> children = nodeService.getChildAssocs(statuses);
        deleteStatusTransitions(nodeRef, children, nodeService);

    }

    private void deleteStatusTransitions(NodeRef status, List<ChildAssociationRef> children, NodeService nodeService) {
        for (ChildAssociationRef child : children) {
            if (nodeService.hasAspect(child.getChildRef(), StatemachineEditorModel.ASPECT_TRANSITION_STATUS)) {
                List<AssociationRef> statusTransition = nodeService.getTargetAssocs(child.getChildRef(), StatemachineEditorModel.ASSOC_TRANSITION_STATUS);
                if (statusTransition.size() > 0 && statusTransition.get(0).getTargetRef().equals(status)) {
                    QName type = nodeService.getType(child.getChildRef());
                    if (type.equals(StatemachineEditorModel.TYPE_TASK_STATUS)) {
                        nodeService.removeAssociation(statusTransition.get(0).getSourceRef(), statusTransition.get(0).getTargetRef(), StatemachineEditorModel.ASSOC_TRANSITION_STATUS);
                    } else {
                        nodeService.deleteNode(child.getChildRef());
                    }
                } else {
                    List<ChildAssociationRef> subChildren = nodeService.getChildAssocs(child.getChildRef());
                    deleteStatusTransitions(status, subChildren, nodeService);
                }
            } else {
                List<ChildAssociationRef> subChildren = nodeService.getChildAssocs(child.getChildRef());
                deleteStatusTransitions(status, subChildren, nodeService);
            }
        }
    }

    private void createActions(NodeRef status, NodeRef statusesFolder, List<String> actions, String execution) {
		NodeService nodeService = serviceRegistry.getNodeService();
		for (String action : actions) {
			HashMap<QName, Serializable> props = new HashMap<QName, Serializable>(1, 1.0f);
			props.put(StatemachineEditorModel.PROP_ACTION_ID, action);
			props.put(StatemachineEditorModel.PROP_ACTION_EXECUTION, execution);
			ChildAssociationRef childAssocRef = nodeService.createNode(
				statusesFolder,
				ContentModel.ASSOC_CONTAINS,
				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, action),
				QName.createQName(StatemachineEditorModel.STATEMACHINE_EDITOR_URI, action),
				props);
			if (action.equals(StateMachineActions.getActionName(FinishStateWithTransitionAction.class))) {
				nodeService.setProperty(status, QName.createQName(StatemachineEditorModel.STATEMACHINE_EDITOR_URI, execution + action + "User"), childAssocRef.getChildRef().toString());
				nodeService.setProperty(status, QName.createQName(StatemachineEditorModel.STATEMACHINE_EDITOR_URI, execution + action + "Workflow"), childAssocRef.getChildRef().toString());
			} else {
				nodeService.setProperty(status, QName.createQName(StatemachineEditorModel.STATEMACHINE_EDITOR_URI, execution + action), childAssocRef.getChildRef().toString());
			}
		}
	}

}
