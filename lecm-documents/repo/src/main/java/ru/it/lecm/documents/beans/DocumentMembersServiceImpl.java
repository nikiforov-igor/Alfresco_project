package ru.it.lecm.documents.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.query.PagingRequest;
import org.alfresco.query.PagingResults;
import org.alfresco.repo.node.getchildren.FilterProp;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.AssociationExistsException;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.FileFilterMode;
import org.alfresco.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.LecmObjectsService;
import ru.it.lecm.base.beans.TransactionNeededException;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.LecmPermissionService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.springframework.context.ApplicationEvent;

/**
 * User: dbashmakov
 * Date: 11.03.13
 * Time: 14:45
 */
public class DocumentMembersServiceImpl extends BaseBean implements DocumentMembersService {

    public static final int MAX_ITEMS = 1000;
    private LecmObjectsService lecmObjectsService;
    private LecmPermissionService lecmPermissionService;
    private NamespaceService namespaceService;

    final public String DEFAULT_ACCESS = LecmPermissionService.LecmPermissionGroup.PGROLE_Reader;
    final protected Logger logger = LoggerFactory.getLogger(DocumentMembersServiceImpl.class);

    private DictionaryService dictionaryService;
    private OrgstructureBean orgstructureService;

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public void setLecmObjectsService(LecmObjectsService lecmObjectsService) {
        this.lecmObjectsService = lecmObjectsService;
    }

