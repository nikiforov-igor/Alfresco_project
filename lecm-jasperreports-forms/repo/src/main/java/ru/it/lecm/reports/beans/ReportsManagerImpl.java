package ru.it.lecm.reports.beans;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.reports.api.ReportGenerator;
import ru.it.lecm.reports.api.ReportsManager;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.api.model.ReportType;
import ru.it.lecm.reports.api.model.DAO.ReportContentDAO;
import ru.it.lecm.reports.api.model.DAO.ReportContentDAO.ContentEnumerator;
import ru.it.lecm.reports.api.model.DAO.ReportContentDAO.IdRContent;
import ru.it.lecm.reports.api.model.DAO.ReportEditorDAO;
import ru.it.lecm.reports.generators.XMLMacroGenerator;
import ru.it.lecm.reports.utils.Utils;
import ru.it.lecm.reports.xml.DSXMLProducer;

public class ReportsManagerImpl implements ReportsManager {

	static final transient Logger logger = LoggerFactory.getLogger(ReportsManagerImpl.class);

	// final static String REPORT_TEMPLATE_FILES_BASEDIR = "/reportdefinitions";
	// final static String REPORT_DS_FILES_BASEDIR = REPORT_TEMPLATE_FILES_BASEDIR+ "/ds-config";

	// final static String DEFAULT_GENXMLTEMPLATE_BASEDIR = REPORT_TEMPLATE_FILES_BASEDIR + "/templates";

	final public static String DEFAULT_REPORT_TYPE = ReportType.RTYPE_MNEMO_JASPER;
	final public static String DEFAULT_REPORT_EXTENSION = ".jrxml";
	final public static String DEFAULT_REPORT_TEMPLATE = "jreportCommonTemplate.jrxml.gen";

	/**
	 * Описатеть умолчаний для типа отчёта.
	 */
	public static class ReportDefaultsDesc {

		private String generationTemplate, fileExtension;

		public ReportDefaultsDesc() {
			super();
		}

		public ReportDefaultsDesc(String fileExtension, String generationTemplate) {
			super();
			this.fileExtension = fileExtension;
			this.generationTemplate = generationTemplate;
		}

		/**
		 * Расширение шаблона отчёта (с точкой в начале). Например, для Jasper это ".jrxml"
		 * @return
		 */
		public String getFileExtension() {
			return fileExtension;
		}

		public void setFileExtension(String fileExtension) {
			this.fileExtension = fileExtension;
		}

		/**
		 * Шаблон для генерации шаблона отчёта ("шаблон шаблона")
		 * @return
		 */
		public String getGenerationTemplate() {
			return generationTemplate;
		}

		public void setGenerationTemplate(String template) {
			this.generationTemplate = template;
		}
	}

	/**
	 * Список зарегистрирванных отчётов
	 */
	private Map<String, ReportDescriptor> descriptors;

	/** список дескрипторов явно заданных бинами */
	private Map<String, ReportDescriptor> beanDescriptors;

	private ReportEditorDAO reportDAO;
	private ReportContentDAO contentRepositoryDAO; // файлы как "cm:content" в репозитории
	private ReportContentDAO contentFileDAO; // файлы в файловой системе (поставка)
	private ReportContentDAO templateFileDAO; // файлы шаблонов для генерации шаблонов отчётов

	// private String defaultGenTemplate = DEFAULT_JRXMLFILENAME; // шаблон для генерации xml-шаблона отчёта
	// private String defaultGenReportType = DEFAULT_REPORT_TYPE; // тип отчёта по-умолчанию
	// private Map< /*ReportType*/ String, String> defaultTemplates; // список шаблонов по-умолчанию

	// генераторы отчётов по типам
	private Map< /*ReportType*/ String, ReportGenerator> reportGenerators;

	// Map<КодТипаОтчёта, [Провайдер,Расширение,Шаблон]>
	private Map< /*ReportType*/ String, ReportDefaultsDesc> reportDefaults;

	private NamespaceService namespaceService;

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	@Override
	public ReportEditorDAO getReportDAO() {
		return reportDAO;
	}

	public void setReportDAO(ReportEditorDAO reportDAO) {
		this.reportDAO = reportDAO;
	}

	/**
	 * @return хранилище файлов в репозитории
	 */
	public ReportContentDAO getContentRepositoryDAO() {
		return contentRepositoryDAO;
	}

