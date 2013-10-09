package ru.it.lecm.reports.generators;

import java.io.File;
import java.io.IOException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.reports.api.JasperReportTargetFileType;
import ru.it.lecm.reports.api.ReportFileData;
import ru.it.lecm.reports.api.model.DataSourceDescriptor;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.api.model.DAO.ReportContentDAO;
import ru.it.lecm.reports.api.model.DAO.ReportContentDAO.IdRContent;
import ru.it.lecm.reports.model.DAO.FileReportContentDAOBean;
import ru.it.lecm.reports.ooffice.OpenOfficeFillManager;
import ru.it.lecm.reports.utils.ArgsHelper;
import ru.it.lecm.reports.utils.Utils;

import com.sun.star.ucb.XFileIdentifierConverter;
import com.sun.star.uno.UnoRuntime;

public class OOfficeReportGeneratorImpl extends ReportGeneratorBase {

    private static final transient Logger logger = LoggerFactory.getLogger(OOfficeReportGeneratorImpl.class);

    private final static Object _lockerUniqueGen = new Object();

    /**
     * файловое расширение для openOffice-файла
     */
    private static final String OO_FILEEXT = ".odt";

    /**
     * файловое расширение для временного openOffice-файла
     */
    private static final String TEMP_OO_FILEEXT = "_tmp" + OO_FILEEXT;

    /**
     * файловая маска-суффикс для временного openOffice-файла генерируемого Шаблона Отчёта
     */
    private static final String FMT_SFX_TEMP_RESULT_FILENAME_S = "-report_ready_%s" + TEMP_OO_FILEEXT;

    /**
     * файловая маска-суффикс для обычного генерируемого openOffice-отчёта
     */
    private static final String FMT_SFX_RESULT_FILENAME_S = "-report_ready_%s" + OO_FILEEXT;

    /**
     * максимальное кол-во попыток сохранить временный файл ("забить место")
     */
    private final static int MAX_CREATE_FILE_RETRY = 10;

    /**
     * Целевой формат отчёта по-умолчанию
     */
    private static final JasperReportTargetFileType DEFAULT_TARGET = JasperReportTargetFileType.RTF;

    /**
     * "Что сгенерировать" = название колонки (типа строка) с целевым форматом файла после генератора
     */
    private static final String COLNAME_TARGETFORMAT = DataSourceDescriptor.COLNAME_REPORT_TARGETFORMAT;

    private FileReportContentDAOBean resultDAO;
    private OpenOfficeConnection connection;

    /**
     * true, если openOffice доступен и connection.connected
     */
    private boolean ooAvailable = false;
    private boolean ooConnectedStrictly = false;
    private int maxConnectionRetries = 3;

    public void init() {
        checkConnection();
    }

    /**
     * true, если openOffice доступен и connection.connected
     * при проверке, если соединение отсутствует и {@link #ooConnectedStrictly is true}
     * , то поднимается исключение, иначе принимает false.
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
                           ReportContentDAO storage) {
        logger.info(String.format("decriptor deploy notification /'%s'/  \"%s\"", desc.getMnem(), desc.getDefault()));
    }

    private String toUrl(File file, OpenOfficeConnection connection) throws ConnectException {
        final Object fcProvider = connection.getFileContentProvider();
        XFileIdentifierConverter fic = UnoRuntime.queryInterface(XFileIdentifierConverter.class, fcProvider);
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
     * @return
     */
    static byte[] loadFileAsData(File srcFile, String errLogInfo) {
        try {
            return Utils.loadFileAsData(srcFile, errLogInfo);
        } catch (IOException ex) {
            final String msg = Utils.coalesce(errLogInfo, "") + ex.getMessage();
            logger.error(msg, ex);
            throw new RuntimeException(msg, ex);
        }
    }

