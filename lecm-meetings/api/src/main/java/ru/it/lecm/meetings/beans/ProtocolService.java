package ru.it.lecm.meetings.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.springframework.extensions.surf.util.I18NUtil;

import java.util.EnumMap;

/**
 *
 * @author snovikov
 */
public interface ProtocolService {
	String PROTOCOL_NAMESPACE = "http://www.it.ru/logicECM/protocol/1.0";
	String PROTOCOL_DICTIONARY_NAMESPACE = "http://www.it.ru/logicECM/protocol/dictionaries/1.0";
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

	enum P_STATUSES_CODES { PERFORMANCE_STATUS, EXECUTED_STATUS, NOT_EXECUTED_STATUS, EXPIRED_STATUS, REMOVED_STATUS};

	enum ATTACHMENT_CATEGORIES { DOCUMENT, APPLICATIONS, ORIGINAL, OTHERS;};

	String PROTOCOL_POINT_DICTIONARY_NAME = "Статусы пунктов протокола";
	QName PROP_PROTOCOL_DIC_POINT_STATUS_CODE = QName.createQName(PROTOCOL_DICTIONARY_NAMESPACE, "protocol-point-status-code");

	void changePointStatus(NodeRef point, String statusKey);

	NodeRef getErrandLinkedPoint(NodeRef errand);
	Boolean checkPointStatus(NodeRef point, String statusKey);
	String getPointStatus(NodeRef point);
	void formErrands(final NodeRef protocol);
	void setPointsStatusRemoved(final NodeRef protocol);
	boolean checkProtocolPointsFields(NodeRef protocol);
	String getPointStatusByCodeFromDictionary(String statusKey);
	String getPointStatusCodeByStatusTextFromDictionary(String statusText);
	String getAttachmentCategoryName(ATTACHMENT_CATEGORIES code);
}
