package ru.it.lecm.meetings.scripts;

import org.alfresco.service.cmr.repository.NodeService;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.meetings.beans.MeetingsService;

/**
 *
 * @author vkuprin
 */
public class MeetingsWebScriptBean extends BaseWebScript {
	private NodeService nodeService;
    private MeetingsService meetingsService;

	public NodeService getNodeService() {
		return nodeService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public MeetingsService getMeetingsService() {
		return meetingsService;
	}

	public void setMeetingsService(MeetingsService meetingsService) {
		this.meetingsService = meetingsService;
	}
	
	
}
