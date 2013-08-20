package ru.it.lecm.reports.generators;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDataSourceProvider;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JasperReport;

import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.reports.api.AssocDataFilter;
import ru.it.lecm.reports.api.AssocDataFilter.AssocKind;
import ru.it.lecm.reports.api.DataFilter;
import ru.it.lecm.reports.api.ReportsManager;
import ru.it.lecm.reports.api.model.ColumnDescriptor;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.beans.ReportProviderExt;
import ru.it.lecm.reports.beans.WKServiceKeeper;
import ru.it.lecm.reports.jasper.AlfrescoJRDataSource;
import ru.it.lecm.reports.jasper.ReportDSContextImpl;
import ru.it.lecm.reports.jasper.config.JRDSConfigXML;
import ru.it.lecm.reports.jasper.filter.AssocDataFilterImpl;
import ru.it.lecm.reports.jasper.utils.DurationLogger;
import ru.it.lecm.reports.jasper.utils.JRUtils;
import ru.it.lecm.reports.utils.ParameterMapper;
import ru.it.lecm.reports.utils.Utils;
import ru.it.lecm.reports.xml.DSXMLProducer;

/**
 * Провайдер данных.
 * Основное назначение - получение НД для указанного описателя шаблона с учётом 
 * параметров фильтрации.
 * @author rabdullin
 *
 */
