package ru.it.lecm.meetings.scripts;

import java.util.List;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.ParameterCheck;
import org.mozilla.javascript.Scriptable;
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

	public Scriptable getMeetingHoldingItems(String meeting) {
		ParameterCheck.mandatory("meeting", meeting);

		NodeRef meetingRef = new NodeRef(meeting);
		if (this.nodeService.exists(meetingRef)) {
			List<NodeRef> results = meetingsService.getMeetingHoldingItems(meetingRef);
			if (results != null) {
				return createScriptable(results);
			}
		}
		return null;
	}

	public Scriptable getTechnicalMembers(ScriptNode meeting) {
		ParameterCheck.mandatory("meeting", meeting);

		List<NodeRef> results = meetingsService.getTechnicalMembers(meeting.getNodeRef());
		if (results != null) {
			return createScriptable(results);
		}
		return null;
	}

	public ScriptNode createNewMeetingItem(String meeting) {
		ParameterCheck.mandatory("meeting", meeting);

		NodeRef meetingRef = new NodeRef(meeting);
		if (this.nodeService.exists(meetingRef)) {
			NodeRef item = meetingsService.createNewMeetingItem(meetingRef);
			if (item != null) {
				return new ScriptNode(item, serviceRegistry, getScope());
			}
		}
		return null;
	}

	public String deleteHoldingItem(String nodeRef) {
		ParameterCheck.mandatory("nodeRef", nodeRef);

		NodeRef ref = new NodeRef(nodeRef);
		if (this.nodeService.exists(ref)) {
			this.meetingsService.deleteHoldingItem(ref);
			return "Success delete";
		}
		return "Failure: node not found";
	}
}
