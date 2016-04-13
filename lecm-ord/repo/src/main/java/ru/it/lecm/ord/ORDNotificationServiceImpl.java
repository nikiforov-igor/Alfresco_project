package ru.it.lecm.ord;

import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.apache.commons.lang.time.DateUtils;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.eds.api.EDSDocumentService;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.ord.api.ORDModel;
import ru.it.lecm.ord.api.ORDNotificationService;
import ru.it.lecm.wcalendar.IWorkCalendar;

import java.util.*;

/**
 *
 * @author dbayandin
 */
public class ORDNotificationServiceImpl extends BaseBean implements ORDNotificationService {

	private IWorkCalendar workCalendarService;
	private NotificationsService notificationsService;
	
	public void setWorkCalendarService(IWorkCalendar workCalendarService) {
		this.workCalendarService = workCalendarService;
	}
	
	public void setNotificationsService(NotificationsService notificationsService) {
		this.notificationsService = notificationsService;
	}
	
	@Override
	public void notifyInitiatorDeadlineComing(NodeRef documentRef, NodeRef initiatorRef, Date deadlineDate) {
		List<NodeRef> recipients = new ArrayList<>();
		recipients.add(initiatorRef);
		
		Date comingSoonDate = workCalendarService.getEmployeePreviousWorkingDay(initiatorRef, deadlineDate, -DEADLINE_ALARM_PERIOD);
		Date currentDate = new Date();
		if (comingSoonDate != null) {
			int comingSoon = DateUtils.truncatedCompareTo(currentDate, comingSoonDate, Calendar.DATE);
			if (comingSoon >= 0) {
				notificationsService.sendNotificationByTemplate(documentRef, recipients, "ORD_EXCEEDED_DEADLINE");
			}
		}
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}

	@Override
	public void notifyAssigneeDeadlineComing(NodeRef documentRef, NodeRef employee, Date deadlineDate) {
		List<NodeRef> recipients = new ArrayList<>();
		recipients.add(employee);
		Date comingSoonDate = workCalendarService.getEmployeePreviousWorkingDay(employee, deadlineDate, -DEADLINE_ALARM_PERIOD);
		Date currentDate = new Date();
		if (comingSoonDate != null) {
			int comingSoon = DateUtils.truncatedCompareTo(currentDate, comingSoonDate, Calendar.DATE);
			if (comingSoon >= 0) {
				notificationsService.sendNotificationByTemplate(documentRef, recipients, "ORD_APPROACHING_DEADLINE");

			}
		}
	}

	@Override
	public void notifyInitiatorDeadlineComing(NodeRef documentRef) {
		Date documentExecutionDate = (Date) nodeService.getProperty(documentRef, EDSDocumentService.PROP_EXECUTION_DATE);
		if (documentExecutionDate != null) {
			List<AssociationRef> controllerAssocs = nodeService.getTargetAssocs(documentRef, ORDModel.ASSOC_ORD_CONTROLLER);
			for (AssociationRef controllerAssoc : controllerAssocs) {
				NodeRef controllerRef = controllerAssoc.getTargetRef();
				notifyInitiatorDeadlineComing(documentRef, controllerRef, documentExecutionDate);
			}
		}
	}
	
	@Override
	public void notifyAssigneesDeadlineComing(NodeRef documentRef) {
		Set<QName> pointType = new HashSet<>(Collections.singletonList(ORDModel.TYPE_ORD_TABLE_ITEM));
		
		List<AssociationRef> tablesAssocs = nodeService.getTargetAssocs(documentRef, ORDModel.ASSOC_ORD_TABLE_ITEMS);
		for (AssociationRef tableAssoc : tablesAssocs) {
			NodeRef tableRef = tableAssoc.getTargetRef();
			List<ChildAssociationRef> pointAssocs = nodeService.getChildAssocs(tableRef, pointType);
			for (ChildAssociationRef pointAssoc : pointAssocs) {
				NodeRef pointRef = pointAssoc.getChildRef();
				Date executionDate = (Date)nodeService.getProperty(pointRef, ORDModel.PROP_ORD_TABLE_EXECUTION_DATE);
				List<AssociationRef> executorsAssocs = nodeService.getTargetAssocs(pointRef, ORDModel.ASSOC_ORD_TABLE_EXECUTOR);
				for (AssociationRef executorAssoc : executorsAssocs) {
					NodeRef executorRef = executorAssoc.getTargetRef();
					if (executorRef != null && executionDate != null) 
						notifyAssigneeDeadlineComing(documentRef, executorRef, executionDate);
				}
			}
		}
	}
}
