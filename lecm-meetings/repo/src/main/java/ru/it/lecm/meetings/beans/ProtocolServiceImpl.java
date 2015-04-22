package ru.it.lecm.meetings.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.BaseBean;

/**
 *
 * @author snovikov
 */
public class ProtocolServiceImpl extends BaseBean implements ProtocolService {

	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}
	
}
