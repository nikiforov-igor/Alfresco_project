package ru.it.lecm.workflow.routes.beans;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.workflow.approval.api.ApprovalService;
import ru.it.lecm.workflow.routes.api.RoutesModel;
import ru.it.lecm.workflow.routes.api.RoutesService;

/**
 *
 * @author vlevin
 */
public class RoutesServiceImpl extends BaseBean implements RoutesService {

	private final static Logger logger = LoggerFactory.getLogger(RoutesServiceImpl.class);
	public final static String ROUTES_FOLDER_ID = "ROUTES_FOLDER";

	private ApprovalService approvalService;

	public void setApprovalService(ApprovalService approvalService) {
		this.approvalService = approvalService;
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return getFolder(ROUTES_FOLDER_ID);
	}

	public void init() {

	}

	@Override
	public NodeRef getRoutesFolder() {
		return getServiceRootFolder();
	}

	@Override
	public NodeRef createNewTemporaryNode(NodeRef parentNode, QName nodeType) {
		NodeRef node = nodeService.createNode(parentNode, ContentModel.ASSOC_CONTAINS, getRandomQName(), nodeType).getChildRef();
		nodeService.addAspect(node, ContentModel.ASPECT_TEMPORARY, null);
		return node;
	}

	private QName getRandomQName() {
		return QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, UUID.randomUUID().toString());
	}

	@Override
	public NodeRef getDocumentCurrentIteration(final NodeRef documentRef) {
		NodeRef documentApprovalFolder = approvalService.getDocumentApprovalFolder(documentRef);
		if (documentApprovalFolder == null) {
			return null;
		}
		Set<QName> types = new HashSet<>();
		types.add(RoutesModel.TYPE_ROUTE);
		List<ChildAssociationRef> children = nodeService.getChildAssocs(documentApprovalFolder, types);
		return children.isEmpty() ? null : children.get(0).getChildRef();
	}

	@Override
	public boolean archiveDocumentCurrentIteration(final NodeRef documentRef) {
		return true;
	}
}
