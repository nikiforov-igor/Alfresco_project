package ru.it.lecm.errands.shedule.processors;

import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.errands.ErrandsService;
import ru.it.lecm.notifications.beans.NotificationsService;

import java.util.*;

/**
 * User: IGanin
 * Date: 17.11.2017
 * Time: 17:25
 */
public class CoexcecutorNotActiveExceptionProcessor extends BaseCreationExceptionProcessor {

    private NotificationsService notificationsService;

    public void setNotificationsService(NotificationsService notificationsService) {
        this.notificationsService = notificationsService;
    }

    /**
     * Если хотя бы один соисполнитель неактивен, то нужно запустить процессор.
     */
    @Override
    public boolean checkConditionsToProcess(final Map<ProcessorParamName, Object> params) {
        final NodeRef periodicalErrand = (NodeRef) params.get(ProcessorParamName.PERIODICAL_ERRAND);

        final List<AssociationRef> coexecutorAssocs = nodeService.getTargetAssocs(periodicalErrand, ErrandsService.ASSOC_ERRANDS_CO_EXECUTORS);
        if (coexecutorAssocs != null && coexecutorAssocs.size() > 0) {
            for (AssociationRef coexecutorAssoc : coexecutorAssocs) {
                if (!isEmployeeActive(coexecutorAssoc.getTargetRef())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void processException(final Map<ProcessorParamName, Object> params) {
        try {
            final Set<NodeRef> recipients = new HashSet<>();
            final NodeRef errandNodeRef = (NodeRef) params.get(ProcessorParamName.PERIODICAL_ERRAND);
            if (errandNodeRef != null) {
                // Уведомляем админинстратору
                NodeRef adminEmployee = getAdminEmployee();
                if (adminEmployee != null) {
                    recipients.add(adminEmployee);
                }
                // Если в настройках действие NOTIFY_ADMIN_AND_AUTHOR, то уведомляем админа и автора
                final ErrandsService.EmployeeNotActiveAction action = errandsService.getCoexecutorNotActiveAction();
                if (ErrandsService.EmployeeNotActiveAction.NOTIFY_ADMIN_AND_AUTHOR.equals(action)) {
                    addAuthorToRecipients(recipients, errandNodeRef);
                }

                // Уведомление о неактивных соисполнителях
                final List<AssociationRef> coexecutorAssocs = nodeService.getTargetAssocs(errandNodeRef, ErrandsService.ASSOC_ERRANDS_CO_EXECUTORS);
                if (coexecutorAssocs != null && coexecutorAssocs.size() > 0) {
                    for (AssociationRef coexecutorAssoc : coexecutorAssocs) {
                        if (!isEmployeeActive(coexecutorAssoc.getTargetRef())) {
                            final Map<String, Object> objects = new HashMap<>();
                            objects.put("actor", coexecutorAssoc.getTargetRef());
                            notificationsService.sendNotificationByTemplate(errandNodeRef,  new ArrayList<NodeRef>(recipients), "ERRANDS_COEXECUTOR_NOT_ACTIVE", objects);
                        }
                    }
                }
            }
        } catch (Throwable e) {
            logger.error("Failed to execute processor " + getClass().getSimpleName(), e);
        }
    }

}
