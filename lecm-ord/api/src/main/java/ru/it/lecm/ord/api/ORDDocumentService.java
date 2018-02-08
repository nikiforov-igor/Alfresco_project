package ru.it.lecm.ord.api;

import org.alfresco.service.cmr.repository.NodeRef;

import java.util.List;

/**
 *
 * @author dbayandin
 */
public interface ORDDocumentService {

	public String getOrdStatusName(ORDModel.ORD_STATUS code);
	public String getAttachmentCategoryName(ORDModel.ATTACHMENT_CATEGORIES code);
	public String getDocumentURL(NodeRef documentRef);
	public void changePointStatus(NodeRef point, String statusKey);
	public NodeRef getErrandLinkedPoint(NodeRef errand);
	public String getPointStatus(NodeRef point);
	public Boolean checkPointStatus(NodeRef point, String statusKey);
	public Boolean haveNotPointsWithController(NodeRef document);
	public Boolean haveNotPointsWithDueDate(NodeRef document);
	public List<NodeRef> getOrdDocumentPoints(NodeRef document);
	Boolean isDocumentHavePointsAndProperties(NodeRef document);
	Boolean haveNotPointsWithDueDateAndController(NodeRef document);
	Boolean haveNotPointsWithControllerAndHaveDueDate(NodeRef document);
	Boolean haveNotPointsWithDueDateAndHaveController(NodeRef document);
	Boolean currentUserIsReviewer(NodeRef document);
}
