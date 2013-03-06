package ru.it.lecm.documents.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * User: AIvkin
 * Date: 06.03.13
 * Time: 11:59
 */
public class DocumentAttachmentsServiceImpl extends BaseBean implements DocumentAttachmentsService {
    private final static Logger logger = LoggerFactory.getLogger(DocumentAttachmentsServiceImpl.class);

    private final Object lock = new Object();

    public NodeRef getAttacmentRootFolder(final NodeRef documentRef) {
        final String attachmentsRootName = DOCUMENT_ATTACHMENTS_ROOT_NAME;

        AuthenticationUtil.RunAsWork<NodeRef> raw = new AuthenticationUtil.RunAsWork<NodeRef>() {
            @Override
            public NodeRef doWork() throws Exception {
                return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
                    @Override
                    public NodeRef execute() throws Throwable {
                        NodeRef attachmentsRef;
                        synchronized (lock) {
                            attachmentsRef = nodeService.getChildByName(documentRef, ContentModel.ASSOC_CONTAINS, attachmentsRootName);
                            if (attachmentsRef == null) {
                                QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
                                QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, attachmentsRootName);
                                QName nodeTypeQName = ContentModel.TYPE_FOLDER;

                                Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1); //optional map of properties to keyed by their qualified names
                                properties.put(ContentModel.PROP_NAME, attachmentsRootName);
                                ChildAssociationRef associationRef = nodeService.createNode(documentRef, assocTypeQName, assocQName, nodeTypeQName, properties);
                                attachmentsRef = associationRef.getChildRef();
                            }
                        }
                        return attachmentsRef;
                    }
                });
            }
        };
        return AuthenticationUtil.runAsSystem(raw);
    }
}
