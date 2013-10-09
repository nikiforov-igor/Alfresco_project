package ru.it.lecm.reports.beans;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.FileNameValidator;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.reports.api.ReportFileData;
import ru.it.lecm.reports.api.ReportGenerator;
import ru.it.lecm.reports.api.ReportsManager;
import ru.it.lecm.reports.api.model.ReportDefaultsDesc;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.api.model.ReportFileDataImpl;
import ru.it.lecm.reports.api.model.ReportType;
import ru.it.lecm.reports.api.model.SubReportDescriptor;
import ru.it.lecm.reports.api.model.DAO.ReportContentDAO;
import ru.it.lecm.reports.api.model.DAO.ReportContentDAO.ContentEnumerator;
import ru.it.lecm.reports.api.model.DAO.ReportContentDAO.IdRContent;
import ru.it.lecm.reports.api.model.DAO.ReportEditorDAO;
import ru.it.lecm.reports.generators.SubreportMetaDataBuilder;
import ru.it.lecm.reports.model.impl.ReportDefaultsDescImpl;
import ru.it.lecm.reports.utils.ParameterMapper;
import ru.it.lecm.reports.utils.Utils;
import ru.it.lecm.reports.xml.DSXMLProducer;

public class ReportsManagerImpl implements ReportsManager {

    static final transient Logger logger = LoggerFactory.getLogger(ReportsManagerImpl.class);

    final public static String DEFAULT_REPORT_TYPE = ReportType.RTYPE_MNEMO_JASPER;
    final public static String DEFAULT_REPORT_EXTENSION = ".jrxml";
    final public static String DEFAULT_REPORT_TEMPLATE = "jreportCommonTemplate.jrxml.gen";

    /**
     * Список зарегистрирванных отчётов
     */
    private Map<String, ReportDescriptor> descriptors;

    /**
     * список дескрипторов явно заданных бинами
     */
    private Map<String, ReportDescriptor> beanDescriptors;

    private ReportEditorDAO reportDAO; // хранилище отчётов редактора
    private ReportContentDAO contentFileDAO; // файлы готовых отчётов (поставка) в файловой системе
    private ReportContentDAO templateFileDAO; // файлы готовых макетов шаблонов (поставка для генерации шаблонов отчётов)
    private ReportContentDAO contentRepositoryDAO; // файлы создаваемых отчётов как "cm:content" в репозитории

    private MimetypeService mimeMap; // для разменований расширений файлов в mime-типы

    // генераторы отчётов по типам
    private Map< /*ReportType*/ String, ReportGenerator> reportGenerators;

    // Map<КодТипаОтчёта, [Провайдер,Расширение,Шаблон]>
    private Map< /*ReportType*/ String, ReportDefaultsDesc> reportDefaults;

    /**
     * Service registry
     */
    protected ServiceRegistry serviceRegistry;

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

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
        logger.debug(String.format("contentRepositoryDAO assigned: %s", value));
        this.contentRepositoryDAO = value;
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

    public MimetypeService getMimeMap() {
        return mimeMap;
    }

    public void setMimeMap(MimetypeService value) {
        this.mimeMap = value;
    }

