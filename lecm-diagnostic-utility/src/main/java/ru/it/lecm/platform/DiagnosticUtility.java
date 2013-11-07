package ru.it.lecm.platform;

import com.sun.management.OperatingSystemMXBean;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.management.ManagementFactory;

import javax.net.ssl.SSLHandshakeException;
import java.io.*;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.SecureRandom;
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

    //Файл с логом диагностики
    private final static String DIAGNOSTIC_LOG_FILENAME = File.separator + "utility-diagnostic.log";

    //Контрольные Файлы
    private final static String CONTROL_INFO_FILENAME = File.separator + "utility-control.log";
    private final static String CONTROL_INFO_HASH_FILENAME = File.separator + "utility-control.md5";

    //Названия архивов
    private final static String LOGS_ARCHIVE_NAME = File.separator + "server-logs.zip";
    private final static String SHARED_CLASSES_ARCHIVE_NAME = File.separator + "server-shared-classes.zip";
    private final static String ALFRESCO_CONFIGS_ARCHIVE_NAME = File.separator + "alfresco-configs.zip";
    private final static String SHARE_CONFIGS_ARCHIVE_NAME = File.separator + "share-configs.zip";
    private final static String TOMCAT_CONFIGS_ARCHIVE_NAME = File.separator + "tomcat-configs.zip";
    private final static String SOLR_CONFIGS_ARCHIVE_NAME = File.separator + "solr-configs.zip";

    //Файл с логом бизнес-журнала
    private final static String BJ_LOG_FILENAME = File.separator + "business-journal.csv";
    private final static String GET_BJ_RECORDS_SCRIPT_URL = "/service/lecm/business-journal/api/last-records?count=1000&includeArchive=true";

    //Скрипт выдачи информации по пользователям
    private final static String ORG_LOG_FILENAME = File.separator + "orgstructure.txt";
    private final static String GET_USERS_INFO_SCRIPT_URL = "/service/lecm/orgstructure/api/getUsersInfo";

    //файл с конфигом утилиты
    private final static String CONFIG_FILENAME = "utility-properties.cfg";

    //конфиги
    private final static String ALF_HOME = "alf_home";
    private final static String OUTPUT_DIR = "output_dir";
    private final static String ADMIN_PASSWORD = "admin_password";

    //карты с конфигами
    private final static Map<String, String> config = new HashMap<String, String>();
    private final static Map<String, String> alfPropsMap = new HashMap<String, String>();

    //буфер
    private final static StringBuilder logText = new StringBuilder();

    //вспомогательный объект
    private final static FileFinder finder = new FileFinder();

    private static String currentDirectoryPath = ".";
    private static boolean isServerAvailable = false;

    private final static DateFormat LOG_DATE_FMT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private final static String SALT = "ac19625a23bd1fa61874694464ac9066";

    public static void main(String[] args) {
        BasicConfigurator.configure();

        if (args != null && args.length > 0 && args[0].equals("checkSum")) {
            Scanner sc = new Scanner(System.in);

            File controlFile = null,
                    hashFile = null;

            System.out.print("Input the full path of the control file: ");
            if (sc.hasNext()) {
                String targetFilePath = sc.nextLine();
                controlFile = new File(targetFilePath);
                if (!controlFile.exists()) {
                    log.error("Cannot find file: " + targetFilePath);
                    return;
                }
            }

            System.out.print("Input the full path of the md5 file: ");
            if (sc.hasNext()) {
                String hashFilePath = sc.nextLine();
                hashFile = new File(hashFilePath);
                if (!hashFile.exists()) {
                    log.error("Cannot find file: " + hashFilePath);
                    return;
                }
            }

            boolean isEquals = checkMD5Sum(controlFile, hashFile);
            System.out.print("Result: MD5 sum " + (!isEquals ? " not " : "") + "equals");
        } else {
            // считываем конфигурационный файл. сохраняем параметры в config
            readConfigFile();

            //устанавливаем директорию куда будем сохранять результаты
            // если здиректория задана в конфиге и создана - используем её, иначе - текущую
            if (config.get(OUTPUT_DIR) != null) {
                File outputFile = new File(config.get(OUTPUT_DIR));
                if (outputFile.exists()) {
                    currentDirectoryPath = config.get(OUTPUT_DIR);
                }
            }

            // более читаемая директория - файлы будем собирать туда
            currentDirectoryPath =
                    currentDirectoryPath + File.separator + "diagnostic-results-" + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());

            boolean logDirectory = new File(currentDirectoryPath).mkdirs();
            if (logDirectory) {
                //создаем диагностический файл и пишем в него инфу
                try {
                    createDiagnosticFile();
                } catch (IOException ex) {
                    log.error("", ex);
                }
                //создаем контрольный файл и пишем в него инфу
                createControlFile();
            } else {
                log.error("Не удалось создать директорию для сохранения результатов!");
            }
        }
    }

    private static boolean checkMD5Sum(File controlFile, File hashFile) {
        if (!controlFile.exists() || !hashFile.exists()) {
            log.error("Could not find the specified file(s): " + controlFile.getAbsolutePath() + "," + hashFile.getAbsolutePath());
            return false;
        }

        String actualMD5Sum = getMD5Sum(controlFile);

        log.info("MD5 checksum file equal to " + actualMD5Sum);

        String md5FromFile = "";
        try {
            Scanner scanner = new Scanner(hashFile);
            if (scanner.hasNext()) {
                md5FromFile = scanner.next();
            }
            log.info("Verification checksum is equal to " + actualMD5Sum);
        } catch (IOException ex) {
            log.error("", ex);
        }

        return actualMD5Sum.equals(md5FromFile);
    }

    private static void readConfigFile() {
        BufferedReader br = null;
        try {
            File configFile = new File(CONFIG_FILENAME);
            if (!configFile.exists()) {
                log.error("Ошибка! Не удалось найти конфигурационный файл: " + CONFIG_FILENAME);
            } else {
                //инициализируем конфиг из файла
                br = new BufferedReader(new FileReader(configFile));
                String row;
                while ((row = br.readLine()) != null) {
                    log.info(row);
                    String[] property = row.split("=");
                    if (property.length == 2) {
                        config.put(property[0], property[1]);
                    }
                }
            }
        } catch (Exception ex) {
            log.error("Ошибка при чтении конфигурационного файла: " + CONFIG_FILENAME, ex);
        } finally {
            IOUtils.closeQuietly(br);
        }
    }

    private static boolean checkServer() {
        //прочитать и распарсить настроечный файл альфреско
        String alfrescoRootDirectory = config.get(ALF_HOME);
        if (alfrescoRootDirectory == null) {
            logText.append("Failed read property \'ALF_HOME\' from config file. Please check utility configuration!").append("\n");
            return false;
        }

        File settingsDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"tomcat", "shared", "classes"}));
        List<File> settingsFile = findFiles(settingsDir, "alfresco-global.properties", false);

        File alfrescoProperties = null;
        if (settingsFile != null && settingsFile.size() > 0) {
            alfrescoProperties = settingsFile.get(0);
        }
        if (alfrescoProperties == null || !alfrescoProperties.exists()) {
            logText.append("Failed read alfresco-global.properties. Please check utility configuration!").append("\n");
            return false;
        }

        BufferedReader br = null;
        try {
            //инициализируем конфиг из файла
            br = new BufferedReader(new FileReader(alfrescoProperties));
            logText.append("Read alfresco-global.properties started").append("\n");
            String row;
            while ((row = br.readLine()) != null) {
                if (row.contains("=") && !row.startsWith("#")) {
                    String[] property = row.split("=");
                    alfPropsMap.put(property[0], property[1]);
                    logText.append(row).append("\n");
                }
            }
        } catch (Exception e) {
            log.error("Cannot read alfresco-global.properties file: " + CONFIG_FILENAME);
        } finally {
            IOUtils.closeQuietly(br);
        }

        logText.append("\n");

        //Нужные свойства
        String dbDriver = alfPropsMap.get("db.driver");
        String dbUser = alfPropsMap.get("db.username");
        String dbPass = alfPropsMap.get("db.password");
        String dbName = alfPropsMap.get("db.name");
        String dbUrl = alfPropsMap.get("db.url");
        String dbHost = alfPropsMap.get("db.host");
        String dbPort = alfPropsMap.get("db.port");

        //запрос к tomcat и БД
        HttpClient httpclient = new HttpClient();
        httpclient.getParams().setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT, 5000);

        logText.append("Check Tomcat Server...").append("\n");
        int requestTomcatStatus = sendRequestToURL(httpclient, new String[]{concatAfrescoServerURL(), concatAfrescoLocalhostServerURL()}, logText);

        logText.append("Check Data Base Server...").append("\n");
        int requestDBStatus = checkDBConnection(dbDriver, dbHost, dbPort, dbUser, dbPass, dbName, dbUrl, logText);

        if (requestTomcatStatus != 200 || requestDBStatus != 200) {
            logText.append("Alfresco Server not started. Cannot send request to webapps").append("\n");
            isServerAvailable = false;
        } else {
            isServerAvailable = true;
        }

        //request to alfresco
        logText.append("Check Alfresco...").append("\n");
        sendRequestToURL(httpclient, new String[]{concatAfrescoURL(), concatLocalhostAfrescoURL()}, logText);

        //request to share
        logText.append("Check Share...").append("\n");
        sendRequestToURL(httpclient, new String[]{concatShareURL(), concatLocalhostShareURL()}, logText);

        //request to solr
        logText.append("Check Solr...").append("\n");
        sendRequestToURL(httpclient, new String[]{concatSolrURL(), concatLocalhostSolrURL()}, logText);

        return true;
    }

    private static String collectFiles() {
        StringBuilder logText = new StringBuilder();
        long numberOfArchivedFiles = 0;

        String alfrescoRootDirectory = config.get(ALF_HOME);
        if (alfrescoRootDirectory == null) {
            logText.append("Failed read property \'ALF_HOME\' from config file. Please check utility configuration!").append("\n");
            return logText.toString();
        }

        File alfRoot = new File(alfrescoRootDirectory);
        if (!alfRoot.exists()) {
            logText.append("Failed read Alfresco Root Directory by Path: ").append(alfrescoRootDirectory).append("\n");
            return logText.toString();
        }
        // Далее log files
        numberOfArchivedFiles += collectLogFiles(logText, alfrescoRootDirectory);

        // Далее properties и xml из shared/classes
        numberOfArchivedFiles += collectSharedFiles(logText, alfrescoRootDirectory);

        // Далее solr files
        numberOfArchivedFiles += collectSolrFiles(logText, alfrescoRootDirectory);

        // Далее конфиги томката
        numberOfArchivedFiles += collectTomcatConfigs(logText, alfrescoRootDirectory);

        // И наконец проходим по альфреско и шаре
        numberOfArchivedFiles += collectAlfrescoFiles(logText, alfrescoRootDirectory);
        numberOfArchivedFiles += collectShareFiles(logText, alfrescoRootDirectory);

        createPhaseLogInformation(logText, "Collect files completed. All archived Files:" + numberOfArchivedFiles, null);
        return logText.toString();
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
                        int readCount;
                        while ((readCount = in.read(buffer)) > 0) {
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

    private static void collectBusinessJournalRecords() {

        HttpClient client = new HttpClient();
        client.getParams().setAuthenticationPreemptive(true);

        Credentials adminCredentials = new UsernamePasswordCredentials("admin", config.get(ADMIN_PASSWORD));
        client.getState().setCredentials(AuthScope.ANY, adminCredentials);
        String getRecordsScript = concatAfrescoURL() + GET_BJ_RECORDS_SCRIPT_URL;

        createPhaseLogInformation(logText, "Attempt to connect to URL " + getRecordsScript, null);

        GetMethod httpGet = new GetMethod(getRecordsScript);

        InputStream in = null;
        OutputStream out = null;

        int status = 500;
        byte[] bytes = new byte[1024];
        try {
            File bjLogFile = new File(currentDirectoryPath + BJ_LOG_FILENAME);
            if (!bjLogFile.exists()) {
                bjLogFile.createNewFile();
            }

            httpGet.setDoAuthentication(true);

            status = client.executeMethod(httpGet);

            in = new BufferedInputStream(httpGet.getResponseBodyAsStream());

            out = new BufferedOutputStream(new FileOutputStream(bjLogFile));

            int readCount;
            while ((readCount = in.read(bytes)) > 0) {
                out.write(bytes, 0, readCount);
            }
            out.flush();
        } catch (IOException e) {
            log.error("", e);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }

        createPhaseLogInformation(logText, "Business journal log was " + (200 <= status && status < 300 ? "" : "not")
                + "written to file " + currentDirectoryPath + BJ_LOG_FILENAME, "STATUS CODE " + status);
    }

    private static String concatAfrescoServerURL() {
        return alfPropsMap.get("alfresco.protocol") + "://" +
                alfPropsMap.get("alfresco.host") + ":" + alfPropsMap.get("alfresco.port");
    }

    private static String concatAfrescoLocalhostServerURL() {
        return alfPropsMap.get("alfresco.protocol") + "://localhost:" + alfPropsMap.get("alfresco.port");
    }

    private static String concatAfrescoURL() {
        return alfPropsMap.get("alfresco.protocol") + "://" +
                alfPropsMap.get("alfresco.host") + ":" + alfPropsMap.get("alfresco.port") + "/" +
                alfPropsMap.get("alfresco.context");
    }

    private static String concatLocalhostAfrescoURL() {
        return alfPropsMap.get("alfresco.protocol") + "://localhost:" +
                alfPropsMap.get("alfresco.port") + "/" +
                alfPropsMap.get("alfresco.context");
    }

    private static String concatShareURL() {
        return alfPropsMap.get("share.protocol") + "://" +
                alfPropsMap.get("share.host") + ":" + alfPropsMap.get("share.port") + "/" +
                alfPropsMap.get("share.context");
    }

    private static String concatLocalhostShareURL() {
        return alfPropsMap.get("share.protocol") + "://localhost:" +
                alfPropsMap.get("share.port") + "/" +
                alfPropsMap.get("share.context");
    }


    private static String concatSolrURL() {
        return "https://" +
                alfPropsMap.get("alfresco.host") + ":" + alfPropsMap.get("solr.port.ssl") + "/solr";
    }

    private static String concatLocalhostSolrURL() {
        return "https://localhost:" +
                alfPropsMap.get("solr.port.ssl") + "/solr";
    }

    private static String collectOrgstructureInfo() {
        HttpClient client = new HttpClient();
        client.getParams().setAuthenticationPreemptive(true);

        Credentials adminCredentials = new UsernamePasswordCredentials("admin", config.get(ADMIN_PASSWORD));
        client.getState().setCredentials(AuthScope.ANY, adminCredentials);
        String getRecordsScript = concatAfrescoURL() + GET_USERS_INFO_SCRIPT_URL;

        createPhaseLogInformation(logText, "Attempt to connect to URL " + getRecordsScript, null);

        GetMethod httpGet = new GetMethod(getRecordsScript);

        InputStream in = null;
        OutputStream out = null;

        int status = 500;
        byte[] bytes = new byte[1024];
        try {
            File orgFile = new File(currentDirectoryPath + ORG_LOG_FILENAME);
            if (!orgFile.exists()) {
                orgFile.createNewFile();
            }

            out = new BufferedOutputStream(new FileOutputStream(orgFile));

            httpGet.setDoAuthentication(true);
            status = client.executeMethod(httpGet);
            in = new BufferedInputStream(httpGet.getResponseBodyAsStream());

            int readCount;
            while ((readCount = in.read(bytes)) > 0) {
                out.write(bytes, 0, readCount);
            }
            out.flush();
        } catch (IOException e) {
            log.error("", e);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }

        createPhaseLogInformation(logText, "Users Info log was " + (200 <= status && status < 300 ? "" : "not")
                + "written to file " + currentDirectoryPath + CONTROL_INFO_FILENAME, "STATUS CODE " + status);
        return out.toString();
    }

    private static boolean collectSystemInformation(boolean onlyInetParams) {
        // IP адрес сервера
        createPhaseLogInformation(logText, "Получение сетевой информации о сервере", null);
        boolean success = false;
        InetAddress addr;
        try {
            addr = InetAddress.getLocalHost();
            logText.append("IP адрес: ").append(addr.getHostAddress()).append("\n");
            //logText.append("Canonic Name: ").append(addr.getCanonicalHostName()).append("\n");
            logText.append("Имя: ").append(addr.getHostName()).append("\n");
            success = true;
        } catch (UnknownHostException e) {
            log.error(e.getMessage(), e);
        }

        if (!onlyInetParams) {
            //Проц, Память и прочее
            createPhaseLogInformation(logText, "Получение системных переменных", null);

            try {
                OperatingSystemMXBean systemBean = ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean());
                long totalMemorySize = systemBean.getTotalPhysicalMemorySize();
                long freeMemorySize = systemBean.getFreePhysicalMemorySize();

                logText.append("Всего памяти = ").append(totalMemorySize / (1024 * 1024)).append(" MB \n");
                logText.append("Свободно памяти = ").append(freeMemorySize / (1024 * 1024)).append(" MB \n");
                File currentDirectory = new File(currentDirectoryPath);
                logText.append("Свободно места на жестком диске = ").append(currentDirectory.getFreeSpace()).append(" Byte \n\n");

                // Системные переменные
                Map<String, String> systemVars = System.getenv();
                for (String key : systemVars.keySet()) {
                    logText.append("\'").append(key).append("\'").append(" = ").append(systemVars.get(key)).append("\n");
                }
                success = true;
            } catch (Exception e) {
                createPhaseLogInformation(logText, "Не удалось получить системные переменные", null);
                success = false;
            }
        }

        return success;
    }

    private static void createDiagnosticFile() throws IOException {
        File diagnosticFile = new File(currentDirectoryPath + DIAGNOSTIC_LOG_FILENAME);

        boolean isDiagnosticFileCreated;
        isDiagnosticFileCreated = diagnosticFile.exists() || diagnosticFile.createNewFile();
        if (!isDiagnosticFileCreated) {
            throw new RuntimeException("Не удалось создать файл для сохранения диагностической информации!" + diagnosticFile.getAbsolutePath());
        }

        PrintWriter out = null;
        try {
            out = new PrintWriter(new FileOutputStream(diagnosticFile));

            //начало записи в лог
            logText.delete(0, logText.length()); // очищаем буфер

            logText.append(LOG_DATE_FMT.format(new Date())).
                    append(" Начат сбор диагностической информации...").
                    append("\n").
                    append("-------------------------------------------------").
                    append("\n");

            writeToFile(out);

            boolean success;

            //Фаза 1 - Получение системных переменных и параметров сервера
            createStartPhaseLogInformation(logText, 1, "Сбор системной информации");
            success = collectSystemInformation(true);
            createPhaseFinishLogInformation(logText, 1, success  ? "Успех" : "Неудача", "");
            writeToFile(out);

            //Фаза 2 - Сканирование файлов сервера
            createStartPhaseLogInformation(logText, 2, "Сканирование файлов Alfresco и Share");
            collectFiles();
            createPhaseFinishLogInformation(logText, 2, "OK", "");
            writeToFile(out);

            //Фаза 3 - Проверка доступности сервера
            createStartPhaseLogInformation(logText, 3, "Проверка доступности сервера\n");
            success = checkServer();
            createPhaseFinishLogInformation(logText, 3, success  ? "Успех" : "Неудача", "");
            writeToFile(out);

            //Фаза 4 - Сбор информации из бизнес-журнала
            if (isServerAvailable) {
                createStartPhaseLogInformation(logText, 4, "Начат сбор записей бизнес-журнала");
                collectBusinessJournalRecords();
                createPhaseFinishLogInformation(logText, 4, "OK", "Сбор записей бизнес-журнала завершен");
                writeToFile(out);
            } else {
                logText.append("Сервер недоступен. Невозможно собрать записи бизнес-журнала.");
                writeToFile(out);
            }

        } catch (IOException ex) {
            throw new RuntimeException("Не удалось сохранить лог-файл!", ex);
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
                    append(" Начат сбор контрольной информации...").
                    append("\n").
                    append("-------------------------------------------------").
                    append("\n");

            writeToFile(out);

            //Фаза 1 - Получение системных переменных и параметров сервера
            createStartPhaseLogInformation(logText, 1, "Получение системной информации\n");
            boolean success = collectSystemInformation(false);
            createPhaseFinishLogInformation(logText, 1, success ? "Успех" : "Неудача", "");

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

            out.flush();

        } catch (IOException ex) {
            throw new RuntimeException("Cannot save log!!!", ex);
        } finally {
            IOUtils.closeQuietly(out);
        }

        String md5Sum = getMD5Sum(controlFile);
        PrintWriter outCoded = null;
        try {
            File controlHashFile = new File(currentDirectoryPath + CONTROL_INFO_HASH_FILENAME);
            if (!controlHashFile.exists()) {
                controlHashFile.createNewFile();
            }

            outCoded = new PrintWriter(controlHashFile);
            if (md5Sum != null) {
                outCoded.write(md5Sum);
            }
        } catch (Exception ex) {
            log.error("", ex);
        } finally {
            IOUtils.closeQuietly(outCoded);
        }
    }

    private static String getMD5Sum(File controlFile) {
        byte[] b = new byte[8192];

        InputStream inNotCoded = null;
        String checksum = null;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            inNotCoded = new FileInputStream(controlFile);

            int readCount;
            while ((readCount = inNotCoded.read(b)) > 0) {
                md.update(b, 0, readCount);
                md.update(SALT.getBytes(), 0, SALT.getBytes().length);
            }

            byte[] hash = md.digest();
            checksum = new BigInteger(1, hash).toString(16);
        } catch (Exception ioEx) {
            log.error("", ioEx);
        } finally {
            IOUtils.closeQuietly(inNotCoded);
        }

        return checksum;
    }

    private static void createStartPhaseLogInformation(StringBuilder buffer, double numberOfPhase, String description) {
        log.info(description != null ? description : "");
        buffer.append(LOG_DATE_FMT.format(new Date())).append(" ");
        buffer.append("Этап ");
        if (numberOfPhase > 0) {
            buffer.append(numberOfPhase);
        }
        buffer.append(" запущен! ").append("\n");
        buffer.append(description).append("\n");
    }

    private static void createPhaseFinishLogInformation(StringBuilder buffer, double numberOfPhase, String status, String resultMsg) {
        log.info(resultMsg != null ? resultMsg : "");
        buffer.append("\n");
        buffer.append(LOG_DATE_FMT.format(new Date())).append(" ");
        buffer.append("Этап ");
        if (numberOfPhase > 0) {
            buffer.append(numberOfPhase);
        }
        buffer.append(" завершен. Статус выполнения ").append(status).append("\n");
        if (resultMsg != null && !resultMsg.isEmpty()) {
            buffer.append(resultMsg).append("\n");
        }
        buffer.append("-------------------------------------------------").append("\n");
    }

    private static void createPhaseLogInformation(StringBuilder buffer, String titleMsg, String infoMsg) {
        buffer.append("\n").append(LOG_DATE_FMT.format(new Date())).append(" ");
        if (titleMsg != null) {
            buffer.append(titleMsg).append("\n");
            log.info(titleMsg + "\n");
        }
        if (infoMsg != null) {
            buffer.append(infoMsg).append("\n");
            log.info(infoMsg + "\n");
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
            } catch (SSLHandshakeException e1) {

            } catch (Exception e) {
                log.error("", e);
            } finally {
                buffer.append("Send request to URL ").append(url).append(" STATUS ").append(requestStatus).append("\n\n");
                if (httpGet != null) {
                    httpGet.releaseConnection();
                }
            }
            if (requestStatus == 200 || requestStatus == 503) { // достучались по адресу
                break;
            }
        }
        return requestStatus;
    }

    private static int checkDBConnection(String dbDriver, String dbHost, String dbPort, String dbUser, String dbPass, String dbName, String dbUrl, StringBuilder buffer) {
        String requestURL;
        int requestDBStatus = 503;
        if (dbDriver == null || dbUser == null || dbPass == null || dbUrl == null) {
            buffer.append("Cannot Send request to DB. Check params").append(". STATUS - ").append(requestDBStatus).append("\n");
            return requestDBStatus;
        }

        requestURL = dbUrl;
        if (dbName != null) {
            requestURL = requestURL.replace("${db.name}", dbName);
        }
        if (dbHost != null) {
            requestURL = requestURL.replace("${db.host}", dbHost);
        }
        if (dbPort != null) {
            requestURL = requestURL.replace("${db.port}", dbPort);
        }
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
        buffer.append("Send request to URL ").append(requestURL).append(" STATUS ").append(requestDBStatus).append("\n\n");
        return requestDBStatus;
    }

    private static byte[] getSalt() {
        SecureRandom sr;
        try {
            sr = SecureRandom.getInstance("SHA1PRNG", "SUN");

            byte[] salt = new byte[16];
            sr.nextBytes(salt);

            return salt;
        } catch (Exception e) {
            log.error("", e);
        }

        return null;
    }
}
