package ru.it.lecm.reports.jasper;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperReport;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.forms.jasperforms.AbstractDataSourceProvider;
import ru.it.lecm.reports.jasper.config.JRDSConfigBaseImpl.JRXField;
import ru.it.lecm.reports.jasper.config.JRDSConfigXML;
import ru.it.lecm.reports.jasper.filter.AssocDataFilterImpl;
import ru.it.lecm.reports.jasper.utils.DurationLogger;
import ru.it.lecm.reports.jasper.utils.JRUtils;
import ru.it.lecm.reports.jasper.utils.Utils;

/**
 * Отчёт по реестру договоров
 * Параметры:
 *   "contractSubject" - тематика договора
 *   "contractType" - тип договора
 *   "contractContractor" - контрагент
 *   "contractActualOnly" - только актуальные
 *   "contractSum" - сумма
 *   "end" - стартовая дата, example "2013-04-03T00:00:00.000+06:00"
 *   "start" - конечна дата
 * @author rabdullin
  */
public class DSProviderReestrDogovorov extends AbstractDataSourceProvider {

	private static final Logger logger = LoggerFactory.getLogger(DSProviderReestrDogovorov.class);

	/**
	 * НД, полученный после запроса к Альфреско
	 */
	private ResultSet alfrescoResult;
	private SearchParameters search;
	private int foundCount;

	/**
	 * список gname Альфреско-атрибутов, которые только и нужны для отчёта
	 * (с короткими префиксами)
	 * null означает, что ограничений нет.
	 */
	private Set<String> alfVisibleProps;

	private JRDSConfigXML conf = new JRDSConfigXML() {
		@Override
		protected void setDefaults() {
			super.setDefaults();
			getArgs().put( XML_OFFSET, null);
			getArgs().put( XML_LIMIT, null);
			// getArgs().put( XML_PGSIZE, null);
		}
	};

	public DSProviderReestrDogovorov() {
		logger.debug( "created");
	}

	/**
	 * Фильтр поиска:
	 * "contractSubject" - тематика договора
	 * "contractType" - тип договора
	 * "contractContractor" - контрагент
	 * 
	 * "contractActualOnly" - только актуальные
	 * "contractSum" - сумма
	 * 
	 * "end" - стартовая дата, fmt like "2013-04-30T00:00:00.000+06:00"
	 * "start" - конечна дата, fmt the same
	 */
	private class SearchFilter  {

		Date dateStart, dateEnd;
		Double contractSum;
		Boolean contractActualOnly;
		NodeRef contractSubject, contractType, contragent;

		public void clear() {
			dateStart = dateEnd = null;
			contractSum = null;
			contractSubject = contractType = contragent = null;
			contractActualOnly = null;
		}

		public AssocDataFilterImpl makeAssocFilter() {

			final boolean hasSubject = (contractSubject != null);
			final boolean hasType = (contractType != null);
			final boolean hasCAgent = (contragent != null);
			final boolean hasAny = hasSubject || hasType || hasCAgent;
			if (!hasAny) // в фильтре ничего не задачно -> любые данные подойдут
				return null;

			final AssocDataFilterImpl result = new AssocDataFilterImpl( serviceRegistry);

			final NamespaceService ns = serviceRegistry.getNamespaceService();

			if (hasSubject) {
				final QName qnCSubject = QName.createQName( "lecm-contract:subjectContract-assoc", ns); // Тематика договора
				result.addChildAssoc( qnCSubject, contractSubject);
			}

			if (hasType) {
				final QName qnCType = QName.createQName( "lecm-contract:typeContract-assoc", ns); // Вид договора
				result.addChildAssoc( qnCType, contractType);
			}

			if (hasCAgent) {
				final QName qnCAgent = QName.createQName( "lecm-contract:partner-assoc", ns); // Контрагенты
				result.addChildAssoc( qnCAgent, contragent);
			}

			return result;
		}

	}

	private final SearchFilter filter = new SearchFilter();

	public void setStart( final String value) {
		filter.dateStart = ArgsHelper.makeDate(value, "dateStart");
	}

	public void setEnd( final String value) {
		filter.dateEnd = ArgsHelper.makeDate(value, "dateEnd");
	}

	public void setContractSubject(String value) {
		filter.contractSubject = ArgsHelper.makeNodeRef(value, "contractSubject");
	}

	public void setContractType(String value) {
		filter.contractType = ArgsHelper.makeNodeRef(value, "contractType");
	}

	public void setContractContractor(String value) {
		filter.contragent = ArgsHelper.makeNodeRef(value, "contragent");
	}

	//"contractActualOnly" - только актуальные
	public void setContractActualOnly(String value) {
		filter.contractActualOnly = Utils.isStringEmpty(value) ? null : Boolean.parseBoolean(value);
	}

	//"contractSum" - сумма
	public void setContractSum(String value) {
		try {
			filter.contractSum = Utils.isStringEmpty(value) ? null : Double.parseDouble(value);
		} catch(Throwable e) {
			logger.error( String.format( "unexpected double value '%s' for contractSum -> ignored as NULL", value), e);
			filter.contractSum = null;
		}
	}


	private void clearSearch(boolean enClearFilter) {
		alfrescoResult = null;
		foundCount = -1;
		search = null;
		if (enClearFilter) filter.clear();
	}


	// параметры смещения и кол-ва записей в выборке
	final static String XML_OFFSET = "query_offset";
	final static String XML_LIMIT = "query_limit";
	// final static String XML_PGSIZE = "query_pgsize";
	final static int UNLIMITED = -1; // value means "no counter limit" for XML_LIMIT and XML_PGSIZE arguments


	@Override
	public void dispose(JRDataSource ds) throws JRException {
		// nothing to do
	}

