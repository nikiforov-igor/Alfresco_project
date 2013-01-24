package ru.it.lecm.businessjournal.schedule;

import java.util.List;

import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;

/**
 * @author dbashmakov
 *         Date: 23.01.13
 *         Time: 15:21
 */
public class BusinessJournalArchiveScheduleExecutor extends ActionExecuterAbstractBase {
	private final static Logger logger = LoggerFactory.getLogger(BusinessJournalArchiveScheduleExecutor.class);

	private BusinessJournalService businessJournalService;

	@Override
	protected void executeImpl(Action action, NodeRef nodeRef) {
		boolean success =businessJournalService.moveRecordToArchive(nodeRef);
		logger.debug(String.format("Результат перемещения записи в архив: [%s] - успех [%s]", nodeRef, success));
	}

	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
	}

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}
}
