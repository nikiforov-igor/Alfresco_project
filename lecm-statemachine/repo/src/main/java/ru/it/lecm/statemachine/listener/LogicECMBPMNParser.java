package ru.it.lecm.statemachine.listener;

import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.impl.bpmn.parser.BpmnParseListener;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ScopeImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.impl.util.xml.Element;
import org.activiti.engine.impl.variable.VariableDeclaration;
import ru.it.lecm.statemachine.action.finishstate.FinishStateWithTransitionEndWorkflowEvent;

import java.util.List;

/**
 * User: PMelnikov
 * Date: 05.09.12
 * Time: 14:26
 * <p/>
 * Класс парсер, вызывается при инициализации рабочего
 * процесса и добавляет слушателя на завершение процесса.
 */
public class LogicECMBPMNParser implements BpmnParseListener {


	@Override
	public void parseProcess(Element element, ProcessDefinitionEntity processDefinitionEntity) {
		System.out.println("start");
	}

	@Override
	public void parseStartEvent(Element element, ScopeImpl scope, ActivityImpl activity) {
		appendExtention(element, activity);
	}

	@Override
	public void parseExclusiveGateway(Element element, ScopeImpl scope, ActivityImpl activity) {
	}

	@Override
	public void parseParallelGateway(Element element, ScopeImpl scope, ActivityImpl activity) {
	}

	@Override
	public void parseScriptTask(Element element, ScopeImpl scope, ActivityImpl activity) {
		appendExtention(element, activity);
	}

	@Override
	public void parseServiceTask(Element element, ScopeImpl scope, ActivityImpl activity) {
		appendExtention(element, activity);
	}

	@Override
	public void parseBusinessRuleTask(Element element, ScopeImpl scope, ActivityImpl activity) {
	}

	@Override
	public void parseTask(Element element, ScopeImpl scope, ActivityImpl activity) {
	}

	@Override
	public void parseManualTask(Element element, ScopeImpl scope, ActivityImpl activity) {
	}

	@Override
	public void parseUserTask(Element element, ScopeImpl scope, ActivityImpl activity) {
		appendExtention(element, activity);
	}

	@Override
	public void parseEndEvent(Element element, ScopeImpl scope, ActivityImpl activity) {
		activity.addExecutionListener(ExecutionListener.EVENTNAME_END, new FinishStateWithTransitionEndWorkflowEvent());
		appendExtention(element, activity);
	}

	@Override
	public void parseBoundaryTimerEventDefinition(Element element, boolean b, ActivityImpl activity) {
	}

	@Override
	public void parseBoundaryErrorEventDefinition(Element element, boolean b, ActivityImpl activity, ActivityImpl activity1) {
	}

	@Override
	public void parseSubProcess(Element element, ScopeImpl scope, ActivityImpl activity) {
	}

	@Override
	public void parseCallActivity(Element element, ScopeImpl scope, ActivityImpl activity) {
	}

	@Override
	public void parseProperty(Element element, VariableDeclaration variableDeclaration, ActivityImpl activity) {
	}

	@Override
	public void parseSequenceFlow(Element element, ScopeImpl scope, TransitionImpl transition) {
	}

	@Override
	public void parseSendTask(Element element, ScopeImpl scope, ActivityImpl activity) {
	}

	@Override
	public void parseMultiInstanceLoopCharacteristics(Element element, Element element1, ActivityImpl activity) {
	}

	@Override
	public void parseIntermediateTimerEventDefinition(Element element, ActivityImpl activity) {
	}

	public void parseRootElement(Element element, List<ProcessDefinitionEntity> processDefinitionEntities) {
	}

	private void appendExtention(Element element, ActivityImpl activity) {
		Element extentionElements = element.element("extensionElements");
		if (extentionElements != null) {
			Element lecmExtention = extentionElements.elementNS("http://www.it.ru/LogicECM/bpmn/1.0", "extension");
			if (lecmExtention != null) {
				String processId = ((ProcessDefinitionEntity)activity.getParent()).getKey();
				StateMachineHandler handler = new StateMachineHandler(lecmExtention, processId);
				activity.addExecutionListener(ExecutionListener.EVENTNAME_START, handler);
				activity.addExecutionListener(ExecutionListener.EVENTNAME_TAKE, handler);
				activity.addExecutionListener(ExecutionListener.EVENTNAME_END, handler);
			}
		}
	}

}
