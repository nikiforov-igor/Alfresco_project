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
public class ExcecutorNotActiveExceptionProcessor extends BaseCreationExceptionProcessor {

    private NotificationsService notificationsService;

    public void setNotificationsService(NotificationsService notificationsService) {
        this.notificationsService = notificationsService;
    }

    /**
     * Если исполнитель неактивен, то нужно запустить процессор.
     */
    @Override
    public boolean checkConditionsToProcess(final Map<ProcessorParamName, Object> params) {
        final NodeRef periodicalErrand = (NodeRef) params.get(ProcessorParamName.PERIODICAL_ERRAND);

        final List<AssociationRef> executorAssocs = nodeService.getTargetAssocs(periodicalErrand, ErrandsService.ASSOC_ERRANDS_EXECUTOR);
        return executorAssocs != null && executorAssocs.size() > 0 && !isEmployeeActive(executorAssocs.get(0).getTargetRef());
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
                final ErrandsService.EmployeeNotActiveAction action = errandsService.getExecutorNotActiveAction();
                if (ErrandsService.EmployeeNotActiveAction.NOTIFY_ADMIN_AND_AUTHOR.equals(action)) {
                    addAuthorToRecipients(recipients, errandNodeRef);
                }

                // Уведомление о неактивном исполнителе
                final List<AssociationRef> executorAssocs = nodeService.getTargetAssocs(errandNodeRef, ErrandsService.ASSOC_ERRANDS_EXECUTOR);
                if (executorAssocs != null && executorAssocs.size() > 0) {
                    final NodeRef executor = executorAssocs.get(0).getTargetRef();
                    final Map<String, Object> objects = new HashMap<>();
                    objects.put("actor", executor);
                    notificationsService.sendNotificationByTemplate(errandNodeRef, new ArrayList<NodeRef>(recipients), "ERRANDS_EXECUTOR_NOT_ACTIVE", objects);
                }
            }
        } catch (Throwable e) {
            logger.error("Failed to execute processor " + getClass().getSimpleName(), e);
        }
    }
}
