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

import ru.it.lecm.reports.api.AssocDataFilter.AssocDesc;
import ru.it.lecm.reports.api.AssocDataFilter.AssocKind;
import ru.it.lecm.reports.jasper.filter.AssocDataFilterImpl;
import ru.it.lecm.reports.jasper.utils.ArgsHelper;
import ru.it.lecm.reports.utils.Utils;

/**
 * Провайдер для построения отчёта «Сводный отчет по договорам»
 * 
 * Фильтры по:
 *   •	Вид договора
 *   •	Тематика договора
 *   •	Контрагент
 *   •	Дата регистрации проекта (Период)
 *   •	Дата договора  (задавать Период)
 *   •	Сумма (интервал)
 *   •	Инициатор
 *
 * Измерение (одно из):
 *   •	Вид договора
 *   •	Тематика договора
 *   •	Контрагент
 *   •	Инициатор
 * 
 * @author rabdullin
 */
public class DSProviderDocflowStatusCounters extends DSProviderSearchQueryReportBase {

	private static final Logger logger = LoggerFactory.getLogger(DSProviderDocflowStatusCounters.class);

	final static String TYPE_CONRACT = "lecm-contract:document";

	/* XML параметры */
	final static String XMLSTATUSES_LIST = "statuses";
	final static String XMLSTATUS_FLAGS_MAP = "status.flags";
	final static String XMLGROUPBY_FORMATS_MAP = "groupby.formats";

	/** надо ли формировать столбец с суммой по строке */
	final static String XMLSTATUS_FLAGS_ITEM_ROWSUM_SHOW = "rowSum.show";
	/** название столбца содержащего сумму по строке */
	final static String XMLSTATUS_FLAGS_ITEM_ROWSUM_COLNAME = "rowSum.colName";

	/** название атрибута со статусом */
	final static String XMLSTATUS_PROPERTY = "doc.statusProperty";

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

	private final SearchDoclowFilter filter = new SearchDoclowFilter();


	public DSProviderDocflowStatusCounters() {
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
		defaults.put( XMLSTATUSES_LIST, null); // list
		defaults.put( XMLGROUPBY_FORMATS_MAP, null); // map
		defaults.put( XMLSTATUS_FLAGS_MAP, null); // map
	}


	/**
	 * Фильтр поиска для отчёта по срокам прохождения маршрута (по статусам):
	 * 1) "contractType" - Вид договора
	 *       association name: "lecm-contract:typeContract-assoc"
	 *           target: lecm-contract-dic:contract-type
	 * 2) "contractSubject" - Тематика договора
	 *       association name: "lecm-contract:subjectContract-assoc"
	 *           target: lecm-doc-dic:subjects
	 * 3) "contractContractor" - Контрагент
	 *       association name: "lecm-contract:partner-assoc"
	 *           target: lecm-contractor:contractor-type
	 * 4) "regAfter" и "regBefore"- Дата регистрации проекта (Период/Интервал)
	 * fmt like "2013-04-30T00:00:00.000+06:00"
	 *       "lecm-contract:dateRegProjectContracts"
	 * 
	 */
	protected class SearchDoclowFilter  {

		// Boolean contractActualOnly;
		Date dateRegAfter, dateRegBefore; // дата регистрации проекта договора
		Date dateContractStartAfter, dateContractStartBefore; // дата начала действия договора
		Double contractSumLow, contractSumHi; // сумма договора (нижняя и верхняя границы)

		/**
		 *  (!) Инициатор (автор) хранится как "cm:creator" : text, и отфильтровывается в основном запросе.
		 *  Остальные являются настоящими ассоциацими и фильтруются в результатах основного запроса.
		 */
		NodeRef contractType, contractSubject, author;
		List<NodeRef> contragents;

		public void clear() {
			dateRegAfter = dateRegBefore = dateContractStartAfter = dateContractStartBefore = null;
			contractSubject = contractType = null;
			contragents = null;
			contractSumLow = contractSumHi = null;
			// contractActualOnly = null;
		}

		public void setContragents(List<NodeRef> list) {
			this.contragents = (list == null || list.isEmpty()) ? null : list; 
		}

