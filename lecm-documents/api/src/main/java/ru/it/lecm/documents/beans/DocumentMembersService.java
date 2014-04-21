package ru.it.lecm.documents.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.security.LecmPermissionService;

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

    String LECM_DOCUMENT_MEMBERS_ROOT_NAME = "Участники документооборота";
    String DMS_ROOT_ID = "DMS_ROOT_ID";

    QName TYPE_DOC_MEMBER = QName.createQName(DOC_MEMBERS_NAMESPACE_URI, "member");
    QName PROP_MEMBER_GROUP = QName.createQName(DOC_MEMBERS_NAMESPACE_URI, "group");
    QName PROP_SILENT = QName.createQName(DOC_MEMBERS_NAMESPACE_URI, "silent");
    boolean PROP_SILENT_DEFAULT_VALUE = false;
    QName ASSOC_MEMBER_EMPLOYEE = QName.createQName(DOC_MEMBERS_NAMESPACE_URI, "employee-assoc");
    QName ASSOC_DOC_MEMBERS = QName.createQName(DocumentService.DOCUMENT_NAMESPACE_URI, "doc-members-assoc");
    QName PROP_DOC_MEMBERS = QName.createQName(DocumentService.DOCUMENT_NAMESPACE_URI, "doc-members-ref");
    QName TYPE_DOC_MEMBERS_UNIT = QName.createQName(DOC_MEMBERS_NAMESPACE_URI, "unit");
    QName ASSOC_UNIT_EMPLOYEE = QName.createQName(DOC_MEMBERS_NAMESPACE_URI, "unit-employee-assoc");

    /**
     * Добавление нового участника с проверкой прав доступа
     * @param document - ссылка на документ
     * @param employee - ссылка на сотрудника
     * @param properties - карта свойств
     * @return ссылка на созданную ноду участника
     */
    NodeRef addMember(NodeRef document, NodeRef employee, Map<QName, Serializable> properties);

    /**
     * Добавление нового участника с проверкой прав доступа
     * @param document - ссылка на документ
     * @param employeeRef - ссылка на сотрудника
     * @param properties - карта свойств
     * @param silent - не отправлять уведомление сотруднику о включении его в участники
     * @return ссылка на созданную ноду участника
     */
    NodeRef addMember(NodeRef document, NodeRef employeeRef, Map<QName, Serializable> properties, boolean silent);

    /**
     * Добавление нового участника  с проверкой прав доступа
     * @param document - ссылка на документ
     * @param employee - ссылка на сотрудника
     * @param permissionGroup - Группа привилегий
     * @return ссылка на созданную ноду участника
     */
    NodeRef addMember(NodeRef document, NodeRef employee, String permissionGroup);

    /**
     * Добавление нового участника без проверки прав доступа
     * @param document - ссылка на документ
     * @param employee - ссылка на сотрудника
     * @param permissionGroup - Группа привилегий
     * @param silent - не отправлять уведомление сотруднику о включении его в участники
     * @return ссылка на созданную ноду участника
     */
    NodeRef addMemberWithoutCheckPermission(NodeRef document, NodeRef employee, String permissionGroup, boolean silent);

    /**
     * Добавление нового участника без проверки прав доступа
     * @param document - ссылка на документ
     * @param employeeRef - ссылка на сотрудника
     * @param properties - карта свойств
     * @param silent - не отправлять уведомление сотруднику о включении его в участники
     * @return ссылка на созданную ноду участника
     */
    NodeRef addMemberWithoutCheckPermission(NodeRef document, NodeRef employeeRef, Map<QName, Serializable> properties, boolean silent);

    /**
     * Добавление нового участника без проверки прав доступа
     * @param document - ссылка на документ
     * @param employee - ссылка на сотрудника
     * @param properties - карта свойств
     * @return ссылка на созданную ноду участника
     */
    NodeRef addMemberWithoutCheckPermission(NodeRef document, NodeRef employee, Map<QName, Serializable> properties);

    /**
     * Добавление нового участника  с проверкой прав доступа
     * @param document - ссылка на документ
     * @param employee - ссылка на сотрудника
     * @param permissionGroup - Группа привилегий
     * @param silent - не отправлять уведомление сотруднику о включении его в участники
     * @return ссылка на созданную ноду участника
     */
    NodeRef addMember(NodeRef document, NodeRef employee, String permissionGroup, boolean silent);

    /**
     * Добавление нового участника без проверки прав доступа
     * @param document - ссылка на документ
     * @param employee - ссылка на сотрудника
     * @param permissionGroup - Группа привилегий
     * @return ссылка на созданную ноду участника
     */
    NodeRef addMemberWithoutCheckPermission(NodeRef document, NodeRef employee, String permissionGroup);

    /**
     * Удаление участника из данного документа
     * @param document - ссылка на документ
     * @param employee - ссылка на сотрудника
     * @return true - удален, false - не удален
     */
    boolean deleteMember(NodeRef document, NodeRef employee);

    /**
     * Возвращает директорию Участники для конкретного документа
     * @param document - ссылка на документ
     * @return ссылка на ноду
     */
    NodeRef getMembersFolderRef(NodeRef document);

    /**
     * Возвращает список участников данного документа
     * @param document - ссылка на документ
     * @return список ссылок на участников
     */
    List<NodeRef> getDocumentMembers(NodeRef document);

    /**
     * Возвращает список участников данного документа
     * @param document - ссылка на документ
     * @param skipCount - сколько результатов надо пропустить
     * @param maxItems - максимальное число результатов
     * @return список ссылок на участников
     */
    List<NodeRef> getDocumentMembers(NodeRef document, int skipCount, int maxItems);

    /**
     * Является ли данный пользователь, участником в данном документе
     * @param employee - ссылка на сотрудника
     * @param document - ссылка на документ
     * @return ссылка на ноду
     */
    boolean isDocumentMember(NodeRef employee, NodeRef document);

    /**
     * Получение корня сервиса (папки LECM/Участники документооборота)
     * @return ссылка на ноду
     */
    NodeRef getRoot();

    /**
     * Получение ссылки на ноду со списком всех участников для конкретного типа документов
     * @param docType тип документов
     * @return ссылка на ноду
     */
    NodeRef getMembersUnit(QName docType);

    /**
     * Добавление нового участника в ноду со списком всех участников для данного типа документа
     * @param employeeRef ссылка на сотрудника
     * @param document ссылка на документ (для извлечения типа)
     */
    public void addMemberToUnit(NodeRef employeeRef, NodeRef document);

	public LecmPermissionService.LecmPermissionGroup getMemberPermissionGroup(NodeRef memberRef);
}
