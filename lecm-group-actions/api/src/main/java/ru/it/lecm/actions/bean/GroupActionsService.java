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

    public static final String GA_ROOT_ID = "GA_ROOT_ID";
    public static final String GA_ROOT_NAME = "GA_ROOT_NAME";

    public static final String NAMESPACE_URI = "http://www.it.ru/lecm/group-actions/1.0";

    public static final QName TYPE_GROUP_ACTION = QName.createQName(NAMESPACE_URI, "action");

    public static final QName PROP_FIELDS_REF = QName.createQName(NAMESPACE_URI, "form-fields-ref");
    public static final QName PROP_TYPE = QName.createQName(NAMESPACE_URI, "type");
    public static final QName PROP_STATUSES = QName.createQName(NAMESPACE_URI, "statuses");
    public static final QName PROP_EXPRESSION = QName.createQName(NAMESPACE_URI, "expression");
    public static final QName PROP_IS_GROUP = QName.createQName(NAMESPACE_URI, "isGroup");
    public static final QName PROP_ORDER = QName.createQName(NAMESPACE_URI, "order");
    public static final QName PROP_SCRIPT = QName.createQName(NAMESPACE_URI, "script");

    public NodeRef getHomeRef();

    public List<NodeRef> getActiveGroupActions(List<NodeRef> forItems);
}
