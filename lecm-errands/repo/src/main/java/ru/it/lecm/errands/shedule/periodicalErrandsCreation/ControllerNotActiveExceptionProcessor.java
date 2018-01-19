package ru.it.lecm.errands.shedule.periodicalErrandsCreation;

import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.errands.ErrandsService;
import ru.it.lecm.errands.shedule.exceptionProcessor.ProcessorParamName;
import ru.it.lecm.notifications.beans.NotificationsService;

import java.util.*;

/**
 * User: IGanin
 * Date: 17.11.2017
 * Time: 17:25
 */
public class ControllerNotActiveExceptionProcessor extends BaseCreationExceptionProcessor {

    private NotificationsService notificationsService;

    public void setNotificationsService(NotificationsService notificationsService) {
        this.notificationsService = notificationsService;
    }

    /**
     * Если хотя бы один контролер неактивен, то нужно запустить процессор.
     */
    @Override
    public boolean checkConditionsToProcess(final Map<ProcessorParamName, Object> params) {
        final NodeRef periodicalErrand = (NodeRef) params.get(ProcessorParamName.PERIODICAL_ERRAND);

        final List<AssociationRef> controllerAssocs = nodeService.getTargetAssocs(periodicalErrand, ErrandsService.ASSOC_ERRANDS_CONTROLLER);
        if (controllerAssocs != null && controllerAssocs.size() > 0) {
            for (AssociationRef controllerAssoc : controllerAssocs) {
                if (!isEmployeeActive(controllerAssoc.getTargetRef())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void processException(final Map<ProcessorParamName, Object> params) {
        try {
            final NodeRef errandNodeRef = (NodeRef) params.get(ProcessorParamName.ERRAND);
            if (errandNodeRef != null) {
                final Set<NodeRef> recipients = new HashSet<>();
                // Уведомляем админинстратору
                NodeRef adminEmployee = getAdminEmployee();
                if (adminEmployee != null) {
                    recipients.add(adminEmployee);
                }
                // Если в настройках действие NOTIFY_ADMIN_AND_AUTHOR, то уведомляем админа и автора
                final ErrandsService.EmployeeNotActiveAction action = errandsService.getControllerNotActiveAction();
                if (ErrandsService.EmployeeNotActiveAction.NOTIFY_ADMIN_AND_AUTHOR.equals(action)) {
                    addAuthorToRecipients(recipients, errandNodeRef);
                }

                // Уведомление о неактивных контролерах
                final List<AssociationRef> controllerAssocs = nodeService.getTargetAssocs(errandNodeRef, ErrandsService.ASSOC_ERRANDS_CONTROLLER);
                if (controllerAssocs != null && controllerAssocs.size() > 0) {
                    for (AssociationRef controllerAssoc : controllerAssocs) {
                        if (!isEmployeeActive(controllerAssoc.getTargetRef())) {
                            final Map<String, Object> objects = new HashMap<>();
                            objects.put("actor", controllerAssoc.getTargetRef());
                            notificationsService.sendNotificationByTemplate(errandNodeRef,  new ArrayList<NodeRef>(recipients), "ERRANDS_CONTROLER_NOT_ACTIVE", objects);
                        }
                    }
                }
            }
        } catch (Throwable e) {
            logger.error("Failed to execute processor " + getClass().getSimpleName(), e);
        }
    }
}
