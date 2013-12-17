package ru.it.lecm.reports.beans;

import org.alfresco.model.ContentModel;
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
import ru.it.lecm.reports.api.ReportGenerator;
import ru.it.lecm.reports.api.ReportsManager;
import ru.it.lecm.reports.api.model.DAO.ReportContentDAO;
import ru.it.lecm.reports.api.model.DAO.ReportContentDAO.ContentEnumerator;
import ru.it.lecm.reports.api.model.DAO.ReportContentDAO.IdRContent;
import ru.it.lecm.reports.api.model.DAO.ReportEditorDAO;
import ru.it.lecm.reports.api.model.*;
import ru.it.lecm.reports.model.impl.ReportDefaultsDesc;
import ru.it.lecm.reports.model.impl.ReportType;
import ru.it.lecm.reports.model.impl.SubReportDescriptorImpl;
import ru.it.lecm.reports.utils.ParameterMapper;
import ru.it.lecm.reports.utils.Utils;
import ru.it.lecm.reports.xml.DSXMLProducer;

import java.io.*;
import java.util.*;

public class ReportsManagerImpl implements ReportsManager {

    static final transient Logger logger = LoggerFactory.getLogger(ReportsManagerImpl.class);

    final public static String DEFAULT_REPORT_TYPE = ReportType.RTYPE_MNEMO_JASPER;
    final public static String DEFAULT_REPORT_EXTENSION = ".jrxml";
    final public static String DEFAULT_REPORT_TEMPLATE = "jreportCommonTemplate.jrxml.gen";
    final public static String DEFAULT_SUB_REPORT_TEMPLATE = "sub-jreportCommonTemplate.jrxml.gen";

    /**
     * Список зарегистрирванных отчётов
     */
    private Map<String, ReportDescriptor> descriptors;

    /**
     * список дескрипторов явно заданных бинами
     */
    private Map<String, ReportDescriptor> beanDescriptors;

    private ReportEditorDAO reportEditorDAO; // хранилище отчётов редактора
    private ReportContentDAO contentFileDAO; // файлы готовых отчётов (поставка) в файловой системе
    private ReportContentDAO templateFileDAO; // файлы готовых макетов шаблонов (поставка для генерации шаблонов отчётов)
    private ReportContentDAO contentRepositoryDAO; // файлы создаваемых отчётов как "cm:content" в репозитории
    private ReportContentDAO subreportFileDAO; // файловое хранилище для шаблонов подотчётов (для jasper надо именно файлы на диске иметь)

    // генераторы отчётов по типам
    private Map< /*ReportType*/ String, ReportGenerator> reportGenerators;

    // Map<КодТипаОтчёта, [Провайдер,Расширение,Шаблон]>
    private Map< /*ReportType*/ String, ReportDefaultsDesc> reportDefaults;

    /**
     * Service registry
     */
    protected ServiceRegistry serviceRegistry;
    private SubstitudeBean substitudeService;

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public ReportEditorDAO getReportEditorDAO() {
        return reportEditorDAO;
    }

