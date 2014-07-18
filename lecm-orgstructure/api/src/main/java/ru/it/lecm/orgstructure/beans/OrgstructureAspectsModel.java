package ru.it.lecm.orgstructure.beans;

import org.alfresco.service.namespace.QName;

/**
 * User: dbashmakov
 * Date: 18.07.2014
 * Time: 15:36
 */
public class OrgstructureAspectsModel {

    private final static String ORGSTRUCTURE_ASPECTS_NAMESPACE_URI = "http://www.it.ru/lecm/org/structure/aspects/1.0";

    public final static QName ASPECT_HAS_ORGANIZATION = QName.createQName(ORGSTRUCTURE_ASPECTS_NAMESPACE_URI, "has-organization-aspect");
    public final static QName ASPECT_HAS_LINKED_CONTRACTOR = QName.createQName(ORGSTRUCTURE_ASPECTS_NAMESPACE_URI, "has-linked-contractor-aspect");
    public final static QName ASSOC_LINKED_CONTRACTOR = QName.createQName(ORGSTRUCTURE_ASPECTS_NAMESPACE_URI, "linked-contractor-assoc");
}
