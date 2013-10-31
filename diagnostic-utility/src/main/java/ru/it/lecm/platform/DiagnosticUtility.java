package ru.it.lecm.platform;

import com.sun.management.OperatingSystemMXBean;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.management.ManagementFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class DiagnosticUtility {
    private static final transient Logger log = LoggerFactory.getLogger(DiagnosticUtility.class);

    private final static String DIAGNOSTIC_LOG_FILENAME = File.separator + "utility-diagnostic-log.txt";
    private final static String CONTROL_INFO_FILENAME = File.separator + "utility-control-log.txt";

    private final static String LOGS_ARCHIVE_NAME = File.separator + "server-logs.zip";
    private final static String SHARED_CLASSES_ARCHIVE_NAME = File.separator + "server-shared-classes.zip";
    private final static String ALFRESCO_CONFIGS_ARCHIVE_NAME = File.separator + "alfresco-configs.zip";
    private final static String SHARE_CONFIGS_ARCHIVE_NAME = File.separator + "share-configs.zip";
    private final static String TOMCAT_CONFIGS_ARCHIVE_NAME = File.separator + "tomcat-configs.zip";
    private final static String SOLR_CONFIGS_ARCHIVE_NAME = File.separator + "solr-configs.zip";

    private final static String CONFIG_FILENAME = "utility-properties.cfg";
    private final static String ALFRESCO_GLOBAL_PROPERTIES = "alfresco-global.properties";
    private final static String ALF_HOME = "ALF_HOME";
    private final static String OUTPUT_DIR = "OUTPUT_DIR";
    private final static String ADMIN_PASSWORD = "ADMIN_PASSWORD";
    private final static DateFormat LOG_DATE_FMT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    final static private Map<String, String> config = new HashMap<String, String>();

    final static StringBuilder logText = new StringBuilder();
    final static FileFinder finder = new FileFinder();

    private static String currentDirectoryPath = ".";

    private static boolean isServerAvailable = false;

    public static void main(String[] args) {
        BasicConfigurator.configure();
        // считываем конфигурационный файл. сохраняем параметры в config
        readConfigFile();

        if (config.get(OUTPUT_DIR) != null) {
            File outputFile = new File(config.get(OUTPUT_DIR));
            if (outputFile.exists()) {
                currentDirectoryPath = config.get(OUTPUT_DIR);
            }
        }

        // более читаемая директория - файлы будем собирать туда
        currentDirectoryPath = currentDirectoryPath + File.separator + "utility-results-" + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
        new File(currentDirectoryPath).mkdirs();

        //создаем диагностический файл и пишем в него инфу
        createDiagnosticFile();

        //создаем контрольный файл и пишем в него инфу
        createControlFile();
    }

    private static void readConfigFile() {
        BufferedReader br = null;
        try {
            File configFile = new File(CONFIG_FILENAME);
            if (!configFile.exists()) {
                log.error("Cannot find utility's config file: " + CONFIG_FILENAME);
            } else {
                //инициализируем конфиг из файла
                br = new BufferedReader(new FileReader(configFile));
                String row;
                log.info("Configurations:\n");
                while ((row = br.readLine()) != null) {
                    log.info(row);
                    String[] property = row.split("=");
                    config.put(property[0], property[1]);
                }
            }
        } catch (Exception e) {
            log.error("Cannot read config file: " + CONFIG_FILENAME);
        } finally {
            IOUtils.closeQuietly(br);
        }
    }

    private static String checkServer() {
        StringBuilder result = new StringBuilder();
        //прочитать и распарсить настроечный файл альфреско
        String alfrescoRootDirectory = config.get(ALF_HOME);
        if (alfrescoRootDirectory == null) {
            result.append("Failed read property \'ALF_HOME\' from config file. Please check utility configuration!").append("\n");
            return result.toString();
        }

        File settingsDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"tomcat", "shared", "classes"}));
        List<File> settingsFile = findFiles(settingsDir, ALFRESCO_GLOBAL_PROPERTIES, false);

        File alfrescoProperties = null;
        if (settingsFile != null && settingsFile.size() > 0) {
            alfrescoProperties = settingsFile.get(0);
        }
        if (alfrescoProperties == null || !alfrescoProperties.exists()) {
            result.append("Failed read alfresco-global.properties. Please check utility configuration!").append("\n");
            return result.toString();
        }

        Map<String, String> alfPropsMap = new HashMap<String, String>();
        BufferedReader br = null;
        try {
            //инициализируем конфиг из файла
            br = new BufferedReader(new FileReader(alfrescoProperties));
            result.append("Read alfresco-global.properties started").append("\n");
            String row;
            while ((row = br.readLine()) != null) {
                if (row.contains("=") && !row.startsWith("#")) {
                    String[] property = row.split("=");
                    alfPropsMap.put(property[0], property[1]);
                    result.append(row).append("\n");
                }
            }
        } catch (Exception e) {
            log.error("Cannot read alfresco-global.properties file: " + CONFIG_FILENAME);
        } finally {
            IOUtils.closeQuietly(br);
        }

        result.append("\n");

        //Нужные свойства
        String alfrescoHost = alfPropsMap.get("alfresco.host");
        String alfrescoPort = alfPropsMap.get("alfresco.port");
        String alfrescoProtocol = alfPropsMap.get("alfresco.protocol");
        String alfrescoContext = alfPropsMap.get("alfresco.context");

        String shareHost = alfPropsMap.get("share.host");
        String sharePort = alfPropsMap.get("share.port");
        String shareProtocol = alfPropsMap.get("share.protocol");
        String shareContext = alfPropsMap.get("share.context");

        String dbDriver = alfPropsMap.get("db.driver");
        String dbUser = alfPropsMap.get("db.username");
        String dbPass = alfPropsMap.get("db.password");
        String dbName = alfPropsMap.get("db.name");
        String dbUrl = alfPropsMap.get("db.url");

        //запрос к tomcat и БД
        HttpClient httpclient = new HttpClient();
        httpclient.getParams().setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT, 5000);

        String requestURL1 = alfrescoProtocol + "://" + alfrescoHost + ":" + alfrescoPort;
        String requestURL2 = alfrescoProtocol + "://localhost:" + alfrescoPort;

        result.append("Check Tomcat Server...").append("\n");
        int requestTomcatStatus = sendRequestToURL(httpclient, new String[]{requestURL1, requestURL2}, result);

        result.append("Check Data Base Server...").append("\n");
        int requestDBStatus = checkDBConnection(dbDriver, dbUser, dbPass, dbName, dbUrl, result);

        if (requestTomcatStatus != 200 || requestDBStatus != 200) {
            result.append("Alfresco Server not started. Cannot send request to webapps").append("\n");
            isServerAvailable = false;
            return result.toString();
        } else {
            isServerAvailable = true;
        }

        //request to alfresco
        result.append("Check Alfresco...").append("\n");
        requestURL1 = alfrescoProtocol + "://" + alfrescoHost + ":" + alfrescoPort + "/" + alfrescoContext;
        requestURL2 = alfrescoProtocol + "://localhost:" + alfrescoPort + "/" + alfrescoContext;
        sendRequestToURL(httpclient, new String[]{requestURL1, requestURL2}, result);

        //request to share
        result.append("Check Share...").append("\n");
        requestURL1 = shareProtocol + "://" + shareHost + ":" + sharePort + "/" + shareContext;
        requestURL2 = shareProtocol + "://localhost:" + sharePort + "/" + shareContext;
        sendRequestToURL(httpclient, new String[]{requestURL1, requestURL2}, result);

        //request to solr
        //result.append("Check Solr...").append("\n");
        //requestURL1 = alfrescoProtocol + "s://" + alfrescoHost + ":" + alfrescoPort + "/solr";
        //requestURL2 = alfrescoProtocol + "s://localhost:" + alfrescoPort + "/solr";
        //sendRequestToURL(httpclient, new String[]{requestURL1, requestURL2}, result);

        return result.toString();
    }

    private static String collectFiles() {
        StringBuilder result = new StringBuilder();
        long numberOfArchivedFiles = 0;

        String alfrescoRootDirectory = config.get(ALF_HOME);
        if (alfrescoRootDirectory == null) {
            result.append("Failed read property \'ALF_HOME\' from config file. Please check utility configuration!").append("\n");
            return result.toString();
        }

        File alfRoot = new File(alfrescoRootDirectory);
        if (!alfRoot.exists()) {
            result.append("Failed read Alfresco Root Directory by Path: ").append(alfrescoRootDirectory).append("\n");
            return result.toString();
        }
        // Далее log files
        numberOfArchivedFiles += collectLogFiles(result, alfrescoRootDirectory);

        // Далее properties и xml из shared/classes
        numberOfArchivedFiles += collectSharedFiles(result, alfrescoRootDirectory);

        // Далее solr files
        numberOfArchivedFiles += collectSolrFiles(result, alfrescoRootDirectory);

        // Далее конфиги томката
        numberOfArchivedFiles += collectTomcatConfigs(result, alfrescoRootDirectory);

        // И наконец проходим по альфреско и шаре
        numberOfArchivedFiles += collectAlfrescoFiles(result, alfrescoRootDirectory);
        numberOfArchivedFiles += collectShareFiles(result, alfrescoRootDirectory);

        createPhaseLogInformation(result, "Collect files completed. All archived Files:" + numberOfArchivedFiles, null);
        return result.toString();
    }

    private static long collectLogFiles(StringBuilder result, String alfrescoRootDirectory) {
        List<File> alfrescoLogs = new ArrayList<File>();
        alfrescoLogs.addAll(findFiles(alfrescoRootDirectory, ".*\\.log", true));

        return writeFilesToArchive(result, "Archive log-files started...",
                alfrescoLogs, new File(alfrescoRootDirectory), new File(currentDirectoryPath + LOGS_ARCHIVE_NAME));
    }

    private static long collectSharedFiles(StringBuilder result, String alfrescoRootDirectory) {
        List<File> alfrescoSharedClasses = new ArrayList<File>();
        File sharedClassesDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"tomcat", "shared", "classes"}));

        alfrescoSharedClasses.addAll(findFiles(sharedClassesDir, ".*\\.xml", true));
        alfrescoSharedClasses.addAll(findFiles(sharedClassesDir, ".*\\.properties", true));

        return writeFilesToArchive(result, "Archive files from shared/classes directory started...",
                alfrescoSharedClasses, new File(alfrescoRootDirectory), new File(currentDirectoryPath + SHARED_CLASSES_ARCHIVE_NAME));
    }

    private static long collectTomcatConfigs(StringBuilder result, String alfrescoRootDirectory) {
        List<File> tomcatConfigs = new ArrayList<File>();
        File tomcatDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"tomcat", "conf"}));

        tomcatConfigs.addAll(findFiles(tomcatDir, "", true));

        return writeFilesToArchive(result, "Archive tomcat config files started...",
                tomcatConfigs, new File(alfrescoRootDirectory), new File(currentDirectoryPath + TOMCAT_CONFIGS_ARCHIVE_NAME));
    }

    private static long collectShareFiles(StringBuilder result, String alfrescoRootDirectory) {
        List<File> shareConfigs = new ArrayList<File>();
        File shareDir;

        //----- log4j.properties -------
        shareDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"tomcat", "webapps", "share", "WEB-INF", "classes"}));
        shareConfigs.addAll(findFiles(shareDir, "log4j.properties", false));

        shareDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"tomcat", "webapps", "share", "WEB-INF", "classes", "alfresco", "module"}));
        shareConfigs.addAll(findFiles(shareDir, "module.properties", true));

        shareDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"tomcat", "webapps", "share", "WEB-INF", "classes", "alfresco", "web-extension"}));
        shareConfigs.addAll(findFiles(shareDir, ".*-share-config-custom\\.xml", true));

        shareDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"tomcat", "webapps", "share", "WEB-INF", "classes", "alfresco"}));
        shareConfigs.addAll(findFiles(shareDir, ".*-share-context\\.xml|.*\\.config\\.xml", true));

        shareDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"tomcat", "webapps", "share", "WEB-INF", "classes", "alfresco"}));
        shareConfigs.addAll(findFiles(shareDir, ".*\\.xml", false));

        shareDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"tomcat", "webapps", "share", "WEB-INF"}));
        shareConfigs.addAll(findFiles(shareDir, ".*\\.xml", false));

        return writeFilesToArchive(result, "Archive Share config files started...",
                shareConfigs, new File(alfrescoRootDirectory), new File(currentDirectoryPath + SHARE_CONFIGS_ARCHIVE_NAME));
    }

    private static long collectSolrFiles(StringBuilder result, String alfrescoRootDirectory) {
        List<File> solrFiles = new ArrayList<File>();
        File solrWorkSpaceDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"alf_data", "solr", "workspace-SpacesStore"}));
        solrFiles.addAll(findFiles(solrWorkSpaceDir, "", true));

        File solrArchiveDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"alf_data", "solr", "archive-SpacesStore"}));
        solrFiles.addAll(findFiles(solrArchiveDir, "", true));

        File solrConfigDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"tomcat", "webapps", "solr", "WEB-INF"}));
        solrFiles.addAll(findFiles(solrConfigDir, ".*\\.xml", true));

        return writeFilesToArchive(result, "Archive solr files started...",
                solrFiles, new File(alfrescoRootDirectory), new File(currentDirectoryPath + SOLR_CONFIGS_ARCHIVE_NAME));
    }

    private static long collectAlfrescoFiles(StringBuilder result, String alfrescoRootDirectory) {
        List<File> alfrescoConfigs = new ArrayList<File>();
        File alfrescoDir;

        //----- log4j.properties -------
        alfrescoDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"tomcat", "webapps", "alfresco", "WEB-INF", "classes"}));
        alfrescoConfigs.addAll(findFiles(alfrescoDir, "log4j.properties", true));

        //----- *.properties -------
        alfrescoDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"tomcat", "webapps", "alfresco", "WEB-INF", "classes", "alfresco", "domain"}));
        alfrescoConfigs.addAll(findFiles(alfrescoDir, ".*\\.properties", true));

        alfrescoDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"tomcat", "webapps", "alfresco", "WEB-INF", "classes", "alfresco", "keystore"}));
        alfrescoConfigs.addAll(findFiles(alfrescoDir, ".*\\.properties", true));

        alfrescoDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"tomcat", "webapps", "alfresco", "WEB-INF", "classes", "alfresco", "subsystems"}));
        alfrescoConfigs.addAll(findFiles(alfrescoDir, ".*\\.properties", true));

        //----- module ----------
        alfrescoDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"tomcat", "webapps", "alfresco", "WEB-INF", "classes", "alfresco", "module"}));
        alfrescoConfigs.addAll(findFiles(alfrescoDir, "module.properties|.*-context\\.xml|.*-model\\.xml|.*bpmn20\\.xml", true));

        alfrescoDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"tomcat", "webapps", "alfresco", "WEB-INF"}));
        alfrescoConfigs.addAll(findFiles(alfrescoDir, ".*\\.xml", false));

        alfrescoDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"tomcat", "webapps", "alfresco", "WEB-INF", "classes", "alfresco"}));
        alfrescoConfigs.addAll(findFiles(alfrescoDir, ".*\\.xml", false));

        alfrescoDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"tomcat", "webapps", "alfresco", "WEB-INF", "classes", "alfresco", "model"}));
        alfrescoConfigs.addAll(findFiles(alfrescoDir, ".*\\.xml", false));

        alfrescoDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"tomcat", "webapps", "alfresco", "WEB-INF", "classes", "alfresco", "bootstrap"}));
        alfrescoConfigs.addAll(findFiles(alfrescoDir, ".*\\.xml", false));

        alfrescoDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"tomcat", "webapps", "alfresco", "WEB-INF", "classes", "alfresco", "dao"}));
        alfrescoConfigs.addAll(findFiles(alfrescoDir, ".*\\.xml", false));

        alfrescoDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"tomcat", "webapps", "alfresco", "WEB-INF", "classes", "alfresco", "ibatis"}));
        alfrescoConfigs.addAll(findFiles(alfrescoDir, ".*\\.xml", false));

        alfrescoDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"tomcat", "webapps", "alfresco", "WEB-INF", "classes", "alfresco", "jgroups"}));
        alfrescoConfigs.addAll(findFiles(alfrescoDir, ".*\\.xml", false));

        alfrescoDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"tomcat", "webapps", "alfresco", "WEB-INF", "classes", "alfresco", "ml"}));
        alfrescoConfigs.addAll(findFiles(alfrescoDir, ".*\\.xml", false));

        alfrescoDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"tomcat", "webapps", "alfresco", "WEB-INF", "classes", "alfresco", "mimetype"}));
        alfrescoConfigs.addAll(findFiles(alfrescoDir, ".*\\.xml", false));

        alfrescoDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"tomcat", "webapps", "alfresco", "WEB-INF", "classes", "alfresco", "mt"}));
        alfrescoConfigs.addAll(findFiles(alfrescoDir, ".*\\.xml", false));

        alfrescoDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"tomcat", "webapps", "alfresco", "WEB-INF", "classes", "alfresco", "subsystems"}));
        alfrescoConfigs.addAll(findFiles(alfrescoDir, ".*\\.xml", false));

        alfrescoDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"tomcat", "webapps", "alfresco", "WEB-INF", "classes", "alfresco", "templates"}));
        alfrescoConfigs.addAll(findFiles(alfrescoDir, ".*\\.xml", false));

        alfrescoDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"tomcat", "webapps", "alfresco", "WEB-INF", "classes", "alfresco", "workflow"}));
        alfrescoConfigs.addAll(findFiles(alfrescoDir, ".*\\.xml", false));

        return writeFilesToArchive(result, "Archive Alfresco config files started...", alfrescoConfigs, new File(alfrescoRootDirectory), new File(currentDirectoryPath + ALFRESCO_CONFIGS_ARCHIVE_NAME));
    }

    private static List<File> findFiles(File rootDirectory, String template, boolean searchInSubDirs) {
        String path;
        if (rootDirectory.exists()) {
            try {
                path = rootDirectory.getCanonicalPath();
                return finder.findFiles(path, template, searchInSubDirs);
            } catch (Exception e) {
                log.error("Cannot find file", e);
            }
        }
        return new ArrayList<File>();
    }

    private static List<File> findFiles(String root, String template, boolean searchInSubDirs) {
        String path;
        File rootDirectory = new File(root);
        if (rootDirectory.exists()) {
            try {
                path = rootDirectory.getCanonicalPath();
                return finder.findFiles(path, template, searchInSubDirs);
            } catch (Exception e) {
                log.error("Cannot find file", e);
            }
        }
        return new ArrayList<File>();
    }

    private static long writeFilesToArchive(final StringBuilder buf, final String phaseDesc, final List<File> filesToWrite, final File alfrescoRoot, final File zipFile) {
        long numberOfArchivedFiles = 0;

        Deque<File> queue = new LinkedList<File>();
        if (filesToWrite != null) {
            for (File file : filesToWrite) {
                if (file != null && file.exists()) {
                    queue.push(file);
                }
            }
        }

        //запись в архив
        byte[] buffer = new byte[1024];
        ZipOutputStream out = null;
        boolean isOk = false;

        try {
            if (!zipFile.exists()) {
                zipFile.createNewFile();
            }
            out = new ZipOutputStream(new FileOutputStream(zipFile));
            out.setLevel(Deflater.DEFAULT_COMPRESSION);
            out.putNextEntry(new ZipEntry(""));
            out.closeEntry();
            while (!queue.isEmpty()) {
                File compressedFile = queue.pop();

                String name = finder.getRelativePath(alfrescoRoot, compressedFile);

                if (compressedFile.isDirectory()) {
                    File[] childFiles = compressedFile.listFiles();
                    if (childFiles != null) {
                        for (File child : childFiles) {
                            queue.push(child);
                        }
                    }
                    name = name.endsWith("/") ? name : name + "/";
                    out.putNextEntry(new ZipEntry(name));
                } else {
                    InputStream in = null;
                    try {
                        out.putNextEntry(new ZipEntry(name));
                        in = new FileInputStream(compressedFile);
                        while (true) {
                            int readCount = in.read(buffer);
                            if (readCount < 0) {
                                break;
                            }
                            out.write(buffer, 0, readCount);
                        }
                        out.closeEntry();
                        numberOfArchivedFiles++;
                    } catch (Exception ex) {
                        log.error("Some problem occured", ex);
                    } finally {
                        IOUtils.closeQuietly(in);
                    }
                }
            }
            isOk = true;
        } catch (FileNotFoundException e) {
            log.error("Cannot read file ", e);
        } catch (IOException e) {
            log.error("Cannot read file", e);
        } finally {
            IOUtils.closeQuietly(out);
        }
        StringBuilder result = new StringBuilder();
        if (isOk && zipFile.exists()) {
            result.append("Number of files archived: ").append(numberOfArchivedFiles).append("\n");
            result.append("Resulted archive created! File Path: ").append(zipFile.getAbsolutePath());
        }

        createPhaseLogInformation(buf, phaseDesc, result.toString());

        return numberOfArchivedFiles;
    }

    private static String collectBusinessJournalRecords() {
        return "";
    }

    private static String collectOrgstructureInfo() {
        return "";
    }

    private static String collectSystemInformation(boolean onlyInetParams) {
        StringBuilder result = new StringBuilder();

        // IP адрес сервера
        log.info("Get Inet Address information");
        createPhaseLogInformation(result, "Get Inet Address information", null);
        InetAddress addr;
        try {
            addr = InetAddress.getLocalHost();

            result.append("Server IP: ").append(addr.getHostAddress()).append("\n");
            result.append("Server Canonic Name: ").append(addr.getCanonicalHostName()).append("\n");
            result.append("Server Name : ").append(addr.getHostName()).append("\n");
        } catch (UnknownHostException e) {
            log.error(e.getMessage(), e);
        }

        if (!onlyInetParams) {
            //Проц, Память и прочее
            log.info("Get System Parameters");
            createPhaseLogInformation(result, "Get System Parameters", null);

            OperatingSystemMXBean systemBean = ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean());
            long totalMemorySize = systemBean.getTotalPhysicalMemorySize();
            long freeMemorySize = systemBean.getFreePhysicalMemorySize();

            result.append("Total Memory Size = ").append(totalMemorySize / (1024 * 1024)).append(" MB \n");
            result.append("Free Memory Size = ").append(freeMemorySize / (1024 * 1024)).append(" MB \n");
            File currentDirectory = new File(currentDirectoryPath);
            result.append("Free Hard Disk Space = ").append(currentDirectory.getFreeSpace()).append(" Byte \n\n");

            // Системные переменные
            Map<String, String> systemVars = System.getenv();
            for (String key : systemVars.keySet()) {
                result.append("\'").append(key).append("\'").append(" set to ").append(systemVars.get(key)).append("\n");
            }
        }

        return result.toString();
    }

    private static void createDiagnosticFile() {
        File diagnosticFile = new File(currentDirectoryPath + DIAGNOSTIC_LOG_FILENAME);

        boolean isDiagnosticFileCreated;
        try {
            isDiagnosticFileCreated = diagnosticFile.exists() || diagnosticFile.createNewFile();
            if (!isDiagnosticFileCreated) {
                throw new IOException("Cannot create diagnostic file");
            }
        } catch (IOException ex) {
            StringBuilder sb = new StringBuilder();
            sb.append("Cannot create log diagnosticFile: ").
                    append(DIAGNOSTIC_LOG_FILENAME).
                    append(" in directory ").
                    append(currentDirectoryPath);
            throw new RuntimeException(sb.toString());
        }

        PrintWriter out = null;
        try {
            out = new PrintWriter(new FileOutputStream(diagnosticFile));

            //начало записи в лог
            logText.delete(0, logText.length()); // очищаем буфер

            logText.append(LOG_DATE_FMT.format(new Date())).
                    append(" Start gathering diagnostic information...").
                    append("\n").
                    append("-------------------------------------------------").
                    append("\n");

            //Фаза 1 - Получение системных переменных и параметров сервера
            createStartPhaseLogInformation(logText, 1, "Get System Information\n");
            String systemInfoStr = collectSystemInformation(true);
            logText.append(systemInfoStr);
            createPhaseFinishLogInformation(logText, 1, systemInfoStr.length() > 0 ? "OK" : "FALSE", "");

            writeToFile(out);

            //Фаза 2 - Сканирование файлов сервера
            createStartPhaseLogInformation(logText, 2, "Scanning Alfresco server files");
            logText.append(collectFiles());
            createPhaseFinishLogInformation(logText, 2, "OK", "");

            writeToFile(out);

            //Фаза 3 - Проверка доступности сервера
            createStartPhaseLogInformation(logText, 3, "Check Server availability\n");
            logText.append(checkServer());
            createPhaseFinishLogInformation(logText, 3, "OK", "See log for details");

            writeToFile(out);

            //Фаза 4 - Сбор информации из бизнес-журнала
            if (isServerAvailable) {
                createStartPhaseLogInformation(logText, 4, "Collect Business Journal information");
                logText.append(collectBusinessJournalRecords());
                createPhaseFinishLogInformation(logText, 4, "OK", "Collect Business Journal information finished");
                writeToFile(out);
            } else {
                logText.append("Server is not available. Cannot collect business journal records.");
                writeToFile(out);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Cannot save log!!!", ex);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    private static void writeToFile(PrintWriter out) {
        out.print(logText.toString());
        out.flush();
        logText.delete(0, logText.length()); // очищаем буфер
    }

    private static void createControlFile() {
        File controlFile = new File(currentDirectoryPath + CONTROL_INFO_FILENAME);

        boolean isControlFileCreated;
        try {
            isControlFileCreated = controlFile.exists() || controlFile.createNewFile();
            if (!isControlFileCreated) {
                throw new IOException("Cannot create control file");
            }
        } catch (IOException ex) {
            StringBuilder sb = new StringBuilder();
            sb.append("Cannot create log controlFile: ").
                    append(CONTROL_INFO_FILENAME).
                    append(" in directory ").
                    append(currentDirectoryPath);
            throw new RuntimeException(sb.toString());
        }

        PrintWriter out = null;
        try {
            out = new PrintWriter(new FileOutputStream(controlFile));

            //начало записи в лог
            //начало записи в лог
            logText.delete(0, logText.length()); // очищаем буфер

            logText.append(LOG_DATE_FMT.format(new Date())).
                    append(" Start gathering information...").
                    append("\n").
                    append("-------------------------------------------------").
                    append("\n");

            log.info("Start gathering information for save control file");

            //Фаза 1 - Получение системных переменных и параметров сервера
            //Фаза 1 - Получение системных переменных и параметров сервера
            createStartPhaseLogInformation(logText, 1, "Get System Information\n");
            String systemInfoStr = collectSystemInformation(false);
            logText.append(systemInfoStr);
            createPhaseFinishLogInformation(logText, 1, systemInfoStr.length() > 0 ? "OK" : "FALSE", "");

            writeToFile(out);

            //Фаза 2 - Проверка доступности сервера
            createStartPhaseLogInformation(logText, 2, "Check Server availability\n");
            logText.append(checkServer());
            createPhaseFinishLogInformation(logText, 2, "OK", "See log for details");
            writeToFile(out);

            //Фаза 3 - Сбор информации с сервисов
            if (isServerAvailable) {
                createStartPhaseLogInformation(logText, 3, "Collect orgStructure information\n");
                logText.append(collectOrgstructureInfo());
                createPhaseFinishLogInformation(logText, 3, "OK", "Collect orgStructure information finished");
                writeToFile(out);
            } else {
                logText.append("Server is not available. Cannot collect orgStructure information.");
                writeToFile(out);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Cannot save log!!!", ex);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    private static void createStartPhaseLogInformation(StringBuilder buffer, double numberOfPhase, String description) {
        log.info(description != null ? description : "");
        buffer.append(LOG_DATE_FMT.format(new Date())).append(" ");
        buffer.append("PHASE ");
        if (numberOfPhase > 0) {
            buffer.append(numberOfPhase);
        }
        buffer.append(" started! ").append("\n");
        buffer.append(description).append("\n");
    }

    private static void createPhaseFinishLogInformation(StringBuilder buffer, double numberOfPhase, String status, String resultMsg) {
        buffer.append("\n");
        buffer.append(LOG_DATE_FMT.format(new Date())).append(" ");
        buffer.append("PHASE ");
        if (numberOfPhase > 0) {
            buffer.append(numberOfPhase);
        }
        buffer.append(" completed. STATUS ").append(status).append("\n");
        if (resultMsg != null && !resultMsg.isEmpty()) {
            buffer.append(resultMsg).append("\n");
        }
        buffer.append("-------------------------------------------------").append("\n");
    }

    private static void createPhaseLogInformation(StringBuilder buffer, String titleMsg, String infoMsg) {
        buffer.append("\n").append(LOG_DATE_FMT.format(new Date())).append(" ");
        if (titleMsg != null) {
            buffer.append(titleMsg).append("\n");
        }
        if (infoMsg != null) {
            buffer.append(infoMsg).append("\n");
        }
    }

    private static String buildFilePath(String root, String[] parts) {
        String resultPath = root;
        for (String part : parts) {
            resultPath += File.separator + part;
        }
        return resultPath;
    }

    private static int sendRequestToURL(HttpClient client, String[] requestURL, StringBuilder buffer) {
        int requestStatus = 503;
        for (String url : requestURL) {
            GetMethod httpGet = null;
            try {
                httpGet = new GetMethod(url);
                requestStatus = client.executeMethod(httpGet);
            } catch (Exception e) {
                log.error("", e);
            } finally {
                buffer.append("Send request to URL ").append(url).append(" STATUS ").append(requestStatus).append("\n");
                if (httpGet != null) {
                    httpGet.releaseConnection();
                }
            }
            if (requestStatus == 200) { // достучались по адресу
                break;
            }
        }
        return requestStatus;
    }

    private static int checkDBConnection(String dbDriver, String dbUser, String dbPass, String dbName, String dbUrl, StringBuilder buffer) {
        String requestURL;
        int requestDBStatus = 503;
        if (dbDriver == null || dbUser == null || dbPass == null || dbName == null || dbUrl == null) {
            buffer.append("Cannot Send request to DB. Check params").append(". STATUS - ").append(requestDBStatus).append("\n");
            return requestDBStatus;
        }
        requestURL = dbUrl.replace("${db.name}", dbName);
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            Class.forName(dbDriver);
            con = DriverManager.getConnection(requestURL, dbUser, dbPass);
            pst = con.prepareStatement("SELECT 1");
            rs = pst.executeQuery();
            requestDBStatus = 200;
        } catch (Exception e) {
            log.error("", e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pst != null) {
                    pst.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                log.error("", ex);
            }
        }
        buffer.append("Send request to URL ").append(dbUrl).append(" STATUS ").append(requestDBStatus).append("\n");
        return requestDBStatus;
    }
}
