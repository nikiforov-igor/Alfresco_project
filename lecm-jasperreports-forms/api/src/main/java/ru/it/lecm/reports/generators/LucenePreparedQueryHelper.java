package ru.it.lecm.reports.generators;

import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.InvalidQNameException;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.SearchQueryProcessorService;
import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.reports.api.ReportDSContext;
import ru.it.lecm.reports.api.model.DataSourceDescriptor;
import ru.it.lecm.reports.api.model.ParameterTypedValue;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.beans.WKServiceKeeper;
import ru.it.lecm.reports.jasper.utils.DurationLogger;
import ru.it.lecm.reports.model.impl.ColumnDescriptor;
import ru.it.lecm.reports.model.impl.JavaDataType;
import ru.it.lecm.reports.utils.ArgsHelper;
import ru.it.lecm.reports.utils.ParameterMapper;
import ru.it.lecm.reports.utils.Utils;
import ru.it.lecm.reports.utils.LuceneSearchWrapper;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Запрос под Lucene Альфреско:
 * 1) сам текст
 * 2) search-структура
 * 3-4) списки целевых атрибутов (и параметров) непосредственных и ссылочных
 *
 * @author rabdullin
 */
public class LucenePreparedQueryHelper {

    private static final Logger logger = LoggerFactory.getLogger(LucenePreparedQueryHelper.class);

    final public static int QUERYROWS_UNLIMITED = -1; // неограниченое кол-во строк в ответе
    final public static int QUERYPG_ALL = -1; // без разбивки на страницы
    public static final String VALUE_PLACEHOLDER = "#value";
    public static final String VALUE_PLACEHOLDER_1 = "#value1";
    public static final String VALUE_PLACEHOLDER_2 = "#value2";

    public final Pattern PARAM_PATTERN = Pattern.compile("\\$P\\{.*?\\}");

    private SearchQueryProcessorService processorService;
    private WKServiceKeeper services;

    public void setProcessorService(SearchQueryProcessorService processorService) {
        this.processorService = processorService;
    }