	public void setContentRepositoryDAO(ReportContentDAO value) {
		logger.debug(String.format( "contentRepositoryDAO assigned: %s", value));
		this.contentRepositoryDAO = value;
	}

	/**
	 * @return хранилище в виде файлов (для отчётов, идущих с поставкой)
	 */
	public ReportContentDAO getContentFileDAO() {
		return contentFileDAO;
	}

	public void setContentFileDAO(ReportContentDAO value) {
		logger.debug(String.format( "contentFileDAO assigned: %s", value));
		this.contentFileDAO = value;
	}

	/**
	 * Файлы шаблонов для генерации шаблонов отчётов
	 * @return
	 */
	public ReportContentDAO getTemplateFileDAO() {
		return templateFileDAO;
	}

	public void setTemplateFileDAO(ReportContentDAO value) {
		logger.debug(String.format( "templateFileDAO assigned: %s", value));
		this.templateFileDAO = value;
	}

	@Override
	public List<ReportDescriptor> getRegisteredReports() {
		return getRegisteredReports(null, null);
	}

	@Override
	public List<ReportDescriptor> getRegisteredReports(String[] docTypes,
			boolean forCollection) {
		final Set<ReportDescriptor> unFilteredReports = new HashSet<ReportDescriptor>();
		if (docTypes != null) {
			// указаны типы отчётов
			for (String docType : docTypes) {
				if (docType != null && docType.length() > 0) {
					if (docType.startsWith(String.valueOf(QName.NAMESPACE_BEGIN))) {
						docType = QName.createQName(docType).toPrefixString(namespaceService);
					}
					unFilteredReports.addAll(getRegisteredReports(docType, null));
				}
			}
		} else {
			unFilteredReports.addAll(getRegisteredReports(null, null));
		}

		final List<ReportDescriptor> resultedReports = new ArrayList<ReportDescriptor>();
		for (ReportDescriptor descriptor : unFilteredReports) {
			if (descriptor.getFlags().isCustom() && forCollection)
				// пропускаем кастомизированные для многострочных отчётов ...
				continue;
			if (forCollection == descriptor.getFlags().isMultiRow()) {
				resultedReports.add(descriptor);
			}
		}
		return resultedReports;
	}

//	public DsLoader getDsloader() {
//		return ModelLoader.getInstance();
//	}

	/**
	 * Дескрипторы, заданные бинами
	 * @return
	 */
	public Map<String, ReportDescriptor> getBeanDescriptors() {
		if (this.beanDescriptors == null) {
			this.beanDescriptors = new HashMap<String, ReportDescriptor>();
		}
		return this.beanDescriptors; 
	}

	/**
	 * Получить список продеплоенных дескрипторов.
	 * В него входят:
	 *    1) дискрипторы, заданные бмнами,
	 *    2) дескрипторы, имеющиеся в виде файлов,
	 *    3) продеплоенные в хранилище дескрипторы.
	 * (в этом списке чем ниже тем приоритетнее)
	 * @return
	 */
	public Map<String, ReportDescriptor> getDescriptors() {
		if (this.descriptors == null) {
			this.descriptors = new HashMap<String, ReportDescriptor>();
			this.descriptors.putAll(this.getBeanDescriptors());
			scanResources();
		}
		return this.descriptors;
	}

	/**
	 * @return не NULL список [ReportType.Mnem -> File Extension+Template+Generator]
	 */
	public Map<String, ReportDefaultsDesc> getReportDefaults() {
		if (this.reportDefaults == null) {
			this.reportDefaults = new HashMap<String, ReportDefaultsDesc>();
			// final ReportDefaultsDesc jdesc = new ReportDefaultsDesc(DEFAULT_REPORT_EXTENSION, DEFAULT_REPORT_TEMPLATE);
			// this.reportDefaults.put(DEFAULT_REPORT_TYPE, jdesc);
		}
		return this.reportDefaults;
	}

	/**
	 * @return не NULL список [ReportType.Mnem -> ReportGenerator]
	 */
	@Override
	public Map</* ReportType */String, ReportGenerator> getReportGenerators() {
		if (reportGenerators == null)
			reportGenerators = new HashMap<String, ReportGenerator>(1);
		return reportGenerators;
	}

	/**
	 * Задать соот-вие типов отчётов и их провайдеров
	 * @param map список [ReportType.Mnem -> ReportGenerator]
	 */
	@Override
	public void setReportGenerators(Map<String, ReportGenerator> map) {
		this.reportGenerators = map;
	}

