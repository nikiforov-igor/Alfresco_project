package ru.it.lecm.reports.generators;

import net.sf.jasperreports.engine.*;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.reports.api.DataFilter;
import ru.it.lecm.reports.api.ReportsManager;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.beans.LinksResolver;
import ru.it.lecm.reports.beans.ReportProviderExt;
import ru.it.lecm.reports.beans.WKServiceKeeper;
import ru.it.lecm.reports.jasper.AlfrescoJRDataSource;
import ru.it.lecm.reports.jasper.GenericJRDataSource;
import ru.it.lecm.reports.jasper.ReportDSContextImpl;
import ru.it.lecm.reports.jasper.config.JRDSConfigXML;
import ru.it.lecm.reports.jasper.filter.DataFilterByLinks;
import ru.it.lecm.reports.jasper.utils.DurationLogger;
import ru.it.lecm.reports.jasper.utils.JRUtils;
import ru.it.lecm.reports.model.impl.ColumnDescriptor;
import ru.it.lecm.reports.utils.ParameterMapper;
import ru.it.lecm.reports.utils.Utils;
import ru.it.lecm.reports.xml.DSXMLProducer;
import ru.it.lecm.utils.LuceneSearchWrapper;

import java.util.*;

/**
 * Провайдер данных.
 * Основное назначение - получение НД для указанного описателя шаблона с учётом
 * параметров фильтрации.
 * Умеет строить lucene-запрос для своей выборки (по простым полям не-ассоциациям)
 * и создаёт фильтр данных для отбора по ассоциациям.
 *
 * @author rabdullin
 */
public class GenericDSProviderBase implements JRDataSourceProvider, ReportProviderExt {

    private static final Logger logger = LoggerFactory.getLogger(GenericDSProviderBase.class);

    /**
     * value means "no counter limit" for XML_LIMIT and XML_PGSIZE arguments
     */
    final static int UNLIMITED = -1;

    private WKServiceKeeper services;
    private LinksResolver resolver;
    private ReportDescriptor reportDescriptor;
    private ReportsManager reportsManager;
    private LucenePreparedQueryHelper queryHelper;

    /**
     * Запрос и НД, полученный после запроса к Альфреско
     */
    protected LuceneSearchWrapper alfrescoQuery;
    protected ResultSet alfrescoResult;
    private JRDSConfigXML xmlConfig = null; // для загрузки конфы из ds-xml

    /**
     * Список простых Альфреско-атрибутов, которые нужны для отчёта.
     * Имена - с короткими префиксами.
     * null означает, что ограничений нет.
     */
    protected Set<String> jrSimpleProps;

    public WKServiceKeeper getServices() {
        return services;
    }

    public void setServices(WKServiceKeeper services) {
        this.services = services;
    }

    public ReportDescriptor getReportDescriptor() {
        return reportDescriptor;
    }

