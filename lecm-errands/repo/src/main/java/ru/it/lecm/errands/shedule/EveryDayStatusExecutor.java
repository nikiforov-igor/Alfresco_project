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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: mshafeev
 * Date: 24.07.13
 * Time: 15:48
 */
public class EveryDayStatusExecutor extends ActionExecuterAbstractBase {
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
        // выставляем атрибут "Просрочен" в значение true
        nodeService.setProperty(nodeRef, errandsService.PROP_ERRANDS_IS_EXPIRED, true);

        // формируем уведомление Исполнителю, Инициатору и Контроллеру:
        Notification notification = new Notification();

        List<NodeRef> employeeList = getEmployeeList(nodeRef);

        notification.setRecipientEmployeeRefs(employeeList); // значения должны быть уникальны
        notification.setAuthor(AuthenticationUtil.getSystemUserName());
        notification.setDescription(getNotificationDescription(nodeRef));
        notification.setObjectRef(nodeRef);
        notificationsService.sendNotification(notification);
    }

    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
    }

    private String getNotificationDescription(NodeRef document) {
        String docDesc = errandsService.wrapperLink(document, nodeService.getProperty(document, DocumentService.PROP_PRESENT_STRING).toString(), documentService.getDocumentUrl(document));
        Boolean justInTime = (Boolean) nodeService.getProperty(document, ErrandsService.PROP_ERRANDS_JUST_IN_TIME);
        if (justInTime) {
            return docDesc + " не исполнено в установленный срок. Работа с ним завершена, поручение переходит в статус \"Не исполнено\"";
        } else {
            return "Обратите внимание: поручение " + docDesc + " не исполнено в установленный срок.";
        }
    }

    /**
     * Получаем список участников в подписке: Исполнителей, Инициатора и Контроллера
     *
     * @param document
     * @return
     */
    private ArrayList<NodeRef> getEmployeeList(NodeRef document) {
        Set<NodeRef> employee = new HashSet<NodeRef>();

        List<AssociationRef> employeeAssocs = new ArrayList<AssociationRef>();

        employeeAssocs.addAll(nodeService.getTargetAssocs(document, ErrandsService.ASSOC_ERRANDS_EXECUTOR));
        employeeAssocs.addAll(nodeService.getTargetAssocs(document, ErrandsService.ASSOC_ERRANDS_INITIATOR));
        employeeAssocs.addAll(nodeService.getTargetAssocs(document, ErrandsService.ASSOC_ERRANDS_CONTROLLER));

        if (employeeAssocs != null && !employeeAssocs.isEmpty()) {
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