public class GenericDSProviderBase
		implements JRDataSourceProvider, ReportProviderExt
{

	private static final Logger logger = LoggerFactory.getLogger(GenericDSProviderBase.class);

	private WKServiceKeeper services;
	private ReportDescriptor reportDescriptor;
	private ReportsManager reportManager;

	/**
	 * Запрос и НД, полученный после запроса к Альфреско
	 */
	protected LucenePreparedQuery alfrescoQuery;
	protected ResultSet alfrescoResult;
	private JRDSConfigXML xmlConfig; // для загрузки конфы из ds-xml

	public WKServiceKeeper getServices() {
		return services;
	}

	@Override
	public void setServices(WKServiceKeeper services) {
		this.services = services;
	}

	public ReportDescriptor getReportDescriptor() {
		return reportDescriptor;
	}

	@Override
	public void setReportDescriptor(ReportDescriptor rdesc) {
		if (Utils.isSafelyEquals(this.reportDescriptor, rdesc))
			return;
		this.reportDescriptor = rdesc;
		reloadConfig();
	}

	/**
	 * Выполнить загрузку конфигурации, если возможно (т.е. присвоены xmlConfig
	 * и reportDescriptor)
	 */
	protected void reloadConfig() {
		if (this.xmlConfig != null && this.reportDescriptor != null)
			// загрузка конфигурации
			this.xmlConfig.setConfigName( DSXMLProducer.makeDsConfigFileName( this.reportDescriptor.getMnem() ));
	}


	@Override
	public void setReportManager(ReportsManager reportMgr) {
		this.reportManager = reportMgr;
	}

	public ReportsManager getReportManager() {
		return reportManager;
	}

	protected void clearSearch() {
		alfrescoResult = null;
		alfrescoQuery = null;
		// foundCount = -1;
	}

	/** value means "no counter limit" for XML_LIMIT and XML_PGSIZE arguments */
	final static int UNLIMITED = -1;

	/**
	 * Стандартное построение запроса согласно параметров this.reportDescriptor.
	 * В классах-потомках может использоваться другая логика параметризации 
	 * и построения отчётов.
	 * Здесь генерируется текст Lucene-запроса с учётомЖ
	 *   1) типа (TYPE), 
	 *   2) ID 
	 *   3) имеющихся простых параметров, 
	 *   4) возможного текста запроса из флагов (reportDescriptor.flags.text).
	 * 
	 * @return
	 */
	protected LucenePreparedQuery buildQuery() {
		return LucenePreparedQuery.prepareQuery(this.reportDescriptor, getServices().getServiceRegistry());
	}

	public JRDSConfigXML conf() {
		if (xmlConfig == null)
			xmlConfig = createXmlConfig();
		return xmlConfig;
	}

	/**
	 * Дополнить конфигурацию значениями по-умолчанию
	 * @param defaults
	 */
	protected void setXMLDefaults(Map<String, Object> defaults) {
		// "add-on" sections для чтения конфигуратором ...
//		defaults.put( DSXMLProducer.XMLNODE_QUERYDESC + "/" + DSXMLProducer.XMLNODE_QUERY_OFFSET, null);
//		defaults.put( DSXMLProducer.XMLNODE_QUERYDESC + "/" + DSXMLProducer.XMLNODE_QUERY_LIMIT, null);
		if (this.xmlConfig != null) {
			if (this.reportDescriptor != null)
				this.xmlConfig.setConfigName( "ds-" + this.reportDescriptor.getMnem()+ ".xml" );
		}
	}

	/**
	 * Вернуть объект конфигуратор
	 * @return
	 */
	protected JRDSConfigXML createXmlConfig() {
		PropertyCheck.mandatory(this, "reportManager", getReportManager());
		return new ConfigXMLOfGenericDsProvider( this.getReportManager());
	}

	private class ConfigXMLOfGenericDsProvider 
			extends JRDSConfigXML
	{
			public ConfigXMLOfGenericDsProvider(ReportsManager mgr) {
				super(mgr);
			}

			@Override
			protected void setDefaults(Map<String, Object> defaults) {
				super.setDefaults(defaults);
				setXMLDefaults( defaults);
			}
	}

	/**
	 * Формирует alfrescoResult согласно запросу полученному от buildQueryText и
	 * параметрам limit/offset.
	 */
	protected ResultSet execQuery() {
		final DurationLogger d = new DurationLogger();

		clearSearch();

		/* формирование запроса: параметры выбираются непосредственно из reportDescriptor */
		this.alfrescoQuery = this.buildQuery();

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Quering Afresco by:>>>\n%s\n<<<", this.alfrescoQuery.luceneQueryText()));
		}

		final SearchParameters search = new SearchParameters();
		search.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		search.setLanguage(SearchService.LANGUAGE_LUCENE);
		search.setQuery(this.alfrescoQuery.luceneQueryText());
		this.alfrescoQuery.setAlfrescoSearch(search);

		int skipCountOffset = -1,
				maxItems = UNLIMITED;

		if (this.reportDescriptor.getFlags() != null) {
			// set offset ...
			skipCountOffset = this.reportDescriptor.getFlags().getOffset();
			// set limit ...
			maxItems = this.reportDescriptor.getFlags().getLimit();
		}
		if (skipCountOffset > 0) {
			this.alfrescoQuery.alfrescoSearch().setSkipCount(skipCountOffset);
		}
		if (maxItems != UNLIMITED) {
			this.alfrescoQuery.alfrescoSearch().setMaxItems(maxItems);
		}

		/* (!) момент истины - выполнение ЗАПРОСА */
		ResultSet rs = null;
		if (!Utils.isStringEmpty(this.alfrescoQuery.luceneQueryText())) {
			rs = getServices().getServiceRegistry().getSearchService().query(this.alfrescoQuery.alfrescoSearch());
		}

		final int foundCount = (rs != null) ? rs.length() : -1;
		d.logCtrlDuration(logger, String.format(
				"\nQuery in {t} msec: found %d rows, limit %d, offset %d" +
						"\n>>>%s\n<<<"
						, foundCount, maxItems, skipCountOffset, this.alfrescoQuery.luceneQueryText()));

		return rs;
	}

	@Override
	public boolean supportsGetFieldsOperation() {
		return true;
	}

	@Override
	public JRField[] getFields(JasperReport report)
			throws JRException, UnsupportedOperationException
	{
		final List<JRField> result = JRUtils.getJRFields(this.getReportDescriptor());
		return (result != null) ? result.toArray( new JRField[result.size()]) : null;
	}

	@Override
	public void dispose(JRDataSource ds) throws JRException {
		logger.debug( String.format("Disposing dataSource: %s", (ds == null ? "null" : ds.getClass().getName()) ));
	}

	@Override
	public JRDataSource create(JasperReport report) throws JRException {
		if (alfrescoResult == null) { // выполнение запроса ...
			alfrescoResult = execQuery();
			if (alfrescoResult == null) {
				return null;
			}
		}

		// Create a new data source
		final AlfrescoJRDataSource dataSource = newJRDataSource(alfrescoResult.iterator());
		fillContext(dataSource.getContext());

		return dataSource;
	}

	/**
	 * Заполнение контекста используемыми службами, описанием полей.
	 * @param context
	 */
	protected void fillContext(ReportDSContextImpl context) {
		if (context != null) {
			context.setSubstitudeService(getServices().getSubstitudeService());
			context.setRegistryService(getServices().getServiceRegistry());
			context.setJrSimpleProps( getColumnNames(this.alfrescoQuery.argsByProps(), this.getServices().getServiceRegistry().getNamespaceService()));
			context.setMetaFields(JRUtils.getDataFields(this.getReportDescriptor()));

			// фильтр данных ...
			context.setFilter(newDataFilter());
		}
	}

	/**
	 * Получить список имён простых колонок в виде последовательности пар "тип", "атрибут" (QName Альфреско).
	 * @param list список колонок, в которых выражение является ссылкой на атрибут
	 * @param ns
	 * @return
	 */
	static Set<String> getColumnNames(List<ColumnDescriptor> list, final NamespaceService ns) {
		if (list == null || list.isEmpty())
			return null;
		final Set<String> result = new HashSet<String>();
		for (ColumnDescriptor col : list) {
			final QName qname = QName.createQName(col.getQNamedExpression(), ns);
			if (qname != null) {
				result.add(qname.toPrefixString(ns)); // (!) регим короткое название
				result.add(col.getColumnName());
			}
		}
		return result;
	}

	/**
	 * Внутренний метод для создания нужного набора данных.
	 * В потомках позволит менять конретный тип НД.
	 * @param iterator
	 * @return
	 */
	protected AlfrescoJRDataSource newJRDataSource(Iterator<ResultSetRow> iterator) {
		return new AlfrescoJRDataSource(iterator);
	}

	/**
	 * Внутренний метод для создания фильтра данных.
	 * Здесь включает такой фильтр, в котором есть отбор по параметрам-ассоциациям (cм this.alfrescoQuery.argsByLinks()).
	 * В потомках позволит менять конретный тип фильтра.
	 * @return
	 */
	protected DataFilter newDataFilter() {
		// фильтр, который может "заглядывать" по ссылкам
		if (this.alfrescoQuery.argsByLinks() == null || this.alfrescoQuery.argsByLinks().isEmpty()) {
			return null;
		}

		// TODO: надо разработать фильтр, который смог бы проверять длинные ссылки (DataFilterByLinks)
		final AssocDataFilterImpl result = new AssocDataFilterImpl(this.getServices().getServiceRegistry());

		final NamespaceService ns = this.getServices().getServiceRegistry().getNamespaceService();
		final DictionaryService ds = this.getServices().getServiceRegistry().getDictionaryService();

		boolean useFilter = false;
		for (ColumnDescriptor colDesc : this.alfrescoQuery.argsByLinks()) {
			/*
			 * Example:
				final QName qnCSubject = QName.createQName( "lecm-doc-dic:subject-code", ns); // Тематика договора, "lecm-contract:subjectContract-assoc"
				final QName qnAssocCSubject = QName.createQName( "lecm-contract:subjectContract-assoc", ns);
				result.addAssoc( qnCSubject, qnAssocCSubject, contractSubject, AssocKind.target);
			 */
			try {
				QName targetType = null;
				String expression = colDesc.getExpression();
				if (expression.startsWith(SubstitudeBean.OPEN_SUBSTITUDE_SYMBOL) && expression.endsWith(SubstitudeBean.CLOSE_SUBSTITUDE_SYMBOL)) {
					if (!expression.contains(SubstitudeBean.SPLIT_TRANSITIONS_SYMBOL)) {
						// TODO добавить обработку parent и source ассоциаций, согласно правилам substitudeService
						expression = expression.replace(SubstitudeBean.OPEN_SUBSTITUDE_SYMBOL, "").replace(SubstitudeBean.CLOSE_SUBSTITUDE_SYMBOL, "");
						final QName qnAssocType = QName.createQName(expression, ns);
						final AssociationDefinition assocDef =  ds.getAssociation(qnAssocType);
						if (assocDef != null) {
							final List<NodeRef> idsTarget = ParameterMapper.getArgAsNodeRef(colDesc);
							if (!idsTarget.isEmpty()) {
								targetType = assocDef.getTargetClass().getName();
								final AssocKind kind = (assocDef.isChild()) ? AssocKind.child : AssocKind.target;
								useFilter = true;
								result.addAssoc(new AssocDataFilter.AssocDesc(kind, qnAssocType, targetType, idsTarget));
							}
						}
					} else {
						// TODO добавить обратку сложных ссылок
					}
				}
			} catch (Exception ignored) {
				logger.warn("Ignoring error at process parameteres:\n", ignored);
			}
		}

		return (useFilter) ? result : null;
	}

}
