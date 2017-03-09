package ru.it.lecm.ord.api;

import org.alfresco.service.cmr.repository.NodeRef;

import java.util.List;

/**
 *
 * @author dbayandin
 */
public interface ORDDocumentService {

	public String getDocumentURL(NodeRef documentRef);
	public void changePointStatus(NodeRef point, ORDModel.P_STATUSES statusKey);
	public NodeRef getErrandLinkedPoint(NodeRef errand);
	public String getPointStatus(NodeRef point);
	public Boolean checkPointStatus(NodeRef point, ORDModel.P_STATUSES statusKey);
	public Boolean haveNotPointsWithController(NodeRef document);
	public Boolean haveNotPointsWithDueDate(NodeRef document);
	public List<NodeRef> getOrdDocumentPoints(NodeRef document);
	Boolean isDocumentHavePointsAndProperties(NodeRef document);
}
