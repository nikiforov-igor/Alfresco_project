package ru.it.lecm.statemachine.bean;

import org.alfresco.service.cmr.repository.NodeRef;

import java.util.HashMap;

/**
 * Created by pmelnikov on 15.07.2015.
 */
public interface UserActionsService {
    HashMap<String, Object> getActions(NodeRef nodeRef);
}
