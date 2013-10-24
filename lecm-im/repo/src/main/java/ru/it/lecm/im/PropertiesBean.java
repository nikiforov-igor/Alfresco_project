/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.it.lecm.im;

import java.util.Properties;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ikhalikov
 */
public class PropertiesBean {

    private final static Logger logger = LoggerFactory.getLogger(PropertiesBean.class);

    private Properties defaultProperties;
    private Properties globalProperties;
    private Properties activeProperties;

    public void setDefaultProperties(Properties defaultProperties) {
        this.defaultProperties = defaultProperties;
        this.activeProperties = new Properties(defaultProperties);
    }

    public void setGlobalProperties(Properties globalProperties) {
        this.globalProperties = globalProperties;
    }

    private void init() {
        Set<String> keys = defaultProperties.stringPropertyNames();
        for (String key : keys) {
            String globalValue = globalProperties.getProperty(key);
            if(globalValue == null){
                String defaultValue = defaultProperties.getProperty(key);
                logger.error("Property " + key + " was not found in alfresco-global.properties. Using default value: " + defaultValue);
            } else {
                activeProperties.put(key, globalValue);
            }
        }
    }

    public boolean isActive() {
        return Boolean.parseBoolean(activeProperties.getProperty("lecmim.enabled"));
    }

    public String getBindURL() {
        return activeProperties.getProperty("lecmim.bind");
    }

    public String getProperty(String key) {
        return activeProperties.getProperty(key);
    }

}
