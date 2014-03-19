package ru.it.lecm.nd.scheduler;

import java.util.Date;
import java.util.List;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ikhalikov
 */
public class InWorkExecutor extends ActionExecuterAbstractBase {

	private final static Logger logger = LoggerFactory.getLogger(InWorkExecutor.class);
	private NodeService nodeService;
	private BehaviourFilter policyFilter;

	public void setPolicyFilter(BehaviourFilter policyFilter) {
		this.policyFilter = policyFilter;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	@Override
	protected void executeImpl(Action action, NodeRef actionedUponNodeRef) {
		logger.info(String.format("ND [%s] is starting.", actionedUponNodeRef.toString()));
		try {
			policyFilter.disableBehaviour(actionedUponNodeRef, ContentModel.ASPECT_AUDITABLE);
			nodeService.setProperty(actionedUponNodeRef, ContentModel.PROP_MODIFIED, new Date());
		} finally {
			policyFilter.enableBehaviour(actionedUponNodeRef, ContentModel.ASPECT_AUDITABLE);
		}
	}

	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> paramList) {

	}

}