	@Override
	public String getReportTypeTag(ReportType rtype) {
		return (	rtype != null
					&& rtype.getMnem() != null
					&& rtype.getMnem().trim().length() > 0) 
				? rtype.getMnem().trim() 
				: DEFAULT_REPORT_TYPE;
	}

	/**
	 * Просканировать указанное хранилище на наличие дескрипторов отчётов и загрузить их
	 * @param destMap целевой список
	 * @param repos
	 * @return кол-во загруженных описаний
	 */
	private static int scanRepository( final Map<String, ReportDescriptor> destMap
			, final ReportContentDAO repos)
	{
		if (repos == null)
			return 0;

		final List<IdRContent> found = new ArrayList<IdRContent>();
		final ContentEnumerator doEnum = new ReportContentDAO.ContentEnumerator() {
			@Override
			public void lookAtItem(IdRContent id) {
				try { // try-catch wraping
					final boolean isDsXml = DSXMLProducer.isDsConfigFileName(id.getFileName());
					if (!isDsXml) // skip none-descriptor files ...
						return;

					logger.debug(String.format( "loading report descriptor from '%s' ...", id));
					final InputStream in = repos.loadContent(id).getContentInputStream();
					try {
						// загружаем описатеть из файла
						try { // try-catch wraping
							final ReportDescriptor desc = DSXMLProducer.parseDSXML(in, id.getFileName());
							if (desc == null)
								return;
							if (desc.getMnem() == null || desc.getMnem().trim().length() == 0)
								// задать название название шаблона по-умолчанию как в Id...
								desc.setMnem(id.getReportMnemo());
							else if (!id.getReportMnemo().equalsIgnoreCase(desc.getMnem()))
								logger.warn(String.format(
										"Loaded report has custom mnemonic:\n\t by id '%s'\n\t loaded with mnem '%s'"
										, id, desc.getMnem()
								));
							destMap.put(desc.getMnem(), desc); // (!) найден очередной
							found.add(id); // запоминаем только при отсутствии ошибок

							logger.debug(String.format("... loaded deployed report descriptor '%s' from '%s'", desc.getMnem(), id));
						} catch (Throwable ex) {
							logger.error( String.format( 
									"Problem parsing deployed ds-xml by id='%s' -> report ignored\n%s"
									, id, ex.getMessage()), ex);
						}
					} finally {
						IOUtils.closeQuietly(in);
					} // finally
				} catch (Throwable ex) {
					// log and skip it
					logger.error(String.format("Fail to load ds-xml file from '%s'\n\t %s\n\t -> ignored", id, ex.getMessage()) );
				}
			}
		};

		logger.info(String.format("Auto scanning reports at root '%s' ...", repos.getRoot()));
		repos.scanContent( doEnum);
		logger.info(String.format("... Found %s reports by auto scanning at root '%s'", found.size(), repos.getRoot()));
		return found.size();
	}

	/**
	 * Выполнить сканирование и загрузку описателей существующих отчётов ...
	 * 
	 * @return кол-во найденных файлов отчётов
	 */
	private int scanResources() {

		// @NOTE: (!) здесь вызов getDescriptors(); зациклит 
		if (this.descriptors == null) {
			this.descriptors = new HashMap<String, ReportDescriptor>();
			this.descriptors.putAll(this.getBeanDescriptors());
		}

		// final List<URL> found = JRLoader.getResources( getDsRelativeFileName("*")); // REPORT_DS_FILES_BASEDIR + "/ds-*.xml"
		int ifound = 0;
		ifound += scanRepository(this.descriptors, this.contentFileDAO);
		ifound += scanRepository(this.descriptors, this.contentRepositoryDAO);
		return ifound;
	}

	public void setDescriptors(List<ReportDescriptor> list) {
		// this.descriptors = list;
		this.descriptors = null;
		if (list != null)
			for (ReportDescriptor desc : list)
				registerReportDescriptor(desc);
	}

