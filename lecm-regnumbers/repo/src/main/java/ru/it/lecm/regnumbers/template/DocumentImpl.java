package ru.it.lecm.regnumbers.template;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;

/**
 *
 * @author vlevin
 */
public class DocumentImpl implements Document {
	private NodeRef documentNode;
	private NodeService nodeService;

	public DocumentImpl(NodeRef documentNode, NodeService nodeService) {
		this.documentNode = documentNode;
		this.nodeService = nodeService;
	}

	@Override
	public Object getAttribute(String attributeName) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Object getAssosiatedAttribute(String assocName, String attributeName) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int getTypeCode() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String getTypeName() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public NodeRef getMember(String memberType) {
		throw new UnsupportedOperationException("Not supported yet.");
	}



}
