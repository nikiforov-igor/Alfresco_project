package ru.it.lecm.orgstructure.exportimport;

import org.alfresco.service.namespace.QName;

/**
 *
 * @author vlevin
 */
public final class ExportImportModel {

	public final static String ORGSTRUCTURE_EXPORT_IMPORT_NAMESPACE = "http://www.it.ru/logicECM/orgstructure/export-import/1.0";
	public final static QName ASPECT_ID = QName.createQName(ORGSTRUCTURE_EXPORT_IMPORT_NAMESPACE, "idAspect");
	public final static QName PROP_ID = QName.createQName(ORGSTRUCTURE_EXPORT_IMPORT_NAMESPACE, "id");

	private ExportImportModel() throws IllegalAccessException {
		throw new IllegalAccessException("You cannot create any instance of ExportImportModel class.");
	}

}
