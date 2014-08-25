package ru.it.lecm.workflow.routes.beans;

import java.util.UUID;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.workflow.routes.api.RoutesService;

/**
 *
 * @author vlevin
 */
public class RoutesServiceImpl extends BaseBean implements RoutesService {

	private final static Logger logger = LoggerFactory.getLogger(RoutesServiceImpl.class);
	public final static String ROUTES_FOLDER_ID = "ROUTES_FOLDER";

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

}
