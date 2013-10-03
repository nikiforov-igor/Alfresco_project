package ru.it.lecm.statemachine.policy;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.TransactionListener;
import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.workflow.WorkflowDefinition;
import org.alfresco.service.cmr.workflow.WorkflowPath;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.statemachine.StateMachineHelper;
import ru.it.lecm.statemachine.StatemachineModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * User: PMelnikov
 * Date: 17.10.12
 * Time: 11:08
 */
public class StateMachineCreateDocumentPolicy implements NodeServicePolicies.OnCreateNodePolicy {

    private static final String STM_POST_TRANSACTION_PENDING_DOCS = "stm_post_transaction_pending_docs";
    private ServiceRegistry serviceRegistry;
	private PolicyComponent policyComponent;
    private ThreadPoolExecutor threadPoolExecutor;
    private TransactionListener transactionListener;
    private BusinessJournalService businessJournalService;

    final static Logger logger = LoggerFactory.getLogger(StateMachineCreateDocumentPolicy.class);

    public void setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

    public void setBusinessJournalService(BusinessJournalService businessJournalService) {
        this.businessJournalService = businessJournalService;
    }

    public final void init() {
		logger.debug( "Installing Policy ...");

		PropertyCheck.mandatory(this, "serviceRegistry", serviceRegistry);
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);
        transactionListener = new StateMachineTransactionListener();
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME, StatemachineModel.TYPE_CONTENT, new JavaBehaviour(this, "onCreateNode", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
	}

	@Override
	public void onCreateNode(final ChildAssociationRef childAssocRef) {
        final NodeRef docRef = childAssocRef.getChildRef();
        final NodeService nodeService = serviceRegistry.getNodeService();
        //append status aspect to new document
        HashMap<QName, Serializable> aspectProps = new HashMap<QName, Serializable>();
        aspectProps.put(StatemachineModel.PROP_STATUS, "Новый");
        nodeService.addAspect(docRef, StatemachineModel.ASPECT_STATUS, aspectProps);
        // Ensure that the transaction listener is bound to the transaction
        AlfrescoTransactionSupport.bindListener(this.transactionListener);

        // Add the pending action to the transaction resource
        List<NodeRef> pendingActions = AlfrescoTransactionSupport
                .getResource(STM_POST_TRANSACTION_PENDING_DOCS);
        if (pendingActions == null) {
            pendingActions = new ArrayList<NodeRef>();
            AlfrescoTransactionSupport.bindResource(STM_POST_TRANSACTION_PENDING_DOCS, pendingActions);
        }

        // Check that action has only been added to the list once
        if (!pendingActions.contains(docRef)) {
            pendingActions.add(docRef);
        }
    }

    private class StateMachineTransactionListener implements TransactionListener
    {

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
            final NodeService nodeService = serviceRegistry.getNodeService();
            final String currentUser = AuthenticationUtil.getFullyAuthenticatedUser();
            List<NodeRef> pendingDocs = AlfrescoTransactionSupport.getResource(STM_POST_TRANSACTION_PENDING_DOCS);
            if (pendingDocs != null) {
                while (!pendingDocs.isEmpty()) {
                    final NodeRef docRef = pendingDocs.remove(0);
                    if (docRef != null) {
                        final QName type = nodeService.getType(docRef);
                        List<String> prefixes = (List<String>) serviceRegistry.getNamespaceService().getPrefixes(type.getNamespaceURI());
                        final String stateMashineId = prefixes.get(0) + "_" + type.getLocalName();
                        Runnable runnable = new Runnable() {
                            public void run() {
                                AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Void>() {
                                    @Override
                                    public Void doWork() throws Exception {
                                        return serviceRegistry.getTransactionService().getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>() {
                                            @Override
                                            public Void execute() throws Throwable {
                                                PersonService personService = serviceRegistry.getPersonService();
                                                NodeRef assigneeNodeRef = personService.getPerson("workflow");

                                                Map<QName, Serializable> workflowProps = new HashMap<QName, Serializable>(16);

                                                WorkflowService workflowService = serviceRegistry.getWorkflowService();

                                                NodeRef stateProcessPackage = workflowService.createPackage(null);
                                                nodeService.addChild(stateProcessPackage, docRef, ContentModel.ASSOC_CONTAINS, type);

                                                workflowProps.put(WorkflowModel.ASSOC_PACKAGE, stateProcessPackage);
                                                workflowProps.put(WorkflowModel.ASSOC_ASSIGNEE, assigneeNodeRef);

                                                workflowProps.put(QName.createQName("{}stm_document"), docRef);
                                                serviceRegistry.getPermissionService().setPermission(docRef, "workflow",
                                                        PermissionService.ALL_PERMISSIONS, true);

                                                // get the moderated workflow
                                                WorkflowDefinition wfDefinition = workflowService.getDefinitionByName("activiti$" + stateMashineId);
                                                if (wfDefinition == null) {
                                                    wfDefinition = workflowService.getDefinitionByName("activiti$default_statemachine");
                                                }
                                                if (wfDefinition == null) {
                                                    throw new IllegalStateException("no workflow: " + stateMashineId);
                                                }
                                                // start the workflow

                                                AuthenticationUtil.setFullyAuthenticatedUser("workflow");
                                                WorkflowPath path = null;
                                                try {
                                                    path = workflowService.startWorkflow(wfDefinition.getId(), workflowProps);
                                                } catch (Exception e) {
                                                    logger.error("Error while start statemachine", e);
                                                } finally {
                                                    AuthenticationUtil.setFullyAuthenticatedUser(currentUser);
                                                }
                                                HashMap<QName, Serializable> aspectProps = new HashMap<QName, Serializable>();
                                                aspectProps.put(StatemachineModel.PROP_STATEMACHINE_ID, path.getInstance().getId());
                                                nodeService.addAspect(docRef, StatemachineModel.ASPECT_STATEMACHINE, aspectProps);

                                                HashMap<QName, Serializable> properties = new HashMap<QName, Serializable>(1, 1.0f);
                                                properties.put(ContentModel.PROP_OWNER, AuthenticationUtil.SYSTEM_USER_NAME);
                                                nodeService.addAspect(docRef, ContentModel.ASPECT_OWNABLE, properties);

                                                List<WorkflowTask> tasks = workflowService.getTasksForWorkflowPath(path.getId());
                                                for (WorkflowTask task : tasks) {
                                                    workflowService.endTask(task.getId(), null);
                                                }
                                                StateMachineHelper helper = new StateMachineHelper();
                                                helper.executePostponedActions(path.getInstance().getId());

                                                String status = (String) nodeService.getProperty(docRef, StatemachineModel.PROP_STATUS);
                                                List<String> objects = new ArrayList<String>(1);
                                                if (status != null) {
                                                    objects.add(status);
                                                }
                                                businessJournalService.log(docRef, EventCategory.ADD, "#initiator создал(а) новый документ \"#mainobject\" в статусе \"#object1\"", objects);
                                                return null;
                                            }
                                        }, false, true);
                                    }
                                }, currentUser);
                            }
                        };

                        threadPoolExecutor.execute(runnable);
                    }
                }
            }
        }

        @Override
        public void afterRollback() {

        }
    }
}
