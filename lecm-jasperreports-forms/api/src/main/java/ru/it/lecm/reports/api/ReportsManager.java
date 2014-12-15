package ru.it.lecm.reports.api;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.FileNameValidator;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.dictionary.beans.XMLExportBean;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.reports.api.model.DAO.ReportContentDAO;
import ru.it.lecm.reports.api.model.DAO.ReportEditorDAO;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.api.model.ReportFileData;
import ru.it.lecm.reports.model.impl.ReportDefaultsDesc;
import ru.it.lecm.reports.model.impl.ReportTemplate;
import ru.it.lecm.reports.model.impl.ReportType;
import ru.it.lecm.reports.utils.ParameterMapper;
import ru.it.lecm.reports.utils.Utils;
import ru.it.lecm.reports.xml.DSXMLProducer;

import java.io.*;
import java.util.*;

/**
 * Биновый интерфейс для работы с шаблонами зарегистрированных отчётов.
 *
 * @author rabdullin
 */
public class ReportsManager {

    static final transient Logger logger = LoggerFactory.getLogger(ReportsManager.class);

    final public static String DEFAULT_REPORT_TYPE = ReportType.RTYPE_MNEMO_JASPER;
    final public static String DEFAULT_REPORT_EXTENSION = ".jrxml";
    final public static String DEFAULT_REPORT_TEMPLATE = "jreportCommonTemplate.jrxml.gen";
    final public static String DEFAULT_SUB_REPORT_TEMPLATE = "sub-jreportCommonTemplate.jrxml.gen";

    /**
     * Список зарегистрирванных отчётов
     */
    private Map<String, ReportDescriptor> descriptors;

    private ReportEditorDAO reportEditorDAO; // хранилище отчётов редактора
    private ReportContentDAO contentFileDAO; // файлы готовых отчётов (поставка) в файловой системе
    private ReportContentDAO templateFileDAO; // файлы готовых макетов шаблонов (поставка для генерации шаблонов отчётов)
    private ReportContentDAO contentRepositoryDAO; // файлы создаваемых отчётов как "cm:content" в репозитории
    private ReportContentDAO subreportFileDAO; // файловое хранилище для шаблонов подотчётов (для jasper надо именно файлы на диске иметь)

    // генераторы отчётов по типам
    private Map<String, ReportGenerator> reportGenerators;

    // Map<КодТипаОтчёта, [Провайдер,Расширение,Шаблон]>
    private Map<String, ReportDefaultsDesc> reportDefaults;

    protected ServiceRegistry serviceRegistry;
    private OrgstructureBean orgstructureBean;

    private SubstitudeBean substitudeService;

    private XMLExportBean xmlExportBean;

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    public void setSubstitudeService(SubstitudeBean substitudeService) {
        this.substitudeService = substitudeService;
    }

    public void setTemplateFileDAO(ReportContentDAO value) {
        this.templateFileDAO = value;
    }

    public void setReportEditorDAO(ReportEditorDAO editorDAO) {
        this.reportEditorDAO = editorDAO;
    }

    public ReportEditorDAO getReportEditorDAO() {
        return reportEditorDAO;
    }

    /**
     * @return хранилище файлов шаблонов отчётов в репозитории
     */
    public ReportContentDAO getContentRepositoryDAO() {
        return contentRepositoryDAO;
    }

    public void setContentRepositoryDAO(ReportContentDAO value) {
        logger.debug(String.format("contentRepositoryDAO assigned: %s", value));
        this.contentRepositoryDAO = value;
    }

    /**
     * @return хранилище файлов шаблонов подотчётов "на диске", т.к. например,
     * для jasper надо иметь именно файлы.
     */
    public ReportContentDAO getSubreportFileDAO() {
        return subreportFileDAO;
    }

    public void setSubreportFileDAO(ReportContentDAO contentDAO) {
        this.subreportFileDAO = contentDAO;
    }

    /**
     * @return хранилище в виде файлов (для отчётов, идущих с поставкой)
     */
    public ReportContentDAO getContentFileDAO() {
        return contentFileDAO;
    }

    public void setContentFileDAO(ReportContentDAO value) {
        logger.debug(String.format("contentFileDAO assigned: %s", value));
        this.contentFileDAO = value;
    }

