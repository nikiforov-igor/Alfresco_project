package ru.it.lecm.contracts.reports;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.reports.calc.DataGroupCounter;
import ru.it.lecm.reports.generators.GenericDSProviderBase;
import ru.it.lecm.reports.jasper.AlfrescoJRDataSource;
import ru.it.lecm.reports.jasper.TypedJoinDS;
import ru.it.lecm.reports.utils.Utils;
import ru.it.lecm.statemachine.StatemachineModel;

import java.io.Serializable;
import java.util.*;

/**
 * Провайдер для построения отчёта «Сводный отчет по договорам»
 * <p/>
 * Фильтры по:
 * •	Вид договора
 * •	Тематика договора
 * •	Контрагент
 * •	Дата регистрации проекта (Период)
 * •	Дата договора  (задавать Период)
 * •	Сумма (интервал)
 * •	Инициатор
 * <p/>
 * Измерение (одно из):
 * •	Вид договора
 * •	Тематика договора
 * •	Контрагент
 * •	Инициатор
 * <p/>
 * НД заполняет колонки с именами "Col_Count1", "Col_Count2" и т.д. значениями
 * кол-ва соот-щих статусов.
 *
 * @author rabdullin
 */
public class DSProviderDocflowStatusCounters extends GenericDSProviderBase {

    private static final Logger logger = LoggerFactory.getLogger(DSProviderDocflowStatusCounters.class);

    /* XML параметры */
    final static String XMLSTATUSES_LIST = "statuses";
    final static String XMLSTATUS_FLAGS_MAP = "status.flags";
    final static String XMLGROUPBY_FORMATS_MAP = "groupby.formats";

    /**
     * надо ли формировать столбец с суммой по строке
     */
    final static String XMLSTATUS_FLAGS_ITEM_ROWSUM_SHOW = "rowSum.show";
    /**
     * название столбца содержащего сумму по строке
     */
    final static String XMLSTATUS_FLAGS_ITEM_ROWSUM_COLNAME = "rowSum.colName";

    /**
     * Формат названия колонки со счётчиками
     */
    final static String COLNAME_TAG = "col_RowTag";

    /**
     * колонка название выбранного пользователем измерения
     */
    final static String COLNAME_MEASURE_TAG = "col_MeasureTag";

    final static String COLNAME_STATUS_FMT = "col_Status%d"; // колонка с названием i-го статуса
    final static String COLNAME_COUNTER_FMT = "col_Count%d"; // колонка с количеством найденных строк в i-м статусе
    final static String COLVALUE_ALL_OTHER = "All other"; // "Все остальные статусы"

    /**
     * Вариант групировки.
     */
    private String groupBy;

    public DSProviderDocflowStatusCounters() {
        super();
    }