    @Override
    public void setReportDescriptor(ReportDescriptor rdesc) {
        if (Utils.isSafelyEquals(this.reportDescriptor, rdesc)) {
            return;
        }
        this.reportDescriptor = rdesc;

        if (this.reportDescriptor != null) {
            try {
                getConfigXML().setConfigName(DSXMLProducer.makeDsConfigFileName(this.getReportDescriptor().getMnem()));
                getConfigXML().loadConfig();
            } catch (JRException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void setReportsManager(ReportsManager reportsManager) {
        this.reportsManager = reportsManager;
    }

    @Override
    public void initializeFromGenerator(ReportGeneratorBase baseGenerator) {
        this.setServices(baseGenerator.getServices());
        this.setResolver(baseGenerator.getResolver());
        this.setQueryHelper(baseGenerator.getQueryHelper());
    }

    public void setResolver(LinksResolver resolver) {
        this.resolver = resolver;
    }

    public void setQueryHelper(LucenePreparedQueryHelper queryHelper) {
        this.queryHelper = queryHelper;
    }

    public ReportsManager getReportsManager() {
        return reportsManager;
    }

    public LucenePreparedQueryHelper getQueryHelper() {
        return queryHelper;
    }

    protected void clearSearch() {
        alfrescoResult = null;
        alfrescoQuery = null;
    }

    /**
     * Стандартное построение запроса согласно параметров this.reportDescriptor.
     * В классах-потомках может использоваться другая логика параметризации
     * и построения отчётов.
     * Здесь генерируется текст Lucene-запроса с учётомЖ
     * 1) типа (TYPE),
     * 2) ID
     * 3) имеющихся простых параметров,
     * 4) возможного текста запроса из флагов (reportDescriptor.flags.text).
     *
     * @return LucenePreparedQueryHelper
     */
    protected LuceneSearchWrapper buildQuery() {
        alfrescoQuery = getQueryHelper().prepareQuery(this.reportDescriptor);
        return alfrescoQuery;
    }

    protected JRDSConfigXML getConfigXML() {
        if (xmlConfig == null) {
            xmlConfig = new ConfigXMLOfGenericDsProvider(this.getReportsManager());
        }
        return xmlConfig;
    }

    /**
     * Дополнить конфигурацию значениями по-умолчанию
     */
    protected void setXMLDefaults(Map<String, Object> defaults) {
        // "add-on" sections для чтения конфигуратором ...
    }

    /**
     * Формирует alfrescoResult согласно запросу полученному от buildQueryText и
     * параметрам limit/offset.
     */
    protected ResultSet execQuery() {
        final DurationLogger d = new DurationLogger();

        clearSearch();
        /* формирование запроса: параметры выбираются непосредственно из reportDescriptor */
        final LuceneSearchWrapper preparedQuery = this.buildQuery();

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Query to Alfresco:>>>\n%s\n<<<", preparedQuery));
        }

        final SearchParameters search = new SearchParameters();
        search.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        search.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
        search.setQuery(preparedQuery.toString());

        int skipCountOffset = -1,
                maxItems = UNLIMITED;

        if (this.reportDescriptor.getFlags() != null) {
            // set offset ...
            skipCountOffset = this.reportDescriptor.getFlags().getOffset();
            // set limit ...
            maxItems = this.reportDescriptor.getFlags().getLimit();
        }
        if (skipCountOffset > 0) {
            search.setSkipCount(skipCountOffset);
        }
        if (maxItems != UNLIMITED) {
            search.setMaxItems(maxItems);
        }

		/* (!) момент истины - выполнение ЗАПРОСА */
        ResultSet rs = null;
        if (!preparedQuery.isEmpty()) {
            rs = getServices().getServiceRegistry().getSearchService().query(search);
        }

        final int foundCount = (rs != null) ? rs.length() : -1;
        d.logCtrlDuration(logger, String.format(
                "\nQuery in {t} msec: found %d rows, limit %d, offset %d" +
                        "\n>>>%s\n<<<"
                , foundCount, maxItems, skipCountOffset, preparedQuery));

        return rs;
    }

    @Override
    public boolean supportsGetFieldsOperation() {
        return true;
    }

    @Override
    public JRField[] getFields(JasperReport report) throws JRException, UnsupportedOperationException {
        final List<JRField> result = JRUtils.getJRFields(this.getReportDescriptor());
        return (result != null) ? result.toArray(new JRField[result.size()]) : null;
    }

    @Override
    public void dispose(JRDataSource ds) throws JRException {
        logger.debug(String.format("Disposing dataSource: %s", (ds == null ? "null" : ds.getClass().getName())));
    }

    @Override
    public JRDataSource create(JasperReport report) throws JRException {
        if (alfrescoResult == null) { // выполнение запроса ...
            alfrescoResult = execQuery();
            if (alfrescoResult == null) {
                return null;
            }
        }

        Iterator<ResultSetRow> iterator = alfrescoResult.iterator();

        final ReportDSContextImpl context = new ReportDSContextImpl();
        fillContext(context);

        String sortSettings = null;
        String querySort = reportDescriptor.getFlags().getSort();
        if (querySort != null && !querySort.isEmpty()) {
            sortSettings = querySort;
        }

        iterator = resolver.sortObjects(iterator, sortSettings, context);

        // Create a new data source
        final AlfrescoJRDataSource dataSource = newJRDataSource(iterator);
        fillContext(dataSource.getContext());

        return dataSource;
    }

    /**
     * Получить список имён простых колонок в виде последовательности пар "тип", "атрибут" (QName Альфреско).
     *
     * @param list список колонок, в которых выражение является ссылкой на атрибут
     * @return Set<String>
     */
    public Set<String> getColumnNames(List<ColumnDescriptor> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        final Set<String> result = new HashSet<String>();
        final NamespaceService ns = getServices().getServiceRegistry().getNamespaceService();
        for (ColumnDescriptor col : list) {
            final QName qname = QName.createQName(col.getQNamedExpression(), ns);
            if (qname != null) {
                result.add(qname.toPrefixString(ns)); // (!) регим короткое название
                result.add(col.getColumnName()); //todo denisB ???
            }
        }
        return result;
    }

    /**
     * Внутренний метод для создания нужного набора данных.
     * В потомках позволит менять конретный тип НД.
     *
     * @param iterator Iterator<ResultSetRow>
     * @return AlfrescoJRDataSource
     */
    protected AlfrescoJRDataSource newJRDataSource(Iterator<ResultSetRow> iterator) {
        return new GenericJRDataSource(reportDescriptor, resolver, iterator);
    }

    /**
     * Внутренний метод для создания фильтра данных.
     * Здесь включает такой фильтр, в котором есть отбор по параметрам-ассоциациям (cм this.alfrescoQuery.argsByLinks()).
     * В потомках позволит менять конретный тип фильтра.
     *
     * @return DataFilter
     */
    protected DataFilter getDataFilter() {
        // фильтр, который может "заглядывать" по ссылкам
        if (this.alfrescoQuery.getArgsByLinks() == null || this.alfrescoQuery.getArgsByLinks().isEmpty()) {
            return null;
        }

        boolean useFilter = false;

        final DataFilterByLinks result = new DataFilterByLinks(getServices().getSubstitudeService());

        for (ColumnDescriptor colDesc : this.alfrescoQuery.getArgsByLinks()) {
            try {
                DataFilter.FilterType filter;
                String expression = colDesc.getExpression();
                if (Utils.hasStartOnce(expression, SubstitudeBean.OPEN_SUBSTITUDE_SYMBOL)
                        && Utils.hasEndOnce(expression, SubstitudeBean.CLOSE_SUBSTITUDE_SYMBOL)) {
                    final List<Object> targetValues = ParameterMapper.getArgsList(colDesc);
                    if (!targetValues.isEmpty()) {
                        filter = ParameterMapper.getFilterType(colDesc);
                        useFilter = true;
                        result.addFilter(new DataFilter.DataFilterDesc(filter, expression, targetValues));
                    }
                }
            } catch (Exception ignored) {
                logger.warn("Ignoring error at process parameteres:\n", ignored);
            }
        }

        return (useFilter) ? result : null;
    }

    /**
     * Заполнение контекста используемыми службами, описанием полей.
     *
     * @param context ReportDSContextImpl
     */
    private void fillContext(ReportDSContextImpl context) {
        if (context != null) {
            context.setRegistryService(getServices().getServiceRegistry());
            context.setJrSimpleProps(getColumnNames(this.alfrescoQuery.getArgsByProps()));
            context.setMetaFields(JRUtils.getDataFields(this.getReportDescriptor()));
            context.setResolver(this.resolver);
            // фильтр данных ...
            context.setFilter(getDataFilter());
        }
    }

    //TODO denisB надо избавиться от этой кастомизации (поможет привести отчеты к универсальному виду(провайдеру)
    private class ConfigXMLOfGenericDsProvider extends JRDSConfigXML {
        public ConfigXMLOfGenericDsProvider(ReportsManager mgr) {
            super(mgr);
        }

        @Override
        protected void setDefaults(Map<String, Object> defaults) {
            super.setDefaults(defaults);
            setXMLDefaults(defaults);
        }
    }
}
