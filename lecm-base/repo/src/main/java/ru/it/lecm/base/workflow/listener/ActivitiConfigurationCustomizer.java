package ru.it.lecm.base.workflow.listener;

import org.activiti.engine.impl.bpmn.deployer.BpmnDeployer;
import org.activiti.engine.impl.bpmn.parser.BpmnParser;
import org.alfresco.repo.workflow.activiti.AlfrescoProcessEngineConfiguration;
import org.springframework.beans.factory.InitializingBean;

/**
 * User: PMelnikov
 * Date: 05.09.12
 * Time: 14:52
 */
public class ActivitiConfigurationCustomizer implements InitializingBean {

    private AlfrescoProcessEngineConfiguration activitiProcessEngineConfiguration;

    public AlfrescoProcessEngineConfiguration getActivitiProcessEngineConfiguration() {
        return activitiProcessEngineConfiguration;
    }

    public void setActivitiProcessEngineConfiguration(AlfrescoProcessEngineConfiguration activitiProcessEngineConfiguration) {
        this.activitiProcessEngineConfiguration = activitiProcessEngineConfiguration;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        BpmnParser parser = ((BpmnDeployer) activitiProcessEngineConfiguration.getDeploymentCache().getDeployers().get(0)).getBpmnParser();
        parser.getParseListeners().add(new EndEventParseListener());
    }

}
