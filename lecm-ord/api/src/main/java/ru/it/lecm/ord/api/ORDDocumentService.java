package ru.it.lecm.ord.api;

import org.alfresco.service.cmr.repository.NodeRef;

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
}
