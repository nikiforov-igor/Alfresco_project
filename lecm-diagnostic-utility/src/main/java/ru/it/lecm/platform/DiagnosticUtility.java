package ru.it.lecm.platform;

import com.sun.management.OperatingSystemMXBean;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLHandshakeException;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class DiagnosticUtility {
    private static final transient Logger log = LoggerFactory.getLogger(DiagnosticUtility.class);

    //Контрольные Файлы
    private final static String CONTROL_INFO_FILENAME = "utility.log";
    private final static String CONTROL_INFO_HASH_FILENAME = File.separator + "utility.md5";
    private final static String RESULT_FILENAME = File.separator + "utility.zip";

    //Файл с логом бизнес-журнала
    private final static String BJ_LOG_FILENAME = File.separator + "business-journal.csv";
    private final static String GET_BJ_RECORDS_SCRIPT_URL = "/service/lecm/business-journal/api/last-records?count=1000&includeArchive=true";

    //Скрипт выдачи информации по пользователям
    private final static String ORG_LOG_FILENAME = File.separator + "orgstructure.txt";
    private final static String ORG_DIAGRAM_FILENAME = File.separator + "orgstructure.png";
    private final static String ORG_ACL_TREE_FILENAME = File.separator + "aclTree.txt";
    private final static String GET_USERS_INFO_SCRIPT_URL = "/service/lecm/orgstructure/api/getUsersInfo";
    private final static String GET_ORG_DIAGRAM_SCRIPT_URL = "/service/lecm/orgstructure/diagram";
    private final static String GET_ACL_TREE_URL = "/service/lecm/documents/aclTree";

    //файл с конфигом утилиты
    private final static String CONFIG_FILENAME = "utility-properties.cfg";

    //конфиги
    private final static String ALF_SETTINGS_PATH = "alf_settings_path";
    private final static String ALF_DATA_PATH = "alf_data_path";
    private final static String ALF_SERVER_SETTINS = "alf_server_settins";
    private final static String ALF_SERVER_LOGS = "alf_server_logs";
    private final static String ALF_REPO_INSTANCE_PATH = "alf_repo_instance_path";
    private final static String ALF_SHARE_INSTANCE_PATH = "alf_share_instance_path";
    private final static String ALF_SOLR_INSTANCE_PATH = "alf_solr_instance_path";

    private final static String OUTPUT_DIR = "output_dir";
    private final static String ADMIN_LOGIN = "admin_login";
    private final static String ADMIN_PASSWORD = "admin_password";

    //карты с конфигами
    private final static Map<String, String> config = new HashMap<String, String>();
    private final static Map<String, String> alfPropsMap = new HashMap<String, String>();

    //вспомогательный объект
    private final static FileFinder finder = new FileFinder();

    private static String currentDirectoryPath = ".";
    private static String resultsDirectory = ".";
    private static boolean isServerAvailable = false;

    private final static DateFormat dateParser = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");

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
                controlFile = new File(controlFileDir.getAbsolutePath() + File.separator + RESULT_FILENAME);
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
            log.info("Diagnostic Utility Output Path set to {}", currentDirectoryPath);
            resultsDirectory = currentDirectoryPath + File.separator + "diagnostic-results-" + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
            File dataDirectory = new File(resultsDirectory);
            if (!dataDirectory.exists()) {
                boolean logDirectory = dataDirectory.mkdirs();
                if (!logDirectory) {
                    log.error("Error! Unable to create a directory to store the results!");
                    return;
                }
            }
            //запускаем диагностику
            File data = new File(resultsDirectory + File.separator + RESULT_FILENAME);
            startDiagnostic(data);
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
                    //log.info(row);
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
        String settinsDirectory = config.get(ALF_SETTINGS_PATH);
        if (settinsDirectory == null) {
            log.error("Failed read property 'ALF_SETTINGS_PATH' from config file. Please check utility configuration!");
            return false;
        }

        File settingsDir = new File(settinsDirectory);
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
                    //log.info(row);
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

    private static List<File> collectFiles() {

        String alfSettingsPath = config.get(ALF_SETTINGS_PATH);
        String alfDataPath = config.get(ALF_DATA_PATH);
        String alfServerSettins = config.get(ALF_SERVER_SETTINS);
        String alfServerLogs = config.get(ALF_SERVER_LOGS);
        String alfRepoInstancePath = config.get(ALF_REPO_INSTANCE_PATH);
        String alfShareInstancePath = config.get(ALF_SHARE_INSTANCE_PATH);
        String alfSolrInstancePath = config.get(ALF_SOLR_INSTANCE_PATH);

        List<File> dataFiles = new ArrayList<File>();
        // Далее log files
        try {
            dataFiles.addAll(collectLogFiles(alfDataPath));
            dataFiles.addAll(collectLogFiles(alfServerLogs));
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }


        // Далее properties и xml из shared/classes
        try {
            dataFiles.addAll(collectSharedFiles(alfSettingsPath));
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        // Далее solr files
        if (alfDataPath != null && alfSolrInstancePath != null) {
            try {
                dataFiles.addAll(collectSolrFiles(alfDataPath, alfSolrInstancePath));
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        }

        // Далее конфиги томката
        try {
            dataFiles.addAll(collectTomcatConfigs(alfServerSettins));
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        // И наконец проходим по альфреско и шаре
        if (alfRepoInstancePath != null) {
            try {
                dataFiles.addAll(collectAlfrescoFiles(alfRepoInstancePath));
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        }

        if (alfShareInstancePath != null) {
            try {
                dataFiles.addAll(collectShareFiles(alfShareInstancePath));
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        }
        return dataFiles;
    }

    private static List<File> collectLogFiles(String path) {
        log.info("Archive log files started");

        List<File> alfrescoLogs = new ArrayList<File>();
        alfrescoLogs.addAll(findFiles(path, ".*\\.log", true));

        return alfrescoLogs;
    }

    private static List<File> collectSharedFiles(String path) {
        log.info("Archive files from shared/classes directory started");

        List<File> alfrescoSharedClasses = new ArrayList<File>();
        File sharedClassesDir = new File(path);

        alfrescoSharedClasses.addAll(findFiles(sharedClassesDir, ".*\\.xml", true));
        alfrescoSharedClasses.addAll(findFiles(sharedClassesDir, ".*\\.properties", true));
        alfrescoSharedClasses.addAll(findFiles(sharedClassesDir, "lecmlicense", true));

        return alfrescoSharedClasses;
    }

    private static List<File> collectTomcatConfigs(String path) {
        log.info("Archive tomcat config files started");

        List<File> serverConfigs = new ArrayList<File>();
        File confPath = new File(path);

        serverConfigs.addAll(findFiles(confPath, "", true));

        return serverConfigs;
    }

    private static List<File> collectShareFiles(String alfrescoRootDirectory) {
        log.info("Archive Share config files started");

        List<File> shareConfigs = new ArrayList<File>();
        File shareDir;

        //----- log4j.properties -------
        shareDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"WEB-INF", "classes"}));
        shareConfigs.addAll(findFiles(shareDir, "log4j.properties", false));

        shareDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"WEB-INF", "classes", "alfresco", "module"}));
        shareConfigs.addAll(findFiles(shareDir, "module.properties", true));

        shareDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"WEB-INF", "classes", "alfresco", "web-extension"}));
        shareConfigs.addAll(findFiles(shareDir, ".*-share-config-custom\\.xml", true));

        shareDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"WEB-INF", "classes", "alfresco"}));
        shareConfigs.addAll(findFiles(shareDir, ".*-share-context\\.xml|.*\\.config\\.xml", true));

        shareDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"WEB-INF", "classes", "alfresco"}));
        shareConfigs.addAll(findFiles(shareDir, ".*\\.xml", false));

        shareDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"WEB-INF"}));
        shareConfigs.addAll(findFiles(shareDir, ".*\\.xml", false));

        return shareConfigs;
    }

    private static List<File> collectSolrFiles(String dataPath, String instancePath) {
        log.info("Archive solr files started");

        List<File> solrFiles = new ArrayList<File>();
        File solrWorkSpaceDir = new File(buildFilePath(dataPath, new String[]{"solr", "workspace-SpacesStore"}));
        solrFiles.addAll(findFiles(solrWorkSpaceDir, "", true));

        File solrArchiveDir = new File(buildFilePath(dataPath, new String[]{"solr", "archive-SpacesStore"}));
        solrFiles.addAll(findFiles(solrArchiveDir, "", true));

        File solrConfigDir = new File(buildFilePath(instancePath, new String[]{"WEB-INF"}));
        solrFiles.addAll(findFiles(solrConfigDir, ".*\\.xml", true));

        return solrFiles;
    }

    private static List<File> collectAlfrescoFiles(String alfrescoRootDirectory) {
        log.info("Archive Alfresco config files started");

        List<File> alfrescoConfigs = new ArrayList<File>();
        File alfrescoDir;

        //----- log4j.properties -------
        alfrescoDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"WEB-INF", "classes"}));
        alfrescoConfigs.addAll(findFiles(alfrescoDir, "log4j.properties", true));

        //----- *.properties -------
        alfrescoDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"WEB-INF", "classes", "alfresco", "domain"}));
        alfrescoConfigs.addAll(findFiles(alfrescoDir, ".*\\.properties", true));

        alfrescoDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"WEB-INF", "classes", "alfresco", "keystore"}));
        alfrescoConfigs.addAll(findFiles(alfrescoDir, ".*\\.properties", true));

        alfrescoDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"WEB-INF", "classes", "alfresco", "subsystems"}));
        alfrescoConfigs.addAll(findFiles(alfrescoDir, ".*\\.properties", true));

        //----- module ----------
        alfrescoDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"WEB-INF", "classes", "alfresco", "module"}));
        alfrescoConfigs.addAll(findFiles(alfrescoDir, "module.properties|.*-context\\.xml|.*-model\\.xml|.*bpmn20\\.xml", true));

        alfrescoDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"WEB-INF"}));
        alfrescoConfigs.addAll(findFiles(alfrescoDir, ".*\\.xml", false));

        alfrescoDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"WEB-INF", "classes", "alfresco"}));
        alfrescoConfigs.addAll(findFiles(alfrescoDir, ".*\\.xml", false));

        alfrescoDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"WEB-INF", "classes", "alfresco", "model"}));
        alfrescoConfigs.addAll(findFiles(alfrescoDir, ".*\\.xml", false));

        alfrescoDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"WEB-INF", "classes", "alfresco", "bootstrap"}));
        alfrescoConfigs.addAll(findFiles(alfrescoDir, ".*\\.xml", false));

        alfrescoDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"WEB-INF", "classes", "alfresco", "dao"}));
        alfrescoConfigs.addAll(findFiles(alfrescoDir, ".*\\.xml", false));

        alfrescoDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"WEB-INF", "classes", "alfresco", "ibatis"}));
        alfrescoConfigs.addAll(findFiles(alfrescoDir, ".*\\.xml", false));

        alfrescoDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"WEB-INF", "classes", "alfresco", "jgroups"}));
        alfrescoConfigs.addAll(findFiles(alfrescoDir, ".*\\.xml", false));

        alfrescoDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"WEB-INF", "classes", "alfresco", "ml"}));
        alfrescoConfigs.addAll(findFiles(alfrescoDir, ".*\\.xml", false));

        alfrescoDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"WEB-INF", "classes", "alfresco", "mimetype"}));
        alfrescoConfigs.addAll(findFiles(alfrescoDir, ".*\\.xml", false));

        alfrescoDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"WEB-INF", "classes", "alfresco", "mt"}));
        alfrescoConfigs.addAll(findFiles(alfrescoDir, ".*\\.xml", false));

        alfrescoDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"WEB-INF", "classes", "alfresco", "subsystems"}));
        alfrescoConfigs.addAll(findFiles(alfrescoDir, ".*\\.xml", false));

        alfrescoDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"WEB-INF", "classes", "alfresco", "templates"}));
        alfrescoConfigs.addAll(findFiles(alfrescoDir, ".*\\.xml", false));

        alfrescoDir = new File(buildFilePath(alfrescoRootDirectory, new String[]{"WEB-INF", "classes", "alfresco", "workflow"}));
        alfrescoConfigs.addAll(findFiles(alfrescoDir, ".*\\.xml", false));

        return alfrescoConfigs;
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

    private static long writeFilesToArchive(final List<File> filesToWrite, final File zipFile) {
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

            String alfSettingsPath = new File(config.get(ALF_SETTINGS_PATH)).getAbsolutePath();
            String alfDataPath = new File(config.get(ALF_DATA_PATH)).getAbsolutePath();
            String alfServerSettins = new File(config.get(ALF_SERVER_SETTINS)).getAbsolutePath();
            String alfServerLogs = new File(config.get(ALF_SERVER_LOGS)).getAbsolutePath();
            String alfRepoInstancePath = config.get(ALF_REPO_INSTANCE_PATH) == null ? null : new File(config.get(ALF_REPO_INSTANCE_PATH)).getAbsolutePath();
            String alfShareInstancePath = config.get(ALF_SHARE_INSTANCE_PATH) == null ? null : new File(config.get(ALF_SHARE_INSTANCE_PATH)).getAbsolutePath();
            String alfSolrInstancePath = config.get(ALF_SOLR_INSTANCE_PATH) == null ? null : new File(config.get(ALF_SOLR_INSTANCE_PATH)).getAbsolutePath();

            if (!zipFile.exists()) {
                zipFile.createNewFile();
            }
            out = new ZipOutputStream(new FileOutputStream(zipFile));
            out.setLevel(Deflater.DEFAULT_COMPRESSION);
            while (!queue.isEmpty()) {
                File compressedFile = queue.pop();

                String name;
                String fileName = compressedFile.getAbsolutePath();
                if (alfRepoInstancePath != null && fileName.startsWith(alfRepoInstancePath)) {
                    name = fileName.replace(alfRepoInstancePath, "alfresco");
                } else if (alfShareInstancePath != null && fileName.startsWith(alfShareInstancePath)) {
                    name = fileName.replace(alfShareInstancePath, "share");
                } else if (alfSolrInstancePath != null && fileName.startsWith(alfSolrInstancePath)) {
                    name = fileName.replace(alfSolrInstancePath, "solr");
                } else if (fileName.startsWith(alfServerLogs)) {
                    name = fileName.replace(alfServerLogs, "server_logs");
                } else if (fileName.startsWith(alfServerSettins)) {
                    name = fileName.replace(alfServerSettins, "server_settings");
                } else if (fileName.startsWith(alfDataPath)) {
                    name = fileName.replace(alfDataPath, "data");
                } else if (fileName.startsWith(alfSettingsPath)) {
                    name = fileName.replace(alfSettingsPath, "settings");
                } else {
                    name = compressedFile.getName();
                }

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

    private static File collectBusinessJournalRecords() {
        return getFileFromURL(GET_BJ_RECORDS_SCRIPT_URL, currentDirectoryPath + BJ_LOG_FILENAME);
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

    private static File collectOrgstructureInfo() {
        return getFileFromURL(GET_USERS_INFO_SCRIPT_URL, currentDirectoryPath + ORG_LOG_FILENAME);
    }

    private static File collectOrgstructureDiagramInfo() {
        return getFileFromURL(GET_ORG_DIAGRAM_SCRIPT_URL, currentDirectoryPath + ORG_DIAGRAM_FILENAME);
    }

    private static File collectAclTree() {
        return getFileFromURL(GET_ACL_TREE_URL, currentDirectoryPath + ORG_ACL_TREE_FILENAME);
    }

    private static File getFileFromURL(String alfrescoServiceURL, String resultFileName) {
        HttpClient client = new HttpClient();
        client.getParams().setAuthenticationPreemptive(true);
        String login = config.get(ADMIN_LOGIN);
        if (login == null || "".equals(login)) {
            login = "admin";
        }
        Credentials adminCredentials = new UsernamePasswordCredentials(login, config.get(ADMIN_PASSWORD));
        client.getState().setCredentials(AuthScope.ANY, adminCredentials);
        String getRecordsScript = concatAfrescoURL() + alfrescoServiceURL;

        log.info("Attempt to connect to URL {}", getRecordsScript);

        GetMethod httpGet = new GetMethod(getRecordsScript);

        InputStream in = null;
        OutputStream out = null;

        int status = 500;
        byte[] bytes = new byte[1024];
        File receivedFile = null;
        try {
            httpGet.setDoAuthentication(true);
            status = client.executeMethod(httpGet);
            if (status == 200) {
                receivedFile = new File(resultFileName);
                if (!receivedFile.exists()) {
                    receivedFile.createNewFile();
                }

                out = new BufferedOutputStream(new FileOutputStream(receivedFile));
                in = new BufferedInputStream(httpGet.getResponseBodyAsStream());

                int readCount;
                while ((readCount = in.read(bytes)) > 0) {
                    out.write(bytes, 0, readCount);
                }
                out.flush();
            }
        } catch (IOException e) {
            log.error("", e);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
        log.info("Data from " + alfrescoServiceURL + " received with status " + status);
        return receivedFile;
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

        log.info("Getting CPU info");
        getCPUInfo();


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
            //Переменные среды
            Properties properties = System.getProperties();
            for (Object key : properties.keySet()) {
                log.info("{} = {}", key, properties.get(key));
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
        StringBuilder resultPath = new StringBuilder(root);
        for (String part : parts) {
            resultPath.append(File.separator).append(part);
        }
        return resultPath.toString();
    }

    private static int sendRequestToURL(HttpClient client, String[] requestURL) {
        int requestStatus = 0;
        for (String url : requestURL) {
            log.info("Try connect to URL: {}", url);
            GetMethod httpGet = null;
            try {
                httpGet = new GetMethod(url);
                requestStatus = client.executeMethod(httpGet);
            } catch (SSLHandshakeException e) {
                log.error("Check connection without certificate. Right status is 503");
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

    private static void getCPUInfo() {
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            getWindowsCPUInfo();
        } else {
            getLinuxCPUInfo();
        }
    }

    private static void getWindowsCPUInfo() {
        InputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            Process process = Runtime.getRuntime().exec("WMIC CPU Get Name,NumberOfCores,NumberOfLogicalProcessors /Format:List");
            is = process.getInputStream();
            os = new ByteArrayOutputStream();
            int readCount;
            byte[] bytes = new byte[8 * 1024];
            while ((readCount = is.read(bytes)) > 0) {
                os.write(bytes, 0, readCount);
            }
            os.flush();
            Pattern p = Pattern.compile("Name=(.*?)\nNumberOfCores=(.*?)\nNumberOfLogicalProcessors=(.*?)\n", Pattern.DOTALL);
            Matcher m = p.matcher(os.toString());
            int count = 1;
            while (m.find()) {
                log.info("CPU " + count);
                log.info("\t\tName: " + m.group(1).replace("\r", ""));
                log.info("\t\tNumberOfCores: " + m.group(2).replace("\r", ""));
                log.info("\t\tNumberOfLogicalProcessors: " + m.group(3).replace("\r", ""));
                count++;
            }
        } catch (IOException e) {
            log.info("CPUs info is not available");
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
        }
    }

    private static void getLinuxCPUInfo() {
        InputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            Process process = Runtime.getRuntime().exec("cat /proc/cpuinfo");
            is = process.getInputStream();
            os = new ByteArrayOutputStream();
            int readCount;
            byte[] bytes = new byte[8 * 1024];
            while ((readCount = is.read(bytes)) > 0) {
                os.write(bytes, 0, readCount);
            }
            os.flush();
            Pattern p = Pattern.compile("model name\\s*?:(.*?)\n.*?cpu cores\\s*?:(.*?)\n", Pattern.DOTALL);
            Matcher m = p.matcher(os.toString());
            int count = 1;
            while (m.find()) {
                log.info("CPU " + count);
                log.info("\t\tName: " + m.group(1).trim());
                log.info("\t\tNumberOfCores: " + m.group(2).trim());
                count++;
            }
        } catch (IOException e) {
            log.info("CPUs info is not available");
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
        }
    }

    private static void startDiagnostic(File data) {
        List<File> dataFiles = new ArrayList<File>();
        File bjLogFile = null;
        File orgstructureFile = null;
        File orgstructureDiagramFile = null;
        File aclTreeFile = null;
        File controlFile = null;

        log.info("Started collecting diagnostic information ...");
        boolean success;
        //Фаза 1 - Получение системных переменных и параметров сервера
        try {
            log.info("Phase 1 - Collecting system information");
            System.out.println("[" + dateParser.format(new Date()) + "] Phase  1 - Collecting system information");
            success = collectSystemInformation();
            log.info("Phase 1 - Collecting system information finished. Phase result is {}", formatStatusString(success));
            System.out.println("[" + dateParser.format(new Date()) + "] Phase  1 - Collecting system information finished. Phase result is " + formatStatusString(success));
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }

        //Фаза 2 - Сканирование файлов сервера
        try {
            log.info("Phase 2 - Scanning files");
            System.out.println("[" + dateParser.format(new Date()) + "] Phase  2 - Scanning files");
            dataFiles = collectFiles();
            log.info("Phase 2 - Scanning files finished. Phase result is {}", formatStatusString(dataFiles != null));
            System.out.println("[" + dateParser.format(new Date()) + "] Phase  2 - Scanning files finished. Phase result is " + formatStatusString(dataFiles != null));
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }

        //Фаза 3 - Проверка доступности сервера
        try {
            log.info("Phase 3 - Checking the availability of the server");
            System.out.println("[" + dateParser.format(new Date()) + "] Phase  3 - Checking the availability of the server");
            success = checkServer();
            log.info("Phase 3 - Checking the availability of the server finished. Phase result is {}", formatStatusString(success));
            System.out.println("[" + dateParser.format(new Date()) + "] Phase  3 - Checking the availability of the server finished. Phase result is " + formatStatusString(success));
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }

        if (isServerAvailable) {
            //Фаза 4 - Сбор информации из бизнес-журнала
            try {
                log.info("Phase 4 - Collecting business journal records");
                System.out.println("[" + dateParser.format(new Date()) + "] Phase  4 - Collecting business journal records");
                bjLogFile = collectBusinessJournalRecords();
                log.info("Phase 4 - Collecting business journal records. Phase result is {}", formatStatusString(bjLogFile != null));
                System.out.println("[" + dateParser.format(new Date()) + "] Phase  4 - Collecting business journal records. Phase result is " + formatStatusString(bjLogFile != null));
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }
            //Фаза 5 - сбор информации об оргструктуре
            try {
                log.info("Phase 5 - Collecting orgstructure info");
                System.out.println("[" + dateParser.format(new Date()) + "] Phase  5 - Collecting orgstructure info");
                orgstructureFile = collectOrgstructureInfo();
                log.info("Phase 5 - Collecting orgstructure info. Phase result is {}", formatStatusString(orgstructureFile != null));
                System.out.println("[" + dateParser.format(new Date()) + "] Phase  5 - Collecting orgstructure info. Phase result is " + formatStatusString(orgstructureFile != null));
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }
            //Фаза 6 - Диаграмма организации
            try {
                log.info("Phase 6 - Download organization diagram");
                System.out.println("[" + dateParser.format(new Date()) + "] Phase  6 - Download organization diagram");
                orgstructureDiagramFile = collectOrgstructureDiagramInfo();
                log.info("Phase 6 - Download organization diagram. Phase result is {}", formatStatusString(orgstructureDiagramFile != null));
                System.out.println("[" + dateParser.format(new Date()) + "] Phase  6 - Download organization diagram. Phase result is " + formatStatusString(orgstructureDiagramFile != null));
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }
            //Фаза 7 - Дерево прав
            try {
                log.info("Phase 7 - Download acl tree");
                System.out.println("[" + dateParser.format(new Date()) + "] Phase  7 - Download acl tree");
                aclTreeFile = collectAclTree();
                log.info("Phase 7 - Download acl tree. Phase result is {}", formatStatusString(orgstructureDiagramFile != null));
                System.out.println("[" + dateParser.format(new Date()) + "] Phase  7 - Download acl tree. Phase result is " + formatStatusString(orgstructureDiagramFile != null));
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }
        } else {
            log.warn("Server is unavailable. Unable to collect the business journal records and orgstructure info");
        }

        System.out.println("[" + dateParser.format(new Date()) + "] Last Phase - Encode control log file");
        controlFile = new File(CONTROL_INFO_FILENAME);
        if (!controlFile.exists()) {
            log.error("Check file paths. Unable to find control file");
            return;
        }

        if (dataFiles == null) {
            log.error("Data can't be collected!");
        }
        //Добавляем собранную информацию к логам
        if (bjLogFile != null) {
            dataFiles.add(bjLogFile);
        }
        if (orgstructureFile != null) {
            dataFiles.add(orgstructureFile);
        }
        if (orgstructureDiagramFile != null) {
            dataFiles.add(orgstructureDiagramFile);
        }
        if (aclTreeFile != null) {
            dataFiles.add(aclTreeFile);
        }

        dataFiles.add(new File(currentDirectoryPath + File.separator + CONTROL_INFO_FILENAME));

        writeFilesToArchive(dataFiles, data);

        //Удаляем временные файлы
        if (bjLogFile != null) {
            bjLogFile.delete();
        }
        if (orgstructureFile != null) {
            orgstructureFile.delete();
        }
        if (orgstructureDiagramFile != null) {
            orgstructureDiagramFile.delete();
        }
        if (aclTreeFile != null) {
            aclTreeFile.delete();
        }
        //создаем контрольный файл
        try {
            //считаем хеш сумму для лога
            String md5Sum = getMD5Sum(data);

            //сохраняем кеш в рабочую директорию
            PrintWriter outCoded = null;
            try {
                File controlHashFile = new File(resultsDirectory + CONTROL_INFO_HASH_FILENAME);
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
