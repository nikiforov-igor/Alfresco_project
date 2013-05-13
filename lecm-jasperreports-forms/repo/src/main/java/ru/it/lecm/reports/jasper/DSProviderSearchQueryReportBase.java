package ru.it.lecm.reports.jasper;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperReport;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.forms.jasperforms.AbstractDataSourceProvider;
import ru.it.lecm.reports.jasper.config.JRDSConfigBaseImpl.JRXField;
import ru.it.lecm.reports.jasper.config.JRDSConfigXML;
import ru.it.lecm.reports.jasper.utils.DurationLogger;
import ru.it.lecm.reports.jasper.utils.JRUtils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class DSProviderSearchQueryReportBase extends AbstractDataSourceProvider 
{
	private static final Logger logger = LoggerFactory.getLogger(DSProviderSearchQueryReportBase.class);

	// параметры смещения и кол-ва записей в выборке
	final static String XML_OFFSET = "query_offset";
	final static String XML_LIMIT = "query_limit";
	// final static String XML_PGSIZE = "query_pgsize";
	final static int UNLIMITED = -1; // value means "no counter limit" for XML_LIMIT and XML_PGSIZE arguments


	/**
	 * Запрос и НД, полученный после запроса к Альфреско
	 */
	protected ResultSet alfrescoResult;
	protected SearchParameters search;
	// protected int foundCount;

	/**
	 * Список простых Альфреско-атрибутов, которые нужны для отчёта.
	 * Имена - с короткими префиксами.
	 * null означает, что ограничений нет.
	 */
	protected Set<String> jrSimpleProps;

	public DSProviderSearchQueryReportBase() {
		logger.debug( "created "+ this.getClass().getSimpleName());
	}

	/**
	 * XML-конфигурация
	 */
	protected JRDSConfigXML conf = new JRDSConfigXML() {
		@Override
		protected void setDefaults() {
			super.setDefaults();
			getArgs().put( XML_OFFSET, null);
			getArgs().put( XML_LIMIT, null);
			// getArgs().put( XML_PGSIZE, null);
		}
	};


	@Override
	public void dispose(JRDataSource ds) throws JRException {
		// nothing to do
	}

	@Override
	protected void initFields() {
	}

	@Override
	public JRDataSource create(JasperReport report) throws JRException {
		return createDS(report);
	}

	protected AlfrescoJRDataSource createDS(JasperReport report) throws JRException {
		if (report != null) {
			// get the data source parameters from the report
			final Map<String, JRParameter> params = JRUtils.buildParamMap(report.getParameters());
			conf.setArgsByJRParams(params); // + conf.loadConfig() inside
		}

		if (alfrescoResult == null) {
			execQuery();
			if (alfrescoResult == null)
				return null;
		}

		// Create a new data source
		final AlfrescoJRDataSource dataSource = newJRDataSource(alfrescoResult.iterator());
		dataSource.setSubstitudeService(substitudeService);
		dataSource.setRegistryService(serviceRegistry);
		dataSource.setJrSimpleProps(jrSimpleProps);
		if (conf != null)
			dataSource.setMetaFields(conf.getMetaFields());

		return dataSource;
	}


	/**
	 * Внутренни метод для создания нужного набора данных
	 * @param iterator
	 * @return
	 */
	protected AlfrescoJRDataSource newJRDataSource(Iterator<ResultSetRow> iterator) {
		return new AlfrescoJRDataSource(iterator);
	}

	protected void clearSearch() {
		alfrescoResult = null;
		// foundCount = -1;
		search = null;
	}

	
	public int getArgQueryOffset() {
		return (conf != null) ? conf.getint(XML_OFFSET, 0) : 0;
	}

	public int getArgQueryLimit() {
		return (conf != null) ? conf.getint(XML_LIMIT, UNLIMITED) : UNLIMITED;
	}

	/*
	private int getArgQueryPgSize() {
		return conf.getint(XML_PGSIZE, UNLIMITED);
	}
	 */

	final static char QUOTE = '\"';
	static String quoted( final String s) {
		return QUOTE + s+ QUOTE;
	}

	/**
	 * Построить запрос к Альфреско (Lucene и пр) по данным соот-го фильтра отчёта.
	 * Example: 
	 *    TYPE:"{http://www.it.ru/logicECM/contract/1.0}document" AND  @lecm\-contract\:totalAmount:(0 TO 23450000)   AND @lecm\-contract\:endDate:[ NOW/DAY TO *]
	 * @return
	 */
	// TODO: функцией экранировать символы в названиях атрибутов
	protected abstract String buildQueryText();

	/**
	 * На основе конфигурации conf.metaFields формируется список jrSimpleProps
	 * с именами простых полей (т.е. отбрасываются вычисляемые). 
	 * Список == null означает включение всех полей.
	 */
	protected void scanSimpleFieldsInMetaConf() {
		jrSimpleProps = null; // no filter = all fields
		if (conf != null) { // вносим только поля Альфреско (которые отличаются от jr-полей)
			final Map<String, JRXField> meta = conf.getMetaFields(); //
			if (meta != null && !meta.isEmpty()) {
				jrSimpleProps = new HashSet<String>();
				final NamespaceService ns = serviceRegistry.getNamespaceService();
				for (JRXField fld: meta.values()) {
					if (AlfrescoJRDataSource.isCalcField(fld.getValueLink())) { // пропускаем вычисляемые значения ... 
						continue;
					}
					final QName qname = QName.createQName( fld.getValueLink(), ns);
					jrSimpleProps.add( qname.toPrefixString(ns)); // (!) регим короткое название
				}
			}
		}
	}

	protected void execQuery() {
		final DurationLogger d = new DurationLogger();

		clearSearch();
		scanSimpleFieldsInMetaConf(); // scan in conf.getMetaFields()

		final String queryText = buildQueryText();
		if (logger.isDebugEnabled()) {
			logger.debug( String.format("Quering afresco by:>>>\n%s\n<<<", queryText));
		}

		this.search = new SearchParameters();
		search.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		search.setLanguage(SearchService.LANGUAGE_LUCENE);
		search.setQuery(queryText);

		final int skipCount = getArgQueryOffset();
		if (skipCount > 0)
			search.setSkipCount(skipCount);

		final int maxItems = getArgQueryLimit();
		if (maxItems != UNLIMITED)
			search.setMaxItems(maxItems);

		if (queryText != null && queryText.length() > 0) { // (!) момент истины ЗАПРОСА
			alfrescoResult = serviceRegistry.getSearchService().query(search);
		}

		final int foundCount = (alfrescoResult != null && alfrescoResult.hasMore()) ? alfrescoResult.length() : -1;
		d.logCtrlDuration(logger, String.format( 
				"\nQuery in {t} msec: found %d rows, limit %d, offset %d" +
				"\n>>>%s\n<<<"
				, foundCount, maxItems,  skipCount, queryText));
	}
}