		/**
		 * Создать фильтр по ассоциациям.
		 * (!) По времени, интервалу регистации, отбор выполнен в основном запросе.
		 * @return
		 */
		public AssocDataFilterImpl makeAssocFilter() {

			final boolean hasSubject = (contractSubject != null);
			final boolean hasType = (contractType != null);
			final boolean hasCAgents = (contragents != null);
			final boolean hasAny = hasSubject || hasType || hasCAgents;
			if (!hasAny) // в фильтре ничего не задачно -> любые данные подойдут
				return null;

			final AssocDataFilterImpl result = new AssocDataFilterImpl( getServices().getServiceRegistry());

			final NamespaceService ns = getServices().getServiceRegistry().getNamespaceService();

			if (hasType) {
				final QName qnCType = QName.createQName( "lecm-contract-dic:contract-type", ns); // Вид договора 
				final QName qnAssocCType = QName.createQName( "lecm-contract:typeContract-assoc", ns);
				result.addAssoc( AssocKind.target, qnAssocCType, qnCType, contractType);
			}

			if (hasSubject) {
				final QName qnCSubject = QName.createQName( "lecm-doc-dic:subjects", ns); // Тематика договора, "lecm-contract:subjectContract-assoc"
				final QName qnAssocCSubject = QName.createQName( "lecm-contract:subjectContract-assoc", ns);
				result.addAssoc( AssocKind.target, qnAssocCSubject, qnCSubject, contractSubject);
			}

			if (hasCAgents) {
				final QName qnCAgent = QName.createQName( "lecm-contractor:contractor-type", ns); // Контрагенты, "lecm-contract:partner-assoc"
				final QName qnAssocCAgent = QName.createQName( "lecm-contract:partner-assoc", ns);
				result.addAssoc( new AssocDesc(AssocKind.target, qnAssocCAgent, qnCAgent, contragents));
			}

			return result;
		}

	}


//	//"contractActualOnly" - только актуальные
//	public void setContractActualOnly(String value) {
//		filter.contractActualOnly = Utils.isStringEmpty(value) ? null : Boolean.parseBoolean(value);
//	}

	/**
	 * Нижняя граница Даты регистрации договора
	 * @param value
	 */
	public void setDateRegAfter( final String value) {
		filter.dateRegAfter = ArgsHelper.makeDate(value, "dateRegAfter");
	}

	/**
	 * Верхняя граница Даты регистрации договора
	 * @param value
	 */
	public void setDateRegBefore( final String value) {
		filter.dateRegBefore = ArgsHelper.makeDate(value, "dateStartBefore");
	}

	/**
	 * Нижняя граница Даты начала действия договора
	 * @param value
	 */
	public void setDateContractStartAfter( final String value) {
		filter.dateContractStartAfter = ArgsHelper.makeDate(value, "dateContractStartAfter");
	}

	/**
	 * Верхняя граница Даты начала действия договора
	 * @param value
	 */
	public void setDateContractStartBefore( final String value) {
		filter.dateContractStartBefore = ArgsHelper.makeDate(value, "dateContractStartBefore");
	}

	public void setContractSubject(String value) {
		filter.contractSubject = ArgsHelper.makeNodeRef(value, "contractSubject");
	}

	public void setContractType(String value) {
		filter.contractType = ArgsHelper.makeNodeRef(value, "contractType");
	}

	public void setContragent(String value) {
		filter.setContragents( ArgsHelper.makeNodeRefs(value, "contragents"));
	}

	/**
	 * Нижняяя граница для суммы договора.
	 * @param value
	 */
	public void setContractSumLow(String value) {
		try {
			filter.contractSumLow = Utils.isStringEmpty(value) ? null : Double.parseDouble(value);
			if (filter.contractSumLow != null && filter.contractSumLow == 0) // значение ноль эквивалентно NULL
				filter.contractSumLow = null;
		} catch(Throwable e) {
			logger.error( String.format( "unexpected double value '%s' for contractSumLow -> ignored as NULL", value), e);
			filter.contractSumLow = null;
		}
	}

