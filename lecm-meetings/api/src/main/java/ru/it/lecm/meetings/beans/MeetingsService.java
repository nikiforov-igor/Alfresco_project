package ru.it.lecm.meetings.beans;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;





/**
 *
 * @author vkuprin
 */
public interface MeetingsService {

	public static final String MEETINGS_ROOT_ID = "MEETINGS_ROOT_ID";
	
	public static final String MEETINGS_NAMESPACE = "http://www.it.ru/logicECM/meetings/1.0";
	public static final String MEETINGS_TS_NAMESPACE = "http://www.it.ru/logicECM/meetings/table-structure/1.0";
	
	public static final QName TYPE_MEETINGS_DOCUMENT = QName.createQName(MEETINGS_NAMESPACE, "document");
	public static final QName TYPE_MEETINGS_TS_AGENDA_ITEM = QName.createQName(MEETINGS_TS_NAMESPACE, "item");
	public static final QName TYPE_MEETINGS_TS_AGENDA_TABLE = QName.createQName(MEETINGS_TS_NAMESPACE, "itemsTable");
	public static final QName TYPE_MEETINGS_TS_ITEM = QName.createQName(MEETINGS_TS_NAMESPACE, "item");
	public static final QName ASSOC_MEETINGS_TS_ITEM_ATTACHMENTS = QName.createQName(MEETINGS_TS_NAMESPACE, "attachments-assoc");
	public static final QName ASSOC_MEETINGS_TS_ITEM_REPORTER = QName.createQName(MEETINGS_TS_NAMESPACE, "reporter-assoc");
	public static final QName ASSOC_MEETINGS_TS_ITEM_COREPORTER = QName.createQName(MEETINGS_TS_NAMESPACE, "coreporter-assoc");
	public static final QName ASSOC_MEETINGS_TEMP_ITEMS = QName.createQName(MEETINGS_NAMESPACE, "temp-items-assoc");
	public static final QName ASSOC_MEETINGS_CHAIRMAN = QName.createQName(MEETINGS_NAMESPACE, "chairman-assoc");
	public static final QName ASSOC_MEETINGS_SECRETARY = QName.createQName(MEETINGS_NAMESPACE, "secretary-assoc");
	public static final QName PROP_MEETINGS_APPROVE_AGENDA = QName.createQName(MEETINGS_NAMESPACE, "approve-agenda");
	public static final QName ASSOC_MEETINGS_TS_ITEM_SITE= QName.createQName(MEETINGS_TS_NAMESPACE, "site-assoc");
	public static final QName PROP_MEETINGS_TS_ITEM_NAME= QName.createQName(MEETINGS_TS_NAMESPACE, "item-name");

	public static final QName TYPE_MEETINGS_TS_HOLDING_ITEM = QName.createQName(MEETINGS_TS_NAMESPACE, "holding-item");
	public static final QName TYPE_MEETINGS_TS_HOLDING_ITEM_START_TIME = QName.createQName(MEETINGS_TS_NAMESPACE, "holding-start-time");
	public static final QName TYPE_MEETINGS_TS_HOLDING_TABLE = QName.createQName(MEETINGS_TS_NAMESPACE, "holding-items-table");

	public static final QName ASSOC_MEETINGS_HOLDING_MEMBERS = QName.createQName(MEETINGS_NAMESPACE, "holding-members-assoc");
	public static final QName ASSOC_MEETINGS_HOLDING_INVITED_MEMBERS = QName.createQName(MEETINGS_NAMESPACE, "holding-invited-members-assoc");

//	public List<NodeRef> getAttendees(NodeRef document);

	List<NodeRef> getMeetingHoldingItems(NodeRef meeting);

	NodeRef getHoldingItemsTable(NodeRef meeting);

	List<NodeRef> getMeetingAgendaItems(NodeRef meeting);

	List<NodeRef> getHoldingTechnicalMembers(NodeRef meeting);

	NodeRef createNewHoldingItem(NodeRef meeting);

	void deleteHoldingItem(NodeRef nodeRef);
	void createRepetedMeetings(NodeRef meeting);
	void updateAgendaItemMembers(NodeRef document);
	String getAgendaInfo(NodeRef meeting);
	String editAgendaItemWorkspace(NodeRef agendaItem, boolean newWorkspace);
}
