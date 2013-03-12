package ru.it.lecm.documents.scripts;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.springframework.extensions.surf.util.ParameterCheck;
import ru.it.lecm.documents.beans.DocumentMembersService;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: dbashmakov
 * Date: 11.03.13
 * Time: 16:53
 */
public class DocumentMembersWebScriptBean extends BaseScopableProcessorExtension {

    private DocumentMembersService documentMembersService;
    private ServiceRegistry serviceRegistry;
    private NodeService nodeService;

    public void setDocumentMembersService(DocumentMembersService documentMembersService) {
        this.documentMembersService = documentMembersService;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public ScriptNode add(String documentRef, String employeeRef, String paramsString) {
        ParameterCheck.mandatory("documentRef", documentRef);
        ParameterCheck.mandatory("employeeRef", employeeRef);

        Map<QName, Serializable> props = new HashMap<QName, Serializable>();
        if (paramsString != null && !paramsString.isEmpty()) {
            String[] params = paramsString.split(";");
            for (String param : params) {
                String[] parameter = param.split("=");
                String name = parameter[0];
                String value = parameter[1];
                QName qName = QName.createQName(name, serviceRegistry.getNamespaceService());
                props.put(qName, value);
            }
        }

        NodeRef member = documentMembersService.addMember(new NodeRef(documentRef), new NodeRef(employeeRef), props);
        return member != null ? new ScriptNode(member, serviceRegistry, getScope()) : null;
    }

    /**
     * Возвращает массив, пригодный для использования в веб-скриптах
     *
     * @return Scriptable
     */
    private Scriptable createScriptable(List<NodeRef> refs) {
        Object[] results = new Object[refs.size()];
        for (int i = 0; i < results.length; i++) {
            results[i] = new ScriptNode(refs.get(i), this.serviceRegistry, getScope());
        }
        return Context.getCurrentContext().newArray(getScope(), results);
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

    public ScriptNode getMembersFolder(String documentRef) {
        ParameterCheck.mandatory("documentRef", documentRef);
        NodeRef document = new NodeRef(documentRef);
        NodeRef folderRef = documentMembersService.getMembersFolderRef(document);
        return new ScriptNode(folderRef, serviceRegistry, getScope());
    }
}
