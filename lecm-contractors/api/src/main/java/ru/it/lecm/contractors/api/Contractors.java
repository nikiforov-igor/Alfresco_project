package ru.it.lecm.contractors.api;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import java.util.List;
import java.util.Map;
import org.json.JSONArray;

public interface Contractors {

    String CONTRACTOR_NAMESPACE = "http://www.it.ru/lecm/contractors/model/contractor/1.0";
    QName TYPE_CONTRACTOR = QName.createQName(CONTRACTOR_NAMESPACE, "contractor-type");
	QName TYPE_REPRESENTATIVE = QName.createQName(CONTRACTOR_NAMESPACE, "representative-type");
	QName TYPE_REPRESENTATIVE_AND_CONTRACTOR = QName.createQName(CONTRACTOR_NAMESPACE, "link-representative-and-contractor");
    QName PROP_CONTRACTOR_SHORTNAME = QName.createQName(CONTRACTOR_NAMESPACE, "shortname");
    QName PROP_CONTRACTOR_INN = QName.createQName(CONTRACTOR_NAMESPACE, "INN");
	QName ASSOC_LINK_TO_REPRESENTATIVE = QName.createQName(CONTRACTOR_NAMESPACE, "link-to-representative-association");

    void assignAsPrimaryRepresentative(NodeRef representativeToAssignAsPrimary);
    Map<String, String> getParentContractor(NodeRef childContractor);
    List<Object> getRepresentatives(NodeRef targetContractor);
	JSONArray getBusyRepresentatives();
}
