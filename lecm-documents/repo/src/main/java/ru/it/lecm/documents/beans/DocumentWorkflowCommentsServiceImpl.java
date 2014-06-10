package ru.it.lecm.documents.beans;

import org.alfresco.model.ContentModel;
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
import ru.it.lecm.base.beans.WriteTransactionNeededException;

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
		//TODO Рефакторинг AL-2733
		return nodeService.getChildByName(documentRef, ContentModel.ASSOC_CONTAINS, DOCUMENT_WORKFLOW_COMMENTS_ROOT_NAME);
	}

//	@Override
//	public NodeRef getOrCreateRootFolder(final NodeRef documentRef) throws WriteTransactionNeededException {
//		//TODO Рефакторинг AL-2733
//		NodeRef rootFolder;
//		rootFolder = nodeService.getChildByName(documentRef, ContentModel.ASSOC_CONTAINS, DOCUMENT_WORKFLOW_COMMENTS_ROOT_NAME);
//
//		if (null == rootFolder){
//			rootFolder = createRootFolder(documentRef);
//		}
//
//		return rootFolder;
//	}

	@Override
	public NodeRef createRootFolder(final NodeRef documentRef) throws WriteTransactionNeededException {
		//TODO Рефакторинг AL-2733
		return createFolder(documentRef, DOCUMENT_WORKFLOW_COMMENTS_ROOT_NAME);
	}

	@Override
	public NodeRef createWorkflowComment(final NodeRef documentRef, String comment) {
		QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
		QName assocQName = QName.createQName (NamespaceService.CONTENT_MODEL_1_0_URI, UUID.randomUUID().toString());

		Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
		properties.put(PROP_DOCUMENT_WORKFLOW_COMMENT_TEXT, comment);

		NodeRef rootNode = getRootFolder(documentRef);

		//TODO Рефакторинг AL-2733
		if(rootNode == null) {
			try {
				rootNode = createRootFolder(documentRef);
			} catch (WriteTransactionNeededException ex) {
				throw new RuntimeException(ex);
			}
		}

		ChildAssociationRef associationRef = nodeService.createNode(rootNode, assocTypeQName, assocQName, TYPE_DOCUMENT_WORKFLOW_COMMENT, properties);
		return associationRef.getChildRef();
	}
}
