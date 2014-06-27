package ru.it.lecm.errands.reports;

import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.errands.ErrandsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.reports.calc.AvgValue;
import ru.it.lecm.reports.calc.DataGroupCounter;
import ru.it.lecm.reports.generators.GenericDSProviderBase;
import ru.it.lecm.reports.jasper.AlfrescoJRDataSource;
import ru.it.lecm.reports.jasper.TypedJoinDS;
import ru.it.lecm.reports.jasper.containers.BasicEmployeeInfo;
import ru.it.lecm.reports.utils.Utils;

import java.io.Serializable;
import java.util.*;

/**
 * Исполнительская дисциплина по исполнителям
 * Фильтры отчета:
 * •	За Период
 * •	Исполнители
 * o	Подразделения
 * o	Сотрудники
 * Выводимые значения:
 * •	Выдано поручений за период (из них важных)
 * •	Закрытых поручений за период (из них важных)
 * •	Процент исполнения в срок
 * •	Процент поручений, отклоненных руководителем
 * •	Процент (количество) важных поручений, неисполненных в срок
 * •	Среднее время исполнения поручения
 *
 * @author rabdullin
 */
public class ErrandsDisciplineDSProvider extends GenericDSProviderBase {

    private static final Logger logger = LoggerFactory.getLogger(ErrandsDisciplineDSProvider.class);

    /**
     * Способ группировки элементов определяется параметрами отчёта
     * Конфигурируется:
     * 1) формат ссылки
     * 2) атрибут-источник для группирования:
     * в колонке данных DsDisciplineColumnNames.COL_PARAM_GROUP_BY должно
     * быть строковое значение с названием способа группировки. Это название
     * строго не регламентируется, но:
     * 1) оно должно быть описано двух xml-секциях:
     * ErrandsReportFilterParams.XMLGROUPBY_FORMATS_MAP ("groupBy.formats")
     * и ErrandsReportFilterParams.XMLGROUPBY_SOURCE_MAP ("groupBy.source")
     * 2) для группировки по Подразделениям, оно ДОЛЖНО СОДЕРЖАТЬ подстроку:
     * DsDisciplineColumnNames.CONTAINS_GROUP_BY_OU ("OrgUnit")
     */
    final private ErrandsReportFilterParams paramsFilter = new ErrandsReportFilterParams(null,
            DsDisciplineColumnNames.COL_PARAM_GROUP_BY
            , DsDisciplineColumnNames.CONTAINS_GROUP_BY_OU
            , DsDisciplineColumnNames.COL_PARAM_EXEC_ORGUNIT
    );

    /**
     * для упрощения работы с QName-объектами
     */
    private LocalQNamesHelper _qnames;

    final protected LocalQNamesHelper qnames() {
        if (this._qnames == null) {
            this._qnames = new LocalQNamesHelper(this.getServices().getServiceRegistry().getNamespaceService());
        }
        return this._qnames;
    }


    @Override
    protected void setXMLDefaults(Map<String, Object> defaults) {
        super.setXMLDefaults(defaults);
        defaults.put(ErrandsReportFilterParams.XMLGROUPBY_FORMATS_MAP, null);
        defaults.put(ErrandsReportFilterParams.XMLGROUPBY_SOURCE_MAP, null);
    }

    @Override
    protected AlfrescoJRDataSource newJRDataSource(Iterator<ResultSetRow> iterator) {
        return new ExecDisciplineJRDataSource(iterator);
    }

    /**
     * Коды/Названия колонок в наборе данных для отчёта.
     */
    final private static class DsDisciplineColumnNames {

        /**
         * Колонка "Группировка" - по Подразделениям или по Исполнителям
         */
        final static String COL_PARAM_GROUP_BY = "Col_GroupBy"; // String значение долно быть в секцииях конфы "groupBy.xxx"
        final static String CONTAINS_GROUP_BY_OU = "OrgUnit"; // подстрока, которая означает группировку по организации

        /**
         * Колонка "Cотрудник"
         * (!) Реальное содержимое определяется настройкой отчёта - Инициатор или Исполнитель
         */
        final static String COL_PARAM_EXEC_PERSON = "Col_Param_Person"; // alfrescoType="lecm-orgstr:employee", PARAM_TYPE_LIST

