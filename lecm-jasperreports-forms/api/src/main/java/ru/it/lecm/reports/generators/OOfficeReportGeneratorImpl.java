package ru.it.lecm.reports.generators;

import com.sun.star.ucb.XFileIdentifierConverter;
import com.sun.star.uno.UnoRuntime;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDataSourceProvider;
import net.sf.jasperreports.engine.JRException;
import net.sf.jooreports.openoffice.connection.OpenOfficeConnection;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.reports.api.JasperReportTargetFileType;
import ru.it.lecm.reports.api.ReportsManager;
import ru.it.lecm.reports.api.model.DAO.ReportContentDAO;
import ru.it.lecm.reports.api.model.DAO.ReportContentDAO.IdRContent;
import ru.it.lecm.reports.api.model.DataSourceDescriptor;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.api.model.ReportFileData;
import ru.it.lecm.reports.dao.FileReportContentDAOBean;
import ru.it.lecm.reports.model.impl.ReportTemplate;
import ru.it.lecm.reports.utils.ArgsHelper;
import ru.it.lecm.reports.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.Map;

public class OOfficeReportGeneratorImpl extends ReportGeneratorBase {
    private static final transient Logger logger = LoggerFactory.getLogger(OOfficeReportGeneratorImpl.class);

    private final static Object _lockerUniqueGen = new Object();

    /**
     * максимальное кол-во попыток сохранить временный файл ("забить место")
     */
    private final static int MAX_CREATE_FILE_RETRY = 10;

    private FileReportContentDAOBean resultDAO;
    private OpenOfficeConnection connection;

    /**
     * true, если openOffice доступен и connection.connected
     */
    private boolean ooAvailable = false;
    private boolean ooConnectedStrictly = false;
    private int maxConnectionRetries = 3;

    // по умолчанию, сервис заточен на работу с ODT
    private String fileExtension = ".odt";
    private String defaultTarget = "DOC";

    private OOTemplateGenerator templateGenerator;