    /**
     * Задание варианта "Измерения"
     * Реальные значения см. "ds-docflow-timings.xml" (т.е. в мета-конфигурации для jrxml-отчёта)
     * Пример: "Вид договора", "Тематика договора" ...
     */
    @SuppressWarnings("unused")
    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }

    @Override
    protected void setXMLDefaults(Map<String, Object> defaults) {
        super.setXMLDefaults(defaults);
        defaults.put(XMLSTATUSES_LIST, null); // list
        defaults.put(XMLGROUPBY_FORMATS_MAP, null); // map
        defaults.put(XMLSTATUS_FLAGS_MAP, null); // map
    }

    @Override
    protected AlfrescoJRDataSource newJRDataSource(Iterator<ResultSetRow> iterator) {
        final DocflowJRDataSource result = new DocflowJRDataSource(iterator);
        result.getContext().setRegistryService(getServices().getServiceRegistry());
        result.getContext().setJrSimpleProps(jrSimpleProps);
        result.getContext().setMetaFields(conf().getMetaFields());
        result.buildJoin();
        return result;
    }

    /**
     * Контейнерный класс для подсчёта объектов по статусам (getAttrCounters())
     *
     * @author rabdullin
     */
    protected class DocStatusGroup extends DataGroupCounter {
        public DocStatusGroup(String groupTag) {
            super(groupTag);
        }
    }

    /**
     * Увеличение статистики для указанной группы (которой может быть "кол-во"
     * или "суммарная длительность", в зависимости от отчёта - и от этого будет
     * зависеть изменение). Здесь увеличение целого значения на единицу.
     *
     * @param group      наборр статусов, в котором надо вычсилить статистику
     * @param statusName статус для модификации статистики
     * @param docId      документ для которого надо модифицировать статистику
     * @return изменённое значение счётчика, соот-го статусу statusName
     */
    protected int adjustStatistic(final DocStatusGroup group, final String statusName, NodeRef docId) {
        return group.incCounter(statusName, 1); // простой счётчик
    }

    /**
     * Базовый класс для вычисления статистики
     *
     * @author rabdullin
     */
    private class DocflowJRDataSource extends TypedJoinDS<DocStatusGroup> {
        /**
         * Ключ здесь это название измерения (tag)
         */
        final protected Map<String, DocStatusGroup> groups = new LinkedHashMap<String, DocStatusGroup>();

        // формат и название выбранного способа группировки
        String fmtForTag, nameForTag;

        public DocflowJRDataSource(Iterator<ResultSetRow> iterator) {
            super(iterator);
        }

        /**
         * Увеличить счётчик связанный с указанным именованным объектом и статусом
         * (сущность счётчика зависит от целевого отчёта - может быть кол-во или
         * длительность, или что-то иное)
         * Воз-ся увеличенный счётчик.
         *
         * @param tag        название объекта-измерения (по нему ищется в groups соот-вие)
         * @param statusName статус для модификации статистики
         * @param docId      документ для которого надо модифицировать статистику
         */
        private DocStatusGroup incCounter(final String tag, final String statusName, final NodeRef docId) {
            final DocStatusGroup result;
            if (groups.containsKey(tag)) {
                result = groups.get(tag);
            } else { // создаём новую
                result = new DocStatusGroup(tag);
                groups.put(tag, result);
            }
            adjustStatistic(result, statusName, docId);
            return result;
        }

        @Override
        public int buildJoin() {
            // построить  в groups список объектов сгруппированных по названиям Измерения (ключ в groups)
            final ArrayList<DocStatusGroup> result = new ArrayList<DocStatusGroup>();
            /* Получение формата строки для выбранного Измерения из конфигурации ... */

            if (groupBy == null) {
                throw new RuntimeException("Too few parameters: 'groupBy' not assigned");
            }

            final Map<String, Object> mapGroupBy = conf().getMap(XMLGROUPBY_FORMATS_MAP);
            if (mapGroupBy == null) {
                throw new RuntimeException(String.format("Invalid configuration: no '%s' map provided", XMLGROUPBY_FORMATS_MAP));
            }

            if (!mapGroupBy.containsKey(groupBy)) {
                throw new RuntimeException(String.format("Invalid configuration: provided map '%s' not contains variant for demanded report groupBy order '%s'", XMLGROUPBY_FORMATS_MAP, groupBy));
            }

            fmtForTag = (String) mapGroupBy.get(groupBy);
            nameForTag = (String) mapGroupBy.get(groupBy + "Tag");

            if (getContext().getRsIter() != null) {
                final NodeService nodeSrv = getServices().getServiceRegistry().getNodeService();

                while (getContext().getRsIter().hasNext()) {
                    final ResultSetRow rs = getContext().getRsIter().next();

                    final NodeRef docId = rs.getNodeRef(); // id документа
                    if (getContext().getFilter() != null && !getContext().getFilter().isOk(docId)) {
                        logger.debug(String.format("Filtered out node %s", docId));
                        continue; // skip data row
                    }

					/* Название ... */
                    final String docTag = getServices().getSubstitudeService().formatNodeTitle(docId, fmtForTag);

					/* Статус */
                    final String statusName = (String) nodeSrv.getProperty(docId, StatemachineModel.PROP_STATUS);
                    incCounter(docTag, statusName, docId);
                } // while
            }

            result.addAll(this.groups.values());

            setData(result);
            setIterData(result.iterator());

            logger.info(String.format("found %s row(s)", result.size()));

            return result.size();
        }

        @Override
        protected Map<String, Serializable> getReportContextProps(DocStatusGroup item) {
            final Map<String, Serializable> result = new LinkedHashMap<String, Serializable>();

			/* Название ... */
            result.put(COLNAME_TAG, item.getGroupTag());
            result.put(COLNAME_MEASURE_TAG, nameForTag);

			/* Счётчики ... */
            @SuppressWarnings("unchecked")
            final List<String> statusOrderedList = conf().getList(XMLSTATUSES_LIST);

            int iCol = 0;
            for (String colName : statusOrderedList) {
                iCol++; // (!) нумерация от Единицы
                // "col_StatusNN": вносим в набор название статуса
                result.put(String.format(COLNAME_STATUS_FMT, iCol), colName);
                // "col_CountNN" = (Integer) счётчик в этом статусе
                result.put(String.format(COLNAME_COUNTER_FMT, iCol), item.getAttrCounters().get(colName));
            }

			/* последняя со статусами колонка будет состоять из всех значений, не вошедших в какие-либо предыдущие ... */
            iCol++;
            result.put(String.format(COLNAME_STATUS_FMT, iCol), COLVALUE_ALL_OTHER);
            result.put(String.format(COLNAME_COUNTER_FMT, iCol), item.sumAllOthers(statusOrderedList));

			/* Параметры формирования колонки с суммой данных по строке ... */
            final Map<String, Object> mapStatusFlags = conf().getMap(XMLSTATUS_FLAGS_MAP);
            if (mapStatusFlags != null) {
                // надо ли формировать столбец с суммой по строке
                final boolean enCalcRowSum = (mapStatusFlags.containsKey(XMLSTATUS_FLAGS_ITEM_ROWSUM_SHOW))
                        && "true".equalsIgnoreCase("" + mapStatusFlags.get(XMLSTATUS_FLAGS_ITEM_ROWSUM_SHOW));
                if (enCalcRowSum) {
                    // название столбца содержащего сумму по строке
                    final String rowSummaryColName = (mapStatusFlags.containsKey(XMLSTATUS_FLAGS_ITEM_ROWSUM_COLNAME))
                            ? "" + mapStatusFlags.get(XMLSTATUS_FLAGS_ITEM_ROWSUM_COLNAME)
                            : null;
                    iCol++;
                    result.put(Utils.coalesce(rowSummaryColName, String.format(COLNAME_COUNTER_FMT, iCol))
                            , item.sumAll());
                }
            }

            return result;
        }
    }
}