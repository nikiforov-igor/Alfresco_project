package ru.it.lecm.barcode;

import org.alfresco.service.namespace.QName;

/**
 *
 * @author vlevin
 */
public final class BarcodeModel {

	public final static String BARCODE_MODEL_URL = "http://www.it.ru/logicECM/barcode/1.0";

	public final static QName TYPE_EMPLOYEE_TO_PRINTER_DICTIONARY = QName.createQName(BARCODE_MODEL_URL, "employeeToPrinterDictionary");
	public final static QName PROP_PRINTER_NAME = QName.createQName(BARCODE_MODEL_URL, "employeeToPrinterDictionaryPrinterName");
	public final static QName PROP_USE_POSTSCRIPT = QName.createQName(BARCODE_MODEL_URL, "employeeToPrinterDictionaryUsePostScript");
	public final static QName ASSOC_EMPLOYEE = QName.createQName(BARCODE_MODEL_URL, "employeeToPrinterDictionaryEmployeeAssoc");

	public final static QName TYPE_PRINT_COMMAND_DICTIONARY = QName.createQName(BARCODE_MODEL_URL, "printCommandDictionary");
	public final static QName PROP_DOCUMENT_TYPE = QName.createQName(BARCODE_MODEL_URL, "printCommandDictionaryDocumentType");
	public final static QName PROP_PRINT_COMMAND = QName.createQName(BARCODE_MODEL_URL, "printCommandDictionaryPrintCommand");
	public final static QName PROP_REPORT_NAME = QName.createQName(BARCODE_MODEL_URL, "printCommandDictionaryReportName");

	private BarcodeModel() {
		throw new IllegalStateException("Class BarcodeModel can not be instantiated");
	}

}
