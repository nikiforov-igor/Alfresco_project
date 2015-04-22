package ru.it.lecm.meetings.beans;

import org.alfresco.service.namespace.QName;

/**
 *
 * @author snovikov
 */
public interface ProtocolService {
	public static final String PROTOCOL_NAMESPACE = "http://www.it.ru/logicECM/protocol/1.0";
	public static final String PROTOCOL_TS_NAMESPACE = "http://www.it.ru/logicECM/protocol/table-structure/1.0";
	
	public static final QName TYPE_PROTOCOL = QName.createQName(PROTOCOL_NAMESPACE, "document");
	
	public static final QName TYPE_PROTOCOL_TS_POINT = QName.createQName(PROTOCOL_TS_NAMESPACE, "point");
	public static final QName TYPE_PROTOCOL_TS_POINTS_TABLE = QName.createQName(PROTOCOL_TS_NAMESPACE, "pointsTable");

}
