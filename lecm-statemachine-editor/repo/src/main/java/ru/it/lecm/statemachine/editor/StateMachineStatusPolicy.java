package ru.it.lecm.statemachine.editor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.TransactionListener;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.util.GUID;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.statemachine.bean.StateMachineActions;

/**
 * User: PMelnikov
 * Date: 16.11.12
 * Time: 14:45
 */
public class StateMachineStatusPolicy implements NodeServicePolicies.OnCreateNodePolicy, NodeServicePolicies.BeforeDeleteNodePolicy {

	private static Logger logger = LoggerFactory.getLogger(StateMachineStatusPolicy.class);
	private static ServiceRegistry serviceRegistry;
	private static PolicyComponent policyComponent;
	private static StateMachineActions stateMachineActions;
	private static DictionaryBean serviceDictionary;
        private TransactionListener transactionListener;
        
        
        private static final String STATE_MACHINE_STATUS_POLICY_TRANSACTION_LISTENER = "state_machine)status_policy_transaction_listener";
        
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

            transactionListener = new StateMachineStatusPolicyTransactionListener();
            
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
        deleteStatusTransitions(nodeRef,  nodeService);
    }
    
    
    private void deleteStatusTransitions(NodeRef status, NodeService nodeService) {
        logger.debug("Delete transitions for "+nodeService.getProperty(status, ContentModel.PROP_NAME)+": "+status.toString());
        List<AssociationRef> incomingTransitions = nodeService.getSourceAssocs(status, RegexQNamePattern.MATCH_ALL);
        logger.debug("Incoming transitions count: "+incomingTransitions.size());
        Queue<NodeRef> pendingRemoveNodes = AlfrescoTransactionSupport.getResource(STATE_MACHINE_STATUS_POLICY_TRANSACTION_LISTENER);
        if (null == pendingRemoveNodes) {
            AlfrescoTransactionSupport.bindListener(this.transactionListener);
            pendingRemoveNodes = new LinkedList<>();
            AlfrescoTransactionSupport.bindResource(STATE_MACHINE_STATUS_POLICY_TRANSACTION_LISTENER, pendingRemoveNodes);
        }
        //удаляем входящие переходы. 
        for (AssociationRef associationRef : incomingTransitions) {
            NodeRef source = associationRef.getSourceRef();
            logger.debug("Processing transition: "+source.toString());
            if (nodeService.hasAspect(source, StatemachineEditorModel.ASPECT_TRANSITION_STATUS) && !nodeService.hasAspect(source, ContentModel.ASPECT_PENDING_DELETE)) {
                logger.debug("Mark transition node for delete: "+source.toString());
                pendingRemoveNodes.add(source);
                //nodeService.deleteNode(source);
            }
        }
        
        //ищем и удаляем исходящие. Они и так уже все помечены на удаление, т.к. находятся внутри удаляемой ноды.
//        Queue<ChildAssociationRef> stack = new LinkedList<>();
//        stack.addAll(nodeService.getChildAssocs(status));
//        while (!stack.isEmpty()) {
//            ChildAssociationRef current = stack.poll();
//            logger.debug("Processing node "+current.getChildRef().toString());
//            List<ChildAssociationRef> subFolders = nodeService.getChildAssocs(current.getChildRef());
//            for (ChildAssociationRef folder : subFolders) {
//                if (nodeService.hasAspect(folder.getChildRef(), StatemachineEditorModel.ASPECT_TRANSITION_STATUS) ) {
//                    logger.debug("Node "+folder.getChildRef().toString() + " is transition");
//                    if (!nodeService.hasAspect(folder.getChildRef(), ContentModel.ASPECT_PENDING_DELETE)) {
//                        logger.debug("Node "+folder.getChildRef().toString() + " deleted");
//                        nodeService.deleteNode(folder.getChildRef()); 
//                    } else {
//                        logger.debug("Node "+folder.getChildRef().toString() + " already marked for delete");
//                    }
//                } else {
//                    stack.add(folder);
//                }
//            }
//        }
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
			if (action.equals(stateMachineActions.getActionName("ru.it.lecm.statemachine.action.finishstate.FinishStateWithTransitionAction"))) {
				nodeService.setProperty(status, QName.createQName(StatemachineEditorModel.STATEMACHINE_EDITOR_URI, execution + action + "User"), childAssocRef.getChildRef().toString());
				nodeService.setProperty(status, QName.createQName(StatemachineEditorModel.STATEMACHINE_EDITOR_URI, execution + action + "Workflow"), childAssocRef.getChildRef().toString());
				nodeService.setProperty(status, QName.createQName(StatemachineEditorModel.STATEMACHINE_EDITOR_URI, execution + action + "Form"), childAssocRef.getChildRef().toString());
				nodeService.setProperty(status, QName.createQName(StatemachineEditorModel.STATEMACHINE_EDITOR_URI, execution + action + "FormTrans"), childAssocRef.getChildRef().toString());
			} else {
				nodeService.setProperty(status, QName.createQName(StatemachineEditorModel.STATEMACHINE_EDITOR_URI, execution + action), childAssocRef.getChildRef().toString());
			}
		}
	}

    private class StateMachineStatusPolicyTransactionListener implements TransactionListener {

        @Override
        public void flush() {
            
        }

        @Override
        public void beforeCommit(boolean readOnly) {
            
        }

        @Override
        public void beforeCompletion() {
            
        }

        @Override
        public void afterCommit() {
            final Queue<NodeRef> pendingDocs = AlfrescoTransactionSupport.getResource(STATE_MACHINE_STATUS_POLICY_TRANSACTION_LISTENER);
            logger.debug("Removing transitions");
            AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Void>() {

                @Override
                public Void doWork() throws Exception {
                    serviceRegistry.getTransactionService().getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>() {
                        
                        @Override
                        public Void execute() throws Throwable {
                            NodeService nodeService = serviceRegistry.getNodeService();
                            while (!pendingDocs.isEmpty()) {                                
                                NodeRef nodeRef = pendingDocs.poll();
                                if (nodeService.exists(nodeRef)) {
                                    logger.debug("Removing transition: "+nodeRef.toString());
                                    nodeService.addAspect(nodeRef, ContentModel.ASPECT_TEMPORARY, null);
                                    nodeService.deleteNode(nodeRef);
                                } else {
                                    logger.debug(nodeRef.toString() + " not exist.");
                                }
                            }
                            return null;
                        }
                    }, false,true);
                    return null;
                }
            });
        }

        @Override
        public void afterRollback() {
            
        }
        
    }

    
}
