package ru.it.lecm.meetings.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import java.util.EnumMap;

/**
 *
 * @author snovikov
 */
public interface ProtocolService {
	String PROTOCOL_NAMESPACE = "http://www.it.ru/logicECM/protocol/1.0";
	String PROTOCOL_TS_NAMESPACE = "http://www.it.ru/logicECM/protocol/table-structure/1.0";
	
	QName TYPE_PROTOCOL = QName.createQName(PROTOCOL_NAMESPACE, "document");
	
	QName ASSOC_PROTOCOL_MEETING_CHAIRMAN = QName.createQName(PROTOCOL_NAMESPACE, "meeting-chairman-assoc");
	
	QName TYPE_PROTOCOL_TS_POINT = QName.createQName(PROTOCOL_TS_NAMESPACE, "point");
	QName TYPE_PROTOCOL_TS_POINTS_TABLE = QName.createQName(PROTOCOL_TS_NAMESPACE, "pointsTable");
	
	QName PROP_PROTOCOL_POINT_FORMULATION = QName.createQName(PROTOCOL_TS_NAMESPACE, "point-formulation");
	QName PROP_PROTOCOL_POINT_EXEC_DATE = QName.createQName(PROTOCOL_TS_NAMESPACE, "execution-date");
	QName PROP_PROTOCOL_POINT_DATE_REAL = QName.createQName(PROTOCOL_TS_NAMESPACE, "execution-date-real");
	QName PROP_PROTOCOL_POINT_DECISION = QName.createQName(PROTOCOL_TS_NAMESPACE, "decision");
	QName ASSOC_PROTOCOL_POINTS = QName.createQName(PROTOCOL_TS_NAMESPACE, "points-assoc");
	QName ASSOC_PROTOCOL_POINT_EXECUTOR = QName.createQName(PROTOCOL_TS_NAMESPACE, "executor-assoc");
	QName ASSOC_PROTOCOL_POINT_ERRAND = QName.createQName(PROTOCOL_TS_NAMESPACE, "errand-assoc");
	QName ASSOC_PROTOCOL_POINT_STATUS = QName.createQName(PROTOCOL_TS_NAMESPACE, "point-status-assoc");
	QName ASSOC_PROTOCOL_TEMP_ITEM = QName.createQName(PROTOCOL_NAMESPACE, "temp-items-assoc");

	enum P_STATUSES { PERFORMANCE_STATUS, EXECUTED_STATUS, NOT_EXECUTED_STATUS, EXPIRED_STATUS, REMOVED_STATUS;};
	EnumMap<P_STATUSES,String> POINT_STATUSES = new EnumMap<P_STATUSES,String>(P_STATUSES.class){{
		put(P_STATUSES.PERFORMANCE_STATUS, "На исполнении");
		put(P_STATUSES.EXECUTED_STATUS, "Исполнен");
		put(P_STATUSES.NOT_EXECUTED_STATUS, "Не исполнен");
		put(P_STATUSES.EXPIRED_STATUS, "Просрочен");
		put(P_STATUSES.REMOVED_STATUS, "Удален");
	}};


	enum ATTACHMENT_CATEGORIES { DOCUMENT, APPLICATIONS, ORIGINAL, OTHERS;};
	EnumMap<ATTACHMENT_CATEGORIES,String> ATTACHMENT_CATEGORIES_MAP = new EnumMap<ATTACHMENT_CATEGORIES,String>(ATTACHMENT_CATEGORIES.class){{
		put(ATTACHMENT_CATEGORIES.DOCUMENT, "Документ");
		put(ATTACHMENT_CATEGORIES.APPLICATIONS, "Приложение");
		put(ATTACHMENT_CATEGORIES.ORIGINAL, "Подлинник");
		put(ATTACHMENT_CATEGORIES.OTHERS, "Прочее");
	}};
	String PROTOCOL_POINT_DICTIONARY_NAME = "Статусы пунктов протокола";

	void changePointStatus(NodeRef point, ProtocolService.P_STATUSES statusKey);

	NodeRef getErrandLinkedPoint(NodeRef errand);
	Boolean checkPointStatus(NodeRef point, ProtocolService.P_STATUSES statusKey);
	String getPointStatus(NodeRef point);
	void formErrands(final NodeRef protocol);
	void setPointsStatusRemoved(final NodeRef protocol);
	boolean checkProtocolPointsFields(NodeRef protocol);
}
