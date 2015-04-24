/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import ru.it.lecm.documents.beans.DocumentConnectionService;
import ru.it.lecm.documents.beans.DocumentEventService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.documents.beans.DocumentTableService;
import ru.it.lecm.eds.api.EDSDocumentService;
import ru.it.lecm.errands.ErrandsService;
import ru.it.lecm.meetings.beans.ProtocolService;
import ru.it.lecm.security.LecmPermissionService;

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
}
