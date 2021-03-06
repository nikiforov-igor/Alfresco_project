package ru.it.lecm.eds.scripts;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.ISO8601DateFormat;
import org.alfresco.util.ParameterCheck;
import org.apache.commons.lang.StringUtils;
import org.mozilla.javascript.Scriptable;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.eds.api.EDSDocumentService;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.LecmPermissionService;

import java.util.*;

/**
 * User: pmelnikov
 * Date: 14.03.14
 * Time: 13:14
 */
public class EDSDocumentWebScriptBean extends BaseWebScript {

    private OrgstructureBean orgstructureService;
    private NodeService nodeService;
    private EDSDocumentService edsService;
    private NotificationsService notificationsService;
    private DocumentService documentService;
    private BusinessJournalService businessJournalService;
    private SubstitudeBean substitudeBean;
    private LecmPermissionService lecmPermissionService;

    public void setSubstitudeBean(SubstitudeBean substitudeBean) {
        this.substitudeBean = substitudeBean;
    }

    public void setBusinessJournalService(BusinessJournalService businessJournalService) {
        this.businessJournalService = businessJournalService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setNotificationsService(NotificationsService notificationsService) {
        this.notificationsService = notificationsService;
    }

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public void setEdsService(EDSDocumentService edsService) {
        this.edsService = edsService;
    }

    public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
        this.lecmPermissionService = lecmPermissionService;
    }

    /**
     * ???????????????? ???????????? ?????????????? ?????????????????????? ?????? ??????-??????????????????
     * @param document  ????????????????
     */
    public List<String> getRecipientLogins(ScriptNode document) {
        NodeRef doc = document.getNodeRef();

        List<String> result = new ArrayList<String>();

        List<AssociationRef> recipients = nodeService.getTargetAssocs(doc, EDSDocumentService.ASSOC_RECIPIENTS);
        for (AssociationRef recipient : recipients) {
            result.add(orgstructureService.getEmployeeLogin(recipient.getTargetRef()));
        }
        return result;
    }

    public void sendChildChangeSignal(ScriptNode baseDocument) {
        ParameterCheck.mandatory("baseDocument", baseDocument);
        edsService.sendChildChangeSignal(baseDocument.getNodeRef());
    }

    public void resetChildChangeSignal(ScriptNode baseDocument) {
        ParameterCheck.mandatory("baseDocument", baseDocument);
        edsService.resetChildChangeSignal(baseDocument.getNodeRef());
    }

    public void sendChangeDueDateSignal(ScriptNode baseDocument, Long shiftSize, Boolean limitless, Date newDate, String reason) {
        ParameterCheck.mandatory("baseDocument", baseDocument);
        edsService.sendChangeDueDateSignal(baseDocument.getNodeRef(), shiftSize, limitless, newDate, reason);
    }

    public void resetChangeDueDateSignal(ScriptNode baseDocument) {
        ParameterCheck.mandatory("baseDocument", baseDocument);
        edsService.resetChangeDueDateSignal(baseDocument.getNodeRef());
    }

    public Date convertComplexDate(String radio, Date date, String daysType, Integer days) {
        ParameterCheck.mandatory("radio", radio);

        return edsService.convertComplexDate(radio, date, daysType, days);
    }

    public Date convertComplexDateString(String radio, String date, String daysType, String days) {
        ParameterCheck.mandatory("radio", radio);

        Integer daysInt = StringUtils.isNotEmpty(days) ? Integer.parseInt(days) : null;
        Date dateParsed = StringUtils.isNotEmpty(date) ? ISO8601DateFormat.parse(date) : null;

        return convertComplexDate(radio, dateParsed, daysType, daysInt);
    }

    public String getComplexDateText(String radio, String date, String daysType, String days) {
        ParameterCheck.mandatory("radio", radio);

        Integer daysInt = StringUtils.isNotEmpty(days) ? Integer.parseInt(days) : null;
        Date dateParsed = StringUtils.isNotEmpty(date) ? ISO8601DateFormat.parse(date) : null;

        return edsService.getComplexDateText(radio, dateParsed, daysType, daysInt);
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    /**
     * ???????????????? ?????????????? ?? ?????????????????????????? ????????????????????
     * @param doc ????????????????, ???????????????? ???????????????????????? ????????????
     * @param reason ?????????????? ??????????????
     * @param sender ?????????????????????? ??????????????
     */
    public void sendCompletionSignal(ScriptNode doc, String reason, ScriptNode sender) {
        NodeRef document = doc.getNodeRef();
        NodeRef signalSender = sender.getNodeRef();
        edsService.sendCompletionSignal(document, reason, signalSender);
    }

    /**
     * ?????????? ?????????????? ????????????????????
     * @param doc ???????? ??????????????????
     */
    public void resetCompletionSignal(ScriptNode doc) {
        NodeRef document = doc.getNodeRef();
        edsService.resetCompletionSignal(document);
    }

    public void sendNotificationAndLogAboutAutoRegistration(ScriptNode document, String presentStringWithProjectNumber) {
        NodeRef currentEmployee = orgstructureService.getCurrentEmployee();
        String author = orgstructureService.getEmployeeLogin(currentEmployee);
        String templateCode = "EDS_DOCUMENT_AUTO_REGISTERED";

        List<NodeRef> recipients = new ArrayList<>();
        recipients.add(documentService.getDocumentAuthor(document.getNodeRef()));
        List<NodeRef> initiators = lecmPermissionService.getEmployeesByDynamicRole(document.getNodeRef(),EDSDocumentService.DYNAMIC_ROLE_CODE_INITIATOR);
        if (initiators != null && initiators.size() > 0) {
            recipients.addAll(initiators);
        }

        Map<String, Object> config = new HashMap<>();
        config.put("mainObject", document.getNodeRef());
        config.put("presentStringWithProjectNumber", presentStringWithProjectNumber);

        notificationsService.sendNotification(author, null, recipients, templateCode, config, false);

        String logText = EDSDocumentService.getFromMessagesOrDefaultValue("ru.it.lecm.eds.bjMessages.autoRegistration", "???????????????? %s ?????????????????????????????? ???????????????? ??????????????????????????. ???????????????? ?????????? {~REGNUM} ???? ???????? {~REGDATE}.");
        logText = String.format(logText, wrapperLink(document.getNodeRef().toString(), presentStringWithProjectNumber, documentService.getDocumentUrl(document.getNodeRef())));
        logText = substitudeBean.formatNodeTitle(document.getNodeRef(), logText);
        businessJournalService.log(document.getNodeRef(), "EDS_AUTO_REGISTRATION", logText, null);
    }

    /**
     * ???????????????? ???? ???????????????? ????????????????
     */
    public boolean isSignedOnPaper(ScriptNode document) {
        NodeRef docNodeRef = document.getNodeRef();
        return edsService.isSignedOnPaper(docNodeRef);
    }

    public Scriptable getCategoriesToSign(ScriptNode document, String documentTypeAssoc) {
        ParameterCheck.mandatory("document", document);
        ParameterCheck.mandatory("documentTypeAssoc", documentTypeAssoc);

        List<NodeRef> result = edsService.getCategoriesToSign(document.getNodeRef(), documentTypeAssoc);
        return createScriptable(result);
    }
}
