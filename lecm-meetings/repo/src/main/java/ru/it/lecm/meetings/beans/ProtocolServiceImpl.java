package ru.it.lecm.meetings.beans;

import com.google.common.collect.Sets;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.documents.beans.DocumentEventService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.documents.beans.DocumentTableService;
import ru.it.lecm.eds.api.EDSDocumentService;
import ru.it.lecm.errands.ErrandsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.LecmPermissionService;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author snovikov
 */
public class ProtocolServiceImpl extends BaseBean implements ProtocolService {

    private DictionaryBean lecmDictionaryService;

    private DocumentService documentService;

    private DocumentEventService documentEventService;

    private LecmPermissionService lecmPermissionService;

    private OrgstructureBean orgstructureService;

    private PersonService personService;

    public PersonService getPersonService() {
        return personService;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    public OrgstructureBean getOrgstructureService() {
        return orgstructureService;
    }

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public DocumentEventService getDocumentEventService() {
        return documentEventService;
    }

    public void setDocumentEventService(DocumentEventService documentEventService) {
        this.documentEventService = documentEventService;
    }

    public LecmPermissionService getLecmPermissionService() {
        return lecmPermissionService;
    }

    public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
        this.lecmPermissionService = lecmPermissionService;
    }

    public DocumentService getDocumentService() {
        return documentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setLecmDictionaryService(DictionaryBean lecmDictionaryService) {
        this.lecmDictionaryService = lecmDictionaryService;
    }

    @Override
    public NodeRef getServiceRootFolder() {
        return null;
    }

    @Override
    public void changePointStatus(NodeRef point, ProtocolService.P_STATUSES statusKey) {
        String status = ProtocolService.POINT_STATUSES.get(statusKey);
        if (null != status) {
            NodeRef newPointStatus = lecmDictionaryService.getDictionaryValueByParam(ProtocolService.PROTOCOL_POINT_DICTIONARY_NAME, ContentModel.PROP_NAME, status);
            List<NodeRef> targetStatus = Arrays.asList(newPointStatus);
            nodeService.setAssociations(point, ProtocolService.ASSOC_PROTOCOL_POINT_STATUS, targetStatus);
        }
    }

    @Override
    public NodeRef getErrandLinkedPoint(NodeRef errand) {
        List<AssociationRef> pointAssocs = nodeService.getSourceAssocs(errand, ProtocolService.ASSOC_PROTOCOL_POINT_ERRAND);
        if (pointAssocs.size() > 0) {
            return pointAssocs.get(0).getSourceRef();
        }
        return null;
    }

    @Override
    public String getPointStatus(NodeRef point) {
        List<AssociationRef> statusAssocs = nodeService.getTargetAssocs(point, ProtocolService.ASSOC_PROTOCOL_POINT_STATUS);
        if (statusAssocs.size() > 0) {
            NodeRef status = statusAssocs.get(0).getTargetRef();
            String statusName = (String) nodeService.getProperty(status, ContentModel.PROP_NAME);
            return statusName;
        }
        return null;
    }

    @Override
    public Boolean checkPointStatus(NodeRef point, ProtocolService.P_STATUSES statusKey) {
        String status = getPointStatus(point);
        if (null != status) {
            if (ProtocolService.POINT_STATUSES.get(statusKey).equals(status)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    public void formErrands(final NodeRef protocol) {
        List<AssociationRef> tableAssocs = nodeService.getTargetAssocs(protocol, ProtocolService.ASSOC_PROTOCOL_POINTS);
        if (tableAssocs.size() > 0) {
            NodeRef table = tableAssocs.get(0).getTargetRef();
            Set<QName> pointType = new HashSet<QName>(Arrays.asList(ProtocolService.TYPE_PROTOCOL_TS_POINT));
            List<ChildAssociationRef> pointAssocs = nodeService.getChildAssocs(table, pointType);

            // Получение инициатора поручений из поля протокола "Председатель совещания"
            NodeRef errandInitiator = null;
            List<AssociationRef> protocolInitatorAssocs = nodeService.getTargetAssocs(protocol, ProtocolService.ASSOC_PROTOCOL_MEETING_CHAIRMAN);
            if (protocolInitatorAssocs.size() > 0) {
                errandInitiator = protocolInitatorAssocs.get(0).getTargetRef();
            }
            int childeIndex = nodeService.getSourceAssocs(protocol, ErrandsService.ASSOC_ADDITIONAL_ERRANDS_DOCUMENT).size();
            for (ChildAssociationRef pointAssoc : pointAssocs) {
                NodeRef point = pointAssoc.getChildRef();
                childeIndex++;
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
                //Срок исполнения
                properties.put("lecm-errands:limitation-date-radio", "DATE");
                //
                properties.put("lecm-errands:child-index-counter", String.valueOf(childeIndex));

                //ассоциации поручения
                Map<String, String> associations = new HashMap<String, String>();

                // Назначить инициатора поручения
                if (null != errandInitiator) {
                    associations.put("lecm-errands:initiator-assoc", errandInitiator.toString());
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

                // выдадим права инициатору
                if (null != errandInitiator) {
                    lecmPermissionService.grantDynamicRole("BR_INITIATOR", errand, errandInitiator.getId(), "LECM_BASIC_PG_Initiator");
                }

                // срок поручения
                Date limitationDate = (Date) nodeService.getProperty(point, ProtocolService.PROP_PROTOCOL_POINT_EXEC_DATE);
                nodeService.setProperty(errand, ErrandsService.PROP_ERRANDS_LIMITATION_DATE, limitationDate);
				nodeService.setProperty(errand, ErrandsService.PROP_ERRANDS_IS_EXPIRED, checkPointStatus(point, P_STATUSES.EXPIRED_STATUS));

                //создадим ассоциацию между между Протоколом и созданным поручением, системная связь создастся автоматически
                nodeService.createAssociation(errand, protocol, ErrandsService.ASSOC_ADDITIONAL_ERRANDS_DOCUMENT);
                // создадим ассоциацию пункта с поручением
                nodeService.createAssociation(point, errand, ProtocolService.ASSOC_PROTOCOL_POINT_ERRAND);
                //подпишем Протокол в качестве наблюдателя за поручением
                documentEventService.subscribe(errand, protocol);
            }
        }
    }

    /**
     * Установить все пункты протокола в статус "Удален"
     *
     * @param protocol
     */
    @Override
    public void setPointsStatusRemoved(final NodeRef protocol) {
        List<AssociationRef> tableAssocs = nodeService.getTargetAssocs(protocol, ProtocolService.ASSOC_PROTOCOL_POINTS);
        if (tableAssocs.size() > 0) {
            NodeRef table = tableAssocs.get(0).getTargetRef();
            Set<QName> pointType = new HashSet<QName>(Arrays.asList(ProtocolService.TYPE_PROTOCOL_TS_POINT));
            List<ChildAssociationRef> pointAssocs = nodeService.getChildAssocs(table, pointType);

            String status = ProtocolService.POINT_STATUSES.get(P_STATUSES.REMOVED_STATUS);
            NodeRef newPointStatus = lecmDictionaryService.getDictionaryValueByParam(ProtocolService.PROTOCOL_POINT_DICTIONARY_NAME, ContentModel.PROP_NAME, status);
            List<NodeRef> targetStatus = Arrays.asList(newPointStatus);

            for (ChildAssociationRef pointAssoc : pointAssocs) {
                NodeRef point = pointAssoc.getChildRef();
                nodeService.setAssociations(point, ProtocolService.ASSOC_PROTOCOL_POINT_STATUS, targetStatus);
            }
        }
    }

    @Override
    public boolean checkProtocolPointsFields(NodeRef protocol) {
        List<AssociationRef> tableAssocs = nodeService.getTargetAssocs(protocol, ProtocolService.ASSOC_PROTOCOL_POINTS);
        if (tableAssocs != null && !tableAssocs.isEmpty()) {
            NodeRef table = tableAssocs.get(0).getTargetRef();

            List<ChildAssociationRef> pointAssocs = nodeService.getChildAssocs(table, Sets.newHashSet(ProtocolService.TYPE_PROTOCOL_TS_POINT));

            for (ChildAssociationRef pointAssoc : pointAssocs) {
                NodeRef point = pointAssoc.getChildRef();

                List<AssociationRef> pointExecutorAssocs = nodeService.getTargetAssocs(point, ProtocolService.ASSOC_PROTOCOL_POINT_EXECUTOR);

                if (pointExecutorAssocs == null || pointExecutorAssocs.isEmpty() ||
                        nodeService.getProperty(point, PROP_PROTOCOL_POINT_EXEC_DATE) == null ||
                        nodeService.getProperty(point, PROP_PROTOCOL_POINT_DECISION) == null) {
                    return false;
                }
            }
        }

        return true;

    }
}