    public void init() {
        checkConnection();
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public void setDefaultTarget(String defaultTarget) {
        this.defaultTarget = defaultTarget;
    }

    public void setTemplateGenerator(OOTemplateGenerator templateGenerator) {
        this.templateGenerator = templateGenerator;
    }

    public void setOoConnectedStrictly(boolean ooConnectedStrictly) {
        this.ooConnectedStrictly = ooConnectedStrictly;
    }

    public void setMaxConnectionRetries(int maxConnectionRetries) {
        this.maxConnectionRetries = maxConnectionRetries;
    }

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
    public void onRegister(ReportDescriptor desc, ReportTemplate template, byte[] templateData, ReportContentDAO storage) {
        logger.info(String.format("decriptor deploy notification /'%s'/  \"%s\"", desc.getMnem(), desc.getDefault()));
    }

    private String toUrl(File file, OpenOfficeConnection connection) throws ConnectException {
        final Object fcProvider = connection.getFileContentProvider();
        XFileIdentifierConverter fic = (XFileIdentifierConverter)UnoRuntime.queryInterface(XFileIdentifierConverter.class, fcProvider);
        return fic.getFileURLFromSystemPath("", file.getAbsolutePath());
    }

    /**
     * Сгенерировать уникальное название на основании маски и (!) создать пустой
     * файл с этим именем ("застолбить" его). Генерируется имя файла вида:
     * ".../"+ кодОтчёта+ "-YYYY-MM-DD-hh-mm-ss" + nameFmtSuffix_s
     * (!) надо иметь в виду, что если файл не сохранить на диске, то одно и то
     * же имя может быть сгенерировано многократно в разных потоках и ошиби
     * начнут возникать только, когда потоки начнут писать в эти одноимённые
     * файлы.
     *
     * @param desc            описатель отчёта
     * @param nameFmtSuffix_s маска с одним параметром, для подстановки подстрокиб
     *                        чтобы обеспечить уникальность
     * @return уникальное название файла, который (!) будет создан здесь как пустой
     * @throws IOException
     */
    private File createNewUniquieFile(ReportDescriptor desc, String nameFmtSuffix_s) throws IOException {
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
                    logger.error(msg);
                    throw new RuntimeException(msg);
                }
                result = Utils.findEmptyFile(fbase, nameFmtSuffix_s); // к имени добавляем генерируемую часть
                if (result.getParentFile() != null) { // гарантируем вышестоящие каталоги ...
                    result.getParentFile().mkdirs();
                }
            } while (!result.createNewFile()); // крутим генерацию пока уникальный файл не создадим
        }
        return result;
    }

    /**
     * Загрузить данные из файла как байтовый блок.
     * IO-исключения обёрнуты как rtm-exceptions.
     *
     * @param srcFile    исходный файл для загрузки
     * @param errLogInfo сообщение при ошибках
     * @return byte[]
     */
    static byte[] loadFileAsData(File srcFile, String errLogInfo) {
        try {
            return Utils.loadFileAsData(srcFile);
        } catch (IOException ex) {
            final String msg = Utils.coalesce(errLogInfo, "") + ex.getMessage();
            logger.error(msg, ex);
            throw new RuntimeException(msg, ex);
        }
    }

    @Override
    public byte[] generateReportTemplateByMaket(final byte[] maketData, final ReportDescriptor desc, final ReportTemplate template) {
        // обмен идёт через файловое хранилище - openOffice работает с файлами ...
        PropertyCheck.mandatory(this, "connection", getConnection());
        PropertyCheck.mandatory(this, "resultDAO", getResultDAO());

        try {
            /**
             * здесь ожидаем, что входной поток имеет формат документа OpenOffice ("*.odt")
             * выполняем сохранение maketData во временный файл (потом удаляем его), чтобы с ним нормально отработал OpenOffice
             * результат получаем в виде того же временного файла, которой читаем и удаляем
             */
            File ooFile = null;
            try {
                // генерация уникального названия для результата (после операции - удалим)
                ooFile = createNewUniquieFile(desc, "-report_ready_%s_tmp" + fileExtension);
                Utils.saveDataToFile(ooFile, maketData);

                final File workFile = ooFile;

                final byte[] result =
                        openOfficeExecWithRetry(new Job<byte[]>() {

                            @Override
                            protected byte[] doIt() throws ConnectException {
                                // входной шаблон
                                if (!workFile.exists()) {
                                    logger.warn(String.format("file not found by id {%s}", workFile));
                                    return null;
                                }
                                /* выходной - "обычный" файлик OpenOffice */
                                final String urlWork = toUrl(workFile, connection);
                                final String author = "LECM USER";

                                final JRDataSourceProvider dsProvider = createDsProvider(null, desc, desc.getProviderDescriptor().getClassName(), null);

								/* Добавление атрибутов из колонок данных и сохранение в urlSave... */
                                templateGenerator.odtAddColumnsAsDocCustomProps(getConnection(), dsProvider, desc, template, urlWork, null, author);

                                /** чтение файла в виде буфера ... */
                                final String errLogInfo = String.format("Fail to load generated [temporary] file: \n '%s'", urlWork);
                                return loadFileAsData(workFile, errLogInfo);
                            }
                        });

                logger.info(String.format("Successfully generated template for report '%s':\n\t data size %s"
                        , desc.getMnem(), (result == null ? "NULL" : result.length)));
                return result;
            } finally {
                if (ooFile != null) {
                    ooFile.delete();
                }
            }
        } catch (IOException ex) {
            final String msg = String.format("Error generating template of report '%s':\n%s", desc.getMnem(), ex.getMessage());
            logger.error(msg, ex);
            throw new RuntimeException(msg, ex);
        }
    }

    /**
     * Найти целевой формат в параметрах ...
     */
    private JasperReportTargetFileType findTargetArg(final Map<String, Object> requestParameters) {
        final String value = ArgsHelper.findArg(requestParameters, DataSourceDescriptor.COLNAME_REPORT_TARGETFORMAT, null);
        JasperReportTargetFileType defaultType = null;
        if (defaultTarget != null) {
            defaultType = JasperReportTargetFileType.findByName(defaultTarget, null);
        }
        return JasperReportTargetFileType.findByName(value, defaultType);
    }

    @Override
    public ReportFileData produceReport(ReportsManager reportsManager, ReportDescriptor reportDesc, ReportTemplate templateDescriptor, Map<String, Object> parameters)
            throws IOException {
        PropertyCheck.mandatory(this, "services", getServices());
        PropertyCheck.mandatory(this, "reportsDesc", reportDesc);
        PropertyCheck.mandatory(this, "connection", getConnection());

        ReportFileData buildResult = new ReportFileData();

        logger.debug(String.format("producing report /'%s'/ \"%s\"", reportDesc.getMnem(), reportDesc.getDefault()));

        final ReportContentDAO rptContent = reportsManager.findContentDAO(reportDesc);
        if (rptContent == null) {
            throw new RuntimeException(String.format("Report '%s' storage point is unknown (possibly report is not registered !?)", reportDesc.getMnem()));
        }

		/* Получение данных шаблона отчёта */
        final String reportTemplateFileName = getTemplateFileName(reportDesc, templateDescriptor, fileExtension);
        final ContentReader reader = rptContent.loadContent(IdRContent.createId(reportDesc, reportTemplateFileName));

        if (reader == null) {
            throw new IOException(String.format("Report is missed - file '%s' not found", reportTemplateFileName));
        }

        File ooFile = null; // файл с шаблоном
        File ooResultFile; // готовый файл с правильным расширением
        try {
            ooFile = createNewUniquieFile(reportDesc, "-report_ready_%s" + fileExtension);
            Utils.saveDataToFile(ooFile, reader.getContentInputStream());

            final JasperReportTargetFileType target = findTargetArg(parameters);

            // конечное название файла
            ooResultFile = createNewUniquieFile(reportDesc, "%s" + target.getExtension());

            try {
                final String reportResultFileName = FilenameUtils.getName(ooResultFile.getAbsolutePath());
                buildResult.setMimeType(target.getMimeType());
                buildResult.setFilename(reportResultFileName);
                buildResult.setEncoding("UTF-8");
                buildResult.setData(null);
				/* создание Провайдера */
                final String dataSourceClass = reportDesc.getProviderDescriptor().getClassName();
                final JRDataSourceProvider dsProvider = createDsProvider(reportsManager, reportDesc, dataSourceClass, parameters);

				/* построение отчёта */
                final byte[] result = generateReport(ooFile, ooResultFile, target, reportDesc, dsProvider, parameters);
                if (result != null) {
                    buildResult.setData(result);
                } else {
                    logger.warn(String.format("Report '%s' got empty result", reportDesc.getMnem()));
                }
            } finally {
                if (ooResultFile != null) {
                    ooResultFile.delete();
                }
            }
        } catch (Throwable e) { // (JRException e) {
            final String msg = String.format("Fail to build openOffice report '%s':\n\t%s", reportDesc.getMnem(), e);
            logger.error(msg, e);
            throw new IOException(msg, e);
        } finally {
            if (ooFile != null) {
                ooFile.delete();
            }
        }
        return buildResult;
    }

    /**
     * Создание отчёта
     *
     * @param srcOOFile   исходный файл с openOffice-шаблоном отчёта
     * @param destDocFile целевой файл (например, *.rtf)
     * @param target      целевой формат (пока не поддерживается кроме rtf)
     * @throws JRException
     */
    private byte[] generateReport(
            final File srcOOFile
            , final File destDocFile
            , final JasperReportTargetFileType target
            , final ReportDescriptor report
            , final JRDataSourceProvider dsProvider
            , final Map<String, Object> requestParameters) throws JRException {
        logger.debug("Generating report " + report.getMnem() + " ...");

        if (srcOOFile == null) {
            throw new IllegalArgumentException("The report file was not specified");
        }

        final byte[] result =
                openOfficeExecWithRetry(new Job<byte[]>() {

                    @Override
                    protected byte[] doIt() throws ConnectException {
                        // Выходной - "обычный" файлик OpenOffice ...
                        final String urlSrc = toUrl(srcOOFile, connection);

                        // Выходной файл ...
                        final String urlSaveAs = toUrl(destDocFile, connection);

                        try {
                            fill(report, requestParameters, dsProvider, urlSrc, urlSaveAs);
                        } catch (JRException ex) {
                            final String msg = String.format("Error filling report '%s':\n%s", report.getMnem(), ex.getMessage());
                            logger.error(msg, ex);
                            throw new RuntimeException(msg, ex);
                        }

                        /** чтение файла в виде буфера ... */
                        final String errLogInfo = String.format("Fail to load generated report file:\n '%s'\n", urlSrc);
                        return loadFileAsData(destDocFile, errLogInfo);
                    }
                });

        logger.info(String.format("Report '%s' as %s generated succefully:\n\t report has %s bytes"
                , report.getMnem(), target, (result == null ? "NULL" : result.length)));
        return result;
    }

    /**
     * Выполнить заполнение данными указанного отчёта файла openOffice
     *
     * @param report       отчёт
     * @param parameters   параметры
     * @param jrProvider провайдер
     * @param urlSrc       исходный файл openOffice (".odt")
     * @param urlSaveAs    целевой файл (может иметь другой формат, например, ".rtf")
     * @throws JRException
     */
    public void fill(ReportDescriptor report, Map<String, Object> parameters, JRDataSourceProvider jrProvider, String urlSrc, String urlSaveAs) throws JRException {
        //в props для обычного провайдера - список заполненных значений, для SQL - дефолтныхce);
        templateGenerator.odtSetColumnsAsDocCustomProps(jrProvider, parameters, report, urlSrc, urlSaveAs, null);
    }

    private abstract class Job<TResult> {

        /**
         * возвращаемое значение
         */
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
     * Выполнить указанное действие, с несколькими повторами при ошибках openOffice-соединения
     * См также {@link #maxConnectionRetries}
     */
    protected <TResult> TResult openOfficeExecWithRetry(Job<TResult> todo) {
        int retryCount = 0;
        while (true) {

            Throwable lastEx;
            try {
                checkConnection();
                if (!getConnection().isConnected()) {
                    throw new RuntimeException("Office connection is down");
                }
                todo.doExec();
                return todo.getResult(); // (!) normal break of while
            } catch (com.sun.star.lang.DisposedException ex) {
                lastEx = ex;
            } catch (ConnectException connEx) {
                lastEx = connEx;
            } catch (Throwable ex) {
                final String msg = String.format("Exception at retry %s of %s:\n%s", retryCount, this.maxConnectionRetries, ex.getMessage());
                logger.error(msg, ex);
                throw new RuntimeException(msg, ex);
            }

            // если оказываемся здесь - идём на повторный retry-цикл
            final boolean canRetry = retryCount < this.maxConnectionRetries;
            retryCount++;

            try {
                this.ooAvailable = false;
                connection.disconnect();
            } catch (Throwable t) { // here there is no need to re-raise something ...
                logger.warn(String.format("ignore dispose error:\n%s", t.getMessage()));
            }

            final String msg = (canRetry)
                    ? String.format("OpenOffice connection disposed -> retry %s of %s", retryCount, this.maxConnectionRetries)
                    : String.format("Cannot restore OpenOffice connection in %s retries -> error", this.maxConnectionRetries);
            if (!canRetry) { // re-raise ...
                logger.error(msg, lastEx);
                throw new RuntimeException(msg, lastEx);
            }
            // go on retry
            logger.warn(msg);
        }
    }


    /**
     * Perform the actual connection check.  If this component is strict},
     * then a disconnected {@link #setConnection(OpenOfficeConnection) connection} will result in a
     * runtime exception being generated.
     */
    protected void checkConnection() {
        final String connectedMessage = "Connected to OpenOffice";
        if (tryConnect()) { // the connection is fine
            logger.info(connectedMessage);
            return;
        }

        // now we have to either fail or report the connection
        final String msgFail = String.format("Connection to openoffice not established by connection:\n%s", getConnection().toString());
        if (this.ooConnectedStrictly) {
            throw new RuntimeException(msgFail);
        }
        logger.warn(msgFail);
    }

    protected boolean tryConnect() {
        PropertyCheck.mandatory(this, "connection", connection);
        this.ooAvailable = false;
        if (!connection.isConnected()) {
            try {
                connection.connect();
            } catch (ConnectException e) {    // No luck
                logger.warn(String.format("Cannot connect to open office by connection %s", connection.toString()));
            }
        }
        this.ooAvailable = connection.isConnected();
        return this.ooAvailable;
    }
}
