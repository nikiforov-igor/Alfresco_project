/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.it.lecm.nd.scheduler;

import java.util.List;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.statemachine.StatemachineModel;

/**
 *
 * @author ikhalikov
 */
public class CancelExecutor extends ActionExecuterAbstractBase {

	private final static Logger logger = LoggerFactory.getLogger(InWorkExecutor.class);
	private NodeService nodeService;

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	@Override
	protected void executeImpl(Action action, NodeRef actionedUponNodeRef) {
		logger.info(String.format("ND [%s] is cancelling.", actionedUponNodeRef.toString()));
		nodeService.setProperty(actionedUponNodeRef, StatemachineModel.PROP_STATUS, "Срок действия окончен");

	}

	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> paramList) {

	}

}