	@Override
	protected void initFields() {
	}

	@Override
	public JRDataSource create(JasperReport report) throws JRException {
		if (report != null) {
			// get the data source parameters from the report
			final Map<String, JRParameter> params = JRUtils.buildParamMap(report.getParameters());
			conf.setArgsByJRParams(params); // + conf.loadConfig() inside
		}

		execQuery();

		if (alfrescoResult == null)
			return null;

		// Create a new CMIS data source
		final AlfrescoJRDataSource dataSource = new AlfrescoJRDataSource(alfrescoResult.iterator());
		dataSource.setSubstitudeService(substitudeService);
		dataSource.setRegistryService(serviceRegistry);
		dataSource.setMetaFields(conf.getMetaFields());
		dataSource.setVisibleProps(alfVisibleProps);

		if (filter != null)
			dataSource.setFilter(filter.makeAssocFilter());

		return dataSource;
	}


	private int getArgQueryOffset() {
		return conf.getint(XML_OFFSET, 0);
	}

	private int getArgQueryLimit() {
		return conf.getint(XML_LIMIT, UNLIMITED);
	}

	/*
	private int getArgQueryPgSize() {
		return conf.getint(XML_PGSIZE, UNLIMITED);
	}
	 */


	final private static char QUOTE = '\"';
	static String quoted( final String s) {
		return QUOTE + s+ QUOTE;
	}

	final String TYPE_CONRACT = "lecm-contract:document";

	/**
	 * Построить Lucene-запрос по данным фильтра.
	 * Example: 
	 *    TYPE:"{http://www.it.ru/logicECM/contract/1.0}document" AND  @lecm\-contract\:totalSum:(0 TO 23450000)   AND @lecm\-contract\:endDate:[ NOW/DAY TO *]
	 * @return
	 */
	private String buildQueryText() {
		final StringBuilder bquery = new StringBuilder();
		final QName qTYPE = QName.createQName(TYPE_CONRACT, this.serviceRegistry.getNamespaceService());
		bquery.append( "TYPE:"+ quoted(qTYPE.toString()));

		// начало
		if (filter.dateStart != null) { // "X to MAX"
			final String stMIN = ArgsHelper.dateToStr( filter.dateStart, "MIN");
			bquery.append( " AND @lecm\\-contract\\:startDate:[" + stMIN + " TO MAX]");
		}

		// окончание
		if (filter.dateEnd != null) { // "MIN to X"
			final String stMAX = ArgsHelper.dateToStr( filter.dateEnd, "MAX");
			bquery.append( " AND @lecm\\-contract\\:endDate:[ MIN TO " + stMAX + "]");
		}

		// Сумма договора (указан минимум)
		if (filter.contractSum != null) { // "X to *"
			bquery.append( " AND @lecm\\-contract\\:totalSum:(" + filter.contractSum.toString() + " TO *)");
		}

		// Контракт актуален: если ещё не истёк срок 
		if ( Boolean.TRUE.equals(filter.contractActualOnly)) {
			bquery.append( " AND @lecm\\-contract\\:endDateSum:[NOW TO MAX]"); // "истекает позже чем сейчас"
		}

		return bquery.toString();
	}


	/*
	@Override
	public List<NodeRef> getRecordsByInterval(Date begin, Date end) {
		List<NodeRef> records = new ArrayList<NodeRef>(10);
		final String MIN = begin != null ? DateFormatISO8601.format(begin) : "MIN";
		final String MAX = end != null ? DateFormatISO8601.format(end) : "MAX";
		SearchParameters sp = new SearchParameters();
		sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		sp.setLanguage(SearchService.LANGUAGE_LUCENE);
		sp.setQuery("TYPE:\"" + TYPE_BR_RECORD.toString()  +"\" AND @lecm\\-busjournal\\:bjRecord\\-date:[" + MIN + " TO " + MAX + "]");
		ResultSet results = null;
		try {
			results = searchService.query(sp);
			for (ResultSetRow row : results) {
				NodeRef currentNodeRef = row.getNodeRef();
				if (!isArchive(currentNodeRef)){
					records.add(currentNodeRef);
				}
			}
		} finally {
			if (results != null) {
				results.close();
			}
		}
		return records;
	}
 * */

	/**
	 * формирует список (alfVisibleProps) отбираемых для Jasper-полей (null = все поля)
	 */
	private void prepareFilteredAlfrescoFieldsSet() {
		alfVisibleProps = null; // no filter
		if (conf != null) { // вносим только поля Альфреско (которые отличаются от jr-полей)
			final Map<String, JRXField> meta = conf.getMetaFields(); //
			if (meta != null && !meta.isEmpty()) {
				alfVisibleProps = new HashSet<String>();
				final NamespaceService ns = serviceRegistry.getNamespaceService();
				for (JRXField fld: meta.values()) {
					if (AlfrescoJRDataSource.isCalcField(fld.getValueLink())) { // пропускаем вычисляемые значения ... 
						continue;
					}
					final QName qname = QName.createQName( fld.getValueLink(), ns);
					alfVisibleProps.add( qname.toPrefixString(ns)); // регим короткое название
				}
			}
		}
	}

	public void execQuery() {
		final DurationLogger d = new DurationLogger();

		clearSearch(false);
		prepareFilteredAlfrescoFieldsSet();

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

		alfrescoResult = serviceRegistry.getSearchService().query(search);
		this.foundCount = (alfrescoResult != null) ? alfrescoResult.length() : -1;

		d.logCtrlDuration(logger, String.format( 
				"\nQuery in {t} msec: found %d rows, limit %d, offset %d" +
				"\n>>>%s\n<<<"
				, foundCount, maxItems,  skipCount, queryText));
	}

}
