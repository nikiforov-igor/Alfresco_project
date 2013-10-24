package ru.it.lecm.documents.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.security.LecmPermissionService;

import java.io.Serializable;
import java.util.*;

/**
 * User: AIvkin
 * Date: 18.10.13
 * Time: 13:47
 */
public class DocumentTableServiceImpl extends BaseBean implements DocumentTableService {
	private final static Logger logger = LoggerFactory.getLogger(DocumentTableServiceImpl.class);

	private LecmPermissionService lecmPermissionService;
	private DictionaryService dictionaryService;
	private  NamespaceService namespaceService;

	public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
		this.lecmPermissionService = lecmPermissionService;
	}

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	// в данном бине не используется каталог в /app:company_home/cm:Business platform/cm:LECM/
	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}

	@Override
	public NodeRef getRootFolder(final NodeRef documentRef) {
		this.lecmPermissionService.checkPermission(LecmPermissionService.PERM_CONTENT_LIST, documentRef);

		final String attachmentsRootName = DOCUMENT_TABLES_ROOT_NAME;

		AuthenticationUtil.RunAsWork<NodeRef> raw = new AuthenticationUtil.RunAsWork<NodeRef>() {
			@Override
			public NodeRef doWork() throws Exception {
				return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
					@Override
					public NodeRef execute() throws Throwable {
						NodeRef attachmentsRef = nodeService.getChildByName(documentRef, ContentModel.ASSOC_CONTAINS, attachmentsRootName);
						if (attachmentsRef == null) {
							QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
							QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, attachmentsRootName);
							QName nodeTypeQName = ContentModel.TYPE_FOLDER;

							Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
							properties.put(ContentModel.PROP_NAME, attachmentsRootName);
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
	public boolean isDocumentTableData(NodeRef nodeRef) {
		QName refType = nodeService.getType(nodeRef);
		return refType != null && dictionaryService.isSubClass(refType, TYPE_TABLE_DATA_ROW);
	}

	@Override
	public NodeRef getDocumentByTableData(NodeRef tableDataRef) {
		if (nodeService.exists(tableDataRef)) {
			NodeRef tableDataRoot = nodeService.getPrimaryParent(tableDataRef).getParentRef();
			if (tableDataRoot != null && nodeService.getProperty(tableDataRoot, ContentModel.PROP_NAME).equals(DOCUMENT_TABLES_ROOT_NAME)) {
				NodeRef document = nodeService.getPrimaryParent(tableDataRoot).getParentRef();
				if (document != null) {
					QName testType = nodeService.getType(document);
					Collection<QName> subDocumentTypes = dictionaryService.getSubTypes(DocumentService.TYPE_BASE_DOCUMENT, true);
					if (subDocumentTypes != null && subDocumentTypes.contains(testType)) {
						return document;
					}
				}
			}
		}
		return null;
	}

	private AssociationRef getDocumentAssocByTableData(NodeRef tableDataRef) {
		if (nodeService.exists(tableDataRef)) {
			List<AssociationRef> sourceAssocs = nodeService.getSourceAssocs(tableDataRef, RegexQNamePattern.MATCH_ALL);
			if (sourceAssocs != null && sourceAssocs.size() > 0) {
				for (AssociationRef assoc: sourceAssocs) {
					NodeRef document = assoc.getSourceRef();
					if (document != null) {
						QName testType = nodeService.getType(document);
						Collection<QName> subDocumentTypes = dictionaryService.getSubTypes(DocumentService.TYPE_BASE_DOCUMENT, true);
						if (subDocumentTypes != null && subDocumentTypes.contains(testType)) {
							return assoc;
						}
					}
				}
			}
		}
		return null;
	}

	@Override
	public List<NodeRef> getTableDataTotalRows(NodeRef tableDataRef) {
		AssociationRef documentAssoc = getDocumentAssocByTableData(tableDataRef);
		if (documentAssoc != null) {
			NodeRef document = documentAssoc.getSourceRef();

			return getTableDataTotalRows(document, documentAssoc.getTypeQName(), false);
		}

		return null;
	}

	@Override
	public List<NodeRef> getTableDataTotalRows(NodeRef document, QName tableDataAssocType, boolean createIfNotExist) {
		String tableDataAssocQName = tableDataAssocType.toPrefixString(namespaceService);
		QName tableDataTotalAssocType = QName.createQName(tableDataAssocQName + DOCUMENT_TABLE_TOTAL_ASSOC_POSTFIX, namespaceService);

		AssociationDefinition assocDefinition = dictionaryService.getAssociation(tableDataTotalAssocType);
		if (assocDefinition != null) {
			List<AssociationRef> totalRowAssocs = nodeService.getTargetAssocs(document, tableDataTotalAssocType);
			if (totalRowAssocs != null && totalRowAssocs.size() > 0) {
				List<NodeRef> result = new ArrayList<NodeRef>();
				for (AssociationRef assoc: totalRowAssocs) {
					result.add(assoc.getTargetRef());
				}
				return result;
			} else {
				NodeRef totalRow = createNode(getRootFolder(document), assocDefinition.getTargetClass().getName(), null, null);
				nodeService.createAssociation(document, totalRow, tableDataTotalAssocType);
				recalculateTotalRow(totalRow, null);
				List<NodeRef> result = new ArrayList<NodeRef>();
				result.add(totalRow);
				return result;
			}
		} else {
			return null;
		}
	}

	@Override
	public void recalculateTotalRows(List<NodeRef> rows, List<QName> properties) {
		if (rows != null) {
			for (NodeRef row: rows) {
				recalculateTotalRow(row, properties);
			}
		}
	}

	@Override
	public void recalculateTotalRow(NodeRef row, List<QName> properties) {
		if (row != null) {

		}
	}
}
