package ru.it.lecm.workflow.beans;

import java.util.UUID;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.workflow.RouteType;
import ru.it.lecm.workflow.api.LecmWorkflowModel;
import ru.it.lecm.workflow.api.RouteService;

/**
 *
 * @author vmalygin
 */
public class RouteServiceImpl extends BaseBean implements RouteService {

	private WorkflowFoldersServiceImpl workflowFoldersServiceImpl;

	public void setWorkflowFoldersServiceImpl(WorkflowFoldersServiceImpl workflowFoldersServiceImpl) {
		this.workflowFoldersServiceImpl = workflowFoldersServiceImpl;
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}

	@Override
	public NodeRef createEmptyRoute(final RouteType routeType) {
		NodeRef workflowFolder = workflowFoldersServiceImpl.getWorkflowFolder();

		QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, UUID.randomUUID().toString());
		NodeRef routeRef = nodeService.createNode(workflowFolder, ContentModel.ASSOC_CONTAINS, assocQName, LecmWorkflowModel.TYPE_ROUTE).getChildRef();

		//TODO: разделение маршрута на подразделенческий и индивидуальный
		switch(routeType) {
			case EMPLOYEE:
				break;
			case UNIT:
				break;
		}

		return routeRef;
	}
}
