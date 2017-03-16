package ru.it.lecm.modelEditor.beans;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.impl.bpmn.deployer.BpmnDeployer;
import org.activiti.engine.impl.bpmn.parser.BpmnParser;
import org.activiti.engine.parse.BpmnParseHandler;
import org.alfresco.repo.dictionary.DictionaryBootstrap;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.dictionary.M2Type;
import org.alfresco.repo.workflow.activiti.AlfrescoProcessEngineConfiguration;
import org.alfresco.service.cmr.dictionary.DictionaryException;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.TypedStringValue;
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
public class DictionaryBootstrapPostProcessor implements BeanFactoryPostProcessor {

	private Map<String, String> map = new HashMap<String, String>();
	private static final transient Logger logger = LoggerFactory.getLogger(DictionaryBootstrapPostProcessor.class);
	
	public Map<String, String> getModels(){
		return map;
	}

	public DictionaryBootstrapPostProcessor() {
	}
    
    @SuppressWarnings("unchecked")
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    	
    	String[] sl = beanFactory.getBeanDefinitionNames();
    	for(String s : sl) {
    		BeanDefinition bean = beanFactory.getBeanDefinition(s);
    		if(bean.getParentName()!=null&&"dictionaryModelBootstrap".equals(bean.getParentName())) {
    			logger.info("!!!!!! bean: "+s);
    			logger.info("!!!!!! parent: "+bean.getParentName());
    			MutablePropertyValues propertyValues = bean.getPropertyValues();
    			PropertyValue models = propertyValues.getPropertyValue("models");
    			if(models!=null) {
	    			if(models.getValue() instanceof ManagedList) {
		    			ManagedList<TypedStringValue> modelList = (ManagedList<TypedStringValue>) models.getValue();
		    			for(TypedStringValue modelXML: modelList) {
		    				logger.info("!!!!!! model string: "+modelXML.getValue());
		    				if(modelXML.getValue()!=null){
			    				InputStream modelStream = getClass().getClassLoader().getResourceAsStream(modelXML.getValue());
			                    if (modelStream == null)
			                    {
			                        throw new DictionaryException("Could not find bootstrap model " + s);
			                    }
			                    try
			                    {
			                        M2Model model = M2Model.createModel(modelStream);
			                        List<M2Type> types = model.getTypes();
			    		            if (types != null && !types.isEmpty()) {//для случая, если в модели нет типов (например только аспекты)
			    		            	for(M2Type mtype:types) {
			    		            		map.put(mtype.getName(),modelXML.getValue());
			    		            		logger.info("!!!!!! model type: "+mtype.getName());
			    		            	}
			    		            }
			                    }catch(DictionaryException e)
			                    {
			                        throw new DictionaryException("Could not import bootstrap model " + s, e);
			                    }
			                    finally
			                    {
			                        try
			                        {
			                            modelStream.close();
			                        } 
			                        catch (IOException ioe)
			                        {
			                            logger.warn("Failed to close model input stream for '"+s+"': "+ioe);
			                        }
			                    }
		    				}
		    			}
	    			} else if(models.getValue() instanceof RuntimeBeanReference) {
	    				RuntimeBeanReference modelList = (RuntimeBeanReference) models.getValue();
	    				logger.info("!!!!!! model ref: "+modelList.getBeanName());
	    				if(modelList.getBeanName()!=null) {
	    					BeanDefinition bean1 = beanFactory.getBeanDefinition(modelList.getBeanName());
	    					MutablePropertyValues propertyValues1 = bean1.getPropertyValues();
	    					PropertyValue models1 = propertyValues1.getPropertyValue("sourceList");
	    					if(models1.getValue() instanceof ManagedList) {
	    						ManagedList<TypedStringValue> modelList1 = (ManagedList<TypedStringValue>) models1.getValue();
	    						for(TypedStringValue modelXML: modelList1) {
	    							logger.info("!!!!!! model ref string val: "+modelXML.getValue());
	    							if(modelXML.getValue()!=null){
	    			    				InputStream modelStream = getClass().getClassLoader().getResourceAsStream(modelXML.getValue());
	    			                    if (modelStream == null)
	    			                    {
	    			                        throw new DictionaryException("Could not find bootstrap model " + s);
	    			                    }
	    			                    try
	    			                    {
	    			                        M2Model model = M2Model.createModel(modelStream);
	    			                        List<M2Type> types = model.getTypes();
	    			    		            if (types != null && !types.isEmpty()) {//для случая, если в модели нет типов (например только аспекты)
	    			    		            	for(M2Type mtype:types) {
	    			    		            		map.put(mtype.getName(),modelXML.getValue());
	    			    		            		logger.info("!!!!!! model type: "+mtype.getName());
	    			    		            	}
	    			    		            }
	    			                    }catch(DictionaryException e)
	    			                    {
	    			                        throw new DictionaryException("Could not import bootstrap model " + s, e);
	    			                    }
	    			                    finally
	    			                    {
	    			                        try
	    			                        {
	    			                            modelStream.close();
	    			                        } 
	    			                        catch (IOException ioe)
	    			                        {
	    			                            logger.warn("Failed to close model input stream for '"+s+"': "+ioe);
	    			                        }
	    			                    }
	    		    				}
	    						}
	    					} else {
	    						logger.info("!!!!!! model ref val: "+models1.getValue());
	    					}
	    				}
	    			} else {
	    				logger.info("!!!!!! model: "+models.getValue());
	    			}
    			}
    		}
    	}
    }

}
