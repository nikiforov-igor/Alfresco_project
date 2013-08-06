package ru.it.lecm.reports.beans;

import java.io.ByteArrayInputStream;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.util.JRLoader;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.surf.util.URLDecoder;

import ru.it.lecm.reports.api.DsLoader;
import ru.it.lecm.reports.api.ReportGenerator;
import ru.it.lecm.reports.api.ReportsManager;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.api.model.ReportTemplate;
import ru.it.lecm.reports.api.model.ReportType;
import ru.it.lecm.reports.api.model.DAO.ReportDAO;
import ru.it.lecm.reports.api.model.share.ModelLoader;
import ru.it.lecm.reports.generators.XMLMacroGenerator;
import ru.it.lecm.reports.utils.Utils;
import ru.it.lecm.reports.xml.DSXMLProducer;

public class ReportsManagerImpl implements ReportsManager {

	static final transient Logger logger = LoggerFactory.getLogger(ReportsManagerImpl.class);

	final static String REPORT_TEMPLATE_FILES_BASEDIR = "/reportdefinitions";
	final static String REPORT_DS_FILES_BASEDIR = REPORT_TEMPLATE_FILES_BASEDIR + "/ds-config";

	final static String DEFAULT_GENXMLTEMPLATE_BASEDIR = REPORT_TEMPLATE_FILES_BASEDIR + "/templates";
	final static String DEFAULT_JRXMLFILENAME = "jreportCommonTemplate.jrxml.gen";
	final public static String DEFAULT_REPORT_TYPE = "JASPER";

	/**
	 * Список зарегистрирванных отчётов
	 */
	private Map<String, ReportDescriptor> descriptors;

	private ReportDAO reportDAO;

	private String defaultGenTemplate = DEFAULT_JRXMLFILENAME; // шаблон для генерации xml-шаблона отчёта
	private String defaultGenReportType = DEFAULT_REPORT_TYPE; // тип отчёта по-умолчанию

	// Map<КодТипаОтчёта, Провайдер>
	private Map< /*ReportType*/String, ReportGenerator> reportGenerators;

	private NamespaceService namespaceService;

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	@Override
	public ReportDAO getReportDAO() {
		return reportDAO;
	}

	public void setReportDAO(ReportDAO reportDAO) {
		this.reportDAO = reportDAO;
	}

	@Override
	public List<ReportDescriptor> getRegisteredReports() {
		return getRegisteredReports(null, null);
	}

	@Override
	public List<ReportDescriptor> getRegisteredReports(String[] docTypes, boolean forCollection) {
		final Set<ReportDescriptor> unFilteredReports = new HashSet<ReportDescriptor>();
		if (docTypes != null) {
			for (String docType : docTypes) {
				if (docType != null && docType.length() > 0) {
					if (docType.startsWith(String.valueOf(QName.NAMESPACE_BEGIN))) {
						docType = QName.createQName(docType).toPrefixString(namespaceService);
					}
					unFilteredReports.addAll( getRegisteredReports(docType, null));
				}
			}
		} else {
			unFilteredReports.addAll( getRegisteredReports(null, null));
		}

		final List<ReportDescriptor> resultedReports = new ArrayList<ReportDescriptor>();
		for (ReportDescriptor descriptor : unFilteredReports) {
			if (descriptor.getFlags().isCustom() && forCollection) // пропускаем кастомизированные для многострочных отчётов ... 
				continue;
			if (forCollection == descriptor.getFlags().isMultiRow()) {
				resultedReports.add(descriptor);
			}
		}
		return resultedReports;
	}

