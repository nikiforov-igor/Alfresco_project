package ru.it.lecm.reports.generators;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.*;
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
import ru.it.lecm.reports.api.model.ColumnDescriptor;
import ru.it.lecm.reports.api.model.DataSourceDescriptor;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.jasper.utils.DurationLogger;
import ru.it.lecm.reports.utils.ParameterMapper;
import ru.it.lecm.reports.utils.Utils;
import ru.it.lecm.utils.LuceneSearchBuilder;

import java.io.Serializable;
import java.util.*;

/**
 * Запрос под Lucene Альфреско:
 *    1) сам текст 
 *    2) search-структура
 *    3-4) списки целевых атрибутов (и параметров) непосредственных и ссылочных
 * @author rabdullin
 *
 */
public class LucenePreparedQuery {

    final public static int QUERYROWS_UNLIMITED = -1; // неограниченое кол-во строк в ответе
    final public static int QUERYPG_ALL = -1; // без разбивки на страницы

	/* тип объектов по-умолчанию (когда не указано явно) */
	public static final String DEFAULT_DOCUMENT_TYPE = "lecm-document:base";

	private static final Logger logger = LoggerFactory.getLogger(LucenePreparedQuery.class);

	// текст Lucene запроса с условиями от простых параметров
	private String luceneQueryText;

	// поисковый запрос
	private SearchParameters alfrescoSearch;

	// колонки простые - с именем свойств  
	final private List<ColumnDescriptor> argsByProps = new ArrayList<ColumnDescriptor>();

	// колонки со сложными условиями (доступ к данных через ассоциации)
	final private List<ColumnDescriptor> argsByLinks = new ArrayList<ColumnDescriptor>();

    /**
     * колонки со сложными условиями (доступ к данным через ассоциации)
     */
    public List<ColumnDescriptor> argsByLinks() {
        return this.argsByLinks;
    }

    /**
     * колонки с простыми условиями (доступ к данным непосредственно по именам свойств Альфреско)
     */
    public List<ColumnDescriptor> argsByProps() {
        return this.argsByProps;
    }

    /** текущий текст запроса */
	public String luceneQueryText() { return this.luceneQueryText; }

	/** поисковый запрос */
	public SearchParameters alfrescoSearch() { 
		return this.alfrescoSearch;
	}

	public void setAlfrescoSearch(SearchParameters search) {
		this.alfrescoSearch = search;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("LuceneQuery [");

		builder.append("\n\t\t argsByProps ");
		builder.append( Utils.getAsString(argsByProps));

		builder.append("\n\t\t, argsByLinks ");
		builder.append( Utils.getAsString(argsByLinks));

		builder.append("\n\t\t, alfrescoSearch=");
		builder.append(alfrescoSearch);
		
		builder.append("\n\t\t, luceneQueryText:");
		builder.append("\n\t>>>\n").append(luceneQueryText).append("\n\t<<<");

		builder.append("\n\t ]");
		return builder.toString();
	}

