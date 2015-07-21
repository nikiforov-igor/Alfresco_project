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

	public Scriptable getMeetingAgendaItems(ScriptNode meeting) {
		ParameterCheck.mandatory("meeting", meeting);

		List<NodeRef> results = meetingsService.getMeetingAgendaItems(meeting.getNodeRef());
		if (results != null) {
			return createScriptable(results);
		}
		return null;
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

	public ScriptNode getMeetingHoldingItemsTable(ScriptNode meeting) {
		ParameterCheck.mandatory("meeting", meeting);

		NodeRef item = meetingsService.getHoldingItemsTable(meeting.getNodeRef());
		if (item != null) {
			return new ScriptNode(item, serviceRegistry, getScope());
		}
		return null;
	}

	public Scriptable getHoldingTechnicalMembers(ScriptNode meeting) {
		ParameterCheck.mandatory("meeting", meeting);

		List<NodeRef> results = meetingsService.getHoldingTechnicalMembers(meeting.getNodeRef());
		if (results != null) {
			return createScriptable(results);
		}
		return null;
	}

	public ScriptNode createNewHoldingItem(String meeting) {
		ParameterCheck.mandatory("meeting", meeting);

		NodeRef meetingRef = new NodeRef(meeting);
		if (this.nodeService.exists(meetingRef)) {
			NodeRef item = meetingsService.createNewHoldingItem(meetingRef);
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
	
	public void createRepetedMeetings(ScriptNode nodeRef) {
		ParameterCheck.mandatory("nodeRef", nodeRef);
		NodeRef meeting = nodeRef.getNodeRef();
		meetingsService.createRepetedMeetings(meeting);
	}
	
	public void editAgendaItemWorkspace(String agendaItemStr, Boolean newWorkspace){
		ParameterCheck.mandatory("nodeRef", agendaItemStr);
		NodeRef agendaItem = new NodeRef(agendaItemStr);
		if (nodeService.exists(agendaItem) && MeetingsService.TYPE_MEETINGS_TS_AGENDA_ITEM.equals(nodeService.getType(agendaItem))){
			meetingsService.editAgendaItemWorkspace(agendaItem, newWorkspace);
		}
		
	}
}