        /**
         * Колонка "Исполняющее подразделение"
         */
        final static String COL_PARAM_EXEC_ORGUNIT = "Col_Param_Exec_OrgUnit"; // alfrescoType="lecm-links:link", PARAM_TYPE_LIST

        /**
         * Колонка результата "Название" в строке данных
         */
        final static String COL_NAMEATAG = "Col_NameTag"; // String

        /**
         * Колонка "Выдано всего поручений за период"
         */
        final static String COL_COUNT_TOTAL = "Col_Count_Total"; // "java.lang.Integer"

        /**
         * Колонка "Выдано важных поручений за период"
         */
        final static String COL_COUNT_TOTAL_IMPORTANT = "Col_Count_Total_Important"; // "java.lang.Integer"

        /**
         * Колонка "Всего закрытых поручений за период"
         */
        final static String COL_COUNT_CLOSED = "Col_Count_Closed"; // "java.lang.Integer"

        /**
         * Колонка "Закрытых важных поручений за период"
         */
        final static String COL_COUNT_CLOSED_IMPORTANT = "Col_Count_Closed_important"; // "java.lang.Integer"

        /**
         * Колонка "Выполнено в срок"
         */
        final static String COL_COUNT_INTIME = "Col_Count_Intime"; // int

        /**
         * Колонка "Кол-во поручений, отклонённых руководителем"
         */
        final static String COL_COUNT_BOSS_REFUSED = "Col_Count_Boss_Refused"; // int

        /**
         * Колонка "Кол-во важных поручений, неисполненных в срок"
         */
        final static String COL_COUNT_IMPORTANT_REFUSED = "Col_Count_Important_Refused"; // java.lang.Integer

        /**
         * Колонка "Среднее время исполнения поручения"
         */
        final static String COL_AVG_EXECUTION = "Col_Avg_Execution.Value"; // java.lang.Float
    }

    /**
     * QName-ссылки на данные Альфреско *************************************
     */
    private class LocalQNamesHelper extends ErrandsQNamesHelper {
        /**
         * Параметр отчёта в НД: Исполнитель, Инициатор или Подразделение, по
         * которому фактически будет выполняться группировка ...
         */
        QName QN_ASSOC_REF;

        LocalQNamesHelper(NamespaceService ns) {
            super(ns);
            this.QN_ASSOC_REF = ErrandsService.ASSOC_ERRANDS_EXECUTOR; // by default = по Исполнителю
        }

        public void setQN_ASSOC_REF(String assocQName) {
            this.QN_ASSOC_REF = super.makeQN(assocQName); // QName.createQName(assocQName, ns);
        }

    }

    /**
     * Структура для хранения данных о статистике по Сотруднику
     */
    protected class DisciplineGroupInfo {

        final private BasicEmployeeInfo employee;

        /* Счётчики данной персоны */
        final DataGroupCounter counters;

        /**
         * Среднее время исполнения, часов
         */
        final AvgValue avgExecTimeInDays = new AvgValue("Avg exec time, h");

        public DisciplineGroupInfo(BasicEmployeeInfo empl) {
            this.employee = empl;
            final NodeRef employeeId = empl.employeeId;

            // регим атрибуты ...
            this.counters = new DataGroupCounter((employeeId != null) ? employeeId.getId() : "");
            // числовые колонки
            this.counters.regAttributes(
                    DsDisciplineColumnNames.COL_COUNT_TOTAL, DsDisciplineColumnNames.COL_COUNT_TOTAL_IMPORTANT
                    , DsDisciplineColumnNames.COL_COUNT_CLOSED, DsDisciplineColumnNames.COL_COUNT_CLOSED_IMPORTANT
                    , DsDisciplineColumnNames.COL_COUNT_INTIME, DsDisciplineColumnNames.COL_COUNT_BOSS_REFUSED
                    , DsDisciplineColumnNames.COL_COUNT_IMPORTANT_REFUSED);
        }

        public void registerDuration(long duration_ms) {
            if (duration_ms <= 0) {
                // нельзя определить
                return;
            }
            final float fact = Utils.getDurationInDays(duration_ms);
            this.avgExecTimeInDays.adjust(fact);
        }
    }

    /**
     * Jasper-НД для вычисления статистики
     */
    private class ExecDisciplineJRDataSource extends TypedJoinDS<DisciplineGroupInfo> {