    public void setReportEditorDAO(ReportEditorDAO editorDAO) {
        this.reportEditorDAO = editorDAO;
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
     *         для jasper надо иметь именно файлы.
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

    /**
     * Файлы шаблонов для генерации шаблонов отчётов
     */
    public ReportContentDAO getTemplateFileDAO() {
        return templateFileDAO;
    }

    public void setTemplateFileDAO(ReportContentDAO value) {
        logger.debug(String.format("templateFileDAO assigned: %s", value));
        this.templateFileDAO = value;
    }


    @Override
    public List<ReportDescriptor> getRegisteredReports() {
        return getRegisteredReports(null, null);
    }


    @Override
    public List<ReportDescriptor> getRegisteredReports(String docType, String reportType) {
        final Map<String, ReportDescriptor> list = this.getDescriptors();
        if (list == null || list.isEmpty()) {
            return new ArrayList<ReportDescriptor>();
        }

        if (docType != null && docType.isEmpty()) {
            docType = null;
        }

        if (reportType != null && reportType.isEmpty()) {
            reportType = null;
        }

        final List<ReportDescriptor> found = new ArrayList<ReportDescriptor>();
        try {
            if (docType == null && reportType == null) {
                // не задано фильтрование -> вернуть сразу всё целиком ... без подотчетов
                for (ReportDescriptor desc : list.values()) {
                    if (!desc.isSubReport()) {
                        found.add(desc);
                    }
                }
            } else {
                for (ReportDescriptor desc : list.values()) {
                    final boolean okDocType = (desc.getFlags() == null) || desc.getFlags().isTypeSupported(docType);

                    final boolean okRType = (reportType == null) // не задан фильтр по типам отчётов
                            || ((desc.getReportType() == null)
                            || desc.getReportType().getMnem() == null) // не задан тип отчёта шаблона -> подходит к любому
                            || reportType.equalsIgnoreCase(desc.getReportType().getMnem()); // совпадение типа

                    if (okDocType && okRType && !desc.isSubReport()) {
                        found.add(desc);
                    }
                }
            }
        } finally {
            // сортируем описания по алфавиту
            Collections.sort(found, new Comparator_ByAlphabet());
        }
        return found;
    }

    @Override
    public List<ReportDescriptor> getRegisteredReports(String[] docTypes, boolean forCollection) {
        final Set<ReportDescriptor> unFilteredReports = new HashSet<ReportDescriptor>();
        if (docTypes != null) {
            // указаны типы отчётов
            for (String docType : docTypes) {
                if (docType != null && docType.length() > 0) {
                    if (docType.startsWith(String.valueOf(QName.NAMESPACE_BEGIN))) {
                        docType = QName.createQName(docType).toPrefixString(serviceRegistry.getNamespaceService());
                    }
                    unFilteredReports.addAll(getRegisteredReports(docType, null));
                }
            }
        } else {
            unFilteredReports.addAll(getRegisteredReports(null, null));
        }

        final List<ReportDescriptor> resultedReports = new ArrayList<ReportDescriptor>();
        for (ReportDescriptor descriptor : unFilteredReports) {
            if (descriptor.getFlags().isCustom() && forCollection) {
                // пропускаем кастомизированные для многострочных отчётов ...
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
     * Дескрипторы, заданные бинами
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
     * 1) дискрипторы, заданные бмнами,
     * 2) дескрипторы, имеющиеся в виде файлов,
     * 3) продеплоенные в хранилище дескрипторы.
     * (в этом списке чем ниже тем приоритетнее)
     */
    public Map<String, ReportDescriptor> getDescriptors() {
        if (this.descriptors == null) {
            this.descriptors = new HashMap<String, ReportDescriptor>();
            this.descriptors.putAll(this.getBeanDescriptors());
            scanResources();
        }
        return this.descriptors;
    }

    @Override
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
     *
     * @param map список [ReportType.Mnem -> ReportGenerator]
     */
    @Override
    public void setReportGenerators(Map<String, ReportGenerator> map) {
        this.reportGenerators = map;
    }

    @Override
    public String getReportTypeTag(ReportType rtype) {
        return (rtype != null && rtype.getMnem() != null && !rtype.getMnem().trim().isEmpty())
                ? rtype.getMnem().trim()
                : DEFAULT_REPORT_TYPE;
    }

    /**
     * Просканировать указанное хранилище на наличие дескрипторов отчётов и загрузить их
     *
     * @param destMap целевой список
     * @param repos   ReportContentDAO
     * @return кол-во загруженных описаний
     */
    private static int scanRepository(final Map<String, ReportDescriptor> destMap, final ReportContentDAO repos) {
        if (repos == null) {
            return 0;
        }

        final List<IdRContent> found = new ArrayList<IdRContent>();
        final ContentEnumerator doEnum = new ReportContentDAO.ContentEnumerator() {
            @Override
            public void lookAtItem(IdRContent id) {
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
                            final ReportDescriptor desc = DSXMLProducer.parseDSXML(in, id.getFileName());
                            if (desc == null) {
                                return;
                            }

                            if (desc.getMnem() == null || desc.getMnem().trim().length() == 0) {
                                // задать название название шаблона по-умолчанию как в Id...
                                desc.setMnem(id.getReportMnemo());
                            } else if (!id.getReportMnemo().equalsIgnoreCase(desc.getMnem())) {
                                logger.warn(String.format(
                                        "Loaded report has custom mnemonic:\n\t by id '%s'\n\t loaded with mnem '%s'"
                                        , id, desc.getMnem()
                                ));
                            }

                            destMap.put(desc.getMnem(), desc); // (!) найден очередной
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
        if (this.descriptors == null) {
            this.descriptors = new HashMap<String, ReportDescriptor>();
            this.descriptors.putAll(this.getBeanDescriptors());
        }

        int ifound = 0;
        ifound += scanRepository(this.descriptors, this.contentFileDAO);
        ifound += scanRepository(this.descriptors, this.contentRepositoryDAO);
        return ifound;
    }

    public void init() {
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
            // добавим обработку подотчетов из репозитория!
            // нам нужно скопировать их в файловую систему, если их там нет
            List<ReportDescriptor> availableDescriptors = new ArrayList<ReportDescriptor>();
            availableDescriptors.addAll(getDescriptors().values());

            for (ReportDescriptor availableDescriptor : availableDescriptors) {
                List<ReportDescriptor> subReports = availableDescriptor.getSubreports();
                if (subReports != null && !subReports.isEmpty()) {
                    for (ReportDescriptor subReport : subReports) {
                        if (subReport instanceof SubReportDescriptorImpl) {
                            String subCode = ((SubReportDescriptorImpl)subReport).getDestColumnName();
                            if (subCode != null && !subCode.isEmpty()) {
                                ReportDescriptor subReportDesc = getDescriptors().get(subCode);
                                if (subReportDesc != null) {
                                    copyTemplate(subReportDesc, this.contentRepositoryDAO, this.subreportFileDAO, false);
                                }
                            }
                        }
                    }
                }
            }
        } else {
            logger.info(" initialized ");
        }
    }

    /**
     * Получить дексриптор отчёта по его мнемонике или вернуть null. Поиск
     * ведётся по зарегистрированным отчётам в this.descriptors и в БД.
     *
     * @param reportMnemoName дескриптор
     * @return описатеть отчёта или null, если не найден
     */
    @Override
    public ReportDescriptor getRegisteredReportDescriptor(String reportMnemoName) {
        if (getDescriptors().containsKey(reportMnemoName)) {
            return this.descriptors.get(reportMnemoName); // FOUND by Mnemonic
        }

        logger.warn(String.format("Report '%s' has no descriptor", reportMnemoName));
        return null; // NOT FOUND
    }

    @Override
    public void registerReportDescriptor(NodeRef rdescId) {
        PropertyCheck.mandatory(this, "reportDAO", getReportEditorDAO());
        final ReportDescriptor rdesc = getReportEditorDAO().getReportDescriptor(rdescId);
        registerReportDescriptor(rdesc);
    }

    @Override
    public void registerReportDescriptor(ReportDescriptor desc) {
        if (desc != null) {
            if (desc.getSubreports() != null) {
                for (ReportDescriptor subReportDescriptor : desc.getSubreports()) {
                    registerReportDescriptor(subReportDescriptor);
                }
            }
            createDsFile(desc); // создание ds-xml
            saveReportTemplate(desc); // сохранение шаблонов отчёта и подотчётов
            getDescriptors().put(desc.getMnem(), desc);
            logger.info(String.format(
                    "Report descriptor with name '%s' registered",
                    desc.getMnem()));
        }
    }

    /**
     * Установить умочания для описателя.
     * <br/> (!) Если шаблоны для отчёта и его подотчётов не заданы явно, то
     * автоматом cгенерируются шаблоны по-умолчанию. Имена вложенных подотчётов
     * генерируются аналогично основному.
     */
    private void setDefaults(ReportDescriptor desc) {
        if (Utils.isStringEmpty(desc.getReportType().getMnem())) {
            desc.getReportType().setMnem(DEFAULT_REPORT_TYPE);
            logger.warn(String.format(
                    "Report '%s' has empty report type -> set to '%s'",
                    desc.getMnem(), desc.getReportType().getMnem()));
        }
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
            final byte[] reportTemplateData = this.generateReportTemplate(desc);
            desc.getReportTemplate().setData(
                    (reportTemplateData == null) ? null : new ByteArrayInputStream(reportTemplateData));
            logger.warn(String.format(
                    "Report '%s' has empty template data -> generated from '%s'"
                    , desc.getMnem()
                    , (def != null) ? def.getGenerationTemplate() : DEFAULT_REPORT_TEMPLATE // makeGenTemplateFileName(...)
            ));
        }
    }

    /**
     * Сохранение шаблона отчёта из desc.getReportTemplate()
     */
    private boolean saveReportTemplate(ReportDescriptor desc) {
        if (desc.getReportTemplate() == null) {
            logger.warn(String.format("Report '%s' has no template", desc.getMnem()));
            return false;
        }

        // подотчёт если есть родительский отчёт ...
        final boolean isSubreport =
                (desc instanceof SubReportDescriptorImpl) && desc.isSubReport();

        // (!) подотчёты сохраняем еще и  в отдельное (файловое) хранилище ...
        final ReportContentDAO storage = this.contentRepositoryDAO;
        final ReportContentDAO subReportStorage = getSubreportFileDAO();

        final String rtag = getReportTypeTag(desc.getReportType());
        final ReportDefaultsDesc def = getReportDefaults().get(rtag);

		/* сохранение в репозиторий "шаблона отчёта"... */
        try {
            byte[] templateRawData = null;

            if (desc.getReportTemplate() != null) {
                final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                final InputStream stm = desc.getReportTemplate().getData();
                if (stm != null) {
                    stm.reset();
                    IOUtils.copy(stm, byteStream);

                    final String ext = (def != null) ? def.getFileExtension() : DEFAULT_REPORT_EXTENSION;

                    final String mainMnem = desc.getMnem();
                    final IdRContent id = IdRContent.createId(desc, String.format("%s%s", mainMnem, ext));

                    templateRawData = byteStream.toByteArray();

                    // сохранение в хранилище
                    storage.storeContent(id, new ByteArrayInputStream(templateRawData));
                    if (isSubreport) {//сохраняем подотчет и в файловую систему!
                        getSubreportFileDAO().storeContent(id, new ByteArrayInputStream(templateRawData));
                    }
                }
            }

			/*
             * оповещение соот-го провайдера (компиляция) ...
			 * т.к. деплоить можно только отчёты в репозиторий, здесь указываем 
			 * именно его (не требуется определять откуда получен описатель - из 
			 * файлового хранилища или из репозитория)
			 */
            findAndCheckReportGenerator(desc.getReportType()).onRegister(desc, templateRawData, storage);
            if (isSubreport) {//сохраняем подотчет и в файловую систему!
                findAndCheckReportGenerator(desc.getReportType()).onRegister(desc, templateRawData, subReportStorage);
            }
            logger.debug(String.format("Report '%s': provider notified", desc.getMnem()));

        } catch (Throwable ex) {
            final String msg = String.format(
                    "Error saving template content for Report Descriptor %s"
                    , desc.getMnem());
            logger.warn(msg, ex);
            throw new RuntimeException(msg, ex);
        }

        return true;
    }

    private boolean copyTemplate(ReportDescriptor descriptor, ReportContentDAO fromStorage, ReportContentDAO toStorage, boolean reWrite) {
        if (descriptor.getReportTemplate() == null) {
            logger.warn(String.format("Report '%s' has no template", descriptor.getMnem()));
            return false;
        }

        final String rtag = getReportTypeTag(descriptor.getReportType());
        final ReportDefaultsDesc def = getReportDefaults().get(rtag);

		/* сохранение в репозиторий "шаблона отчёта"... */
        InputStream stm = null, baStm = null;
        byte[] templateRawData;
        try {
            final String ext = (def != null) ? def.getFileExtension() : DEFAULT_REPORT_EXTENSION;
            final String mainMnem = descriptor.getMnem();

            final IdRContent id = IdRContent.createId(descriptor, String.format("%s%s", mainMnem, ext));

            if (fromStorage.exists(id) && (!toStorage.exists(id) || reWrite)) {
                ContentReader fromReader = fromStorage.loadContent(id);

                templateRawData = Utils.ContentToBytes(fromReader);
                if (templateRawData != null) {
                    baStm = new ByteArrayInputStream(templateRawData);
                    toStorage.storeContent(id, baStm);
                }

                findAndCheckReportGenerator(descriptor.getReportType()).onRegister(descriptor, templateRawData, toStorage);
            }
        } catch (Throwable ex) {
            final String msg = String.format(
                    "Error saving template content for Report Descriptor %s"
                    , descriptor.getMnem());
            logger.warn(msg, ex);
            throw new RuntimeException(msg, ex);
        } finally {
            IOUtils.closeQuietly(stm);
            IOUtils.closeQuietly(baStm);
        }

        return true;
    }

    /**
     * Вернуть зарегистрированный генератор шаблонов для указанного типа отчётов.
     * Если нет такого - поднимается исключение.
     *
     * @param rtype тип отчёта
     */
    protected ReportGenerator findAndCheckReportGenerator(ReportType rtype) {
        if (rtype == null) {
            return null;
        }

        final String rtag = getReportTypeTag(rtype);
        final ReportGenerator result = getReportGenerators().get(rtag);
        if (result == null) {
            throw new RuntimeException(String.format("Report type '%s' is not supported (no registered report generators)"
                    , Utils.coalesce(rtag, rtype.getMnem(), rtype.getDefault())));
        }
        return result;
    }

    @Override
    public void unregisterReportDescriptor(String reportCode) {
        if (reportCode != null) {
            if (this.descriptors != null && this.descriptors.containsKey(reportCode)) {
                final ReportDescriptor desc = this.descriptors.get(reportCode);

                // удаляем из репозитория contentRepositoryDAO
                // (!) из файловых не убираем никогда
                this.contentRepositoryDAO.delete(new IdRContent(desc.getReportType(), reportCode, "*"));

                // убираем из списка активных ...
                this.descriptors.remove(reportCode);

                logger.debug(String.format("Report descriptor with name '%s' unregistered", reportCode));
            } else
                logger.warn(String.format("Report descriptor with code '%s' NOT exists", reportCode));
        }
    }

    /**
     * Создание ds-xml файла с названием "ds-"+desc.getMnem()+".xml"
     */
    private void createDsFile(ReportDescriptor desc) {
        if (desc == null) {
            return;
        }

        checkReportDescData(desc);
        if (desc instanceof SubReportDescriptorImpl && desc.getReportType().getMnem() == null) {
            return;
        }

        setDefaults(desc);

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
     * Выполнить проверку данных.
     * <br/>Поднять исключения при неверном или недостаточном заполнении полей описателя отчёта.
     */
    private void checkReportDescData(ReportDescriptor desc) {
        if (desc.getMnem() == null || desc.getMnem().trim().isEmpty()) {
            throw new RuntimeException(String.format("Report descriptor must have mnemo code"));
        }
    }

    @Override
    public byte[] loadDsXmlBytes(String reportCode) {
        PropertyCheck.mandatory(this, "contentFileDAO", getContentFileDAO());
        PropertyCheck.mandatory(this, "contentRepositoryDAO", getContentRepositoryDAO());

        final ReportDescriptor desc = this.getRegisteredReportDescriptor(reportCode);
        final IdRContent idDS = DSXMLProducer.makeDsXmlId(desc);
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

        if (result == null) {
            return null;
        }

        final ByteArrayOutputStream os = new ByteArrayOutputStream((int) result.getSize());
        result.getContent(os);
        return os.toByteArray();
    }

    @Override
    public NodeRef produceDefaultTemplate(NodeRef reportRef) {
        final ReportDescriptor desc = getReportEditorDAO().getReportDescriptor(reportRef);
        if (desc == null) {
            return null;
        }

        final ReportFileData templateFileData = new ReportFileData();

        final byte[] contentBytes = generateReportTemplate(desc); // (!) ГЕНЕРАЦИЯ
        templateFileData.setData(contentBytes);

        // формирование названия
        final ReportDefaultsDesc def = getReportDefaultsDesc(desc.getReportType()); // умолчания для типа
        String extension = (def != null ? def.getFileExtension() : null);
        if (Utils.isStringEmpty(extension)) {
            extension = ".txt";
        }

        templateFileData.setEncoding("UTF-8");
        templateFileData.setMimeType(findMimeType(extension));

        final String reportTemplateName = desc.getMnem() + extension;
        templateFileData.setFilename(reportTemplateName);

        return storeAsContent(templateFileData, reportRef);
    }

    /**
     * Вернуть mime-тип по расширению.
     *
     * @param extension расширение файла с точкой или без, т.е. вида ".abc" или "abc".
     */
    private String findMimeType(String extension) {
        return (extension != null)
                ? serviceRegistry.getMimetypeService().getMimetype(extension.replace(".", ""))
                : null;
    }

    @Override
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
     * Сгенерировать для указанного описателя файл с шаблоном отчёта.
     * Используется сконфигурированное имя gen-шаблона.
     */
    private byte[] generateReportTemplate(ReportDescriptor reportDesc) {
        if (reportDesc == null) {
            return null;
        }

        PropertyCheck.mandatory(this, "templateFileDAO", getTemplateFileDAO());
        PropertyCheck.mandatory(this, "reportDefaults", getReportDefaults());

        final IdRContent id = IdRContent.createId(reportDesc, null);
        final ReportDefaultsDesc defaults = this.getReportDefaultsDesc(reportDesc.getReportType());

        // получение названия файла-шаблона для генерации
        final String templateFileName = (defaults != null)
                ? (!reportDesc.isSubReport() ? defaults.getGenerationTemplate() : defaults.getSubReportGenerationTemplate()) // название шаблона из установок по-умолчанию для данного типа отчёта
                : (!reportDesc.isSubReport() ? DEFAULT_REPORT_TEMPLATE : DEFAULT_SUB_REPORT_TEMPLATE );
        id.setFileName(templateFileName);

        // загрузка макета шаблона ...
        final ContentReader reader = this.getTemplateFileDAO().loadContent(id);
        try {
            final byte[] maketData = ru.it.lecm.reports.utils.Utils.ContentToBytes(reader);

					/* генерация шаблона отчёта по макету шаблона ... */

            final ReportGenerator rg = findAndCheckReportGenerator(reportDesc.getReportType());

            return rg.generateReportTemplateByMaket(maketData, reportDesc);

        } catch (Throwable ex) {
            final String msg = String.format("Report '%s': get generated template '%s' problem\n\t%s"
                    , reportDesc.getMnem(), id.getFileName(), ex.getMessage());
            logger.error(msg, ex);
            throw new RuntimeException(msg, ex);
        }
    }


    @Override
    public ReportContentDAO findContentDAO(ReportDescriptor desc) {
        if (desc == null) {
            return null;
        }

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

    @Override
    public ReportFileData generateReport(String reportName, Map<String, String> args) throws IOException {
        ReportDescriptor reportDesc = this.getRegisteredReportDescriptor(reportName);
        if (reportDesc == null) {
            throw new RuntimeException(String.format("Report descriptor '%s' not accessible (possibly report is not registered !?)", reportName));
        }

        // (!) клонирование Дескриптора, чтобы не трогать общий для всех дескриптор ...
        reportDesc = Utils.clone(reportDesc);

        final ReportContentDAO storage = this.findContentDAO(reportDesc);
        if (storage == null) {
            throw new RuntimeException(String.format("Report '%s' storage point is unknown (possibly report is not registered !?)", reportName));
        }

        // (1) передача параметров из запроса в ReportDescriptor на основании их типов
        // (2) расширение списка пришедших параметров: для диапазонов - добавление крайних значений, для ID - добавить доп поле node_id (для SQL запросов)
        Map<String, Object> paramsMap = ParameterMapper.assignParameters(reportDesc, args, serviceRegistry, substitudeService);
        if (logger.isInfoEnabled()) {
            logParameters(paramsMap, String.format("Processing report '%s' with args: \n", reportName));
        }
        // получение провайдера ...
        final String rType = Utils.coalesce(reportDesc.getReportType().getMnem(), ReportsManagerImpl.DEFAULT_REPORT_TYPE);
        final ReportGenerator reporter = this.getReportGenerators().get(rType);
        if (reporter == null) {
            throw new RuntimeException(String.format("Unsupported report kind '%s': no provider registered", rType));
        }

        return reporter.produceReport(reportDesc, paramsMap, storage);
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

    public void setSubstitudeService(SubstitudeBean substitudeService) {
        this.substitudeService = substitudeService;
    }

    /**
     * Сортировка по-умолчанию для списков Дескрипторов отчёта будет по алфавиту по названиям (кодам).
     * Здесь null-значения будут ниже в списке ("тяжёлые").
     */
    static class Comparator_ByAlphabet implements Comparator<ReportDescriptor>
    {
        @Override
        public int compare(ReportDescriptor rd1, ReportDescriptor rd2) {
            if (rd1 == rd2)
                return 0;
            final String name1 = Utils.nonblank( rd1.getDefault(), rd1.getMnem());
            final String name2 = Utils.nonblank( rd2.getDefault(), rd2.getMnem());
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
