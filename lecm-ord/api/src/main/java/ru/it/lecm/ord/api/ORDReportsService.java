package ru.it.lecm.ord.api;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * @author dbayandin
 */
public interface ORDReportsService {
    NodeRef generateDocumentReport(String reportCode, String templateCode, String documentRef);
}
