/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.secretary;

import java.util.List;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 *
 * @author ikhalikov
 */
public interface SecretaryService {

	String SECRETARY_NAMESPACE = "http://www.it.ru/lecm/secretary/aspects/1.0";

	QName ASPECT_IS_SECRETARY = QName.createQName(SECRETARY_NAMESPACE, "is-secretary");
	QName ASSOC_CHIEF_ASSOC = QName.createQName(SECRETARY_NAMESPACE, "chief-assoc");
	QName ASSOC_CAN_RECEIVE_TASKS_FROM_CHIEFS = QName.createQName(SECRETARY_NAMESPACE, "can-receive-tasks-from-chiefs");
	QName PROP_CAN_RECEIVE_TASKS = QName.createQName(SECRETARY_NAMESPACE, "can-receive-tasks");
	QName PROP_SECRETARY_ASSOC_REF = QName.createQName(SECRETARY_NAMESPACE, "secretary-assoc-ref");
	QName PROP_SECRETARY_ASSOC_REF_TEXT_CONTENT = QName.createQName(SECRETARY_NAMESPACE, "secretary-assoc-ref-text-content");
	QName PROP_CHIEF_ASSOC_REF = QName.createQName(SECRETARY_NAMESPACE, "chief-assoc-ref");
	QName PROP_CHIEF_ASSOC_REF_TEXT_CONTENT = QName.createQName(SECRETARY_NAMESPACE, "chief-assoc-ref-text-content");

	List<NodeRef> getChiefs(NodeRef secretaryRef);

	List<NodeRef> getPrimaryChiefs(NodeRef secretaryRef);

	List<NodeRef> employeesToStaff(List<NodeRef> employees);

	List<NodeRef> staffToEmployees(List<NodeRef> staff);

	boolean isSecretary(final NodeRef employeeRef);

	boolean isChief(final NodeRef employeeRef);

	List<NodeRef> getSecretaries(final NodeRef chiefRef);

	boolean removeSecretary(final NodeRef chiefRef, final NodeRef secretaryRef);

	void removeSecretaries(final NodeRef chiefRef);

	/**
	 * получение основного секретаря по следующему сценарию
	 * 1) если у chief нет секретарей, то возвращается chief
	 * 3) если у chief есть секретари, но нет ни одного основного, то возвращается chief
	 * 4) если у chief есть секретари и один из них является основным, то возвращается основной секретарь
	 * @param chief "руководитель" для которого ищется основной секретарь
	 * @return основной секретарь, в случае его отсутствия - employee
	 */
	NodeRef getEffectiveEmployee(final NodeRef chief);

	NodeRef getTasksSecretary(NodeRef chief);
}