	public void init() {
		// {
		// 	final File result = new File( getDsConfigDir() + "/test-subdir");
		// 	logger.info( String.format( "creating dir '%s'\n\t%s", result, result.mkdirs()) );
		// }
		// logger.info( Utils.dumpAlfData( System.getProperties(), "System.getProperties():\n").toString() );
		if (getDescriptors() != null && getDescriptors().size() > 0) {
			if (logger.isInfoEnabled()) // отобразить только названия шаблонов ...
				logger.info(String.format(
						" initialized templates count %s\n\t%s",
						getDescriptors().size(),
						Utils.getAsString(getDescriptors().keySet(), "\n\t")));
			else if (logger.isDebugEnabled()) // целиком загруженные шаблоны ...
				logger.debug(String.format(
						" initialized templates count %s\n\t%s",
						getDescriptors().size(),
						Utils.getAsString(getDescriptors().values(), "\n\t")));
		}
	}

	/**
	 * Получить дексриптор отчёта по его мнемонике или вернуть null. Поиск
	 * ведётся по зарегистрированным отчётам в this.descriptors и в БД.
	 * @param reportMnemoName дескриптор
	 * @return описатеть отчёта или null, если не найден
	 */
	@Override
	public ReportDescriptor getRegisteredReportDescriptor(String reportMnemoName) {

		if (getDescriptors().containsKey(reportMnemoName)) {
			final ReportDescriptor d = this.descriptors.get(reportMnemoName);
			return d; // FOUND by Mnemonic
		}

		// попытка загрузить DAO-объект
		// TODO: после автоподъёма at boot-time файлов ds-xml, здесь уже не понадобится
		if (reportDAO != null) {
			final ReportDescriptor d = reportDAO.getReportDescriptor(reportMnemoName);
			if (d != null) {
				if (logger.isDebugEnabled()) {
					logger.debug(String.format(
						"Load Report Editor template descriptor with mnem '%s' as:\n%s",
						reportMnemoName, d));
				} else {
					logger.info( String.format(
							"Load Report Editor template descriptor with mnem '%s'",
							reportMnemoName));
				}
				return d; // FOUND by DAO mnemonic
			}
		}

		logger.warn(String.format( "Report '%s' has no descriptor", reportMnemoName));
		return null; // NOT FOUND
	}

	@Override
	public void registerReportDescriptor(NodeRef rdescId) {
		PropertyCheck.mandatory(this, "reportDAO", getReportDAO());
		final ReportDescriptor rdesc =
				getReportDAO().getReportDescriptor(rdescId);
		registerReportDescriptor(rdesc);
	}

	@Override
	public void registerReportDescriptor(ReportDescriptor desc) {
		if (desc != null) {
			checkReportDescData(desc);
			setDefaults(desc);
			createDsFile(desc); // создание ds-xml
			saveReportTemplate(desc); // сохранение шаблона отчёта
			getDescriptors().put(desc.getMnem(), desc);
			logger.info( String.format(
					"Report descriptor with name '%s' registered",
					desc.getMnem()));
		}
	}

	private void setDefaults(ReportDescriptor desc) {
		if (Utils.isStringEmpty(desc.getReportType().getMnem())) {
			desc.getReportType().setMnem( DEFAULT_REPORT_TYPE);
			logger.warn(String.format(
					"Report '%s' has empty report type -> set to '%s'",
					desc.getMnem(), desc.getReportType().getMnem()));
		}
		// TODO: ввести зависимость от типа очёта
		final ReportDefaultsDesc def = this.getReportDefaults().get(desc.getMnem());

		if (desc.getReportTemplate().getFileName() == null) { // задать default-название файла
			final String ext = (def != null) ? def.getFileExtension() : DEFAULT_REPORT_EXTENSION;
			desc.getReportTemplate().setFileName(desc.getMnem() + ext);
			logger.warn(String.format(
					"Report '%s' has empty template FileName -> set to '%s'",
					desc.getMnem(), desc.getReportTemplate().getFileName()));
		}
		if (desc.getReportTemplate().getData() == null) {
			// сгенерировать xml отчёта по gen-шаблону ...
			final ByteArrayOutputStream stm = this.generateReportTemplate(desc);
			desc.getReportTemplate().setData(
					(stm == null) ? null : new ByteArrayInputStream(stm.toByteArray()));
			logger.warn(String.format(
						"Report '%s' has empty template data -> generated from '%s'"
						, desc.getMnem()
						, (def != null) ? def.getGenerationTemplate() : DEFAULT_REPORT_TEMPLATE // // makeGenTemplateFileName(...)
			));
		}
	}

