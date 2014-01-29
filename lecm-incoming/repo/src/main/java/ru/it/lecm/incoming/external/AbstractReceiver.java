package ru.it.lecm.incoming.external;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.TransactionListener;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.documents.beans.DocumentAttachmentsService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.incoming.beans.IncomingServiceImpl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * User: pmelnikov
 * Date: 28.01.14
 * Time: 9:04
 */
abstract public class AbstractReceiver {

    protected NodeService nodeService;
    private DocumentService documentService;
    private DocumentAttachmentsService documentAttachmentsService;
    private ThreadPoolExecutor threadPoolExecutor;
    private TransactionListener transactionListener;
    private static String TRANSACTION_INCOMING_RECEIVER_DATA = "transaction_incoming_receiver_data";
    private ServiceRegistry serviceRegistry;

    public void init() {
        transactionListener = new ReceiverTransactionListener();
    }

    public abstract void receive(NodeRef document);

    public void store(ExternalIncomingDocument document) {
        AlfrescoTransactionSupport.bindListener(this.transactionListener);

        List<ExternalIncomingDocument> pendingActions = AlfrescoTransactionSupport.getResource(TRANSACTION_INCOMING_RECEIVER_DATA);
        if (pendingActions == null) {
            pendingActions = new ArrayList<ExternalIncomingDocument>();
            AlfrescoTransactionSupport.bindResource(TRANSACTION_INCOMING_RECEIVER_DATA, pendingActions);
        }
        pendingActions.add(document);
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setDocumentAttachmentsService(DocumentAttachmentsService documentAttachmentsService) {
        this.documentAttachmentsService = documentAttachmentsService;
    }

    public void setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    private class ReceiverTransactionListener implements TransactionListener
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
            final List<ExternalIncomingDocument> pendingCreate = AlfrescoTransactionSupport.getResource(TRANSACTION_INCOMING_RECEIVER_DATA);
            final String currentUser = AuthenticationUtil.getFullyAuthenticatedUser();
            if (pendingCreate != null) {
                Runnable runnable = new Runnable() {
                    public void run() {
                        AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Void>() {
                            @Override
                            public Void doWork() throws Exception {
                                return serviceRegistry.getTransactionService().getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>() {
                                    @Override
                                    public Void execute() throws Throwable {
                                        for (ExternalIncomingDocument document : pendingCreate) {
                                            NodeRef incoming = serviceRegistry.getTransactionService().getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
                                                @Override
                                                public NodeRef execute() throws Throwable {
                                                    HashMap<QName, Serializable> props = new HashMap<QName, Serializable>();
                                                    props.put(IncomingServiceImpl.PROP_IS_BY_CHANNEL, true);
                                                    return nodeService.createNode(documentService.getDraftRoot(), ContentModel.ASSOC_CONTAINS, QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "incoming"), IncomingServiceImpl.TYPE_INCOMING, props).getChildRef();
                                                }
                                            }, false, true);

                                            //Добавляем вложения
                                            for (NodeRef attach : document.getContent()) {
                                                nodeService.createAssociation(incoming, attach, DocumentService.ASSOC_TEMP_ATTACHMENTS);
                                            }
                                            //Ассоциируем с типом доставки
                                            if (document.getDeliveryType() != null) {
                                                nodeService.createAssociation(incoming, document.getDeliveryType(), IncomingServiceImpl.ASSOC_DELIVERY_METHOD);
                                            }
                                            //Ассоциируем с адресантом
                                            if (document.getAddresser() != null) {
                                                nodeService.createAssociation(incoming, document.getAddresser(), IncomingServiceImpl.ASSOC_ADDRESSEE);
                                            }
                                            //Ассоциируем с организацией отправителем
                                            if (document.getSenderOrganization() != null) {
                                                nodeService.createAssociation(incoming, document.getSenderOrganization(), IncomingServiceImpl.ASSOC_SENDER);
                                            }
                                        }
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

        @Override
        public void afterRollback() {

        }
    }
}
