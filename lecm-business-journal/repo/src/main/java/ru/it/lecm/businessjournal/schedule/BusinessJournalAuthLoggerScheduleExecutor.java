package ru.it.lecm.businessjournal.schedule;

import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;

import java.util.List;

/**
 * @author dbashmakov
 *         Date: 23.01.13
 *         Time: 15:21
 */
public class BusinessJournalAuthLoggerScheduleExecutor extends ActionExecuterAbstractBase {
	private final static Logger logger = LoggerFactory.getLogger(BusinessJournalAuthLoggerScheduleExecutor.class);

	private BusinessJournalService businessJournalService;

	@Override
	protected void executeImpl(Action action, NodeRef nodeRef) {
	}

	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
	}

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}
}
