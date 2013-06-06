package ru.it.lecm.reports.jasper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.reports.api.AssocDataFilter.AssocKind;
import ru.it.lecm.reports.jasper.filter.AssocDataFilterImpl;
import ru.it.lecm.reports.jasper.utils.Utils;

/**
 * Провайдер для построения отчёта "Сроки прохождения маршрута"
 * 
 * Фильтры по:
 *   •	Вид договора
 *   •	Тематика договора
 *   •	Контрагент
 *   •	Дата регистрации проекта (Период)
 *
 * Измерение (одно из):
 *   •	Вид договора
 *   •	Тематика договора
 *   •	Контрагент
 *   •	Инициатор
 * 
 * @author rabdullin
 */
public class DSProviderDocflowStatusTimings extends
		DSProviderSearchQueryReportBase {

	// см как основной ru.it.lecm.reports.jasper.DSProviderReestrDogovorov


	private static final Logger logger = LoggerFactory.getLogger(DSProviderDocflowStatusTimings.class);

	final static String TYPE_CONRACT = "lecm-contract:document";
	final static String XMLSTATUSES = "statuses"; 
	final static String XMLGROUPBY_FORMATS = "groupby.formats";

	/**
	 * Формат названия колонки со счётчиками
	 */
	final static String COLNAME_TAG = "col_RowTag";
	final static String COLNAME_COUNTER_FMT = "col_Count%d";

	final static String ATTR_STATUS = "lecm-statemachine:status";

	/**
	 * Вариант групировки. 
	 */
	private String groupBy;

	public DSProviderDocflowStatusTimings() {
		super();
		super.setPreferedType(TYPE_CONRACT);
	}


	/**
	 * Выбор вараинта "Измерения"
	 * Реальные значения см. "ds-docflow-timings.xml" (т.е. в мета-конфигурации для jrxml-отчёта)
	 * Пример: "Вид договора", "Тематика договора" ...
	 * @return
	 */
	public String getGroupBy() {
		return groupBy;
	}


	/**
	 * Задание варианта "Измерения"
	 * Реальные значения см. "ds-docflow-timings.xml" (т.е. в мета-конфигурации для jrxml-отчёта)
	 * Пример: "Вид договора", "Тематика договора" ...
	 * @groupBy
	 */
	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
	}


	@Override
	protected void setXMLDefaults(Map<String, Object> defaults) {
		super.setXMLDefaults(defaults);
		defaults.put( XMLSTATUSES, null); // list
		defaults.put( XMLGROUPBY_FORMATS, null); // map
	}

	/**
	 * Фильтр поиска для отчёта по срокам прохождения маршрута (по статусам):
	 * 1) "contractType" - Вид договора
	 *       association name: "lecm-contract:typeContract-assoc"
	 *           target: lecm-contract-dic:contract-type
	 * 2) "contractSubject" - Тематика договора
	 *       association name: "lecm-contract:subjectContract-assoc"
	 *           target: lecm-contract-dic:contract-subjects
	 * 3) "contractContractor" - Контрагент
	 *       association name: "lecm-contract:partner-assoc"
	 *           target: lecm-contractor:contractor-type
	 * 4) "regAfter" и "regBefore"- Дата регистрации проекта (Период/Интервал)
	 * fmt like "2013-04-30T00:00:00.000+06:00"
	 *       "lecm-contract:dateRegProjectContracts"
	 * 
	 */
	class SearchDoclowFilter  {

		Date dateRegAfter, dateRegBefore;
		// Boolean contractActualOnly;
		NodeRef contractType, contractSubject, contragent;

		public void clear() {
			dateRegAfter = dateRegBefore = null;
			contractSubject = contractType = contragent = null;
			// contractActualOnly = null;
		}

		/**
		 * Создать фильтр по ассоциациям.
		 * (!) По времени, интервалу регистации, отбор выполнен в основном запросе.
		 * @return
		 */
		public AssocDataFilterImpl makeAssocFilter() {

			final boolean hasSubject = (contractSubject != null);
			final boolean hasType = (contractType != null);
			final boolean hasCAgent = (contragent != null);
			final boolean hasAny = hasSubject || hasType || hasCAgent;
			if (!hasAny) // в фильтре ничего не задачно -> любые данные подойдут
				return null;

			final AssocDataFilterImpl result = new AssocDataFilterImpl( serviceRegistry);

			final NamespaceService ns = serviceRegistry.getNamespaceService();

			if (hasType) {
				final QName qnCType = QName.createQName( "lecm-contract-dic:contract-type", ns); // Вид договора 
				final QName qnAssocCType = QName.createQName( "lecm-contract:typeContract-assoc", ns);
				result.addAssoc( qnCType, qnAssocCType, contractType, AssocKind.target);
			}

			if (hasSubject) {
				final QName qnCSubject = QName.createQName( "lecm-contract-dic:contract-subjects", ns); // Тематика договора, "lecm-contract:subjectContract-assoc"
				final QName qnAssocCSubject = QName.createQName( "lecm-contract:subjectContract-assoc", ns);
				result.addAssoc( qnCSubject, qnAssocCSubject, contractSubject, AssocKind.target);
			}

			if (hasCAgent) {
				final QName qnCAgent = QName.createQName( "lecm-contractor:contractor-type", ns); // Контрагенты, "lecm-contract:partner-assoc"
				final QName qnAssocCAgent = QName.createQName( "lecm-contract:partner-assoc", ns);
				result.addAssoc( qnCAgent, qnAssocCAgent, contragent, AssocKind.target);
			}

			return result;
		}

	}

	private final SearchDoclowFilter filter = new SearchDoclowFilter();

	public void setDateRegAfter( final String value) {
		filter.dateRegAfter = ArgsHelper.makeDate(value, "dateRegAfter");
	}

	public void setDateRegBefore( final String value) {
		filter.dateRegBefore = ArgsHelper.makeDate(value, "dateStartBefore");
	}

	public void setContractSubject(String value) {
		filter.contractSubject = ArgsHelper.makeNodeRef(value, "contractSubject");
	}

	public void setContractType(String value) {
		filter.contractType = ArgsHelper.makeNodeRef(value, "contractType");
	}

	public void setContragent(String value) {
		filter.contragent = ArgsHelper.makeNodeRef(value, "contragent");
	}

