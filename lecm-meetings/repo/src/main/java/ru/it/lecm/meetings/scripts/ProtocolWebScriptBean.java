package ru.it.lecm.meetings.scripts;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ParameterCheck;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.documents.beans.DocumentConnectionService;
import ru.it.lecm.documents.beans.DocumentEventService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.documents.beans.DocumentTableService;
import ru.it.lecm.eds.api.EDSDocumentService;
import ru.it.lecm.errands.ErrandsService;
import ru.it.lecm.meetings.beans.ProtocolReportsService;
import ru.it.lecm.meetings.beans.ProtocolService;
import ru.it.lecm.security.LecmPermissionService;
import ru.it.lecm.statemachine.StatemachineModel;

/**
 *
 * @author snovikov
 */
public class ProtocolWebScriptBean extends BaseWebScript {
	private NodeService nodeService;
    private ProtocolService protocolService;
	private DocumentService documentService;
	private LecmPermissionService lecmPermissionService;
	private DocumentConnectionService documentConnectionService;
	private DocumentEventService documentEventService;
	private BusinessJournalService businessJournalService;
	private ProtocolReportsService protocolReportsService;
	
	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}
	
	public void setProtocolService(ProtocolService protocolService) {
		this.protocolService = protocolService;
	}
	
	public void setDocumentService(final DocumentService documentService) {
		this.documentService = documentService;
	}
	
	public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
		this.lecmPermissionService = lecmPermissionService;
	}
	
	public void setDocumentConnectionService(DocumentConnectionService documentConnectionService) {
		this.documentConnectionService = documentConnectionService;
	}
	
	public void setDocumentEventService(DocumentEventService documentEventService) {
		this.documentEventService = documentEventService;
	}
	
	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}
	
	public void setProtocolReportsService(ProtocolReportsService protocolReportsService) {
		this.protocolReportsService = protocolReportsService;
	}

	/**
	 * Сформировать поручения по пунктам Протокола
	 *
	 * @param protocolSNode ссылка на Протокол
	 */
	public void formErrands(ScriptNode protocolSNode) {
		ParameterCheck.mandatory("protocolSNode", protocolSNode);
		//Поручения должны создаваться от имени председателя(ALF-4620)
		NodeRef protocol = protocolSNode.getNodeRef();
		protocolService.formErrands(protocol);
	}
	
	public void changePointStatusByErrand(ScriptNode protocolSNode){
		NodeRef protocol = protocolSNode.getNodeRef();
        Set<NodeRef> senders = documentEventService.getEventSenders(protocol);
		for (NodeRef sender : senders){
			if (ErrandsService.TYPE_ERRANDS.equals(nodeService.getType(sender))){
				String errandStatus = (String) nodeService.getProperty(sender, StatemachineModel.PROP_STATUS);
				NodeRef point = protocolService.getErrandLinkedPoint(sender);
				if (null!=point){
					if (!checkPointExecutedStatus(point) && "Исполнено".equals(errandStatus)){
						// переведем пункт в статус "Исполнен"
						protocolService.changePointStatus(point,ProtocolService.P_STATUSES.EXECUTED_STATUS);
						//установим атрибут дату исполнеия
						nodeService.setProperty(point, ProtocolService.PROP_PROTOCOL_POINT_DATE_REAL, new Date());
						//запись в бизнес журнал о том, что пункт перешел в статус исполнен
						Integer pointNumber = (Integer) nodeService.getProperty(point, DocumentTableService.PROP_INDEX_TABLE_ROW);
						String bjMessage = String.format("Пункт номер %s документа #mainobject перешел в статус Исполнен", pointNumber);
						List<String> secondaryObj = Arrays.asList(point.toString());
						businessJournalService.log("System", protocol, "PROTOCOL_POINT_STATUS_CHANGE", bjMessage, secondaryObj);
					}
					if (!checkPointExecutedStatus(point) && "Не исполнено".equals(errandStatus)){
						// переведем пункт в статус "Не исполнен"
						protocolService.changePointStatus(point,ProtocolService.P_STATUSES.NOT_EXECUTED_STATUS);
						//установим атрибут дата исполнеия
						nodeService.setProperty(point, ProtocolService.PROP_PROTOCOL_POINT_DATE_REAL, new Date());
						//запись в бизнес журнал о том, что пункт перешел в статус не исполнен
						Integer pointNumber = (Integer) nodeService.getProperty(point, DocumentTableService.PROP_INDEX_TABLE_ROW);
						String bjMessage = String.format("Пункт номер %s документа #mainobject перешел в статус Не исполнен", pointNumber);
						List<String> secondaryObj = Arrays.asList(point.toString());
						businessJournalService.log("System", protocol, "PROTOCOL_POINT_STATUS_CHANGE", bjMessage, secondaryObj);
					}

					Boolean is_expired = (Boolean) nodeService.getProperty(sender,ErrandsService.PROP_ERRANDS_IS_EXPIRED);
					if (!checkPointExecutedStatus(point) && !"Исполнено".equals(errandStatus) && is_expired){
						// переведем пункт в статус "Просрочен"
						protocolService.changePointStatus(point, ProtocolService.P_STATUSES.EXPIRED_STATUS);
						//запись в бизнес журнал о том, что пункт перешел в статус просрочен
						Integer pointNumber = (Integer) nodeService.getProperty(point, DocumentTableService.PROP_INDEX_TABLE_ROW);
						String bjMessage = String.format("Исполнение пункта № %s документа #mainobject просрочено", pointNumber);
						List<String> secondaryObj = Arrays.asList(point.toString());
						businessJournalService.log("System", protocol, "PROTOCOL_POINT_STATUS_CHANGE", bjMessage, secondaryObj);
					}
				}
			}
			// удалим отправителей из списка, чтобы в следующий раз были только новые
			documentEventService.removeEventSender(protocol, sender);
		}
	}
	
	public void changePointStatus(String sPointRef, String status){
		if (null!=sPointRef && !sPointRef.isEmpty()){
			NodeRef point = new NodeRef(sPointRef);
			if (nodeService.exists(point)){
				protocolService.changePointStatus(point,ProtocolService.P_STATUSES.valueOf(status));
			}
		}
	}

	public Boolean checkPointExecutedStatus(String sPointRef){
		if (null != sPointRef && !sPointRef.isEmpty()){
			NodeRef point = new NodeRef(sPointRef);
			if (nodeService.exists(point)){
				return protocolService.checkPointStatus(point, ProtocolService.P_STATUSES.EXECUTED_STATUS);
			}
		}
		return false;
	}
	
	public Boolean checkPointExecutedStatus(NodeRef point){
		if (null != point){
			if (nodeService.exists(point)){
				return protocolService.checkPointStatus(point, ProtocolService.P_STATUSES.EXECUTED_STATUS);
			}
		}
		return false;
	}
	
	public ScriptNode generateDocumentReport(final String reportCode, final String templateCode, final String documentRef) {
		NodeRef reportNodeRef = protocolReportsService.generateDocumentReport(reportCode, templateCode, documentRef);

		return new ScriptNode(reportNodeRef, serviceRegistry, getScope());
	}

	public ScriptNode getDraftRoot() {
		NodeRef draftRoot = documentService.getDraftRootByType(ProtocolService.TYPE_PROTOCOL);

		if (draftRoot == null) {
			try {
				draftRoot = documentService.createDraftRoot(ProtocolService.TYPE_PROTOCOL);
			} catch (WriteTransactionNeededException ex) {
				throw new RuntimeException(ex);
			}
		}

		if (draftRoot != null) {
			return new ScriptNode(draftRoot, serviceRegistry, getScope());
		} else {
			return null;
		}
	}
}
