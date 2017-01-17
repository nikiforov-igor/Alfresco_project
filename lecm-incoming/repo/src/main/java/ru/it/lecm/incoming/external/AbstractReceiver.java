package ru.it.lecm.incoming.external;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.incoming.beans.IncomingServiceImpl;

import java.io.Serializable;
import java.util.HashMap;

/**
 * User: pmelnikov
 * Date: 28.01.14
 * Time: 9:04
 */
@Deprecated
abstract public class AbstractReceiver {

    protected NodeService nodeService;
    private DocumentService documentService;
    private ServiceRegistry serviceRegistry;

    public void init() {
    }

    public abstract void receive(NodeRef document);

    public void store(ExternalIncomingDocument document) {
//		TODO: По идее, выполняется только в executor'ах, должны быть в транзакциях.
//        NodeRef incoming = serviceRegistry.getTransactionService().getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
//            @Override
//            public NodeRef execute() throws Throwable {
                HashMap<QName, Serializable> props = new HashMap<QName, Serializable>();
                props.put(IncomingServiceImpl.PROP_IS_BY_CHANNEL, true);
                NodeRef incoming = nodeService.createNode(documentService.getDraftRoot(), ContentModel.ASSOC_CONTAINS, QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "incoming"), IncomingServiceImpl.TYPE_INCOMING, props).getChildRef();
//            }
//        }, false, true);

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

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

}