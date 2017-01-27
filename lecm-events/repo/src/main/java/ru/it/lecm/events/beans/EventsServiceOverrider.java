package ru.it.lecm.events.beans;

import java.util.List;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.ManagedList;

/**
 *
 * @author vmalygin/apalm
 */
public class EventsServiceOverrider implements BeanFactoryPostProcessor, BeanNameAware {

    private String name;
    private String target;
    private String propertyName;

    public void setTarget(String target) {
        this.target = target;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public void setPropertyValue(List<String> propertyValue) {
        
    }

    @Override
    public void setBeanName(String name) {
        this.name = name;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        BeanDefinition overriderDef = beanFactory.getBeanDefinition(name);
        MutablePropertyValues overriderPropertyValues = overriderDef.getPropertyValues();
        PropertyValue overriderPropertyValue = overriderPropertyValues.getPropertyValue("propertyValue");
        ManagedList<TypedStringValue> overriderValuesList = (ManagedList<TypedStringValue>)overriderPropertyValue.getValue();
        
        BeanDefinition targetDef = beanFactory.getBeanDefinition(target);
        MutablePropertyValues targetPropertyValues = targetDef.getPropertyValues();
        PropertyValue targetPropertyValue = targetPropertyValues.getPropertyValue(propertyName);
        ManagedList<TypedStringValue> targetValuesList = (ManagedList<TypedStringValue>)targetPropertyValue.getValue();

        targetValuesList.clear();
        targetValuesList.addAll(overriderValuesList);
    }
}