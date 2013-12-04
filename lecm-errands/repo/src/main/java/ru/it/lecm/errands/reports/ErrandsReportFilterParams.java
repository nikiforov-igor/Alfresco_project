package ru.it.lecm.errands.reports;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.service.cmr.repository.NodeRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.reports.model.impl.ColumnDescriptor;
import ru.it.lecm.reports.api.model.DataSourceDescriptor;
import ru.it.lecm.reports.jasper.config.JRDSConfigXML;
import ru.it.lecm.reports.utils.Utils;

/**
 * Параметры НД для построения отчёта по задачам - настройка фильтра данных.
 * <br/> Хранит название колонки НД с параметром-периодом и сконфигурированный
 * способ группировки (из ds-ABC.xml).
 * <br/>Способ группировки конфигурируется так:
 * <li>   1) формат ссылки </li>
 * <li>  2) атрибут-источник для группирования: </li>
 * <br/>в колонке данных DsDisciplineColumnNames.COL_PARAM_GROUP_BY должно
 * быть строковое значение с названием способа группировки. Это название
 * строго не регламентируется, но:<br/>
 * <li>1) оно должно быть описано двух xml-секциях:<br/>
 * <ul>"groupBy.formats" (ErrandsReportFilterParams.XMLGROUPBY_FORMATS_MAP)</ul>
 * <ul> и "groupBy.source" (ErrandsReportFilterParams.XMLGROUPBY_SOURCE_MAP)</ul>
 * </li>
 * <li> 2) для группировки по Подразделениям, название группы ДОЛЖНО СОДЕРЖАТЬ подстроку:
 * <ul>"OrgUnit" (DsDisciplineColumnNames.CONTAINS_GROUP_BY_OU) </ul>
 * </li>
 */
public class ErrandsReportFilterParams {

    private static final Logger logger = LoggerFactory.getLogger(ErrandsReportFilterParams.class);

    /**
     * XML Config-параметр с ссылками на assoc-атрибут в Задаче для выборки
     * группирующих значений:
     * Ключ = название группы, Значение = стд qname-ссылка вида "тип:атрибут"
     * (см также XMLGROUPBY_FORMATS_MAP - и там и здесь должны быть одинаковые Ключи)
     */
    final public static String XMLGROUPBY_SOURCE_MAP = "groupBy.source";

    /**
     * XML Config-параметр с форматами (для substBean), применяемыми для
     * получения группирующих значений:
     * Ключ = название группы, Значение = форматная строка для substituteBean
     * будет применяться к объектам, полученным через ассоциацию из "groupBy.source".
     * (см также XMLGROUPBY_SOURCE_MAP - и там и здесь должны быть одинаковые Ключи)
     */
    final public static String XMLGROUPBY_FORMATS_MAP = "groupBy.formats";

    final public static String XMLSTATUS_LIST_ENABLED = "status.enabled";
    final public static String XMLSTATUS_LIST_DISABLED = "status.disabled";

    /**
     * форматы и исходные поля для групп (ключ=название группы)
     */
    private LinkedHashMap<String, GroupByInfo> groupByMap;

    /**
     * Названия колонок с параметрами "GroupBy", "Period" и список подразделений
     */
    final private String colnameGroupBy, colnamePeriod, colnameOrgUnits;

    /**
     * Префикс значения колонки "GroupBy", которое будет означать использование
     * фильтрации по подразделениям
     */
    final private String valPrefixGroupByOU;

    Set<String> enabledStatuses, disabledStatuses;

    public ErrandsReportFilterParams(String colnamePeriod
            , String colnameGroupBy
            , String valPrefixGroupByOU
            , String colnameOrgUnits) {
        super();
        this.colnamePeriod = colnamePeriod;
        this.colnameGroupBy = colnameGroupBy;
        this.colnameOrgUnits = colnameOrgUnits;
        this.valPrefixGroupByOU = valPrefixGroupByOU;
    }

    /**
     * Название колонки с параметром список id Подразделений
     */
    public String getColnameOrgUnits() {
        return colnameOrgUnits;
    }

    private ColumnDescriptor getCheckedPeriodColumn(DataSourceDescriptor ds) {
        final ColumnDescriptor period = ds.findColumnByName(colnamePeriod);
        if (period == null || period.getParameterValue() == null) {
            logger.warn(String.format("Dataset not contains parameterized column '%s'", colnamePeriod));
        }
        return period;
    }

    /**
     * Получить параметр-начало периода
     */
    public Date getParamPeriodEnd(DataSourceDescriptor ds) {
        final ColumnDescriptor period = getCheckedPeriodColumn(ds);
        return (period != null && period.getParameterValue() != null) ? (Date) period.getParameterValue().getBound2() : null;
    }

