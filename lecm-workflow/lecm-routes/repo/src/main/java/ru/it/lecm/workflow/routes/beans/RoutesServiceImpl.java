package ru.it.lecm.workflow.routes.beans;

import org.alfresco.service.cmr.repository.NodeRef;
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


}