	private boolean saveReportTemplate(ReportDescriptor desc) {
		if (desc.getReportTemplate() == null) {
			logger.warn(String.format( "Report '%s' has no template", desc.getMnem()));
			return false;
		}

		/*
		 * NOTE: если требуется жёсткая проверка наличия данных:
		 * if (desc.getReportTemplate().getData() == null) {
		 * 		logger.warn(String.format( "Report '%s' has no template data", desc.getMnem()));
		 * 		return false; 
		 * }
		 * if (desc.getReportTemplate().getFileName() == null) {
		 * 		logger.warn(String.format( "Report '%s' has empty template FileName", desc.getMnem())); 
		 * 		return false;
		 * }
		 */

		final String rtag = getReportTypeTag(desc.getReportType());
		final ReportDefaultsDesc def = getReportDefaults().get(rtag);

		/* сохранение в репозиторий "шаблона отчёта"... */
		try {
			final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

			if (desc.getReportTemplate() != null) {
				final InputStream stm = desc.getReportTemplate().getData();
				if (stm != null) {
					stm.reset();
					IOUtils.copy(stm, byteStream);

					final String ext = (def != null) ? def.getFileExtension() : DEFAULT_REPORT_EXTENSION;
					final IdRContent id = IdRContent.createId(
								desc, String.format("%s%s", desc.getMnem(), ext)
						);

					this.contentRepositoryDAO.storeContent(id, new ByteArrayInputStream(byteStream.toByteArray()));
				}
			}

			/*
			 * оповещение соот-го провайдера (компиляция) ...
			 * т.к. деплоить можно только отчёты в репозиторий, здесь указываем 
			 * именно его (не требуется определять откуда получен описатель - из 
			 * файлового хранилища или из репозитория)
			 */
			// getReportGenerators().get(rtag).onRegister(outFullName, desc);
			getReportGenerators().get(rtag).onRegister(desc, byteStream.toByteArray(), this.contentRepositoryDAO);
			logger.debug(String.format("Report '%s': provider notified", desc.getMnem()));

		} catch (Throwable ex) {
			final String msg = String.format(
					"Error saving template content for Report Descriptor %s"
					, desc.getMnem());
			logger.warn( msg, ex);
			throw new RuntimeException( msg, ex);
		}

		return true;
	}

	@Override
	public void unregisterReportDescriptor(String reportCode) {
		if (reportCode != null) {
			if (this.descriptors != null && this.descriptors.containsKey(reportCode)) {
				final ReportDescriptor desc = this.descriptors.get(reportCode);

				// удаляем из репозитория contentRepositoryDAO
				// (!) из файловых не убираем никогда
				this.contentRepositoryDAO.delete( new IdRContent( desc.getReportType(), reportCode, "*"));
				// removeDsFile(reportCode); // удаляем файл

				// убираем из списка активных ...
				this.descriptors.remove(reportCode);

				logger.debug( String.format( "Report descriptor with name '%s' unregistered", reportCode));
			} else
				logger.warn( String.format( "Report descriptor with code '%s' NOT exists", reportCode));
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
			final IdRContent idds = DSXMLProducer.makeDsXmlId(desc);
			try {
				// файл создадим только в репозитории (но не в файловом хранилище) ...
				this.contentRepositoryDAO.storeContent(idds, new ByteArrayInputStream(dsxml.toByteArray()));
			} catch (Throwable ex) {
				final String msg = String.format(
						"Report '%s': error saving ds-xml into storage by id '%s'", desc.getMnem(), idds);
				logger.error(msg, ex);
				throw new RuntimeException(msg, ex);
			}
		}
	}

	/**
	 * Выполнить проверку данных. Поднять исключения при неверном/недостаточном
	 * заполнении.
	 * @param desc
	 */
	private void checkReportDescData(ReportDescriptor desc) {
		if (desc.getMnem() == null || desc.getMnem().trim().length() == 0)
			throw new RuntimeException(
					String.format("Report descriptor must have mnemo code"));
	}

