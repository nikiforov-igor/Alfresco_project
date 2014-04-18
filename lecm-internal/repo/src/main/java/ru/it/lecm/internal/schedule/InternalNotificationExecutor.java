package ru.it.lecm.internal.schedule;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.documents.beans.DocumentConnectionService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.eds.api.EDSDocumentService;
import ru.it.lecm.internal.api.InternalService;
import ru.it.lecm.internal.beans.InternalServiceImpl;
import ru.it.lecm.notifications.beans.Notification;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.statemachine.StatemachineModel;
import ru.it.lecm.wcalendar.IWorkCalendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * User: dbashmakov
 * Date: 19.03.14
 * Time: 9:46
 */
public class InternalNotificationExecutor implements Job {

    public static final String KEY_DOC_SERVICE = "documentService";
    public static final String KEY_NODE_SERVICE = "nodeService";
    public static final String KEY_NOTIFICATION_SERVICE = "notificationsService";
    public static final String KEY_CALENDAR_SERVICE = "calendarBean";
    public static final String KEY_INTERNAL_SERVICE = "internalService";
    public static final String KEY_CONNECTION_SERVICE = "connectionService";
    public static final String KEY_TRANSACTION_SERVICE = "transactionService";

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        final DocumentService documentService = (DocumentService) jobExecutionContext.getJobDetail().getJobDataMap().get(KEY_DOC_SERVICE);
        final NodeService nodeService = (NodeService) jobExecutionContext.getJobDetail().getJobDataMap().get(KEY_NODE_SERVICE);
        final NotificationsService notificationsService = (NotificationsService) jobExecutionContext.getJobDetail().getJobDataMap().get(KEY_NOTIFICATION_SERVICE);
        final IWorkCalendar calendarBean = (IWorkCalendar) jobExecutionContext.getJobDetail().getJobDataMap().get(KEY_CALENDAR_SERVICE);
        final InternalServiceImpl internalService = (InternalServiceImpl) jobExecutionContext.getJobDetail().getJobDataMap().get(KEY_INTERNAL_SERVICE);
        final DocumentConnectionService connectionService = (DocumentConnectionService) jobExecutionContext.getJobDetail().getJobDataMap().get(KEY_CONNECTION_SERVICE);
        final TransactionService transactionService = (TransactionService) jobExecutionContext.getJobDetail().getJobDataMap().get(KEY_TRANSACTION_SERVICE);

        AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
            public Object doWork() {
                return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
                    @Override
                    public NodeRef execute() throws Throwable {
                        List<NodeRef> internals = getInternal(documentService, notificationsService, calendarBean);

                        List<NodeRef> connectedDocs = new ArrayList<NodeRef>();
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
                            List<NodeRef> employeeList = new ArrayList<NodeRef>();
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
                            Date internalExecutionDate = (Date) nodeService.getProperty(internal, DocumentService.PROP_EDS_EXECUTION_DATE);
                            internalExecutionDate = normalizeDate(internalExecutionDate);
                            int days = notificationsService.getSettingsNDays();
                            Date workCalendarDate = calendarBean.getNextWorkingDate(now, days, Calendar.DAY_OF_MONTH);

                            String notificationDescription = null;
                            if (now.after(internalExecutionDate)) {
                                notificationDescription = "Превышен срок исполнения по документу ";
                            } else if (now.equals(internalExecutionDate) || (internalExecutionDate.after(now) && (internalExecutionDate.before(workCalendarDate) || internalExecutionDate.equals(workCalendarDate)))) {
                                notificationDescription = "Приближается срок исполнения документа ";
                            }

                            if (notificationDescription != null) {
                                notificationDescription += internalService.wrapperLink(internal, nodeService.getProperty(internal, DocumentService.PROP_PRESENT_STRING).toString(), BaseBean.DOCUMENT_LINK_URL);
                                Notification notification = new Notification();
                                notification.setRecipientEmployeeRefs(employeeList);
                                notification.setAuthor(AuthenticationUtil.getSystemUserName());
                                notification.setDescription(notificationDescription);
                                notification.setObjectRef(internal);
                                notificationsService.sendNotification(notification);
                            }
                        }
                        return null;
                    }
                });
            }
        });
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

    private List<NodeRef> getInternal(DocumentService documentService, NotificationsService notificationsService, IWorkCalendar calendarBean) {
        // ищем внутренние документы, в статусе "Направлен", срок исполнения которых истекается менее чем через N дней
        Date start = new Date(0);

        Calendar calendar = Calendar.getInstance();
        int days = notificationsService.getSettingsNDays();
        Date end = calendarBean.getNextWorkingDate(new Date(), days, Calendar.DAY_OF_MONTH);
        calendar.setTime(end);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        end = calendar.getTime();

        List<QName> types = new ArrayList<QName>();
        types.add(InternalService.TYPE_INTERNAL);

        List<String> paths = new ArrayList<String>();
        paths.add(documentService.getDocumentsFolderPath());

        List<String> statuses = new ArrayList<String>();
        statuses.add("Направлен");

        String filters = "@lecm\\-eds\\-document\\:execution\\-date: [\"" + DocumentService.DateFormatISO8601.format(start) + "\" to \"" + DocumentService.DateFormatISO8601.format(end) + "\"]";
        return documentService.getDocumentsByFilter(types, paths, statuses, filters, null);
    }

    private boolean isEmployeeCreatorComments(NodeRef employee, DocumentService documentService) {
        List<QName> types = new ArrayList<QName>();
        types.add(InternalService.TYPE_ANSWER);

        List<String> paths = new ArrayList<String>();
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
