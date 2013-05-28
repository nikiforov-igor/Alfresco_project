package ru.it.lecm.contractors.api;

import org.alfresco.service.cmr.repository.NodeRef;
import java.util.List;
import java.util.Map;

public interface Contractors {
    void assignAsPrimaryRepresentative(NodeRef representativeToAssignAsPrimary);
    Map<String, String> getParentContractor(NodeRef childContractor);
    List<Object> getRepresentatives(NodeRef targetContractor);
}
