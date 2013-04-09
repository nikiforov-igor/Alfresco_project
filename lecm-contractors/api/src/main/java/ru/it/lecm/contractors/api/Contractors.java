package ru.it.lecm.contractors.api;

import org.alfresco.service.cmr.repository.NodeRef;

public interface Contractors {
    void assignAsPrimaryRepresentative(NodeRef representativeToAssignAsPrimary);
}