    /**
     * Файлы шаблонов для генерации шаблонов отчётов
     *
     * @return
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
                // не задано фильтрование -> вернуть сразу всё целиком ...
                found.addAll(list.values());
            } else {
                for (ReportDescriptor desc : list.values()) {
                    final boolean okDocType = (desc.getFlags() == null) || desc.getFlags().isTypeSupported(docType);

                    final boolean okRType = (reportType == null) // не задан фильтр по типам отчётов
                            || ((desc.getReportType() == null)
                            || desc.getReportType().getMnem() == null) // не задан тип отчёта шаблона -> подходит к любому
                            || reportType.equalsIgnoreCase(desc.getReportType().getMnem()); // совпадение типа

                    if (okDocType && okRType) {
                        found.add(desc);
                    }
                }
            }
        } finally {
            // сортируем описания по алфавиту
            Collections.sort(found, new ReportDescriptor.Comparator_ПоАлфавиту());
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
            if (descriptor.getFlags().isCustom() && forCollection) {
                // пропускаем кастомизированные для многострочных отчётов ...
                continue;
            }

            if (forCollection == descriptor.getFlags().isMultiRow()) {
                resultedReports.add(descriptor);
            }
        }
        // сортируем описания по алфавиту
        Collections.sort(resultedReports, new ReportDescriptor.Comparator_ПоАлфавиту());
        return resultedReports;
    }

    /**
     * Дескрипторы, заданные бинами
     *
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
     * 1) дискрипторы, заданные бмнами,
     * 2) дескрипторы, имеющиеся в виде файлов,
     * 3) продеплоенные в хранилище дескрипторы.
     * (в этом списке чем ниже тем приоритетнее)
     *
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
            final ReportDefaultsDesc jdesc = new ReportDefaultsDescImpl(DEFAULT_REPORT_EXTENSION, DEFAULT_REPORT_TEMPLATE);
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
     * @param repos
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
            return descriptors.get(reportMnemoName); // FOUND by Mnemonic
        }

        logger.warn(String.format("Report '%s' has no descriptor", reportMnemoName));
        return null; // NOT FOUND
    }

    @Override
    public void registerReportDescriptor(NodeRef rdescId) {
        PropertyCheck.mandatory(this, "reportDAO", getReportDAO());
        final ReportDescriptor rdesc = getReportDAO().getReportDescriptor(rdescId);
        registerReportDescriptor(rdesc);
    }

    @Override
    public void registerReportDescriptor(ReportDescriptor desc) {
        if (desc != null) {
            createDsFile(desc); // создание ds-xml
            saveReportTemplate(desc); // сохранение шаблона отчёта
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
     *
     * @param desc
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
                    , (def != null) ? def.getGenerationTemplate() : DEFAULT_REPORT_TEMPLATE // // makeGenTemplateFileName(...)
            ));
        }

        // рекурсивно для подотчётов ...
        if (desc.getSubreports() != null) {
            for (SubReportDescriptor sr : desc.getSubreports()) {
                // TODO: шаблоны для подотчётов надо сгенерировать ТАМ же где основной отчёт
                // TODO: при построении подотчётов придётся выгрузить файлы подотчётов на диск, а значит и основной отчёт тоже.
                setDefaults(sr);
            }
        }
    }

    /**
     * Сохранение шаблона отчёта из desc.getReportTemplate()
     *
     * @param desc
     * @return
     */
    private boolean saveReportTemplate(ReportDescriptor desc) {
        if (desc.getReportTemplate() == null) {
            logger.warn(String.format("Report '%s' has no template", desc.getMnem()));
            return false;
        }

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
                    final IdRContent id = IdRContent.createId(desc, String.format("%s%s", desc.getMnem(), ext));

                    templateRawData = byteStream.toByteArray();
                    this.contentRepositoryDAO.storeContent(id, new ByteArrayInputStream(templateRawData));
                }
            }

			/*
             * оповещение соот-го провайдера (компиляция) ...
			 * т.к. деплоить можно только отчёты в репозиторий, здесь указываем 
			 * именно его (не требуется определять откуда получен описатель - из 
			 * файлового хранилища или из репозитория)
			 */
            findAndCheckReportGenerator(desc.getReportType()).onRegister(desc, templateRawData, this.contentRepositoryDAO);
            logger.debug(String.format("Report '%s': provider notified", desc.getMnem()));

            // рекурсивно для подотчётов ...
            if (desc.getSubreports() != null) {
                for (SubReportDescriptor sr : desc.getSubreports()) {
                    saveReportTemplate(sr);
                } // for sr
            }

        } catch (Throwable ex) {
            final String msg = String.format(
                    "Error saving template content for Report Descriptor %s"
                    , desc.getMnem());
            logger.warn(msg, ex);
            throw new RuntimeException(msg, ex);
        }

        return true;
    }

    /**
     * Вернуть зарегистрированный генератор шаблонов для указанного типа отчётов.
     * Если нет такого - поднимается исключение.
     *
     * @param rtype тип отчёта
     * @return
     */
    protected ReportGenerator findAndCheckReportGenerator(ReportType rtype) {
        if (rtype == null) {
            return null;
        }

        String rtag = getReportTypeTag(rtype);
        ReportGenerator result = getReportGenerators().get(rtag);
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
     *
     * @param desc
     */
    private void createDsFile(ReportDescriptor desc) {
        if (desc == null) {
            return;
        }

        checkReportDescData(desc);
        checkSubreports(desc);
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
     *
     * @param desc
     */
    private void checkReportDescData(ReportDescriptor desc) {
        if (desc.getMnem() == null || desc.getMnem().trim().isEmpty()) {
            throw new RuntimeException(String.format("Report descriptor must have mnemo code"));
        }
    }

    /**
     * Если нет явно заданного списка подотчётов desc.subreports - cформировать
     * его на основе линейного списка колонок НД.
     *
     * @param desc
     */
    private void checkSubreports(ReportDescriptor desc) {
        /** формирование Subreports из общего линейного списка колонок */
        if (desc.getSubreports() == null) { // (!) только если подотчётов ещё нет
            final List<SubReportDescriptor> srlist = SubreportMetaDataBuilder.scanSubreports(desc.getDsDescriptor());
            if (srlist != null) {
                logger.info(String.format("Report '%s': found %s subreports", desc.getMnem(), srlist.size()));
                desc.setSubreports(srlist);
            } else {
                logger.info(String.format("Report '%s': no subreports found", desc.getMnem()));
            }
        } else {
            logger.info(String.format("Report '%s' contains %s subreports", desc.getMnem(), desc.getSubreports().size()));
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
        final ReportDescriptor desc = getReportDAO().getReportDescriptor(reportRef);
        if (desc == null) {
            return null;
        }

        final ReportFileDataImpl templateFileData = new ReportFileDataImpl();

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
     * @return
     */
    private String findMimeType(String extension) {
        return (extension == null) ? null : this.getMimeMap().getMimetype(extension.replace(".", ""));
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
     *
     * @param reportDesc
     * @return
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
                ? defaults.getGenerationTemplate() // название шаблона из установок по-умолчанию для данного типа отчёта
                : DEFAULT_REPORT_TEMPLATE;
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
    public ReportFileData generateReport(String reportName, Map<String, String[]> args) throws IOException {
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

        ParameterMapper.assignParameters(reportDesc, args);

        final String rtype = Utils.coalesce(reportDesc.getReportType().getMnem(), ReportsManagerImpl.DEFAULT_REPORT_TYPE);

        // получение провайдера ...
        final ReportGenerator reporter = this.getReportGenerators().get(rtype);
        if (reporter == null) {
            throw new RuntimeException(String.format("Unsupported report kind '%s': no provider registered", rtype));
        }

        final ReportFileData result = new ReportFileDataImpl();
        reporter.produceReport(result, reportDesc, args, storage);
        return result;
    }
}
