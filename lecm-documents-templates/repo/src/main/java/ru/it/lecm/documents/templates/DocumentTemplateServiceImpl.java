package ru.it.lecm.documents.templates;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.springframework.context.ApplicationEvent;
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
			NodeRef currentEmployee = orgstructureService.getCurrentEmployee();

			boolean isAllowed = templateOrganization == null;

			if (templateOrganization != null) {
				NodeRef employeeOrganization = orgstructureService.getOrganization(currentEmployee);
				isAllowed = Objects.equals(templateOrganization, employeeOrganization);
			}

			if (isAllowed) {
				List<NodeRef> templateUnits = findNodesByAssociationRef(template, ASSOC_ORG_UNIT, null, ASSOCIATION_TYPE.TARGET);
				List<NodeRef> employeeUnits = orgstructureService.getEmployeeUnits(currentEmployee, false);

				isAllowed = templateUnits.isEmpty();

				for (NodeRef templateUnit : templateUnits) {
					if (employeeUnits.contains(templateUnit)) {
						isAllowed = true;
						break;
					}
				}
			}
			if (isAllowed) {
				templates.add(template);
			}
		}
		return templates;
	}

}
