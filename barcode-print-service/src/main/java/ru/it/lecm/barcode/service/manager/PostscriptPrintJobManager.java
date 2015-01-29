package ru.it.lecm.barcode.service.manager;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.print.DocFlavor;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.SimpleDoc;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PrintQuality;
import javax.print.attribute.standard.PrinterResolution;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;
import net.sf.jasperreports.engine.xml.JRPrintXmlLoader;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import ru.it.lecm.barcode.service.entity.PrintJob;

/**
 *
 * @author vmalygin
 */
public class PostscriptPrintJobManager extends AbstractPrintJobManager {

	private final static Logger logger = LoggerFactory.getLogger(PostscriptPrintJobManager.class);
	private final static float PDF_DPI = 72f;

	private final String pdfdtops;
	private final String conversionMode;
	private final String postscriptLevel;

	private File pdfFile;
	private File psFile;
	private JRPrintServiceExporter printServiceExporter;

	public PostscriptPrintJobManager(final PrintJob printJob, final PrintService printer, final String pdfdtops, final String conversionMode, final String postscriptLevel) {
		super(printJob, printer);
		this.pdfdtops = pdfdtops;
		this.conversionMode = conversionMode;
		this.postscriptLevel = postscriptLevel;
	}

	@Override
	public void prepareData() {
		switch (conversionMode) {
			case "postscript":
				preparePostscriptData();
				break;
			case "image":
				prepareImageData();
				break;
			case "jasper-print":
				prepareJasperPrintData();
				break;
			case "jasper-local":
				prepareJasperLocalData();
				break;
			default:
				throw new IllegalArgumentException(String.format("%s is unsupported conversion mode", conversionMode));
		}

	}

