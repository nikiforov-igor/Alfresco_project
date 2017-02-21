package ru.it.lecm.internal.schedule;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
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
import ru.it.lecm.documents.beans.DocumentGlobalSettingsService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.eds.api.EDSDocumentService;
import ru.it.lecm.internal.api.InternalService;
import ru.it.lecm.internal.beans.InternalServiceImpl;
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

    private static final String KEY_DOC_SERVICE = "documentService";
    private static final String KEY_NODE_SERVICE = "nodeService";
    private static final String KEY_NOTIFICATION_SERVICE = "notificationsService";
    private static final String KEY_CALENDAR_SERVICE = "calendarBean";
    private static final String KEY_INTERNAL_SERVICE = "internalService";
    private static final String KEY_CONNECTION_SERVICE = "connectionService";
    private static final String KEY_TRANSACTION_SERVICE = "transactionService";
    private static final String KEY_DOC_SETTINGS = "documentGlobalSettings";

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        final DocumentService documentService = (DocumentService) jobExecutionContext.getJobDetail().getJobDataMap().get(KEY_DOC_SERVICE);
        final NodeService nodeService = (NodeService) jobExecutionContext.getJobDetail().getJobDataMap().get(KEY_NODE_SERVICE);
        final NotificationsService notificationsService = (NotificationsService) jobExecutionContext.getJobDetail().getJobDataMap().get(KEY_NOTIFICATION_SERVICE);
        final IWorkCalendar calendarBean = (IWorkCalendar) jobExecutionContext.getJobDetail().getJobDataMap().get(KEY_CALENDAR_SERVICE);
        final InternalServiceImpl internalService = (InternalServiceImpl) jobExecutionContext.getJobDetail().getJobDataMap().get(KEY_INTERNAL_SERVICE);
        final DocumentConnectionService connectionService = (DocumentConnectionService) jobExecutionContext.getJobDetail().getJobDataMap().get(KEY_CONNECTION_SERVICE);
        final TransactionService transactionService = (TransactionService) jobExecutionContext.getJobDetail().getJobDataMap().get(KEY_TRANSACTION_SERVICE);
        final DocumentGlobalSettingsService documentGlobalSettings = (DocumentGlobalSettingsService) jobExecutionContext.getJobDetail().getJobDataMap().get(KEY_DOC_SETTINGS);

        AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
            public Object doWork() {
//				TODO: Скорее всего выполняется в транзакции шедулера
//                return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
//                    @Override
//                    public NodeRef execute() throws Throwable {
                        List<NodeRef> internals = getInternal(documentService, documentGlobalSettings, calendarBean);

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
                            int days = documentGlobalSettings.getSettingsNDays();
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
                        return null;
//                    }
//                });
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

    private List<NodeRef> getInternal(DocumentService documentService, DocumentGlobalSettingsService documentlSettings, IWorkCalendar calendarBean) {
        // ищем внутренние документы, в статусе "Направлен", срок исполнения которых истекается менее чем через N дней
        Date start = new Date(0);

        Calendar calendar = Calendar.getInstance();
        int days = documentlSettings.getSettingsNDays();
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
