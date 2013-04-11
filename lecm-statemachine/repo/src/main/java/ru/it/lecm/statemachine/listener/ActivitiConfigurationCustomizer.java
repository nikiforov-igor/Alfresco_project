package ru.it.lecm.statemachine.listener;

import org.activiti.engine.impl.bpmn.deployer.BpmnDeployer;
import org.activiti.engine.impl.bpmn.parser.BpmnParser;
import org.alfresco.repo.workflow.activiti.AlfrescoProcessEngineConfiguration;
import org.springframework.beans.factory.InitializingBean;

/**
 * User: PMelnikov
 * Date: 05.09.12
 * Time: 14:52
 * <p/>
 * Утилитарный класс предназначен для инициализации парсера,
 * который добаляет листенер на окончание пользовательского процесса.
 */
public class ActivitiConfigurationCustomizer implements InitializingBean {

	private AlfrescoProcessEngineConfiguration activitiProcessEngineConfiguration;
	private static LogicECMBPMNParser logicECMBPMNParser;

    public AlfrescoProcessEngineConfiguration getActivitiProcessEngineConfiguration() {
		return activitiProcessEngineConfiguration;
	}

	public void setActivitiProcessEngineConfiguration(AlfrescoProcessEngineConfiguration activitiProcessEngineConfiguration) {
		this.activitiProcessEngineConfiguration = activitiProcessEngineConfiguration;
	}

    public void setLogicECMBPMNParser(LogicECMBPMNParser logicECMBPMNParser) {
        ActivitiConfigurationCustomizer.logicECMBPMNParser = logicECMBPMNParser;
    }

    @Override
	public void afterPropertiesSet() throws Exception {
		BpmnParser parser = ((BpmnDeployer) activitiProcessEngineConfiguration.getDeploymentCache().getDeployers().get(0)).getBpmnParser();
        parser.getParseListeners().add(logicECMBPMNParser);
	}

}
