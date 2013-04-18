package ru.it.lecm.contractors.api;

import org.alfresco.service.cmr.repository.NodeRef;
import java.util.List;

public interface Contractors {
    void assignAsPrimaryRepresentative(NodeRef representativeToAssignAsPrimary);
    String getParentContractor(NodeRef childContractor);
    List<Object> getRepresentatives(NodeRef targetContractor);
}
