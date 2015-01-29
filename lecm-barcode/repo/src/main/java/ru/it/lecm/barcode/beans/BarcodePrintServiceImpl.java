package ru.it.lecm.barcode.beans;

import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.validator.routines.UrlValidator;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.barcode.BarcodeModel;
import ru.it.lecm.barcode.entity.PrintJob;
import ru.it.lecm.barcode.entity.PrintResult;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.reports.api.ReportsManager;
import ru.it.lecm.reports.api.model.ReportFileData;

/**
 *
 * @author vlevin
 */
public class BarcodePrintServiceImpl extends BaseBean implements BarcodePrintService {

	private final static Logger logger = LoggerFactory.getLogger(BarcodePrintServiceImpl.class);

	private final static String PRINT_COMMANDS_DIC_NAME = "Команды печати штрихкодов";
	private final static String DEFAULT_PRINT_SERVICE_URL = "http://localhost:8080/barcode-print-service";
	private final static String DEFAULT_REPORT_RENDER_MODE = "REMOTE";

	private String printServiceURL = DEFAULT_PRINT_SERVICE_URL;
	private NodeRef printCommandsDic = null;
	private String reportRenderMode;
	private boolean barcodeEnabled;

	private OrgstructureBean orgstructureService;
	private NamespaceService namespaceService;
	private DictionaryBean dictionaryService;
	private DocumentService documentService;
	private DictionaryService alfrescoDictionaryService;
	private ReportsManager reportsManager;
	private SubstitudeBean substitudeService;
	private ThreadPoolExecutor threadPoolExecutor;

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	public void setDictionaryService(DictionaryBean dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	public void setAlfrescoDictionaryService(DictionaryService alfrescoDictionaryService) {
		this.alfrescoDictionaryService = alfrescoDictionaryService;
	}

	public void setReportsManager(ReportsManager reportsManager) {
		this.reportsManager = reportsManager;
	}

	public void setSubstitudeService(SubstitudeBean substitudeService) {
		this.substitudeService = substitudeService;
	}

	public void setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor) {
		this.threadPoolExecutor = threadPoolExecutor;
	}

