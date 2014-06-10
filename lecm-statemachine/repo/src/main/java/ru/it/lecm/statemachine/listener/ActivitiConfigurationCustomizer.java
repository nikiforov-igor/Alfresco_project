package ru.it.lecm.statemachine.listener;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.impl.bpmn.deployer.BpmnDeployer;
import org.activiti.engine.impl.bpmn.parser.BpmnParser;
import org.activiti.engine.parse.BpmnParseHandler;
import org.alfresco.repo.workflow.activiti.AlfrescoProcessEngineConfiguration;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.ManagedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * User: PMelnikov
 * Date: 05.09.12
 * Time: 14:52
 * <p/>
 * Утилитарный класс предназначен для инициализации парсера,
 * который добаляет листенер на окончание пользовательского процесса.
 */
public class ActivitiConfigurationCustomizer implements BeanFactoryPostProcessor {

	private static final transient Logger logger = LoggerFactory.getLogger(ActivitiConfigurationCustomizer.class);

	public ActivitiConfigurationCustomizer() {
	}
    
    @SuppressWarnings("unchecked")
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            BeanDefinition activitiConfiguration = beanFactory
                            .getBeanDefinition("activitiProcessEngineConfiguration");
            MutablePropertyValues propertyValues = activitiConfiguration
                            .getPropertyValues();
            PropertyValue postParseHandlers = propertyValues
                            .getPropertyValue("postBpmnParseHandlers");
            ManagedList<RuntimeBeanReference> refsList = (ManagedList<RuntimeBeanReference>) postParseHandlers
                            .getValue();
            refsList.add(new RuntimeBeanReference("logicECMBPMNParser"));
    }

}
