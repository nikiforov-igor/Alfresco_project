package ru.it.lecm.meetings.scripts;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.ParameterCheck;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.documents.beans.DocumentConnectionService;
import ru.it.lecm.documents.beans.DocumentEventService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.documents.beans.DocumentTableService;
import ru.it.lecm.errands.ErrandsService;
import ru.it.lecm.meetings.beans.ProtocolReportsService;
import ru.it.lecm.meetings.beans.ProtocolService;
import ru.it.lecm.security.LecmPermissionService;
import ru.it.lecm.statemachine.StatemachineModel;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
	private ErrandsService errandsService;


	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setProtocolService(ProtocolService protocolService) {
		this.protocolService = protocolService;
	}

	public void setErrandsService(ErrandsService errandsService) {
		this.errandsService = errandsService;
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

	@Deprecated
	public void changePointStatusByErrand(ScriptNode protocolSNode){
		NodeRef protocol = protocolSNode.getNodeRef();
		Set<NodeRef> senders = documentEventService.getEventSenders(protocol);
		for (NodeRef sender : senders){
			if (ErrandsService.TYPE_ERRANDS.equals(nodeService.getType(sender))){
				String errandStatus = (String) nodeService.getProperty(sender, StatemachineModel.PROP_STATUS);
				NodeRef point = protocolService.getErrandLinkedPoint(sender);
				if (null!=point){
					Boolean isExpired = (Boolean) nodeService.getProperty(sender,ErrandsService.PROP_ERRANDS_IS_EXPIRED);
					Boolean justInTime = (Boolean) nodeService.getProperty(sender,ErrandsService.PROP_ERRANDS_JUST_IN_TIME);
					if (!checkPointExecutedStatus(point) && errandsService.getErrandStatusName(ErrandsService.ERRANDS_STATUSES.ERRAND_EXECUTED_STATUS).equals(errandStatus)){
						// Переведем пункт в статус "Исполнен":
						changePointStatus(protocol, point, ProtocolService.P_STATUSES_CODES.EXECUTED_STATUS.toString());
					}
					else if (!checkPointExecutedStatus(point) && isExpired && justInTime){
						// Переведем пункт в статус "Не исполнен":
						changePointStatus(protocol, point, ProtocolService.P_STATUSES_CODES.NOT_EXECUTED_STATUS.toString());
					}
					else if (!checkPointExecutedStatus(point) && isExpired && !justInTime) {
						// Переведем пункт в статус "Просрочен":
						changePointStatus(protocol, point, ProtocolService.P_STATUSES_CODES.EXPIRED_STATUS.toString());
					}
				}
			}
			// Удалим отправителей из списка, чтобы в следующий раз были только новые:
			documentEventService.removeEventSender(protocol, sender);
		}
	}

	public ScriptNode getErrandLinkedPoint(ScriptNode errand) {
		if (errand != null) {
			NodeRef errandRef = errand.getNodeRef();
			if (errandRef != null) {
				NodeRef point = protocolService.getErrandLinkedPoint(errandRef);
				if (point != null) {
					return new ScriptNode(point, serviceRegistry, getScope());
				}
			}
		}
		return null;
	}

	/**
	 * Перевести пункт протокола в необходимый статус, записать дату фактичесоого завершения, сделать запись в БЖ.
	 *
	 * @param protocol - NodeRef протокола
	 * @param point - NodeRef пункта протокола
	 * @param statusKey - Статус, в который нужно перевести пункт протокола
     */
	private void changePointStatus(NodeRef protocol, NodeRef point, String statusKey) {
		// переведем пункт в необходимый статус
		protocolService.changePointStatus(point, statusKey);
		Integer pointNumber = (Integer) nodeService.getProperty(point, DocumentTableService.PROP_INDEX_TABLE_ROW);


		String newStatus = protocolService.getPointStatusByCodeFromDictionary(statusKey);
		if (newStatus != null) {
			boolean isExpired = ProtocolService.P_STATUSES_CODES.EXPIRED_STATUS.toString().equals(statusKey);
			String bjMessage = "";
			if (isExpired) {
				bjMessage = String.format("Исполнение пункта № %s документа #mainobject просрочено", pointNumber);
			} else {
				bjMessage = String.format("Пункт номер %s документа #mainobject перешел в статус %s", pointNumber, newStatus);
				// установим атрибут дату исполнения
				nodeService.setProperty(point, ProtocolService.PROP_PROTOCOL_POINT_DATE_REAL, new Date());
			}
			List<String> secondaryObj = Arrays.asList(point.toString());
			businessJournalService.log("System", protocol, "PROTOCOL_POINT_STATUS_CHANGE", bjMessage, secondaryObj);
		}
	}

	public void changePointStatus(String sPointRef, String statusKey){
		if (null!=sPointRef && !sPointRef.isEmpty()){
			NodeRef point = new NodeRef(sPointRef);
			if (nodeService.exists(point)){
				protocolService.changePointStatus(point, statusKey);
			}
		}
	}

	private Boolean checkPointStatus(String sPointRef, ProtocolService.P_STATUSES_CODES statusKey) {
		return null != sPointRef && !sPointRef.isEmpty() && checkPointStatus(new NodeRef(sPointRef), statusKey.toString());
	}

	private Boolean checkPointStatus(NodeRef pointRef, String statusKey) {
		return null != pointRef && nodeService.exists(pointRef) && protocolService.checkPointStatus(pointRef, statusKey);
	}

	public Boolean checkPointExecutedStatus(String sPointRef){
		return checkPointStatus(sPointRef, ProtocolService.P_STATUSES_CODES.EXECUTED_STATUS);
	}

	public Boolean checkPointExecutedStatus(NodeRef point){
		return checkPointStatus(point, ProtocolService.P_STATUSES_CODES.EXECUTED_STATUS.toString());
	}

	public Boolean checkPointRemovedStatus(String sPointRef){
		return checkPointStatus(sPointRef, ProtocolService.P_STATUSES_CODES.REMOVED_STATUS);
	}

	public Boolean checkPointRemovedStatus(NodeRef point){
		return checkPointStatus(point, ProtocolService.P_STATUSES_CODES.REMOVED_STATUS.toString());
	}

	public Boolean checkPointExpiredStatus(ScriptNode point) {
		return point != null && checkPointStatus(point.getNodeRef(), ProtocolService.P_STATUSES_CODES.EXPIRED_STATUS.toString());
	}

	@Deprecated
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

	/**
	 * Установить все пункты протокола в статус "Удален"
	 * @param protocolSNode
     */
	public void setPointsStatusRemoved(ScriptNode protocolSNode) {
		ParameterCheck.mandatory("protocolSNode", protocolSNode);
		NodeRef protocol = protocolSNode.getNodeRef();
		protocolService.setPointsStatusRemoved(protocol);
	}
}
