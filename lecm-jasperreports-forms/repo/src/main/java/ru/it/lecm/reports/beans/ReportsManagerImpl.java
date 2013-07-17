package ru.it.lecm.reports.beans;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.util.JRLoader;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.surf.util.URLDecoder;
import ru.it.lecm.reports.api.ReportsManager;
import ru.it.lecm.reports.api.model.DAO.ReportDAO;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.api.model.ReportTemplate;
import ru.it.lecm.reports.api.model.ReportType;
import ru.it.lecm.reports.generators.JRXMLMacroGenerator;
import ru.it.lecm.reports.utils.Utils;
import ru.it.lecm.reports.xml.DSXMLProducer;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportsManagerImpl implements ReportsManager {

	static final transient Logger logger = LoggerFactory.getLogger(ReportsManagerImpl.class);

	/**
	 * Список зарегистрирванных отчётов
	 */
	private Map<String, ReportDescriptor> descriptors;

	private ReportDAO reportDAO;

	private String defaultTemplate; //  шаблон для генерации (jrxml-)шаблона

    static final String JRXML_DEFAULT_TEMPLATE_ROOT = "/reportdefinitions/templates";

	public ReportDAO getReportDAO() {
		return reportDAO;
	}

	public void setReportDAO(ReportDAO reportDAO) {
		this.reportDAO = reportDAO;
	}

	public Map<String, ReportDescriptor> getDescriptors() {
		if (this.descriptors == null) {
			this.descriptors = new HashMap<String, ReportDescriptor>();
			scanResources();
		}
//		final Collection<ReportDescriptor> col = this.descriptors.values();
//		return (col != null) ? new ArrayList<ReportDescriptor>(col) : new ArrayList<ReportDescriptor>();
		return this.descriptors;
	}

	public String getDefaultTemplate() {
		return defaultTemplate;
	}

	public void setDefaultTemplate(String defaultTemplate) {
		this.defaultTemplate = defaultTemplate;
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
				final File scanner = new File(URLDecoder.decode(base.get(0).getFile()));
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
				this.descriptors = new HashMap<String, ReportDescriptor>();
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

						this.descriptors.put(desc.getMnem(), desc);
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
		// this.descriptors = list;
		this.descriptors = null;
		if (list != null)
			for (ReportDescriptor desc: list)
				registerReportDescriptor(desc);
	}

	public void init() {
//		{
//			final File result = new File( getDsConfigDir() + "/test-subdir");
//			logger.info( String.format( "creating dir '%s'\n\t%s", result, result.mkdirs()) );
//		}

		logger.info( String.format( " initialized templates count %s\n%s",
					getDescriptors().size(), Utils.getAsString(getDescriptors().values()) 
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

//		for(ReportDescriptor d: getDescriptors()) {
//			quals(reportMnemoName, d.getMnem())) {
//				if (logger.isDebugEnabled())
//					logger.debug( String.format( "Found bean report mnem '%s' as:\n%s", reportMnemoName, d));
//				return d; // FOUND by Mnemonic
//			}
//		}
		if (getDescriptors().containsKey(reportMnemoName)) {
			final ReportDescriptor d = this.descriptors.get(reportMnemoName);
			if (logger.isDebugEnabled())
				logger.debug( String.format( "Found bean report mnem '%s' as:\n%s", reportMnemoName, d));
			return d; // FOUND by Mnemonic
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
			getDescriptors().put(desc.getMnem(), desc);
			createDsFile( desc); // создание ds-xml
			saveReportTemplate( desc) ; // создание шаблона отчёта 
			logger.info(String.format( "Report descriptor with name '%s' registered", desc.getMnem()));
		}
	}

	private boolean saveReportTemplate(ReportDescriptor desc) {
		if (desc.getReportTemplate() == null) {
			logger.warn(String.format( "Report '%s' has no template", desc.getMnem()));
			return false;
		}
		if (desc.getReportTemplate().getData() == null) {
			logger.warn(String.format( "Report '%s' has no template data", desc.getMnem()));
			return false;
		}
		if (desc.getReportTemplate().getFileName() == null) {
			logger.warn(String.format( "Report '%s' has empty template FileName", desc.getMnem()));
			return false;
		}

		final String outFullName = makeTemplateFileName(desc.getReportTemplate());
		{	// гарантируем, что с таким именем данных нет ...
			final File fout = new File( outFullName);
			if (fout.exists()) {
				final File backupName = Utils.findEmptyFile( fout, ".bak%s");
				logger.warn(String.format( "Report '%s': saving previous template as '%s' ..."
						, desc.getMnem(), backupName));
				fout.renameTo(backupName);
			}
		}

		// сохранение
		try {
			final FileOutputStream wout = new FileOutputStream(outFullName);
			try {
				IOUtils.copy(desc.getReportTemplate().getData(), wout);
			} finally {
				IOUtils.closeQuietly(wout);
			}
			logger.info(String.format( "Report '%s': template file created as '%s'"
					, desc.getMnem(), outFullName));
			return true;
		} catch (IOException ex) {
			final String msg = String.format( "Report '%s': error creating template file '%s'\n%s"
				, desc.getMnem(), outFullName, ex.getMessage());
			logger.error( msg, ex);
			throw new RuntimeException( msg, ex);
		}
	}

	@Override
	public void unregisterReportDescriptor(String reportCode) {
		if (reportCode != null) {
			if (this.descriptors != null)
				this.descriptors.remove(reportCode); // убираем из списка активных
			removeDsFile( reportCode); // удаляем файл
			logger.info(String.format( "Report descriptor with name '%s' unregistered", reportCode));
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

		// создание ds-файла ...
		final ByteArrayOutputStream dsxml = DSXMLProducer.xmlCreateDSXML(desc.getMnem(), desc);
		if (dsxml != null) {
			final File fout = new File( makeDsXmlFileName(desc.getMnem()));
			try {
				final OutputStream out = new FileOutputStream( fout);
				try {
					dsxml.writeTo(out); 
					logger.info( String.format( "report '%s': ds xml created '%s'" , desc.getMnem(), fout) );
				} finally {
					IOUtils.closeQuietly(out);
				}
			} catch(Throwable ex) {
				final String msg = String.format( "Report '%s': error saving ds-xml into '%s'" , desc.getMnem(), fout);
				logger.error( msg, ex);
				throw new RuntimeException(msg, ex);
			}
		}
	}

	private boolean removeDsFile(String reportCode) {
		boolean flagDelete = false;
		File fout = null;
		try {
			if (reportCode == null)
				return false;
			// удаление ds-файла ...
			fout = new File( makeDsXmlFileName(reportCode));
			flagDelete = fout.exists() && fout.delete();
			return flagDelete;
		} catch(Throwable ex) {
			final String msg = String.format( "Report '%s': error deleting ds-xml '%s'" , reportCode, fout);
			logger.error( msg, ex);
			throw new RuntimeException(msg, ex);
		} finally {
			logger.info( String.format( "ds-xml of report '%s' was%s deleted", reportCode, (flagDelete ? "" : " NOT")));
		}
	}

	/**
	 * Получить название базового каталога в файловой системе (".")
	 * @return
	 */
	private File getBaseDir() {
		final URL base = JRLoader.getResource( "/"); // получение главного каталога
		return new File(URLDecoder.decode(base.getFile())); // NPE скажет о проблемах, т.к. базовый каталог обязан существовать
	}

	/**
	 * Получить имя конфигурационного каталога (например, для хранения dsxml файлов getConfigDir("/reportdefinitions/ds-config"); )
	 * @param relativePath суб-каталог внутри рабочего
	 * @return
	 */
	private File getConfigDir(final String relativePath) {
		return new File( getBaseDir().getAbsolutePath() + relativePath);
	}

	/**
	 * Получить и гарантировать наличие каталога для хранения dsxml файлов
	 * @return
	 */
	private File ensureDsConfigDir() {
		final File result = getConfigDir(REPORT_DS_FILES_BASEDIR);
		result.mkdirs();
		return result;
	}

	/**
	 * Получить и гарантировать наличие каталога для хранения шаблонов
	 * @return
	 */
	private File ensureTemplateConfigDir() {
		final File result = getConfigDir(REPORT_TEMPLATE_FILES_BASEDIR);
		result.mkdirs();
		return result;
	}

    private File ensureDefaultTemplateConfigDir() {
        final File result = getConfigDir(JRXML_DEFAULT_TEMPLATE_ROOT);
        result.mkdirs();
        return result;
    }
	/**
	 * Получить полное название ds-xml файла, в котором может храниться ds-xml описание.
	 * указанного отчёта.
	 * Родительские каталоги, при их отсутствии, создаются автоматом.
	 * @param mnem мнемоника отчёта
	 * @return полное имя файла.
	 */
	private String makeDsXmlFileName(String mnem) {
		final File base = ensureDsConfigDir();
		return String.format( "%s/ds-%s.xml", base.getAbsolutePath(), mnem); 
	}

	/**
	 * Получить полное название файла указанного шаблона.
	 * Родительские каталоги, при их отсутствии, создаются автоматом.
	 * @param template  описание отчёта
	 * @return полное имя файла.
	 */
	private String makeTemplateFileName(ReportTemplate template) {
        final File base = ensureTemplateConfigDir();
		return (template == null) ? null : makeTemplateFileName(template.getFileName()); 
	}

	/**
	 * Получить полное название файла указанного шаблона.
	 * Родительские каталоги, при их отсутствии, создаются автоматом.
	 * @param templateFileName название файла-шаблона
	 * @return полное имя файла.
	 */
	private String makeTemplateFileName(String templateFileName) {
		final File base = ensureTemplateConfigDir();
		return String.format( "%s/%s", base.getAbsolutePath(), templateFileName); 
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
		return reportCode != null ? JRLoader.getResource(getDsRelativeFileName(reportCode)) : null;
	}

	@Override
	public byte[] loadDsXmlBytes(String reportCode) {
		final URL url = getDsXmlResourceUrl(reportCode);
		try {
			return url != null ? JRLoader.loadBytes(url) : null;
		} catch (JRException ex) {
			logger.error( String.format( "Error for report '%s' reading ds file from '%s'", reportCode, url), ex);
			return null;
		}
	}

	@Override
	public byte[] produceDefaultTemplate(ReportDescriptor reportDesc) {
		if (reportDesc == null) return null;

		PropertyCheck.mandatory (this, "defaultTemplate", getDefaultTemplate());
		// PropertyCheck.mandatory (this, "jrxmlGenerator", getJrxmlGenerator());

		final JRXMLMacroGenerator jrxmlGenerator = new JRXMLMacroGenerator();
		jrxmlGenerator.setReportDesc(reportDesc);

		final String tname = makeDefaultTemplateFileName(this.defaultTemplate); // название файла-шаблона для генерации
		FileInputStream fin = null; 
		try {
			fin = new FileInputStream( tname);
			final ByteArrayOutputStream result = jrxmlGenerator.xmlGenerateByTemplate( reportDesc.getMnem(), fin);
			return (result != null) ? result.toByteArray() : null;
		} catch (FileNotFoundException ex) {
			final String msg = String.format( "Generator template file not found at '%s'", tname);
			logger.error(msg, ex);
			throw new RuntimeException( msg, ex);
		} finally {
			if (fin != null)
				IOUtils.closeQuietly(fin);
		}
	}

    private String makeDefaultTemplateFileName(String defaultTemplate) {
        final File base = ensureDefaultTemplateConfigDir();
        return String.format( "%s/%s", base.getAbsolutePath(), defaultTemplate);
    }

    final private static String REPORT_TEMPLATE_FILES_BASEDIR = "/reportdefinitions";
	final private static String REPORT_DS_FILES_BASEDIR = "/reportdefinitions/ds-config";

	@Override
	public String getDsRelativeFileName(String reportCode) {
		return String.format( "%s/ds-%s.xml", REPORT_DS_FILES_BASEDIR, reportCode);
	}

	@Override
	public String getReportTemplateFileDir(ReportType reportType) {
		// TODO: задавать каталог надо в зависимости от типа отчёта
		return REPORT_TEMPLATE_FILES_BASEDIR;
	}

	@Override
	public List<ReportDescriptor> getRegisteredReports( String docType
			, String reportType)
	{
		final Map<String, ReportDescriptor> list = this.getDescriptors();
		if (list == null || list.isEmpty())
			return null;

		if (docType != null && docType.length() == 0)
			docType = null;

		if (reportType != null && reportType.length() == 0)
			reportType = null;

		if (docType == null && reportType == null) // не задано фильтрование
			return new ArrayList<ReportDescriptor>( list.values());
		final List<ReportDescriptor> found = new ArrayList<ReportDescriptor>();
		for (ReportDescriptor desc: list.values()) {
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

    @Override
    public List<ReportDescriptor> getRegisteredReports() {
        return getRegisteredReports(null, null);
    }
}
