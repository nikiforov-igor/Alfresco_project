package ru.it.lecm.base.statemachine.bean;

import org.springframework.beans.factory.InitializingBean;

import java.util.HashMap;

/**
 * User: PMelnikov
 * Date: 17.10.12
 * Time: 11:15
 */
public class DocumentStateMachineBean implements InitializingBean {

    private String documentType = null;
    private String stateMachineId = null;
    private static HashMap<String, String> stateMachines = new HashMap<String, String>();

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public void setStateMachineId(String stateMachineId) {
        this.stateMachineId = stateMachineId;
    }

    public HashMap<String, String> getStateMachines() {
        return stateMachines;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (documentType != null && stateMachineId != null) {
            stateMachines.put(documentType, stateMachineId);
        }
    }

}
