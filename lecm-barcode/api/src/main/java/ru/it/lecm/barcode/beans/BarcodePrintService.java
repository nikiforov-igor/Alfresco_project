package ru.it.lecm.barcode.beans;

import java.util.List;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 *
 * @author vlevin
 */
public interface BarcodePrintService {

	NodeRef getBarcodeSettingsByDocumentType(QName documentType);

	NodeRef getEmployeeToPrinterMapperByEmployee(NodeRef employeeNode);

	String getPrintCommandByDocumentType(QName documentType);

	String getPrinterNameByEmployee(NodeRef employeeRef);

	String getReportNameByDocumentType(QName documentType);

	boolean isEmployeeUsesPostScript(NodeRef employeeNode);

	void print(NodeRef documentRef, List<String> additionalStrings);

	void printSync(NodeRef documentRef, List<String> additionalStrings);

	void printSync(NodeRef documentRef, List<String> additionalStrings, NodeRef employeeNode);

	boolean isBarcodeEnabled();

	String getPrintMode();

}
