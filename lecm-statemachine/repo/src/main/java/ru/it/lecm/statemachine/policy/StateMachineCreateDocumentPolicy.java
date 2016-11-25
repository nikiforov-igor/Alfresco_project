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
import org.alfresco.service.cmr.repository.DuplicateChildNodeNameException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.workflow.WorkflowDefinition;
import org.alfresco.service.cmr.workflow.WorkflowPath;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.RepositoryStructureHelper;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.statemachine.StateMachineServiceBean;
import ru.it.lecm.statemachine.StatemachineModel;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.documents.beans.DocumentConnectionService;

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
	private DocumentConnectionService documentConnectionService;
    private DocumentService documentService;
    private RepositoryStructureHelper repositoryStructureHelper;

    public void setRepositoryStructureHelper(RepositoryStructureHelper repositoryStructureHelper) {
        this.repositoryStructureHelper = repositoryStructureHelper;
    }

    public void setDocumentConnectionService(DocumentConnectionService documentConnectionService) {
		this.documentConnectionService = documentConnectionService;
	}

    final static Logger logger = LoggerFactory.getLogger(StateMachineCreateDocumentPolicy.class);
    private StateMachineServiceBean stateMachineHelper;

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

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
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
		logger.debug("!!!!!!!! onCreateNode");
        final NodeRef docRef = childAssocRef.getChildRef();
        final NodeService nodeService = serviceRegistry.getNodeService();
        final QName type = nodeService.getType(docRef);

        serviceRegistry.getPermissionService().setPermission(docRef, "ORGUNIT", "Read", false);

        //append status aspect to new document
        HashMap<QName, Serializable> aspectProps = new HashMap<QName, Serializable>();
        aspectProps.put(StatemachineModel.PROP_STATUS, "Новый");
        nodeService.addAspect(docRef, StatemachineModel.ASPECT_STATUS, aspectProps);

        //Вынесено создание папки "Связи"
        try {
            documentConnectionService.createRootFolder(docRef);
        } catch (WriteTransactionNeededException ex) {
            logger.error("Cannot create connections root folder", ex);
        }

        if (!stateMachineHelper.isSimpleDocument(type)) {
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
        } else {
            AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
                @Override
                public Object doWork() throws Exception {
					String archiveFolderStr = stateMachineHelper.getArchiveFolder(type);
					
                    String rootFolder = documentService.execStringExpression(docRef, archiveFolderStr);
                    if (rootFolder == null) {
                        rootFolder = "/Документы без МС";
                    }
					
					stateMachineHelper.checkArchiveFolder(type, false);

                    NodeRef archiveFolder = repositoryStructureHelper.getCompanyHomeRef();
                    //Создаем основной путь до папки
                    try {
                        StringTokenizer tokenizer = new StringTokenizer(rootFolder, "/");
                        while (tokenizer.hasMoreTokens()) {
                            String folderName = tokenizer.nextToken();
                            if (!"".equals(folderName)) {
                                NodeRef folder = nodeService.getChildByName(archiveFolder, ContentModel.ASSOC_CONTAINS, folderName);
                                if (folder == null) {
                                    folder = createFolder(archiveFolder, folderName);
                                }
                                archiveFolder = folder;
                            }
                        }
                    } catch (Exception e) {
                        logger.error("Error while create type folder", e);
                    }

                    String name = nodeService.getProperty(docRef, ContentModel.PROP_NAME).toString();
                    QName storeQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name);

                    nodeService.moveNode(docRef, archiveFolder, ContentModel.ASSOC_CONTAINS, storeQName);
                    return null;
                }
            });
        }
    }

    private NodeRef createFolder(final NodeRef parent, final String name) {
        final NodeService nodeService = serviceRegistry.getNodeService();
        final HashMap<QName, Serializable> props = new HashMap<QName, Serializable>(1, 1.0f);
        props.put(ContentModel.PROP_NAME, name);
        try {
//            ChildAssociationRef childAssocRef = serviceRegistry.getTransactionService().getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<ChildAssociationRef>() {
//                @Override
//                public ChildAssociationRef execute() throws Throwable {
//                    return 
        	ChildAssociationRef childAssocRef = nodeService.createNode(
                            parent,
                            ContentModel.ASSOC_CONTAINS,
                            QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName(name)),
                            ContentModel.TYPE_FOLDER,
                            props);
//                }
//            }, false, true);
            return childAssocRef.getChildRef();
        } catch(DuplicateChildNodeNameException e) {
            return nodeService.getChildByName(parent, ContentModel.ASSOC_CONTAINS, name);
        } catch(Exception e) {
            return nodeService.getChildByName(parent, ContentModel.ASSOC_CONTAINS, name);
        }
    }

    public void setStateMachineHelper(StateMachineServiceBean stateMachineHelper) {
        this.stateMachineHelper = stateMachineHelper;
    }

    private class StateMachineTransactionListener implements TransactionListener
    {

        @Override
        public void flush() {
        	logger.debug("!!!!!!!! flush");
        }

        @Override
        public void beforeCommit(boolean readOnly) {
        	logger.debug("!!!!!!!! beforeCommit");
        }

        @Override
        public void beforeCompletion() {
        	logger.debug("!!!!!!!! beforeCompletion");
        }

        @Override
        public void afterCommit() {
        	logger.debug("!!!!!!!! afterCommit");
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
                            	//TODO transaction in loop!!!
                            	serviceRegistry.getTransactionService().getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>() {
                            		@Override
                            		public Void execute() throws Throwable {
                            			return AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Void>() {
                                            @Override
                                            public Void doWork() throws Exception {
                                            	logger.debug("!!!!!!!! afterCommit execute");
                                                PersonService personService = serviceRegistry.getPersonService();
                                                NodeRef assigneeNodeRef = personService.getPerson("workflow");

                                                Map<QName, Serializable> workflowProps = new HashMap<QName, Serializable>(16);

                                                final WorkflowService workflowService = serviceRegistry.getWorkflowService();
                                                logger.debug("!!!!!!!! afterCommit createPackage");
                                                NodeRef stateProcessPackage = workflowService.createPackage(null);
                                                nodeService.addChild(stateProcessPackage, docRef, WorkflowModel.ASSOC_PACKAGE_CONTAINS, type);

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

                                                //AuthenticationUtil.setFullyAuthenticatedUser("workflow");
                                                WorkflowPath path = null;
                                                try {
                                                	logger.debug("!!!!!!!! afterCommit startWorkflow");
                                                    path = workflowService.startWorkflow(wfDefinition.getId(), workflowProps);
                                                } catch (Exception e) {
                                                    logger.error("Error while start statemachine", e);
                                                } finally {
                                                    AuthenticationUtil.setFullyAuthenticatedUser(currentUser);
                                                }
                                                logger.debug("!!!!!!!! afterCommit addAspect");
                                                HashMap<QName, Serializable> aspectProps = new HashMap<QName, Serializable>();
                                                aspectProps.put(StatemachineModel.PROP_STATEMACHINE_ID, path.getInstance().getId());
                                                nodeService.addAspect(docRef, StatemachineModel.ASPECT_STATEMACHINE, aspectProps);

                                                HashMap<QName, Serializable> properties = new HashMap<QName, Serializable>(1, 1.0f);
                                                properties.put(ContentModel.PROP_OWNER, AuthenticationUtil.SYSTEM_USER_NAME);
                                                nodeService.addAspect(docRef, ContentModel.ASPECT_OWNABLE, properties);

                                                final String pathId = path.getId();
                                                AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Void>() {
                                                    @Override
                                                    public Void doWork() throws Exception {
		                                                List<WorkflowTask> tasks = workflowService.getTasksForWorkflowPath(pathId);
		                                                for (WorkflowTask task : tasks) {
		                                                	logger.debug("!!!!!!!! afterCommit endTask");
		                                                    workflowService.endTask(task.getId(), null);
		                                                }
		                                                return null;
                                                    }
                                                });
                                                //stateMachineHelper.executePostponedActions(path.getInstance().getId());

                                                String status = (String) nodeService.getProperty(docRef, StatemachineModel.PROP_STATUS);
                                                List<String> objects = new ArrayList<String>(1);
                                                if (status != null) {
                                                    objects.add(status);
                                                }
                                                businessJournalService.log(docRef, EventCategory.ADD, "#initiator создал(а) новый документ \"#mainobject\" в статусе \"#object1\"", objects);
                                                return null;
                                            }
                                        }, currentUser);
                            		}
                            	}, false, true);
                            }
                        };

                        threadPoolExecutor.execute(runnable);
                    }
                }
            }
            logger.debug("!!!!!!!! afterCommit end");
        }

        @Override
        public void afterRollback() {

        }
    }
}