    @Override
    public byte[] generateReportTemplateByMaket(final byte[] maketData, final ReportDescriptor desc) {
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
                ooFile = createNewUniquieFile(desc, FMT_SFX_TEMP_RESULT_FILENAME_S);
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
                                final String urlSaveAs = null; // если надо задать название файла другим ...

                                final String author = "lecm user";

								/* Добавление атрибутов из колонок данных и сохранение в urlSave... */
                                final OpenOfficeTemplateGenerator ooGen = new OpenOfficeTemplateGenerator();
                                ooGen.odtAddColumnsAsDocCustomProps(getConnection(), desc, urlWork, urlSaveAs, author);

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
     *
     * @param requestParameters
     * @return
     */
    // DONE: (?) разрешить задавать формат в колонках данных (константой или выражением)
    private JasperReportTargetFileType findTargetArg(final Map<String, String[]> requestParameters) {
        final String value = ArgsHelper.findArg(requestParameters, COLNAME_TARGETFORMAT, null);
        return JasperReportTargetFileType.findByName(value, DEFAULT_TARGET);
    }

    @Override
    public void produceReport(ReportFileData buildResult, ReportDescriptor reportDesc, Map<String, String[]> parameters, ReportContentDAO rptContent)
            throws IOException {
        PropertyCheck.mandatory(this, "services", getServices());
        PropertyCheck.mandatory(this, "reportsManager", getReportsManager());
        PropertyCheck.mandatory(this, "reportsDesc", reportDesc);
        PropertyCheck.mandatory(this, "reportsDesc.providerDesc", reportDesc.getProviderDescriptor());

        PropertyCheck.mandatory(this, "connection", getConnection());
        PropertyCheck.mandatory(this, "resultDAO", getResultDAO());

        logger.debug(String.format("producing report /'%s'/ \"%s\"", reportDesc.getMnem(), reportDesc.getDefault()));

		/* Получение данных шаблона отчёта */
        final ContentReader reader;

        final String reportTemplateFileName = // имя файла openOffice с шаблоном документа
                String.format("%s%s", reportDesc.getMnem(), OO_FILEEXT);

        reader = rptContent.loadContent(IdRContent.createId(reportDesc, reportTemplateFileName));
        if (reader == null) {
            throw new IOException(String.format("Report is missed - file '%s' not found", reportTemplateFileName));
        }


        File ooFile = null; // файл с шаблоном
        File ooResultFile; // готовый файл с правильным расширением
        try {
            ooFile = createNewUniquieFile(reportDesc, FMT_SFX_RESULT_FILENAME_S);
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
                final String dataSourceClass = reportDesc.getProviderDescriptor().className();
                final JRDataSourceProvider dsProvider = super.createDsProvider(reportDesc, dataSourceClass, parameters);

					/* построение отчёта */
                final byte[] result = generateReport(ooFile, ooResultFile, target, reportDesc, dsProvider, parameters);
                if (result != null)
                    buildResult.setData(result);
                else {
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
    }

    /**
     * Создание отчёта
     *
     * @param srcOOFile   исходный файл с openOffice-шаблоном отчёта
     * @param destDocFile целевой файл (например, *.rtf)
     * @param target      целевой формат (пока не поддерживается кроме rtf)
     * @param dsProvider
     * @throws JRException
     */
    private byte[] generateReport(
            final File srcOOFile
            , final File destDocFile
            , final JasperReportTargetFileType target
            , final ReportDescriptor report
            , final JRDataSourceProvider dsProvider
            , final Map<String, String[]> requestParameters) throws JRException {
        logger.debug("Generating report " + report.getMnem() + " ...");

        if (srcOOFile == null) {
            throw new IllegalArgumentException("The report file was not specified");
        }

        final JRDataSource dataSource = dsProvider.create(null);

        final byte[] result =
                openOfficeExecWithRetry(new Job<byte[]>() {

                    @Override
                    protected byte[] doIt() throws ConnectException {
                        // Выходной - "обычный" файлик OpenOffice ...
                        final String urlSrc = toUrl(srcOOFile, connection);

                        // Выходной файл ...
                        final String urlSaveAs = toUrl(destDocFile, connection);

						/* Добавление атрибутов из колонок данных и сохранение в urlSave... */
                        final OpenOfficeFillManager filler = new OpenOfficeFillManager(getConnection());
                        try {
                            filler.fill(report, requestParameters, dataSource, urlSrc, urlSaveAs);
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
     *
     * @param todo
     * @return
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
                return todo.getResult();
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
