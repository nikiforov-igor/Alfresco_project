package ru.it.lecm.resolutions.shedule;

import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.resolutions.api.ResolutionsService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: AIvkin
 * Date: 17.01.2017
 * Time: 17:20
 */
public class ResolutionsExpiredExecutor extends ActionExecuterAbstractBase {
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
        Boolean expired = (Boolean) nodeService.getProperty(nodeRef, ResolutionsService.PROP_IS_EXPIRED);
        if (expired) {
            notificationsService.sendNotificationByTemplate(nodeRef, getEmployeeList(nodeRef), "RESOLUTION_EXPIRED_REMEMBER");
        } else {
            nodeService.setProperty(nodeRef, ResolutionsService.PROP_IS_EXPIRED, true);
            notificationsService.sendNotificationByTemplate(nodeRef, getEmployeeList(nodeRef), "RESOLUTION_EXPIRED_MESSAGE");
            businessJournalService.log(nodeRef, "ERRAND_EXPIRED", "Истек срок исполнения для #mainobject");
        }
    }

    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
    }

    private List<NodeRef> getEmployeeList(NodeRef document) {
        Set<NodeRef> employee = new HashSet<>();

        List<AssociationRef> employeeAssocs = new ArrayList<>();

        employeeAssocs.addAll(nodeService.getTargetAssocs(document, ResolutionsService.ASSOC_AUTHOR));
        employeeAssocs.addAll(nodeService.getTargetAssocs(document, ResolutionsService.ASSOC_CONTROLLER));

        if (!employeeAssocs.isEmpty()) {
            for (AssociationRef employeeAssoc : employeeAssocs) {
                employee.add(employeeAssoc.getTargetRef());
            }
        }

        return new ArrayList<>(employee);
    }
}
