package ru.it.lecm.base.beans;

import java.util.List;
import java.util.Set;

import org.alfresco.query.PagingRequest;
import org.alfresco.query.PagingResults;
import org.alfresco.repo.node.getchildren.FilterProp;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.Pair;

/**
 * Класс для поиска LECM-объектов без использования Solr
 * @author dbashmakov
 *         Date: 08.02.13
 *         Time: 14:59
 */
public interface LecmObjectsService {

    public PagingResults<NodeRef> list(NodeRef contextNodeRef,
                                       QName childType,
                                       List<FilterProp> filterProps,
                                       List<Pair<QName, Boolean>> sortProps,
                                       PagingRequest pagingRequest);

    public PagingResults<NodeRef> list(NodeRef contextNodeRef,
                                       boolean files,
                                       boolean folders,
                                       Set<QName> ignoreTypeQNames,
                                       List<Pair<QName, Boolean>> sortProps,
                                       PagingRequest pagingRequest);

    public Set<QName> buildLecmObjectTypes();
}
