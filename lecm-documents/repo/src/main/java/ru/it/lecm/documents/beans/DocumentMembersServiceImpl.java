package ru.it.lecm.documents.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.query.PagingRequest;
import org.alfresco.query.PagingResults;
import org.alfresco.repo.node.getchildren.FilterProp;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.FileFilterMode;
import org.alfresco.util.GUID;
import org.alfresco.util.Pair;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.LecmObjectsService;
import ru.it.lecm.security.LecmPermissionService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: dbashmakov
 * Date: 11.03.13
 * Time: 14:45
 */
public class DocumentMembersServiceImpl extends BaseBean implements DocumentMembersService {

    public static final int MAX_ITEMS = 1000;
    private LecmObjectsService lecmObjectsService;
    private LecmPermissionService lecmPermissionService;

    public void setLecmObjectsService(LecmObjectsService lecmObjectsService) {
        this.lecmObjectsService = lecmObjectsService;
    }

    public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
        this.lecmPermissionService = lecmPermissionService;
    }

    @Override
    public NodeRef addMember(final NodeRef document, final NodeRef employeeRef, final Map<QName, Serializable> properties) {
        lecmPermissionService.checkPermission("_lecmPerm_MemberAdd", document);
        final NodeRef documentMembersFolder = getMembersFolderRef(document);
        if (employeeRef != null && !isDocumentMember(document, employeeRef)) {
            return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
                @Override
                public NodeRef execute() throws Throwable {
                    ChildAssociationRef associationRef = nodeService.createNode(documentMembersFolder, ContentModel.ASSOC_CONTAINS,
                            QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, GUID.generate()), TYPE_DOC_MEMBER, properties);
                    NodeRef newMemberRef = associationRef.getChildRef();
                    nodeService.createAssociation(newMemberRef, employeeRef, DocumentMembersService.ASSOC_MEMBER_EMPLOYEE);
                    return newMemberRef;
                }
            });
        }
        return null;
    }

    private String generateMemberName(NodeRef memberRef) {
        Object propGroup = nodeService.getProperty(memberRef, PROP_MEMBER_GROUP);
        String groupName = propGroup != null ? (String) propGroup : "";
        List<AssociationRef> employeeList = nodeService.getTargetAssocs(memberRef, ASSOC_MEMBER_EMPLOYEE);
        NodeRef employee = null;
        if (employeeList.size() > 0) {
            employee = employeeList.get(0).getTargetRef();
        }
        String propName = employee != null ? (String) nodeService.getProperty(employee, ContentModel.PROP_NAME) : "unnamed";

        return (propName + " " + groupName).trim();
    }

    @Override
    public synchronized NodeRef getMembersFolderRef(final NodeRef document) {
        NodeRef membersFolder = nodeService.getChildByName(document, ContentModel.ASSOC_CONTAINS, DOCUMENT_MEMBERS_ROOT_NAME);
        if (membersFolder == null) {
            membersFolder = AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<NodeRef>() {
                @Override
                public NodeRef doWork() throws Exception {
                    RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper();
                    return transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
                        @Override
                        public NodeRef execute() throws Throwable {
                            QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, DOCUMENT_MEMBERS_ROOT_NAME);
                            Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
                            properties.put(ContentModel.PROP_NAME, DOCUMENT_MEMBERS_ROOT_NAME);
                            ChildAssociationRef childAssoc = nodeService.createNode(document, ContentModel.ASSOC_CONTAINS, assocQName, ContentModel.TYPE_FOLDER, properties);
                            return childAssoc.getChildRef();
                        }
                    });
                }
            });
        }
        return membersFolder;
    }

    @Override
    public List<NodeRef> getDocumentMembers(NodeRef document) {
        return getDocumentMembers(document, 0, MAX_ITEMS);
    }

    @Override
    public List<NodeRef> getDocumentMembers(NodeRef document, int skipCount, int maxItems) {
        lecmPermissionService.checkPermission("_lecmPerm_MemberList", document);
        List<NodeRef> results = new ArrayList<NodeRef>();
        List<Pair<QName, Boolean>> sortProps = new ArrayList<Pair<QName, Boolean>>(1);
        sortProps.add(new Pair<QName, Boolean>(ContentModel.PROP_MODIFIED, false));

        PagingRequest pageRequest = new PagingRequest(skipCount, maxItems, null);
        pageRequest.setRequestTotalCountMax(MAX_ITEMS);

        PagingResults<NodeRef> pageOfNodeInfos = null;
        FileFilterMode.setClient(FileFilterMode.Client.script);
        try {
            pageOfNodeInfos = lecmObjectsService.list(getMembersFolderRef(document), TYPE_DOC_MEMBER, new ArrayList<FilterProp>(), sortProps, pageRequest);
        } finally {
            FileFilterMode.clearClient();
        }

        List<NodeRef> nodeInfos = pageOfNodeInfos.getPage();
        for (NodeRef ref : nodeInfos) {
            results.add(ref);
        }
        return results;
    }

    @Override
    public boolean isDocumentMember(NodeRef document, NodeRef employee) {
        return getDocumentMember(document, employee) != null;
    }

    @Override
    public String generateMemberNodeName(NodeRef member) {
        return generateMemberName(member);
    }

    private NodeRef getDocumentMember(NodeRef document, NodeRef employee) {
        List<AssociationRef> empMembers = nodeService.getTargetAssocs(document, DocumentService.ASSOC_DOC_MEMBERS);
        for (AssociationRef empMember : empMembers) {
            NodeRef member = empMember.getTargetRef();
            NodeRef employeeRef = nodeService.getTargetAssocs(member, ASSOC_MEMBER_EMPLOYEE).get(0).getTargetRef();
            if (employeeRef.equals(employee)) {
                return member;
            }
        }
        return null;
    }
}
