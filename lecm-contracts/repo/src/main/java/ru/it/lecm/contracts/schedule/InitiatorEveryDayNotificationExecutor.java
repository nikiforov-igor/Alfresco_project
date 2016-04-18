package ru.it.lecm.contracts.schedule;

import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import ru.it.lecm.contracts.beans.ContractsBeanImpl;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.notifications.beans.NotificationsService;

import java.util.*;

/**
 * User: dbashmakov
 * Date: 24.04.13
 * Time: 14:04
 */
public class InitiatorEveryDayNotificationExecutor extends ActionExecuterAbstractBase {
    private NotificationsService notificationsService;
    private NodeService nodeService;
    private DocumentService documentService;

    public void setNotificationsService(NotificationsService notificationsService) {
        this.notificationsService = notificationsService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    @Override
    protected void executeImpl(Action action, NodeRef nodeRef) {
        ArrayList<NodeRef> employeeList = new ArrayList<>();
        NodeRef initiator = documentService.getDocumentAuthor(nodeRef);
        employeeList.add(initiator);

        Map<String, Object> objects = new HashMap<>();
        objects.put("isStarting", isStarting(nodeRef));

        notificationsService.sendNotificationByTemplate(nodeRef, employeeList, "CONTRACT_EXECUTION_TERM", objects);
    }

    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> parameterDefinitions) {
    }

    private Boolean isStarting(NodeRef document) {
        Date now = new Date();
        Date contractEndDate = (Date) nodeService.getProperty(document, ContractsBeanImpl.PROP_END_DATE);
        return contractEndDate != null && now.before(contractEndDate);
    }
}
