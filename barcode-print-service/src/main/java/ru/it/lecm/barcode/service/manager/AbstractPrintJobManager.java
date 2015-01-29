package ru.it.lecm.barcode.service.manager;

import java.io.InputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import javax.print.Doc;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.PrinterResolution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.barcode.service.entity.PrintJob;
import ru.it.lecm.barcode.service.service.BarcodePrintJobListener;

/**
 *
 * @author vmalygin
 */
public abstract class AbstractPrintJobManager implements PrintJobManager {

	private final static Logger logger = LoggerFactory.getLogger(AbstractPrintJobManager.class);

	private final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault());

	protected final PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet(getPrintJobName());
	protected final PrintJob printJob;
	protected final DocPrintJob docPrintJob;
	protected final PrintService printer;
	protected InputStream dataInputStream;
	protected Doc doc;

	public AbstractPrintJobManager(final PrintJob printJob, final PrintService printer) {
		this.printJob = printJob;
		this.printer = printer;

		docPrintJob = printer.createPrintJob();
		docPrintJob.addPrintJobListener(new BarcodePrintJobListener());
	}

	private JobName getPrintJobName() {
		String jobName = String.format("Alfresco barcode job [%s]", dateFormat.format(new Date()));
		return new JobName(jobName, Locale.getDefault());
	}

	protected PrinterResolution getPrinterResolution(PrintService printer) {
		PrinterResolution[] supportedResolutions = (PrinterResolution[]) printer.getSupportedAttributeValues(javax.print.attribute.standard.PrinterResolution.class, null, null);

		PrinterResolution printerResolution = supportedResolutions[0];

		if (logger.isDebugEnabled()) {
			logger.debug("Resolutions");
			for (PrinterResolution supportedResolution : supportedResolutions) {
				int[] resolution = supportedResolution.getResolution(PrinterResolution.DPI);
				logger.debug(resolution[0] + "x" + resolution[1]);
			}
		}

		logger.info("Got resolution {}", printerResolution);
		return printerResolution;
	}

	@Override
	public Doc getDoc() {
		return doc;
	}

	@Override
	public PrintRequestAttributeSet getPrintRequestAttributeSet() {
		return printRequestAttributeSet;
	}

	@Override
	public void print() throws PrintException {
		docPrintJob.print(doc, printRequestAttributeSet);
	}
}
