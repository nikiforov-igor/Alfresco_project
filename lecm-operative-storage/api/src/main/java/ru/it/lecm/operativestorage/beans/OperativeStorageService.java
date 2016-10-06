/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.operativestorage.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import java.util.List;

/**
 *
 * @author ikhalikov
 */
public interface OperativeStorageService {

	String OS_NAMESPACE = "http://www.it.ru/logicECM/operative-storage/1.0";
	String OS_SETTINGS_NAMESPACE = "http://www.it.ru/logicECM/model/os/global-settings/1.0";
	String OS_ASPECTS_NAMESPACE = "http://www.it.ru/logicECM/model/os/aspects/1.0";

	QName ASSOC_NOMENCLATURE_CASE = QName.createQName(OS_NAMESPACE, "nomenclature-case-assoc");
	QName ASSOC_NOMENCLATURE_CASE_ROOT = QName.createQName(OS_NAMESPACE, "nomenclature-case-root-assoc");
	QName ASSOC_NOMENCLATURE_CASE_VISIBILITY_EMPLOYEE = QName.createQName(OS_NAMESPACE, "nomenclature-case-visibility-employee-assoc");
	QName ASSOC_NOMENCLATURE_CASE_VISIBILITY_UNIT = QName.createQName(OS_NAMESPACE, "nomenclature-case-visibility-unit-assoc");
	QName ASSOC_NOMENCLATURE_CASE_VISIBILITY_WORKGROUP = QName.createQName(OS_NAMESPACE, "nomenclature-case-visibility-workgroup-assoc");
	QName ASSOC_NOMENCLATURE_CASE_VISIBILITY_WORK_GROUP = QName.createQName(OS_NAMESPACE, "nomenclature-case-visibility-workgroup-assoc");
	QName ASSOC_NOMENCLATURE_CASE_VOLUME = QName.createQName(OS_NAMESPACE, "nomenclature-case-volume-child-assoc");
	QName ASSOC_NOMENCLATURE_CASE_YEAR = QName.createQName(OS_NAMESPACE, "nomenclature-case-year-assoc");
	QName ASSOC_NOMENCLATURE_UNIT_TO_ORGUNIT = QName.createQName(OS_NAMESPACE, "nomenclature-unit-section-unit-assoc");


	String BR_ARCHIVIST = "DA_ARCHIVISTS";
	String CONSTRAINT_NOMENCLATURE_CASE_STATUS_OPEN = "OPEN";
	String DEFAULT_GRANTED_ROLE = "LECM_BASIC_PG_Reader";
	String EDIT_ROLE = "LECM_BASIC_PG_Reviewer";
	String OPERATIVE_STORAGE_FOLDER_ID = "OS_NOMENCLATURE_FOLDER_ID";
	String NOMENCLATURE_DOCS_FOLDER_NAME = "Документы";
	String NOMENCLATURE_FOLDER_NAME = "Номенклатура дел";
	String NOMENCLATURE_REFERENCES_FOLDER_NAME = "Справки";
	String NOMENCLATURE_REFERENCE_TEMPLATE_NAME = "СПРАВКА-Заместитель.docx";
	String OPERATIVE_STORAGE_GLOBAL_SETTING_NAME = "Глобальные настройки оперативного хранения";

	QName PROP_IN_CASE = QName.createQName(OS_NAMESPACE, "in-case");
	QName PROP_NOMENCLATURE_CASE_INDEX = QName.createQName(OS_NAMESPACE, "nomenclature-case-index");
	QName PROP_NOMENCLATURE_CASE_STATUS = QName.createQName(OS_NAMESPACE, "nomenclature-case-status");
	QName PROP_NOMENCLATURE_CASE_IS_SHARED = QName.createQName(OS_NAMESPACE, "shared");
	QName PROP_NOMENCLATURE_UNIT_SECTION_INDEX = QName.createQName(OS_NAMESPACE, "nomenclature-unit-section-index");
	QName PROP_NOMENCLATURE_UNIT_SECTION_COMMENT = QName.createQName(OS_NAMESPACE, "nomenclature-unit-section-comment");
	QName PROP_NOMENCLATURE_UNIT_SECTION_STATUS = QName.createQName(OS_NAMESPACE, "nomenclature-unit-section-status");
	QName PROP_NOMENCLATURE_VOLUMES_NUMBER = QName.createQName(OS_NAMESPACE, "nomenclature-case-volumes-number");
	QName PROP_NOMENCLATURE_YEAR_SECTION_YEAR = QName.createQName(OS_NAMESPACE, "nomenclature-year-section-year");
	QName PROP_NOMENCLATURE_YEAR_SECTION_STATUS = QName.createQName(OS_NAMESPACE, "nomenclature-year-section-status");
	QName PROP_NOMENCLATURE_CASE_YEAR_STATUS = QName.createQName(OS_NAMESPACE, "nomenclature-case-year-section-status");
	QName PROP_NOMENCLATURE_CASE_YEAR_COMMENT = QName.createQName(OS_NAMESPACE, "nomenclature-year-section-comment");
	QName PROP_NOMENCLATURE_CASE_CREATION_DATE = QName.createQName(OS_NAMESPACE, "nomenclature-case-creation-date");
	QName PROP_NOMENCLATURE_CASE_CLOSE_DATE = QName.createQName(OS_NAMESPACE, "nomenclature-case-close-date");
	QName PROP_NOMENCLATURE_CASE_TRANSIENT = QName.createQName(OS_NAMESPACE, "nomenclature-case-transient");
	QName PROP_NO_PERM_CHANGE = QName.createQName(OS_NAMESPACE, "nomenclature-case-no-permissions-change-on-archivation");
	QName TYPE_BASE_OS = QName.createQName(OS_NAMESPACE, "base");
	QName TYPE_NOMENCLATURE_CASE = QName.createQName(OS_NAMESPACE, "nomenclature-case");
	QName TYPE_NOMENCLATURE_CASE_VOLUME = QName.createQName(OS_NAMESPACE, "nomenclature-case-volume");
	QName TYPE_NOMENCLATURE_UNIT_SECTION = QName.createQName(OS_NAMESPACE, "nomenclature-unit-section");
	QName TYPE_NOMENCLATURE_YEAR_SECTION = QName.createQName(OS_NAMESPACE, "nomenclature-year-section");
	QName TYPE_NOMENCLATURE_VOLUME = QName.createQName(OS_NAMESPACE, "nomenclature-case-volume");
	QName PROP_NOMENCLATURE_COMMON_INDEX = QName.createQName(OS_ASPECTS_NAMESPACE, "common-index");

