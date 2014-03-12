package ru.it.lecm.workflow.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.apache.commons.lang.StringUtils;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.workflow.RouteType;
import ru.it.lecm.workflow.api.LecmWorkflowModel;
import ru.it.lecm.workflow.api.RouteService;

/**
 *
 * @author vmalygin
 */
public class RouteServiceImpl extends BaseBean implements RouteService {

	private WorkflowFoldersServiceImpl workflowFoldersServiceImpl;
	private OrgstructureBean orgstructureBean;

	public void setWorkflowFoldersServiceImpl(final WorkflowFoldersServiceImpl workflowFoldersServiceImpl) {
		this.workflowFoldersServiceImpl = workflowFoldersServiceImpl;
	}

	public void setOrgstructureBean(final OrgstructureBean orgstructureBean) {
		this.orgstructureBean = orgstructureBean;
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}

	private List<NodeRef> getEmptyRoutesByCurrentEmployee() {
		NodeRef employeeRef = orgstructureBean.getCurrentEmployee();
		String username = orgstructureBean.getEmployeeLogin(employeeRef);
		NodeRef workflowFolder = workflowFoldersServiceImpl.getWorkflowFolder();
		List<ChildAssociationRef> children = nodeService.getChildAssocs(workflowFolder, ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);
		List<NodeRef> routes = new ArrayList<NodeRef>();
		for (ChildAssociationRef child : children) {
			NodeRef nodeRef = child.getChildRef();
			String creator = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_CREATOR);
			boolean isTemp = nodeService.hasAspect(nodeRef, LecmWorkflowModel.ASPECT_TEMP);
			if (isTemp && StringUtils.equals(username, creator)) {
				routes.add(nodeRef);
			}
		}
		return routes;
	}

	@Override
	public NodeRef createEmptyRoute(final RouteType routeType) {
		//получение списка маршрутов которые TEMP и у которых creator это currentEmployee
		List<NodeRef> routes = getEmptyRoutesByCurrentEmployee();
		for (NodeRef route : routes) {
			nodeService.addAspect(route, ContentModel.ASPECT_TEMPORARY, null);
			nodeService.deleteNode(route);
		}

		NodeRef workflowFolder = workflowFoldersServiceImpl.getWorkflowFolder();
		QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, UUID.randomUUID().toString());
		NodeRef routeRef = nodeService.createNode(workflowFolder, ContentModel.ASSOC_CONTAINS, assocQName, LecmWorkflowModel.TYPE_ROUTE).getChildRef();
		nodeService.addAspect(routeRef, LecmWorkflowModel.ASPECT_TEMP, null);

		//разделение маршрута на подразделенческий и индивидуальный
		switch (routeType) {
			case EMPLOYEE:
				NodeRef employeeRef = orgstructureBean.getCurrentEmployee();
				nodeService.createAssociation(routeRef, employeeRef, LecmWorkflowModel.ASSOC_WORKFLOW_ASSIGNEES_LIST_OWNER);
				break;
			case UNIT:
				break;
		}

		return routeRef;
	}

	@Override
	public NodeRef getAssigneesListByWorkflowType(final NodeRef routeRef, final String workflowType) {
		NodeRef assigneesListRef = null;
		List<ChildAssociationRef> children = nodeService.getChildAssocs(routeRef, LecmWorkflowModel.ASSOC_ROUTE_CONTAINS_WORKFLOW_ASSIGNEES_LIST, RegexQNamePattern.MATCH_ALL);
		for (ChildAssociationRef child : children) {
			NodeRef nodeRef = child.getChildRef();
			String type = (String) nodeService.getProperty(nodeRef, LecmWorkflowModel.PROP_WORKFLOW_TYPE);
			if (workflowType.equals(type)) {
				assigneesListRef = nodeRef;
				break;
			}
		}
		return assigneesListRef;
	}
}
