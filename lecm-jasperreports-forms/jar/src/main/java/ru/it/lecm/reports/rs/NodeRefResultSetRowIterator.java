package ru.it.lecm.reports.rs;

import org.alfresco.repo.search.AbstractResultSetRowIterator;
import org.alfresco.service.cmr.search.ResultSetRow;

/**
 * User: dbashmakov
 * Date: 14.01.2015
 * Time: 10:44
 */
public class NodeRefResultSetRowIterator extends AbstractResultSetRowIterator {

    public NodeRefResultSetRowIterator(NodeRefsResultSet resultSetRows) {
        super(resultSetRows);
    }

    @Override
    public ResultSetRow next() {
        return new NodeRefResultSetRow((NodeRefsResultSet)getResultSet(), moveToNextPosition());
    }

    @Override
    public ResultSetRow previous() {
        return new NodeRefResultSetRow((NodeRefsResultSet)getResultSet(), moveToPreviousPosition());
    }
}
