package ru.it.lecm.reports.beans;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.util.JRLoader;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import ru.it.lecm.reports.api.DsLoader;
import ru.it.lecm.reports.api.ReportsManager;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.api.model.DAO.ReportDAO;
import ru.it.lecm.reports.api.model.share.ModelLoader;
import ru.it.lecm.reports.utils.Utils;
import ru.it.lecm.reports.xml.DSXMLProducer;

public class ReportsManagerImpl implements ReportsManager {

	static final transient Logger logger = LoggerFactory.getLogger(ReportsManagerImpl.class);

	/**
	 * Список зарегистрирванных отчётов
	 */
	private List<ReportDescriptor> descriptors;

	private ReportDAO reportDAO;

		public ReportDAO getReportDAO() {
		return reportDAO;
	}

	public void setReportDAO(ReportDAO reportDAO) {
		this.reportDAO = reportDAO;
	}

	public DsLoader getDsloader() {
		return ModelLoader.getInstance();
	}

	public List<ReportDescriptor> getDescriptors() {
		if (this.descriptors == null) {
			this.descriptors = new ArrayList<ReportDescriptor>();
			scanResources();
		}
		return this.descriptors;
	}

	/**
	 * Выбираем название отчёта из описателя вида "xxx\ds-reportname.xml" 
	 * @param filename
	 * @return
	 */
	private static String extractReportName( String filename) {
		if (filename == null)
			return null;
		int start = filename.indexOf("ds-");
		if (start < 0) 
			start = 0; // если нет "ds-", то с начала строки
		else // иначе с смивола после "ds-"
			start+=3;
		int end = filename.lastIndexOf(".");
		if (end < 0) end = filename.length(); // если нет точки - до конца строки
		return filename.substring(start, end);
	}

	/**
	 * Выполнить сканирование и загрузку описателей существующих отчётов ...
	 * @return кол-во найденных файлов отчётов 
	 */
	private int scanResources() {

		// final List<URL> found = JRLoader.getResources( getDsRelativeFileName("*")); // REPORT_DS_FILES_BASEDIR + "/ds-*.xml"

		File[] found = null;
		{
			final List<URL> base = JRLoader.getResources( REPORT_DS_FILES_BASEDIR); // REPORT_DS_FILES_BASEDIR + "/ds-*.xml"
			if (base != null && !base.isEmpty()) {
				final File scanner = new File( base.get(0).getFile());
				found = scanner.listFiles(new FileFilter() {
					@Override
					public boolean accept(File file) {
						return (file != null) && file.isFile() && file.getName().endsWith(".xml");
					}
				});
			}
		}

		int ifound = 0;
		if (found != null) {
			if (this.descriptors == null)
				this.descriptors = new ArrayList<ReportDescriptor>();
			// for (URL u: found) {
			for (File item: found) {
				try {
					final String reportName = item.getName(); // StringUtils.getFilename(item.getFile());
					final InputStream in = new FileInputStream(item);
					try {
						// загружаем описатеть из файла
						final ReportDescriptor desc = DSXMLProducer.parseDSXML(in, reportName);
						if (desc == null) continue;
						if (desc.getMnem() == null)
							desc.setMnem( extractReportName(item.getName()));

						this.descriptors.add(0, desc);
						ifound++;
						logger.info( String.format( "Load report '%s' from descriptor from '%s'", desc.getMnem(), item) );
					} finally {
						IOUtils.closeQuietly(in);
					} // finally
				} catch(FileNotFoundException ex) {
					// log and skip it
					logger.error( String.format( "Fail to find file at '%s' -> ignored", item) );
				}
			}
		}
		return ifound;
	}

	public void setDescriptors(List<ReportDescriptor> list) {
		this.descriptors = list;
	}

	public void init() {
		logger.info( String.format( " initialized templates count %s\n%s",
					getDescriptors().size(), Utils.getAsString(getDescriptors()) 
		));
	}

	/**
	 * Получить дексриптор отчёта по его мнемонике или вернуть null.
	 * Поиск ведётся по зарегистрированным отчётам в this.descriptors и в БД.
	 * @param reportMnemoName дескриптор
	 * @return описатеть отчёта или null, если не найден
	 */
	@Override
	public ReportDescriptor getRegisteredReportDescriptor(String reportMnemoName) {

		for(ReportDescriptor d: getDescriptors()) {
			if (Utils.isSafelyEquals(reportMnemoName, d.getMnem())) {
				if (logger.isDebugEnabled())
					logger.debug( String.format( "Found bean report mnem '%s' as:\n%s", reportMnemoName, d));
				return d; // FOUND by Mnemonic
			}
		}

		// попытка загрузить DAO-объект
		// TODO: после автоподъёма at boot-time файлов ds-xml, здесь уже не понадобится
		if (reportDAO != null) {
			final ReportDescriptor d = reportDAO.getReportDescriptor(reportMnemoName);
			if (d != null) {
				if (logger.isDebugEnabled())
					logger.debug( String.format( "Load template descriptor for mnem '%s' as:\n%s", reportMnemoName, d));
				return d; // FOUND by DAO mnemonic
			}
		}

		logger.warn(String.format( "Report '%s' has no descriptor", reportMnemoName));
		return null; // NOT FOUND
	}

