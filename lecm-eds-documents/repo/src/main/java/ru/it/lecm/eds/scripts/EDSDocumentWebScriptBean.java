package ru.it.lecm.eds.scripts;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.ISO8601DateFormat;
import org.alfresco.util.ParameterCheck;
import org.apache.commons.lang.StringUtils;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.eds.api.EDSDocumentService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: pmelnikov
 * Date: 14.03.14
 * Time: 13:14
 */
public class EDSDocumentWebScriptBean extends BaseWebScript {

    private OrgstructureBean orgstructureService;
    private NodeService nodeService;
    private EDSDocumentService edsService;

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public void setEdsService(EDSDocumentService edsService) {
        this.edsService = edsService;
    }

    /**
     * Получить список логинов получателей для СЭД-документа
     * @param document  документ
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
}