	public void setPrintServiceURL(String printServiceURL) {
		UrlValidator urlValidator = new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS | UrlValidator.ALLOW_2_SLASHES);
		if (urlValidator.isValid(printServiceURL)) {
			this.printServiceURL = printServiceURL;
		} else {
			logger.warn("No barcode print service URL specified in alfresco-global.properties. Using default URL {}", DEFAULT_PRINT_SERVICE_URL);
		}
	}

	public void setReportRenderMode(String reportRenderMode) {
		boolean validValue = reportRenderMode.equalsIgnoreCase("REMOTE") || reportRenderMode.equalsIgnoreCase("LOCAL");
		this.reportRenderMode = validValue ? reportRenderMode : DEFAULT_REPORT_RENDER_MODE;
	}

	public void setBarcodeEnabled(boolean barcodeEnabled) {
		this.barcodeEnabled = barcodeEnabled;
	}

	@Override
	public boolean isBarcodeEnabled() {
		return barcodeEnabled;
	}

	public void init() {
		PropertyCheck.mandatory(this, "orgstructureService", orgstructureService);
		PropertyCheck.mandatory(this, "namespaceService", namespaceService);
		PropertyCheck.mandatory(this, "dictionaryService", dictionaryService);
		PropertyCheck.mandatory(this, "documentService", documentService);
		PropertyCheck.mandatory(this, "alfrescoDictionaryService", alfrescoDictionaryService);
		PropertyCheck.mandatory(this, "reportsManager", reportsManager);
		PropertyCheck.mandatory(this, "substitudeService", substitudeService);
		PropertyCheck.mandatory(this, "transactionService", transactionService);
		PropertyCheck.mandatory(this, "threadPoolExecutor", threadPoolExecutor);
	}

	@Override
	public void print(NodeRef documentRef, List<String> additionalStrings) {
		if (barcodeEnabled) {
			BarcodePrintServiceRunner runner = new BarcodePrintServiceRunner(documentRef, additionalStrings, transactionService, this, logger);
			threadPoolExecutor.execute(runner);
		} else {
			logger.info("Barcode service is disabled");
		}
	}

	@Override
	public void printSync(NodeRef documentRef, List<String> additionalStrings) {
		if (!barcodeEnabled) {
			logger.info("Barcode service is disabled");
			return;
		}

		NodeRef currentEmployee = orgstructureService.getCurrentEmployee();
		printSync(documentRef, additionalStrings, currentEmployee);
	}

	@Override
	public void printSync(NodeRef documentRef, List<String> additionalStrings, NodeRef employeeNode) {
		if (!barcodeEnabled) {
			logger.info("Barcode service is disabled");
			return;
		}

		String printerName = getPrinterNameByEmployee(employeeNode);
		String printCommand;

		if (printerName == null) {
			logger.warn("No printer associated with employee {}", employeeNode);
			return;
		}

		QName documentType = nodeService.getType(documentRef);

		boolean employeeUsesPostScript = isEmployeeUsesPostScript(employeeNode);

		Long documentDBId = (Long) nodeService.getProperty(documentRef, ContentModel.PROP_NODE_DBID);

		PrintJob job = new PrintJob();
		job.setPrinterName(printerName);
		job.setUsesPostScript(employeeUsesPostScript);

		if (employeeUsesPostScript) {
			String reportName = getReportNameByDocumentType(documentType);

			switch (reportRenderMode) {
				case "LOCAL":
					printCommand = getRenderedReportString(documentRef, reportName);
					break;
				case "REMOTE":
					printCommand = getDatasourceForRemoteReport(documentRef, reportName, documentDBId);
					break;
				default:
					throw new IllegalStateException("Unknown reportRenderMode " + reportRenderMode);
			}

			Object[] logMessageParams = {documentRef, printServiceURL, printerName, reportName};
			logger.debug("Printing barcode\nDocumentNodeRef: {}\nPrinserver URL: {}\nPrinter name: {}\nReport name:\n{}", logMessageParams);
		} else {
			String printCommandFormat = getPrintCommandByDocumentType(documentType);

			if (printCommandFormat == null) {
				logger.warn("No print command for type {}", documentType.toPrefixString(namespaceService));
				return;
			}

			List<Object> commandArguments = new ArrayList<>();
			commandArguments.add(String.format("%019d", documentDBId));

			if (additionalStrings != null) {
				commandArguments.addAll(additionalStrings);
			}

			MessageFormat formatter = new MessageFormat(printCommandFormat);

			printCommand = formatter.format(commandArguments.toArray());

			Object[] logMessageParams = {documentRef, printServiceURL, printerName, printCommand};
			logger.debug("Printing barcode\nDocumentNodeRef: {}\nPrinserver URL: {}\nPrinter name: {}\nPrint command:\n{}", logMessageParams);
		}

		job.setPrintCommand(printCommand);

		PrintRESTClient client = new PrintRESTClient(printServiceURL);

		PrintResult printResult = client.print(job);

		if (!printResult.isSuccess()) {
			Object[] errLogMessageParams = {printerName, documentRef, printResult.getErrorMessage()};
			logger.error("Error printing barcode.\nPrinter name {}.\nDocument nodeRef: {}.\nEror message: {}", errLogMessageParams);
		}

	}

	// если нужно рендерить отчет на стороне Альфрески
	private String getRenderedReportString(NodeRef documentRef, String reportName) {
		String printCommand;
		Map<String, String> reportParams = new HashMap<>();
		reportParams.put("ID", documentRef.toString());

		// для режима печати jasper-print
		reportParams.put("targetFormat", "xml");

		ReportFileData barcodeReport;
		try {
			barcodeReport = reportsManager.generateReport(reportName, null, reportParams);
		} catch (IOException ex) {
			throw new AlfrescoRuntimeException("Error forming barcode report", ex);
		}

		printCommand = Base64.encodeBase64String(barcodeReport.getData());

		return printCommand;
	}

	// если рендерим отчеты на стороне принт-сервера
	private String getDatasourceForRemoteReport(NodeRef documentRef, String reportName, long documentDBId) {
		String printCommand;
		String regNum = substitudeService.formatNodeTitle(documentRef, "{~REGNUM}");
		Date regDate = (Date) nodeService.getProperty(documentRef, DocumentService.PROP_DOCUMENT_DATE);
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");

		Map<String, Object> printServiceParams = new HashMap<>();
		printServiceParams.put("reportName", reportName);

		Map<String, Object> datasource = new HashMap<>();
		datasource.put("col_id", documentDBId);
		datasource.put("col_regdate", dateFormat.format(regDate));
		datasource.put("col_regnum", regNum);

		printServiceParams.put("datasource", datasource);

		printCommand = new JSONObject(printServiceParams).toString();

		return printCommand;
	}

	@Override
	public String getPrintCommandByDocumentType(QName documentType) {
		NodeRef barcodeSettings = getBarcodeSettingsByDocumentType(documentType);

		if (barcodeSettings == null) {
			return null;
		}

		String printCommand = (String) nodeService.getProperty(barcodeSettings, BarcodeModel.PROP_PRINT_COMMAND);

		return printCommand;
	}

	@Override
	public String getReportNameByDocumentType(QName documentType) {
		NodeRef barcodeSettings = getBarcodeSettingsByDocumentType(documentType);

		if (barcodeSettings == null) {
			return null;
		}

		String reportName = (String) nodeService.getProperty(barcodeSettings, BarcodeModel.PROP_REPORT_NAME);

		return reportName;
	}

	@Override
	public NodeRef getBarcodeSettingsByDocumentType(QName documentType) {
		if (printCommandsDic == null) {
			printCommandsDic = dictionaryService.getDictionaryByName(PRINT_COMMANDS_DIC_NAME);
		}

		List<NodeRef> dictionaryRecords = dictionaryService.getChildren(printCommandsDic);
		NodeRef matchedDictionaryRecord = null;

		for (NodeRef dictionaryRecord : dictionaryRecords) {
			String dictionaryDocTypeStr = (String) nodeService.getProperty(dictionaryRecord, BarcodeModel.PROP_DOCUMENT_TYPE);
			QName dictionaryDocType = QName.resolveToQName(namespaceService, dictionaryDocTypeStr);

			if (alfrescoDictionaryService.isSubClass(documentType, dictionaryDocType)) {
				matchedDictionaryRecord = dictionaryRecord;
				break;
			}
		}

		return matchedDictionaryRecord;
	}

	@Override
	public String getPrinterNameByEmployee(NodeRef employeeRef) {
		String result;

		NodeRef employeeToPrinterRef = getEmployeeToPrinterMapperByEmployee(employeeRef);

		if (employeeToPrinterRef == null) {
			return null;
		}

		result = (String) nodeService.getProperty(employeeToPrinterRef, BarcodeModel.PROP_PRINTER_NAME);

		return result;
	}

	@Override
	public NodeRef getEmployeeToPrinterMapperByEmployee(NodeRef employeeNode) {
		NodeRef employeeToPrinterRef = null;

		List<NodeRef> employeeToPrinterRefs = findNodesByAssociationRef(employeeNode, BarcodeModel.ASSOC_EMPLOYEE, BarcodeModel.TYPE_EMPLOYEE_TO_PRINTER_DICTIONARY, ASSOCIATION_TYPE.SOURCE);

		for (NodeRef node : employeeToPrinterRefs) {
			if (!isArchive(node)) {
				employeeToPrinterRef = node;
				break;
			}
		}

		return employeeToPrinterRef;
	}

	@Override
	public boolean isEmployeeUsesPostScript(NodeRef employeeNode) {
		NodeRef employeeToPrinterRef = getEmployeeToPrinterMapperByEmployee(employeeNode);

		if (employeeToPrinterRef == null) {
			return false;
		}

		return (boolean) nodeService.getProperty(employeeToPrinterRef, BarcodeModel.PROP_USE_POSTSCRIPT);
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}

}