	@Override
	public byte[] loadDsXmlBytes(String reportCode) {
		// PropertyCheck.mandatory(this, "dsloader", getDsloader());
		PropertyCheck.mandatory(this, "contentFileDAO", getContentFileDAO());
		PropertyCheck.mandatory(this, "contentRepositoryDAO", getContentRepositoryDAO());

		final ReportDescriptor desc = this.getRegisteredReportDescriptor(reportCode);
		final IdRContent idDS = DSXMLProducer.makeDsXmlId(desc);
		ContentReader result = null;
		if (contentRepositoryDAO.exists(idDS)) {
			// описатеть имеется в репозитории продеплоенных ...
			result = contentRepositoryDAO.loadContent(idDS);
		} else if (contentFileDAO.exists(idDS)) {
			// описатель имеется в стд файловом хранении ...
			result = contentFileDAO.loadContent(idDS);
		} else { 
			// непонятно где хранится ...
			final String msg = String.format( "ds-xml cannot be loaded for report '%s':\n\t by id {%s}\n\t (!) not found at repository storage nor at file storage", reportCode, idDS);
			logger.error(msg);
			throw new RuntimeException(msg);
		}

		if (result == null)
			return null;

		final ByteArrayOutputStream os = new ByteArrayOutputStream( (int) result.getSize());
		result.getContent(os);
		return os.toByteArray();
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
	public ByteArrayOutputStream generateReportTemplate(ReportDescriptor reportDesc)
	{
		if (reportDesc == null)
			return null;

		PropertyCheck.mandatory(this, "templateFileDAO", getTemplateFileDAO());
		PropertyCheck.mandatory(this, "reportDefaults", getReportDefaults());

		final IdRContent id = IdRContent.createId(reportDesc, null);
		{
			final String reportTypeTag = getReportTypeTag(reportDesc.getReportType());
			final ReportDefaultsDesc defaults
					= this.getReportDefaults().get( reportTypeTag);

			// название файла-шаблона для генерации
			final String templateFileName = (defaults != null)
							? defaults.getGenerationTemplate()
							: DEFAULT_REPORT_TEMPLATE;
			id.setFileName(templateFileName);
		}

		final XMLMacroGenerator xmlGenerator = new XMLMacroGenerator(reportDesc);
		final ContentReader reader = this.getTemplateFileDAO().loadContent(id);
		InputStream fin = null;
		try {
			fin = reader.getContentInputStream();
			final ByteArrayOutputStream result = xmlGenerator.xmlGenerateByTemplate(id.getFileName(), fin);
			return result;
		} catch (Throwable ex) {
			final String msg = String.format( "Report '%s': get generated template '%s' problem\n\t%s"
					, reportDesc.getMnem(), id.getFileName(), ex.getMessage());
			logger.error(msg, ex);
			throw new RuntimeException(msg, ex);
		} finally {
			if (fin != null)
				IOUtils.closeQuietly(fin);
		}
	}

	@Override
	public List<ReportDescriptor> getRegisteredReports( String docType,
			String reportType)
	{
		final Map<String, ReportDescriptor> list = this.getDescriptors();
		if (list == null || list.isEmpty())
			return new ArrayList<ReportDescriptor>();

		if (docType != null && docType.length() == 0) {
			docType = null;
		}

		if (reportType != null && reportType.length() == 0) {
			reportType = null;
		}

		if (docType == null && reportType == null) {
			// не задано фильтрование -> вернуть сразу всё целиком ...
			return new ArrayList<ReportDescriptor>(list.values());
		}

		final List<ReportDescriptor> found = new ArrayList<ReportDescriptor>();
		for (ReportDescriptor desc : list.values()) {
			final boolean okDocType = (docType == null) // не задан фильтр по типам доков
					|| ((desc.getFlags() == null)
					|| desc.getFlags().getPreferedNodeType() == null) // нет флажков у шаблона -> подходит к любому
					|| docType.equalsIgnoreCase(desc.getFlags().getPreferedNodeType()) // совпадение типа (если заданы искомый тип и флажки)
			;

			final boolean okRType = (reportType == null) // не задан фильтр по типам отчётов
					|| ((desc.getReportType() == null)
					|| desc.getReportType().getMnem() == null) // не задан тип отчёта шаблона -> подходит к любому
					|| reportType.equalsIgnoreCase(desc.getReportType().getMnem()) // совпадение типа
			;
			if (okDocType && okRType) {
				found.add(desc);
			}
		}
		// return (found.isEmpty()) ? null : found;
		return found;
	}

	@Override
	public ReportContentDAO findContentDAO(ReportDescriptor desc) {
		if (desc == null)
			return null;

		final IdRContent idDS = DSXMLProducer.makeDsXmlId(desc);
		ReportContentDAO result = null;
		if (contentRepositoryDAO.exists(idDS)) {
			// описатеть имеется в репозитории продеплоенных ...
			result = contentRepositoryDAO;
		} else if (contentFileDAO.exists(idDS)) {
			// описатель имеется в стд файловом хранении ...
			result = contentFileDAO;
		}
		return result;
	}

}
