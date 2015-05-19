package ru.it.lecm.contracts.schedule;

import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.contracts.beans.ContractsBeanImpl;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.notifications.beans.Notification;
import ru.it.lecm.notifications.beans.NotificationsService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: dbashmakov
 * Date: 24.04.13
 * Time: 14:04
 */
public class InitiatorEveryDayNotificationExecutor extends ActionExecuterAbstractBase {
    private ContractsBeanImpl contractsService;
    private NotificationsService notificationsService;
    private NodeService nodeService;
    private DocumentService documentService;

    public void setContractsService(ContractsBeanImpl contractsService) {
        this.contractsService = contractsService;
    }

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
        Notification notification = new Notification();

        ArrayList<NodeRef> employeeList = new ArrayList<NodeRef>();
        NodeRef initiator = documentService.getDocumentAuthor(nodeRef);
        if (initiator != null) {
            employeeList.add(initiator);
        }

        notification.setRecipientEmployeeRefs(employeeList);

        notification.setAuthor(AuthenticationUtil.getSystemUserName());
        notification.setDescription(getNotificationDescription(nodeRef));
        notification.setObjectRef(nodeRef);
        notification.setInitiatorRef(null);
        notificationsService.sendNotification(notification);
    }

    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> parameterDefinitions) {
    }

    private String getNotificationDescription(NodeRef document) {
        Date now = new Date();
        Date contractEndDate = (Date) nodeService.getProperty(document, ContractsBeanImpl.PROP_END_DATE);
        String docDesc = contractsService.wrapperLink(document, nodeService.getProperty(document, DocumentService.PROP_PRESENT_STRING).toString(), documentService.getDocumentUrl(document));
        String desc;
        if (contractEndDate != null && now.before(contractEndDate)) {
            desc = "Начинается срок исполнения договора " + docDesc;
        } else {
            desc = "Заканчивается срок исполнения договора " + docDesc;
        }
        return desc;
    }
}