	/**
	 * Верхняя граница для суммы договора.
	 * @param value
	 */
	public void setContractSumHi(String value) {
		try {
			filter.contractSumHi = Utils.isStringEmpty(value) ? null : Double.parseDouble(value);
			if (filter.contractSumHi != null && filter.contractSumHi == 0) // значение ноль эквивалентно NULL
				filter.contractSumHi = null;
		} catch(Throwable e) {
			logger.error( String.format( "unexpected double value '%s' for contractSumHi -> ignored as NULL", value), e);
			filter.contractSumHi = null;
		}
	}

	/**
	 * Задать автора (инициатора) договора.
	 * @param value
	 */
	public void setAuthor(String value) {
		filter.author = ArgsHelper.makeNodeRef(value, "author");
	}

	/**
	 * Построить Lucene-запрос по данным фильтра.
	 * Example: 
	 *    TYPE:"{http://www.it.ru/logicECM/contract/1.0}document" AND  @lecm\-contract\:totalSum:(0 TO 23450000)   AND @lecm\-contract\:endDate:[ NOW/DAY TO *]
	 * @return
	 */
	// NOTE: отбор по ассоциациям происходит на уровне фильтров
	@Override
	protected String buildQueryText() {
		final StringBuilder bquery = new StringBuilder();

		bquery.append( super.buildQueryText() ); // "TYPE:\"{http://www.it.ru/logicECM/contract/1.0}document\""

//		// Контракт актуален: если ещё не истёк срок 
//		if ( Boolean.TRUE.equals(filter.contractActualOnly)) {
//			bquery.append( " AND (@lecm\\-contract\\:unlimited:true OR @lecm\\-contract\\:endDate:[NOW TO MAX])"); // неограниченый или "истекает позже чем сейчас"
//		}

		// начало .. конец <!-- Дата регистрации проекта -->
		{
			final String cond = Utils.emmitDateIntervalCheck("lecm\\-contract\\:dateRegProjectContracts", filter.dateRegAfter, filter.dateRegBefore);
			if (cond != null)
				bquery.append( " AND "+ cond);
		}

		// начало .. конец <!-- Дата начала действия договора -->
		{
			final String cond = Utils.emmitDateIntervalCheck("lecm\\-contract\\:startDate", filter.dateContractStartAfter, filter.dateContractStartBefore);
			if (cond != null)
				bquery.append( " AND "+ cond);
		}

		// Сумма договора (указан диапазон)
		{
			// bquery.append( " AND @lecm\\-contract\\:totalAmount:(" + filter.contractSumLow.toString() + " TO *)");
			final String cond = Utils.emmitNumericIntervalCheck("lecm\\-contract\\:totalAmount", filter.contractSumLow, filter.contractSumHi);
			if (cond != null)
				bquery.append( " AND "+ cond);
		}

		/*
		 *  Инициатор у нас хранится как текст с Login пользователя, так что это
		 *  можно фильтрануть прямо тут
		 */
		if (filter.author != null) {
			// cm:creator
			final String login = getServices().getOrgstructureService().getEmployeeLogin(filter.author);
			bquery.append( " AND @cm\\:creator:\"" + login + "\"");
		}

		return bquery.toString();
	}

	@Override
	protected AlfrescoJRDataSource newJRDataSource(Iterator<ResultSetRow> iterator) {
		final DocflowJRDataSource result = new DocflowJRDataSource(iterator);
		result.context.setSubstitudeService(getServices().getSubstitudeService());
		result.context.setRegistryService(getServices().getServiceRegistry());
		result.context.setJrSimpleProps(jrSimpleProps);
		result.context.setMetaFields(conf().getMetaFields());
		if (filter != null)
			result.context.setFilter(filter.makeAssocFilter());
		result.buildJoin();
		return result;
	}


	final static String JRFLD_Executor_Staff = "col_Executor_Staff";

	/**
	 * Контейнерный класс
	 * @author rabdullin
	 */
	protected class DocStatusGroup {
		/**
		 * Тэг данной группы
		 */
		final String groupTag;

		/**
		 * Счётчики связанные со статусами.
		 */
		final Map<String, Integer> statusCounters = new HashMap<String, Integer>();