    public void setServices(WKServiceKeeper services) {
        this.services = services;
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
     */
    public LuceneSearchWrapper prepareQuery(final ReportDescriptor reportDescriptor, final ReportDSContext parentContext) {
        final NamespaceService ns = services.getServiceRegistry().getNamespaceService();
        final DictionaryService dictionaryService = services.getServiceRegistry().getDictionaryService();

        final LuceneSearchWrapper bquery = new LuceneSearchWrapper();
        final StringBuilder blog = new StringBuilder(); // для журналирования

        int iblog = 0;

		/* создаём базовый запрос: TYPE, ID */
        final String masterCondition = !reportDescriptor.isSubReport() ? makeMasterCondition(reportDescriptor) : "";
        bquery.emmit(masterCondition);

        String queryText = reportDescriptor.getFlags().getText();
        boolean isSubstituteQuery = isSubstCalcExpr(queryText);
        if (queryText != null && !queryText.isEmpty()) {
            if (parentContext != null) {
                if (!isSubstituteQuery) {
                    queryText = insertParamsToQuery(queryText, parentContext);
                }
            }
            if (!isSubstituteQuery) {
                bquery.emmit(!bquery.isEmpty() ? " AND " : "")
                        .emmit("(")
                        .emmit(processorService.processQuery(queryText))
                        .emmit(")");
            } else {
                bquery.emmit(queryText);
            }
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
                            "Required parameter '%s' must be specified (data column '%s')"
                            , ParameterMapper.getArgRootName(colDesc)
                            , colDesc.getColumnName()
                    ));
                }
                continue; // Если нет условия для необязательного параметра - просто его пропускаем
            }

            final String substituteExpression = colDesc.getExpression();
            if (Utils.isStringEmpty(substituteExpression)) {
                // пустая ссылка ...
                logger.debug(String.format("Column '%s' parameter has empty expression -> skipped", colDesc.getColumnName()));
                continue;
            }

            final boolean isProcessedField = isProcessedFieldLink(substituteExpression);

            if (isProcessedField) {
                final String cond = makeProcessedCondition(substituteExpression, colDesc.getParameterValue());
                if (cond != null && cond.length() > 0 && !cond.equals(substituteExpression)){
                    bquery.emmit(!bquery.isEmpty() ? " AND " : "").emmit(cond);
                }
                continue;
            }
            final boolean isSimpleLink = isDirectAlfrescoPropertyLink(substituteExpression);
            if (!isSimpleLink) {
                // сложный параметр будем проверять позже - в фильтре данных
                bquery.getArgsByLinks().add(colDesc);
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
                continue; // у нас записано нечто непонятное - пропускаем
            }

            final boolean isLink = (dictionaryService.getAssociation(expressionQName) != null);
            if (isLink) { /* здесь colDesc содержит ассоциацию ... */
                bquery.getArgsByLinks().add(colDesc); // сложный параметр (с ассоциацией) будем проверять позже - в фильтре данных
                continue;
            } else {
                bquery.getArgsByProps().add(colDesc);
            }

			/* сгенерировать условие (VALUE_PLACEHOLDER/LIST/RANGE) ... */
            final String cond = makeValueCondition(colDesc.getQNamedExpression(), colDesc.getParameterValue());

            if (cond != null && cond.length() > 0) {
                bquery.emmit(!bquery.isEmpty() ? " AND " : "").emmit("(").emmit(processorService.processQuery(cond)).emmit(")");
                iblog++;
                blog.append(String.format("\t[%d]\t%s\n", iblog, cond));
            }
        }

        boolean hasData = !bquery.isEmpty();
        if (!isSubstituteQuery) {
            if (!bquery.getQuery().toString().contains("ID:") && !reportDescriptor.isSubReport()) {
                // если для родительского отчета нет явного задания ID -> исключаем черновики
                String condition = emmitFieldCondition((hasData ? " AND NOT(" : ""), "lecm-statemachine-aspects:is-draft", true);
                bquery.emmit(condition);
                bquery.emmit(hasData ? ")" : "");
            }

            if (!reportDescriptor.getFlags().isIncludeAllOrganizations()) {
                bquery.emmit((!bquery.isEmpty() ? " AND " : "") + processorService.processQuery("{{IN_SAME_ORGANIZATION}}"));
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Quering nodes by Lucene conditions:\n%s\n", blog.toString()));
        }

        return bquery;
    }

    private String insertParamsToQuery(String baseQuery, ReportDSContext context) {
        if (baseQuery != null && context != null) {
            // заменяем все ключевые слова
            NodeRef parent = context.getCurNodeRef();

            NodeService nodeService = services.getServiceRegistry().getNodeService();
            NamespaceService namespaceService = services.getServiceRegistry().getNamespaceService();

            baseQuery = baseQuery.replaceAll("\\$P\\{PARENT_ID\\}", parent.toString());
            baseQuery = baseQuery.replaceAll("\\$P\\{PARENT_PATH\\}",nodeService.getPath(parent).toPrefixString(namespaceService));

            // идём по параметрам
            Matcher m = PARAM_PATTERN.matcher(baseQuery);
            while (m.find()) {
                String paramText = m.group();
                String paramKey = paramText.substring(3, paramText.length() - 1).trim(); // удаляем спецсимволы
                if (!paramKey.isEmpty()) {
                    JavaDataType.SupportedTypes type;
                    Object paramValue = context.getPropertyValueByJRField(paramKey);
                    if (!(paramValue instanceof List)) { // у нас мог прийти ArrayList - он тоже List
                        type = paramValue != null ?
                                JavaDataType.SupportedTypes.findType(paramValue.getClass().getName()) :
                                JavaDataType.SupportedTypes.NULL;
                    } else {
                        type = JavaDataType.SupportedTypes.LIST;
                    }

                    baseQuery = baseQuery.replaceAll("\\$P\\{" + paramKey + "\\}", type.getFTSPreparedValue(paramValue));
                }
            }
            // заменяем все пустые параметры, если такие остались
            baseQuery = baseQuery.replaceAll("\\$P\\{.*\\}","*");
        }
        return baseQuery;
    }

    private String makeProcessedCondition(String processorExpression, ParameterTypedValue parType) {
        if (processorExpression == null || parType == null) {
            return null;
        }

        Object bound1 = parType.getBound1();
        Object bound2 = parType.getBound2();

        if (processorExpression.contains(VALUE_PLACEHOLDER)) { //есть что подменять
            switch (parType.getType()) {
                case RANGE:
                    if (bound2 != null) { //только в случае параметра-диапозона есть второе граничное значение
                        if (bound2 instanceof Date) {
                            bound2 = Utils.dateToString((Date) bound2);
                        }
                        processorExpression = processorExpression.replace(VALUE_PLACEHOLDER_2, bound2.toString());
                    }
                    // без break - падаем ниже, чтобы обработать первое граничное значение
                case VALUE:
                case LIST:
                    if (bound1 != null) {
                        if (bound1 instanceof Object[]) {
                            bound1 = Utils.getAsString((Object[]) bound1, ",", "'", "'");
                        }
                        if (bound1 instanceof Date) {
                            bound1 = Utils.dateToString((Date) bound1);
                        }
                        processorExpression = processorExpression.replace(VALUE_PLACEHOLDER, bound1.toString());
                        processorExpression = processorExpression.replace(VALUE_PLACEHOLDER_1, bound1.toString());
                    }
                    break;
                default: // непонятный тип - сообщение об ошибке и игнор ...
                    logger.error(String.format("Unsupported parameter type '%s' -> condition skipped", Utils.coalesce(parType.getType(), "NULL")));
                    break;
            }
        }
        return processorService.processQuery(processorExpression);
    }

    private String makeValueCondition(String propertyName, ParameterTypedValue parType) {
        return makeValueCondition(propertyName, parType, false, null);
    }

    /**
     * Сгенерировать условие для проверки атрибута
     *
     * @param propertyName атрибут Альфреско (строка вида "тип:поле")
     * @param parType      параметр, с которым надо сгенерировать условие
     */
    private String makeValueCondition(String propertyName, ParameterTypedValue parType, boolean splitValue, String delimiter) {
        if (parType == null || propertyName == null) {
            return null;
        }

		/*
         *  граничные значения для поиска. По-идее здесь, после проверки
		 *  isEmpty(), для LIST/VALUE_PLACEHOLDER нижняя граница не пустая, а для
		 *  RANGE - одна из границ точно не пустая
		 */
        final Object bound1 = parType.getBound1();
        final Object bound2 = parType.getBound2();

        final StringBuilder condition = new StringBuilder(); // сгенерированное условие

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
                final String rangeCondition;
                if ((bound1 instanceof Date) || (bound2 instanceof Date)) {
                    rangeCondition = emmitDateIntervalCheck(propertyName, (Date) bound1, (Date) bound2);
                } else if ((bound1 instanceof Number) || (bound2 instanceof Number)) {
                    rangeCondition = emmitNumericIntervalCheck(propertyName, (Number) bound1, (Number) bound2);
                } else {
                    throw new RuntimeException(String.format("Unsupported RANGE values of bounds: %s/%s"
                            , (bound1 == null ? "NULL" : bound1.getClass().getName())
                            , (bound2 == null ? "NULL" : bound2.getClass().getName())
                    ));
                }
                if (rangeCondition != null) {
                    condition.append(rangeCondition);
                }
                break; // case

            case VALUE:
            case LIST: // DONE: сгенерить запрос для списка (LIST) полное условие со всеми значениями
                // пример формируемой строки: bquery.append( " AND @cm\\:creator:\"" + login + "\"");
                final String[] values;
                if (bound1 == null) {
                    values = null;
                } else if (bound1 instanceof String[]) {
                    if (splitValue) {
                        Set<String> valuesSet = new HashSet<String>();
                        for (String bound : (String[]) bound1) {
                            if (bound != null && !bound.isEmpty()) {
                                String[] boundVs = bound.split(delimiter);
                                Collections.addAll(valuesSet, boundVs);
                            }
                        }
                        values = valuesSet.toArray(new String[valuesSet.size()]);
                    } else {
                        values = (String[]) bound1;
                    }
                } else {
                    if (splitValue) {
                        values = bound1.toString().split(delimiter);
                    } else {
                        values = new String[]{bound1.toString()};
                    }
                }

                condition.append(emmitValuesInsideList(propertyName, values));
                break;

            default: // непонятный тип - сообщение об ошибке и игнор ...
                logger.error(String.format("Unsupported parameter type '%s' -> condition skipped", Utils.coalesce(parType.getType(), "NULL")));
                break;
        }

        return condition.toString();
    }

    @SuppressWarnings("unused")
    public ResultSet executePreparedQuery(final LuceneSearchWrapper bquery) {
        return execFindQuery(bquery, 0, QUERYROWS_UNLIMITED, services.getServiceRegistry().getSearchService());
    }

    /**
     * Выполнить поиск по указанному запросу
     */
    private ResultSet execFindQuery(final LuceneSearchWrapper bquery, int skipCountOffset, int queryItemsLimit, final SearchService searchService) {
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
     */
    private String makeMasterCondition(ReportDescriptor reportDescriptor) {
        StringBuilder sb = new StringBuilder();
        if (reportDescriptor != null && reportDescriptor.getDsDescriptor() != null) {
            // @NOTE: (reportDescriptor.getFlags().isMultiRow()) не достаточно для определения того что именно долждно проверяться TYPE или ID
            // так что выбираем оба значения

            // по типу - из колонки, если она имеется и заполнена или из preferedNodeType-атрибута шаблона ...
            boolean hasType = false;
            // пробуем из колонки данных "TYPE" ...
            final ColumnDescriptor colWithType = reportDescriptor.getDsDescriptor().findColumnByParameter(DataSourceDescriptor.COLNAME_TYPE);
            if (colWithType != null && colWithType.getParameterValue() != null) {
                if (!colWithType.getParameterValue().isEmpty()) {
                    String typeCondition = emmitTypeCond(colWithType.getParameterValue().getBound1().toString(), null);
                    sb.append(typeCondition);
                    hasType = sb.length() > 0;
                }
            }

			/* по ID/NodeRef ... */
            boolean hasId = false;

            final ColumnDescriptor colWithID = reportDescriptor.getDsDescriptor().findColumnByParameter(DataSourceDescriptor.COLNAME_ID);
            if (colWithID != null && colWithID.getParameterValue() != null && !colWithID.getParameterValue().isEmpty()) {
                final String idCond = makeValueCondition("ID", colWithID.getParameterValue(), true, ",");
                if (idCond != null && idCond.length() > 0) {
                    sb.append(hasType ? " AND " : "").append(idCond);
                    hasId = true;
                }
            }


            if (!(hasType || hasId))
                logger.warn(String.format("None of main parameteres specified: '%s' nor '%s' ", DataSourceDescriptor.COLNAME_TYPE, DataSourceDescriptor.COLNAME_ID));

        } else { // если НД не задан - выборка по всем документам ...
            logger.warn("Report Descriptor is NULL or DS is NULL. Empty query added");
        }
        return sb.toString();
    }

    /**
     * Сформировать lucene-style проверку попадания поля даты в указанный интервал.
     * Формируется условие вида " @fld:[ x TO y]"
     * Если обе даты пустые - ничего не формируется
     *
     * @param fldName (!) экранированное имя поля, (!) без символа '@' в начале
     * @param from    дата начала
     * @param upto    дата конца
     * @return условие проверки вхождения даты в диапазон или NULL, если обе даты NULL
     */
    public String emmitDateIntervalCheck(String fldName, Date from, Date upto) {
        return emmitDateIntervalCheck(fldName, from, upto, false);
    }

    public String emmitDateIntervalCheck(String fldName, Date from, Date upto, boolean includeNullValue) {
        final boolean needEmmition = (from != null || upto != null);
        if (!needEmmition) {
            return null;
        }

        // если даты не по-порядку - поменяем их местами
        if (from != null && upto != null) {
            if (from.after(upto)) {
                final Date temp = from;
                from = upto;
                upto = temp;
            }
        }

        // add " ... [X TO Y]"
        final String stMIN = ArgsHelper.dateToStr(from, "MIN");
        final String stMAX = ArgsHelper.dateToStr(upto, "MAX");

        return (includeNullValue ? " (ISNULL:\"" + fldName +  "\" OR NOT EXISTS:\"" + fldName + "\" ) OR " : "@") + luceneEncode(fldName) + ":[\"" + stMIN + "\" TO \"" + stMAX + "\"]";
    }

    /**
     * Сформировать lucene-style проверку попадания поля числа в указанный интервал.
     * Формируется условие вида " @fld:[ x TO y]"
     * Если обе границы пустые - ничего не формируется
     *
     * @param fldName (!) экранированное имя поля, (!) без символа '@' в начале
     * @param from    числовая границы слева
     * @param upto    числовая границы справа
     * @return условие проверки вхождения числа в диапазон или NULL, если обе границы NULL
     */
    public String emmitNumericIntervalCheck(String fldName, Number from, Number upto) {
        final boolean needEmmition = (from != null || upto != null);
        if (!needEmmition) {
            return null;
        }
        // если даты не по-порядку - поменяем их местами
        if (from != null && upto != null) {
            if (from.doubleValue() > upto.doubleValue()) {
                final Number temp = from;
                from = upto;
                upto = temp;
            }
        }

        // add " ... [X TO Y]"
        //  используем формат без разделителя, чтобы нормально выполнялся строковый поиск ...
        final String stMIN = (from != null) ? String.valueOf(from.doubleValue()) : "MIN";
        final String stMAX = (upto != null) ? String.valueOf(upto.doubleValue()) : "MAX";
        return " (ISNULL:\"" + fldName + "\" OR NOT EXISTS:\"" + fldName + "\" ) OR " + "@" + luceneEncode(fldName) + ":[" + stMIN + " TO " + stMAX + "]";
    }

    /**
     * Экранировка символов [':', '-'] в указанной строке символом '\' для Lucene-строк
     *
     * @return String
     */
    public String luceneEncode(String s) {
        return doCharsProtection(s, ":-");
    }

    /**
     * Экранировка указанных символов в строке символом '\'
     *
     * @param s     экранируемая строка
     * @param chars символы, которые подлежат экранировке
     * @return String
     */
    private String doCharsProtection(String s, String chars) {
        if (Utils.isStringEmpty(s) || Utils.isStringEmpty(chars)) {
            return s;
        }
        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            final char ch = s.charAt(i);
            if (Utils.CH_WALL == ch   /* сам символ "экрана" тоже надо экранировать */
                    || chars.indexOf(ch) >= 0
                    )// надо экранировку
                result.append(Utils.CH_WALL);
            result.append(ch); // сам символ
        }
        return result.toString();
    }

    /**
     * Выполнить вставку условия для проверки равенства поля указанной константе.
     * Экранированные кавычки для значения добавляются автоматически.
     *
     * @param prefix вставляется перед сгенерированным условием, если оно будет получено
     * @param fld    ссылка на поле (экранирование '-' и ':' не требуется)
     * @param value  значение или Null (генерации не будет в этом случае)
     * @return true, если условие было добавлено
     */
    public String emmitFieldCondition(final String prefix, final String fld, Object value) {
        if (value == null || fld == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        if (prefix != null) {
            sb.append(prefix);
        }
        sb.append(" @").append(luceneEncode(fld)).append(":\"").append(value.toString()).append("\"");
        return sb.toString();
    }

    protected String emmitOneTypeCond(final String typeName, String prefix, boolean strictCond) {
        if (typeName == null || typeName.trim().isEmpty()) {
            return null;
        }
        final QName qType = QName.createQName(typeName.trim().replace("{", "").replace("}", ""), services.getServiceRegistry().getNamespaceService());
        return emmitTypeCond(qType, prefix, strictCond);
    }

    /**
     * Добавить условие для проверки по типу
     *
     * @param typeNames требующийся тип или список типов, если NULL ничего не добавляется
     * @param prefix    префикс, добавляемый перед условием на тип  (например " AND")
     *                  , может быть NULL
     * @return true, если условие было добавлено
     */
    public String emmitTypeCond(final String typeNames, String prefix) {
        if (typeNames == null || typeNames.isEmpty()) {
            return null;
        }
        final String[] values = typeNames.split("\\s*[,;]\\s*");
        return emmitTypeCond(Arrays.asList(values), prefix);
    }

    public String emmitTypeCond(final Collection<String> typeNames, final String prefix) {
        if (typeNames == null || typeNames.isEmpty()) {
            return null;
        }

        if (typeNames.size() == 1) {
            // единичное значение присвоим без доп скобок ...
            return emmitOneTypeCond(typeNames.iterator().next(), prefix, true);
        }

        StringBuilder sb = new StringBuilder();
        if (prefix != null) {
            sb.append(prefix);
        }
        sb.append("("); // (!) экранируем выражение с проверкой двух и более типов и "плюсом" задаём строгое условие ...
        boolean first = true;
        for (String typeName : typeNames) {
            // (!) условие здесь не строгое - плюс выставлен перед скобкой
            sb.append(emmitOneTypeCond(typeName, (first ? "" : "OR "), false));
            first = false;
        }

        return sb.toString();
    }

    /**
     * Добавить условие для проверки по типу
     *
     * @param qType           требующийся тип, если NULL ничего не добавляется
     * @param prefix          префикс, добавляемый перед условием на тип  (например " AND")
     *                        , может быть NULL
     * @param strictCondition true, чтобы условие было строгим (добавляется "+" перед
     *                        типом), false иначе.
     */
    public String emmitTypeCond(final QName qType, final String prefix, final boolean strictCondition) {
        if (qType == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        if (prefix != null) {
            sb.append(prefix);
        }
        final String typeTag = (strictCondition) ? "+TYPE:" : " TYPE:";
        sb.append(typeTag).append(Utils.quoted(qType.toString()));
        return sb.toString();
    }

    /**
     * Сформировать условие для проверки значения на вхождение в список вида:
     * "( fld:value1 OR fld:value2 ...)"
     * (!) скобки включаются, операция между значениями "OR"
     *
     * @param fldName String
     * @param values  String[]
     * @return boolean
     */
    public String emmitValuesInsideList(String fldName, final String[] values) {
        if (!Utils.hasNonEmptyValues(values)) {
            return null;
        }

        final StringBuilder sb = new StringBuilder();
        final boolean isSpecialName = "TYPE ID".contains(fldName);
        sb.append("( ");
        boolean addOR = false;
        for (String value : values) {
            if (value != null && !value.isEmpty()) {
                final String quotedValue = Utils.quoted(value);
                if (addOR)
                    sb.append(" OR ");
                if (!isSpecialName) // добавление '@' требуется ТОЛЬКО для обычных полей
                    sb.append("@"); //
                sb.append(luceneEncode(fldName)).append(":").append(quotedValue);
                addOR = true;
            }
        }
        sb.append(") ");
        return sb.toString();
    }

    /**
     * Проверить, является ли ссылка простой.
     * Простой ссылкой считаем ссылки на конкретные поля в виде выражений "{abc}"
     *
     * @return true, если колонка содержит просто ссылку на поле
     */
    public boolean isDirectAlfrescoPropertyLink(final String expression) {
        return (expression != null) && (expression.length() > 0)
                && Utils.hasStartOnce(expression, SubstitudeBean.OPEN_SUBSTITUDE_SYMBOL) // есть певая "{" и она одна
                && Utils.hasEndOnce(expression, SubstitudeBean.CLOSE_SUBSTITUDE_SYMBOL) // есть последняя "}" и она одна
                && !expression.contains(SubstitudeBean.SPLIT_TRANSITIONS_SYMBOL) && !expression.contains(SubstitudeBean.PSEUDO_PROPERTY_SYMBOL) // нет символов "/"
                && !expression.contains(SubstitudeBean.EXPRESSION_SYMBOL);
    }

    /**
     * Проверить, является ли ссылка вычисляемым выражением "{{abc}}"
     *
     * @return true, если колонка содержит вычисляемое выражение
     */
    private boolean isProcessedFieldLink(final String expression) {
        return (expression != null) && (expression.length() > 0)
                && expression.matches(SearchQueryProcessorService.PROC_PATTERN.toString());
    }

    public boolean isSubstCalcExpr(final String expression) {
        return (expression != null) &&
                Utils.hasStartOnce(expression, SubstitudeBean.OPEN_SUBSTITUDE_SYMBOL)
                && Utils.hasEndOnce(expression, SubstitudeBean.CLOSE_SUBSTITUDE_SYMBOL);
    }

    public SearchQueryProcessorService getProcessorService() {
        return processorService;
    }
}