	@Override
	public void registerReportDescriptor(NodeRef rdescId) {
		PropertyCheck.mandatory (this, "reportDAO", getReportDAO());
		final ReportDescriptor rdesc = getReportDAO().getReportDescriptor( rdescId);
		registerReportDescriptor(rdesc);
	}

	@Override
	public void registerReportDescriptor(ReportDescriptor desc) {
		if (desc != null) {
			checkReportDescData(desc);

			getDescriptors().add(0, desc);
			createDsFile( desc);

			logger.info(String.format( "Report descriptor with name '%s' registered", desc.getMnem()));
		}
	}

	/**
	 * Создание ds-xml файла с названием "ds-"+desc.getMnem()+".xml"
	 * @param desc
	 */
	private void createDsFile(ReportDescriptor desc) {
		if (desc == null)
			return;
		checkReportDescData(desc);

		// содзание ds-файла ...
		final ByteArrayOutputStream dsxml = DSXMLProducer.xmlCreateDSXML(desc.getMnem(), desc);
		if (dsxml != null) {
			final URL url = getDsXmlResourceUrl(desc.getMnem());
			try {
				final OutputStream out = new FileOutputStream( url.getFile());
				try {
					dsxml.writeTo(out);
				} finally {
					IOUtils.closeQuietly(out);
				}
			} catch(Throwable ex) {
				final String msg = String.format( "Report '%s': error saving ds-xml into '%s'" , desc.getMnem(), url);
				logger.error( msg, ex);
				throw new RuntimeException(msg, ex);
			}
		}
	}

	/**
	 * Выполнить проверку данных. Поднять исключения при неверном/недостаточном заполнении.
	 * @param desc
	 */
	private void checkReportDescData(ReportDescriptor desc) {
		if (desc.getMnem() == null || desc.getMnem().trim().length() == 0)
			throw new RuntimeException( String.format( "Report descriptor must have mnemo code"));
	}

	@Override
	public URL getDsXmlResourceUrl(String reportCode) {
		PropertyCheck.mandatory (this, "dsloader", getDsloader());
		return reportCode != null ? JRLoader.getResource(getDsRelativeFileName(reportCode)) : null;
	}

	@Override
	public byte[] loadDsXmlBytes(String reportCode) {
		PropertyCheck.mandatory (this, "dsloader", getDsloader());
		final URL url = getDsXmlResourceUrl(reportCode);
		try {
			return url != null ? JRLoader.loadBytes(url) : null;
		} catch (JRException ex) {
			logger.error( String.format( "Error for report '%s' reading ds file from '%s'", reportCode, url), ex);
			return null;
		}
	}

	final private static String REPORT_TEMPLATE_FILES_BASEDIR = "/reportdefinitions";
	final private static String REPORT_DS_FILES_BASEDIR = "/reportdefinitions/ds-config";

	@Override
	public String getDsRelativeFileName(String reportCode) {
		return String.format( "%s/ds-%s.xml", REPORT_DS_FILES_BASEDIR, reportCode);
	}

	@Override
	public String getReportTemplateFileDir(final String reportCode) {
		return REPORT_TEMPLATE_FILES_BASEDIR;
	}

	@Override
	public List<ReportDescriptor> getRegisteredReports( String docType
			, String reportType)
	{
		final List<ReportDescriptor> list = this.getDescriptors();
		if (list == null || list.isEmpty())
			return null;
		if (docType == null && reportType == null) // не задано фильтрование
			return list;
		final List<ReportDescriptor> found = new ArrayList<ReportDescriptor>();
		for (ReportDescriptor desc: list) {
			final boolean okDocType = 
					(docType == null)	// не задан фильтр по типам доков 
					|| ( (desc.getFlags() == null) || desc.getFlags().getPreferedNodeType() == null) // нет флажков у шаблона -> подходит к любому
					|| docType.equalsIgnoreCase(desc.getFlags().getPreferedNodeType()) // совпадение типа
					;
			final boolean okRType = 
					(reportType == null)	// не задан фильтр по типам отчётов 
					|| ( (desc.getReportType() == null) || desc.getReportType().getMnem() == null) // не задан тип отчёта шаблона -> подходит к любому
					|| reportType.equalsIgnoreCase(desc.getReportType().getMnem()) // совпадение типа
					;
			if (okDocType && okRType) {
				found.add(desc);
			}
		}
		return (found.isEmpty()) ? null : found;
	}
}