    public void setDictionaryService(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
        this.lecmPermissionService = lecmPermissionService;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    @Override
    public NodeRef addMember(final NodeRef document, final NodeRef employeeRef, final Map<QName, Serializable> properties) throws WriteTransactionNeededException {
        return addMember(document, employeeRef, properties, DocumentMembersService.PROP_SILENT_DEFAULT_VALUE);
    }

    @Override
    public NodeRef addMember(final NodeRef document, final NodeRef employeeRef, final Map<QName, Serializable> properties, boolean silent)  throws WriteTransactionNeededException {
        lecmPermissionService.checkPermission(LecmPermissionService.PERM_MEMBERS_ADD, document);
        return addMemberWithoutCheckPermission(document, employeeRef, properties, silent);
    }

    @Override
    public NodeRef addMember(NodeRef document, NodeRef employee, String permissionGroup)  throws WriteTransactionNeededException {
        return addMember(document, employee, permissionGroup, DocumentMembersService.PROP_SILENT_DEFAULT_VALUE);
    }

    @Override
    public NodeRef addMember(NodeRef document, NodeRef employee, String permissionGroup, boolean silent) throws WriteTransactionNeededException  {
        lecmPermissionService.checkPermission(LecmPermissionService.PERM_MEMBERS_ADD, document);
        Map<QName,Serializable> props = new HashMap<QName, Serializable>();
        props.put(DocumentMembersService.PROP_MEMBER_GROUP, permissionGroup);
        return addMemberWithoutCheckPermission(document, employee, props, silent);
    }

	@Override
	public NodeRef addMemberWithoutCheckPermission(NodeRef document, NodeRef employee, String permissionGroup) throws WriteTransactionNeededException {
        return addMemberWithoutCheckPermission(document, employee, permissionGroup, DocumentMembersService.PROP_SILENT_DEFAULT_VALUE);
    }

    @Override
	public NodeRef addMemberWithoutCheckPermission(NodeRef document, NodeRef employee, String permissionGroup, boolean silent) throws WriteTransactionNeededException {
        Map<QName,Serializable> props = new HashMap<QName, Serializable>();
        props.put(DocumentMembersService.PROP_MEMBER_GROUP, permissionGroup);
        return addMemberWithoutCheckPermission(document, employee, props, silent);
	}

    @Override
    public NodeRef addMemberWithoutCheckPermission(final NodeRef document, final NodeRef employeeRef, final Map<QName, Serializable> properties, boolean silent) throws WriteTransactionNeededException {
        properties.put(DocumentMembersService.PROP_SILENT, silent);
        return addMemberWithoutCheckPermission(document, employeeRef, properties);
    }

    @Override
    public NodeRef addMemberWithoutCheckPermission(final NodeRef document, final NodeRef employeeRef, final Map<QName, Serializable> properties)  throws WriteTransactionNeededException  {
        if (employeeRef != null && !isDocumentMember(employeeRef, document)) {
            //Проверка на выполнение в транзакции
            try {
                    lecmTransactionHelper.checkTransaction();
            } catch (TransactionNeededException ex) {
                    throw new RuntimeException("Transaction needed.");
            }
            final NodeRef documentMembersFolder = (null == getMembersFolderRef(document) ? createMembersFolderRef(document) : getMembersFolderRef(document));

//          TODO: Транзакция убрана, но непонятно, где этот метод выше оборачивать,
//          т.к используется в итоге вообще везде
            
//            ChildAssociationRef associationRef = nodeService.createNode(documentMembersFolder, ContentModel.ASSOC_CONTAINS,
//           QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, GUID.generate()), TYPE_DOC_MEMBER, properties);
//            NodeRef newMemberRef = associationRef.getChildRef();
            
            NodeRef newMemberRef = createNode(documentMembersFolder, TYPE_DOC_MEMBER, null, properties);
            
            nodeService.createAssociation(newMemberRef, employeeRef, DocumentMembersService.ASSOC_MEMBER_EMPLOYEE);

            LecmPermissionService.LecmPermissionGroup pgGranting = getMemberPermissionGroup(newMemberRef);
            lecmPermissionService.grantAccess(pgGranting, document, employeeRef);
            return newMemberRef;
        }
        return null;
    }

    @Override
    public boolean deleteMember(NodeRef document, NodeRef employeeRef) {
        final NodeRef memberRef = getDocumentMember(document,employeeRef);
        if (memberRef != null) {
            nodeService.deleteNode(memberRef);
            return true;
        }
        return false;
    }

    @Override
    public NodeRef getMembersFolderRef(final NodeRef document) {
        //А нужно ли получать от системы
//        return AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<NodeRef>() {
//            @Override
//            public NodeRef doWork() throws Exception {
//				TODO: Метод разделён, создание вынесено в createMembersFolderRef
				return nodeService.getChildByName(document, ContentModel.ASSOC_CONTAINS, DOCUMENT_MEMBERS_ROOT_NAME);
//            }
//        });
    }

	@Override
	public NodeRef createMembersFolderRef(final NodeRef document) throws WriteTransactionNeededException {
		try {
            lecmTransactionHelper.checkTransaction();
        } catch (TransactionNeededException ex) {
            throw new WriteTransactionNeededException("Can't create members folder for document " + document);
        }

//		QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, DOCUMENT_MEMBERS_ROOT_NAME);
//		Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
//		properties.put(ContentModel.PROP_NAME, DOCUMENT_MEMBERS_ROOT_NAME);
//		ChildAssociationRef childAssoc = nodeService.createNode(document, ContentModel.ASSOC_CONTAINS, assocQName, ContentModel.TYPE_FOLDER, properties);
//		NodeRef nodeRef = childAssoc.getChildRef();
                
                NodeRef nodeRef = createNode(document, ContentModel.TYPE_FOLDER, DOCUMENT_MEMBERS_ROOT_NAME, null);
                
		hideNode(nodeRef, true);
		return nodeRef;
	}

    @Override
    public List<NodeRef> getDocumentMembers(NodeRef document) {
        return getDocumentMembers(document, 0, MAX_ITEMS);
    }

    @Override
    public List<NodeRef> getDocumentMembers(NodeRef document, int skipCount, int maxItems) {
        List<NodeRef> results = new ArrayList<NodeRef>();
        if (lecmPermissionService.hasPermission(LecmPermissionService.PERM_MEMBERS_LIST, document)) {
            List<Pair<QName, Boolean>> sortProps = new ArrayList<Pair<QName, Boolean>>(1);
            sortProps.add(new Pair<QName, Boolean>(ContentModel.PROP_MODIFIED, false));

            PagingRequest pageRequest = new PagingRequest(skipCount, maxItems, null);
            pageRequest.setRequestTotalCountMax(MAX_ITEMS);

            PagingResults<NodeRef> pageOfNodeInfos = null;
            FileFilterMode.setClient(FileFilterMode.Client.script);
            try {
	            NodeRef membersFolder = getMembersFolderRef(document);
	            if (membersFolder != null) {
		            pageOfNodeInfos = lecmObjectsService.list(membersFolder, TYPE_DOC_MEMBER, new ArrayList<FilterProp>(), sortProps, pageRequest);
	            }
            } catch (Exception e) {
	            logger.error("Error get members", e);
            } finally {
                FileFilterMode.clearClient();
            }

	        if (pageOfNodeInfos != null) {
		        List<NodeRef> nodeInfos = pageOfNodeInfos.getPage();
		        for (NodeRef ref : nodeInfos) {
                    List<AssociationRef> employeeRef = nodeService.getTargetAssocs(ref, ASSOC_MEMBER_EMPLOYEE);
                    if (!employeeRef.isEmpty() && orgstructureService.hasAccessToOrgElement(employeeRef.get(0).getTargetRef())) {
                        results.add(ref);
                    }
		        }
	        }
        }
        return results;
    }

    @Override
    public boolean isDocumentMember(NodeRef employee, NodeRef document) {
        return getDocumentMember(document, employee) != null;
    }

    @Override
    public NodeRef getRoot() {
        return getServiceRootFolder();
    }

    @Override
    public NodeRef getMembersUnit(QName docType) {
        if (dictionaryService.isSubClass(docType, DocumentService.TYPE_BASE_DOCUMENT)) {
            String type = docType.toPrefixString(namespaceService).replaceAll(":", "_");
            return getDocMembersUnit(type);
        }
        return null;
    }

    private NodeRef getDocumentMember(NodeRef document, NodeRef employee) {
        List<NodeRef> empMembers = findNodesByAssociationRef(document,DocumentMembersService.ASSOC_DOC_MEMBERS,null,ASSOCIATION_TYPE.TARGET);
        for (NodeRef member : empMembers) {
            NodeRef employeeRef = nodeService.getTargetAssocs(member, ASSOC_MEMBER_EMPLOYEE).get(0).getTargetRef();
            if (employeeRef.equals(employee)) {
                return member;
            }
        }
        return null;
    }

	@Override
	public NodeRef getServiceRootFolder() {
            return getFolder(DMS_ROOT_ID);
	}

    /**
     * Получение(или создание, если нет) ноды со списком участников документооборота
     * @param docType тип документов (краткое представление с заменой ":" на "_" )
     * @return ссылка на ноду
     */
    public NodeRef getDocMembersUnit(final String docType) {
        AuthenticationUtil.RunAsWork<NodeRef> raw = new AuthenticationUtil.RunAsWork<NodeRef>() {
            @Override
            public NodeRef doWork() throws Exception {
				return nodeService.getChildByName(getRoot(), ContentModel.ASSOC_CONTAINS, docType.replaceAll(":", "_"));
			}
        };
        return AuthenticationUtil.runAsSystem(raw);
    }

	@Override
	public NodeRef createDocMemberUnit(final String docType) throws WriteTransactionNeededException {
		try {
            lecmTransactionHelper.checkTransaction();
        } catch (TransactionNeededException ex) {
            throw new WriteTransactionNeededException("RW transaction needed");
        }

		Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
		properties.put(ContentModel.PROP_NAME, docType.replaceAll(":", "_"));
		return createNode(getRoot(), DocumentMembersService.TYPE_DOC_MEMBERS_UNIT, docType, properties);
	}

    /**
     * Добавление нового участника в ноду со списком всех участников для данного типа документа
     * @param employeeRef ссылка на сотрудника
     * @param document ссылка на документ (для извлечения типа)
     */
    public void addMemberToUnit(NodeRef employeeRef, NodeRef document) {
        NodeRef memberUnit = getMembersUnit(nodeService.getType(document));
            try {
                List<AssociationRef> assocs = nodeService.getTargetAssocs(memberUnit, DocumentMembersService.ASSOC_UNIT_EMPLOYEE);
                AssociationRef ref = new AssociationRef(memberUnit, DocumentMembersService.ASSOC_UNIT_EMPLOYEE, employeeRef);
                if (!assocs.contains(ref)) {
                    nodeService.createAssociation(memberUnit, employeeRef, DocumentMembersService.ASSOC_UNIT_EMPLOYEE);
                }
            } catch (AssociationExistsException ex) {
                logger.debug("Сотрудник уже сохранен в участниках документооборота для данного типа документов:" + nodeService.getType(document));
            }
    }

	public LecmPermissionService.LecmPermissionGroup getMemberPermissionGroup(NodeRef memberRef) {
		LecmPermissionService.LecmPermissionGroup pgGranting = null;
		String permGroup = (String) nodeService.getProperty(memberRef, DocumentMembersService.PROP_MEMBER_GROUP);
		if (permGroup != null && !permGroup.isEmpty()) {
			pgGranting = lecmPermissionService.findPermissionGroup(permGroup);
		}
		if (pgGranting == null) {
			pgGranting = lecmPermissionService.findPermissionGroup(DEFAULT_ACCESS);
		}
		return pgGranting;
	}

}