//	//"contractActualOnly" - только актуальные
//	public void setContractActualOnly(String value) {
//		filter.contractActualOnly = Utils.isStringEmpty(value) ? null : Boolean.parseBoolean(value);
//	}

	/**
	 * Построить Lucene-запрос по данным фильтра.
	 * Example: 
	 *    TYPE:"{http://www.it.ru/logicECM/contract/1.0}document" AND  @lecm\-contract\:totalSum:(0 TO 23450000)   AND @lecm\-contract\:endDate:[ NOW/DAY TO *]
	 * @return
	 */
	@Override
	protected String buildQueryText() {
		final StringBuilder bquery = new StringBuilder();

		bquery.append( super.buildQueryText() ); // "TYPE:\"{http://www.it.ru/logicECM/contract/1.0}document\""

		// начало .. конец
		// начало == <!-- Дата регистрации проекта -->
		{
			final String cond = Utils.emmitDateIntervalCheck("lecm\\-contract\\:dateRegProjectContracts", filter.dateRegAfter, filter.dateRegBefore);
			if (cond != null)
				bquery.append( " AND "+ cond);
		}

//		// Контракт актуален: если ещё не истёк срок 
//		if ( Boolean.TRUE.equals(filter.contractActualOnly)) {
//			bquery.append( " AND (@lecm\\-contract\\:unlimited:true OR @lecm\\-contract\\:endDate:[NOW TO MAX])"); // неограниченый или "истекает позже чем сейчас"
//		}

		// NOTE: отбор по ассоциациям происходит на уровне фильтров

		return bquery.toString();
	}


	final static String JRFLD_Executor_Staff = "col_Executor_Staff";

	@Override
	protected AlfrescoJRDataSource newJRDataSource(Iterator<ResultSetRow> iterator) {
		final DocflowJRDataSource result = new DocflowJRDataSource(iterator);
		result.context.setSubstitudeService(substitudeService);
		result.context.setRegistryService(serviceRegistry);
		result.context.setJrSimpleProps(jrSimpleProps);
		result.context.setMetaFields(conf().getMetaFields());
		if (filter != null)
			result.context.setFilter(filter.makeAssocFilter());
		result.buildJoin();
		return result;
	}

	/**
	 * Контейнерный класс
	 * @author rabdullin
	 */
	private class DocStatusGroups {
		/**
		 * Тэг данной группы
		 */
		final String groupTag;

		/**
		 * Счётчики связанные со статусами.
		 */
		final Map<String, Integer> statusCounters = new HashMap<String, Integer>();


		public DocStatusGroups(String groupTag) {
			super();
			this.groupTag = groupTag;
		}

		@Override
		public String toString() {
			return "[group '" + groupTag+ "' (" + statusCounters + ")]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((groupTag == null) ? 0 : groupTag.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final DocStatusGroups other = (DocStatusGroups) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (groupTag == null) {
				if (other.groupTag != null)
					return false;
			} else if (!groupTag.equals(other.groupTag))
				return false;
			return true;
		}

		private DSProviderDocflowStatusTimings getOuterType() {
			return DSProviderDocflowStatusTimings.this;
		}

		/**
		 * Увеличить счётчик связанный с указанным статусом.
		 * Воз-ся увеличенный счётчик.
		 * @param statusName
		 * @return
		 */
		public int incCounter(final String statusName) {
			Integer counter = null;
			if (statusCounters.containsKey(statusName))
				counter = statusCounters.get(statusName);
			final int result = (counter == null) ? 1 : counter.intValue() + 1;
			statusCounters.put(statusName, result);
			return result;
		}

		/**
		 * Вычислить суммы всех статусов, которые НЕ перечислены в указанном списке
		 * @param statusMarked
		 * @return
		 */
		public Integer sumAllOthers(Collection<String> statusMarked) {
			int result = 0;
			for(Map.Entry<String, Integer> e: this.statusCounters.entrySet()) {
				if (!statusMarked.contains(e.getKey())) { // учесть ...
					if (e.getValue() != null)
						result += e.getValue().intValue(); 
				}
			}
			return (result != 0) ? result : null;
		}
	}

	private class DocflowJRDataSource extends TypedJoinDS<DocStatusGroups> {

		/**
		 * Ключ здесь это название измерения (tag)
		 */
		final private Map<String, DocStatusGroups> groups = new LinkedHashMap<String, DocStatusGroups>();

		public DocflowJRDataSource(Iterator<ResultSetRow> iterator) {
			super(iterator);
		}

		/**
		 * Увеличить счётчик связанный с указанным именованным объектом и статусом.
		 * Воз-ся увеличенный счётчик.
		 * @param tag название объекта-измерения
		 * @param statusName
		 * @return
		 */
		private DocStatusGroups incCounter(final String tag, final String statusName) {
			final DocStatusGroups result;
			if (groups.containsKey(tag)) {
				result = groups.get( tag);
			} else { // создаём новую
				groups.put( tag, result = new DocStatusGroups(tag));
			}
			result.incCounter(statusName);
			return result;
		}

		@Override
		public int buildJoin() {
			// TODO: построить  в groups список объектов сгруппированных по названиям Измерения (ключ в groups)

			final ArrayList<DocStatusGroups> result = new ArrayList<DocStatusGroups>();

			/* Получение формата строки для выбранного Измерения из конфигурации ... */
			final String fmtForTag;
			{
				if (groupBy == null)
					throw new RuntimeException( "Too few parameters: 'groupBy' not assigned");

				final Map<String, Object> mapGroupBy = conf().getMap(XMLGROUPBY_FORMATS);
				if (mapGroupBy == null)
					throw new RuntimeException( String.format("Invalid configuration: no '%s' map provided", XMLGROUPBY_FORMATS));
				if (!mapGroupBy.containsKey(groupBy))
					throw new RuntimeException( String.format("Invalid configuration: provided map '%s' not contains variant for demanded report order '%s'", XMLGROUPBY_FORMATS, groupBy));
				fmtForTag = (String) mapGroupBy.get(groupBy);
			}

			if (context.getRsIter() != null) {

				final NodeService nodeSrv = serviceRegistry.getNodeService();
//				final NamespaceService ns = serviceRegistry.getNamespaceService();
//				final ApproveQNameHelper approveQNames = new ApproveQNameHelper(ns);

				final QName status = QName.createQName( ATTR_STATUS, serviceRegistry.getNamespaceService());

				while(context.getRsIter().hasNext()) {
					final ResultSetRow rs = context.getRsIter().next();

					final NodeRef docId = rs.getNodeRef(); // id документа
					if (context.getFilter() != null && !context.getFilter().isOk(docId)) {
						logger.debug( String.format("Filtered out node %s", docId));
						continue; // skip data row
					}

					/* Название ... */
					final String docTag = substitudeService.formatNodeTitle(docId, fmtForTag);

					/* Статус */
					final String statusName = (String) nodeSrv.getProperty(docId, status);

					incCounter(docTag, statusName);

				} // while
			}

			result.addAll(this.groups.values());

			setData(result);
			setIterData( result.iterator());

			logger.info( String.format( "found %s row(s)", result.size()));

			return result.size();
		}

		@Override
		protected Map<String, Serializable> getReportContextProps(DocStatusGroups item)
		{
			final Map<String, Serializable> result = new LinkedHashMap<String, Serializable>();

			/* Название ... */
			result.put( COLNAME_TAG, item.groupTag);

			/* Счётчики ... */
			final List<String> statusOrdered = conf().getList(XMLSTATUSES);

			int iCol = 0;
			for( String colName: statusOrdered) {
				iCol++; // (!) нумерация от Единицы
				// "col_CountNN" = (Integer) счётчик в этом статусе
				result.put( String.format( COLNAME_COUNTER_FMT, iCol), item.statusCounters.get(colName));
				// TODO: TEMP DEBUG ONLY
				// final int iRow = super.getData().indexOf(item);
				// result.put( String.format( COLNAME_COUNTER_FMT, iCol), iCol + iRow * 100);
			}

			// последняя колонка будет состоять из всех значений, не вошедших в какие-либо предыдущие ... 
			iCol++;
			result.put( String.format( COLNAME_COUNTER_FMT, iCol), item.sumAllOthers(statusOrdered));

			return result;
		}

	}
}