    public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
        this.orgstructureBean = orgstructureBean;
    }

    /**
     * Файлы шаблонов для генерации шаблонов отчётов
     */
    public ReportContentDAO getTemplateFileDAO() {
        return templateFileDAO;
    }

    public void setXmlExportBean(XMLExportBean xmlExportBean) {
        this.xmlExportBean = xmlExportBean;
    }
        /**
         * @return не NULL список [ReportTypeMnemonic -> ReportGenerator]
         */
    public Map<String, ReportGenerator> getReportGenerators() {
        if (reportGenerators == null) {
            reportGenerators = new HashMap<String, ReportGenerator>(1);
        }
        return reportGenerators;
    }

    /**
     * Задать соот-вие типов отчётов и их провайдеров
     *
     * @param map список [ReportTypeMnemonic -> ReportGenerator]
     */
    @SuppressWarnings("unused")
    public void setReportGenerators(Map<String, ReportGenerator> map) {
        this.reportGenerators = map;
    }

    public void init() {
        if (getDescriptors() != null && getDescriptors().size() > 0) {
            if (logger.isInfoEnabled()) {// отобразить только названия шаблонов ...
                logger.info(String.format(
                        " initialized templates count %s\n\t%s",
                        getDescriptors().size(),
                        Utils.getAsString(getDescriptors().keySet(), "\n\t")));
            } else if (logger.isDebugEnabled()) {// целиком загруженные шаблоны ...
                logger.debug(String.format(
                        " initialized templates count %s\n\t%s",
                        getDescriptors().size(),
                        Utils.getAsString(getDescriptors().values(), "\n\t")));
            }

            // добавим обработку подотчетов из репозитория!
            // нам нужно скопировать их в файловую систему, если их там нет
            List<ReportDescriptor> availableDescriptors = new ArrayList<ReportDescriptor>();
            availableDescriptors.addAll(getDescriptors().values());

            for (ReportDescriptor availableDescriptor : availableDescriptors) {
                copySubreportsTemplatesInternal(availableDescriptor);
            }
        }
    }

    private void copySubreportsTemplatesInternal(ReportDescriptor availableDescriptor) {
        List<ReportDescriptor> subReports = availableDescriptor.getSubreports();
        if (subReports != null && !subReports.isEmpty()) {
            for (ReportDescriptor subReport : subReports) { // здесь у нас дескрипторы заполнены не полностью!
                if (subReport.isSubReport()) { // чтобы избежать возможных ошибок в будущем
                    copyTemplate(subReport, this.contentRepositoryDAO, this.subreportFileDAO, false);
                }
                copySubreportsTemplatesInternal(subReport);
            }
        }
    }

    /**
     * Получить описатеть отчёта по названию.
     * Просматриваются отчёты по порядку:
     * 1) зарегистрированные "свежие"
     * 2) хранимые в виде ds-xml файлов (поставка)
     * 3) зарегенные в виде бинов (сконфигурированные spring-beans)
     *
     * @param reportMnemoName мнемонический код отчёта (уникальный)
     */
    public ReportDescriptor getRegisteredReportDescriptor(String reportMnemoName) {
        if (getDescriptors().containsKey(reportMnemoName)) {
            return getDescriptors().get(reportMnemoName); // FOUND by Mnemonic
        }

        logger.warn(String.format("Report '%s' has no descriptor", reportMnemoName));
        return null; // NOT FOUND
    }

    /**
     * Получить список зарегистрированных редакторов отчётов для указанного типа
     * документов и тип отчёта
     *
     * @param docType    тип документов или null, если для любых типов док-ов
     * @return список зарегеных отчётов (отчёты с типом документов null, воз-ся
     * при любом состоянии параметра docType)
     */
    public List<ReportDescriptor> getRegisteredReports(String docType) {
        return getRegisteredReports(docType, false);
    }

    public List<ReportDescriptor> getRegisteredReports(String docType, boolean dontFilterByRole) {
        final Map<String, ReportDescriptor> list = this.getDescriptors();
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }

        if (docType != null && docType.trim().isEmpty()) {
            docType = null;
        }

        final Set<String> employeeAuth = new HashSet<>();
        if (!dontFilterByRole) {
            NodeRef currentEmployee = orgstructureBean.getCurrentEmployee();

            if (currentEmployee != null) {
                final String employeeLogin = orgstructureBean.getEmployeeLogin(currentEmployee);
                //noinspection unchecked
                employeeAuth.addAll((Set<String>) AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
                    @Override
                    public Object doWork() throws Exception {
                        return serviceRegistry.getAuthorityService().getAuthoritiesForUser(employeeLogin);
                    }
                }));
            }
        }

        final List<ReportDescriptor> found = new ArrayList<>();
        try {
            if (docType == null) {
                // не задано фильтрование -> вернуть сразу всё целиком ... без подотчетов
                for (ReportDescriptor desc : list.values()) {
                    if (!desc.isSubReport()) {
                        if (dontFilterByRole || hasPermissionToReport(desc, employeeAuth)) {
                            found.add(desc);
                        }
                    }
                }
            } else {
                for (ReportDescriptor desc : list.values()) {
                    if (!desc.isSubReport()) {
                        final boolean okDocType = (desc.getFlags() == null) || desc.getFlags().isTypeSupported(docType);
                        if (okDocType && (dontFilterByRole || hasPermissionToReport(desc, employeeAuth))) {
                            found.add(desc);
                        }
                    }
                }
            }
        } finally {
            // сортируем описания по алфавиту
            //Collections.sort(found, new Comparator_ByAlphabet());
        }
        return found;
    }

    private boolean hasPermissionToReport(ReportDescriptor descriptor, Set<String> auth) {
        Set<String> reportRoles = descriptor.getBusinessRoles();
        if (reportRoles.isEmpty()) {
            return true;
        }
        for (String reportRole : reportRoles) {
            if (auth.contains("GROUP__LECM$BR%" + reportRole)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Получить список зарегистрированных редакторов отчётов для указанных типа
     * документов и типу отчёта
     *
     * @param docTypes      массив типов документов  или null, если для любых типов
     * @param forCollection возвращать отчеты для коллекции или нет?
     * @return список зарегистрированных отчётов
     */
    public List<ReportDescriptor> getRegisteredReports(String[] docTypes, boolean forCollection) {
        return getRegisteredReports(docTypes, forCollection, false);
    }

    public List<ReportDescriptor> getRegisteredReports(String[] docTypes, boolean forCollection, boolean dontFilterByRole) {
        final Set<ReportDescriptor> unFilteredReports = new HashSet<ReportDescriptor>();
        if (docTypes != null) {
            // указаны типы отчётов
            for (String docType : docTypes) {
                if (docType != null && docType.length() > 0) {
                    if (docType.startsWith(String.valueOf(QName.NAMESPACE_BEGIN))) {
                        docType = QName.createQName(docType).toPrefixString(serviceRegistry.getNamespaceService());
                    }
                    unFilteredReports.addAll(getRegisteredReports(docType, dontFilterByRole));
                }
            }
        } else {
            unFilteredReports.addAll(getRegisteredReports((String) null, dontFilterByRole));
        }

        final List<ReportDescriptor> resultedReports = new ArrayList<ReportDescriptor>();
        for (ReportDescriptor descriptor : unFilteredReports) {
            if (descriptor.isSubReport() || descriptor.getFlags().isCustom()) {
                continue;
            }

            if (forCollection == descriptor.getFlags().isMultiRow()) {
                resultedReports.add(descriptor);
            }
        }
        // сортируем описания по алфавиту
        Collections.sort(resultedReports, new Comparator_ByAlphabet());
        return resultedReports;
    }

    /**
     * Получить список продеплоенных дескрипторов.
     * В него входят:
     * 1) дискрипторы, заданные бмнами,
     * 2) дескрипторы, имеющиеся в виде файлов,
     * 3) продеплоенные в хранилище дескрипторы.
     * (в этом списке чем ниже тем приоритетнее)
     */
    public Map<String, ReportDescriptor> getDescriptors() {
        if (this.descriptors == null) {
            this.descriptors = new HashMap<String, ReportDescriptor>();
            scanResources();
        }
        return this.descriptors;
    }

    /**
     * Получить список всех зарегистрированных отчётов
     *
     * @return список зарегеных отчётов
     */
    public List<ReportDescriptor> getRegisteredReports() {
        return getRegisteredReports(null);
    }

    /**
     * Зарегистрировать указанный описатель отчёта. Создать ds-xml.
     */
    public void registerReportDescriptor(ReportDescriptor desc, NodeRef reportRef) throws RuntimeException{
        if (desc != null) {
            this.contentRepositoryDAO.delete(new ReportContentDAO.IdRContent(desc.getMnem(), "*"));
            if (desc.getSubreports() != null) {
                for (ReportDescriptor subReportDescriptor : desc.getSubreports()) {
                    try {
                        registerReportDescriptor(subReportDescriptor, null);
                    } catch (RuntimeException ex) {
                        logger.error(ex.getMessage(), ex);
                    }
                }
            }

            if (!desc.isSubReport() && reportRef != null) {
                try {
                    createDsFile(desc, reportRef); // создание ds-xml и файла импорта (на всякий случай) для основных отчетов
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }

            saveReportTemplate(desc); // сохранение шаблонов отчёта и подотчётов
            if (!desc.isSubReport()) {
                getDescriptors().put(desc.getMnem(), desc);
            }
            logger.info(String.format("Report descriptor with name '%s' registered!", desc.getMnem()));
        }
    }

    /**
     * Зарегистрировать отчёт, созданный редактором отчётов ("lecm-reports-editor"), указав его id.
     * Доступ к данным будет выполняться через reportDAO.
     */
    public boolean registerReportDescriptor(NodeRef rdescId) {
        boolean result = false;
        PropertyCheck.mandatory(this, "reportDAO", getReportEditorDAO());
        try {
            registerReportDescriptor(getReportEditorDAO().getReportDescriptor(rdescId), rdescId);
            getReportEditorDAO().markAsDeployed(rdescId);
            result = true;
        } catch (RuntimeException ex) {
            logger.error(ex.getMessage(), ex);
        }
        return result;
    }

    /**
     * Обратная к registerReportDescriptor.
     * Если отчёт стандартный из поставки, то он становится недоступен только до
     * следующей перезагрузки приложения.
     */
    public void unregisterReportDescriptor(String reportCode) {
        if (reportCode != null && !reportCode.trim().isEmpty()) {
            if (getDescriptors().containsKey(reportCode)) {
                final ReportDescriptor desc = getDescriptors().get(reportCode);
                unregisterReportDescriptor(desc);
                logger.debug(String.format("Report descriptor with name '%s' unregistered", reportCode));
            } else {
                logger.warn(String.format("Report descriptor with code '%s' NOT exists", reportCode));
            }
        }
    }

    public void unregisterReportDescriptor(ReportDescriptor descriptor) {
        if (descriptor != null && descriptor.getMnem() != null) {
            final List<ReportDescriptor> subReports = descriptor.getSubreports();
            if (subReports != null) {
                for (ReportDescriptor subReport : subReports) {
                    unregisterReportDescriptor(subReport);
                }
            }

            for (ReportTemplate reportTemplate : descriptor.getReportTemplates()) {
                // удаляем из репозитория contentRepositoryDAO
                // (!) из файловых не убираем никогда
                if (reportTemplate.getReportType() != null) {
                    this.contentRepositoryDAO.delete(new ReportContentDAO.IdRContent(descriptor.getMnem(), "*"));
                }
            }
            if (getDescriptors().containsKey(descriptor.getMnem())) {
                // убираем из списка активных ...
                getDescriptors().remove(descriptor.getMnem());
                logger.debug(String.format("Report descriptor with name '%s' unregistered", descriptor.getMnem()));
            }
        }
    }

    /**
     * Загрузить данные ds-файла указанного шаблонаn
     */
    public byte[] loadDsXmlBytes(String reportCode) {
        PropertyCheck.mandatory(this, "contentFileDAO", getContentFileDAO());
        PropertyCheck.mandatory(this, "contentRepositoryDAO", getContentRepositoryDAO());

        final ReportDescriptor desc = this.getRegisteredReportDescriptor(reportCode);
        final ReportContentDAO.IdRContent idDS = DSXMLProducer.makeDsXmlId(desc);
        ContentReader result;
        if (contentRepositoryDAO.exists(idDS)) {
            // описатеть имеется в репозитории продеплоенных ...
            result = contentRepositoryDAO.loadContent(idDS);
        } else if (contentFileDAO.exists(idDS)) {
            // описатель имеется в стд файловом хранении ...
            result = contentFileDAO.loadContent(idDS);
        } else {
            // непонятно где хранится ...
            final String msg = String.format("ds-xml cannot be loaded for report '%s':\n\t by id {%s}\n\t (!) not found at repository storage nor at file storage", reportCode, idDS);
            logger.error(msg);
            throw new RuntimeException(msg);
        }

        final ByteArrayOutputStream os = new ByteArrayOutputStream((int) result.getSize());
        result.getContent(os);
        return os.toByteArray();
    }


    /**
     * Сформировать шаблон по-умолчанию для НД указанного описателя отчёта
     */
    public NodeRef produceDefaultTemplate(NodeRef reportRef) {
        final ReportDescriptor desc = getReportEditorDAO().getReportDescriptor(reportRef);
        if (desc == null) {
            return null;
        }

        final ReportFileData templateFileData = new ReportFileData();

        /* генерация шаблона отчёта по макету шаблона ... */
        final ReportGenerator rg = findAndCheckReportGenerator(null);

        final byte[] contentBytes = generateReportTemplate(rg, desc); // (!) ГЕНЕРАЦИЯ
        templateFileData.setData(contentBytes);

        // формирование названия
        String extension =  DEFAULT_REPORT_EXTENSION;

        templateFileData.setEncoding("UTF-8");
        templateFileData.setMimeType(findMimeType(extension));

        templateFileData.setFilename(rg.getTemplateFileName(desc, null, extension));

        return storeAsContent(templateFileData, reportRef);
    }

    public NodeRef produceDefaultTemplate(NodeRef reportRef, NodeRef templateRef) {
        final ReportDescriptor desc = getReportEditorDAO().getReportDescriptor(reportRef);
        if (desc == null) {
            return null;
        }

        final ReportTemplate template = getReportEditorDAO().getReportTemplate(templateRef);
        if (template == null) {
            return null;
        }

        final ReportFileData templateFileData = new ReportFileData();

        final ReportGenerator rg = findAndCheckReportGenerator(template.getReportType());

        final byte[] contentBytes = generateReportTemplate(rg, desc, template); // (!) ГЕНЕРАЦИЯ
        templateFileData.setData(contentBytes);

        // формирование названия
        final ReportDefaultsDesc def = getReportDefaultsDesc(template.getReportType()); // умолчания для типа
        String extension = (def != null ? def.getFileExtension() : null);
        if (Utils.isStringEmpty(extension)) {
            extension = DEFAULT_REPORT_EXTENSION;
        }

        templateFileData.setEncoding("UTF-8");
        templateFileData.setMimeType(findMimeType(extension));

        templateFileData.setFilename(rg.getTemplateFileName(desc, template, extension));
        return storeAsContent(templateFileData, reportRef);
    }

    /**
     * Получить строковое не NULL название указанного типа отчёта
     *
     * @param rtype тип отчёта, допустимо NULL
     * @return при rtype != null воз-ся rtype.code, иначе значение по-умолчанию для данного менеджера
     */
    public String getReportTypeTag(ReportType rtype) {
        return (rtype != null && rtype.getMnem() != null && !rtype.getMnem().trim().isEmpty())
                ? rtype.getMnem().trim()
                : DEFAULT_REPORT_TYPE;
    }

    /**
     * Вернуть хранилище, которое содержит указанный описатель или NULL
     */
    public ReportContentDAO findContentDAO(ReportDescriptor desc) {
        if (desc == null) {
            return null;
        }

        final ReportContentDAO.IdRContent idDS = DSXMLProducer.makeDsXmlId(desc);
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

    /**
     * Список умочаний для указанного типа отчёта
     *
     * @return не NULL список [key=ReportType.Mnem -> value={ file_Extension + template_of_template}]
     */
    public ReportDefaultsDesc getReportDefaultsDesc(ReportType rtype) {
        if (rtype != null && getReportDefaults() != null) {
            if (getReportDefaults().containsKey(rtype.getMnem()))
                return getReportDefaults().get(rtype.getMnem()); // FOUND
        }

        return null; // NOT FOUND
    }

    /**
     * @return не NULL список [ReportType.Mnem -> FileExtension+Template]
     */
    public Map<String, ReportDefaultsDesc> getReportDefaults() {
        if (this.reportDefaults == null) {
            this.reportDefaults = new HashMap<String, ReportDefaultsDesc>();

            // автодобавление для умолчания:
            final ReportDefaultsDesc jdesc = new ReportDefaultsDesc(DEFAULT_REPORT_EXTENSION, DEFAULT_REPORT_TEMPLATE, DEFAULT_SUB_REPORT_TEMPLATE);
            this.reportDefaults.put(DEFAULT_REPORT_TYPE, jdesc);
        }
        return this.reportDefaults;
    }

    /**
     * Создать отчёт
     *
     *
     *
     * @param reportName код отчёта
     * @param templateCode  код шаблона
     * @param args       параметры
     * @throws IOException
     */
    public ReportFileData generateReport(String reportName, String templateCode, Map<String, String> args) throws IOException {
        return generateReport(this.getRegisteredReportDescriptor(reportName), templateCode, args);
    }

    public ReportFileData generateReport(final ReportDescriptor report, final String templateCode, final Map<String, String> args) throws IOException {
        if (report == null) {
            throw new RuntimeException(String.format("Report descriptor not accessible (possibly report is not registered !?)"));
        }

        final String reportName = report.getMnem();
        Set<String> bRoles = report.getBusinessRoles();

        if (!bRoles.isEmpty()) {
            final HashSet<String> employeeRoles = new HashSet<String>();
            NodeRef currentEmployee = orgstructureBean.getCurrentEmployee();

            if (currentEmployee != null) {
                List<NodeRef> roleRefs = orgstructureBean.getEmployeeRoles(currentEmployee, true, true);
                for (NodeRef role : roleRefs) {
                    String name = (String) serviceRegistry.getNodeService().getProperty(role, OrgstructureBean.PROP_BUSINESS_ROLE_IDENTIFIER);
                    employeeRoles.add(name);
                }
            }
            if (!hasPermissionToReport(report, employeeRoles)) {
                throw new RuntimeException(String.format("Current Employee has not permission to view report '%s' !!!", reportName));
            }
        }
        // (!) клонирование Дескриптора, чтобы не трогать общий для всех дескриптор ...
        final ReportDescriptor reportDesc = Utils.clone(report);

        // (1) передача параметров из запроса в ReportDescriptor на основании их типов
        // (2) расширение списка пришедших параметров: для диапазонов - добавление крайних значений, для ID - добавить доп поле node_id (для SQL запросов)
        final Map<String, Object> paramsMap = ParameterMapper.assignParameters(reportDesc, args, serviceRegistry, substitudeService, orgstructureBean);
        if (logger.isInfoEnabled()) {
            logParameters(paramsMap, String.format("Processing report '%s' with args: \n", reportName));
        }

        final ReportTemplate rTemplate = getTemplateByCode(reportDesc, templateCode);
        if (rTemplate == null) {
            throw new RuntimeException(String.format("Report '%s' has not any template !", reportName));
        }

        final String rType = Utils.coalesce(rTemplate.getReportType().getMnem(), ReportsManager.DEFAULT_REPORT_TYPE);
        final ReportGenerator reporter = this.getReportGenerators().get(rType);
        if (reporter == null) {
            throw new RuntimeException("Unsupported report kind '" + rType + "': no provider registered");
        }

        final Boolean isRunAsSystem = reportDesc.getFlags().isRunAsSystem();

        final ReportsManager manager = this;

        final AuthenticationUtil.RunAsWork<ReportFileData> runAsWork = new AuthenticationUtil.RunAsWork<ReportFileData>() {
            @Override
            public ReportFileData doWork() throws Exception {
                return reporter.produceReport(manager, reportDesc, rTemplate, paramsMap);
            }
        };

        return isRunAsSystem ?
                AuthenticationUtil.runAsSystem(runAsWork) :
                AuthenticationUtil.runAs(runAsWork, AuthenticationUtil.getFullyAuthenticatedUser());
    }

    public ReportTemplate getTemplateByCode(ReportDescriptor descriptor, String templateCode) {
        if (descriptor == null) {
            return null;
        }
        if (templateCode == null || templateCode.isEmpty()) {
            return descriptor.getDefaultTemplate();
        }
        for (ReportTemplate reportTemplate : descriptor.getReportTemplates()) {
            if (reportTemplate.getMnem().equals(templateCode)) {
                return reportTemplate;
            }
        }
        return null;
    }
    /**
     * Сохранить данные в указанной папке репозитория.
     * Сохраняет как обычный дочерний "cm:content" c именем и содержанием файла.
     * Mime-тип будет определяться автоматом по расширению файла, если srcData.mimeType == null.
     *
     * @return созданный id узла
     */
    public NodeRef storeAsContent(final ReportFileData srcData, final NodeRef destParentRef) {
        if (srcData == null) {
            return null;
        }

        final QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, UUID.randomUUID().toString());
        final Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
        String srcDataFilename = srcData.getFilename();
        if (srcDataFilename != null && !srcDataFilename.isEmpty()) {
            FileNameValidator.getValidFileName(srcDataFilename);
        }

        properties.put(ContentModel.PROP_NAME, srcData.getFilename());

        if (srcData.getFilename() != null) {
            // предварительно удалим старый файл, если он имеется ...
            final NodeRef prevFileNode = serviceRegistry.getNodeService().getChildByName(destParentRef, ContentModel.ASSOC_CONTAINS, srcData.getFilename());
            if (prevFileNode != null) {
                serviceRegistry.getNodeService().deleteNode(prevFileNode);  // удаляем старый файл
            }
        }

        // создание нового узла ...
        final ChildAssociationRef newChild =
                serviceRegistry.getNodeService().createNode(destParentRef, ContentModel.ASSOC_CONTAINS, assocQName, ContentModel.TYPE_CONTENT, properties);
        final NodeRef resultFileRef = newChild.getChildRef();

        final ContentService contentService = serviceRegistry.getContentService();
        final ContentWriter writer = contentService.getWriter(resultFileRef, ContentModel.PROP_CONTENT, true);
        writer.setEncoding(srcData.getEncoding()); // "UTF-8"

        // mime-тип берём по-возможности из источника ...
        String mimeType = srcData.getMimeType();
        if (mimeType == null && srcData.getFilename() != null) { // autodetecting ...
            mimeType = findMimeType(FilenameUtils.getExtension(srcData.getFilename()));
        }

        if (mimeType == null) {
            // если уж ничего не задано - ставим default
            mimeType = "text/xml";
        }

        writer.setMimetype(mimeType);

        if (srcData.getData() != null) {
            writer.putContent(new ByteArrayInputStream(srcData.getData()));
        }

        return resultFileRef;
    }

    /**
     * Вернуть mime-тип по расширению.
     *
     * @param extension расширение файла с точкой или без, т.е. вида ".abc" или "abc".
     */
    private String findMimeType(String extension) {
        return (extension != null) ? serviceRegistry.getMimetypeService().getMimetype(extension.replace(".", "")) : null;
    }

    /**
     * Сгенерировать для указанного описателя файл с шаблоном отчёта.
     * Используется сконфигурированное имя gen-шаблона.
     */
    private byte[] generateReportTemplate(final ReportGenerator rg, final ReportDescriptor reportDesc) {
        if (reportDesc == null) {
            return null;
        }

        checkReportDescData(reportDesc, false);

        PropertyCheck.mandatory(this, "templateFileDAO", getTemplateFileDAO());
        PropertyCheck.mandatory(this, "reportDefaults", getReportDefaults());

        // получение названия файла-шаблона для генерации
        final String templateFileName = (!reportDesc.isSubReport() ? DEFAULT_REPORT_TEMPLATE : DEFAULT_SUB_REPORT_TEMPLATE);

        final ReportContentDAO.IdRContent id = ReportContentDAO.IdRContent.createId(reportDesc, templateFileName);

        // загрузка макета шаблона ...
        final ContentReader reader = this.getTemplateFileDAO().loadContent(id);
        try {
            final byte[] maketData = ru.it.lecm.reports.utils.Utils.ContentToBytes(reader);

            return rg.generateReportTemplateByMaket(maketData, reportDesc, null);

        } catch (Throwable ex) {
            final String msg = String.format("Report '%s': get generated template '%s' problem\n\t%s"
                    , reportDesc.getMnem(), id.getFileName(), ex.getMessage());
            logger.error(msg, ex);
            throw new RuntimeException(msg, ex);
        }
    }

    private byte[] generateReportTemplate(ReportGenerator rg, ReportDescriptor reportDesc, ReportTemplate template) {
        if (reportDesc == null) {
            return null;
        }

        checkReportDescData(reportDesc, template, false);

        PropertyCheck.mandatory(this, "templateFileDAO", getTemplateFileDAO());
        PropertyCheck.mandatory(this, "reportDefaults", getReportDefaults());

        final ReportDefaultsDesc defaults = this.getReportDefaultsDesc(template.getReportType());

        // получение названия файла-шаблона для генерации
        final String templateFileName = (defaults != null)
                ? (!reportDesc.isSubReport() ? defaults.getGenerationTemplate() : defaults.getSubReportGenerationTemplate()) // название шаблона из установок по-умолчанию для данного типа отчёта
                : (!reportDesc.isSubReport() ? DEFAULT_REPORT_TEMPLATE : DEFAULT_SUB_REPORT_TEMPLATE);

        final ReportContentDAO.IdRContent id = ReportContentDAO.IdRContent.createId(reportDesc.getMnem(), templateFileName, template.getReportType().getMnem().toLowerCase());


        // загрузка макета шаблона ...
        final ContentReader reader = this.getTemplateFileDAO().loadContent(id);
        try {
            final byte[] maketData = ru.it.lecm.reports.utils.Utils.ContentToBytes(reader);

            return rg.generateReportTemplateByMaket(maketData, reportDesc, template);

        } catch (Throwable ex) {
            final String msg = String.format("Report '%s': get generated template '%s' problem\n\t%s"
                    , reportDesc.getMnem(), id.getFileName(), ex.getMessage());
            logger.error(msg, ex);
            throw new RuntimeException(msg, ex);
        }
    }

    private static void logParameters(final Map<String, Object> params, final String msg) {
        final StringBuilder infosb = new StringBuilder();
        if (msg != null) {
            infosb.append(msg);
        }
        int i = 0;
        if (params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                ++i;
                final String paramName = entry.getKey();
                final String value = String.valueOf(entry.getValue());
                infosb.append(String.format("\t[%d]\t'%s' \t'%s'\n", i, paramName, Utils.coalesce(value)));
            }
        }
        logger.info(String.format("Call report maker with args count=%d:\n %s", i, infosb.toString()));
    }

    /**
     * Просканировать указанное хранилище на наличие дескрипторов отчётов и загрузить их
     *
     * @param destMap целевой список
     * @param repos   ReportContentDAO
     * @return кол-во загруженных описаний
     */
    private int scanRepository(final Map<String, ReportDescriptor> destMap, final ReportContentDAO repos, final boolean useImportFormat) {
        if (repos == null) {
            return 0;
        }

        final List<ReportContentDAO.IdRContent> found = new ArrayList<ReportContentDAO.IdRContent>();
        final ReportContentDAO.ContentEnumerator doEnum = new ReportContentDAO.ContentEnumerator() {
            @Override
            public void lookAtItem(ReportContentDAO.IdRContent id) {
                try {
                    final boolean isDsXml = DSXMLProducer.isDsConfigFileName(id.getFileName());
                    if (!isDsXml) {
                        // skip none-descriptor files ...
                        return;
                    }

                    final InputStream in = repos.loadContent(id).getContentInputStream();
                    try {
                        // загружаем описатеть из файла
                        try {
                            ReportDescriptor desc = null;
                            if (!useImportFormat) {
                                desc = DSXMLProducer.parseDSXML(in, id.getFileName());
                                if (desc == null) {
                                    return;
                                }

                                if (desc.getMnem() == null || desc.getMnem().trim().length() == 0) {
                                    // задать название название шаблона по-умолчанию как в Id...
                                    desc.setMnem(id.getReportMnemo());
                                }
                            } else {
                                String reportCode = id.getReportMnemo();
                                NodeRef report = getReportEditorDAO().getReportDescriptorNodeByCode(reportCode);
                                if (report != null) {
                                    desc = getReportEditorDAO().getReportDescriptor(report);
                                }
                            }
                            if (desc != null) {
                                destMap.put(desc.getMnem(), desc); // (!) найден очередной
                            }

                            found.add(id); // запоминаем только при отсутствии ошибок
                        } catch (Throwable ex) {
                            logger.error(String.format(
                                    "Problem parsing deployed ds-xml by id='%s' -> report ignored\n%s"
                                    , id, ex.getMessage()), ex);
                        }
                    } finally {
                        IOUtils.closeQuietly(in);
                    }
                } catch (Throwable ex) {
                    // log and skip it
                    logger.error(String.format("Fail to load ds-xml file from '%s'\n\t %s\n\t -> ignored", id, ex.getMessage()));
                }
            }
        };

        repos.scanContent(doEnum);
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
        int ifound = 0;
        ifound += scanRepository(this.descriptors, this.contentFileDAO, false);
        ifound += scanRepository(this.descriptors, this.contentRepositoryDAO, true);
        return ifound;
    }


    /**
     * Сохранение шаблона отчёта из desc.getReportTemplates()
     */
    private boolean saveReportTemplate(ReportDescriptor desc) throws RuntimeException {
        if (desc.getReportTemplates() == null || desc.getReportTemplates().isEmpty()) {
            logger.warn(String.format("Report '%s' has no template", desc.getMnem()));
            return false;
        }

        // подотчёт если есть родительский отчёт ...
        final boolean isSubreport = desc.isSubReport();

        // (!) подотчёты сохраняем еще и  в отдельное (файловое) хранилище ...
        final ReportContentDAO storage = this.contentRepositoryDAO;
        final ReportContentDAO subReportStorage = getSubreportFileDAO();

        for (ReportTemplate template : desc.getReportTemplates()) {
            final String rtag = getReportTypeTag(template.getReportType());
            final ReportDefaultsDesc def = getReportDefaults().get(rtag);
		    /* сохранение в репозиторий "шаблона отчёта"... */
            try {
                byte[] templateRawData = null;
                final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                final InputStream stm = template.getData();

                ReportGenerator reportGenerator = findAndCheckReportGenerator(template.getReportType());
                if (stm != null) {
                    stm.reset();
                    IOUtils.copy(stm, byteStream);

                    final String ext = (def != null) ? def.getFileExtension() : DEFAULT_REPORT_EXTENSION;

                    final ReportContentDAO.IdRContent id = ReportContentDAO.IdRContent.createId(desc, reportGenerator.getTemplateFileName(desc, template, ext));

                    templateRawData = byteStream.toByteArray();

                    // сохранение в хранилище
                    storage.storeContent(id, new ByteArrayInputStream(templateRawData));
                    if (isSubreport) {//сохраняем подотчет и в файловую систему!
                        getSubreportFileDAO().storeContent(id, new ByteArrayInputStream(templateRawData));
                    }
                }

                /*
                 * оповещение соот-го провайдера (компиляция) ...
                 * т.к. деплоить можно только отчёты в репозиторий, здесь указываем
                 * именно его (не требуется определять откуда получен описатель - из
                 * файлового хранилища или из репозитория)
                 */
                reportGenerator.onRegister(desc, template, templateRawData, storage);
                if (isSubreport) {//сохраняем подотчет и в файловую систему!
                    reportGenerator.onRegister(desc, template, templateRawData, subReportStorage);
                }
                logger.debug(String.format("Report '%s': provider notified", desc.getMnem()));

            } catch (Throwable ex) {
                final String msg = String.format(
                        "Error saving template content for Report Descriptor %s"
                        , desc.getMnem());
                logger.warn(msg, ex);
                throw new RuntimeException(msg, ex);
            }

        }
        return true;
    }

    private boolean copyTemplate(ReportDescriptor descriptor, ReportContentDAO fromStorage, ReportContentDAO toStorage, boolean reWrite) {
        if (descriptor.getReportTemplates() == null || descriptor.getReportTemplates().isEmpty()) {
            logger.warn(String.format("Report '%s' has no template", descriptor.getMnem()));
            return false;
        }
        for (ReportTemplate template : descriptor.getReportTemplates()) {
            final String rtag = getReportTypeTag(template.getReportType());
            final ReportDefaultsDesc def = getReportDefaults().get(rtag);
        /* сохранение в репозиторий "шаблона отчёта"... */
            InputStream baStm = null;
            byte[] templateRawData;
            try {
                final ReportGenerator rg = findAndCheckReportGenerator(template.getReportType());
                final String ext = (def != null) ? def.getFileExtension() : DEFAULT_REPORT_EXTENSION;
                final ReportContentDAO.IdRContent id = ReportContentDAO.IdRContent.createId(descriptor, rg.getTemplateFileName(descriptor, template, ext));

                if (fromStorage.exists(id) && (!toStorage.exists(id) || reWrite)) {
                    ContentReader fromReader = fromStorage.loadContent(id);

                    templateRawData = Utils.ContentToBytes(fromReader);
                    if (templateRawData != null) {
                        baStm = new ByteArrayInputStream(templateRawData);
                        toStorage.storeContent(id, baStm);
                    }

                    rg.onRegister(descriptor, template, templateRawData, toStorage);
                }
            } catch (Throwable ex) {
                final String msg = String.format(
                        "Error saving template content for Report Descriptor %s"
                        , descriptor.getMnem());
                logger.warn(msg, ex);
                throw new RuntimeException(msg, ex);
            } finally {
                IOUtils.closeQuietly(baStm);
            }

        }

        return true;
    }

    /**
     * Вернуть зарегистрированный генератор шаблонов для указанного типа отчётов.
     * Если нет такого - поднимается исключение.
     *
     * @param rtype тип отчёта
     */
    private ReportGenerator findAndCheckReportGenerator(ReportType rtype) {
        final String rtag = getReportTypeTag(rtype);
        final ReportGenerator result = getReportGenerators().get(rtag);
        if (result == null) {
            throw new RuntimeException(String.format("Report type '%s' is not supported (no registered report generators)"
                    , Utils.coalesce(rtag, rtype.getMnem(), rtype.getDefault())));
        }
        return result;
    }

    /**
     * Создание ds-xml файла с названием "ds-"+desc.getMnem()+".xml"
     */
    private void createDsFile(ReportDescriptor desc, NodeRef reportRef) throws IOException {
        if (desc == null) {
            return;
        }

        checkReportDescData(desc, !(desc.isSubReport()));

        if (desc.isSubReport() &&
                (desc.getReportTemplates() == null || desc.getReportTemplates().isEmpty())) {
            return;
        }

        // создание ds-файла ...
        final ByteArrayOutputStream dsxml = DSXMLProducer.xmlCreateDSXML(desc.getMnem(), desc);
        if (dsxml != null) {
            final ReportContentDAO.IdRContent idds = DSXMLProducer.makeDsXmlId(desc);
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

        // создание импорт-файла ...
        /*ByteArrayOutputStream importXml = null;
        try {
            importXml = new ByteArrayOutputStream();
            XMLExportBean.XMLExporter xmlDictionaryExporter = xmlExportBean.getXMLExporter(importXml);
            xmlDictionaryExporter.writeItems(reportRef);
            xmlDictionaryExporter.close();
            importXml.flush();

            final ReportContentDAO.IdRContent idds = DSXMLProducer.makeImportXmlId(desc);
            try {
                // файл создадим только в репозитории (но не в файловом хранилище) ...
                this.contentRepositoryDAO.storeContent(idds, new ByteArrayInputStream(importXml.toByteArray()));
            } catch (Throwable ex) {
                final String msg = String.format(
                        "Report '%s': error saving ds-xml into storage by id '%s'", desc.getMnem(), idds);
                logger.error(msg, ex);
            }
        } catch (XMLStreamException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (importXml != null) {
                importXml.close();
            }
        }*/
    }

    /**
     * Выполнить проверку данных.
     * <br/>Поднять исключения при неверном или недостаточном заполнении полей описателя отчёта.
     */
    private void checkReportDescData(ReportDescriptor desc, boolean checkTemplate) {
        if (desc.getMnem() == null || desc.getMnem().trim().isEmpty()) {
            throw new RuntimeException(
                    String.format("Report descriptor must have mnemo code"));
        }
        if (desc.getProviderDescriptor() == null || desc.getProviderDescriptor().getMnem().trim().isEmpty()) {
            throw new RuntimeException(
                    String.format("Report '%s' must have Provider! Check report settings!", desc.getMnem()));
        }
        if (checkTemplate) {
            if (desc.getReportTemplates() == null || desc.getReportTemplates().isEmpty()) {
                throw new RuntimeException(
                        String.format("Report '%s' must have Representation Template! Please select template from dictionary or create new!", desc.getMnem()));
            }
            for (ReportTemplate template : desc.getReportTemplates()) {
                if (template.getReportType() == null) {
                    throw new RuntimeException(
                            String.format("Report Template '%s' must have Report Type! Please check Representation Template and his Type!", template.getMnem()));
                }
                if (template.getFileName() == null || template.getMnem() == null) {
                    throw new RuntimeException(
                            String.format("Report '%s' must have Representation Template! Please select template from dictionary or create new!", template.getMnem()));
                }
            }
        }
    }

    private void checkReportDescData(ReportDescriptor desc, ReportTemplate template, boolean checkTemplate) {
        if (desc.getMnem() == null || desc.getMnem().trim().isEmpty()) {
            throw new RuntimeException(
                    String.format("Report descriptor must have mnemo code"));
        }
        if (desc.getProviderDescriptor() == null || desc.getProviderDescriptor().getMnem().trim().isEmpty()) {
            throw new RuntimeException(
                    String.format("Report '%s' must have Provider! Check report settings!", desc.getMnem()));
        }
        if (checkTemplate) {
            if (template == null) {
                throw new RuntimeException(
                        String.format("Report '%s' must have Representation Template! Please select template from dictionary or create new!", desc.getMnem()));
            }
            if (template.getReportType() == null) {
                throw new RuntimeException(
                        String.format("Report Template '%s' must have Report Type! Please check Representation Template and his Type!", template.getMnem()));
            }
            if (template.getFileName() == null || template.getMnem() == null) {
                throw new RuntimeException(
                        String.format("Report '%s' must have Representation Template! Please select template from dictionary or create new!", template.getMnem()));
            }
        }
    }

    /**
     * Сортировка по-умолчанию для списков Дескрипторов отчёта будет по алфавиту по названиям (кодам).
     * Здесь null-значения будут ниже в списке ("тяжёлые").
     */
    static class Comparator_ByAlphabet implements Comparator<ReportDescriptor> {
        @Override
        public int compare(ReportDescriptor rd1, ReportDescriptor rd2) {
            if (rd1 == rd2)
                return 0;
            final String name1 = Utils.nonblank(rd1.getDefault(), rd1.getMnem());
            final String name2 = Utils.nonblank(rd2.getDefault(), rd2.getMnem());
            // null == null, null > any other
            if (name1 == null)
                return (name2 == null) ? 0 : 1;
            if (name2 == null)
                return -1; // x < null

            // here name1 <> null, name2 <> null ...
            return name1.compareToIgnoreCase(name2);
        }
    }
}
