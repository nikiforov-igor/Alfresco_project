package ru.it.lecm.contractors.api;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.json.JSONArray;

import java.util.List;
import java.util.Map;

public interface Contractors {

    String CONTRACTOR_NAMESPACE = "http://www.it.ru/lecm/contractors/model/contractor/1.0";
    String REPRESENTATIVE_NAMESPACE = "http://www.it.ru/lecm/contractors/model/representative/1.0";
    String LEGALFORM_NAMESPACE = "http://www.it.ru/lecm/contractors/model/legalform/1.0";

    QName TYPE_CONTRACTOR = QName.createQName(CONTRACTOR_NAMESPACE, "contractor-type");
    QName TYPE_PHYSICAL_PERSON = QName.createQName(CONTRACTOR_NAMESPACE, "physical-person-type");
    QName TYPE_REPRESENTATIVE = QName.createQName(REPRESENTATIVE_NAMESPACE, "representative-type");
    QName TYPE_REPRESENTATIVE_AND_CONTRACTOR = QName.createQName(CONTRACTOR_NAMESPACE, "link-representative-and-contractor");
    QName PROP_CONTRACTOR_SHORTNAME = QName.createQName(CONTRACTOR_NAMESPACE, "shortname");
    QName PROP_CONTRACTOR_FULLNAME = QName.createQName(CONTRACTOR_NAMESPACE, "fullname");
    QName PROP_CONTRACTOR_CODE = QName.createQName(CONTRACTOR_NAMESPACE, "contractor-code");
    QName PROP_CONTRACTOR_LEGAL_ADDRESS = QName.createQName(CONTRACTOR_NAMESPACE, "legal-address");
    QName PROP_CONTRACTOR_PHISICAL_ADDRESS = QName.createQName(CONTRACTOR_NAMESPACE, "physical-address");
    QName PROP_CONTRACTOR_INN = QName.createQName(CONTRACTOR_NAMESPACE, "INN");
    QName PROP_CONTRACTOR_KPP = QName.createQName(CONTRACTOR_NAMESPACE, "KPP");
    QName PROP_REPRESENTATIVE_SURNAME = QName.createQName(REPRESENTATIVE_NAMESPACE, "surname");
    QName PROP_REPRESENTATIVE_FIRSTNAME = QName.createQName(REPRESENTATIVE_NAMESPACE, "firstname");
    QName PROP_REPRESENTATIVE_MIDDLENAME = QName.createQName(REPRESENTATIVE_NAMESPACE, "middlename");
    QName PROP_REPRESENTATIVE_EMAIL = QName.createQName(REPRESENTATIVE_NAMESPACE, "email");
    QName ASSOC_LINK_TO_REPRESENTATIVE = QName.createQName(CONTRACTOR_NAMESPACE, "link-to-representative-association");
    QName PROP_CONTRACTOR_INTERACTION_TYPE = QName.createQName(CONTRACTOR_NAMESPACE, "interaction-type");
    QName PROP_CONTRACTOR_EMAIL = QName.createQName(CONTRACTOR_NAMESPACE, "email");
    QName PROP_PHYSICAL_PERSON_LAST_NAME = QName.createQName(CONTRACTOR_NAMESPACE, "lastName");
    QName PROP_PHYSICAL_PERSON_FIST_NAME = QName.createQName(CONTRACTOR_NAMESPACE, "firstName");
    QName PROP_PHYSICAL_PERSON_MIDDLE_NAME = QName.createQName(CONTRACTOR_NAMESPACE, "middleName");

    QName PROP_LEGALFORM_SHORT_TITLE = QName.createQName(LEGALFORM_NAMESPACE, "short-title");
    QName PROP_LEGALFORM_FULL_TITLE = QName.createQName(LEGALFORM_NAMESPACE, "full-title");

    String OPF_DIC_NAME = "Контрагенты Организационно-правовые формы";
    QName[] DIC_REPLACE_PROPERTIES = {Contractors.PROP_LEGALFORM_FULL_TITLE, Contractors.PROP_LEGALFORM_SHORT_TITLE};

    void assignAsPrimaryRepresentative(NodeRef representativeToAssignAsPrimary);

    List<NodeRef> getContractorsForRepresentative(NodeRef childContractor);

    Map<String, String> getParentContractor(NodeRef childContractor);

    List<Object> getRepresentatives(NodeRef targetContractor);

    JSONArray getBusyRepresentatives();

    /**
     * Возвращает адресанта контрагента по E-Mail
     * @param email
     * @return
     */
    NodeRef getRepresentativeByEmail(String email);

    /**
     * Возвращает ссылку на контрагента, которому пренадлежит адресант
     * @param representative
     * @return
     */
    NodeRef getContractor(NodeRef representative);

    /**
     * Возвращает обработанное название контрагента
     * @param originalName название контрагента
     * @return String обработанная строка
     */
    String formatContractorName(String originalName);

}
