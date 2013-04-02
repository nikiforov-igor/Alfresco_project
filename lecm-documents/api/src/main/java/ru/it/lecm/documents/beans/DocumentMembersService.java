package ru.it.lecm.documents.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * User: dbashmakov
 * Date: 11.03.13
 * Time: 14:26
 */
public interface DocumentMembersService {
    String DOC_MEMBERS_NAMESPACE_URI = "http://www.it.ru/logicECM/document/member/1.0";
    String DOCUMENT_MEMBERS_ROOT_NAME = "Участники";

    QName TYPE_DOC_MEMBER = QName.createQName(DOC_MEMBERS_NAMESPACE_URI, "member");
    QName PROP_MEMBER_GROUP = QName.createQName(DOC_MEMBERS_NAMESPACE_URI, "group");
    QName ASSOC_MEMBER_EMPLOYEE = QName.createQName(DOC_MEMBERS_NAMESPACE_URI, "employee-assoc");

    NodeRef addMember(NodeRef document, NodeRef employeeRef, Map<QName, Serializable> properties);

    NodeRef addMemberWithoutCheckPermission(NodeRef document, NodeRef employeeRef, Map<QName, Serializable> properties);

    NodeRef getMembersFolderRef(NodeRef document);

    List<NodeRef> getDocumentMembers(NodeRef document);

    List<NodeRef> getDocumentMembers(NodeRef document, int skipCount, int maxItems);

    boolean isDocumentMember (NodeRef document, NodeRef employee);

    String generateMemberNodeName(NodeRef member);
}