	public DsLoader getDsloader() {
		return ModelLoader.getInstance();
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

	public String getDefaultGenTemplate() {
		return defaultGenTemplate;
	}

	public void setDefaultGenTemplate(String defaultGenTemplate) {
		this.defaultGenTemplate = defaultGenTemplate;
	}

	public String getDefaultGenReportType() {
		return defaultGenReportType;
	}

	public void setDefaultGenReportType(String defaultGenReportType) {
		this.defaultGenReportType = defaultGenReportType;
	}

	/**
	 * @return не NULL список [ReportTypeMnemonic -> ReportGenerator]
	 */
	@Override
	public Map</*ReportType*/String, ReportGenerator> getReportGenerators() {
		if (reportGenerators == null)
			reportGenerators = new HashMap<String, ReportGenerator>(1);
		return reportGenerators;
	}

	/**
	 * Задать соот-вие типов отчётов и их провайдеров
	 * @param map список [ReportTypeMnemonic -> ReportGenerator]
	 */
	@Override
	public void setReportGenerators(Map<String, ReportGenerator> map) {
		this.reportGenerators = map;
	}

	@Override
	public String getReportTypeTag(ReportType rtype) {
		return (rtype != null && rtype.getMnem() != null) ? rtype.getMnem() : DEFAULT_REPORT_TYPE;
	}

	/**
	 * Выполнить сканирование и загрузку описателей существующих отчётов ...
	 * @return кол-во найденных файлов отчётов 
	 */
	private int scanResources() {

		// final List<URL> found = JRLoader.getResources( getDsRelativeFileName("*")); // REPORT_DS_FILES_BASEDIR + "/ds-*.xml"

		logger.info( String.format( "Auto scanning reports at path '%s' ...", REPORT_DS_FILES_BASEDIR));
		File[] found = null;
		{
			final List<URL> base = JRLoader.getResources( REPORT_DS_FILES_BASEDIR); // REPORT_DS_FILES_BASEDIR + "/ds-*.xml"
			if (base != null && !base.isEmpty()) {
				final File scanner = new File(URLDecoder.decode(base.get(0).getFile()));
				logger.debug( String.format( " ... path found as '%s' ...", scanner));
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
				logger.debug( String.format( "loading report descriptor from file '%s' ...", item) );
				try {
					final String reportName = item.getName(); // StringUtils.getFilename(item.getFile());
					final InputStream in = new FileInputStream(item);
					try {
						// загружаем описатеть из файла
						try { // try-catch wraping
							final ReportDescriptor desc = DSXMLProducer.parseDSXML(in, reportName);
							if (desc == null) continue;
							if (desc.getMnem() == null) // задать название название шаблона по-умолчанию ...
								desc.setMnem( DSXMLProducer.extractReportName(item.getName()));
							this.descriptors.put(desc.getMnem(), desc);
							ifound++;
							logger.debug( String.format( "... loaded report descriptor '%s' from '%s'", desc.getMnem(), item));
						} catch (Throwable ex) {
							logger.error( String.format( "Problem parsing xml file at '%s' -> ignored\n%s", item, ex.getMessage()), ex);
						}
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
		// logger.info( Utils.dumpAlfData( System.getProperties(),  "System.getProperties():\n").toString() );
		if (getDescriptors() != null && getDescriptors().size() > 0) {
			if (logger.isInfoEnabled()) // только названия шаблонов ...
				logger.info( String.format( " initialized templates count %s\n\t%s"
						, getDescriptors().size(), Utils.getAsString(getDescriptors().keySet(), "\n\t")
				)); 
			else if (logger.isDebugEnabled()) // целиком загруженные шаблоны ...
				logger.debug( String.format( " initialized templates count %s\n\t%s"
						, getDescriptors().size(), Utils.getAsString(getDescriptors().values(), "\n\t") 
				));
		}
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
			return d; // FOUND by Mnemonic
		}

		// попытка загрузить DAO-объект
		// TODO: после автоподъёма at boot-time файлов ds-xml, здесь уже не понадобится
		if (reportDAO != null) {
			final ReportDescriptor d = reportDAO.getReportDescriptor(reportMnemoName);
			if (d != null) {
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
			setDefaults(desc);
			createDsFile( desc); // создание ds-xml
			saveReportTemplate( desc) ; // сохранение шаблона отчёта 
			getDescriptors().put(desc.getMnem(), desc);
			logger.info(String.format( "Report descriptor with name '%s' registered", desc.getMnem()));
		}
	}

	private void setDefaults(ReportDescriptor desc) {
		if ( Utils.isStringEmpty(desc.getReportType().getMnem())) {
			desc.getReportType().setMnem( this.getDefaultGenReportType() );
			logger.warn(String.format( "Report '%s' has empty reeport type -> set to '%s'", desc.getMnem(), desc.getReportType().getMnem()) );
		}
		// TODO: ввести зависимость от типа очёта
		if (desc.getReportTemplate().getFileName() == null) { // задать default-название файла
			desc.getReportTemplate().setFileName( desc.getMnem() + ".jrxml");
			logger.warn(String.format( "Report '%s' has empty template FileName -> set to '%s'", desc.getMnem(), desc.getReportTemplate().getFileName()) );
		}
		if (desc.getReportTemplate().getData() == null) {
			// сгенерировать xml отчёта по gen-шаблону ...
			final ByteArrayOutputStream stm = this.generateReportTemplate(desc); // stm.writeTo( new java.io.FileOutputStream("x.jrxml"));
			desc.getReportTemplate().setData( (stm == null) ? null : new ByteArrayInputStream(stm.toByteArray()));
			logger.warn(String.format( "Report '%s' has empty template data -> generated from '%s'", desc.getMnem(), makeGenTemplateFileName(this.getDefaultGenTemplate())) );
		}
	}

	private boolean saveReportTemplate(ReportDescriptor desc) {
		if (desc.getReportTemplate() == null) {
			logger.warn(String.format( "Report '%s' has no template", desc.getMnem()));
			return false;
		}

		/* NOTE: если требуется жёсткая проверка наличия данных
		if (desc.getReportTemplate().getData() == null) {
			logger.warn(String.format( "Report '%s' has no template data", desc.getMnem()));
			return false;
		}
		if (desc.getReportTemplate().getFileName() == null) {
			logger.warn(String.format( "Report '%s' has empty template FileName", desc.getMnem()));
			return false;
		}
		 */

		// @NOTE: название шаблона существенно зависит от типа отчёта и известно только описателю
		final String outFullName = makeReportTemplateFileName(desc);
		// ensureFileNotPresent(outFullName);
		{	// гарантируем, что с таким именем данных не будет ...
			final File fout = new File( outFullName);
			if (fout.exists()) {
				final File backupName = Utils.findEmptyFile( fout, ".bak%s");
				logger.warn(String.format( "Report '%s': saving previous template as '%s' ...", desc.getMnem(), backupName));
				fout.renameTo(backupName); // переименование
			}
		}

		// оповещение соот-го провайдера (компиляция) ...
		getReportGenerators().get( getReportTypeTag(desc.getReportType())).onRegister(outFullName, desc);
		logger.debug(String.format( "Report '%s': provider notified", desc.getMnem()));

		return true;
	}

	@Override
	public void unregisterReportDescriptor(String reportCode) {
		if (reportCode != null) {
			if (this.descriptors != null)
				this.descriptors.remove(reportCode); // убираем из списка активных
			removeDsFile( reportCode); // удаляем файл
			logger.debug(String.format( "Report descriptor with name '%s' unregistered", reportCode));
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
					logger.debug( String.format( "report '%s': ds xml created '%s'" , desc.getMnem(), fout) );
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
			logger.debug( String.format( "ds-xml of report '%s' was%s deleted", reportCode, (flagDelete ? "" : " NOT")));
		}
	}

	/**
	 * Получить название базового каталога в файловой системе (".")
	 * @return
	 */
	private File getBaseDir() {
		final URL base = JRLoader.getResource( "/"); // получение главного каталога
		return new File(URLDecoder.decode(base.getFile()));// NPE скажет о проблемах, т.к. базовый каталог обязан существовать
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
	 * Получить и гарантировать наличие каталога для хранения ГОТОВЫХ шаблонов отчётов (*.jrxml файлы)
	 * @return
	 */
	private File ensureReportTemplateConfigDir() {
		final File result = getConfigDir(REPORT_TEMPLATE_FILES_BASEDIR);
		result.mkdirs();
		return result;
	}

	/**
	 * Гарантировать каталог шаблонов для генератора (*.gen файлы)
	 * @return
	 */
	private File ensureGenTemplateConfigDir() {
		final File result = getConfigDir(DEFAULT_GENXMLTEMPLATE_BASEDIR);
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
		return String.format( "%s/%s%s.xml", base.getAbsolutePath(), DSXMLProducer.PFX_DS, mnem); 
	}

	/**
	 * Получить полное название файла шаблона для указанного описателя (наример, для jasper-отчётов это будет файл с расширением "*.jrxml").
	 * Родительские каталоги, при их отсутствии, создаются автоматом.
	 * @param template описание отчёта
	 * @return полное имя файла (мнемоника шаблона . расширение)
	 */
	private String makeReportTemplateFileName(ReportDescriptor desc) {
		if (desc == null || desc.getReportTemplate() == null)
			return null;
		final ReportTemplate template = desc.getReportTemplate();
		ensureReportTemplateConfigDir(); 
		return makeReportTemplateFileName( desc.getMnem()
				+ "." + FilenameUtils.getExtension(Utils.coalesce( template.getFileName(), "template")) );
	}

	/**
	 * Получить полное название файла указанного шаблона.
	 * Родительские каталоги, при их отсутствии, создаются автоматом.
	 * @param xmlFileName название файла-шаблона
	 * @return полное имя файла.
	 */
	private String makeReportTemplateFileName(String xmlFileName) {
		if (xmlFileName == null)
			return null;
		final File base = ensureReportTemplateConfigDir();
		return String.format( "%s/%s", base.getAbsolutePath(), xmlFileName); 
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
		if (url == null) {
			logger.warn( String.format("ds-xml for report '%s' is not found", reportCode));
		}
		try {
			return url != null ? JRLoader.loadBytes(url) : null;
		} catch (JRException ex) {
			logger.error( String.format( "Error for report '%s' reading ds file from '%s'", reportCode, url), ex);
			return null;
		}
	}

	@Override
	public byte[] produceDefaultTemplate(ReportDescriptor reportDesc) {
		final ByteArrayOutputStream result = generateReportTemplate(reportDesc);
		return (result != null) ? result.toByteArray() : null;
	}

	/**
	 * Сгенерировать для указанного описателя файл с шаблоном отчёта.
	 * Используется сконфигурированное имя gen-шаблона.
	 * @param reportDesc
	 * @return
	 */
	public ByteArrayOutputStream generateReportTemplate(ReportDescriptor reportDesc) {
		if (reportDesc == null) return null;

		PropertyCheck.mandatory (this, "defaultGenTemplate", getDefaultGenTemplate());
		// PropertyCheck.mandatory (this, "jrxmlGenerator", getJrxmlGenerator());

		final XMLMacroGenerator xmlGenerator = new XMLMacroGenerator(reportDesc);

		final String tname = makeGenTemplateFileName(this.getDefaultGenTemplate()); // название файла-шаблона для генерации
		FileInputStream fin = null;
		try {
			fin = new FileInputStream( tname);
			final ByteArrayOutputStream result = xmlGenerator.xmlGenerateByTemplate( tname, fin);
			return result;
		} catch (FileNotFoundException ex) {
			final String msg = String.format( "Generator template file not found at '%s'", tname);
			logger.error(msg, ex);
			throw new RuntimeException( msg, ex);
		} finally {
			if (fin != null)
				IOUtils.closeQuietly(fin);
		}
	}

	/**
	 * Получить полный файловый путь с названием gen-шаблона 
	 * @param defaultTemplate
	 * @return
	 */
	private String makeGenTemplateFileName(String defaultTemplate) {
		final File base = ensureGenTemplateConfigDir();
		return String.format( "%s/%s", base.getAbsolutePath(), defaultTemplate);
	}

	@Override
	public String getDsRelativeFileName(String reportCode) {
		return String.format( "%s/%s%s.xml", REPORT_DS_FILES_BASEDIR, DSXMLProducer.PFX_DS, reportCode);
	}

	@Override
	public String getReportTemplateFileDir(ReportType reportType) {
		// TODO: задавать каталог надо в зависимости от типа отчёта
		return REPORT_TEMPLATE_FILES_BASEDIR;
	}

	@Override
	public List<ReportDescriptor> getRegisteredReports(String docType, String reportType) {
		final Map<String, ReportDescriptor> list = this.getDescriptors();
		if (list == null || list.isEmpty())
			return new ArrayList<ReportDescriptor>();

		if (docType != null && docType.length() == 0) {
			docType = null;
		}

		if (reportType != null && reportType.length() == 0) {
			reportType = null;
		}

		if (docType == null && reportType == null)  {
			// не задано фильтрование
			return new ArrayList<ReportDescriptor>(list.values());
		}

		final List<ReportDescriptor> found = new ArrayList<ReportDescriptor>();
		for (ReportDescriptor desc : list.values()) {
			final boolean okDocType = 
					(docType == null)	// не задан фильтр по типам доков 
					|| ( (desc.getFlags() == null) || desc.getFlags().getPreferedNodeType() == null) // нет флажков у шаблона -> подходит к любому
					|| docType.equalsIgnoreCase(desc.getFlags().getPreferedNodeType()) // совпадение типа (если заданы искомый тип и флажки)
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
		// return (found.isEmpty()) ? null : found;
		return found;
	}

}
