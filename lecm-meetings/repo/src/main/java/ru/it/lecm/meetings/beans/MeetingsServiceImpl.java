package ru.it.lecm.meetings.beans;

import java.util.List;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.workflow.WorkflowService;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.statemachine.StateMachineServiceBean;

/**
 *
 * @author vkuprin
 */
public class MeetingsServiceImpl extends BaseBean implements MeetingsService {

	private WorkflowService workflowService;
	private PersonService personService;
	private StateMachineServiceBean stateMachineService;
	private BusinessJournalService businessJournalService;

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

	@Override
	public NodeRef getServiceRootFolder() {
		return getFolder(MEETINGS_ROOT_ID);
	}

//	@Override
//	public List<NodeRef> getAttendees(NodeRef document) {
//		return null;
//	}

}
