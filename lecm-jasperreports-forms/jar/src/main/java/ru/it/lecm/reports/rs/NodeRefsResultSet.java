package ru.it.lecm.reports.rs;

import org.alfresco.repo.search.AbstractResultSet;
import org.alfresco.repo.search.SimpleResultSetMetaData;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.*;

import java.util.Iterator;
import java.util.List;

/**
 * User: dbashmakov
 * Date: 14.01.2015
 * Time: 10:33
 */
public class NodeRefsResultSet extends AbstractResultSet {

    private List<NodeRef> cars;
    private NodeService nodeService;

    public NodeRefsResultSet(NodeService nodeService, List<NodeRef> nodeRefs) {
        super();
        this.nodeService = nodeService;
        this.cars = nodeRefs;
    }

    @Override
    public int length() {
        return cars.size();
    }

    @Override
    public long getNumberFound() {
        return cars.size();
    }

    @Override
    public NodeRef getNodeRef(int n) {
        return cars.get(n);
    }

    @Override
    public ResultSetRow getRow(int i) {
        return new NodeRefResultSetRow(this, i);
    }

    @Override
    public ChildAssociationRef getChildAssocRef(int n) {
        return null;
    }

    @Override
    public ResultSetMetaData getResultSetMetaData() {
        return new SimpleResultSetMetaData(LimitBy.UNLIMITED, PermissionEvaluationMode.EAGER, new SearchParameters());
    }

    @Override
    public int getStart() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasMore() {
        throw new UnsupportedOperationException();
    }

    public NodeService getNodeService() {
        return nodeService;
    }

    @Override
    public Iterator<ResultSetRow> iterator() {
        return new NodeRefResultSetRowIterator(this);
    }
}