	private void preparePostscriptData() {
		try {
			byte[] decodedPDF = Base64.decodeBase64(printJob.getPrintCommand());

			ByteBuffer bb = ByteBuffer.wrap(decodedPDF);
			PDFFile pdf = new PDFFile(bb);
			PDFPage page = pdf.getPage(0);
			Rectangle2D bBox = page.getBBox();
			int pageWidth = (int) bBox.getWidth();
			int pageHeigh = (int) bBox.getHeight();

			PrinterResolution printerResolution = getPrinterResolution(printer);
			printRequestAttributeSet.add(printerResolution);
			printRequestAttributeSet.add(PrintQuality.HIGH);

			pdfFile = File.createTempFile("barcode", ".pdf");
			psFile = File.createTempFile("barcode", ".ps");

			FileUtils.writeByteArrayToFile(pdfFile, decodedPDF);

			Runtime runtime = Runtime.getRuntime();
			//pdftops "./Проверка ШК-20-11-14-13-03-40.pdf" -level3 -paper match -paperw 440 -paperh 320 -nocrop -noshrink "./Проверка ШК-20-11-14-13-03-40.ps"
			String cmd[] = {
				pdfdtops,
				pdfFile.getAbsolutePath(),
				postscriptLevel,
				"-nocrop",
				"-noshrink",
				"-paper",
				"match",
				"-paperw",
				Integer.toString(Math.round(pageWidth / PDF_DPI * printerResolution.getCrossFeedResolution(PrinterResolution.DPI))),
				"-paperh",
				Integer.toString(Math.round(pageHeigh / PDF_DPI * printerResolution.getFeedResolution(PrinterResolution.DPI))),
				psFile.getAbsolutePath()
			};
			logger.debug(Arrays.toString(cmd));
			Process process = runtime.exec(cmd);
			int exitCode = process.waitFor();

			logger.debug("{} finished with exit code {}", pdfdtops, exitCode);

			dataInputStream = new FileInputStream(psFile);
			doc = new SimpleDoc(dataInputStream, DocFlavor.INPUT_STREAM.AUTOSENSE, null);

		} catch (IOException | InterruptedException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	private void prepareImageData() {
		try {
			byte[] decodedPDF = Base64.decodeBase64(printJob.getPrintCommand());

			ByteBuffer bb = ByteBuffer.wrap(decodedPDF);
			PDFFile pdf = new PDFFile(bb);
			PDFPage page = pdf.getPage(0);
			Rectangle2D bBox = page.getBBox();
			int pageWidth = (int) bBox.getWidth();
			int pageHeigh = (int) bBox.getHeight();

			PrinterResolution printerResolution = getPrinterResolution(printer);
			MediaSize ms = MediaSize.getMediaSizeForName(MediaSizeName.ISO_B9);
			printRequestAttributeSet.add(MediaSizeName.ISO_B9);
			printRequestAttributeSet.add(new MediaPrintableArea(3, 3, ms.getY(MediaSize.MM) + 10, ms.getX(MediaSize.MM) + 10, MediaSize.MM));

			printRequestAttributeSet.add(printerResolution);
			printRequestAttributeSet.add(OrientationRequested.PORTRAIT);
			printRequestAttributeSet.add(PrintQuality.HIGH);

			int imageWidth = Math.round(pageWidth / PDF_DPI * printerResolution.getCrossFeedResolution(PrinterResolution.DPI));
			int imageHeight = Math.round(pageHeigh / PDF_DPI * printerResolution.getFeedResolution(PrinterResolution.DPI));

			Rectangle rect = new Rectangle(0, 0, pageWidth, pageHeigh);
			BufferedImage bufferedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);

			Image image = page.getImage(imageWidth, imageHeight, rect, null, true, true);
			Graphics2D bufImageGraphics = bufferedImage.createGraphics();
			bufImageGraphics.drawImage(image, 0, 0, null);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(bufferedImage, "png", baos);
			baos.flush();

			doc = new SimpleDoc(baos.toByteArray(), DocFlavor.BYTE_ARRAY.PNG, null);
			IOUtils.closeQuietly(baos);
		} catch (IOException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	@Override
	public void closeAll() {
		IOUtils.closeQuietly(dataInputStream);
		//проверить что временные файлики существуют и удалить их
		if (pdfFile != null && pdfFile.exists()) {
			if (!FileUtils.deleteQuietly(pdfFile)) {
				logger.warn("Can't delete file {}", pdfFile.getAbsolutePath());
			}
		}
		if (psFile != null && psFile.exists()) {
			if (!FileUtils.deleteQuietly(psFile)) {
				logger.warn("Can't delete file {}", psFile.getAbsolutePath());
			}
		}
	}

	private void prepareJasperPrintData() {
		try {
			byte[] decodedXML = Base64.decodeBase64(printJob.getPrintCommand());
			dataInputStream = new ByteArrayInputStream(decodedXML);
			JasperPrint jasperPrint = JRPrintXmlLoader.load(dataInputStream);

			preparePrintParametersForJasper();

			printServiceExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		} catch (JRException ex) {
			throw new RuntimeException(ex);
		}
	}

	private void preparePrintParametersForJasper() {
		PrinterResolution printerResolution = getPrinterResolution(printer);
//		MediaSize ms = MediaSize.getMediaSizeForName(MediaSizeName.ISO_B9);
//		printRequestAttributeSet.add(MediaSizeName.ISO_B9);
//		printRequestAttributeSet.add(new MediaPrintableArea(3, 3, ms.getY(MediaSize.MM) + 10, ms.getX(MediaSize.MM) + 10, MediaSize.MM));

		printRequestAttributeSet.add(printerResolution);
		printRequestAttributeSet.add(OrientationRequested.PORTRAIT);
		printRequestAttributeSet.add(PrintQuality.HIGH);

		printServiceExporter = new JRPrintServiceExporter();
		printServiceExporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE, printer);
		printServiceExporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, printRequestAttributeSet);

	}

	@Override
	public void print() throws PrintException {
		if (conversionMode.equalsIgnoreCase("jasper-print") | conversionMode.equalsIgnoreCase("jasper-local")) {
			try {
				printServiceExporter.exportReport();
			} catch (JRException ex) {
				throw new PrintException(ex);
			}
		} else {
			super.print();
		}
	}

	private void prepareJasperLocalData() {
		try {
			JSONObject json = new JSONObject(printJob.getPrintCommand());

			String reportName = json.getString("reportName");
			JSONObject jsonDatasource = json.getJSONObject("datasource");
			String jsonDatasourceStr = jsonDatasource.toString();

			dataInputStream = new ByteArrayInputStream(jsonDatasourceStr.getBytes(Charset.forName("UTF-8")));

			ClassPathResource reportResource = new ClassPathResource("/" + reportName + ".jrxml");
			if (reportResource == null) {
				throw new IllegalStateException("Can not find report with name " + reportName);
			}

			JasperDesign jasperDesign = JRXmlLoader.load(reportResource.getInputStream());
			JasperReport report = JasperCompileManager.compileReport(jasperDesign);
			JsonDataSource ds = new JsonDataSource(dataInputStream);
			ds.setDatePattern("yyyy-MM-dd'T'HH:mmZ");

			Map<String, Object> params = new HashMap<>();
			JasperPrint jasperPrint = JasperFillManager.fillReport(report, params, ds);

			preparePrintParametersForJasper();

			printServiceExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		} catch (IOException | JRException | JSONException ex) {
			throw new RuntimeException(ex);
		}
	}

}
