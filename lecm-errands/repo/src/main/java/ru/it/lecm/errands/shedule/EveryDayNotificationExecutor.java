package ru.it.lecm.errands.shedule;

import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.errands.ErrandsService;
import ru.it.lecm.errands.beans.ErrandsServiceImpl;
import ru.it.lecm.notifications.beans.Notification;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.statemachine.StatemachineModel;

import java.util.*;

/**
 * User: mshafeev
 * Date: 26.07.13
 * Time: 10:52
 */
public class EveryDayNotificationExecutor extends ActionExecuterAbstractBase {
    private ErrandsServiceImpl errandsService;
    private NotificationsService notificationsService;
    private NodeService nodeService;
    private DocumentService documentService;

    public void setErrandsService(ErrandsServiceImpl errandsService) {
        this.errandsService = errandsService;
    }

    public void setNotificationsService(NotificationsService notificationsService) {
        this.notificationsService = notificationsService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @Override
    protected void executeImpl(Action action, NodeRef nodeRef) {
        String notificationDescription;
        Date now = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_MONTH, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        now = calendar.getTime();

        // Уведомление о начале отложенного поручения
        Date startDate = (Date)nodeService.getProperty(nodeRef, ErrandsService.PROP_ERRANDS_START_DATE);

        if (startDate != null) {
            calendar.setTime(startDate);
            calendar.add(Calendar.DAY_OF_MONTH, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            startDate = calendar.getTime();
            if (startDate.equals(now)) {
                notificationDescription = "Наступил срок выполнения поручения: " +
                        errandsService.wrapperLink(nodeRef, nodeService.getProperty(nodeRef, ErrandsService.PROP_ERRANDS_TITLE).toString(), documentService.getDocumentUrl(nodeRef));
                sendNotification(nodeRef, getEmployeeList(nodeRef), notificationDescription);
            }
        }

        // Уведомление о приближении срока исполнения поручения.
        Date limitDate = (Date)nodeService.getProperty(nodeRef, ErrandsService.PROP_ERRANDS_LIMITATION_DATE);

        calendar.add(Calendar.DAY_OF_MONTH, 5);
        now = calendar.getTime();
        if (limitDate != null && limitDate.before(now)) {
            notificationDescription = "Приближается срок выполнения поручения: " +
                    errandsService.wrapperLink(nodeRef, nodeService.getProperty(nodeRef, ErrandsService.PROP_ERRANDS_TITLE).toString(), documentService.getDocumentUrl(nodeRef));
            sendNotification(nodeRef, getEmployeeList(nodeRef), notificationDescription);
        }

        // Уведомление о направленном поручении
        if (nodeService.getProperty(nodeRef, StatemachineModel.PROP_STATUS).equals("Ожидает исполнения")) {
            notificationDescription = "Вам направлено " +
                    errandsService.wrapperLink(nodeRef, nodeService.getProperty(nodeRef, DocumentService.PROP_PRESENT_STRING).toString(), documentService.getDocumentUrl(nodeRef));
            sendNotification(nodeRef, getEmployeeList(nodeRef), notificationDescription);
        }

    }

    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
    }

    private void sendNotification(NodeRef document, List<NodeRef> employeeList, String notificationDescription){
        Notification notification = new Notification();
        notification.setRecipientEmployeeRefs(employeeList); // значения должны быть уникальны
        notification.setAuthor(AuthenticationUtil.getSystemUserName());
        notification.setDescription(notificationDescription);
        notification.setObjectRef(document);
        notificationsService.sendNotification(notification);

    }

    /**
     * Получаем список участников: исполнителей и контроллеров
     *
     * @param document подписка на документ
     * @return список исполнителей и контроллеров
     */
    private ArrayList<NodeRef> getEmployeeList(NodeRef document) {
        Set<NodeRef> employee = new HashSet<NodeRef>();

        List<AssociationRef> employeeAssocs = new ArrayList<AssociationRef>();

        employeeAssocs.addAll(nodeService.getTargetAssocs(document, ErrandsService.ASSOC_ERRANDS_EXECUTOR));
        employeeAssocs.addAll(nodeService.getTargetAssocs(document, ErrandsService.ASSOC_ERRANDS_CONTROLLER));

        if (!employeeAssocs.isEmpty()) {
            for (AssociationRef employeeAssoc : employeeAssocs) {
                employee.add(employeeAssoc.getTargetRef());
            }
        }

        return new ArrayList<NodeRef>(employee);
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
}
