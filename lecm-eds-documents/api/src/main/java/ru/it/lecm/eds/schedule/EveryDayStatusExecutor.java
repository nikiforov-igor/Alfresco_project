package ru.it.lecm.eds.schedule;

import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.eds.api.EDSDocumentService;
import ru.it.lecm.notifications.beans.NotificationsService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EveryDayStatusExecutor extends ActionExecuterAbstractBase {
    private NotificationsService notificationsService;
    private NodeService nodeService;
    private List<QName> recipientsQNames;

    public void setNotificationsService(NotificationsService notificationsService) {
        this.notificationsService = notificationsService;
    }

    public void setRecipientsQNames(List<QName> recipientsQNames) {
        this.recipientsQNames = recipientsQNames;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @Override
    protected void executeImpl(Action action, NodeRef nodeRef) {
        // выставляем атрибут "Просрочен" в значение true
        nodeService.setProperty(nodeRef, EDSDocumentService.PROP_IS_EXPIRED, true);

        // формируем уведомление:
        notificationsService.sendNotificationByTemplate(nodeRef, getEmployeeList(nodeRef), "EDS_DOCUMENT_IS_EXPIRED");
    }

    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
    }

    /**
     * Получаем список участников в подписке
     *
     * @param document
     * @return
     */
    private ArrayList<NodeRef> getEmployeeList(NodeRef document) {
        Set<NodeRef> employee = new HashSet<>();
        List<AssociationRef> employeeAssocs = new ArrayList<>();
        for (QName recipientQName : recipientsQNames) {
            employeeAssocs.addAll(nodeService.getTargetAssocs(document, recipientQName));
        }

        if (!employeeAssocs.isEmpty()) {
            for (AssociationRef employeeAssoc : employeeAssocs) {
                employee.add(employeeAssoc.getTargetRef());
            }
        }

        return new ArrayList<>(employee);
    }

}