		/**
		 * Общая сумма значений
		 */
		private Integer total; 

		public DocStatusGroup(String groupTag) {
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
			final DocStatusGroup other = (DocStatusGroup) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (groupTag == null) {
				if (other.groupTag != null)
					return false;
			} else if (!groupTag.equals(other.groupTag))
				return false;
			return true;
		}

		private DSProviderDocflowStatusCounters getOuterType() {
			return DSProviderDocflowStatusCounters.this;
		}

		/**
		 * Увеличить счётчик связанный с указанным статусом.
		 * Воз-ся увеличенный счётчик.
		 * @param statusName
		 * @return
		 */
		public int incCounter(final String statusName, int delta) {
			Integer counter = null;
			total = delta + ((total == null) ? 0 : total.intValue());
			if (statusCounters.containsKey(statusName))
				counter = statusCounters.get(statusName);
			final int result = delta + ( (counter == null) ? 0 : counter.intValue() );
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

		public Integer sumAll() {
			return total;
		}
	}

	/**
	 * Вычисление статистики для указанной группы (это может быть увеличение 
	 * кол-ва или суммарной длительности, в зависимости от отчёта)
	 * @param group наборр статусов, в котором надо вычсилить статистику
	 * @param statusName статус для модификации статистики
	 * @param docId документ для которого надо модифицировать статистику
	 * @return изменённое значение счётчика, соот-го статусу statusName
	 */
	protected int adjustStatistic( final DocStatusGroup group, final String statusName, NodeRef docId)
	{
		return group.incCounter(statusName, 1); // простой счётчик
	}

	/**
	 * Базовый класс для вычисления статистики
	 * @author rabdullin
	 */
	private class DocflowJRDataSource extends TypedJoinDS<DocStatusGroup> {

		/**
		 * Ключ здесь это название измерения (tag)
		 */
		final protected Map<String, DocStatusGroup> groups = new LinkedHashMap<String, DocStatusGroup>();

		public DocflowJRDataSource(Iterator<ResultSetRow> iterator) {
			super(iterator);
		}

		/**
		 * Увеличить счётчик связанный с указанным именованным объектом и статусом
		 * (сущность счётчика зависит от целевого отчёта - может быть кол-во или 
		 * длительность, или что-то иное)
		 * Воз-ся увеличенный счётчик.
		 * @param tag название объекта-измерения (по нему ищется в groups соот-вие) 
		 * @param statusName статус для модификации статистики
		 * @param statusName
		 * @param docId документ для которого надо модифицировать статистику
		 * @return
		 */
		private DocStatusGroup incCounter(final String tag, final String statusName, final NodeRef docId) {
			final DocStatusGroup result;
			if (groups.containsKey(tag)) {
				result = groups.get( tag);
			} else { // создаём новую
				groups.put( tag, result = new DocStatusGroup(tag));
			}
			adjustStatistic( result, statusName, docId);
			return result;
		}


		@Override
		public int buildJoin() {
			// TODO: построить  в groups список объектов сгруппированных по названиям Измерения (ключ в groups)

			final ArrayList<DocStatusGroup> result = new ArrayList<DocStatusGroup>();

			/* Получение формата строки для выбранного Измерения из конфигурации ... */
			final String fmtForTag;
			{
				if (groupBy == null)
					throw new RuntimeException( "Too few parameters: 'groupBy' not assigned");

				final Map<String, Object> mapGroupBy = conf().getMap(XMLGROUPBY_FORMATS_MAP);
				if (mapGroupBy == null)
					throw new RuntimeException( String.format("Invalid configuration: no '%s' map provided", XMLGROUPBY_FORMATS_MAP));
				if (!mapGroupBy.containsKey(groupBy))
					throw new RuntimeException( String.format("Invalid configuration: provided map '%s' not contains variant for demanded report order '%s'", XMLGROUPBY_FORMATS_MAP, groupBy));
				fmtForTag = (String) mapGroupBy.get(groupBy);
			}

			if (context.getRsIter() != null) {

				final NodeService nodeSrv = getServices().getServiceRegistry().getNodeService();
//				final NamespaceService ns = serviceRegistry.getNamespaceService();
//				final ApproveQNameHelper approveQNames = new ApproveQNameHelper(ns);

				final QName status = QName.createQName( getStatusAttrName(), getServices().getServiceRegistry().getNamespaceService());

				while(context.getRsIter().hasNext()) {
					final ResultSetRow rs = context.getRsIter().next();

					final NodeRef docId = rs.getNodeRef(); // id документа
					if (context.getFilter() != null && !context.getFilter().isOk(docId)) {
						logger.debug( String.format("Filtered out node %s", docId));
						continue; // skip data row
					}

					/* Название ... */
					final String docTag = getServices().getSubstitudeService().formatNodeTitle(docId, fmtForTag);

					/* Статус */
					final String statusName = (String) nodeSrv.getProperty(docId, status);

					incCounter(docTag, statusName, docId);

				} // while
			}

			result.addAll(this.groups.values());

			setData(result);
			setIterData( result.iterator());

			logger.info( String.format( "found %s row(s)", result.size()));

			return result.size();
		}

