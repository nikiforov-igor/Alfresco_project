package ru.it.lecm.contracts.schedule;

import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import ru.it.lecm.contracts.beans.ContractsBeanImpl;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.documents.beans.DocumentTableService;
import ru.it.lecm.notifications.beans.NotificationsService;

import java.util.*;

/**
 * User: PMelnikov
 * Date: 20.11.13
 * Time: 9:27
 */
public class ContractStageDateNotificationExecutor extends ActionExecuterAbstractBase {

    private NotificationsService notificationsService;
    private NodeService nodeService;
    private DocumentService documentService;
    private DocumentTableService documentTableService;

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
        Boolean isStartPoint = getIsStartPoint(nodeRef);
        if (isStartPoint != null) {
            Integer stageNumber = (Integer) nodeService.getProperty(nodeRef, DocumentTableService.PROP_INDEX_TABLE_ROW);
            NodeRef documentRef = documentTableService.getDocumentByTableDataRow(nodeRef);
            Map<String, Object> objects = new HashMap<>();
            objects.put("stageNumber", stageNumber);
            objects.put("isStartPoint", isStartPoint);

            ArrayList<NodeRef> employeeList = new ArrayList<>();
            NodeRef initiator = documentService.getDocumentAuthor(documentRef);
            employeeList.add(initiator);

            notificationsService.sendNotificationByTemplate(documentRef, employeeList, "CONTRACT_WORK_PLANNING", objects);
        }
    }

    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> parameterDefinitions) {
    }

    private Boolean getIsStartPoint(NodeRef row) {
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

        if (normalStartDate != null && startPoint.equals(normalStartDate) && "Не начат".equals(stageStatus)) {
            return true;
        } else if (normalEndDate != null && endPoint.equals(normalEndDate) && !"Закрыт".equals(stageStatus)){
            return false;
        }
        return null;
    }
}