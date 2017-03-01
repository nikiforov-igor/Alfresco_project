package ru.it.lecm.internal.schedule;

import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.documents.beans.DocumentConnectionService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.eds.api.EDSDocumentService;
import ru.it.lecm.internal.api.InternalService;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.statemachine.StatemachineModel;
import ru.it.lecm.wcalendar.IWorkCalendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import ru.it.lecm.base.beans.BaseTransactionalSchedule;
import ru.it.lecm.documents.beans.DocumentGlobalSettingsService;

/**
 * User: dbashmakov
 * Date: 19.03.14
 * Time: 9:46
 */
public class InternalNotificationSchedule extends BaseTransactionalSchedule {

	private DocumentService documentService;
	private IWorkCalendar calendarBean;
	private NotificationsService notificationsService;
	private DocumentConnectionService connectionService;
	private DocumentGlobalSettingsService documentGlobalSettingsService;

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	public void setCalendarBean(IWorkCalendar calendarBean) {
		this.calendarBean = calendarBean;
	}

	public void setNotificationsService(NotificationsService notificationsService) {
		this.notificationsService = notificationsService;
	}

	public void setConnectionService(DocumentConnectionService connectionService) {
		this.connectionService = connectionService;
	}

	public void setDocumentGlobalSettingsService(DocumentGlobalSettingsService documentGlobalSettingsService) {
		this.documentGlobalSettingsService = documentGlobalSettingsService;
	}
	
	@Override
	public List<NodeRef> getNodesInTx() {
		// TODO: Попытаться понять и разомкнуть логику. Пока не вижу как можно
		// вынести отдельного executor'а для каждого документа, т.к нужен список
		// connectedDocs, который опять же собирается по всем документам
		List<NodeRef> internals = getInternal();

		List<NodeRef> connectedDocs = new ArrayList<>();
		for (NodeRef internal : internals) {
			List<NodeRef> connectedRefs = connectionService.getConnectedDocuments(internal, "inResponseTo", InternalService.TYPE_INTERNAL);
			for (NodeRef connectedRef : connectedRefs) {
				String status = (String) nodeService.getProperty(connectedRef, StatemachineModel.PROP_STATUS);
				if ("Направлен".equals(status) || "Закрыт".equals(status)) {
					connectedDocs.add(connectedRef);
				}
			}
		}

		for (NodeRef internal : internals) {
			List<NodeRef> employeeList = new ArrayList<>();
			List<AssociationRef> recipientsAssoc = nodeService.getTargetAssocs(internal, EDSDocumentService.ASSOC_RECIPIENTS);

			for (AssociationRef associationRef : recipientsAssoc) {
				NodeRef employee = associationRef.getTargetRef();
				if (!isEmployeeCreatorComments(employee, documentService) && !isEmployeeInternalLinkCreator(employee, connectedDocs, documentService)) {
					employeeList.add(employee);
				}
			}

			if (employeeList.isEmpty()) {
				continue;
			}

			Date now = normalizeDate(new Date());
			Date internalExecutionDate = (Date) nodeService.getProperty(internal, InternalService.PROP_INTERNAL_RESPONSE_DATE);
			internalExecutionDate = normalizeDate(internalExecutionDate);
			int days = documentGlobalSettingsService.getSettingsNDays();
			Date workCalendarDate = calendarBean.getNextWorkingDate(now, days, Calendar.DAY_OF_MONTH);

			String notificationTemplateCode = null;
			if (now.after(internalExecutionDate)) {
				notificationTemplateCode = "INTERNAL_EXCEEDED_DEADLINE";
			} else if (now.equals(internalExecutionDate) || (internalExecutionDate.after(now) && (internalExecutionDate.before(workCalendarDate) || internalExecutionDate.equals(workCalendarDate)))) {
				notificationTemplateCode = "INTERNAL_APPROACHING_DEADLINE";
			}

			if (notificationTemplateCode != null) {
				notificationsService.sendNotificationByTemplate(internal, employeeList, notificationTemplateCode);
			}
		}
		
		return new ArrayList<>();
	}

    private Date normalizeDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private List<NodeRef> getInternal() {
        // ищем внутренние документы, в статусе "Направлен", срок исполнения которых истекается менее чем через N дней
        Date start = new Date(0);

        Calendar calendar = Calendar.getInstance();
        int days = documentGlobalSettingsService.getSettingsNDays();
        Date end = calendarBean.getNextWorkingDate(new Date(), days, Calendar.DAY_OF_MONTH);
        calendar.setTime(end);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        end = calendar.getTime();

        List<QName> types = new ArrayList<>();
        types.add(InternalService.TYPE_INTERNAL);

        List<String> paths = new ArrayList<>();
        paths.add(documentService.getDocumentsFolderPath());

        List<String> statuses = new ArrayList<>();
        statuses.add("Направлен");

        String filters = "@lecm\\-eds\\-document\\:execution\\-date: [\"" + BaseBean.DateFormatISO8601.format(start) + "\" to \"" + BaseBean.DateFormatISO8601.format(end) + "\"]";
        return documentService.getDocumentsByFilter(types, paths, statuses, filters, null);
    }

    private boolean isEmployeeCreatorComments(NodeRef employee, DocumentService documentService) {
        List<QName> types = new ArrayList<>();
        types.add(InternalService.TYPE_ANSWER);

        List<String> paths = new ArrayList<>();
        paths.add(documentService.getDocumentsFolderPath());

        String filters = "@lecm\\-document\\:author\\-assoc\\-ref:\"" + employee.toString() + "\"";
        return documentService.getAmountDocumentsByFilter(types, paths, null, filters, null) > 0L;
    }

    private boolean isEmployeeInternalLinkCreator(NodeRef employee, List<NodeRef> connected, DocumentService documentService) {
        for (NodeRef nodeRef : connected) {
            NodeRef authorRef = documentService.getDocumentAuthor(nodeRef);
            if (employee.equals(authorRef)) {
                return true;
            }
        }
        return false;
    }
}
