package ru.it.lecm.actions.bean;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import java.util.List;

/**
 * User: pmelnikov
 * Date: 19.02.14
 * Time: 16:13
 */
public interface GroupActionsService {

    String GA_ROOT_ID = "GA_ROOT_ID";
    String GA_ROOT_NAME = "Сервис Групповые операции";

    String NAMESPACE_URI = "http://www.it.ru/lecm/group-actions/1.0";

    QName TYPE_GROUP_ACTION = QName.createQName(NAMESPACE_URI, "base-action");
    QName TYPE_GROUP_SCRIPT_ACTION = QName.createQName(NAMESPACE_URI, "script-action");
    QName TYPE_GROUP_WORKFLOW_ACTION = QName.createQName(NAMESPACE_URI, "workflow-action");
    QName TYPE_GROUP_DOCUMENT_ACTION = QName.createQName(NAMESPACE_URI, "document-action");

    QName PROP_TYPE = QName.createQName(NAMESPACE_URI, "type");
    QName PROP_STATUSES = QName.createQName(NAMESPACE_URI, "statuses");
    QName PROP_EXPRESSION = QName.createQName(NAMESPACE_URI, "expression");
    QName PROP_IS_GROUP = QName.createQName(NAMESPACE_URI, "isGroup");
    QName PROP_FOR_COLLECTION = QName.createQName(NAMESPACE_URI, "forCollection");
    QName PROP_ORDER = QName.createQName(NAMESPACE_URI, "order");
    QName PROP_SCRIPT = QName.createQName(NAMESPACE_URI, "script");
    QName PROP_DOCUMENT_TYPE = QName.createQName(NAMESPACE_URI, "document-type");
    QName PROP_DOCUMENT_CONNECTION = QName.createQName(NAMESPACE_URI, "document-connection");
    QName PROP_DOCUMENT_CONNECTION_SYSTEM = QName.createQName(NAMESPACE_URI, "document-connection-system");
    QName PROP_WORKFLOW = QName.createQName(NAMESPACE_URI, "workflow");

    NodeRef getHomeRef();

    /**
     * Вывод груповвых действий для набора документов
     *
     * @param forItems
     * @return
     */
    List<NodeRef> getActiveGroupActions(List<NodeRef> forItems);

    /**
     * Вывод не груповвых действий для документа
     *
     * @param item
     * @return
     */
    List<NodeRef> getActiveActions(NodeRef item);

	List<String> getAspects();
}
