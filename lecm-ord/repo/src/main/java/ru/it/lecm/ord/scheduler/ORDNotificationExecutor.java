package ru.it.lecm.ord.scheduler;

import java.util.List;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.ord.api.ORDNotificationService;

/**
 *
 * @author dbayandin
 */
public class ORDNotificationExecutor extends ActionExecuterAbstractBase {

	private final static Logger logger = LoggerFactory.getLogger(ORDNotificationExecutor.class);
	private ORDNotificationService ordNotificationService;
	
	public void setOrdNotificationService(ORDNotificationService ordNotificationService) {
		this.ordNotificationService = ordNotificationService;
	}
	
	@Override
	protected void executeImpl(Action action, NodeRef actionedUponNodeRef) {
		logger.info(String.format("ORD [%s] is starting.", actionedUponNodeRef.toString()));
		
		ordNotificationService.notifyInitiatorDeadlineComing(actionedUponNodeRef);
		ordNotificationService.notifyAssigneesDeadlineComing(actionedUponNodeRef);
	}

	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> paramList) {

	}

}
