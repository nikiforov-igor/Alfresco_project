/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.deputy;

import java.util.List;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 *
 * @author ikhalikov
 */
public interface DeputyService {

	public final static String DEPUTY_FOLDER = "DEPUTY_FOLDER";
	public final static String DEPUTY_SETTINGS_FOLDER = "DEPUTY_SETTINGS_FOLDER";


	public final static String DEPUTY_NAMESPACE = "http://www.it.ru/lecm/deputy/1.0";
	public final static QName TYPE_DEPUTY_SETTINGS = QName.createQName(DEPUTY_NAMESPACE, "deputy-settings");
	public final static QName TYPE_DEPUTY_NODE = QName.createQName(DEPUTY_NAMESPACE, "deputy");
	public final static QName ASSOC_DEPUTY_EMPLOYEE = QName.createQName(DEPUTY_NAMESPACE, "employee-assoc");
	public final static QName ASSOC_DEPUTY_SUBJECT = QName.createQName(DEPUTY_NAMESPACE, "subject-assoc");
	public final static QName ASSOC_EMPLOYEE_TO_DEPUTY = QName.createQName(DEPUTY_NAMESPACE, "deputy-assoc");
	public final static QName ASSOC_SETTINGS_DICTIONARY = QName.createQName(DEPUTY_NAMESPACE, "dictionary-assoc");
	public final static QName PROP_DEPUTY_REF = QName.createQName(DEPUTY_NAMESPACE, "deputy-assoc-ref");
	public final static QName PROP_DEPUTY_TEXT_CONTENT = QName.createQName(DEPUTY_NAMESPACE, "deputy-assoc-text-content");
	public final static QName PROP_DEPUTY_COMPLETE = QName.createQName(DEPUTY_NAMESPACE, "complete-deputy-flag");
	public final static QName PROP_DEPUTY_EMPLOYEE_REF = QName.createQName(DEPUTY_NAMESPACE, "employee-assoc-ref");
	public final static QName PROP_DEPUTY_EMPLOYEE_TEXT_CONTENT = QName.createQName(DEPUTY_NAMESPACE, "employee-assoc-text-content");
	public final static QName PROP_CHIEF_REF = QName.createQName(DEPUTY_NAMESPACE, "chief-assoc-ref");
	public final static QName PROP_CHIEF_TEXT_CONTENT = QName.createQName(DEPUTY_NAMESPACE, "chief-assoc-text-content");	

	public NodeRef getDeputyFolder();

	public NodeRef getDeputySettingsFolder();

	public NodeRef getDeputySettingsNode();

	public NodeRef createDeputySettingsNode();

	public NodeRef createDeputy(NodeRef chiefNodeRef, NodeRef deputyNodeRef, List<NodeRef> subjects);

	public NodeRef createFullDeputy(NodeRef chiefNodeRef, NodeRef deputyNodeRef);

	public void removeDeputy(NodeRef deputyRef);

	public void removeFullDeputy(NodeRef chiefRef, NodeRef deputyRef);

	public List<NodeRef> getPrimaryChiefs(NodeRef deputyEmployeeRef);

	public List<NodeRef> getAllChiefs(NodeRef deputyEmployeeRef);

	List<NodeRef> employeesToStaff(List<NodeRef> employees);

	boolean isDeputyAcceptable(NodeRef docNodeRef, NodeRef deputyNodeRef);

	public NodeRef getDeputyEmployee(NodeRef deputyNode);

	public List<NodeRef> getDeputiesByChief(NodeRef chiefNodeRef);

	public void deleteAllSubjectDeputies();

}
