package ru.it.lecm.base.workflow.listener;

import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.bpmn.parser.BpmnParseListener;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ScopeImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.impl.util.xml.Element;
import org.activiti.engine.impl.variable.VariableDeclaration;
import org.activiti.engine.runtime.Execution;

import java.util.List;

/**
 * User: PMelnikov
 * Date: 05.09.12
 * Time: 14:26
 * <p/>
 * Класс парсер, вызывается при инициализации рабочего
 * процесса и добавляет слушателя на завершение процесса.
 */
public class EndEventParseListener implements BpmnParseListener {


    @Override
    public void parseProcess(Element element, ProcessDefinitionEntity processDefinitionEntity) {
    }

    @Override
    public void parseStartEvent(Element element, ScopeImpl scope, ActivityImpl activity) {
    }

    @Override
    public void parseExclusiveGateway(Element element, ScopeImpl scope, ActivityImpl activity) {
    }

    @Override
    public void parseParallelGateway(Element element, ScopeImpl scope, ActivityImpl activity) {
    }

    @Override
    public void parseScriptTask(Element element, ScopeImpl scope, ActivityImpl activity) {
    }

    @Override
    public void parseServiceTask(Element element, ScopeImpl scope, ActivityImpl activity) {
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
    }

    @Override
    public void parseEndEvent(Element element, ScopeImpl scope, ActivityImpl activity) {
        activity.addExecutionListener(ExecutionListener.EVENTNAME_END, new EndEventListener());
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

}
