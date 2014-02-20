package ru.it.lecm.actions.bean;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

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

    public NodeRef getHomeRef();
}
