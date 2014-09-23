package ru.it.lecm.workflow.api;

import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.workflow.RouteType;

/**
 *
 * @author vmalygin
 */
@Deprecated
public interface RouteService {

	@Deprecated
	NodeRef createEmptyRoute(final RouteType routeType);

	@Deprecated
	NodeRef getAssigneesListByWorkflowType(final NodeRef routeRef, final String workflowType);
}