    /**
     * Получить параметр-конец периода
     */
    public Date getParamPeriodStart(DataSourceDescriptor ds) {
        final ColumnDescriptor period = getCheckedPeriodColumn(ds);
        return (period != null && period.getParameterValue() != null) ? (Date) period.getParameterValue().getBound1() : null;
    }

    /**
     * @return ключ=название группы, значение = описатель
     */
    public Map<String, GroupByInfo> getGroupByMap() {
        return groupByMap;
    }

    /**
     * Проверить одходит ли указанный статус.
     * <br/> Значение Null не подходит всегда.
     */
    public boolean isStatusOk(String status) {
        return (status != null)
                && (enabledStatuses == null || enabledStatuses.contains(status))
                && (disabledStatuses == null || !disabledStatuses.contains(status));
    }

    /**
     * Контейнерный класс с инфой по группирующему объекту
     * Тип группировки задаётся колонокой (@link{COL_GROUP_BY}) в наборе данных
     */
    public class GroupByInfo {
        /**
         * Название, Форматная строка и ассоциативная ссылка на поле группировки
         */
        final public String grpName; // пример: byPerson | byOrgUnit
        String grpFmt; // пример: "Исполнитель {lecm-errands:executor-assoc/cm:name}"
        String grpAssocQName; // пример: "lecm-errands:executor-assoc"

        public GroupByInfo(String groupName) {
            this.grpName = groupName;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((grpName == null) ? 0 : grpName.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final GroupByInfo other = (GroupByInfo) obj;
            if (grpName == null) {
                if (other.grpName != null) {
                    return false;
                }
            } else if (!grpName.equals(other.grpName)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append("GroupByInfo ['");
            builder.append(grpName);
            builder.append("', assocQName {").append(grpAssocQName);
            builder.append("}, format '").append(grpFmt);
            builder.append("']");
            return builder.toString();
        }
    }


    /**
     * Получить характеристику groupBy согласно текущему состоянию НД
     *
     * @param ds набор данных
     */
    public GroupByInfo getCurrentGroupBy(DataSourceDescriptor ds) {
        String groupBy = null; // not found

        // поиск способа группировки в колонке НД ...
        final ColumnDescriptor colGroupBy = ds.findColumnByName(colnameGroupBy);
        if (colGroupBy != null) {
            groupBy = colGroupBy.getExpression();
            if (groupBy != null) {
                groupBy = groupBy.trim();
            }
        }

        if (Utils.isStringEmpty(groupBy)) {
            // не задан параметр группирования ... -> выбираем первый попавшийся

            if (groupByMap.isEmpty()) {
                // вообще не сконфигурировано - отвалиться ...
                throw new RuntimeException(String.format("GroupBy is not set (check data column '%s' value)", colnameGroupBy));
            }

            groupBy = groupByMap.keySet().iterator().next(); // первый элемент
            logger.warn(String.format("Column parameter '%s' is not present or has empty expression -> groupBy is set to '%s'", colnameGroupBy, groupBy));
        }

        final GroupByInfo result = groupByMap.get(groupBy);
        if (result == null) {
            final String msg = String.format("Invalid groupBy criteria '%s':\n\tColumn '%s' contains expression that was not defined at config sections '%s'/'%s'\n\tUsable defines are: %s"
                    , groupBy, colnameGroupBy, XMLGROUPBY_FORMATS_MAP, XMLGROUPBY_SOURCE_MAP, groupByMap);
            logger.error(msg);
            throw new RuntimeException(msg);
        }

        return result;
    }

    /**
     * Загрузка параметром группирования из конфигурации
     */
    public void scanGroupByInfo(JRDSConfigXML config) {
        if (config == null) {
            return;
        }

        this.groupByMap = new LinkedHashMap<String, GroupByInfo>();

         /* Название-Формат */
        final Map<String, Object> grpFormats = config.getMap(XMLGROUPBY_FORMATS_MAP);
        if (grpFormats != null) {
            for (Map.Entry<String, Object> entry : grpFormats.entrySet()) {
                if (!this.groupByMap.containsKey(entry.getKey())) {
                    // новый элемент ...
                    this.groupByMap.put(entry.getKey(), new GroupByInfo(entry.getKey()));
                }
                this.groupByMap.get(entry.getKey()).grpFmt = Utils.coalesce(entry.getValue(), "");
            }
        }


         /* Название-Источник */
        final Map<String, Object> grpAssocs = config.getMap(XMLGROUPBY_SOURCE_MAP);
        if (grpAssocs != null) {
            for (Map.Entry<String, Object> entry : grpAssocs.entrySet()) {
                if (!this.groupByMap.containsKey(entry.getKey())) {
                    // новый элемент здесь выглядит странно ...
                    logger.warn(String.format("Config section '%s' contains item '%s' that was not present at section '%s'"
                            , XMLGROUPBY_SOURCE_MAP, entry.getKey(), XMLGROUPBY_FORMATS_MAP));
                    this.groupByMap.put(entry.getKey(), new GroupByInfo(entry.getKey()));
                }
                this.groupByMap.get(entry.getKey()).grpAssocQName = Utils.coalesce(entry.getValue(), "");
            }
        }


        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Config loaded for groupBy:\n%s", this.groupByMap));
        }
    }

