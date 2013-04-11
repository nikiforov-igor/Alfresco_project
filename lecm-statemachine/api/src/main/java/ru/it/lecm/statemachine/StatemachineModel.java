package ru.it.lecm.statemachine;

import org.alfresco.service.namespace.QName;

/**
 * User: PMelnikov
 * Date: 18.10.12
 * Time: 9:54
 */
public class StatemachineModel {

	public final static String STATEMACHINE_URI = "http://www.it.ru/logicECM/statemachine/1.0";

    public final static QName TYPE_TIMER = QName.createQName(STATEMACHINE_URI, "timer");
    public final static QName PROP_EXECUTION_ID = QName.createQName(STATEMACHINE_URI, "executionId");
    public final static QName PROP_TASK_ID = QName.createQName(STATEMACHINE_URI, "taskId");
    public final static QName PROP_VARIABLE = QName.createQName(STATEMACHINE_URI, "variable");
    public final static QName PROP_FINISH_TIMESTAMP = QName.createQName(STATEMACHINE_URI, "finishTimestamp");

	public final static QName PROP_STATUS = QName.createQName(STATEMACHINE_URI, "status");
	public final static QName ASPECT_WORKFLOW_DOCUMENT_TASK = QName.createQName("http://www.it.ru/logicECM/statemachine/1.0", "documentTask");
	public final static QName PROP_WORKFLOW_DOCUMENT_TASK_STATE_PROCESS = QName.createQName("http://www.it.ru/logicECM/statemachine/1.0", "stateProcess");
	public final static QName TYPE_CONTENT = QName.createQName("http://www.it.ru/logicECM/document/1.0", "base");
	public final static QName ASPECT_STATUS = QName.createQName(STATEMACHINE_URI, "documentStatus");
	public final static QName ASPECT_STATEMACHINE = QName.createQName(STATEMACHINE_URI, "statemachineAspect");
	public final static QName PROP_STATEMACHINE_ID = QName.createQName(STATEMACHINE_URI, "statemachineId");


}
