package ru.it.lecm.barcode.service.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.print.DocFlavor;
import javax.print.PrintException;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.PrinterName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.it.lecm.barcode.service.entity.PrintJob;
import ru.it.lecm.barcode.service.entity.PrintResult;
import ru.it.lecm.barcode.service.entity.PrinterProperties;
import ru.it.lecm.barcode.service.manager.PostscriptPrintJobManager;
import ru.it.lecm.barcode.service.manager.PrintJobManager;
import ru.it.lecm.barcode.service.manager.RawPrintJobManager;

/**
 *
 * @author vlevin
 */
@Service("PrintService")
public class PrintService {

	private final static Logger logger = LoggerFactory.getLogger(PrintService.class);

	@Value("${pdftops.exe}")
	private String pdfdtops;

	@Value("${conversion.mode}")
	private String conversionMode;

	@Value("${postscript.level}")
	private String postscriptLevel;

	public PrintResult print(PrintJob printJob) throws IOException {
		logger.debug("PrinterName: {}\nisUsesPostScript: {}", printJob.getPrinterName(), printJob.isUsesPostScript());

		PrintResult result = new PrintResult();
		PrintJobManager printJobManager = null;

		try {
			javax.print.PrintService printer = findPrinterByName(printJob.getPrinterName());

			if (printer == null) {
				throw new IllegalStateException("Can not find printer with name " + printJob.getPrinterName());
			}

			if (logger.isDebugEnabled()) {
				logger.debug("Supported doc flavors");
				DocFlavor[] flavors = printer.getSupportedDocFlavors();
				for (DocFlavor flavor : flavors) {
					logger.debug(flavor.toString());
				}
			}

			if (logger.isDebugEnabled()) {
				logger.debug("supported attribute categories");
				Class<?>[] categories = printer.getSupportedAttributeCategories();
				for (Class<?> category : categories) {
					logger.debug(category.getName());
				}
			}

			if (printJob.isUsesPostScript()) {
				printJobManager = new PostscriptPrintJobManager(printJob, printer, pdfdtops, conversionMode, postscriptLevel);
			} else {
				printJobManager = new RawPrintJobManager(printJob, printer);
			}

			printJobManager.prepareData();

			logger.info("Sending print job to printer ");
			printJobManager.print();

			result.setSuccess(true);
		} catch (RuntimeException | PrintException ex) {
			logger.error(ex.getMessage(), ex);
			result.setSuccess(false);
			result.setErrorMessage(ex.getMessage());
		} finally {
			if (printJobManager != null) {
				printJobManager.closeAll();
			}
		}
		return result;
	}

	public javax.print.PrintService findPrinterByName(String printerName) {
		PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
		printServiceAttributeSet.add(new PrinterName(printerName, null));
		javax.print.PrintService[] services = PrintServiceLookup.lookupPrintServices(null, printServiceAttributeSet);
		javax.print.PrintService printService;
		if (services == null || services.length == 0) {
			logger.error("Can not find printer with name '" + printerName + "'");
			return null;
		}
		printService = services[0];

		return printService;
	}

	public boolean isPrinterAvailable(String printerName) {
		return findPrinterByName(printerName) != null;
	}

	public List<PrinterProperties> listPrinters() {
		List<PrinterProperties> printersList = new ArrayList<>();

		javax.print.PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
		for (javax.print.PrintService printService : printServices) {
			PrinterProperties printerProperties = new PrinterProperties();
			printerProperties.setName(printService.getName());
			printersList.add(printerProperties);
		}

		return printersList;
	}
}
