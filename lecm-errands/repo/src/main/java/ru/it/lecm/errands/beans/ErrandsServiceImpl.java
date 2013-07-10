package ru.it.lecm.errands.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.errands.ErrandsService;

import java.io.Serializable;
import java.util.*;

/**
 * User: AIvkin
 * Date: 10.07.13
 * Time: 11:43
 */
public class ErrandsServiceImpl extends BaseBean implements ErrandsService {
	private static enum ModeChoosingExecutors {
		ORGANIZATION,
		UNIT
	}

	private DocumentService documentService;

	private final Object lock = new Object();

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return getFolder(ERRANDS_ROOT_ID);
	}

	public NodeRef getDraftRoot() {
		return  documentService.getDraftRootByType(TYPE_ERRANDS);
	}

	public NodeRef getSettingsNode() {
		final NodeRef draftRoot = this.getServiceRootFolder();

		NodeRef settings = nodeService.getChildByName(draftRoot, ContentModel.ASSOC_CONTAINS, ERRANDS_SETTINGS_NODE_NAME);
		if (settings != null) {
			return settings;
		} else {
			AuthenticationUtil.RunAsWork<NodeRef> raw = new AuthenticationUtil.RunAsWork<NodeRef>() {
				@Override
				public NodeRef doWork() throws Exception {
					return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
						@Override
						public NodeRef execute() throws Throwable {
							NodeRef settingsRef;
							synchronized (lock) {
								settingsRef = nodeService.getChildByName(draftRoot, ContentModel.ASSOC_CONTAINS, ERRANDS_SETTINGS_NODE_NAME);
								if (settingsRef == null) {
									QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
									QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, ERRANDS_SETTINGS_NODE_NAME);
									QName nodeTypeQName = TYPE_ERRANDS_SETTINGS;

									Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
									properties.put(ContentModel.PROP_NAME, ERRANDS_SETTINGS_NODE_NAME);
									ChildAssociationRef associationRef = nodeService.createNode(draftRoot, assocTypeQName, assocQName, nodeTypeQName, properties);
									settingsRef = associationRef.getChildRef();
								}
							}
							return settingsRef;
						}
					});
				}
			};
			return AuthenticationUtil.runAsSystem(raw);
		}
	}

	public ModeChoosingExecutors getModeChoosingExecutors() {
		NodeRef settings = getSettingsNode();
		if (settings != null) {
		 	String modeChoosingExecutors = (String) nodeService.getProperty(settings, SETTINGS_PROP_MODE_CHOOSING_EXECUTORS);
			if (modeChoosingExecutors.equals(SETTINGS_PROP_MODE_CHOOSING_EXECUTORS_ORGANIZATION)) {
				return ModeChoosingExecutors.ORGANIZATION;
			}
		}
		return ModeChoosingExecutors.UNIT;
	}
}
