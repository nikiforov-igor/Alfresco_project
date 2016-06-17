package ru.it.lecm.documents.beans;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 *
 * @author vmalygin
 */
public interface DocumentMessageService {
	NodeRef getDocumentMessageFolder();
	boolean registerResourceBundle(NodeRef messageResource);
	boolean unregisterResourceBundle(NodeRef messageResource);
}
