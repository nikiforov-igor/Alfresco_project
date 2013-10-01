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
import java.util.UUID;

/**
 * User: AIvkin
 * Date: 30.07.13
 * Time: 10:47
 */
public class DocumentWorkflowCommentsServiceImpl extends BaseBean implements DocumentWorkflowCommentsService {
	private final static Logger logger = LoggerFactory.getLogger(DocumentWorkflowCommentsServiceImpl.class);

	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}

	@Override
	public NodeRef getRootFolder(final NodeRef documentRef) {
		final String rootName = DOCUMENT_WORKFLOW_COMMENTS_ROOT_NAME;

		AuthenticationUtil.RunAsWork<NodeRef> raw = new AuthenticationUtil.RunAsWork<NodeRef>() {
			@Override
			public NodeRef doWork() throws Exception {
				return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
					@Override
					public NodeRef execute() throws Throwable {
						NodeRef attachmentsRef = nodeService.getChildByName(documentRef, ContentModel.ASSOC_CONTAINS, rootName);
							if (attachmentsRef == null) {
								QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
								QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, rootName);
								QName nodeTypeQName = ContentModel.TYPE_FOLDER;

								Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
								properties.put(ContentModel.PROP_NAME, rootName);
								ChildAssociationRef associationRef = nodeService.createNode(documentRef, assocTypeQName, assocQName, nodeTypeQName, properties);
								attachmentsRef = associationRef.getChildRef();
							}
						return attachmentsRef;
					}
				});
			}
		};
		return AuthenticationUtil.runAsSystem(raw);
	}

	@Override
	public NodeRef createWorkflowComment(NodeRef documentRef, String comment) {
		QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
		QName assocQName = QName.createQName (NamespaceService.CONTENT_MODEL_1_0_URI, UUID.randomUUID().toString());

		Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
		properties.put(PROP_DOCUMENT_WORKFLOW_COMMENT_TEXT, comment);

		NodeRef rootNode = getRootFolder(documentRef);
		ChildAssociationRef associationRef = nodeService.createNode(rootNode, assocTypeQName, assocQName, TYPE_DOCUMENT_WORKFLOW_COMMENT, properties);
		return associationRef.getChildRef();
	}
}
