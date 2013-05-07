package ru.it.lecm.documents.scripts;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.mozilla.javascript.Scriptable;
import org.springframework.extensions.surf.util.ParameterCheck;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.documents.beans.DocumentMembersService;

import java.util.List;

/**
 * User: dbashmakov
 * Date: 11.03.13
 * Time: 16:53
 */
public class DocumentMembersWebScriptBean extends BaseWebScript {

    private DocumentMembersService documentMembersService;
    private NodeService nodeService;

    public void setDocumentMembersService(DocumentMembersService documentMembersService) {
        this.documentMembersService = documentMembersService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public ScriptNode add(String documentRef, String employeeRef, String permGroup) {
        ParameterCheck.mandatory("documentRef", documentRef);
        ParameterCheck.mandatory("employeeRef", employeeRef);

        NodeRef member = documentMembersService.addMember(new NodeRef(documentRef), new NodeRef(employeeRef), permGroup);
        return member != null ? new ScriptNode(member, serviceRegistry, getScope()) : null;
    }

    public Scriptable getMembers(String documentNodeRef, String skipItemsCount, String loadItemsCount) {
        ParameterCheck.mandatory("documentNodeRef", documentNodeRef);
        ParameterCheck.mandatory("skipItemsCount", skipItemsCount);
        ParameterCheck.mandatory("loadItemsCount", loadItemsCount);

        NodeRef documentRef = new NodeRef(documentNodeRef);
        if (this.nodeService.exists(documentRef)) {
            List<NodeRef> members = this.documentMembersService.getDocumentMembers(documentRef, Integer.parseInt(skipItemsCount), Integer.parseInt(loadItemsCount));
            return createScriptable(members);
        }
        return null;
    }

    public Scriptable getMembers(String documentNodeRef) {
        ParameterCheck.mandatory("documentNodeRef", documentNodeRef);
        NodeRef documentRef = new NodeRef(documentNodeRef);
        if (this.nodeService.exists(documentRef)) {
            List<NodeRef> members = this.documentMembersService.getDocumentMembers(documentRef);
            return createScriptable(members);
        }
        return null;
    }

    public ScriptNode getMembersFolder(String documentRef) {
        ParameterCheck.mandatory("documentRef", documentRef);
        NodeRef document = new NodeRef(documentRef);
        NodeRef folderRef = documentMembersService.getMembersFolderRef(document);
        return new ScriptNode(folderRef, serviceRegistry, getScope());
    }
}
