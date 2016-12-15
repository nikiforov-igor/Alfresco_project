package ru.it.lecm.errands.shedule;

import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import ru.it.lecm.errands.ErrandsService;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.statemachine.StatemachineModel;

import java.util.*;

/**
 * User: mshafeev
 * Date: 26.07.13
 * Time: 10:52
 */
public class EveryDayNotificationExecutor extends ActionExecuterAbstractBase {
    private NotificationsService notificationsService;
    private NodeService nodeService;

    public void setNotificationsService(NotificationsService notificationsService) {
        this.notificationsService = notificationsService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @Override
    protected void executeImpl(Action action, NodeRef nodeRef) {
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
                notificationsService.sendNotificationByTemplate(nodeRef, getEmployeeList(nodeRef), "ERRANDS_DEADLINE_COME");
            }
        }
        // Уведомлении об истечении срока исполнения
        boolean isExpired = (boolean) nodeService.getProperty(nodeRef, ErrandsService.PROP_ERRANDS_IS_EXPIRED);
        if (isExpired) {
            notificationsService.sendNotificationByTemplate(nodeRef, getEmployeeList(nodeRef), "ERRANDS_EXCEEDED_DEADLINE");
        }

        // Уведомление о истечении половины срока исполнения поручения.
        Date halfLimitDate = (Date) nodeService.getProperty(nodeRef, ErrandsService.PROP_ERRANDS_HALF_LIMIT_DATE);
        if(halfLimitDate != null && halfLimitDate.equals(now)){
            notificationsService.sendNotificationByTemplate(nodeRef, getEmployeeList(nodeRef), "ERRANDS_HALF_DEADLINE");
        }

        // Уведомление о приближении срока исполнения поручения.
        Date limitDate = (Date)nodeService.getProperty(nodeRef, ErrandsService.PROP_ERRANDS_LIMITATION_DATE);
        boolean isLimitShort = (boolean) nodeService.getProperty(nodeRef, ErrandsService.PROP_ERRANDS_IS_LIMIT_SHORT_DATE);
        if(isLimitShort) {
            // Уведомление о приближении срока исполнения карткосрочного поручения.
            calendar.add(Calendar.DAY_OF_MONTH, notificationsService.getSettingsShortNDays());
        }else{
            // Уведомление о приближении срока исполнения долгосрочного поручения.
            calendar.add(Calendar.DAY_OF_MONTH, notificationsService.getSettingsNDays());
        }
        now = calendar.getTime();
        if (limitDate != null && limitDate.before(now)) {
            notificationsService.sendNotificationByTemplate(nodeRef, getEmployeeList(nodeRef), "ERRANDS_APPROACHING_DEADLINE");
        }

        // Уведомление о направленном поручении
        if (nodeService.getProperty(nodeRef, StatemachineModel.PROP_STATUS).equals("Ожидает исполнения")) {
            notificationsService.sendNotificationByTemplate(nodeRef, getEmployeeList(nodeRef), "ERRANDS_DIRECTED");
        }

    }

    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
    }

    /**
     * Получаем список участников: исполнителей и контроллеров
     *
     * @param document подписка на документ
     * @return список исполнителей и контроллеров
     */
    private ArrayList<NodeRef> getEmployeeList(NodeRef document) {
        Set<NodeRef> employee = new HashSet<>();

        List<AssociationRef> employeeAssocs = new ArrayList<>();

        employeeAssocs.addAll(nodeService.getTargetAssocs(document, ErrandsService.ASSOC_ERRANDS_EXECUTOR));
        employeeAssocs.addAll(nodeService.getTargetAssocs(document, ErrandsService.ASSOC_ERRANDS_CONTROLLER));
        employeeAssocs.addAll(nodeService.getTargetAssocs(document, ErrandsService.ASSOC_ERRANDS_INITIATOR));
        employeeAssocs.addAll(nodeService.getTargetAssocs(document, ErrandsService.ASSOC_ERRANDS_CO_EXECUTORS));

        if (!employeeAssocs.isEmpty()) {
            for (AssociationRef employeeAssoc : employeeAssocs) {
                employee.add(employeeAssoc.getTargetRef());
            }
        }

        return new ArrayList<>(employee);
    }

}
