package ru.it.lecm.statemachine.listener;

import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.parse.BpmnParseHandler;

import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.statemachine.LifecycleStateMachineHelper;
import ru.it.lecm.statemachine.action.listener.EndWorkflowEvent;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: PMelnikov
 * Date: 05.09.12
 * Time: 14:26
 * <p/>
 * Класс парсер, вызывается при инициализации рабочего
 * процесса и добавляет слушателя на завершение процесса.
 */
public class LogicECMBPMNParser implements BpmnParseHandler  {
//	private static StateMachineHandler stateMachineHandler;
	private static DocumentService documentService;
	private static LifecycleStateMachineHelper stateMachineHelper;
	
	private static final transient Logger logger = LoggerFactory.getLogger(LogicECMBPMNParser.class);
	
//	public void setStateMachineHandler(StateMachineHandler stateMachineHandler) {
//		LogicECMBPMNParser.stateMachineHandler = stateMachineHandler;
//	}
	public void setDocumentService(DocumentService documentService) {
		LogicECMBPMNParser.documentService = documentService;
    }
	public void setStateMachineHelper(LifecycleStateMachineHelper stateMachineHelper) {
        this.stateMachineHelper = stateMachineHelper;
    }

    @Override
	public void parse(org.activiti.engine.impl.bpmn.parser.BpmnParse parse,BaseElement element) {
    	EndWorkflowEvent event = new EndWorkflowEvent();
    	event.setStateMachineHelper(stateMachineHelper);
    	event.setDocumentService(documentService);
    	parse.getProcessDefinition(element.getId()).addExecutionListener(org.activiti.engine.impl.pvm.PvmEvent.EVENTNAME_END, event);
	}
    
	public Collection<Class<? extends BaseElement>> getHandledTypes() {
		Collection<Class<? extends BaseElement>> handledTypes = new ArrayList<Class<? extends BaseElement>>();
		handledTypes.add(Process.class); 
        return handledTypes;
	}
}

//	public void parseStartEvent(Element element, ScopeImpl scope, ActivityImpl activity) {
//		appendExtention(element, activity);
//	}
//	public void parseScriptTask(Element element, ScopeImpl scope, ActivityImpl activity) {
//		appendExtention(element, activity);
//	}
//	public void parseServiceTask(Element element, ScopeImpl scope, ActivityImpl activity) {
//		appendExtention(element, activity);
//	}
//	public void parseUserTask(Element element, ScopeImpl scope, ActivityImpl activity) {
//		appendExtention(element, activity);
//	}
//	public void parseEndEvent(Element element, ScopeImpl scope, ActivityImpl activity) {
//		activity.addExecutionListener(ExecutionListener.EVENTNAME_END, new EndWorkflowEvent());
//		appendExtention(element, activity);
//	}
//	private void appendExtention(Element element, ActivityImpl activity) {
//		Element extentionElements = element.element("extensionElements");
//		if (extentionElements != null) {
//			Element lecmExtention = extentionElements.elementNS("http://www.it.ru/LogicECM/bpmn/1.0", "extension");
//			if (lecmExtention != null) {
//				String processId = ((ProcessDefinitionEntity)activity.getParent()).getKey();
//                StateMachineHandler.StatemachineTaskListener listener = stateMachineHandler.configure(lecmExtention, processId);
//                activity.addExecutionListener(ExecutionListener.EVENTNAME_START, listener);
//                activity.addExecutionListener(ExecutionListener.EVENTNAME_TAKE, listener);
//                activity.addExecutionListener(ExecutionListener.EVENTNAME_END, listener);
//			}
//		}
//	}