        private ErrandsReportFilterParams.DSGroupByInfo groupBy; // способ группировки, заполняется внутри buildJoin

        public ExecDisciplineJRDataSource(Iterator<ResultSetRow> iterator) {
            super(iterator);
        }

        /**
         * Получить характерное для текущей группировки название объекта:
         * название Подразделения или инициалы Сотрудника.
         */
        String getItemName(DisciplineGroupInfo item) {
            return (groupBy.isUseOUFilter() ? item.employee.unitName : item.employee.ФамилияИО());
        }

        /**
         * Прогрузить строку отчёта
         */
        @Override
        protected Map<String, Serializable> getReportContextProps(DisciplineGroupInfo item) {
            final Map<String, Serializable> result = new LinkedHashMap<String, Serializable>();

			/* Название подразделения или имя пользователя ... */
            result.put(DsDisciplineColumnNames.COL_NAMEATAG, getItemName(item));

            result.put(DsDisciplineColumnNames.COL_PARAM_GROUP_BY, this.groupBy.getGroupByInfo().grpName);

			/* Сотрудник и его Подразделение */
            result.put(DsDisciplineColumnNames.COL_PARAM_EXEC_PERSON, item.employee.employeeId);
            result.put(DsDisciplineColumnNames.COL_PARAM_EXEC_ORGUNIT, item.employee.unitId);

			/* Счётчики ... */
            // Имена колонок совпадают с названиями счётчиков
            for (Map.Entry<String, Integer> entry : item.counters.getAttrCounters().entrySet()) {
                result.put(entry.getKey(), entry.getValue());
            }

			/* Среднее время исполнения */
            result.put(DsDisciplineColumnNames.COL_AVG_EXECUTION, item.avgExecTimeInDays.getAvg());

			/* (!) Проценты вычисляются непосредственно в jasper-отчёте */

            return result;
        }


