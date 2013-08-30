package ru.it.lecm.reports.generators;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDataSourceProvider;
import net.sf.jasperreports.engine.JRException;
import net.sf.jooreports.openoffice.connection.OpenOfficeConnection;

import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.WebScriptResponse;

import ru.it.lecm.reports.api.JasperReportTargetFileType;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.api.model.DAO.ReportContentDAO;
import ru.it.lecm.reports.api.model.DAO.ReportContentDAO.IdRContent;
import ru.it.lecm.reports.model.DAO.FileReportContentDAOBean;
import ru.it.lecm.reports.ooffice.OpenOfficeFillManager;
import ru.it.lecm.reports.utils.Utils;

import com.sun.star.beans.XPropertyContainer;
import com.sun.star.beans.XPropertySet;
import com.sun.star.beans.XPropertySetInfo;
import com.sun.star.document.XDocumentInfo;
import com.sun.star.document.XDocumentInfoSupplier;
import com.sun.star.lang.XComponent;
import com.sun.star.ucb.XFileIdentifierConverter;
import com.sun.star.uno.UnoRuntime;

public class OOfficeReportGeneratorImpl extends ReportGeneratorBase {

	private static final transient Logger logger = LoggerFactory.getLogger(OOfficeReportGeneratorImpl.class);

	// private FileReportContentDAOBean templatesDAO;
	private FileReportContentDAOBean resultDAO;
	private OpenOfficeConnection connection;

	/** true, если openOffice доступен и connection.connected */
	private boolean ooAvailable = false;
	private boolean ooConnectedStrictly = false;
	private int maxConnectionRetries = 3; 

	@Override
	public void init() {
		super.init();
		checkConnection();
	}

	/**
	 * true, если openOffice доступен и connection.connected
	 * при проверке, если соединение отсутствует и {@link #ooConnectedStrictly is true}
	 * , то поднимается исключение, иначе {@link #ooAvailabe} принимает false.
	 */
	public boolean isOoAvailable() {
		return ooAvailable;
	}

	/**
	 * true, если надо строго поддерживать соединение
	 */
	public boolean isOoConnectedStrictly() {
		return ooConnectedStrictly;
	}

	public void setOoConnectedStrictly(boolean ooConnectedStrictly) {
		this.ooConnectedStrictly = ooConnectedStrictly;
	}

	/**
	 * максимальное кол-во дополнительных попыток соединиться с OOffice
	 * (одна = один раз основная проверка + одна дополнительная)
	 */
	public int getMaxConnectionRetries() {
		return maxConnectionRetries;
	}

	public void setMaxConnectionRetries(int maxConnectionRetries) {
		this.maxConnectionRetries = maxConnectionRetries;
	}

	//	/** репа для шаблонов отчётов OpenOffice */
	//	public FileReportContentDAOBean getTemplatesDAO() {
	//		return templatesDAO;
	//	}
	//
	//	public void setTemplatesDAO(FileReportContentDAOBean templatesDAO) {
	//		this.templatesDAO = templatesDAO;
	//	}

	/** 
	 * репа для результатов отчётов OpenOffice
	 * Файловая, т.к. openOffice работает с файлами
	 */
	public FileReportContentDAOBean getResultDAO() {
		return resultDAO;
	}

	public void setResultDAO(FileReportContentDAOBean resultDAO) {
		this.resultDAO = resultDAO;
	}

	public OpenOfficeConnection getConnection() {
		return connection;
	}

	public void setConnection(OpenOfficeConnection connection) {
		this.connection = connection;
	}

	/**
	 * Здесь (openOffice) ничего дополнительно не требуется.
	 */
	@Override
	public void onRegister(ReportDescriptor desc, byte[] templateData,
			ReportContentDAO storage)
	{
		logger.info( String.format( "decriptor deploy notification /'%s'/  \"%s\"", desc.getMnem(), desc.getDefault()));
		// openOfficeGenerateExecute(desc, storage);
	}

	private String toUrl(File file, OpenOfficeConnection connection) throws ConnectException
	{
		final Object fcProvider = connection.getFileContentProvider();
		XFileIdentifierConverter fic = (XFileIdentifierConverter)
				UnoRuntime.queryInterface( XFileIdentifierConverter.class, fcProvider);
		return fic.getFileURLFromSystemPath("", file.getAbsolutePath());
	}


	private final static Object _lockerUniqueGen = new Object();

	/** максимальное кол-во попыток сохранить временный файл ("забить место") */
	private final static int MAX_CREATE_FILE_RETRY = 10;

