package ru.it.lecm.reports.generators;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
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
import ru.it.lecm.reports.api.model.ColumnDescriptor;
import ru.it.lecm.reports.api.model.DataSourceDescriptor;
import ru.it.lecm.reports.api.model.ParameterTypedValue;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.jasper.ReportDSContextImpl;
import ru.it.lecm.reports.jasper.utils.DurationLogger;
import ru.it.lecm.reports.utils.ParameterMapper;
import ru.it.lecm.reports.utils.Utils;
import ru.it.lecm.utils.LuceneSearchBuilder;

/**
 * Запрос под Lucene Альфреско:
 * 1) сам текст
 * 2) search-структура
 * 3-4) списки целевых атрибутов (и параметров) непосредственных и ссылочных
 *
 * @author rabdullin
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

    /**
     * текущий текст запроса
     */
    public String luceneQueryText() {
        return luceneQueryText;
    }

    public void setLuceneQueryText(String qtext) {
        this.luceneQueryText = qtext;
    }

    /**
     * поисковый запрос
     */
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
        builder.append(Utils.getAsString(argsByProps));

        builder.append("\n\t\t, argsByLinks ");
        builder.append(Utils.getAsString(argsByLinks));

        builder.append("\n\t\t, alfrescoSearch=");
        builder.append(alfrescoSearch);

        builder.append("\n\t\t, luceneQueryText:");
        builder.append("\n\t>>>\n").append(luceneQueryText).append("\n\t<<<");

        builder.append("\n\t ]");
        return builder.toString();
    }

    /**
     * Сгенерить текст Lucene-запроса с учётом имеющихся:
     * 1) типа (TYPE),
     * 2) ID,
     * 3) простых параметров,
     * 4) и возможного текста запроса из флагов (reportDescriptor.flags.text).
     * Заполняются списки result.argsByLinks (с параметрами-сложными ссылками)
     * и result.argsByProps (с простыми, условия для которых здесь добавляются
     * в текст запроса)
     * Выполняется проверка заполнения обязательных параметров, с поднятием исключений при ошибках.
     *
     * @param reportDescriptor
     * @return
     */
    public static LucenePreparedQuery prepareQuery(final ReportDescriptor reportDescriptor, ServiceRegistry registry) {
        final NamespaceService ns = (registry == null) ? null : registry.getNamespaceService();
        final DictionaryService dictionaryService = (registry != null) ? registry.getDictionaryService() : null;

        final LucenePreparedQuery result = new LucenePreparedQuery();
        final LuceneSearchBuilder bquery = new LuceneSearchBuilder(ns);
        final StringBuilder blog = new StringBuilder(); // для журналирования

        int iblog = 0;

        boolean hasData = false; // true становится после внесения первого любого условия в bquery

        result.argsByLinks.clear();
        result.argsByProps.clear();

		/* создаём базовый запрос: TYPE */
        makeMasterCondition(bquery, reportDescriptor);

        if (!bquery.isEmpty()) {
            hasData = true;
        }

        if (reportDescriptor.getFlags().getText() != null && !reportDescriptor.getFlags().getText().isEmpty()) {
            bquery.emmit(hasData ? " AND " : "").emmit(reportDescriptor.getFlags().getText());
            hasData = true;
        }

		/* 
         * проход по параметрам, которые являются простыми - и включение их в
		 * выражение поиска, другие параметры надо будет проверять после загрузки
		 * в фильтре данных
		 */
        for (ColumnDescriptor colDesc : reportDescriptor.getDsDescriptor().getColumns()) {
            if (colDesc == null || colDesc.getParameterValue() == null) { // не параметр
                continue;
            }

			/* пропускаем уже обработанные тип и ID ... */
            if (DataSourceDescriptor.COLNAME_TYPE.equals(colDesc.getColumnName())
                    || DataSourceDescriptor.COLNAME_ID.equals(colDesc.getColumnName())) {
                continue;
            }

			/*
             * Здесь у колонки используется:
			 *   1) getAlfrescoType() ТИП ПОЛЯ АЛЬФРЕСКО, исп-ся для определения является ли ссылка ассоциацией 
			 *   2) getExpression()  ВЫРАЖЕНИЕ ДЛЯ ПОЛУЧЕНИЯ ЗНАЧЕНИЯ ПОЛЯ - это в терминах провайдера, так что используется метод isDirectAlfrescoPropertyLink
			 *   (предполагается, что простые ссылки ВСЕГДА имеют вид "{abc:def}")
			 *   3) bound1/bound2 ИСКОМЫЕ (ПРОВЕРЯЕМЫЕ) ЗНАЧЕНИЯ 
			 */

            // параметр пустой? обязательный? ...
            if (colDesc.getParameterValue().isEmpty()) {
                if (colDesc.getParameterValue().isRequired()) {
                    // пустой и обязательный - это криминал ...
                    throw new RuntimeException(String.format(
                            "Required parameter '%s' must be spesified (data column '%s')"
                            , ParameterMapper.getArgRootName(colDesc)
                            , colDesc.getColumnName()
                    ));
                }
                continue; // Если нет условия для необязательного параметра - просто его пропускаем
            }

            final String substitudeExpression = colDesc.getExpression();
            if (Utils.isStringEmpty(substitudeExpression)) {
                // пустая ссылка ...
                logger.debug(String.format("Column '%s' parameter has empty expression -> skipped", colDesc.getColumnName()));
                continue;
            }

            final boolean isSimpleLink = ReportDSContextImpl.isDirectAlfrescoPropertyLink(substitudeExpression);
            if (!isSimpleLink) {
                // сложный параметр будем проверять позже - в фильтре данных
                result.argsByLinks.add(colDesc);
                continue;
            }

			/* здесь поле задано ссылкой */
            // вставляем в список ссылок или простых полей ...

            if (Utils.isStringEmpty(colDesc.getAlfrescoType())) {
                logger.warn(String.format("Column '%s' parameter has empty Alfresco type -> skipped", colDesc.getColumnName()));
                continue;
            }

            // параметр может быть как название свойства, так и названием ассоциации - проверяем
            QName expressionQName = null;
            try { // создать qname поля ...
                expressionQName = QName.createQName(colDesc.getQNamedExpression(), ns);
            } catch (InvalidQNameException ex) {
                logger.warn("Unsupported parameter type '%s' for column '%s' -> column condition skipped", colDesc.getAlfrescoType(), colDesc.getColumnName());
            }

            if (expressionQName == null) {
                try { // qname типа, для проверки наличия ассоциаций
                    expressionQName = QName.createQName(colDesc.getAlfrescoType(), ns);
                } catch (InvalidQNameException ex) {
                    logger.warn("Unsupported parameter type '%s' for column '%s' -> column condition skipped", colDesc.getAlfrescoType(), colDesc.getColumnName());
                }
            }

            if (expressionQName == null) {
                continue; // у нас записано нечто непонятное - пропускаем
            }

            if (dictionaryService != null) {
                final AssociationDefinition definition = dictionaryService.getAssociation(expressionQName);

                final boolean isLink = (definition != null);
                if (isLink) { /* здесь colDesc содержит ассоциацию ... */
                    result.argsByLinks.add(colDesc); // сложный параметр (с ассоциацией) будем проверять позже - в фильтре данных
                    continue;
                }
            }

				/* здесь colDesc содержит простой параметр и для него надо будет генерировать условие тут ... */
            result.argsByProps.add(colDesc);


            // экранированное имя с именем поля для поиска в Lucene
            final String propertyName = substitudeExpression.replace(SubstitudeBean.OPEN_SUBSTITUDE_SYMBOL, "").replace(SubstitudeBean.CLOSE_SUBSTITUDE_SYMBOL, "");

			/* сгенерировать условие (VALUE/LIST/RANGE) ... */
            final StringBuilder cond = makeValueCond(propertyName, colDesc.getParameterValue());

            if (cond != null && cond.length() > 0) {
                bquery.emmit((hasData ? " AND" : "") + " (" + cond + ")");
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

    /**
     * Сгенерировать условие для проверки атрибута
     *
     * @param propertyName атрибут Альфреско (строка вида "тип:поле")
     * @param parType      параметр, с которым надо сгенерировать условие
     * @return
     */
    private static StringBuilder makeValueCond(String propertyName, ParameterTypedValue parType) {

        if (parType == null || propertyName == null) {
            return null;
        }

        final String luceneFldName = Utils.luceneEncode(propertyName);

		/*
		 *  граничные значения для поиска. По-идее здесь, после проверки
		 *  isEmpty(), для LIST/VALUE нижняя граница не пустая, а для
		 *  RANGE - одна из границ точно не пустая
		 */
		final Object
				bound1 = parType.getBound1(),
				bound2 = parType.getBound2();

        final StringBuilder cond = new StringBuilder(); // сгенерированное условие

		/* генерим условие поиска - одно значение или интервал ... */
        switch (parType.getType()) {
		/*
			1) экранировка символов в полном имени поля: ':', '-'
			2) кавычки для значения
			3) (для LIST)  что-то для списка элементов (посмотреть синтаксис люцена)
			4) при подстановке дат надо их форматировать
				если надо чётко указать формат, его можно предусмотреть в 
				описателе колонки - для самой колонки и для параметра
		 */
            case RANGE:
			/*
			проверить тип значения фактических значений параметра:
					для дат вызывать emmitDate
					для чисел (и строк) emmitNumeric
			 */
                final boolean isArgDate = (bound1 instanceof Date) || (bound2 instanceof Date);
                final boolean isArgNumber = (bound1 instanceof Number) || (bound2 instanceof Number);
                final String condRange;
                if (isArgDate) {
                    condRange = Utils.emmitDateIntervalCheck(luceneFldName, (Date) bound1, (Date) bound2);
                } else if (isArgNumber) {
                    condRange = Utils.emmitNumericIntervalCheck(luceneFldName, (Number) bound1, (Number) bound2);
                } else {
                    throw new RuntimeException(String.format("Unsupported RANGE values of bounds: %s/%s"
                            , (bound1 == null ? "NULL" : bound1.getClass().getName())
                            , (bound2 == null ? "NULL" : bound2.getClass().getName())
                    ));
                }
                if (condRange != null) {
                    cond.append(condRange);
                }
                break; // case

            case VALUE:
            case LIST: // DONE: сгенерить запрос для списка (LIST) полное условие со всеми значениями
                // пример формируемой строки: bquery.append( " AND @cm\\:creator:\"" + login + "\"");
                final String[] values;
                if (bound1 == null) {
                    values = null;
                } else if (bound1 instanceof String[]) {
                    values = (String[]) bound1;
                } else {
                    values = new String[]{bound1.toString()};
                }

                Utils.emmitValuesInsideList(cond, luceneFldName, values);
                break;

            default: // непонятный тип - сообщение об ошибке и игнор ...
                logger.error(String.format("Unsupported parameter type '%s' -> condition skipped", Utils.coalesce(parType.getType(), "NULL")));
                break;
        }

        return cond;
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
     *
     * @param rset
     * @param info     название (пояснение) для загружаемых данных
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
     *
     * @param rset
     * @return
     */
    static public List<Map<QName, Serializable>> loadNodeProps(ResultSet rset, final NodeService nodeSrv) {
        if (rset == null) {
            return null;
        }

        final List<Map<QName, Serializable>> result = new ArrayList<Map<QName, Serializable>>();

        for (final ResultSetRow row : rset) {
            final NodeRef nodeId = row.getNodeRef(); // id узла
            result.add(nodeSrv.getProperties(nodeId));
        } // while

        return result.isEmpty() ? null : result;
    }


    /**
     * Загрузить список одну или ноль дочернюю запись
     *
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
     *
     * @param node
     * @param assocType заказанный тип связи "детишек", если null -> не ограничено
     * @param minCount  минимальное кол-во (включительно)
     * @param maxCount  максимальное кол-во зависимостей (включительно) (-1 = UNLIMITED)
     * @param nodeSrv
     * @param nameSrv
     * @return непустой список "детишек" узла нужного типа, NULL если нет ни
     *         одного и разрешено minCount = 0 или исключение, если найдено неверное
     *         кол-во "детишек"
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
     * @param node
     * @param assocType заказанный тип связи "детишек"
     * @param minCount  минимальное кол-во (включительно)
     * @param maxCount  максимальное кол-во зависимостей (включительно) (-1 = UNLIMITED)
     * @param nodeSrv
     * @param nameSrv
     * @return непустой список "детишек" узла нужного типа, NULL если нет ни
     *         одного и разрешено minCount = 0 или исключение, если найдено неверное
     *         кол-во "детишек"
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
     *
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
     *
     * @param bquery
     * @param reportDescriptor
     */
    private static void makeMasterCondition(final LuceneSearchBuilder bquery, ReportDescriptor reportDescriptor) {
        if (reportDescriptor != null && reportDescriptor.getDsDescriptor() != null) {
            // @NOTE: (reportDescriptor.getFlags().isMultiRow()) не достаточно для определения того что именно долждно проверяться TYPE или ID
            // так что выбираем оба значения

            // по типу - из колонки, если она имеется и заполнена или из preferedNodeType-атрибута шаблона ...
            boolean hasType = false;
            { // пробуем из колонки данных "TYPE" ...
                final ColumnDescriptor colWithType = reportDescriptor.getDsDescriptor().findColumnByParameter(DataSourceDescriptor.COLNAME_TYPE);
                if (colWithType != null && colWithType.getParameterValue() != null) {
                    if (!colWithType.getParameterValue().isEmpty()) {
                        hasType = bquery.emmitTypeCond(colWithType.getParameterValue().getBound1().toString(), null);
                    }
                }

                if (!hasType) { // если тп не задан параметрами - пробуем из описателя атрибута ...
                    if (reportDescriptor.getFlags() != null) {
                        hasType = bquery.emmitTypeCond(reportDescriptor.getFlags().getSupportedNodeTypes(), null);
                    }
                }
            }

			/* по ID/NodeRef ... */
            boolean hasId = false;

            final ColumnDescriptor colWithID = reportDescriptor.getDsDescriptor().findColumnByParameter(DataSourceDescriptor.COLNAME_ID);
			if (	colWithID != null
					&& colWithID.getParameterValue() != null
					&& !colWithID.getParameterValue().isEmpty()
			) {
                final StringBuilder idCond = makeValueCond("ID", colWithID.getParameterValue());
                if (idCond != null && idCond.length() > 0) {
                    final String sCond = idCond.toString().replace("@ID", "ID"); // замена обычной ссылки на атрибут, ссылкой на тип
                    bquery.emmit((hasType ? " AND" : "") + " (" + sCond + ")");
                    hasId = true;
                }
            }


            if (!(hasType || hasId))
                logger.warn(String.format("None of main parameteres specified: '%s' nor '%s' ", DataSourceDescriptor.COLNAME_TYPE, DataSourceDescriptor.COLNAME_ID));

        } else { // если НД не задан - выборка по документам ...
            bquery.emmitTypeCond(DEFAULT_DOCUMENT_TYPE, null);
        }
    }
}
