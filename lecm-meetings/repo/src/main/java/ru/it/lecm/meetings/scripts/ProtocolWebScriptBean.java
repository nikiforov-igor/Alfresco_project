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
import ru.it.lecm.base.beans.BaseWebScript;
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
		NodeRef protocol = protocolSNode.getNodeRef();
		//найдем таблицу с пунктами
		List<AssociationRef> tableAssocs = nodeService.getTargetAssocs(protocol, ProtocolService.ASSOC_PROTOCOL_POINTS);
		if (tableAssocs.size() > 0) {
			NodeRef table = tableAssocs.get(0).getTargetRef();
			Set<QName> pointType = new HashSet<QName>(Arrays.asList(ProtocolService.TYPE_PROTOCOL_TS_POINT));
			List<ChildAssociationRef> pointAssocs = nodeService.getChildAssocs(table, pointType);
			for (ChildAssociationRef pointAssoc : pointAssocs) {
				NodeRef point = pointAssoc.getChildRef();

				//свойства поручения
				Map<String, String> properties = new HashMap<String, String>();
				//заголовок
				Integer pointNumber = (Integer) nodeService.getProperty(point, DocumentTableService.PROP_INDEX_TABLE_ROW);
				List<AssociationRef> protocolDocTypeAssocs = nodeService.getTargetAssocs(protocol, EDSDocumentService.ASSOC_DOCUMENT_TYPE);
				String protocolDocType = "";
				if (protocolDocTypeAssocs.size() > 0) {
					NodeRef protocolDocTypeRef = protocolDocTypeAssocs.get(0).getTargetRef();
					protocolDocType = (String) nodeService.getProperty(protocolDocTypeRef, ContentModel.PROP_NAME);
				}
				String protocolNumber = (String) nodeService.getProperty(protocol, DocumentService.PROP_REG_DATA_DOC_NUMBER);
				Date protocolRegDate = (Date) nodeService.getProperty(protocol, DocumentService.PROP_REG_DATA_DOC_DATE);
				String protocolRegDateStr = new SimpleDateFormat("dd.MM.yyyy").format(protocolRegDate);

				StringBuilder errandTtitle = new StringBuilder();
				errandTtitle.append("Пункт № ").append(pointNumber.toString()).append(" документа ").append(protocolDocType);
				errandTtitle.append(" №").append(protocolNumber).append(" от ").append(protocolRegDateStr);
				properties.put("lecm-errands:title", errandTtitle.toString());
				//содержание
				String content = (String) nodeService.getProperty(point, ProtocolService.PROP_PROTOCOL_POINT_FORMULATION);
				properties.put("lecm-errands:content", content);
				//важность
				properties.put("lecm-errands:is-important", "true");

				//ассоциации поручения
				Map<String, String> associations = new HashMap<String, String>();
				//инициатор поручения
				NodeRef errandInitiator = null;
				List<AssociationRef> chairmanAssocs = nodeService.getTargetAssocs(protocol, ProtocolService.ASSOC_PROTOCOL_MEETING_CHAIRMAN);
				if (chairmanAssocs.size() > 0) {
					NodeRef chairman = chairmanAssocs.get(0).getTargetRef();
					errandInitiator = chairman;
					associations.put("lecm-errands:initiator-assoc", chairman.toString());
				}
				//исполнитель
				List<AssociationRef> pointExecutorAssocs = nodeService.getTargetAssocs(point, ProtocolService.ASSOC_PROTOCOL_POINT_EXECUTOR);
				if (pointExecutorAssocs.size() > 0) {
					NodeRef executor = pointExecutorAssocs.get(0).getTargetRef();
					associations.put("lecm-errands:executor-assoc", executor.toString());
				}
				//тематика поручения
				List<AssociationRef> subjectAssocs = nodeService.getTargetAssocs(protocol, DocumentService.ASSOC_SUBJECT);
				if (subjectAssocs.size() > 0) {
					NodeRef subject = subjectAssocs.get(0).getTargetRef();
					associations.put("lecm-document:subject-assoc", subject.toString());
				}

				NodeRef errand = documentService.createDocument("lecm-errands:document", properties, associations);

				// выдадим права контролеру
				if (null != errandInitiator){
					lecmPermissionService.grantDynamicRole("BR_INITIATOR", errand, errandInitiator.getId(), "LECM_BASIC_PG_Initiator");
				}

				// срок поручения
				Date limitationDate = (Date) nodeService.getProperty(point, ProtocolService.PROP_PROTOCOL_POINT_EXEC_DATE);
				nodeService.setProperty(errand, ErrandsService.PROP_ERRANDS_LIMITATION_DATE, limitationDate);

				// установим системную связь между Протоколом и созданным поручением
				documentConnectionService.createConnection(protocol, errand, DocumentConnectionService.DOCUMENT_CONNECTION_ON_BASIS_DICTIONARY_VALUE_CODE, true, true);
				// создадим ассоциацию пункта с поручением
				nodeService.createAssociation(point, errand, ProtocolService.ASSOC_PROTOCOL_POINT_ERRAND);
				// переведем пункт в статус "на исполнениии"
				protocolService.changePointStatus(point,ProtocolService.P_STATUSES.PERFORMANCE_STATUS);
				//подпишем Протокол в качестве наблюдателя за поручением
				documentEventService.subscribe(errand, protocol);
			}
		}
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
}
