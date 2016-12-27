package ru.it.lecm.eds.scripts;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.ParameterCheck;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.eds.api.EDSDocumentService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.util.ArrayList;
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


    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }
}
