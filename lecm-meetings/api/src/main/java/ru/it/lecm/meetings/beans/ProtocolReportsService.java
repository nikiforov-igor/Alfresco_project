package ru.it.lecm.meetings.beans;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 *
 * @author snovikov
 */
@Deprecated
public interface ProtocolReportsService {
	public NodeRef generateDocumentReport(final String reportCode, final String templateCode, final String documentRef);
}
