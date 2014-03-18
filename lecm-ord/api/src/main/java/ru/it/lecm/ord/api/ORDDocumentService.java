package ru.it.lecm.ord.api;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 *
 * @author dbayandin
 */
public interface ORDDocumentService {
	
	public String getDocumentURL(NodeRef documentRef);
}
