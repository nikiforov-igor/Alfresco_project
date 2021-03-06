package ru.it.lecm.meetings.beans;

import com.google.common.collect.Sets;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.lang.StringUtils;
import org.springframework.extensions.surf.util.I18NUtil;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.documents.beans.DocumentEventService;
import ru.it.lecm.documents.beans.DocumentMembersService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.documents.beans.DocumentTableService;
import ru.it.lecm.eds.api.EDSDocumentService;
import ru.it.lecm.errands.ErrandsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.LecmPermissionService;

import java.text.SimpleDateFormat;
import java.util.*;

import static ru.it.lecm.eds.api.EDSDocumentService.getFromMessagesOrDefaultValue;

/**
 * @author snovikov
 */
public class ProtocolServiceImpl extends BaseBean implements ProtocolService {

    private DictionaryBean lecmDictionaryService;

    private NamespaceService namespaceService;

    private DocumentService documentService;

    private DocumentEventService documentEventService;

    private LecmPermissionService lecmPermissionService;

    private OrgstructureBean orgstructureService;

    private PersonService personService;

    private DocumentMembersService documentMembersService;

    private ErrandsService errandsService;

    private EnumMap<ATTACHMENT_CATEGORIES,String> attachmentCategoriesMap;

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

    public void setErrandsService(ErrandsService errandsService) {
        this.errandsService = errandsService;
    }

    public void setDocumentMembersService(DocumentMembersService documentMembersService) {
        this.documentMembersService = documentMembersService;
    }

    public DocumentEventService getDocumentEventService() {
        return documentEventService;
    }