        @Override
        public int buildJoin() {
            // построить  в groups список объектов сгруппированных по названиям Измерения (ключ в groups)
            // Ключ здесь это Сотрудник или Подразделение, в ~ от группировки (см this.useOUFilter)
            final Map<NodeRef, DisciplineGroupInfo> result = new HashMap<NodeRef, DisciplineGroupInfo>();

            paramsFilter.scanGroupByInfo(conf());

            if (context.getRsIter() != null) {
                final NodeService nodeSrv = getServices().getServiceRegistry().getNodeService();

				/* Получение формата и ссылки для выбранного groupby-Измерения из конфигурации ... */
                this.groupBy = paramsFilter.findGroupByInfo(getReportDescriptor().getDsDescriptor());
                qnames().setQN_ASSOC_REF(groupBy.getGroupByInfo().grpAssocQName); // задать название ассоциации для получения Инициаторов или Подразделений

                final OrgstructureBean beanOU;
                if (this.groupBy.isUseOUFilter()) {
                    beanOU = getServices().getOrgstructureService();
                    logger.info(String.format("group by OU, filter is [%s]", Utils.nonblank(this.groupBy.getNodesIdsLine(), "*")));
                } else {
                    beanOU = null;
                    logger.info("group by executors");
                }

				/* проход по всем загруженным Поручениям ... */
                while (context.getRsIter().hasNext()) {
                    final ResultSetRow rs = context.getRsIter().next();
                    final NodeRef errandId = rs.getNodeRef(); // id Поручения

                    // (!) Фильтрование
                    if (context.getFilter() != null && !context.getFilter().isOk(errandId)) {
                        if (logger.isDebugEnabled())
                            logger.debug(String.format("{%s} filtered out", errandId));
                        continue;
                    }

                    // Исполнители
                    final List<AssociationRef> employees = nodeSrv.getTargetAssocs(errandId, qnames().QN_ASSOC_REF);
                    if (employees == null || employees.isEmpty()) {// (!?) с Поручением не связан никакой Сотрудник-Исполнитель ...
                        logger.warn(String.format("No execution employee found for errand item %s", errandId));
                        continue;
                    }

                    // прогружаем атрибуты Поручения и корректируем данные ...
                    final Map<QName, Serializable> props = nodeSrv.getProperties(errandId);

                    for (AssociationRef employee : employees) {
                        final NodeRef executorId = employee.getTargetRef(); // id Сотрудника-Исполнителя
                        final BasicEmployeeInfo execEmployee = new BasicEmployeeInfo(executorId);

                        // грузим данные по подразделениям, только если надо по
                        // ним группировать (указав beanOU != null)
                        execEmployee.loadProps(nodeSrv, beanOU);

                        if (!this.groupBy.isOUEnabled(execEmployee.unitId)) { // фильтрование по ID Подразделения
                            logger.info(String.format("Filtered out OU '%s' for executor %s '%s'", execEmployee.unitName, execEmployee.staffName, execEmployee.ФамилияИО()));
                            continue; // for i
                        }

                        // ипользуем как ключ либо Сотрудника, либо его Подразделение ...
                        // TODO: иметь в виду несколько должностей Сотрудников и вложенность подразделений
                        final NodeRef keyId = (this.groupBy.isUseOUFilter()) ? execEmployee.unitId : execEmployee.employeeId;

                        final DisciplineGroupInfo executor;
                        if (result.containsKey(keyId)) {
                            executor = result.get(keyId);
                        } else { // создание нового Сотрудника-Исполнителя
                            executor = new DisciplineGroupInfo(execEmployee);
                            result.put(keyId, executor);
                        }

                        // среднее время исполнения ...
                        // [ props.get(QNFLD_START_DATE), props.get(QNFLD_END_DATE) ]
                        executor.registerDuration(qnames().getErrandExecutionTime(props));

						/* остальные счётчики ... */
                        executor.counters.incCounter(DsDisciplineColumnNames.COL_COUNT_TOTAL); // общее кол-во

                        final boolean isImportant = qnames().isErrandImportant(props);
                        final boolean isInDate = qnames().isErrandExpired(props);

                        if (isImportant) {
                            executor.counters.incCounter(DsDisciplineColumnNames.COL_COUNT_TOTAL_IMPORTANT);
                        }

                        if (qnames().isErrandClosed(props)) {
                            executor.counters.incCounter(DsDisciplineColumnNames.COL_COUNT_CLOSED);
                            if (isImportant) {
                                executor.counters.incCounter(DsDisciplineColumnNames.COL_COUNT_CLOSED_IMPORTANT);
                            }
                            if (isInDate) {
                                executor.counters.incCounter(DsDisciplineColumnNames.COL_COUNT_INTIME); // исполнено isInDate
                            }
                        } else { // Поручение НЕ Закрыто
                            if (isImportant && !isInDate) {
                                // isImportant И неисполненное в срок ...
                                executor.counters.incCounter(DsDisciplineColumnNames.COL_COUNT_IMPORTANT_REFUSED);
                            }
                        }
                        if (qnames().isErrandsRejected(props)) {
                            executor.counters.incCounter(DsDisciplineColumnNames.COL_COUNT_BOSS_REFUSED);
                        }
                    }
                } // while

                // (!) перенос в основной блок
                this.setData(getSortedItemsList(result.values()));
            } // if

            if (this.getData() != null) {
                this.setIterData(this.getData().iterator());
            }

            logger.info(String.format("found %s row(s)", result.size()));

            return result.size();
        }

        private List<DisciplineGroupInfo> getSortedItemsList(Collection<DisciplineGroupInfo> rawItems) {
            final ArrayList<DisciplineGroupInfo> result = new ArrayList<DisciplineGroupInfo>();
            if (rawItems != null) {
                result.addAll(rawItems);
                // сортировка по Алфавиту ...
                Collections.sort(result, new GrpComparator(this));
            }
            return result;
        }

        final private class GrpComparator implements Comparator<DisciplineGroupInfo> {

            final ExecDisciplineJRDataSource ds;

            public GrpComparator(ExecDisciplineJRDataSource ds) {
                super();
                this.ds = ds;
            }

            @Override
            public int compare(DisciplineGroupInfo o1, DisciplineGroupInfo o2) {
                final String s1 = ds.getItemName(o1);
                final String s2 = ds.getItemName(o2);
                return (s1 == null)
                        ? (s2 == null ? 0 : 1)
                        : (s2 == null ? -1 : s1.compareToIgnoreCase(s2));
            }
        }
    }
}
