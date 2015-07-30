package ru.it.lecm.contractors.api;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.json.JSONArray;

import java.util.List;
import java.util.Map;

public interface Contractors {

    public static final String CONTRACTOR_NAMESPACE = "http://www.it.ru/lecm/contractors/model/contractor/1.0";
    public static final String REPRESENTATIVE_NAMESPACE = "http://www.it.ru/lecm/contractors/model/representative/1.0";
    public static final QName TYPE_CONTRACTOR = QName.createQName(CONTRACTOR_NAMESPACE, "contractor-type");
    public static final QName TYPE_REPRESENTATIVE = QName.createQName(REPRESENTATIVE_NAMESPACE, "representative-type");
    public static final QName TYPE_REPRESENTATIVE_AND_CONTRACTOR = QName.createQName(CONTRACTOR_NAMESPACE, "link-representative-and-contractor");
    public static final QName PROP_CONTRACTOR_SHORTNAME = QName.createQName(CONTRACTOR_NAMESPACE, "shortname");
    public static final QName PROP_CONTRACTOR_FULLNAME = QName.createQName(CONTRACTOR_NAMESPACE, "fullname");
    public static final QName PROP_CONTRACTOR_CODE = QName.createQName(CONTRACTOR_NAMESPACE, "contractor-code");
    public static final QName PROP_CONTRACTOR_LEGAL_ADDRESS = QName.createQName(CONTRACTOR_NAMESPACE, "legal-address");
    public static final QName PROP_CONTRACTOR_PHISICAL_ADDRESS = QName.createQName(CONTRACTOR_NAMESPACE, "physical-address");
    public static final QName PROP_CONTRACTOR_INN = QName.createQName(CONTRACTOR_NAMESPACE, "INN");
    public static final QName PROP_CONTRACTOR_KPP = QName.createQName(CONTRACTOR_NAMESPACE, "KPP");
    public static final QName PROP_REPRESENTATIVE_SURNAME = QName.createQName(REPRESENTATIVE_NAMESPACE, "surname");
    public static final QName PROP_REPRESENTATIVE_FIRSTNAME = QName.createQName(REPRESENTATIVE_NAMESPACE, "firstname");
    public static final QName PROP_REPRESENTATIVE_MIDDLENAME = QName.createQName(REPRESENTATIVE_NAMESPACE, "middlename");
    public static final QName PROP_REPRESENTATIVE_EMAIL = QName.createQName(REPRESENTATIVE_NAMESPACE, "email");
    public static final QName ASSOC_LINK_TO_REPRESENTATIVE = QName.createQName(CONTRACTOR_NAMESPACE, "link-to-representative-association");
    public static final QName PROP_CONTRACTOR_INTERACTION_TYPE = QName.createQName(CONTRACTOR_NAMESPACE, "interaction-type");
    public static final QName PROP_CONTRACTOR_EMAIL = QName.createQName(CONTRACTOR_NAMESPACE, "email");

    public void assignAsPrimaryRepresentative(NodeRef representativeToAssignAsPrimary);

    public List<NodeRef> getContractorsForRepresentative(NodeRef childContractor);

    public Map<String, String> getParentContractor(NodeRef childContractor);

    public List<Object> getRepresentatives(NodeRef targetContractor);

    public JSONArray getBusyRepresentatives();

    /**
     * Возвращает адресанта контрагента по E-Mail
     * @param email
     * @return
     */
    public NodeRef getRepresentativeByEmail(String email);

    /**
     * Возвращает ссылку на контрагента, которому пренадлежит адресант
     * @param representative
     * @return
     */
    public NodeRef getContractor(NodeRef representative);

}
