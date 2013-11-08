package ru.it.lecm.platform;

import com.sun.management.OperatingSystemMXBean;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.management.ManagementFactory;

import java.io.*;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class DiagnosticUtility {
    private static final transient Logger log = LoggerFactory.getLogger(DiagnosticUtility.class);

    //Контрольные Файлы
    private final static String CONTROL_INFO_FILENAME = "utility.log";
    private final static String CONTROL_INFO_HASH_FILENAME = File.separator + "utility.md5";

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

    //вспомогательный объект
    private final static FileFinder finder = new FileFinder();

    private static String currentDirectoryPath = ".";
    private static boolean isServerAvailable = false;

    private final static String SALT = "ac19625a23bd1fa61874694464ac9066";

    public static void main(String[] args) {
        if (args != null && args.length > 0 && args[0].equals("checkSum")) {
            Scanner sc = new Scanner(System.in);

            File controlFileDir = null,
                    controlFile,
                    hashFile;

            System.out.print("Input the directory path with utility files: ");
            if (sc.hasNext()) {
                String targetFilePath = sc.nextLine();
                controlFileDir = new File(targetFilePath);
                if (!controlFileDir.exists()) {
                    log.error("Cannot find file: " + targetFilePath);
                    return;
                }
                if (!controlFileDir.isDirectory()) {
                    log.error("File by Path: {} is not Directory!", targetFilePath);
                    return;
                }
            }

            if (controlFileDir != null) {
                controlFile = new File(controlFileDir.getAbsolutePath() + File.separator + CONTROL_INFO_FILENAME);
                hashFile = new File(controlFileDir.getAbsolutePath() + File.separator + CONTROL_INFO_HASH_FILENAME);

                boolean isEquals = checkMD5Sum(controlFile, hashFile);
                System.out.print("Result: MD5 sum " + (!isEquals ? " NOT " : "") + "EQUALS");
            } else {
                log.error("Directory is NULL");
            }
        } else {
            // считываем конфигурационный файл. сохраняем параметры в config
            log.info("Diagnostic Utility started");

            readConfigFile();

            //устанавливаем директорию куда будем сохранять результаты
            // если здиректория задана в конфиге и создана - используем её, иначе - текущую
            if (config.get(OUTPUT_DIR) != null) {
                File outputFile = new File(config.get(OUTPUT_DIR));
                if (outputFile.exists()) {
                    currentDirectoryPath = outputFile.getAbsolutePath();
                }
            }

            // более читаемая директория - файлы будем собирать туда
            currentDirectoryPath =
                    currentDirectoryPath + File.separator + "diagnostic-results-" + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());

            log.info("Diagnostic Utility Output Path set to {}", currentDirectoryPath);

            boolean logDirectory = new File(currentDirectoryPath).mkdirs();
            if (logDirectory) {
                //запускаем диагностику
                startDiagnostic();
            } else {
                log.error("Error! Unable to create a directory to store the results!");
            }
        }
    }

    private static boolean checkMD5Sum(File controlFile, File hashFile) {
        if (!controlFile.exists() || !hashFile.exists()) {
            log.error("Could not find the specified file(s): " + controlFile.getAbsolutePath() + "," + hashFile.getAbsolutePath());
            return false;
        }

        String actualMD5Sum = getMD5Sum(controlFile);

        //log.info("MD5 checksum file is equal to " + actualMD5Sum);

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
        log.info("Start reading config file");
        BufferedReader br = null;
        try {
            File configFile = new File(CONFIG_FILENAME);
            if (!configFile.exists()) {
                log.error("Error! Could not find a config file: " + CONFIG_FILENAME);
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
            log.error("An error occurred while reading the configuration file: " + CONFIG_FILENAME, ex);
        } finally {
            IOUtils.closeQuietly(br);
        }
        log.info("Reading config file finished");
    }

    private static boolean checkServer() {
        //прочитать и распарсить настроечный файл альфреско
        String alfrescoRootDirectory = config.get(ALF_HOME);
        if (alfrescoRootDirectory == null) {
            log.error("Failed read property 'alf_home' from config file. Please check utility configuration!");
            return false;
        }

        File settingsDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"tomcat", "shared", "classes"}));
        List<File> settingsFile = findFiles(settingsDir, "alfresco-global.properties", false);

        File alfrescoProperties = null;
        if (settingsFile != null && settingsFile.size() > 0) {
            alfrescoProperties = settingsFile.get(0);
        }
        if (alfrescoProperties == null || !alfrescoProperties.exists()) {
            log.error("Failed read alfresco-global.properties. Please check utility configuration!");
            return false;
        }

        BufferedReader br = null;
        try {
            //инициализируем конфиг из файла
            br = new BufferedReader(new FileReader(alfrescoProperties));
            log.info("Read alfresco-global.properties started");
            String row;
            while ((row = br.readLine()) != null) {
                if (row.contains("=") && !row.startsWith("#")) {
                    String[] property = row.split("=");
                    alfPropsMap.put(property[0], property[1]);
                    log.info(row);
                }
            }
        } catch (Exception e) {
            log.error("Cannot read alfresco-global.properties file: " + CONFIG_FILENAME, e);
        } finally {
            IOUtils.closeQuietly(br);
        }

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

        log.info("Check Tomcat Server...");
        int requestTomcatStatus = sendRequestToURL(httpclient, new String[]{concatAfrescoServerURL(), concatAfrescoLocalhostServerURL()});

        log.info("Check Data Base Server...");
        int requestDBStatus = checkDBConnection(dbDriver, dbHost, dbPort, dbUser, dbPass, dbName, dbUrl);

        if (requestTomcatStatus != 200 || requestDBStatus != 200) {
            log.error("Alfresco Server not started. Tomcat Status: {}, DB Server Status: {}.", requestTomcatStatus, requestDBStatus);
            isServerAvailable = false;
        } else {
            isServerAvailable = true;

            //request to alfresco
            log.info("Check Alfresco...");
            int status1, status2;
            status1 = sendRequestToURL(httpclient, new String[]{concatAfrescoURL(), concatLocalhostAfrescoURL()});

            //request to share
            log.info("Check Share...");
            status2 = sendRequestToURL(httpclient, new String[]{concatShareURL(), concatLocalhostShareURL()});

            //request to solr
            log.info("Check Solr...");
            sendRequestToURL(httpclient, new String[]{concatSolrURL(), concatLocalhostSolrURL()});

            return (status1 != 503 && status2 != 503);
        }

        return false;
    }

    private static boolean collectFiles() {
        long numberOfArchivedFiles = 0;

        String alfrescoRootDirectory = config.get(ALF_HOME);
        if (alfrescoRootDirectory == null) {
            log.error("Failed read property 'alf_home' from config file. Please check utility configuration!");
            return false;
        }

        File alfRoot = new File(alfrescoRootDirectory);
        if (!alfRoot.exists()) {
            log.error("Failed read Alfresco Root Directory by Path: {}", alfrescoRootDirectory);
            return false;
        }
        // Далее log files
        try {
            numberOfArchivedFiles += collectLogFiles(alfrescoRootDirectory);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        // Далее properties и xml из shared/classes
        try {
            numberOfArchivedFiles += collectSharedFiles(alfrescoRootDirectory);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        // Далее solr files
        try {
            numberOfArchivedFiles += collectSolrFiles(alfrescoRootDirectory);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        // Далее конфиги томката
        try {
            numberOfArchivedFiles += collectTomcatConfigs(alfrescoRootDirectory);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        // И наконец проходим по альфреско и шаре
        try {
            numberOfArchivedFiles += collectAlfrescoFiles(alfrescoRootDirectory);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        try {
            numberOfArchivedFiles += collectShareFiles(alfrescoRootDirectory);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        log.info("All archived Files: {}", numberOfArchivedFiles);
        return true;
    }

    private static long collectLogFiles(String alfrescoRootDirectory) {
        log.info("Archive log files started");

        List<File> alfrescoLogs = new ArrayList<File>();
        alfrescoLogs.addAll(findFiles(alfrescoRootDirectory, ".*\\.log", true));

        return writeFilesToArchive(alfrescoLogs, new File(alfrescoRootDirectory), new File(currentDirectoryPath + LOGS_ARCHIVE_NAME));
    }

    private static long collectSharedFiles(String alfrescoRootDirectory) {
        log.info("Archive files from shared/classes directory started");

        List<File> alfrescoSharedClasses = new ArrayList<File>();
        File sharedClassesDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"tomcat", "shared", "classes"}));

        alfrescoSharedClasses.addAll(findFiles(sharedClassesDir, ".*\\.xml", true));
        alfrescoSharedClasses.addAll(findFiles(sharedClassesDir, ".*\\.properties", true));

        return writeFilesToArchive(alfrescoSharedClasses, new File(alfrescoRootDirectory), new File(currentDirectoryPath + SHARED_CLASSES_ARCHIVE_NAME));
    }

    private static long collectTomcatConfigs(String alfrescoRootDirectory) {
        log.info("Archive tomcat config files started");

        List<File> tomcatConfigs = new ArrayList<File>();
        File tomcatDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"tomcat", "conf"}));

        tomcatConfigs.addAll(findFiles(tomcatDir, "", true));

        return writeFilesToArchive(tomcatConfigs, new File(alfrescoRootDirectory), new File(currentDirectoryPath + TOMCAT_CONFIGS_ARCHIVE_NAME));
    }

    private static long collectShareFiles(String alfrescoRootDirectory) {
        log.info("Archive Share config files started");

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

        return writeFilesToArchive(shareConfigs, new File(alfrescoRootDirectory), new File(currentDirectoryPath + SHARE_CONFIGS_ARCHIVE_NAME));
    }

    private static long collectSolrFiles(String alfrescoRootDirectory) {
        log.info("Archive solr files started");

        List<File> solrFiles = new ArrayList<File>();
        File solrWorkSpaceDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"alf_data", "solr", "workspace-SpacesStore"}));
        solrFiles.addAll(findFiles(solrWorkSpaceDir, "", true));

        File solrArchiveDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"alf_data", "solr", "archive-SpacesStore"}));
        solrFiles.addAll(findFiles(solrArchiveDir, "", true));

        File solrConfigDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"tomcat", "webapps", "solr", "WEB-INF"}));
        solrFiles.addAll(findFiles(solrConfigDir, ".*\\.xml", true));

        return writeFilesToArchive(solrFiles, new File(alfrescoRootDirectory), new File(currentDirectoryPath + SOLR_CONFIGS_ARCHIVE_NAME));
    }

    private static long collectAlfrescoFiles(String alfrescoRootDirectory) {
        log.info("Archive Alfresco config files started");

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

        return writeFilesToArchive(alfrescoConfigs, new File(alfrescoRootDirectory), new File(currentDirectoryPath + ALFRESCO_CONFIGS_ARCHIVE_NAME));
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

    private static long writeFilesToArchive(final List<File> filesToWrite, final File alfrescoRoot, final File zipFile) {
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
                        log.error(ex.getMessage(), ex);
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
        if (isOk && zipFile.exists()) {
            log.info("Number of files archived: {}", numberOfArchivedFiles);
            log.info("Resulted archive created by path: {}", zipFile.getAbsolutePath());
        } else {
            log.error("Some error occured! Unable to archive files");
        }

        return numberOfArchivedFiles;
    }

    private static boolean collectBusinessJournalRecords() {
        boolean success = true;

        HttpClient client = new HttpClient();
        client.getParams().setAuthenticationPreemptive(true);

        Credentials adminCredentials = new UsernamePasswordCredentials("admin", config.get(ADMIN_PASSWORD));
        client.getState().setCredentials(AuthScope.ANY, adminCredentials);
        String getRecordsScript = concatAfrescoURL() + GET_BJ_RECORDS_SCRIPT_URL;

        log.info("Attempt to connect to URL {}", getRecordsScript);

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
            success = false;
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }

        log.info("Business journal log was " + (200 <= status && status < 300 ? "" : "not") + "written to file " + currentDirectoryPath + BJ_LOG_FILENAME);
        return success;
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

    private static boolean collectOrgstructureInfo() {
        boolean success = true;
        HttpClient client = new HttpClient();
        client.getParams().setAuthenticationPreemptive(true);

        Credentials adminCredentials = new UsernamePasswordCredentials("admin", config.get(ADMIN_PASSWORD));
        client.getState().setCredentials(AuthScope.ANY, adminCredentials);
        String getRecordsScript = concatAfrescoURL() + GET_USERS_INFO_SCRIPT_URL;

        log.info("Attempt to connect to URL {}", getRecordsScript);

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
            success = false;
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }

        log.info("Users Info log was " + (200 <= status && status < 300 ? "" : "not") + "written to file " + currentDirectoryPath + CONTROL_INFO_FILENAME);

        return success;
    }

    private static boolean collectSystemInformation() {
        // IP адрес сервера
        log.info("Getting a network server information");
        boolean success1, success2;
        InetAddress addr;
        try {
            addr = InetAddress.getLocalHost();
            log.info("Server IP - {}", addr.getHostAddress());
            log.info("Server Name - {}", addr.getHostName());
            success1 = true;
        } catch (UnknownHostException e) {
            success1 = false;
            log.error("Unable to get network server information", e.getMessage());
        }
        log.info("Getting a network server information. Status: {}", formatStatusString(success1));


        log.info("Getting system variables");
        try {
            OperatingSystemMXBean systemBean = ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean());
            long totalMemorySize = systemBean.getTotalPhysicalMemorySize();
            long freeMemorySize = systemBean.getFreePhysicalMemorySize();

            log.info("Total Memory - {}", totalMemorySize / (1024 * 1024) + " MB");
            log.info("Free Memory - {}", freeMemorySize / (1024 * 1024) + " MB");

            File currentDirectory = new File(currentDirectoryPath);
            log.info("Free hard disk space - {}", currentDirectory.getFreeSpace() + " Byte");

            // Системные переменные
            Map<String, String> systemVars = System.getenv();
            for (String key : systemVars.keySet()) {
                log.info("{} = {}", key, systemVars.get(key));
            }
            success2 = true;
        } catch (Exception e) {
            log.error("Unable to get system variables", e.getMessage());
            success2 = false;
        }
        log.info("Getting system variables. Status: {}", formatStatusString(success2));

        return success1 || success2;
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
            log.error(ioEx.getMessage(), ioEx);
        } finally {
            IOUtils.closeQuietly(inNotCoded);
        }

        return checksum;
    }

    private static String buildFilePath(String root, String[] parts) {
        String resultPath = root;
        for (String part : parts) {
            resultPath += File.separator + part;
        }
        return resultPath;
    }

    private static int sendRequestToURL(HttpClient client, String[] requestURL) {
        int requestStatus = 503;
        for (String url : requestURL) {
            log.info("Try connect to URL: {}", url);
            GetMethod httpGet = null;
            try {
                httpGet = new GetMethod(url);
                requestStatus = client.executeMethod(httpGet);
            } catch (Exception e) {
                log.error("Unable connect to URL", e);
            } finally {
                log.info("Send request to URL {}. Status Code = {}", url, requestStatus);
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

    private static int checkDBConnection(String dbDriver, String dbHost, String dbPort, String dbUser, String dbPass, String dbName, String dbUrl) {
        String requestURL;
        int requestDBStatus = 503;
        if (dbDriver == null || dbUser == null || dbPass == null || dbUrl == null) {
            log.error("Cannot Send request to DB. Check db params");
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
        log.info("Send request to DB Server to URL {}. Status: {}", requestURL, requestDBStatus);
        return requestDBStatus;
    }

    private static void startDiagnostic() {
        log.info("Started collecting diagnostic information ...");
        boolean success;
        //Фаза 1 - Получение системных переменных и параметров сервера
        try {
            log.info("Phase 1 - Collecting system information");
            success = collectSystemInformation();
            log.info("Phase 1 - Collecting system information finished. Phase result is {}", formatStatusString(success));
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }

        //Фаза 2 - Сканирование файлов сервера
        try {
            log.info("Phase 2 - Scanning files");
            success = collectFiles();
            log.info("Phase 2 - Scanning files finished. Phase result is {}", formatStatusString(success));
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }

        //Фаза 3 - Проверка доступности сервера
        try {
            log.info("Phase 3 - Checking the availability of the server");
            success = checkServer();
            log.info("Phase 3 - Checking the availability of the server finished. Phase result is {}", formatStatusString(success));
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }

        if (isServerAvailable) {
            //Фаза 4 - Сбор информации из бизнес-журнала
            try {
                log.info("Phase 4 - Collecting business journal records");
                success = collectBusinessJournalRecords();
                log.info("Phase 4 - Collecting business journal records. Phase result is {}", formatStatusString(success));
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }
            //Фаза 5 - Проверка доступности сервера
            try {
                log.info("Phase 5 - Collecting orgstructure info");
                success = collectOrgstructureInfo();
                log.info("Phase 5 - Collecting orgstructure info. Phase result is {}", formatStatusString(success));
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }
        } else {
            log.warn("Server is unavailable. Unable to collect the business journal records and orgstructure info");
        }

        log.info("Last Phase - Encode control log file");
        try {
            File controlFile = new File(CONTROL_INFO_FILENAME);
            if (!controlFile.exists()) {
                log.error("Check file paths. Unable to find control file");
                return;
            }
            File destFile = new File(currentDirectoryPath + File.separator + CONTROL_INFO_FILENAME);

            //копируем наш лог из рабочей директории к остальным файлам
            FileUtils.copyFile(controlFile, destFile);

            //считаем хеш сумму для рабочей копии лога
            String md5Sum = getMD5Sum(destFile);

            //сохраняем кеш в рабочую директорию
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
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    private static String formatStatusString(boolean success) {
        return success ? "SUCCESS" : "FAILURE";
    }
}
