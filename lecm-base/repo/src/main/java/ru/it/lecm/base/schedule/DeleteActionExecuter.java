package ru.it.lecm.base.schedule;

import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import java.util.List;
import org.alfresco.model.ContentModel;

/**
 * User: AIvkin
 * Date: 24.01.13
 * Time: 17:43
 */
public class DeleteActionExecuter extends ActionExecuterAbstractBase {
	private NodeService nodeService;

	public void setNodeService(NodeService nodeService)
	{
		this.nodeService = nodeService;
	}

	@Override
	protected void executeImpl(Action action, NodeRef nodeRef) {
		nodeService.addAspect(nodeRef, ContentModel.ASPECT_TEMPORARY, null);
		nodeService.deleteNode(nodeRef);
	}

	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
	}
}
