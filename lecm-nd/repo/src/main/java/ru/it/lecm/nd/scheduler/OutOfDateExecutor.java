/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.it.lecm.nd.scheduler;

import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.nd.NDDocumentServiceImpl;
import ru.it.lecm.statemachine.StatemachineModel;

import java.util.List;

/**
 *
 * @author ikhalikov
 */
public class OutOfDateExecutor extends ActionExecuterAbstractBase {

	private final static Logger logger = LoggerFactory.getLogger(InWorkExecutor.class);
	private NodeService nodeService;
    private BusinessJournalService businessJournalService;
    private NDDocumentServiceImpl ndDocumentService;
    private SubstitudeBean substitudeBean;
    private DocumentService documentService;

    public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	@Override
	protected void executeImpl(Action action, NodeRef actionedUponNodeRef) {
		logger.info(String.format("ND [%s] is cancelling.", actionedUponNodeRef.toString()));
		nodeService.setProperty(actionedUponNodeRef, StatemachineModel.PROP_STATUS, "Срок действия окончен");

        //логирование
        String bjMessage = "Документ " + ndDocumentService.wrapperLink(actionedUponNodeRef, "№ {~REGNUM} от {~REGDATE}", documentService.getDocumentUrl(actionedUponNodeRef))+ " завершил срок действия";
        bjMessage = substitudeBean.formatNodeTitle(actionedUponNodeRef, bjMessage);
        businessJournalService.log("System", actionedUponNodeRef, "EXPIRATION_DATE", bjMessage, null);
	}

	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> paramList) {

	}

    public void setBusinessJournalService(BusinessJournalService businessJournalService) {
        this.businessJournalService = businessJournalService;
    }

    public void setNdDocumentService(NDDocumentServiceImpl ndDocumentService) {
        this.ndDocumentService = ndDocumentService;
    }

    public void setSubstitudeBean(SubstitudeBean substitudeBean) {
        this.substitudeBean = substitudeBean;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
}
