package ru.it.lecm.br5.semantic.actions;

import java.util.List;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.br5.semantic.api.SemanticBean;

/**
 *
 * @author vkuprin
 */
public class UpdateIndexAction extends ActionExecuterAbstractBase {

	private SemanticBean semanticService;

	public SemanticBean getSemanticService() {
		return semanticService;
	}

	public void setSemanticService(SemanticBean semanticService) {
		this.semanticService = semanticService;
	}
	
	@Override
	protected void executeImpl(Action action, NodeRef actionedUponNodeRef) {
		semanticService.refreshDocument(actionedUponNodeRef);
	}

	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
//		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	
}
