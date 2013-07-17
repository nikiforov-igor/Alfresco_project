package ru.it.lecm.contracts.schedule;

import org.alfresco.model.ContentModel;
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
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

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
    private OrgstructureBean orgstructureService;
    private NodeService nodeService;

    public void setContractsService(ContractsBeanImpl contractsService) {
        this.contractsService = contractsService;
    }

    public void setNotificationsService(NotificationsService notificationsService) {
        this.notificationsService = notificationsService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    @Override
    protected void executeImpl(Action action, NodeRef nodeRef) {
        Notification notification = new Notification();

        ArrayList<NodeRef> employeeList = new ArrayList<NodeRef>();
        employeeList.add(getInitiator(nodeRef));
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

    private NodeRef getInitiator(NodeRef document) {
        String personName = (String) nodeService.getProperty(document, ContentModel.PROP_CREATOR);
        return orgstructureService.getEmployeeByPerson(personName);
    }

    private String getNotificationDescription(NodeRef document) {
        Date now = new Date();
        Date contractEndDate = (Date) nodeService.getProperty(document, ContractsBeanImpl.PROP_END_DATE);
        String docDesc = contractsService.wrapperLink(document, nodeService.getProperty(document, DocumentService.PROP_PRESENT_STRING).toString(), BaseBean.DOCUMENT_LINK_URL);
        String desc;
        if (contractEndDate != null && now.before(contractEndDate)) {
            desc = "Начинается срок исполнения договора " + docDesc;
        } else {
            desc = "Заканчивается срок исполнения договора " + docDesc;
        }
        return desc;
    }
}
