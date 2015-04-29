package ru.it.lecm.meetings.beans;

import java.util.EnumMap;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 *
 * @author snovikov
 */
public interface ProtocolService {
	public static final String PROTOCOL_NAMESPACE = "http://www.it.ru/logicECM/protocol/1.0";
	public static final String PROTOCOL_TS_NAMESPACE = "http://www.it.ru/logicECM/protocol/table-structure/1.0";
	
	public static final QName TYPE_PROTOCOL = QName.createQName(PROTOCOL_NAMESPACE, "document");
	
	public static final QName ASSOC_PROTOCOL_MEETING_CHAIRMAN = QName.createQName(PROTOCOL_NAMESPACE, "meeting-chairman-assoc");
	
	public static final QName TYPE_PROTOCOL_TS_POINT = QName.createQName(PROTOCOL_TS_NAMESPACE, "point");
	public static final QName TYPE_PROTOCOL_TS_POINTS_TABLE = QName.createQName(PROTOCOL_TS_NAMESPACE, "pointsTable");
	
	public static final QName PROP_PROTOCOL_POINT_FORMULATION = QName.createQName(PROTOCOL_TS_NAMESPACE, "point-formulation");
	public static final QName PROP_PROTOCOL_POINT_EXEC_DATE = QName.createQName(PROTOCOL_TS_NAMESPACE, "execution-date");
	public static final QName PROP_PROTOCOL_POINT_DATE_REAL = QName.createQName(PROTOCOL_TS_NAMESPACE, "execution-date-real");
	public static final QName ASSOC_PROTOCOL_POINTS = QName.createQName(PROTOCOL_TS_NAMESPACE, "points-assoc");
	public static final QName ASSOC_PROTOCOL_POINT_EXECUTOR = QName.createQName(PROTOCOL_TS_NAMESPACE, "executor-assoc");
	public static final QName ASSOC_PROTOCOL_POINT_ERRAND = QName.createQName(PROTOCOL_TS_NAMESPACE, "errand-assoc");
	public static final QName ASSOC_PROTOCOL_POINT_STATUS = QName.createQName(PROTOCOL_TS_NAMESPACE, "point-status-assoc");
	
	public static enum P_STATUSES { PERFORMANCE_STATUS, EXECUTED_STATUS, NOT_EXECUTED_STATUS, EXPIRED_STATUS };
	public static final EnumMap<P_STATUSES,String> POINT_STATUSES = new EnumMap<P_STATUSES,String>(P_STATUSES.class){{
		put(P_STATUSES.PERFORMANCE_STATUS, "На исполнении");
		put(P_STATUSES.EXECUTED_STATUS, "Исполнен");
		put(P_STATUSES.NOT_EXECUTED_STATUS, "Не исполнен");
		put(P_STATUSES.EXPIRED_STATUS, "Просрочен");
	}};
	
	public static enum ATTACHMENT_CATEGORIES { DOCUMENT, APPLICATIONS, ORIGINAL, OTHERS };
	public static final EnumMap<ATTACHMENT_CATEGORIES,String> ATTACHMENT_CATEGORIES_MAP = new EnumMap<ATTACHMENT_CATEGORIES,String>(ATTACHMENT_CATEGORIES.class){{
		put(ATTACHMENT_CATEGORIES.DOCUMENT, "Документ");
		put(ATTACHMENT_CATEGORIES.APPLICATIONS, "Приложение");
		put(ATTACHMENT_CATEGORIES.ORIGINAL, "Подлинник");
		put(ATTACHMENT_CATEGORIES.OTHERS, "Прочее");
	}};
	
	public static final String PROTOCOL_POINT_DICTIONARY_NAME = "Статусы пунктов протокола";
	
	public void changePointStatus(NodeRef point, ProtocolService.P_STATUSES statusKey);
	public NodeRef getErrandLinkedPoint(NodeRef errand);
	public Boolean checkPointStatus(NodeRef point, ProtocolService.P_STATUSES statusKey);
	public String getPointStatus(NodeRef point);

}
