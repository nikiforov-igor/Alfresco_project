package ru.it.lecm.meetings.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.BaseBean;

/**
 *
 * @author vkuprin
 */
public class MeetingsServiceImpl extends BaseBean implements MeetingsService {

	
	@Override
	public NodeRef getServiceRootFolder() {
		return getFolder(MEETINGS_ROOT_ID);
	}
	
}
