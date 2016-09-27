package ru.it.lecm.reports.generators;

import net.sf.jasperreports.engine.*;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.search.EmptyResultSet;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.InvalidQNameException;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.reports.api.DataFilter;
import ru.it.lecm.reports.api.ReportDSContext;
import ru.it.lecm.reports.api.ReportsManager;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.beans.LinksResolver;
import ru.it.lecm.reports.beans.ReportProviderExt;
import ru.it.lecm.reports.beans.WKServiceKeeper;
import ru.it.lecm.reports.jasper.AlfrescoJRDataSource;
import ru.it.lecm.reports.jasper.ReportDSContextImpl;
import ru.it.lecm.reports.jasper.config.JRDSConfigXML;
import ru.it.lecm.reports.jasper.filter.DataFilterByLinks;
import ru.it.lecm.reports.jasper.utils.JRUtils;
import ru.it.lecm.reports.model.impl.ColumnDescriptor;
import ru.it.lecm.reports.rs.NodeRefsResultSet;
import ru.it.lecm.reports.utils.ParameterMapper;
import ru.it.lecm.reports.utils.Utils;
import ru.it.lecm.reports.xml.DSXMLProducer;
import ru.it.lecm.reports.utils.LuceneSearchWrapper;

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
    private JRDSConfigXML xmlConfig = null; // для загрузки конфы из ds-xml

    public WKServiceKeeper getServices() {
        return services;
    }

    public void setServices(WKServiceKeeper services) {
        this.services = services;
    }

    @Override
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

    public LinksResolver getResolver() {
        return this.resolver;
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
    protected LuceneSearchWrapper buildQuery(ReportDescriptor descriptor, ReportDSContext parentContext) {
        return getQueryHelper().prepareQuery(descriptor, parentContext);
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
    protected LuceneSearchWrapper execQuery(ReportDescriptor descriptor, ReportDSContext parentContext) {
        /* формирование запроса: параметры выбираются непосредственно из reportDescriptor */
        final LuceneSearchWrapper preparedQuery = this.buildQuery(descriptor, parentContext);

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Query to Alfresco:>>>\n%s\n<<<", preparedQuery));
        }

        ResultSet rs = null;

        if (!resolver.isSubstCalcExpr(preparedQuery.getQuery().toString())) {
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

            if (skipCountOffset < 0) {
                skipCountOffset = 0;
            }
            if (maxItems != UNLIMITED) {
                search.setMaxItems(maxItems);
            }

            search.addSort("@" + ContentModel.PROP_NODE_DBID, true);

            /* (!) момент истины - выполнение ЗАПРОСА */
            List<NodeRef> nodes = new ArrayList<>();

            boolean hasNodes = true;

            if (!preparedQuery.isEmpty()) {
                while(hasNodes) {
                    search.setSkipCount(skipCountOffset);

                    rs = getServices().getServiceRegistry().getSearchService().query(search);

                    for (ResultSetRow row : rs) {
                        nodes.add(row.getNodeRef());
                    }

                    hasNodes = rs.length() > 0;
                    skipCountOffset = skipCountOffset + rs.length();
                }
            }
            rs = new NodeRefsResultSet(getServices().getServiceRegistry().getNodeService(), nodes);
        } else if (parentContext != null) {
            NodeRef docId = parentContext.getCurNodeRef();
            List<NodeRef> children = getServices().getSubstitudeService().getObjectsByTitle(docId, preparedQuery.getQuery().toString());
            if (children != null && !children.isEmpty()) {
                rs = new NodeRefsResultSet(getServices().getServiceRegistry().getNodeService(), children);
            } else {
                rs = new EmptyResultSet();
            }
        }

        preparedQuery.setSearchResults(rs);

        final int foundCount = (rs != null) ? rs.length() : -1;

        if (logger.isDebugEnabled()) {
            logger.debug("Query to Alfresco:foundCount=" + foundCount);
        }
        return preparedQuery;
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
        return createDS(this.reportDescriptor, null);
    }

    protected AlfrescoJRDataSource createDS(ReportDescriptor descriptor, ReportDSContext parentContext) {
        this.reportDescriptor = descriptor;
        LuceneSearchWrapper alfrescoQuery = execQuery(descriptor, parentContext);
        if (alfrescoQuery == null || alfrescoQuery.getSearchResults() == null) {
            return null;
        }

        Iterator<ResultSetRow> iterator = alfrescoQuery.getSearchResults().iterator();

        // Create a new data source
        final AlfrescoJRDataSource dataSource = new AlfrescoJRDataSource(this);
        fillContext(alfrescoQuery, dataSource.getContext());

        String sortSettings = null;
        String querySort = reportDescriptor.getFlags().getSort();
        if (querySort != null && !querySort.isEmpty()) {
            sortSettings = querySort;
        }

        iterator = resolver.sortObjects(iterator, sortSettings, dataSource.getContext());
        dataSource.getContext().setRsIter(iterator); // устанавливаем итератор в ДС

        return dataSource;
    }

    /**
     * Получить список имён простых колонок в виде последовательности пар "тип", "атрибут" (QName Альфреско).
     *
     * @return Set<String>
     */
    public Set<String> getSimpleColumnNames(ReportDescriptor descriptor) {
        if (descriptor == null) {
            return null;
        }
        final Set<String> result = new HashSet<String>();
        final NamespaceService namespaceService = getServices().getServiceRegistry().getNamespaceService();
        final DictionaryService dictionaryService = getServices().getServiceRegistry().getDictionaryService();
        for (ColumnDescriptor colDesc : descriptor.getDsDescriptor().getColumns()) {
            final String substituteExpression = colDesc.getExpression();
            if (!Utils.isStringEmpty(substituteExpression)) {
                final boolean isSimpleLink = getQueryHelper().isDirectAlfrescoPropertyLink(substituteExpression);
                if (isSimpleLink) {
                    // параметр может быть как название свойства, так и названием ассоциации - проверяем
                    QName expressionQName = null;
                    try { // создать qname поля ...
                        expressionQName = QName.createQName(colDesc.getQNamedExpression(), namespaceService);
                    } catch (InvalidQNameException ignored) {
                    }

                    if (expressionQName == null) {
                        continue; // у нас записано нечто непонятное - пропускаем
                    }

                    final boolean isProperty = (dictionaryService.getProperty(expressionQName) != null);
                    if (isProperty) {
                        result.add(colDesc.getQNamedExpression());
                    }
                }
            }
        }
        return result;
    }

    /**
     * Внутренний метод для создания фильтра данных.
     * Здесь включает такой фильтр, в котором есть отбор по параметрам-ассоциациям (cм this.alfrescoQuery.argsByLinks()).
     * В потомках позволит менять конретный тип фильтра.
     *
     * @return DataFilter
     */
    protected DataFilter getDataFilter(LuceneSearchWrapper alfrescoQuery) {
        // фильтр, который может "заглядывать" по ссылкам
        if (alfrescoQuery.getArgsByLinks() == null || alfrescoQuery.getArgsByLinks().isEmpty()) {
            return null;
        }

        boolean useFilter = false;

        final DataFilterByLinks result = new DataFilterByLinks(getServices().getSubstitudeService());

        for (ColumnDescriptor colDesc : alfrescoQuery.getArgsByLinks()) {
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
    private void fillContext(LuceneSearchWrapper alfrescoQuery, ReportDSContextImpl context) {
        if (context != null) {
            context.setRegistryService(getServices().getServiceRegistry());
            context.setJrSimpleProps(getSimpleColumnNames(this.getReportDescriptor()));
            context.setMetaFields(JRUtils.getDataFields(this.getReportDescriptor()));
            context.setResolver(this.resolver);
            // фильтр данных ...
            context.setFilter(getDataFilter(alfrescoQuery));
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