	/**
	 * Сгенерировать уникальное название на основании маски и (!) создать пустой 
	 * файл с этим именем ("застолбить" его). Генерируется имя файла вида:
	 * 		".../"+ кодОтчёта+ "-YYYY-MM-DD-hh-mm-ss" + nameFmtSuffix_s 
	 * (!) надо иметь в виду, что если файл не сохранить на диске, то одно и то
	 * же имя может быть сгенерировано многократно в разных потоках и ошиби 
	 * начнут возникать только, когда потоки начнут писать в эти одноимённые
	 * файлы. 
	 * @param desc описатель отчёта
	 * @param nameFmtSuffix_s маска с одним параметром, для подстановки подстрокиб
	 * чтобы обеспечить уникальность
	 * @return уникальное название файла, который (!) будет создан здесь как пустой
	 * @throws IOException 
	 */
	private File createNewUniquieFile( ReportDescriptor desc, String nameFmtSuffix_s) 
			throws IOException
			{
		// часть с текущим временем, после которой можно будем докручивать уникальность номером ...
		final String curBasePart = Utils.coalesce(desc.getMnem(), "report")
				+ "-"
				+ (new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss")).format(new java.util.Date());
		final IdRContent id = IdRContent.createId(desc, curBasePart); //здесь имя файла = код отчёта и дата-время его создания
		final File fbase = getResultDAO().makeAbsFilePath(id);

		// далее к имени будет добавляться генерируемая часть, до получения уникальности ... 
		File result; 
		synchronized (_lockerUniqueGen) {
			int iRetry = 0;
			do {
				if (iRetry++ >= MAX_CREATE_FILE_RETRY) {
					final String msg = String.format(
							"Fail to find empty file name in %s retries,\n\t tring file mask was '%s'\n\t possibly folder is ReadOnly or too much users are now connected \n\t retry the action later"
							, MAX_CREATE_FILE_RETRY, fbase.getAbsolutePath() + nameFmtSuffix_s);
					logger.error( msg);
					throw new RuntimeException( msg);
				}
				result = Utils.findEmptyFile( fbase, nameFmtSuffix_s); // к имени добавляем генерируемую часть
			} while (!result.createNewFile()); // крутим генерацию пока уникальный файл не создадим
		}
		return result;
			}

	/**
	 * Загрузить данные из файла как байтовый блок.
	 * IO-исключения обёрнуты как rtm-exceptions.
	 * @param srcFile исходный файл для загрузки
	 * @param errLogInfo сообщение при ошибках
	 * @return
	 */
	static byte[] loadFileAsData (File srcFile, String errLogInfo) 
	{
		try {
			return Utils.loadFileAsData(srcFile, errLogInfo); 
		} catch (IOException ex) {
			final String msg = Utils.coalesce(errLogInfo, "") + ex.getMessage();
			logger.error( msg, ex);
			throw new RuntimeException( msg, ex);
		}
	}


	/** файловое расширение для openOffice-файла */
	private static final String OO_FILEEXT = ".odt";

	/** файловое расширение для временного openOffice-файла */
	private static final String TEMP_OO_FILEEXT = "_tmp" + OO_FILEEXT;

	/** файловая маска-суффикс для временного openOffice-файла Макета Шаблона */
	// private static final String FMT_SFX_TEMP_TEMPLATE_FILENAME_S = "-report_maketTemplate_%s"+ TEMP_OO_FILEEXT;

	/** файловая маска-суффикс для временного openOffice-файла генерируемого Шаблона Отчёта */
	private static final String FMT_SFX_TEMP_RESULT_FILENAME_S = "-report_ready_%s" + TEMP_OO_FILEEXT;

	/** файловая маска-суффикс для обычного генерируемого openOffice-отчёта */
	private static final String FMT_SFX_RESULT_FILENAME_S = "-report_ready_%s" + OO_FILEEXT;

	@Override
	public byte[] generateReportTemplateByMaket(final byte[] maketData, final ReportDescriptor desc)
	{
		// обмен идёт через файловое хранилище - openOffice работает с файлами ...
		PropertyCheck.mandatory(this, "connection", getConnection());
		// PropertyCheck.mandatory(this, "templatesDAO", getTemplatesDAO());
		PropertyCheck.mandatory(this, "resultDAO", getResultDAO());

		try {
			/**
			 * здесь ожидаем, что входной поток имеет формат документа OpenOffice ("*.odt")
			 * выполняем сохранение maketData во временный файл (потом удаляем его), чтобы с ним нормально отработал OpenOffice
			 * результат получаем в виде того же временного файла, которой читаем и удаляем
			 */
			// File ooFileTemplate = null, ooFileResult = null;
			File ooFile = null;
			try {
				// генерация уникального названия для входного шаблона (после операции - удалим)
				// ooFileTemplate = createNewUniquieFile( desc, FMT_SFX_TEMP_TEMPLATE_FILENAME_S);

				// генерация уникального названия для результата (после операции - удалим)
				ooFile = createNewUniquieFile( desc, FMT_SFX_TEMP_RESULT_FILENAME_S);
				Utils.saveDataToFile(ooFile, maketData);

				final File workFile = ooFile;

				final byte[] result = 
						openOfficeExecWithRetry(new Job<byte[]>() {

							@Override
							protected byte[] doIt() throws ConnectException {
								// входной шаблон
								if (!workFile.exists()) {
									logger.warn(String.format( "file not found by id {%s}", workFile));
									return null;
								}
								/* выходной - "обычный" файлик OpenOffice */
								final String urlWork = toUrl(workFile, connection);
								final String urlSaveAs = null; // если надо задать название файла другим ...

								final String author = "lecm user";

								/* Добавление атрибутов из колонок данных и сохранение в urlSave... */
								final OpenOfficeTemplateGenerator ooGen = new OpenOfficeTemplateGenerator();
								ooGen.odtAddColumnsAsDocCustomProps(getConnection(), desc, urlWork, urlSaveAs, author);

								/** чтение файла в виде буфера ... */
								final String errLogInfo = String.format( "Fail to load generated [temporary] file: \n '%s'", urlWork);
								return loadFileAsData(workFile, errLogInfo);
							}
						});

				logger.info( String.format( "Successfully generated template for report '%s':\n\t data size %s"
						, desc.getMnem(), (result == null ? "NULL" : result.length) ));
				return result;
			} finally {
				if (ooFile != null)
					ooFile.delete();
			}
		} catch (IOException ex) {
			final String msg = String.format( "Error generating template of report '%s':\n%s", desc.getMnem(), ex.getMessage());
			logger.error( msg, ex);
			throw new RuntimeException( msg, ex);
		}
	}

	@Override
	public void produceReport(
			WebScriptResponse webScriptResponse
			, ReportDescriptor reportDesc
			, Map<String, String[]> parameters
			, ReportContentDAO rptContent
			) throws IOException
	{
		PropertyCheck.mandatory (this, "services", getServices());
		PropertyCheck.mandatory (this, "reportsManager", getReportsManager());
		PropertyCheck.mandatory (this, "reportsDesc", reportDesc);
		PropertyCheck.mandatory (this, "reportsDesc.providerDesc", reportDesc.getProviderDescriptor());

		PropertyCheck.mandatory(this, "connection", getConnection());
		PropertyCheck.mandatory(this, "resultDAO", getResultDAO());
		// PropertyCheck.mandatory(this, "templatesDAO", getTemplatesDAO());

		logger.debug( String.format( "producing report /'%s'/ \"%s\"", reportDesc.getMnem(), reportDesc.getDefault()));


		/* Получение данных шаблона отчёта */
		final ContentReader reader;
		{
			final String reportTemplateFileName = // имя файла openOffice с шаблоном документа
					String.format( "%s%s", reportDesc.getMnem(), OO_FILEEXT);

			reader = rptContent.loadContent( IdRContent.createId(reportDesc, reportTemplateFileName));
			if (reader == null)
				throw new IOException( String.format("Report is missed - file '%s' not found", reportTemplateFileName ));
		}

		OutputStream outputStream = null;
		File ooFile = null; // файл с шаблоном
		File ooResultFile = null; // готовый файл с правильным расширением
		try {
			ooFile = createNewUniquieFile( reportDesc, FMT_SFX_RESULT_FILENAME_S);
			Utils.saveDataToFile(ooFile, reader.getContentInputStream());

			final JasperReportTargetFileType target = JasperReportTargetFileType.RTF;
			// конечное название файла
			ooResultFile = createNewUniquieFile( reportDesc, "%s"+ target.getExtension());

			try {
				final String reportResultFileName = FilenameUtils.getName(ooResultFile.getAbsolutePath());

				// отдельные параметры в запросе ...
				// final Type1 arg1 = getArg1(parameters);

				webScriptResponse.setContentType( String.format(
						"%s;charset=UTF-8;filename=%s"
						, target.getMimeType()
						, reportResultFileName 
						));
				outputStream = webScriptResponse.getOutputStream();
				try {
					/* создание Провайдера */
					final String dataSourceClass = reportDesc.getProviderDescriptor().className(); // report.getProperty("dataSource");
					final JRDataSourceProvider dsProvider = super.createDsProvider(reportDesc, dataSourceClass, parameters);

					/* построение отчёта */
					final byte[] result = generateReport(ooFile, ooResultFile, target, reportDesc, dsProvider, parameters);
					if (result != null)
						IOUtils.write(result, outputStream);
					else
						logger.warn( String.format( "Report '%s' got empty result", reportDesc.getMnem()));

				} finally {
					if (outputStream != null) {
						outputStream.flush();
						outputStream.close();
					}
				} 
			} finally {
				if (ooResultFile != null)
					ooResultFile.delete();
			} 
		} catch (Throwable e) { // (JRException e) {
			final String msg = String.format( "Fail to build openOffice report '%s':\n\t%s", reportDesc.getMnem(), e);
			logger.error( msg, e);
			throw new IOException(msg, e);
		} finally {
			if (ooFile != null)
				ooFile.delete();
		}
	}

	/**
	 * Создание отчёта
	 * @param srcOOFile исходный файл с openOffice-шаблоном отчёта
	 * @param destDocFile целевой файл (например, *.rtf)
	 * @param target целевой формат (пока не поддерживается кроме rtf)
	 * @param reportDesc
	 * @param dsProvider
	 * @param parameters
	 * @throws JRException 
	 */
	private byte[] generateReport(
			final File srcOOFile
			, final File destDocFile
			, final JasperReportTargetFileType target
			, final ReportDescriptor report
			, final JRDataSourceProvider dsProvider
			, final Map<String, String[]> requestParameters
	) throws JRException
	{
		logger.debug("Generating report " + report.getMnem() + " ...");

		if (srcOOFile == null) {
			throw new IllegalArgumentException("The report file was not specified");
		}

		// final Map<String, Object> reportParameters = new HashMap<String, Object>();
		// reportParameters.putAll(requestParameters);

		final JRDataSource dataSource = dsProvider.create(null);

		// final byte[] result = filler.fill(report, requestParameters, dataSource, srcOOFile, destDocFile);
 		final byte[] result = 
				openOfficeExecWithRetry(new Job<byte[]>() {

					@Override
					protected byte[] doIt() throws ConnectException {
						// Выходной - "обычный" файлик OpenOffice ...
						final String urlSrc = toUrl(srcOOFile, connection);

						// Выходной файл ...
						final String urlSaveAs = toUrl(destDocFile, connection);

						// final String author = "lecm user";

						/* Добавление атрибутов из колонок данных и сохранение в urlSave... */
						final OpenOfficeFillManager filler = new OpenOfficeFillManager(getConnection()); // DefaultReportsContext.getInstance()
						try {
							filler.fill(report, requestParameters, dataSource, urlSrc, urlSaveAs);
						} catch (JRException ex) {
							final String msg = String.format( "Error filling report '%s':\n%s", report.getMnem(), ex.getMessage());
							logger.error( msg, ex);
							throw new RuntimeException( msg, ex);
						}

						/** чтение файла в виде буфера ... */
						final String errLogInfo = String.format( "Fail to load generated report file:\n '%s'\n", urlSrc);
						return loadFileAsData(destDocFile, errLogInfo);
					}
				});

		logger.info( String.format( "Report '%s' as %s generated succefully:\n\t report has %s bytes"
					, report.getMnem(), target, (result == null ? "NULL" : result.length)));
		return result;
	}

	private abstract class Job<TResult> {

		/** возвращаемое значение */
		private TResult result;

		private Job() {
			super();
		}

		public TResult getResult() {
			return this.result;
		}

		void doExec() throws ConnectException {
			this.result = doIt();
		} 

		protected abstract TResult doIt() throws ConnectException;
	}

	/**
	 * Выполнить указанное действие, с повторами при ошибках соединения
	 * @param todo
	 * @return
	 */
	protected <TResult> TResult openOfficeExecWithRetry(Job<TResult> todo) {
		int retryCount = 0;
		while (true) {

			Throwable lastEx;
			try {
				checkConnection();
				if (!getConnection().isConnected())
					throw new RuntimeException("Office connection is down");
				todo.doExec();
				return todo.getResult(); // (!) normal break of while
			} catch (com.sun.star.lang.DisposedException ex) {
				lastEx = ex;
			} catch (ConnectException connEx) {
				lastEx = connEx;
			} catch (Throwable ex) {
				final String msg = String.format("Exception at retry %s of %s:\n%s", retryCount, this.maxConnectionRetries, ex.getMessage());
				logger.error( msg, ex);
				throw new RuntimeException( msg, ex);
			}

			// если оказываемся здесь - идём на повторный retry-цикл
			final boolean canRetry = retryCount < this.maxConnectionRetries;
			retryCount++;

			try {
				this.ooAvailable = false;
				connection.disconnect();
			} catch(Throwable t) { // here there is no need to re-raise something ...
				logger.warn( String.format( "ignore dispose error:\n%s", t.getMessage()));
			}

			final String msg = (canRetry)
					? String.format("OpenOffice connection disposed -> retry %s of %s", retryCount, this.maxConnectionRetries)
							: String.format("Cannot restore OpenOffice connection in %s retries -> error", this.maxConnectionRetries);
					if (!canRetry) { // re-raise ...
						logger.error( msg, lastEx);
						throw new RuntimeException( msg, lastEx);
					}
					// go on retry
					logger.warn( msg);
		}
	}

	/**
	 * Сгенерировать openOffice отчёт
	 * @param desc
	 * @param storage
	 */
	private byte[] openOfficeGenerateExecute( ReportDescriptor desc, ReportContentDAO storage) {
		// PropertyCheck.mandatory(this, "templatesDAO", getTemplatesDAO());
		PropertyCheck.mandatory(this, "resultDAO", getResultDAO());
		PropertyCheck.mandatory (this, "reportsManager", getReportsManager());
		PropertyCheck.mandatory (this, "reportDesc", desc);

		try {
			// final String ooInFileNameTemplate = "ExampleArgsOfTheDoc.odt";

			// "/reportdefinitions/oo-templates/generated.odt";
			// final String ooOutFileNameResult = Utils.nonblank(desc.getMnem(), "generated") + ".odt";

			// File ooFileResult = null;
			// генерация уникального названия для входного шаблона (после операции - удалим)
			final File ooFileResult = createNewUniquieFile( desc, FMT_SFX_RESULT_FILENAME_S);
			try {
				// предварительно сохраняем данные на диск ...
				final String reportFileName = String.format( "%s%s", desc.getMnem(), OO_FILEEXT);
				final ContentReader reader = storage.loadContent( IdRContent.createId(desc, reportFileName));
				Utils.saveDataToFile( ooFileResult, ru.it.lecm.reports.utils.Utils.ContentToBytes(reader));

				final byte[] result = 
						openOfficeExecWithRetry(new Job<byte[]>() {

							@Override
							protected byte[] doIt() throws ConnectException {
								// входной шаблон
								if (!ooFileResult.exists()) {
									logger.warn(String.format( "file not found by id {%s}", ooFileResult));
									return null;
								}
								final String urlProcess = toUrl(ooFileResult, connection);

								/* Добавление атрибутов из колонок данных и сохранение в urlSave... */
								final OpenOfficeTemplateGenerator ooGen = new OpenOfficeTemplateGenerator();

								/** чтение файла в виде буфера ... */
								return loadFileAsData( ooFileResult, String.format( "Fail to load generated [temporary] report file \n '%s':\n", ooFileResult));
							}
						});

				logger.info( String.format( "Successfully generation for report '%s':\n\t result data size %s"
						, desc.getMnem(), (result == null ? "NULL" : result.length) ));
				return result;
			} finally {
				// if (ooFileResult != null) ooFileResult.delete();
			}
		} catch(Throwable ex) {
			final String msg = String.format("Exception at build report '%s':\n%s", desc.getMnem(), ex.getMessage());
			logger.error( msg, ex);
			throw new RuntimeException( msg, ex);
		}
	}

	/**
	 * Perform the actual connection check.  If this component is {@link #setStrict(boolean) strict},
	 * then a disconnected {@link #setConnection(OpenOfficeConnection) connection} will result in a
	 * runtime exception being generated.
	 */
	protected void checkConnection()
	{
		final String connectedMessage = "Connected to OpenOffice"; // I18NUtil.getMessage(INFO_CONNECTION_VERIFIED);
		if (tryConnect()) { // the connection is fine
			logger.info( connectedMessage);
			return;
		}

		// now we have to either fail or report the connection
		final String msgFail = String.format( "Connection to openoffice not established by connection:\n%s", getConnection().toString()); // I18NUtil.getMessage(ERR_CONNECTION_FAILED);
		if (this.ooConnectedStrictly)
			throw new RuntimeException(msgFail);
		logger.warn(msgFail);
	}

	protected boolean tryConnect()
	{
		PropertyCheck.mandatory(this, "connection", connection);
		this.ooAvailable = false;
		if (!connection.isConnected()) {
			try {
				connection.connect();
			}
			catch (ConnectException e) {	// No luck
				logger.warn( String.format( "Cannot connect to open office by connection %s", connection.toString()));
			}
		}
		this.ooAvailable = connection.isConnected();
		return this.ooAvailable;
	}


	/**
	 * Вывести состояние свойств и обновить заранее заданные именованные.
	 */
	private void dumpAndUpdateUserProperties(final XPropertySet userPropSet) {
		final String[] KNOWN_FIELD_NAMES = {"МоёПолеТекст", "МояДата", "MyFieldText", "MyFieldNumber"};
		final XPropertySetInfo userPropSetInfo = (XPropertySetInfo) userPropSet.getPropertySetInfo();

		final StringBuilder sb = new StringBuilder("Custom properties accessing:\n");
		try {
			for (final String propertyName: KNOWN_FIELD_NAMES) {
				sb.append( String.format( "\n\t name='%s' -> ", propertyName));
				try {
					if (userPropSetInfo.hasPropertyByName(propertyName) == false) {
						sb.append("NOT FOUND");
					} else {
						sb.append( String.format( "value '%s'", Utils.coalesce( userPropSet.getPropertyValue(propertyName), "NULL")) );
						{ // обновление значения свойства 
							Object newValue = null;
							if (propertyName.equals("МоёПолеТекст"))
								newValue = "new text as if (propertyName.equals(\"МоёПолеТекст\"))";
							else if (propertyName.equals("МояДата"))
								newValue = new com.sun.star.util.DateTime( (short) 2011, (short) 1, (short) 12, (short) 10, (short) 20, (short) 33, (short) 23);
							else if (propertyName.equals("MyFieldText"))
								newValue = " my-field-text value assigned programatically";
							else if (propertyName.equals("MyFieldNumber"))
								newValue = new Double(990230);

							if (newValue != null) {
								sb.append( String.format( "\n\t\t new value will be '%s'", Utils.coalesce( newValue, "NULL")) );
								userPropSet.setPropertyValue(propertyName, newValue);
							}
						}
					}
				} catch(Throwable ex) {
					sb.append("\n (!) Error ").append(ex.toString());
				}
			}
		} finally {
			logger.info( sb.toString());
		}
	}

	private void addUserProperties(XComponent xCompDoc, String docInfoMsg) {

		final StringBuilder sb = new StringBuilder();
		sb.append( String.format( "Document: '%s'\n\t Custom properties accessing:\n", docInfoMsg));
		try {

			final XDocumentInfoSupplier xDocumentInfoSupplier = UnoRuntime.queryInterface(
					XDocumentInfoSupplier.class, xCompDoc);
			final XDocumentInfo docInfo = xDocumentInfoSupplier.getDocumentInfo();
			final XPropertySet docProperties = UnoRuntime.queryInterface(XPropertySet.class, docInfo);

			// if (!docProperties.getPropertySetInfo().hasPropertyByName(Constants.LICENSE_URI)) { ... add props ... }
			// add the necessary properties to this document
			final XPropertyContainer docPropertyContainer = UnoRuntime.queryInterface( XPropertyContainer.class, docInfo);

			if (docPropertyContainer == null) {
				logger.warn( String.format("Document '%s' custom properties list is empty", docInfoMsg));
				return;
			}

			final int fldOptions = OpenOfficeTemplateGenerator.DOC_PROP_GOLD_FLAG_FOR_PERSISTENCE;
			OpenOfficeTemplateGenerator.addPropertySafely( docProperties, docPropertyContainer, sb, "ExtraDataString", fldOptions, "str-value-adskhsadfhkadhf");
			OpenOfficeTemplateGenerator.addPropertySafely( docProperties, docPropertyContainer, sb, "ExtraData_Date", fldOptions, new com.sun.star.util.DateTime( (short) 2013, (short) 8, (short) 28, (short) 13, (short) 36, (short) 0, (short) 0));
			OpenOfficeTemplateGenerator.addPropertySafely( docProperties, docPropertyContainer, sb, "ExtraData.Number", fldOptions, 12.345d);
		} finally {
			logger.info( sb.toString());
		}
	}

}
