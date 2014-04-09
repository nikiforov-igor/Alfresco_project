
package ru.it.lecm.nd.api;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 *
 * @author snovikov
 */
public interface NDDocumentService {
	
	public boolean checkApprovalORDExists(NodeRef nd);
}
