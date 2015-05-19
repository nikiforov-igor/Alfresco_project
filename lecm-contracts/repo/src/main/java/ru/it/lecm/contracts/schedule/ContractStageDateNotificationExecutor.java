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
import ru.it.lecm.documents.beans.DocumentTableService;
import ru.it.lecm.notifications.beans.Notification;
import ru.it.lecm.notifications.beans.NotificationsService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * User: PMelnikov
 * Date: 20.11.13
 * Time: 9:27
 */
public class ContractStageDateNotificationExecutor extends ActionExecuterAbstractBase {

    private ContractsBeanImpl contractsService;
    private NotificationsService notificationsService;
    private NodeService nodeService;
    private DocumentService documentService;
    private DocumentTableService documentTableService;

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

    public void setDocumentTableService(DocumentTableService documentTableService) {
        this.documentTableService = documentTableService;
    }

    @Override
    protected void executeImpl(Action action, NodeRef nodeRef) {
        Notification notification = new Notification();

        NodeRef documentRef = documentTableService.getDocumentByTableDataRow(nodeRef);
        ArrayList<NodeRef> employeeList = new ArrayList<NodeRef>();
        NodeRef initiator = documentService.getDocumentAuthor(documentRef);
        if (initiator != null) {
            employeeList.add(initiator);
        }

        String description = getNotificationDescription(documentRef, nodeRef);
        if (description != null) {
            notification.setRecipientEmployeeRefs(employeeList);
            notification.setAuthor(AuthenticationUtil.getSystemUserName());
            notification.setDescription(description);
            notification.setObjectRef(nodeRef);
            notification.setInitiatorRef(null);
            notificationsService.sendNotification(notification);
        }
    }

    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> parameterDefinitions) {
    }

    private String getNotificationDescription(NodeRef document, NodeRef row) {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startPoint = calendar.getTime();

        calendar.setTime(now);
        calendar.set(Calendar.HOUR_OF_DAY, 24);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date endPoint = calendar.getTime();

        Object stageStartDate = nodeService.getProperty(row, ContractsBeanImpl.PROP_STAGE_START_DATE);
        Object stageEndDate = nodeService.getProperty(row, ContractsBeanImpl.PROP_STAGE_END_DATE);
        Object stageStatus = nodeService.getProperty(row, ContractsBeanImpl.PROP_STAGE_STATUS);
        Integer index = (Integer) nodeService.getProperty(row, DocumentTableService.PROP_INDEX_TABLE_ROW);
        String docDesc = contractsService.wrapperLink(document, nodeService.getProperty(document, DocumentService.PROP_PRESENT_STRING).toString(), documentService.getDocumentUrl(document));

        Date normalStartDate = null;
        if (stageStartDate != null) {
            calendar.setTime((Date) stageStartDate);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            normalStartDate = calendar.getTime();
        }

        Date normalEndDate = null;
        if (stageEndDate != null) {
            calendar.setTime((Date) stageEndDate);
            calendar.set(Calendar.HOUR_OF_DAY, 24);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            normalEndDate = calendar.getTime();
        }

        String desc = null;
        if (normalStartDate != null && startPoint.equals(normalStartDate) && "Не начат".equals(stageStatus)) {
            desc = "Сегодня запланировано начало работ по этапу № " + index + " к договору " + docDesc;
        } else if (normalEndDate != null && endPoint.equals(normalEndDate) && !"Закрыт".equals(stageStatus)){
            desc = "Сегодня запланировано окончание работ по этапу № " + index + " к договору " + docDesc;
        }
        return desc;
    }
}