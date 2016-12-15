package ru.it.lecm.errands.shedule;

import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.errands.ErrandsService;
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
    private NotificationsService notificationsService;
    private NodeService nodeService;
    private BusinessJournalService businessJournalService;

    public void setBusinessJournalService(BusinessJournalService businessJournalService) {
        this.businessJournalService = businessJournalService;
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
        nodeService.setProperty(nodeRef, ErrandsService.PROP_ERRANDS_IS_EXPIRED, true);

        // формируем уведомление Исполнителю, Инициатору и Контроллеру:
        notificationsService.sendNotificationByTemplate(nodeRef, getEmployeeList(nodeRef), "ERRANDS_EXCEEDED_DEADLINE");
        // формируем запись в журнал
        String logText = "Срок исполнения поручения #mainobject превышен";
        businessJournalService.log(nodeRef, "ERRAND_EXPIRED", logText);
    }

    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
    }


    /**
     * Получаем список участников в подписке: Исполнителей, Инициатора и Контроллера
     *
     * @param document
     * @return
     */
    private ArrayList<NodeRef> getEmployeeList(NodeRef document) {
        Set<NodeRef> employee = new HashSet<>();

        List<AssociationRef> employeeAssocs = new ArrayList<>();

        employeeAssocs.addAll(nodeService.getTargetAssocs(document, ErrandsService.ASSOC_ERRANDS_EXECUTOR));
        employeeAssocs.addAll(nodeService.getTargetAssocs(document, ErrandsService.ASSOC_ERRANDS_INITIATOR));
        employeeAssocs.addAll(nodeService.getTargetAssocs(document, ErrandsService.ASSOC_ERRANDS_CONTROLLER));
        employeeAssocs.addAll(nodeService.getTargetAssocs(document, ErrandsService.ASSOC_ERRANDS_CO_EXECUTORS));

        if (!employeeAssocs.isEmpty()) {
            for (AssociationRef employeeAssoc : employeeAssocs) {
                employee.add(employeeAssoc.getTargetRef());
            }
        }

        return new ArrayList<>(employee);
    }

}