    /**
     * Загрузка параметром группирования из конфигурации
     */
    @SuppressWarnings("unchecked")
    public void scanStatuses(JRDSConfigXML config) {
        if (config == null) {
            return;
        }

        List<String> list;
        {
            list = config.getList(XMLSTATUS_LIST_ENABLED);
            this.enabledStatuses = (list == null || list.isEmpty()) ? null : new HashSet<String>(list);
        }
        {
            list = config.getList(XMLSTATUS_LIST_DISABLED);
            this.disabledStatuses = (list == null || list.isEmpty()) ? null : new HashSet<String>(list);
        }

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Config status lists loaded:\n\t enabled statuses: %s\n\t disabled statuses: %s"
                    , this.enabledStatuses, this.disabledStatuses));
        }
    }

    /**
     * Получить список Подразделений организации, по которым надо фильтровать ...
     *
     * @param ds              набор данных
     * @param colNameOrgUnits название колонки с заданными id подразделений
     */
    public static String getOUNodesFromParams(DataSourceDescriptor ds, String colNameOrgUnits) {
        final ColumnDescriptor colOU = ds.findColumnByParameter(colNameOrgUnits);
        if (colOU != null && colOU.getParameterValue() != null) {
            final Object val = colOU.getParameterValue().getBound1();
            if (val != null) {
                final String result;
                if (val instanceof String) {
                    result = (String) val;
                } else if (val instanceof String[]) {
                    final String[] arr = (String[]) val;
                    result = (arr.length > 0) ? arr[0] : null;
                } else {
                    result = val.toString();
                }

                if (result != null && result.trim().length() > 0) {
                    return result.trim(); // (!) FOUND non-empty
                }
            }
        }
        return null; // not present or is empty
    }

    /**
     * Вернуть для НД его текущеу состояние параметра "groupBy"
     */
    public DSGroupByInfo findGroupByInfo(DataSourceDescriptor ds) {
        final GroupByInfo groupByInfo = getCurrentGroupBy(ds);

        // использовать фильтр по организациям, если указано значение "groupBy"
        // с подтекстом valPrefixGroupByOU ...
        final boolean useOUFilter = (groupByInfo != null)
                && Utils.nonblank(groupByInfo.grpName, "").toLowerCase().contains(this.valPrefixGroupByOU.toLowerCase());

        final String nodesIdsLine = (useOUFilter) ? getOUNodesFromParams(ds, this.colnameOrgUnits) : null;

        return new DSGroupByInfo(groupByInfo, useOUFilter, nodesIdsLine);
    }

    /**
     * Контейнерный класс для описания состояния группирования в наборе данных
     */
    public class DSGroupByInfo {
        final private GroupByInfo groupByInfo;
        final private boolean useOUFilter;
        final private String nodesIdsLine;

        private DSGroupByInfo(GroupByInfo groupByInfo, boolean useOUFilter, String nodesIdsLine) {
            super();
            this.groupByInfo = groupByInfo;
            this.useOUFilter = useOUFilter;
            this.nodesIdsLine = Utils.nonblank(nodesIdsLine, null); // пустые строки воспринимаем как Null
        }

        /**
         * Используемая группировка
         */
        public GroupByInfo getGroupByInfo() {
            return groupByInfo;
        }

        /**
         * true, если используется группировка по Подразделениям
         */
        public boolean isUseOUFilter() {
            return useOUFilter;
        }

        /**
         * Строка со списком (через запятую) id подразделений.
         * Значение null означает отсуствие фильтра по Подразделениям (т.е. "любое подразделение")
         * (имеет смысл когда useOUFilter=true, иначе всегда null)
         */
        public String getNodesIdsLine() {
            return (useOUFilter) ? nodesIdsLine : null;
        }

        /**
         * Проверить, подходит ли указанное Подразделение под условия группового фильтра
         * (если фильтр по Подразделениям не указан - подходит любое подразделение (даже unitID = null)
         * , если указан списком, то проверяется, чтобы unitId имелся в этом списке)
         *
         * @param unitId id Подразделения, значение null никогда не походит (return всегда будет false)
         * @return true, если подходит
         */
        public boolean isOUEnabled(NodeRef unitId) {
            final String filter = getNodesIdsLine();
            return (filter == null) // фильтрование не задано -> пдходит любое значение OU
                    || (
                    (unitId != null) // должен быть Id указан
                            && filter.contains(unitId.getId()) // или id перечислен в фильтре
            );
        }
    } // DSGroupByInfo
}
