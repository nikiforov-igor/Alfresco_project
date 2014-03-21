package ru.it.lecm.ord.api;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 *
 * @author dbayandin
 */
public interface ORDReportsService {
	public NodeRef generateDocumentReport(final String reportCode, final String templateCode, final String documentRef);
}
