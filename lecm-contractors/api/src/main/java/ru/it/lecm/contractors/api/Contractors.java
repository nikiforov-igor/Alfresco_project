package ru.it.lecm.contractors.api;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import java.util.List;
import java.util.Map;
import org.json.JSONArray;

public interface Contractors {

    String CONTRACTOR_NAMESPACE = "http://www.it.ru/lecm/contractors/model/contractor/1.0";
    String REPRESENTATIVE_NAMESPACE = "http://www.it.ru/lecm/contractors/model/representative/1.0";
    QName TYPE_CONTRACTOR = QName.createQName(CONTRACTOR_NAMESPACE, "contractor-type");
	QName TYPE_REPRESENTATIVE = QName.createQName(CONTRACTOR_NAMESPACE, "representative-type");
	QName TYPE_REPRESENTATIVE_AND_CONTRACTOR = QName.createQName(CONTRACTOR_NAMESPACE, "link-representative-and-contractor");
    QName PROP_CONTRACTOR_SHORTNAME = QName.createQName(CONTRACTOR_NAMESPACE, "shortname");
    QName PROP_CONTRACTOR_INN = QName.createQName(CONTRACTOR_NAMESPACE, "INN");
    QName PROP_REPRESENTATIVE_SURNAME = QName.createQName(REPRESENTATIVE_NAMESPACE, "surname");
    QName PROP_REPRESENTATIVE_FIRSTNAME = QName.createQName(REPRESENTATIVE_NAMESPACE, "firstname");
    QName PROP_REPRESENTATIVE_MIDDLENAME = QName.createQName(REPRESENTATIVE_NAMESPACE, "middlename");
	QName ASSOC_LINK_TO_REPRESENTATIVE = QName.createQName(CONTRACTOR_NAMESPACE, "link-to-representative-association");
	QName PROP_CONTRACTOR_INTERACTION_TYPE = QName.createQName(CONTRACTOR_NAMESPACE, "interaction-type");
	QName PROP_CONTRACTOR_EMAIL = QName.createQName(CONTRACTOR_NAMESPACE, "email");

    void assignAsPrimaryRepresentative(NodeRef representativeToAssignAsPrimary);
    Map<String, String> getContractorForRepresentative(NodeRef childContractor);
    Map<String, String> getParentContractor(NodeRef childContractor);
    List<Object> getRepresentatives(NodeRef targetContractor);
	JSONArray getBusyRepresentatives();
}
