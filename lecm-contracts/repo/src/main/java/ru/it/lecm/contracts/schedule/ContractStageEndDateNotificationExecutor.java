package ru.it.lecm.contracts.schedule;

import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.documents.beans.DocumentGlobalSettingsService;
import ru.it.lecm.documents.beans.DocumentMembersService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.documents.beans.DocumentTableService;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.util.*;

/**
 * User: AZinovin
 * Date: 12.05.16
 * Time: 10:51
 */
public class ContractStageEndDateNotificationExecutor extends ActionExecuterAbstractBase {

    private NotificationsService notificationsService;
    private DocumentTableService documentTableService;
    private DocumentMembersService documentMembersService;
    private DocumentService documentService;
    private OrgstructureBean orgstructureService;
    private NodeService nodeService;
    private NamespaceService namespaceService;
    private DocumentGlobalSettingsService documentGlobalSettings;

    private String templateCode = "CONTRACT_STAGE_END";

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    public void setNotificationsService(NotificationsService notificationsService) {
        this.notificationsService = notificationsService;
    }

    public void setDocumentTableService(DocumentTableService documentTableService) {
        this.documentTableService = documentTableService;
    }

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public void setDocumentMembersService(DocumentMembersService documentMembersService) {
        this.documentMembersService = documentMembersService;
    }

    public void setDocumentGlobalSettings(DocumentGlobalSettingsService documentGlobalSettings) {
        this.documentGlobalSettings = documentGlobalSettings;
    }

    @Override
    protected void executeImpl(Action action, NodeRef stageRef) {
        NodeRef contractRef = documentTableService.getDocumentByTableDataRow(stageRef);
        final String contractStatus = (String) nodeService.getProperty(contractRef, QName.createQName("lecm-statemachine:status", namespaceService));
        if (!"Действует".equals(contractStatus)) {
            return; //Рассылать уведомления необходимо только по действующим договорам
        }

        final Date contractStatusDate = (Date) nodeService.getProperty(contractRef, QName.createQName("lecm-document:status-changed-date", namespaceService));
        final Date stageEndDate = (Date) nodeService.getProperty(stageRef, QName.createQName("lecm-contract-table-structure:end-date", namespaceService));
        if ((stageEndDate.getTime() - contractStatusDate.getTime()) < 1000 * 3600 * 24 * documentGlobalSettings.getSettingsNDays()) {
            return; //Если с момента регистрации (возобновления действия) договора и до ближайшей даты завершения этапа по договору осталось менее указанного количества дней, уведомление не рассылается
        }

        Map<String, Object> config = new HashMap<>();
        config.put("mainObject", contractRef);
        config.put("stage", stageRef);

        final NodeRef author = documentService.getDocumentAuthor(contractRef);

        List<NodeRef> recipients = new ArrayList<>();
        recipients.add(author);
        recipients.addAll(orgstructureService.getEmployeesByBusinessRole("CONTRACT_EXECUTOR"));
        recipients.addAll(orgstructureService.getEmployeesByBusinessRole("CONTRACT_CURATOR"));

        notificationsService.sendNotification(AuthenticationUtil.getSystemUserName(), null, recipients, templateCode, config, false);
    }

    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> parameterDefinitions) {
    }
}