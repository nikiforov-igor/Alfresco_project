package ru.it.lecm.documents.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.WriteTransactionNeededException;

import java.util.List;
import java.util.Map;

/**
 * User: dbashmakov
 * Date: 19.03.13
 * Time: 11:52
 */
public interface DocumentFrequencyAnalysisService {

    String DJ_NAMESPACE_URI = "http://www.it.ru/logicECM/document/frequency/1.0";

    String DFA_ROOT_ID = "DFA_ROOT_ID";
    String DFA_ROOT_NAME = "Частотный анализ";
    QName TYPE_FREQUENCY_UNIT = QName.createQName(DJ_NAMESPACE_URI, "unit");
    QName ASSOC_UNIT_EMPLOYEE = QName.createQName(DJ_NAMESPACE_URI, "employee-assoc");
    QName PROP_UNIT_COUNT = QName.createQName(DJ_NAMESPACE_URI, "unit-count");
    QName PROP_UNIT_DOC_TYPE = QName.createQName(DJ_NAMESPACE_URI, "unit-doc-type");
    QName PROP_UNIT_ACTION_ID = QName.createQName(DJ_NAMESPACE_URI, "unit-actionId");

    Long getFrequencyCount(NodeRef employee, String docType, String actionId);

    Map<String, Long> getFrequenciesCountsByDocType(NodeRef employee, String docType);

    void updateFrequencyCount(NodeRef employee, String docType, String actionId);

    NodeRef getFrequencyUnit(NodeRef employee, String docType, String actionId);

    List<NodeRef> getFrequencyUnits(NodeRef employee, String docType);

	NodeRef createDocTypeFolder(NodeRef employee) throws WriteTransactionNeededException;

	public NodeRef createFrequencyUnit(final NodeRef employee, final String docType, final String actionId) throws WriteTransactionNeededException;

	NodeRef getWorkDirectory(final NodeRef employee);

	NodeRef createWorkDirectory(NodeRef employee, String docType) throws WriteTransactionNeededException;

}