	/**
	 * Сгенерить текст Lucene-запроса с учётом имеющихся простых параметров
	 * и создать список сложных параметров.
	 * Выполняется также проверка заполнения обязательных параметров, с поднятием исключений.
	 * @param reportDescriptor
	 * @return
	 */
    public static LucenePreparedQuery prepareQuery(final ReportDescriptor reportDescriptor, ServiceRegistry service) {
        final LucenePreparedQuery result = new LucenePreparedQuery();
        final StringBuilder bquery = new StringBuilder();
        final StringBuilder blog = new StringBuilder(); // для журналирования

        int iblog = 0;

        boolean hasData = false; // true становится после внесения первого любого условия в bquery

        result.argsByLinks.clear();
        result.argsByProps.clear();

		/* создаём базовый запрос:  по ID или TYPE */
        makeMasterCondition(bquery, reportDescriptor);

        if (bquery.length() > 0) {
            hasData = true;
        }

        if (reportDescriptor.getFlags().getText() != null && !reportDescriptor.getFlags().getText().isEmpty()) {
            bquery.append(hasData ? " AND" : "").append(reportDescriptor.getFlags().getText());
            hasData = true;
        }

        DictionaryService dictionaryService = service.getDictionaryService();

		/* 
         * проход по параметрам, которые являются простыми - и включение их в
		 * выражение поиска, другие параметры надо будет проверять после загрузки
		 * в фильтре данных
		 */
        // TODO: проход по простым параметрам
        for (ColumnDescriptor colDesc : reportDescriptor.getDsDescriptor().getColumns()) {
            if (colDesc == null || colDesc.getParameterValue() == null || Utils.isStringEmpty(colDesc.getExpression())) {
                continue;
            }

            if (colDesc.getExpression().startsWith(SubstitudeBean.OPEN_SUBSTITUDE_SYMBOL)) { //если подходит для сервиса
                String substitudeExpression = colDesc.getExpression();
                if (substitudeExpression.contains(SubstitudeBean.SPLIT_TRANSITIONS_SYMBOL)) {
                    // у нас ассоциация
                    result.argsByLinks.add(colDesc); // сложный параметр будем проверять позже - в фильтре данных
                } else {
                    // параметр может быть как название свойства, так и названием ассоциации - проверяем
                    QName expressionQName = null;
                    try {
                        expressionQName = QName.createQName(colDesc.getQNamedExpression(), service.getNamespaceService());
                    } catch (InvalidQNameException ex) {
                        logger.warn("Unsupported expression '%s' for column '%s'. Column skipped", substitudeExpression, colDesc);
                    }

                    if (expressionQName != null) {
                        AssociationDefinition definition = dictionaryService.getAssociation(expressionQName);
                        if (definition != null) {
                            /* здесь colDesc содержит ассоциацию ... */
                            result.argsByLinks.add(colDesc); // сложный параметр будем проверять позже - в фильтре данных
                        } else {
                            /* здесь colDesc содержит простой параметр ... */
                            result.argsByProps.add(colDesc);
                        }
                    } else {
                        continue; // у нас записано нечто непонятное - пропускаем
                    }
                }
            } else {
                // не подходит для сервиса - пропускаем
                continue;
            }

            // параметр пустой?  обязательный ? ...
            if (colDesc.getParameterValue().isEmpty()) {
                if (colDesc.getParameterValue().isRequired()) {
                    // пустой и обязательный - это криминал ...
                    throw new RuntimeException(String.format(
                            "Required parameter '%s' must be spesified '%s' (data column '%s')"
                            , ParameterMapper.getArgRootName(colDesc)
                            , colDesc.getColumnName()
                    ));
                } else {
                    continue; // Если нет условия для необязательного параметра - просто его пропускаем
                }
            }

            // экранированное имя с именем поля для поиска в Lucene
            String propertyName = colDesc.getExpression().replace(SubstitudeBean.OPEN_SUBSTITUDE_SYMBOL, "").replace(SubstitudeBean.CLOSE_SUBSTITUDE_SYMBOL, "");
            final String luceneFldName = Utils.luceneEncode(propertyName);

			/*
             *  граничные значения для поиска. По-идее здесь, после проверки
			 *  isEmpty(), для LIST/VALUE нидняя граница не пустая, а для
			 *  RANGE - одна из границ точно не пустая
			*/
            final Object bound1 = colDesc.getParameterValue().getBound1(),
                    bound2 = colDesc.getParameterValue().getBound2();

            String cond = ""; // сгенерированное условие

			/* генерим условие поиска - одно значение или интервал ... */
            switch (colDesc.getParameterValue().getType()) {

			/*
			1) экранировка символов в полном имени поля: ':', '-' 
			2) кавычки для значения
			3) (для LIST)  что-то для списка элементов (посмотреть синтаксис люцена)

			4) при подстановке дат надо их форматировать
				если надо чётко указать формат, его можно предусмотреть в 
				описателе колонки - для самой колонки и для параметра
			 */
                case VALUE:
                case LIST: // TODO: сгенерить запрос для списка (LIST) полное условие со всеми значениями
                    // пример формируемой строки: bquery.append( " AND @cm\\:creator:\"" + login + "\"");
                    String[] values;
                    if (bound1 instanceof String[]) {
                        values = (String[]) bound1;
                    } else {
                        values = new String[]{bound1.toString()};
                    }

                    boolean addOR = false;
                    for (String value : values) {
                        if (value != null && !value.isEmpty()){
                            String quotedValue = Utils.quoted(value);
                            cond += (addOR ? " OR " : "") + "@" + luceneFldName + ":" + quotedValue;
                            addOR = true;
                        }
                    }
                    break;
                case RANGE:
				/*
				проверить тип значения фактических значений параметра: 
						для дат вызывать emmitDate 
						для чисел (и строк) emmitNumeric
				 */
                    final boolean isArgDate = (bound1 instanceof Date) || (bound2 instanceof Date);
                    if (isArgDate) {
                        cond = Utils.emmitDateIntervalCheck(luceneFldName, (Date) bound1, (Date) bound2);
                    } else {
                        cond = Utils.emmitNumericIntervalCheck(luceneFldName, (Number) bound1, (Number) bound2);
                    }
                    break;

                default: // непонятный тип - сообщение об ошибке и игнор ...
                    cond = null;
                    logger.error(String.format("Unsupported parameter type '%s' skipped", Utils.coalesce(colDesc.getParameterValue().getType(), "NULL")));
                    break;
            }

            if (cond != null && !cond.isEmpty()) {
                bquery.append((hasData ? " AND " : "") + cond);
                hasData = true;

                iblog++;
                blog.append(String.format("\t[%d]\t%s\n", iblog, cond));
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Quering nodes by Lucene conditions:\n%s\n", blog.toString()));
        }

        result.luceneQueryText = bquery.toString();
        return result;
    }

    public static <T> List<T> checkSize(List<T> list, final int minCount, final int maxCount, final String msg) {
        final int size = (list == null) ? 0 : list.size();
        if ((size < minCount) || (maxCount < size)) {
            throw new RuntimeException(String.format("%s counter %s, expecting is [%s..%s]"
                    , msg
                    , size
                    , (minCount < 0 ? "*" : minCount)
                    , (maxCount < minCount ? "*" : maxCount)
            ));
        }
        return list;
    }

    /**
	 * Загрузить свойства  узлов, указанных в НД
	 * @param rset
	 * @param info название (пояснение) для загружаемых данных
	 * @param minCount минимальное кол-во (включительно)
	 * @param maxCount максимальное кол-во зависимостей (включительно) (-1 = UNLIMITED)
	 * @param nodeSrv
	 * @return
	 */
    static public List<Map<QName, Serializable>> loadNodeProps(ResultSet rset,
                                                               String info, int minCount, int maxCount, final NodeService nodeSrv) {

        final List<Map<QName, Serializable>> found = loadNodeProps(rset, nodeSrv);

        final boolean isUniqueCheck = (minCount <= 1) && (maxCount == 1);
        final String fmtMsg = (isUniqueCheck)
                ? "Unique constraint '%s' failed: found"
                : "Invalid '%s' nodes";

        return checkSize(found, minCount, maxCount, String.format(fmtMsg, info));
    }

	/**
	 * Загрузить свойства  узлов, указанных в НД
	 * @param rset
	 * @return
	 */
    static public List<Map<QName, Serializable>> loadNodeProps(ResultSet rset, final NodeService nodeSrv) {
        if (rset == null) {
            return null;
        }

        final List<Map<QName, Serializable>> result = new ArrayList<Map<QName, Serializable>>();

        for (Iterator<ResultSetRow> iter = rset.iterator(); iter.hasNext(); ) {
            final ResultSetRow row = iter.next();
            final NodeRef nodeId = row.getNodeRef(); // id узла
            result.add(nodeSrv.getProperties(nodeId));

        } // while

        return result.isEmpty() ? null : result;
    }


	/**
	 * Загрузить список одну или ноль дочернюю запись
	 * @param node
	 * @param assocType заказанный тип связи "детишек", если null -> не ограничено
	 * @param nodeSrv
	 * @param nameSrv
	 * @return null, одну запись или исключение, если найдено более одной
	 */
    public static NodeRef getAssocChild(NodeRef node, String assocType
            , NodeService nodeSrv, NamespaceService nameSrv) {
        final List<NodeRef> found = getAssocChildren(node, assocType, 0, 1, nodeSrv, nameSrv);
        return (found != null) ? found.get(0) : null;
    }

	/**
	 * Загрузить список дочерних записей состоящий из указанного кол-ва элементов
	 * @param node
	 * @param assocType заказанный тип связи "детишек", если null -> не ограничено
	 * @param minCount минимальное кол-во (включительно)
	 * @param maxCount максимальное кол-во зависимостей (включительно) (-1 = UNLIMITED)
	 * @param nodeSrv
	 * @param nameSrv
	 * @return непустой список "детишек" узла нужного типа, NULL если нет ни 
	 * одного и разрешено minCount = 0 или исключение, если найдено неверное 
	 * кол-во "детишек"
	 */
    public static List<NodeRef> getAssocChildren(NodeRef node
            , String assocType
            , final int minCount
            , final int maxCount
            , final NodeService nodeSrv
            , final NamespaceService nameSrv) {
        final List<NodeRef> found = getAssocChildren(node, assocType, nodeSrv, nameSrv);
        return checkSize(found, minCount, maxCount, String.format("Node '%s' has invalid child items '%s'", node, assocType));
    }

	/**
	 * Загрузить список дочерних записей по типу связи
	 * @param node
	 * @param assocType заказанный тип связи "детишек", если null -> не ограничено
	 * @param nodeSrv
	 * @param nameSrv
	 * @return непустой список "детишек" узла нужного типа или NULL
	 */
    public static List<NodeRef> getAssocChildren(NodeRef node
            , String assocType
            , final NodeService nodeSrv
            , final NamespaceService nameSrv) {
        final List<NodeRef> result = new ArrayList<NodeRef>(5);

        final List<ChildAssociationRef> links = nodeSrv.getChildAssocs(node);
        if (links != null && !links.isEmpty()) {
            final QName qAssocType = QName.createQName(assocType, nameSrv);
            for (ChildAssociationRef item : links) {
                if (qAssocType == null || qAssocType.equals(item.getTypeQName())) {
                    result.add(item.getChildRef());
                }
            }
        }
        return (result.isEmpty()) ? null : result;
    }

	/**
	 * Загрузить список дочерних записей по типу дочерних узлов
	 * @param node
	 * @param nodeSrv
	 * @param nameSrv
	 * @return непустой список "детишек" узла нужного типа или NULL
	 */
    public static List<NodeRef> getAssocChildrenByType(NodeRef node
            , String type
            , final NodeService nodeSrv
            , final NamespaceService nameSrv) {
        final List<NodeRef> result = new ArrayList<NodeRef>(5);

        final QName qtype = QName.createQName(type, nameSrv);
        final List<ChildAssociationRef> links = (qtype != null)
                ? nodeSrv.getChildAssocs(node, new HashSet<QName>(Arrays.asList(qtype)))
                : nodeSrv.getChildAssocs(node);
        if (links != null && !links.isEmpty()) {
            for (ChildAssociationRef item : links) {
                result.add(item.getChildRef());
            }
        }
        return (result.isEmpty()) ? null : result;
    }


	/**
	 * Загрузить список одну или ноль дочернюю запись
	 * @param node
	 * @param nodeSrv
	 * @param nameSrv
	 * @return null, одну запись или исключение, если найдено более одной
	 */
    public static NodeRef getAssocChildByType(NodeRef node, String type
            , NodeService nodeSrv, NamespaceService nameSrv) {
        final List<NodeRef> found = getAssocChildrenByType(node, type, nodeSrv, nameSrv);
        checkSize(found, 0, 1, String.format("Node '%s' has invalid child items '%s'", node, type));
        return (found != null) ? found.get(0) : null;
    }


    /**
	 * Загрузить список одну или ноль дочернюю запись
	 * @param node
	 * @param assocType заказанный тип связи "детишек", если null -> не ограничено
	 * @param nodeSrv
	 * @param nameSrv
	 * @return null, одну запись или исключение, если найдено более одной
	 */
    public static NodeRef getAssocTarget(NodeRef node, String assocType
            , NodeService nodeSrv, NamespaceService nameSrv) {
        final List<NodeRef> found = getAssocTargets(node, assocType, 0, 1, nodeSrv, nameSrv);
        return (found != null) ? found.get(0) : null;
    }

	/**
	 * Загрузить список дочерних записей состоящий из указанного кол-ва элементов
	 * @param node
	 * @param assocType заказанный тип связи "детишек"
	 * @param nodeSrv
	 * @param nameSrv
	 * @return непустой список "детишек" узла нужного типа, NULL если нет ни одного
	 */
    public static List<NodeRef> getAssocTargets(NodeRef node
            , String assocType
            , final NodeService nodeSrv
            , final NamespaceService nameSrv) {
        final List<AssociationRef> found = nodeSrv.getTargetAssocs(node, QName.createQName(assocType, nameSrv));
        final List<NodeRef> result = new ArrayList<NodeRef>();
        if (found != null) {
            for (AssociationRef a : found) result.add(a.getTargetRef());
        }
        return (result.isEmpty()) ? null : result;
    }

	/**
	 * Загрузить список дочерних записей состоящий из указанного кол-ва элементов
	 * @param node
	 * @param assocType заказанный тип связи "детишек"
	 * @param minCount минимальное кол-во (включительно)
	 * @param maxCount максимальное кол-во зависимостей (включительно) (-1 = UNLIMITED)
	 * @param nodeSrv
	 * @param nameSrv
	 * @return непустой список "детишек" узла нужного типа, NULL если нет ни 
	 * одного и разрешено minCount = 0 или исключение, если найдено неверное 
	 * кол-во "детишек"
	 */
    public static List<NodeRef> getAssocTargets(NodeRef node
            , String assocType
            , final int minCount
            , final int maxCount
            , final NodeService nodeSrv
            , final NamespaceService nameSrv) {
        final List<NodeRef> found = getAssocTargets(node, assocType, nodeSrv, nameSrv);
        return checkSize(found, minCount, maxCount, String.format("Node '%s' has invalid child items '%s'", node, assocType));
    }

    static public ResultSet execFindQuery(final LuceneSearchBuilder bquery, final SearchService searchService) {
        return execFindQuery(bquery, 0, QUERYROWS_UNLIMITED, searchService);
    }

	/**
	 * Выполнить поиск по указанному запросу
	 * @param bquery
	 * @param skipCountOffset
	 * @param queryItemsLimit
	 * @param searchService
	 * @return
	 */
    static public ResultSet execFindQuery(final LuceneSearchBuilder bquery, int skipCountOffset, int queryItemsLimit, final SearchService searchService) {
        if (bquery == null || bquery.isEmpty()) {
            return null;
        }

        final DurationLogger d = new DurationLogger();

        final SearchParameters search = new SearchParameters();
        search.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        search.setLanguage(SearchService.LANGUAGE_LUCENE);
        search.setQuery(bquery.getQuery().toString());

        // set offset ...
        if (skipCountOffset > 0) {
            search.setSkipCount(skipCountOffset);
        }

        // set limit ...
        if (queryItemsLimit != QUERYROWS_UNLIMITED) {
            search.setMaxItems(queryItemsLimit);
        }

        // (!) момент истины - ЗАПРОС
        final ResultSet alfrescoResult = searchService.query(search);

        final int foundCount = (alfrescoResult != null) ? alfrescoResult.length() : -1;
        d.logCtrlDuration(logger, String.format("\nQuery in {t} msec: found %d rows, limit %d, offset %d" + "\n>>>%s\n<<<"
                , foundCount, queryItemsLimit, skipCountOffset, bquery));
        return alfrescoResult;
    }

	/**
	 * Вставить основу запроса this.reportDescriptor: выборку по типу или по ID,
	 * в зависимости от isMultiRow().
	 * При отстутсвии параметров поднимается исключение.
	 * @param bquery
	 * @param reportDescriptor 
	 */
    private static void makeMasterCondition(final StringBuilder bquery, ReportDescriptor reportDescriptor) {
        if (reportDescriptor != null && reportDescriptor.getDsDescriptor() != null) {
            // @NOTE: (reportDescriptor.getFlags().isMultiRow()) не достаточно для определения того что именно долждно проверяться TYPE или ID
            // так что выбираем оба значения

            // по типу ...
            ColumnDescriptor colWithType = reportDescriptor.getDsDescriptor().findColumnByParameter(DataSourceDescriptor.COLNAME_TYPE);
            final boolean hasType =
                    Utils.emmitParamCondition(bquery, colWithType, "TYPE:");
            // по ID/NodeRef ...
            ColumnDescriptor colWithID = reportDescriptor.getDsDescriptor().findColumnByParameter(DataSourceDescriptor.COLNAME_ID);
            final boolean hasId = Utils.emmitParamCondition(bquery, colWithID, "ID:");

            if (!(hasType || hasId))
                logger.warn(String.format("None of main parameteres specified: '%s' nor '%s' ", DataSourceDescriptor.COLNAME_TYPE, DataSourceDescriptor.COLNAME_ID));
        } else { // если НД не задан - выборка по документам ...
            bquery.append("TYPE:").append(Utils.quoted(DEFAULT_DOCUMENT_TYPE));
        }
    }

}
