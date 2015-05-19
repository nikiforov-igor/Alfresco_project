package ru.it.lecm.incoming.schedule;

import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.incoming.beans.IncomingServiceImpl;
import ru.it.lecm.notifications.beans.Notification;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.wcalendar.IWorkCalendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * User: pmelnikov
 * Date: 21.01.14
 * Time: 13:18
 */
public class ExecutionNotificationExecutor extends ActionExecuterAbstractBase {

    private NotificationsService notificationsService;
    private NodeService nodeService;
    private IWorkCalendar calendarBean;
    private IncomingServiceImpl incomingService;
    private OrgstructureBean orgstructureBean;
    private DocumentService documentService;

    public void setNotificationsService(NotificationsService notificationsService) {
        this.notificationsService = notificationsService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setCalendarBean(IWorkCalendar calendarBean) {
        this.calendarBean = calendarBean;
    }

    public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
        this.orgstructureBean = orgstructureBean;
    }

    @Override
    protected void executeImpl(Action action, NodeRef nodeRef) {
        List<NodeRef> employeeList = new ArrayList<NodeRef>();
        List<NodeRef> unitList = new ArrayList<NodeRef>();
        List<AssociationRef> executorAssoc = nodeService.getTargetAssocs(nodeRef, IncomingServiceImpl.ASSOC_RECIPIENT);
        if (executorAssoc.size() > 0) {
            NodeRef recipient = executorAssoc.get(0).getTargetRef();
            QName type = nodeService.getType(recipient);
            if (OrgstructureBean.TYPE_EMPLOYEE.equals(type)) {
                employeeList.add(recipient);
            } else if (OrgstructureBean.TYPE_ORGANIZATION_UNIT.equals(type)) {
                unitList.add(recipient);
            }
        }

        //Если некому отсылать - выходим
        if (employeeList.size() == 0 && unitList.size() == 0) {
            return;
        }

        Date now = normalizeDate(new Date());
        Date incomingExecutionDate = (Date) nodeService.getProperty(nodeRef, DocumentService.PROP_EDS_EXECUTION_DATE);
        incomingExecutionDate = normalizeDate(incomingExecutionDate);
        int days = notificationsService.getSettingsNDays();
        Date workCalendarDate = calendarBean.getNextWorkingDate(now, days, Calendar.DAY_OF_MONTH);

        String notificationDescription = null;
        if (now.after(incomingExecutionDate)) {
            notificationDescription = "Превышен срок исполнения по документу ";
        } else if (now.equals(incomingExecutionDate) || (incomingExecutionDate.after(now) && (incomingExecutionDate.before(workCalendarDate) || incomingExecutionDate.equals(workCalendarDate)))) {
            notificationDescription = "Приближается срок исполнения документа ";
        }

        if (notificationDescription != null) {
            notificationDescription += incomingService.wrapperLink(nodeRef, nodeService.getProperty(nodeRef, DocumentService.PROP_PRESENT_STRING).toString(), documentService.getDocumentUrl(nodeRef));
            if (nodeService.getProperty(nodeRef, DocumentService.PROP_TITLE) != null) {
                notificationDescription += ": " + nodeService.getProperty(nodeRef, DocumentService.PROP_TITLE).toString();
            }
            Notification notification = new Notification();
            if (employeeList.size() > 0) {
                notification.setRecipientEmployeeRefs(employeeList);
            } else if (unitList.size() > 0) {
                notification.setRecipientOrganizationUnitRefs(unitList);
            }
            notification.setAuthor(AuthenticationUtil.getSystemUserName());
            notification.setDescription(notificationDescription);
            notification.setObjectRef(nodeRef);
            notificationsService.sendNotification(notification);
        }
    }

    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
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

    public void setIncomingService(IncomingServiceImpl incomingService) {
        this.incomingService = incomingService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
}
