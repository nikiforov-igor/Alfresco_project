package ru.it.lecm.reports.rs;

import org.alfresco.repo.search.AbstractResultSetRow;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import java.io.Serializable;
import java.util.Map;

/**
 * User: dbashmakov
 * Date: 14.01.2015
 * Time: 10:37
 */
public class NodeRefResultSetRow extends AbstractResultSetRow {

    public NodeRefResultSetRow(NodeRefsResultSet resultSet, int index) {
        super(resultSet, index);
    }

    @Override
    protected Map<QName, Serializable> getDirectProperties() {
        return ((NodeRefsResultSet) getResultSet()).getNodeService().getProperties(getNodeRef());
    }

    @Override
    public ChildAssociationRef getChildAssocRef() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, NodeRef> getNodeRefs() {
        throw new UnsupportedOperationException();
    }

    @Override
    public NodeRef getNodeRef(String selectorName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Float> getScores() {
        throw new UnsupportedOperationException();
    }

    @Override
    public float getScore(String selectorName) {
        throw new UnsupportedOperationException();
    }
}
