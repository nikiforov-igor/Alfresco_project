package ru.it.lecm.documents.removal;

import java.util.List;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import ru.it.lecm.documents.beans.DocumentService;

/**
 *
 * @author vmalygin
 */
public class DocumentsRemovalServiceImpl implements DocumentsRemovalService {

	DictionaryService dictionaryService;
	NodeService nodeService;

	public void setDictionaryService(final DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	public void setNodeService(final NodeService nodeService) {
		this.nodeService = nodeService;
	}

	private void removeAssociations(final List<AssociationRef> assocs) {
		for (AssociationRef assoc : assocs) {
			nodeService.removeAssociation(assoc.getSourceRef(), assoc.getTargetRef(), assoc.getTypeQName());
		}
	}

	@Override
	public void purge(final NodeRef documentRef) {
		QName documentType = nodeService.getType(documentRef);
		boolean isDocument = dictionaryService.isSubClass(documentType, DocumentService.TYPE_BASE_DOCUMENT);
		if (!isDocument) {
			String template = "Node %s of type %s is not subtype of %s. This service can't remove it, please use standard removal mechanism";
			String msg = String.format(template, documentRef, documentType, DocumentService.TYPE_BASE_DOCUMENT);
			throw new AlfrescoRuntimeException(msg);
		}
		//пробуем удалить все ассоциации без разбора
		removeAssociations(nodeService.getTargetAssocs(documentRef, RegexQNamePattern.MATCH_ALL));
		removeAssociations(nodeService.getSourceAssocs(documentRef, RegexQNamePattern.MATCH_ALL));
	}
}