	QName TYPE_OPERATIVE_STORAGE_SETTING = QName.createQName(OS_SETTINGS_NAMESPACE, "settings");
	QName PROP_OPERATIVE_STORAGE_CENRALIZED = QName.createQName(OS_SETTINGS_NAMESPACE, "centralized");

	QName ASPECT_MOVE_TO_CASE = QName.createQName(OS_ASPECTS_NAMESPACE, "move-to-case");

	QName ASSOC_NOMENCLATURE_LINKED_ORG = QName.createQName(OS_ASPECTS_NAMESPACE, "nomenclature-organization-assoc");

	void cleanVisibilityList(NodeRef nodeRef);

	NodeRef createDocsFolder(NodeRef caseNodeRef);

	NodeRef createReferencesFolder(NodeRef caseNodeRef);

	NodeRef getDocuemntsFolder(NodeRef caseNodeRef);

	NodeRef getNomenclatureFolder();

	NodeRef getOperativeStorageFolder();

	NodeRef getReferenceTemplate(NodeRef caseNodeRef);

	NodeRef getReferencesFolder(NodeRef caseNodeRef);

	NodeRef getServiceRootFolder();

	NodeRef getYearSection(NodeRef nodeRef);

	void grantAll(NodeRef nodeRef);

	void grantPermToEmployee(NodeRef nodeRef, NodeRef employee);

	void grantPermToUnit(NodeRef nodeRef, NodeRef unit, boolean isShared);

	void grantPermToWG(NodeRef nodeRef, NodeRef group);

	void grantPermissionsToAllArchivists(NodeRef nodeRef);

	void moveDocToNomenclatureCase(NodeRef docNodeRef, NodeRef caseNodeRef);

	void moveDocToNomenclatureCase(NodeRef docNodeRef);

	void revokeAll(NodeRef nodeRef);

	void revokePermFromEmployee(NodeRef nodeRef, NodeRef employee);

	void revokePermFromUnit(NodeRef nodeRef, NodeRef unit);

	void revokePermFromUnit(NodeRef nodeRef, NodeRef unit, boolean isShared);

	void revokePermFromWG(NodeRef nodeRef, NodeRef group);

	void updatePermissions(NodeRef nodeRef);

	NodeRef getSettings();

	boolean orgUnitAssociationExists(NodeRef nodeRef, NodeRef orgUnitRef);

	public boolean checkNDSectionAssociationExists(NodeRef orgUnitRef, NodeRef ndSectionRef);

	public List<NodeRef> getOrganizationsYearSections(NodeRef organizationRef);

	void createTreeByOrgUnits(NodeRef yearSectionRef);

	public void createSectionByUnit(NodeRef unitRef, NodeRef root, boolean fuckingDeepCopy);

	public void grantPermissionToArchivist(NodeRef docNodeRef);

	public boolean caseHasDocumentsVolumes(NodeRef caseRef);
	public boolean caseHasDocumentsVolumes(NodeRef caseRef, boolean checkVolumes);

	public boolean canCopyUnits(List<NodeRef> units, NodeRef dest);

	public boolean isCetralized();

	public void removeYearSection(NodeRef yearSection);

	public void removeUnitSection(NodeRef unitSection);

	public void removeCase(NodeRef caseRef);

	public void sendToArchiveAction(NodeRef caseRef);

}