		/**
		 * Вернуть название атрибута со статусом.
		 * Если имеется непустой конфигурационный параметр [(Map)"status.flags"]["doc.statusProperty"]
		 * то используется он, иначе используется const ATTR_STATUS.
		 * @return
		 */
		private String getStatusAttrName() {
			String result = null;
			final Map<String, Object> mapStatusFlags =  conf().getMap(XMLSTATUS_FLAGS_MAP);
			if (mapStatusFlags != null && mapStatusFlags.containsKey(XMLSTATUS_PROPERTY)) {
				result = Utils.coalesce( mapStatusFlags.get(XMLSTATUS_PROPERTY), ATTR_STATUS).trim();
			}
			return ( (result != null) && result.length() > 0 ) ?  result : ATTR_STATUS;
		}

		@Override
		protected Map<String, Serializable> getReportContextProps(DocStatusGroup item)
		{
			final Map<String, Serializable> result = new LinkedHashMap<String, Serializable>();

			/* Название ... */
			result.put( COLNAME_TAG, item.groupTag);

			/* Счётчики ... */
			final List<String> statusOrderedList = conf().getList(XMLSTATUSES_LIST);

			int iCol = 0;
			for( String colName: statusOrderedList) {
				iCol++; // (!) нумерация от Единицы
				// "col_CountNN" = (Integer) счётчик в этом статусе
				result.put( String.format( COLNAME_COUNTER_FMT, iCol), item.statusCounters.get(colName));
				// TODO: TEMP DEBUG ONLY
				// final int iRow = super.getData().indexOf(item);
				// result.put( String.format( COLNAME_COUNTER_FMT, iCol), iCol + iRow * 100);
			}

			/* последняя со статусами колонка будет состоять из всех значений, не вошедших в какие-либо предыдущие ... */ 
			iCol++;
			result.put( String.format( COLNAME_COUNTER_FMT, iCol), item.sumAllOthers(statusOrderedList));

			/* Параметры формирования колонки с суммой данных по строке ... */
			final Map<String, Object> mapStatusFlags =  conf().getMap(XMLSTATUS_FLAGS_MAP);
			if (mapStatusFlags != null) {
				// надо ли формировать столбец с суммой по строке
				final boolean enCalcRowSum = (mapStatusFlags.containsKey(XMLSTATUS_FLAGS_ITEM_ROWSUM_SHOW))
						&& "true".equalsIgnoreCase(""+mapStatusFlags.get(XMLSTATUS_FLAGS_ITEM_ROWSUM_SHOW));
				if (enCalcRowSum) {
					// название столбца содержащего сумму по строке
					final String rowSummaryColName = (mapStatusFlags.containsKey(XMLSTATUS_FLAGS_ITEM_ROWSUM_COLNAME))
						? ""+ mapStatusFlags.get(XMLSTATUS_FLAGS_ITEM_ROWSUM_COLNAME)
								: null;
					iCol++;
					result.put( Utils.coalesce( rowSummaryColName, String.format( COLNAME_COUNTER_FMT, iCol))
							, item.sumAll());
				}
			}

			return result;
		}

	}
}
