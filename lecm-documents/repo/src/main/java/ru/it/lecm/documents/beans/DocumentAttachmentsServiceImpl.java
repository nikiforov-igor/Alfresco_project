package ru.it.lecm.documents.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.dictionary.constraint.ListOfValuesConstraint;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.dictionary.ConstraintDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: AIvkin
 * Date: 06.03.13
 * Time: 11:59
 */
public class DocumentAttachmentsServiceImpl extends BaseBean implements DocumentAttachmentsService {
    private final static Logger logger = LoggerFactory.getLogger(DocumentAttachmentsServiceImpl.class);

    private DictionaryService dictionaryService;
    private final Object lock = new Object();

    public void setDictionaryService(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    public NodeRef getRootFolder(final NodeRef documentRef) {
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

                                Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
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

    public List<NodeRef> getCategories(final NodeRef documentRef) {
        List<String> categories = new ArrayList<String>();

        QName type = nodeService.getType(documentRef);
        ConstraintDefinition constraint = dictionaryService.getConstraint(QName.createQName(type.getNamespaceURI(), CONSTRAINT_ATTACHMENT_CATEGORIES));
        if (constraint != null && constraint.getConstraint() != null && (constraint.getConstraint() instanceof ListOfValuesConstraint)) {
            ListOfValuesConstraint psConstraint = (ListOfValuesConstraint) constraint.getConstraint();
            if (psConstraint.getAllowedValues() != null) {
                categories.addAll(psConstraint.getAllowedValues());
            }
        }

        if (categories.size() == 0) {
            categories.add("Основные");
        }

        List<NodeRef> result = new ArrayList<NodeRef>();
        NodeRef attachmentRootRef = getRootFolder(documentRef);
        for (String category: categories) {
            NodeRef categoryFolderRef = getCategoryFolder(category, attachmentRootRef);
            result.add(categoryFolderRef);
        }
        return result;
    }

    public NodeRef getCategoryFolder(final String category, final NodeRef attachmentRootRef) {
        AuthenticationUtil.RunAsWork<NodeRef> raw = new AuthenticationUtil.RunAsWork<NodeRef>() {
            @Override
            public NodeRef doWork() throws Exception {
                return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
                    @Override
                    public NodeRef execute() throws Throwable {
                        NodeRef categoryFolderRef;
                        synchronized (lock) {
                            categoryFolderRef = nodeService.getChildByName(attachmentRootRef, ContentModel.ASSOC_CONTAINS, category);
                            if (categoryFolderRef == null) {
                                QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
                                QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, category);
                                QName nodeTypeQName = ContentModel.TYPE_FOLDER;

                                Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
                                properties.put(ContentModel.PROP_NAME, category);
                                ChildAssociationRef associationRef = nodeService.createNode(attachmentRootRef, assocTypeQName, assocQName, nodeTypeQName, properties);
                                categoryFolderRef = associationRef.getChildRef();
                            }
                        }
                        return categoryFolderRef;
                    }
                });
            }
        };
        return AuthenticationUtil.runAsSystem(raw);
    }
}