    public void setDocumentEventService(DocumentEventService documentEventService) {
        this.documentEventService = documentEventService;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
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
    public void changePointStatus(NodeRef point, String statusKey) {
       if (point != null && statusKey != null) {
           NodeRef newPointStatus = lecmDictionaryService.getDictionaryValueByParam(getFromMessagesOrDefaultValue("ru.it.lecm.dictionaries.protocolPoints.name", ProtocolService.PROTOCOL_POINT_DICTIONARY_NAME), ProtocolService.PROP_PROTOCOL_DIC_POINT_STATUS_CODE, statusKey);
           if (newPointStatus != null) {
               List<NodeRef> targetStatus = Arrays.asList(newPointStatus);
               nodeService.setAssociations(point, ProtocolService.ASSOC_PROTOCOL_POINT_STATUS, targetStatus);
           }
       }
    }

    @Override
    public void changePointStatus(NodeRef point, ProtocolService.P_STATUSES statusKey) {
        changePointStatus(point, statusKey.toString());
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
    public Boolean checkPointStatus(NodeRef point, String statusKey) {
        if (point != null) {
            String pointStatus = getPointStatus(point);
            String statusByCode = null;
            if (statusKey != null) {
                NodeRef statusRef = lecmDictionaryService.getDictionaryValueByParam(getFromMessagesOrDefaultValue("ru.it.lecm.dictionaries.protocolPoints.name", ProtocolService.PROTOCOL_POINT_DICTIONARY_NAME), ProtocolService.PROP_PROTOCOL_DIC_POINT_STATUS_CODE, statusKey);
                if (statusRef != null) {
                    statusByCode = (String) nodeService.getProperty(statusRef, ContentModel.PROP_NAME);
                }
            }
            return null != pointStatus && pointStatus.equals(statusByCode);
        }
        return null;
    }

    @Override
    public Boolean checkPointStatus(NodeRef point, ProtocolService.P_STATUSES statusKey) {
        return checkPointStatus(point, statusKey.toString());
    }

    @Override
    public void formErrands(final NodeRef protocol) {
        List<AssociationRef> tableAssocs = nodeService.getTargetAssocs(protocol, ProtocolService.ASSOC_PROTOCOL_POINTS);
        if (tableAssocs.size() > 0) {
            NodeRef table = tableAssocs.get(0).getTargetRef();
            Set<QName> pointType = new HashSet<QName>(Arrays.asList(ProtocolService.TYPE_PROTOCOL_TS_POINT));
            List<ChildAssociationRef> pointAssocs = nodeService.getChildAssocs(table, pointType);

            // ?????????????????? ???????????????????? ?????????????????? ???? ???????? ?????????????????? "???????????????????????? ??????????????????"
            NodeRef errandInitiator = null;
            List<AssociationRef> protocolInitatorAssocs = nodeService.getTargetAssocs(protocol, ProtocolService.ASSOC_PROTOCOL_MEETING_CHAIRMAN);
            if (protocolInitatorAssocs.size() > 0) {
                errandInitiator = protocolInitatorAssocs.get(0).getTargetRef();
            }
            int childeIndex = nodeService.getSourceAssocs(protocol, ErrandsService.ASSOC_ADDITIONAL_ERRANDS_DOCUMENT).size();
            for (ChildAssociationRef pointAssoc : pointAssocs) {
                NodeRef point = pointAssoc.getChildRef();
                childeIndex++;
                //???????????????? ??????????????????
                Map<String, String> properties = new HashMap<String, String>();
                //??????????????????
                Integer pointNumber = (Integer) nodeService.getProperty(point, DocumentTableService.PROP_INDEX_TABLE_ROW);
                List<AssociationRef> protocolDocTypeAssocs = nodeService.getTargetAssocs(protocol, EDSDocumentService.ASSOC_DOCUMENT_TYPE);
                String protocolDocType = "";
                if (protocolDocTypeAssocs.size() > 0) {
                    NodeRef protocolDocTypeRef = protocolDocTypeAssocs.get(0).getTargetRef();
                    protocolDocType = (String) nodeService.getProperty(protocolDocTypeRef, ContentModel.PROP_TITLE);
                }
                String protocolNumber = (String) nodeService.getProperty(protocol, DocumentService.PROP_REG_DATA_DOC_NUMBER);
                Date protocolRegDate = (Date) nodeService.getProperty(protocol, DocumentService.PROP_REG_DATA_DOC_DATE);
                String protocolRegDateStr = new SimpleDateFormat("dd.MM.yyyy").format(protocolRegDate);

                String errandTitle = "";
                String presentString = I18NUtil.getMessage("lecm.protocol.point.present-string", I18NUtil.getLocale());
                if (presentString != null) {
                    errandTitle = presentString.replace("{pointNumber}", pointNumber.toString())
                            .replace("{protocolDocType}", protocolDocType)
                            .replace("{protocolNumber}", protocolNumber)
                            .replace("{protocolRegDate}", protocolRegDateStr);
                } else {
                    errandTitle = "?????????? ??? " + pointNumber.toString() + " ?????????????????? " + protocolDocType +
                            " ???" + protocolNumber + " ???? " + protocolRegDateStr;
                }
                properties.put("lecm-errands:title", errandTitle);
                //????????????????????
                String content = (String) nodeService.getProperty(point, ProtocolService.PROP_PROTOCOL_POINT_FORMULATION);
                properties.put("lecm-errands:content", content);
                //????????????????
                properties.put("lecm-errands:is-important", "true");
                //???????? ????????????????????
                properties.put("lecm-errands:limitation-date-radio", "DATE");
                //
                properties.put("lecm-errands:child-index-counter", String.valueOf(childeIndex));

                //???????????????????? ??????????????????
                Map<String, String> associations = new HashMap<String, String>();

                // ?????????????????? ???????????????????? ??????????????????
                if (null != errandInitiator) {
                    associations.put("lecm-errands:initiator-assoc", errandInitiator.toString());
                    if (errandsService.isTransferRightToBaseDocument()) {
                        documentMembersService.addMemberWithoutCheckPermission(protocol, errandInitiator, true);
                    }
                }
                // ?????? ??????????????????
                String errandTypeOnProtocolPointName = getFromMessagesOrDefaultValue("lecm.protocol.point.errand.type.name", ErrandsService.ERRAND_TYPE_ON_POINT_PROTOCOL);
                NodeRef type = lecmDictionaryService.getRecordByParamValue(getFromMessagesOrDefaultValue("ru.it.lecm.dictionaries.errandTypes.name", ErrandsService.ERRANDS_TYPE_DICTIONARY_NAME), ContentModel.PROP_NAME, errandTypeOnProtocolPointName);
                associations.put(ErrandsService.ASSOC_ERRANDS_TYPE.toPrefixString(namespaceService), type.toString());
                //??????????????????????
                List<AssociationRef> pointExecutorAssocs = nodeService.getTargetAssocs(point, ProtocolService.ASSOC_PROTOCOL_POINT_EXECUTOR);
                if (pointExecutorAssocs.size() > 0) {
                    NodeRef executor = pointExecutorAssocs.get(0).getTargetRef();
                    associations.put("lecm-errands:executor-assoc", executor.toString());
                    if (errandsService.isTransferRightToBaseDocument()) {
                        documentMembersService.addMemberWithoutCheckPermission(protocol, executor, true);
                    }
                }
                //???????????????? ??????????????????
                List<AssociationRef> subjectAssocs = nodeService.getTargetAssocs(protocol, DocumentService.ASSOC_SUBJECT);
                if (!subjectAssocs.isEmpty()) {
                    List<String> subjects = new ArrayList<>();
                    for (AssociationRef subjectAssoc : subjectAssocs) {
                        subjects.add(subjectAssoc.getTargetRef().toString());
                    }
                    associations.put("lecm-document:subject-assoc", StringUtils.join(subjects, ","));
                }

                NodeRef errand = documentService.createDocument(ErrandsService.TYPE_ERRANDS.toPrefixString(namespaceService), properties, associations);
                nodeService.addAspect(errand, ErrandsService.ASPECT_SKIP_TRANSFER_RIGHT_TO_PARENT_ASPECT, null);

                // ?????????????? ?????????? ????????????????????
                if (null != errandInitiator) {
                    lecmPermissionService.grantDynamicRole("BR_INITIATOR", errand, errandInitiator.getId(), "LECM_BASIC_PG_Initiator");
                }

                // ???????? ??????????????????
                Date limitationDate = (Date) nodeService.getProperty(point, ProtocolService.PROP_PROTOCOL_POINT_EXEC_DATE);
                nodeService.setProperty(errand, ErrandsService.PROP_ERRANDS_LIMITATION_DATE, limitationDate);
				nodeService.setProperty(errand, ErrandsService.PROP_ERRANDS_IS_EXPIRED, checkPointStatus(point, P_STATUSES.EXPIRED_STATUS.toString()));

                //???????????????? ???????????????????? ?????????? ?????????? ???????????????????? ?? ?????????????????? ????????????????????, ?????????????????? ?????????? ?????????????????? ??????????????????????????
                nodeService.createAssociation(errand, protocol, ErrandsService.ASSOC_ADDITIONAL_ERRANDS_DOCUMENT);
                // ???????????????? ???????????????????? ???????????? ?? ????????????????????
                nodeService.createAssociation(point, errand, ProtocolService.ASSOC_PROTOCOL_POINT_ERRAND);
                //???????????????? ???????????????? ?? ???????????????? ?????????????????????? ???? ????????????????????
                documentEventService.subscribe(errand, protocol);
            }
        }
    }

    /**
     * ???????????????????? ?????? ???????????? ?????????????????? ?? ???????????? "????????????"
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

            String statusKey = P_STATUSES.REMOVED_STATUS.toString();
            NodeRef newPointStatus = lecmDictionaryService.getDictionaryValueByParam(getFromMessagesOrDefaultValue("ru.it.lecm.dictionaries.protocolPoints.name", ProtocolService.PROTOCOL_POINT_DICTIONARY_NAME), PROP_PROTOCOL_DIC_POINT_STATUS_CODE, statusKey);
            if (newPointStatus != null) {
                List<NodeRef> targetStatus = Arrays.asList(newPointStatus);

                for (ChildAssociationRef pointAssoc : pointAssocs) {
                    NodeRef point = pointAssoc.getChildRef();
                    nodeService.setAssociations(point, ProtocolService.ASSOC_PROTOCOL_POINT_STATUS, targetStatus);
                }
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

    public String getPointStatusByCodeFromDictionary(String statusKey){
        NodeRef statusRef = lecmDictionaryService.getDictionaryValueByParam(getFromMessagesOrDefaultValue("ru.it.lecm.dictionaries.protocolPoints.name", ProtocolService.PROTOCOL_POINT_DICTIONARY_NAME), PROP_PROTOCOL_DIC_POINT_STATUS_CODE, statusKey);
        if (statusRef != null) {
            return (String) nodeService.getProperty(statusRef, ContentModel.PROP_NAME);
        }
        return null;
    }

    public String getPointStatusCodeByStatusTextFromDictionary(String statusText){
        NodeRef statusRef = lecmDictionaryService.getDictionaryValueByParam(getFromMessagesOrDefaultValue("ru.it.lecm.dictionaries.protocolPoints.name", ProtocolService.PROTOCOL_POINT_DICTIONARY_NAME), ContentModel.PROP_NAME, statusText);
        if (statusRef != null) {
            return (String) nodeService.getProperty(statusRef, PROP_PROTOCOL_DIC_POINT_STATUS_CODE);
        }
        return null;
    }

    @Override
    protected void initServiceImpl() {
        attachmentCategoriesMap = new EnumMap<ATTACHMENT_CATEGORIES,String>(ATTACHMENT_CATEGORIES.class){{
            put(ATTACHMENT_CATEGORIES.DOCUMENT, getFromMessagesOrDefaultValue("lecm.protocol.document.attachment.category.DOCUMENT.title", "????????????????"));
            put(ATTACHMENT_CATEGORIES.APPLICATIONS, getFromMessagesOrDefaultValue("lecm.protocol.document.attachment.category.APPENDICES.title", "????????????????????"));
            put(ATTACHMENT_CATEGORIES.ORIGINAL, getFromMessagesOrDefaultValue("lecm.protocol.document.attachment.category.ORIGINAL.title", "??????????????????"));
            put(ATTACHMENT_CATEGORIES.OTHERS, getFromMessagesOrDefaultValue("lecm.protocol.document.attachment.category.OTHER.title", "????????????"));
        }};
    }

    @Override
    public String getAttachmentCategoryName(ATTACHMENT_CATEGORIES code) {
        return attachmentCategoriesMap != null ? attachmentCategoriesMap.get(code) : null;
    }
}
