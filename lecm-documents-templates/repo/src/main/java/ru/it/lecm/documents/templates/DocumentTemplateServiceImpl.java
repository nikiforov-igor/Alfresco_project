package ru.it.lecm.documents.templates;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.documents.templates.api.DocumentTemplateModel;
import ru.it.lecm.documents.templates.api.DocumentTemplateService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 *
 * @author vmalygin
 */
public class DocumentTemplateServiceImpl extends BaseBean implements DocumentTemplateService {

	public final static String DOCUMENT_TEMPLATE_FOLDER_ID = "DOCUMENT_TEMPLATE_FOLDER_ID";

	QName TYPE_CONTRACTOR = QName.createQName("http://www.it.ru/lecm/contractors/model/contractor/1.0", "contractor-type");

	private NamespaceService namespaceService;
	private OrgstructureBean orgstructureService;

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
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
			NodeRef template = child.getChildRef();

			NodeRef templateOrganization = findNodeByAssociationRef(template, ASSOC_ORGANIZATION, TYPE_CONTRACTOR, ASSOCIATION_TYPE.TARGET);
			boolean isAllowed = templateOrganization == null;

			if (templateOrganization != null) {
				NodeRef employeeOrganization = orgstructureService.getOrganization(orgstructureService.getCurrentEmployee());
				isAllowed = Objects.equals(templateOrganization, employeeOrganization);
			}

			if (isAllowed) {
				templates.add(template);
			}
		}
		return templates;
	}
}
