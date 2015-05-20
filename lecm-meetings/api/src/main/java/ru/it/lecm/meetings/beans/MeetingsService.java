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
	public static final QName ASSOC_MEETINGS_TEMP_ITEMS = QName.createQName(MEETINGS_NAMESPACE, "temp-items-assoc");

	public static final QName TYPE_MEETINGS_TS_HOLDING_ITEM = QName.createQName(MEETINGS_TS_NAMESPACE, "holding-item");
	public static final QName TYPE_MEETINGS_TS_HOLDING_ITEM_START_TIME = QName.createQName(MEETINGS_TS_NAMESPACE, "holding-start-time");
	public static final QName TYPE_MEETINGS_TS_HOLDING_TABLE = QName.createQName(MEETINGS_TS_NAMESPACE, "holding-items-table");

//	public List<NodeRef> getAttendees(NodeRef document);

	List<NodeRef> getMeetingHoldingItems(NodeRef meeting);

	NodeRef getHoldingItemsTable(NodeRef meeting);

	List<NodeRef> getMeetingAgendaItems(NodeRef meeting);

	List<NodeRef> getTechnicalMembers(NodeRef meeting);

	NodeRef createNewHoldingItem(NodeRef meeting);

	public void deleteHoldingItem(NodeRef nodeRef);
}
