package ru.it.lecm.base.statemachine;

import org.alfresco.service.namespace.QName;

/**
 * User: PMelnikov
 * Date: 18.10.12
 * Time: 9:54
 */
public class StateMachineModel {
	public final static QName PROP_STATUS = QName.createQName("http://www.it.ru/logicECM/statemachine/1.0", "status");
	public final static QName ASPECT_WORKFLOW_DOCUMENT_TASK = QName.createQName("http://www.it.ru/logicECM/statemachine/1.0", "documentTask");
	public final static QName PROP_WORKFLOW_DOCUMENT_TASK_STATE_PROCESS = QName.createQName("http://www.it.ru/logicECM/statemachine/1.0", "stateProcess");
	public final static QName TYPE_CONTENT = QName.createQName("http://www.it.ru/logicECM/document/1.0", "base");
	public final static QName ASPECT_STATUS = QName.createQName("http://www.it.ru/logicECM/statemachine/1.0", "documentStatus");
}
