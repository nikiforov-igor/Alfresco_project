package ru.it.lecm.documents.templates;

import java.util.ArrayList;
import java.util.List;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.documents.templates.api.DocumentTemplateModel;
import ru.it.lecm.documents.templates.api.DocumentTemplateService;

/**
 *
 * @author vmalygin
 */
public class DocumentTemplateServiceImpl extends BaseBean implements DocumentTemplateService {

	public final static String DOCUMENT_TEMPLATE_FOLDER_ID = "DOCUMENT_TEMPLATE_FOLDER_ID";

	private NamespaceService namespaceService;

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return getFolder(DOCUMENT_TEMPLATE_FOLDER_ID);
	}

	@Override
	public NodeRef getDocumentTemplateFolder() {
		return getServiceRootFolder();
	}

	public void init() {

	}

	@Override
	public List<NodeRef> getDocumentTemplatesForType(QName type) {
		return getDocumentTemplatesForType(type.toPrefixString(namespaceService));
	}

	@Override
	public List<NodeRef> getDocumentTemplatesForType(String type) {
		List<ChildAssociationRef> childs = nodeService.getChildAssocsByPropertyValue(getDocumentTemplateFolder(), DocumentTemplateModel.PROP_DOCUMENT_TEMPLATE_DOC_TYPE, type);
		List<NodeRef> templates = new ArrayList<>();
		for (ChildAssociationRef child : childs) {
			templates.add(child.getChildRef());
		}
		return templates;
	}
}
