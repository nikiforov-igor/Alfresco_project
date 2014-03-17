package ru.it.lecm.documents.policy;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.TransactionListener;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.documents.beans.DocumentEventService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * User: pmelnikov
 * Date: 13.03.14
 * Time: 10:22
 */
public class DocumentEventPolicy implements NodeServicePolicies.OnUpdatePropertiesPolicy, NodeServicePolicies.OnCreateAssociationPolicy, NodeServicePolicies.OnDeleteAssociationPolicy, NodeServicePolicies.OnCreateChildAssociationPolicy, NodeServicePolicies.OnDeleteChildAssociationPolicy {

    private PolicyComponent policyComponent;
    private NodeService nodeService;
    private ThreadPoolExecutor threadPoolExecutor;
    private TransactionListener transactionListener;
    private TransactionService transactionService;

    private static final String DOCUMENT_EVENTS_TRANSACTION_LISTANER = "document_events_transaction_listaner";

    final static Logger logger = LoggerFactory.getLogger(DocumentEventPolicy.class);


    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
    }

    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public final void init() {

        transactionListener = new DocumentEventPolicyTransactionListener();

        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME, ContentModel.TYPE_CMOBJECT, new JavaBehaviour(this, "onUpdateProperties"));

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME, ContentModel.TYPE_CMOBJECT, new JavaBehaviour(this, "onCreateAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateChildAssociationPolicy.QNAME, ContentModel.TYPE_CMOBJECT, new JavaBehaviour(this, "onCreateChildAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME, ContentModel.TYPE_CMOBJECT, new JavaBehaviour(this, "onDeleteAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteChildAssociationPolicy.QNAME, ContentModel.TYPE_CMOBJECT, new JavaBehaviour(this, "onDeleteChildAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

    }

    @Override
    public void onCreateAssociation(AssociationRef nodeAssocRef) {
        NodeRef node = nodeAssocRef.getSourceRef();
        if (nodeService.exists(node) && nodeService.hasAspect(node, DocumentEventService.ASPECT_EVENT_LISTENERS) && !nodeAssocRef.getTypeQName().equals(DocumentEventService.ASSOC_EVENT_LISTENERS)) {
            fireEvent(node);
        }
    }

    @Override
    public void onCreateChildAssociation(ChildAssociationRef childAssocRef, boolean isNewNode) {
        NodeRef node = childAssocRef.getParentRef();
        if (nodeService.hasAspect(node, DocumentEventService.ASPECT_EVENT_LISTENERS)) {
            fireEvent(node);
        }
    }

    @Override
    public void onDeleteAssociation(AssociationRef nodeAssocRef) {
        NodeRef node = nodeAssocRef.getSourceRef();
        if (nodeService.hasAspect(node, DocumentEventService.ASPECT_EVENT_LISTENERS) && !nodeAssocRef.getTypeQName().equals(DocumentEventService.ASSOC_EVENT_LISTENERS)) {
            fireEvent(node);
        }
    }

    @Override
    public void onDeleteChildAssociation(ChildAssociationRef childAssocRef) {
        NodeRef node = childAssocRef.getParentRef();
        if (nodeService.hasAspect(node, DocumentEventService.ASPECT_EVENT_LISTENERS)) {
            fireEvent(node);
        }
    }

    @Override
    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        fireEvent(nodeRef);
    }

    private void fireEvent(NodeRef node) {
        AlfrescoTransactionSupport.bindListener(this.transactionListener);

        // Add the pending action to the transaction resource
        List<NodeRef> pendingActions = AlfrescoTransactionSupport.getResource(DOCUMENT_EVENTS_TRANSACTION_LISTANER);
        if (pendingActions == null) {
            pendingActions = new ArrayList<NodeRef>();
            AlfrescoTransactionSupport.bindResource(DOCUMENT_EVENTS_TRANSACTION_LISTANER, pendingActions);
        }

        // Check that action has only been added to the list once
        if (!pendingActions.contains(node)) {
            pendingActions.add(node);
        }

    }

    private class DocumentEventPolicyTransactionListener implements TransactionListener {

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
            List<NodeRef> pendingDocs = AlfrescoTransactionSupport.getResource(DOCUMENT_EVENTS_TRANSACTION_LISTANER);
            if (pendingDocs != null) {
                while (!pendingDocs.isEmpty()) {
                    final NodeRef docRef = pendingDocs.remove(0);
                    if (nodeService.exists(docRef)) {
                        List<AssociationRef> listeners = nodeService.getTargetAssocs(docRef, DocumentEventService.ASSOC_EVENT_LISTENERS);
                        for (AssociationRef listener : listeners) {
                            final NodeRef node = listener.getTargetRef();
                            Runnable runnable = new Runnable() {
                                public void run() {
                                    try {
                                        AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Void>() {
                                            @Override
                                            public Void doWork() throws Exception {
                                                return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>() {
                                                    @Override
                                                    public Void execute() throws Throwable {
                                                        String senders = nodeService.getProperty(node, DocumentEventService.PROP_EVENT_SENDER).toString();
                                                        if ("".equals(senders)) {
                                                            senders = docRef.toString();
                                                        } else {
                                                            senders += "," + docRef.toString();
                                                        }
                                                        nodeService.setProperty(node, DocumentEventService.PROP_EVENT_SENDER, senders);
                                                        return null;
                                                    }
                                                }, false, true);
                                            }
                                        });
                                    } catch (Exception e) {
                                        logger.error("Error while send document events", e);
                                    }
                                }
                            };
                            threadPoolExecutor.execute(runnable);
                        }
                    }
                }
            }
        }

        @Override
        public void afterRollback() {

        }
    }
}
