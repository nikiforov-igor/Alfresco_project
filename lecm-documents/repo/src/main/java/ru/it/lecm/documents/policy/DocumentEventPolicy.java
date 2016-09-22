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
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
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
    	logger.debug("!!!!!!!!! onCreateAssociation");
        NodeRef node = nodeAssocRef.getSourceRef();
        if (nodeService.exists(node) && nodeService.hasAspect(node, DocumentEventService.ASPECT_EVENT_LISTENERS) && !nodeAssocRef.getTypeQName().equals(DocumentEventService.ASSOC_EVENT_LISTENERS)) {
            fireEvent(node);
        }
    }

    @Override
    public void onCreateChildAssociation(ChildAssociationRef childAssocRef, boolean isNewNode) {
    	logger.debug("!!!!!!!!! onCreateChildAssociation childAssocRef: "+childAssocRef+" ,isNewNode: "+isNewNode);
        NodeRef node = childAssocRef.getParentRef();
        if (nodeService.exists(node) && nodeService.hasAspect(node, DocumentEventService.ASPECT_EVENT_LISTENERS)) {
            fireEvent(node);
        }
    }

    @Override
    public void onDeleteAssociation(AssociationRef nodeAssocRef) {
    	logger.debug("!!!!!!!!! onDeleteAssociation");
        NodeRef node = nodeAssocRef.getSourceRef();
        if (nodeService.exists(node) && nodeService.hasAspect(node, DocumentEventService.ASPECT_EVENT_LISTENERS) && !nodeAssocRef.getTypeQName().equals(DocumentEventService.ASSOC_EVENT_LISTENERS)) {
            fireEvent(node);
        }
    }

    @Override
    public void onDeleteChildAssociation(ChildAssociationRef childAssocRef) {
    	logger.debug("!!!!!!!!! onDeleteChildAssociation");
        NodeRef node = childAssocRef.getParentRef();
        if (nodeService.exists(node) && nodeService.hasAspect(node, DocumentEventService.ASPECT_EVENT_LISTENERS)) {
            fireEvent(node);
        }
    }

    @Override
    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
    	logger.debug("!!!!!!!!! onUpdateProperties");
        fireEvent(nodeRef);
    }

    private void fireEvent(NodeRef node) {
    	logger.debug("!!!!!!!!! fireEvent");
        AlfrescoTransactionSupport.bindListener(this.transactionListener);
        getPostTxnNodes().add(node);
    }
    
    private Set<NodeRef> getPostTxnNodes(){
		@SuppressWarnings("unchecked")
		Set<NodeRef> pendingActions = (Set<NodeRef>)AlfrescoTransactionSupport.getResource(DOCUMENT_EVENTS_TRANSACTION_LISTANER);
		if (pendingActions == null) {
			pendingActions = new LinkedHashSet<NodeRef>(11);
			AlfrescoTransactionSupport.bindResource(DOCUMENT_EVENTS_TRANSACTION_LISTANER, pendingActions);
		}
		return pendingActions;
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
        	 logger.debug("!!!!!!!!! afterCommit");
        	 for(final NodeRef nodeRef : getPostTxnNodes()) {
//                	 logger.debug("!!!!!!!!! afterCommit pendingDocs: "+pendingDocs);
                    	 List<AssociationRef> listeners = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<List<AssociationRef>>() {
                             @Override
                             public List<AssociationRef> execute() throws Throwable {
                            	 if (nodeService.exists(nodeRef)) {
                            		 return nodeService.getTargetAssocs(nodeRef, DocumentEventService.ASSOC_EVENT_LISTENERS);
                            	 }
                            	 return new ArrayList<AssociationRef>();
                             }
                         },true,true);
                    	 logger.debug("!!!!!!!!! afterCommit listeners: "+listeners);
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
                                                             senders = nodeRef.toString();
                                                         } else {
                                                             senders += "," + nodeRef.toString();
                                                         }
                                                         logger.debug("!!!!!!!!! afterCommit senders: "+senders);
                                                         nodeService.setProperty(node, DocumentEventService.PROP_EVENT_SENDER, senders);
                                                         return null;
                                                     }
                                                 }, false,true);
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

        @Override
        public void afterRollback() {

        }
    }
}
