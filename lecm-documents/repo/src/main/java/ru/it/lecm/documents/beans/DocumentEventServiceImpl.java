package ru.it.lecm.documents.beans;

import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.TransactionListener;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * User: pmelnikov
 * Date: 13.03.14
 * Time: 10:10
 */
public class DocumentEventServiceImpl implements DocumentEventService {

    private NodeService nodeService;
    private BehaviourFilter behaviourFilter;
    private ThreadPoolExecutor threadPoolExecutor;
    private TransactionListener transactionListener;
    private TransactionService transactionService;

    final static Logger logger = LoggerFactory.getLogger(DocumentEventServiceImpl.class);

    private static final String DOCUMENT_EVENTS_SERVICE_TRANSACTION_LISTENER = "document_events_service_transaction_listener";

    public void init() {
        transactionListener = new DocumentEventServiceTransactionListener();
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setBehaviourFilter(BehaviourFilter behaviourFilter) {
        this.behaviourFilter = behaviourFilter;
    }

    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public void setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
    }

    @Override
    public void subscribe(NodeRef object, NodeRef listener) {
        AlfrescoTransactionSupport.bindListener(this.transactionListener);

        // Add the pending action to the transaction resource
        List<DocumentListener> pendingActions = AlfrescoTransactionSupport.getResource(DOCUMENT_EVENTS_SERVICE_TRANSACTION_LISTENER);
        if (pendingActions == null) {
            pendingActions = new ArrayList<DocumentListener>();
            AlfrescoTransactionSupport.bindResource(DOCUMENT_EVENTS_SERVICE_TRANSACTION_LISTENER, pendingActions);
        }

        // Check that action has only been added to the list once
        DocumentListener dl = new DocumentListener(object, listener);
        if (!pendingActions.contains(dl)) {
            pendingActions.add(dl);
        }

    }

    @Override
    public void unsubscribe(NodeRef object, NodeRef listener) {
        if (nodeService.hasAspect(object, ASPECT_EVENT_LISTENERS)) {
            nodeService.removeAssociation(object, listener, ASSOC_EVENT_LISTENERS);
        }
    }

    @Override
    public Set<NodeRef> getEventSenders(NodeRef listener) {
        String sendersStr = (String) nodeService.getProperty(listener, PROP_EVENT_SENDER);
        Set<NodeRef> result = new HashSet<NodeRef>();
        if (null != sendersStr) {
            String[] senders = sendersStr.split(",");
            for (String sender : senders) {
                if (NodeRef.isNodeRef(sender.trim())) {
                    result.add(new NodeRef(sender.trim()));
                }
            }
        }
        return result;
    }

    @Override
    public void removeEventSender(NodeRef listener, NodeRef sender) {
        Set<NodeRef> nodes = getEventSenders(listener);
        nodes.remove(sender);
        String senders = "";
        for (NodeRef node : nodes) {
            if (senders.length() == 0) {
                senders = node.toString();
            } else {
                senders += "," + node.toString();
            }
        }
        try {
            behaviourFilter.disableBehaviour(listener);//блокируем повторный вызов
            nodeService.setProperty(listener, PROP_EVENT_SENDER, senders);
        } finally {
            behaviourFilter.enableBehaviour(listener);
        }
    }

    private class DocumentEventServiceTransactionListener implements TransactionListener {

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
            List<DocumentListener> pendingDocs = AlfrescoTransactionSupport.getResource(DOCUMENT_EVENTS_SERVICE_TRANSACTION_LISTENER);
            if (pendingDocs != null) {
                while (!pendingDocs.isEmpty()) {
                    final DocumentListener dl = pendingDocs.remove(0);
                    Runnable runnable = new Runnable() {
                        public void run() {
                            try {
                                AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Void>() {
                                    @Override
                                    public Void doWork() throws Exception {
                                        return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>() {
                                            @Override
                                            public Void execute() throws Throwable {
                                                if (!nodeService.hasAspect(dl.getObject(), ASPECT_EVENT_LISTENERS)) {
                                                    nodeService.addAspect(dl.getObject(), ASPECT_EVENT_LISTENERS, new HashMap<QName, Serializable>());
                                                }

                                                nodeService.createAssociation(dl.getObject(), dl.getListener(), ASSOC_EVENT_LISTENERS);

                                                if (!nodeService.hasAspect(dl.getListener(), ASPECT_EVENT_SENDER)) {
                                                    HashMap<QName, Serializable> props = new HashMap<QName, Serializable>();
                                                    props.put(PROP_EVENT_SENDER, "");
                                                    nodeService.addAspect(dl.getListener(), ASPECT_EVENT_SENDER, props);
                                                }
                                                return null;
                                            }
                                        }, false, true);
                                    }
                                });
                            } catch (Exception e) {
                                logger.error("Error while subscribe document events", e);
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

    private class DocumentListener {

        private NodeRef object;
        private NodeRef listener;

        private DocumentListener(NodeRef object, NodeRef listener) {
            this.object = object;
            this.listener = listener;
        }

        public NodeRef getObject() {
            return object;
        }

        public NodeRef getListener() {
            return listener;
        }
    }


}
