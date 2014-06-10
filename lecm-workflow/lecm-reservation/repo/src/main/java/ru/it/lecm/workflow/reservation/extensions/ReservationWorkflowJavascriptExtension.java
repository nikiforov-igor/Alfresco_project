package ru.it.lecm.workflow.reservation.extensions;

import java.util.List;
import org.activiti.engine.delegate.DelegateTask;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNodeList;
import org.alfresco.service.cmr.repository.NodeRef;
import org.springframework.extensions.webscripts.WebScriptException;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.notifications.beans.Notification;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.workflow.reservation.DecisionResult;
import ru.it.lecm.workflow.reservation.ReservationWorkflowService;
import ru.it.lecm.workflow.WorkflowTaskDecision;

/**
 *
 * @author snovikov
 */
public class ReservationWorkflowJavascriptExtension extends BaseWebScript {

	private OrgstructureBean orgstructureService;
	private ReservationWorkflowService reservationWorkflowService;

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setReservationWorkflowService(ReservationWorkflowService reservationWorkflowService) {
		this.reservationWorkflowService = reservationWorkflowService;
	}

	public void setReservationActive(final ActivitiScriptNode bpmPackage, final boolean isActive) {
		reservationWorkflowService.setReservationActive(bpmPackage.getNodeRef(), isActive);
	}

	public ActivitiScriptNodeList getRegistrars(final ActivitiScriptNode bpmPackage, final String registrarRole) {
		List<NodeRef> registrars = reservationWorkflowService.getRegistrars(bpmPackage.getNodeRef(), registrarRole);
		ActivitiScriptNodeList registrarsScriptList = new ActivitiScriptNodeList();
		for (NodeRef registrar : registrars) {
			NodeRef userRef = orgstructureService.getPersonForEmployee(registrar);
			ActivitiScriptNode userScriptNode = new ActivitiScriptNode(userRef, serviceRegistry);
			registrarsScriptList.add(userScriptNode);
		}
		return registrarsScriptList;
	}

	public ActivitiScriptNode getCurrentEmployee() {
		return new ActivitiScriptNode(orgstructureService.getCurrentEmployee(), serviceRegistry);
	}

	public ActivitiScriptNode getEmployeeByPerson(final ActivitiScriptNode personRef) {
		return new ActivitiScriptNode(orgstructureService.getEmployeeByPerson(personRef.getNodeRef()), serviceRegistry);
	}

	public void assignTask(final ActivitiScriptNode assignee, final DelegateTask task) {
            try {
                reservationWorkflowService.assignTask(assignee.getNodeRef(), task);
            } catch (WriteTransactionNeededException ex) {
                throw new WebScriptException(ex.getMessage(), ex);
            }
	}

	public void reassignTask(final ActivitiScriptNode assignee, final DelegateTask task) {
            try {
		reservationWorkflowService.reassignTask(assignee.getNodeRef(), task);
            } catch (WriteTransactionNeededException ex) {
                throw new WebScriptException(ex.getMessage(), ex);
            }
	}

	public void notifyReservationStarted(final ActivitiScriptNode bpmPackage, final ScriptNode employee) {
		reservationWorkflowService.notifyWorkflowStarted(employee.getNodeRef(), null, bpmPackage.getNodeRef());
	}

	public WorkflowTaskDecision completeTask(final ActivitiScriptNode assignee, final DelegateTask task) {
            try {
		return reservationWorkflowService.completeTask(assignee.getNodeRef(), task);
            } catch (WriteTransactionNeededException ex) {
                throw new WebScriptException(ex.getMessage(), ex);
            }
	}

	public void notifyReservationFinished(final ActivitiScriptNode bpmPackage, final DelegateTask task) {
		String taskDecision = (String) task.getVariable("taskDecision");
		String comment = (String) task.getVariable("bpm_comment");
		ScriptNode reservateInitiator = (ScriptNode)task.getVariable("reservateInitiator");
		String decision;
		if (DecisionResult.RESERVED.name().equals(taskDecision)) {
			decision = "выполнен";
		} else if (DecisionResult.REJECTED.name().equals(taskDecision)) {
			decision = String.format("<a href='#' title='%s'>отклонен</a>", comment);
		} else {
			decision = "еще не выполнен";
		}

		reservationWorkflowService.notifyWorkflowFinished(reservateInitiator.getNodeRef(), decision, bpmPackage.getNodeRef());
	}

	public void setEmptyRegnum(final ActivitiScriptNode bpmPackage) {
		reservationWorkflowService.setEmptyRegnum(bpmPackage.getNodeRef());
	}

	public Notification prepareNotificationAboutEmptyRegistrars(final ActivitiScriptNode bpmPackage, final ActivitiScriptNode reservateInitiator) {
		return reservationWorkflowService.prepareNotificationAboutEmptyRegistrars(bpmPackage.getNodeRef(), reservateInitiator.getNodeRef());
	}
}
