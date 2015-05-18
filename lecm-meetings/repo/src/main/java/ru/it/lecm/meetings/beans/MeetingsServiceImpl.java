package ru.it.lecm.meetings.beans;

import java.util.List;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.documents.beans.DocumentTableService;
import ru.it.lecm.statemachine.StateMachineServiceBean;

/**
 *
 * @author vkuprin
 */
public class MeetingsServiceImpl extends BaseBean implements MeetingsService {
	private final static Logger logger = LoggerFactory.getLogger(MeetingsServiceImpl.class);

	private WorkflowService workflowService;
	private PersonService personService;
	private StateMachineServiceBean stateMachineService;
	private BusinessJournalService businessJournalService;
	private DocumentTableService documentTableService;

	private final static String ACTIVITI_PREFIX = "activiti$";
	private final static String APPROVEMENT_WORKFLOW_DEFINITION_ID = ACTIVITI_PREFIX + "lecmApprovementWorkflow";

	public BusinessJournalService getBusinessJournalService() {
		return businessJournalService;
	}

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	public StateMachineServiceBean getStateMachineService() {
		return stateMachineService;
	}

	public void setStateMachineService(StateMachineServiceBean stateMachineService) {
		this.stateMachineService = stateMachineService;
	}

	public PersonService getPersonService() {
		return personService;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	public WorkflowService getWorkflowService() {
		return workflowService;
	}

	public void setWorkflowService(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}

	public void setDocumentTableService(DocumentTableService documentTableService) {
		this.documentTableService = documentTableService;
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return getFolder(MEETINGS_ROOT_ID);
	}

//	@Override
//	public List<NodeRef> getAttendees(NodeRef document) {
//		return null;
//	}

	public NodeRef getHoldingItemsTable(NodeRef meeting) {
		return documentTableService.getTable(meeting, TYPE_MEETINGS_TS_AGENDA_TABLE);
	}

	public List<NodeRef> getMeetingHoldingItems(NodeRef meeting) {
		NodeRef table = getHoldingItemsTable(meeting);
		if (table != null) {
			return documentTableService.getTableDataRows(table);
		}
		return null;
	}

	public NodeRef createNewMeetingItem(NodeRef meeting) {
		NodeRef table = getHoldingItemsTable(meeting);
		if (table != null) {
			try {
				return createNode(table, TYPE_MEETINGS_TS_ITEM, null, null);
			} catch (WriteTransactionNeededException ex) {
				logger.error("Error create new meeting item", ex);
			}

		}
		return null;
	}

	@Override
	public void deleteHoldingItem(NodeRef nodeRef) {
		nodeService.deleteNode(nodeRef);
	}
}
