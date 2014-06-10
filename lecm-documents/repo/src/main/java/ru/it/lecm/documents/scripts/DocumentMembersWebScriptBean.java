package ru.it.lecm.documents.scripts;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.mozilla.javascript.Scriptable;
import org.springframework.extensions.surf.util.ParameterCheck;
import org.springframework.extensions.webscripts.WebScriptException;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
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

    @SuppressWarnings("unused")
    public ScriptNode addMember(String documentRef, String employeeRef, String permGroup) {
        return addMember(documentRef, employeeRef, permGroup, DocumentMembersService.PROP_SILENT_DEFAULT_VALUE);
    }

    @SuppressWarnings("unused")
    public ScriptNode addMember(String documentRef, String employeeRef, String permGroup, boolean silent) {
        ParameterCheck.mandatory("documentRef", documentRef);
        ParameterCheck.mandatory("employeeRef", employeeRef);

        return addMember(new NodeRef(documentRef), new NodeRef(employeeRef), permGroup, silent);
    }

    @SuppressWarnings("unused")
    public ScriptNode addMember(NodeRef documentRef, NodeRef employeeRef, String permGroup) {
        return addMember(documentRef, employeeRef, permGroup, DocumentMembersService.PROP_SILENT_DEFAULT_VALUE);
    }

    /**
     * Добавление сотрудника в участники заданного документа
     *
     * @param documentRef ссылка на документ документ
     * @param employeeRef ссылка на  сотрудника
     * @param permGroup   группа привилегий
     * @param silent      не отправлять уведомление сотруднику о включении его в участники
     */
    public ScriptNode addMember(NodeRef documentRef, NodeRef employeeRef, String permGroup, boolean silent) {
        ParameterCheck.mandatory("documentRef", documentRef);
        ParameterCheck.mandatory("employeeRef", employeeRef);

        //может использоваться в *.js или *.bpmn20.xml. транзакциями должно рулиться выше, но здесь выбросим WebScriptException
        try {
            NodeRef member = documentMembersService.addMember(documentRef, employeeRef, permGroup, silent);
            return member != null ? new ScriptNode(member, serviceRegistry, getScope()) : null;
        } catch (WriteTransactionNeededException ex) {
            throw new WebScriptException("Can't add document member.", ex);
        }
    }

    @SuppressWarnings("unused")
    public ScriptNode addMember(ScriptNode document, ScriptNode employee, String permGroup) {
        return addMember(document, employee, permGroup, DocumentMembersService.PROP_SILENT_DEFAULT_VALUE);
    }

    @SuppressWarnings("unused")
    public ScriptNode addMember(ScriptNode document, ScriptNode employee, String permGroup, boolean silent) {
        ParameterCheck.mandatory("document", document);
        ParameterCheck.mandatory("employee", employee);

        return addMember(document.getNodeRef(), employee.getNodeRef(), permGroup, silent);
    }

    /**
     * Добавление сотрудника в участники заданного документа без проверки прав на пермиссию «Добавление нового участника»
     *
     * @param document  документ
     * @param employee  сотрудник
     * @param permGroup группа привилегий
     */
    @SuppressWarnings("unused")
    public ScriptNode addMemberWithoutCheckPermission(ScriptNode document, ScriptNode employee, String permGroup) {
        return addMemberWithoutCheckPermission(document, employee, permGroup, DocumentMembersService.PROP_SILENT_DEFAULT_VALUE);
    }

    /**
     * Добавление сотрудника в участники заданного документа без проверки прав на пермиссию «Добавление нового участника»
     *
     * @param document  документ
     * @param employee  сотрудник
     * @param permGroup группа привилегий
     * @param silent    не отправлять уведомление сотруднику о включении его в участники
     */
    public ScriptNode addMemberWithoutCheckPermission(ScriptNode document, ScriptNode employee, String permGroup, boolean silent) {
        ParameterCheck.mandatory("document", document);
        ParameterCheck.mandatory("employee", employee);

        //может использоваться в *.js или *.bpmn20.xml. транзакциями должно рулиться выше, но здесь выбросим WebScriptException
        NodeRef member;
        try {
            member = documentMembersService.addMemberWithoutCheckPermission(document.getNodeRef(), employee.getNodeRef(), permGroup, silent);
            return member != null ? new ScriptNode(member, serviceRegistry, getScope()) : null;
        } catch (WriteTransactionNeededException ex) {
            throw new WebScriptException("Can't add document member.", ex);
        }
    }

    /**
     * Удаление сотрудника из участников заданного документа
     *
     * @param document документ
     * @param employee сотрудник
     */
    @SuppressWarnings("unused")
    public boolean deleteMember(ScriptNode document, ScriptNode employee) {
        ParameterCheck.mandatory("document", document);
        ParameterCheck.mandatory("employee", employee);

        return documentMembersService.deleteMember(document.getNodeRef(), employee.getNodeRef());
    }

    /**
     * Получение списка участников заданного документа
     *
     * @param documentNodeRef документ
     * @param skipItemsCount  сколько участников пропустить при поиске
     * @param loadItemsCount  сколько участников загрузить
     */
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

    /**
     * Получение списка участников заданного документа
     *
     * @param documentNodeRef документ
     */
    public Scriptable getMembers(String documentNodeRef) {
        ParameterCheck.mandatory("documentNodeRef", documentNodeRef);
        NodeRef documentRef = new NodeRef(documentNodeRef);
        if (this.nodeService.exists(documentRef)) {
            List<NodeRef> members = this.documentMembersService.getDocumentMembers(documentRef);
            return createScriptable(members);
        }
        return null;
    }

    /**
     * Получение директории с участниками для документа
     *
     * @param documentRef документ
     */
    public ScriptNode getMembersFolder(String documentRef) {
        ParameterCheck.mandatory("documentRef", documentRef);
        NodeRef document = new NodeRef(documentRef);
        NodeRef folderRef = documentMembersService.getMembersFolderRef(document);
        return new ScriptNode(folderRef, serviceRegistry, getScope());
    }
}
