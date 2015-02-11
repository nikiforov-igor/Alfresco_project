package ru.it.lecm.errands.reports;

import net.sf.jasperreports.engine.JRException;
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
import ru.it.lecm.reports.api.ReportDSContext;
import ru.it.lecm.reports.api.model.DataSourceDescriptor;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.generators.GenericDSProviderBase;
import ru.it.lecm.reports.jasper.AlfrescoJRDataSource;
import ru.it.lecm.reports.jasper.TypedJoinDS;
import ru.it.lecm.reports.jasper.containers.BasicEmployeeInfo;
import ru.it.lecm.reports.jasper.utils.JRUtils;
import ru.it.lecm.reports.model.impl.ColumnDescriptor;
import ru.it.lecm.reports.utils.Utils;
import ru.it.lecm.reports.xml.DSXMLProducer;
import ru.it.lecm.statemachine.StatemachineModel;
import ru.it.lecm.utils.LuceneSearchWrapper;

import java.io.Serializable;
import java.util.*;

/**
 * Отчёты для ОПГ: Исполнение задач в подразделениях по Сотрудникам.
 * <li>   1) Задачи, по которым вышел срок исполнения
 * <li>   2) Исполненные задачи
 * <br/> Фильтр:
 * <li>  •	Подразделение
 * <br/>
 * <br/> В отчет должны попадать задачи, для которых выполняются следующие условия:
 * <li> •	Задачи не находится в финальном статусе или находится в финальном статусе «Не исполнено»
 * <li> •	атрибут Просрочено = true
 * <li> •	Сотрудник выбранного в фильтре Подразделения является Исполнителем по задаче.
 * <br/>
 * <br/> Выводить требуется:
 * <li>   первый столбец: ФИО Сотрудников
 * <li>   второй столбец - Названия отобранных для этого Сотрудника задач
 * <br/>
 * <br/> По Сотруднику и Задачам доступно конфигурирование формата.
 * <br/> Разрешённые/запрещённые статусы конфигурируются отдельными списками.
 * Отсутствие списка означает снятие соответствующих ограничений.
 * <br/> см также {@link ErrandsDisciplineDSProvider}
 *
 * @author rabdullin
 */
public class ErrandsExecutionsDSProvider extends GenericDSProviderBase {

    private static final Logger logger = LoggerFactory.getLogger(ErrandsExecutionsDSProvider.class);

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
    final private ErrandsReportFilterParams paramsFilter = new ErrandsReportFilterParams(
            DsExecСolumnNames.COL_PARAM_PERIOD
            , DsExecСolumnNames.COL_PARAM_GROUP_BY
            , DsExecСolumnNames.CONTAINS_GROUP_BY_OU
            , DsExecСolumnNames.COL_PARAM_EXEC_ORGUNIT
    );

    final static String CM_NAME = "{cm:name}";

    /**
     * для упрощения работы с QName-объектами
     */
    private LocalQNamesHelper _qnames;
    private Date periodStart, periodEnd; // устанавливаются внутри buildQuery
    private String fmtNameTag, fmtTitle; // subst-форматы для формирования отображаемых данных для сотрудника и задачи


    final protected LocalQNamesHelper qnames() {
        if (this._qnames == null) {
            this._qnames = new LocalQNamesHelper(this.getServices().getServiceRegistry().getNamespaceService());
        }
        return this._qnames;
    }


    @Override
    protected void setXMLDefaults(Map<String, Object> defaults) {

        defaults.put(ErrandsReportFilterParams.XMLGROUPBY_FORMATS_MAP, null);
        defaults.put(ErrandsReportFilterParams.XMLGROUPBY_SOURCE_MAP, null);

        // статусы
        defaults.put(ErrandsReportFilterParams.XMLSTATUS_LIST_ENABLED, null);
        defaults.put(ErrandsReportFilterParams.XMLSTATUS_LIST_DISABLED, null);
    }


    private void loadConfig() {
        try {
            getConfigXML().setConfigName(DSXMLProducer.makeDsConfigFileName(this.getReportDescriptor().getMnem()));
            getConfigXML().loadConfig();
            this.paramsFilter.scanGroupByInfo(getConfigXML());
            this.paramsFilter.scanStatuses(getConfigXML());
        } catch (JRException e) {
            logger.error(e.getMessage(), e);
        }
    }

    protected AlfrescoJRDataSource createDS(ReportDescriptor descriptor, ReportDSContext parentContext) {
        this.setReportDescriptor(descriptor);
        LuceneSearchWrapper alfrescoQuery = execQuery(descriptor, parentContext);
        if (alfrescoQuery == null || alfrescoQuery.getSearchResults() == null) {
            return null;
        }

        Iterator<ResultSetRow> iterator = alfrescoQuery.getSearchResults().iterator();

        ExecTaskJRDataSource result = new ExecTaskJRDataSource(this);
        result.getContext().setRegistryService(getServices().getServiceRegistry());
        result.getContext().setJrSimpleProps(getSimpleColumnNames(this.getReportDescriptor()));
        result.getContext().setMetaFields(JRUtils.getDataFields(this.getReportDescriptor()));
        result.getContext().setResolver(this.getResolver());
        result.getContext().setRsIter(iterator);

        // фильтр данных ...
        result.getContext().setFilter(getDataFilter(alfrescoQuery));
        return result;
    }

    protected LuceneSearchWrapper execQuery(ReportDescriptor descriptor, ReportDSContext parentContext) {
        loadConfig();
        return super.execQuery(descriptor, parentContext);
    }


    @Override
    protected LuceneSearchWrapper buildQuery(ReportDescriptor descriptor, ReportDSContext parentContext) {
        final NamespaceService namespaceService = getServices().getServiceRegistry().getNamespaceService();

        final LuceneSearchWrapper builder = super.buildQuery(descriptor, parentContext);

        final DataSourceDescriptor ds = getReportDescriptor().getDsDescriptor();

        final ColumnDescriptor col = ds.findColumnByName(DsExecСolumnNames.COL_PARAM_OUTOFDATE);
        if (col != null) { // если имеется колонка для просроченности - выбираем её значение
            if (col.getParameterValue() == null && col.getExpression() != null) {
                // если это НЕ параметр (т.к. параметры отрабатываются
                // внутри super.buildQuery), и задано выражение - активируем
                // соот-щее условие атрибута "просроченности"...
                final boolean flag = Boolean.parseBoolean(col.getExpression().trim());

                String condition = getQueryHelper().emmitFieldCondition((!builder.isEmpty() ? " AND " : null), ErrandsQNamesHelper.QNFLD_IS_EXPIRED.toPrefixString(namespaceService), flag);
                builder.emmit(condition);
            }
        }

		/*
         * Критерий двойной:
		 * 		Время Начала
		 * 		или Время Окончания заданы внутри указанного интервала
		 * Формируется в виде
		 * 		"AND ( start_inside OR end_inside )"
		 */
        this.periodStart = paramsFilter.getParamPeriodStart(ds);
        this.periodEnd = paramsFilter.getParamPeriodEnd(ds);

        // "Дата исполнения" внутри периода ...
        final String condEnd = getQueryHelper().emmitDateIntervalCheck(ErrandsService.PROP_ERRANDS_EXECUTION_DATE.toPrefixString(namespaceService), this.periodStart, this.periodEnd);
        final boolean hasEnd = !Utils.isStringEmpty(condEnd);
        if (hasEnd) {
            final String cond = String.format(" (%s)\n\t", condEnd);
            builder.emmit(!builder.isEmpty() ? " AND " : "").emmit(cond);
        }

        return builder;
    }


    /**
     * Коды/Названия колонок в наборе данных для отчёта.
     */
    final private static class DsExecСolumnNames {

        /**
         * Колонка "Группировка" - по Подразделениям или по Исполнителям
         */
        final static String COL_PARAM_GROUP_BY = "Col_GroupBy"; // String значение долно быть в секцииях конфы "groupBy.xxx"
        final static String CONTAINS_GROUP_BY_OU = "OrgUnit"; // подстрока, которая означает группировку по организации
        final static String GROUP_BY_FORMAT_OF_TITLE = "taskTitle"; // название item в groupBy.formats с форматом для заголовка выводимого в отчёт значения (названия задачи)

        /**
         * Период с ... по ...
         */
        final static String COL_PARAM_PERIOD = "Col_Period"; // date, PARAM_DELTA
        final static String COL_PERIOD_START = "Col_Period.Start"; // date, const fact value
        final static String COL_PERIOD_END = "Col_Period.End"; // date, const fact value

        final static String COL_PARAM_OUTOFDATE = "Col_Param_OutOfDate"; // boolean, просроченость

        /**
         * Колонка "Cотрудник"
         * (!) Реальное содержимое определяется настройкой отчёта - Инициатор или Исполнитель
         */
        // final static String COL_PARAM_EXEC_PERSON = "Col_Param_Exec_Person"; // alfrescoType="lecm-orgstr:employee", PARAM_TYPE_LIST

        /**
         * Колонка "Исполняющее подразделение"
         */
        final static String COL_PARAM_EXEC_ORGUNIT = "Col_Param_Exec_OrgUnit"; // alfrescoType="lecm-links:link", PARAM_TYPE_LIST

        /**
         * Колонка результата "Название" в строке данных
         */
        final static String COL_NAME_TAG = "Col_NameTag"; // String

        /**
         * Колонка "Задачи"
         */
        final static String COL_TASK_TITLE = "Col_TaskTitle"; // String
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
        QName QN_FLD_DOCSTATUS;

        LocalQNamesHelper(NamespaceService ns) {
            super(ns);
            this.QN_ASSOC_REF = ErrandsService.ASSOC_ERRANDS_EXECUTOR; // by default = по Исполнителю
            this.QN_FLD_DOCSTATUS = StatemachineModel.PROP_STATUS;
        }

        public void setQN_ASSOC_REF(String assocQName) {
            this.QN_ASSOC_REF = super.makeQN(assocQName); // QName.createQName(assocQName, ns);
        }

    }

    /**
     * Структура для хранения данных о Задачах Сотрудника
     */
    protected class ExecutorGroupInfo {

        final BasicEmployeeInfo employee;
        final String tag; // тэг для вывода в отчёт

        /* Заголовки задачь, выбранных для данной персоны */
        final List<String> tasksTitles = new ArrayList<String>();

        public ExecutorGroupInfo(BasicEmployeeInfo empl) {
            this.employee = empl;

            final NodeRef employeeId = empl.employeeId;
            this.tag = (fmtNameTag != null)
                    ? getServices().getSubstitudeService().formatNodeTitle(employeeId, fmtNameTag)
                    : null;
        }

        public void registerTask(NodeRef taskId) {
            final String taskTag = getServices().getSubstitudeService().formatNodeTitle(taskId, fmtTitle);
            registerTask(!Utils.isStringEmpty(taskTag) ? taskTag : getServices().getSubstitudeService().formatNodeTitle(taskId, CM_NAME));
        }

        public void registerTask(String taskTag) {
            tasksTitles.add(taskTag);
        }

    }

    /**
     * Описание задачи - одна строка выходного НД
     */
    protected class TaskInfo {

        final String tagEmployee, tagTaskTitle, orgUnitName;

        TaskInfo(String tagEmployee, String tagTaskTitle, String ouName) {
            super();
            this.tagEmployee = tagEmployee;
            this.tagTaskTitle = tagTaskTitle;
            this.orgUnitName = ouName;
        }
    }

    /**
     * Jasper-НД для вычисления статистики
     */
    private class ExecTaskJRDataSource extends TypedJoinDS<TaskInfo> {

        private ErrandsReportFilterParams.DSGroupByInfo groupBy; // способ группировки, заполняется внутри buildJoin

        public ExecTaskJRDataSource(GenericDSProviderBase provider) {
            super(provider);
        }

        /**
         * Получить характерное для текущей группировки название объекта:
         * название Подразделения или инициалы Сотрудника.
         *
         */
        public String getItemName(ExecutorGroupInfo item) {
            return (groupBy.isUseOUFilter() ? item.employee.unitName : item.employee.ФамилияИО());
        }

        /**
         * Прогрузить строку отчёта
         */
        @Override
        protected Map<String, Serializable> getReportContextProps(TaskInfo item) {
            final Map<String, Serializable> result = new LinkedHashMap<String, Serializable>();

			/* Название и заголовок ... */
            result.put(DsExecСolumnNames.COL_NAME_TAG, item.tagEmployee);
            result.put(DsExecСolumnNames.COL_TASK_TITLE, item.tagTaskTitle);
            result.put(DsExecСolumnNames.COL_PARAM_EXEC_ORGUNIT, item.orgUnitName);

            result.put(DsExecСolumnNames.COL_PARAM_GROUP_BY, this.groupBy.getGroupByInfo().grpName);

            result.put(DsExecСolumnNames.COL_PERIOD_START, periodStart);
            result.put(DsExecСolumnNames.COL_PERIOD_END, periodEnd);

            return result;
        }


        @Override
        public int buildJoin() {
            // построить  в groups список объектов сгруппированных по названиям Измерения (ключ в groups)

            final List<TaskInfo> result = new ArrayList<TaskInfo>();

            if (getContext().getRsIter() != null) {

                // Ключ здесь это Сотрудник
                final Map<NodeRef, ExecutorGroupInfo> executors = new HashMap<NodeRef, ExecutorGroupInfo>();

				/* список выбранных организаций */
                final String orgUnitSelected = ErrandsReportFilterParams.getOUNodesFromParams(getReportDescriptor().getDsDescriptor(), paramsFilter.getColnameOrgUnits());
                final NodeService nodeSrv = getServices().getServiceRegistry().getNodeService();

				/* Получение формата и ссылки для выбранного groupby-Измерения из конфигурации ... */
                this.groupBy = paramsFilter.findGroupByInfo(getReportDescriptor().getDsDescriptor());
                qnames().setQN_ASSOC_REF(groupBy.getGroupByInfo().grpAssocQName); // задать название ассоциации для получения Инициаторов или Подразделений

                // Формат для обозначения Сотрудника
                fmtNameTag = this.groupBy.getGroupByInfo().grpFmt;

                // формат формирования заголовка

                final ErrandsReportFilterParams.GroupByInfo fmt = paramsFilter.getGroupByMap().get(DsExecСolumnNames.GROUP_BY_FORMAT_OF_TITLE);
                fmtTitle = (fmt != null && fmt.grpFmt != null) ? fmt.grpFmt : CM_NAME;


                final OrgstructureBean beanOU = getServices().getOrgstructureService();
                logger.info(String.format("group by %s, filter is [%s]"
                        , (this.groupBy.isUseOUFilter() ? "OU" : "employee")
                        , Utils.nonblank(orgUnitSelected, "*")));

				/* проход по всем загруженным задачам... */
                while (getContext().getRsIter().hasNext()) {

                    final ResultSetRow rs = getContext().getRsIter().next();

                    final NodeRef errandId = rs.getNodeRef(); // id Задачи

                    // (!) Фильтрование
                    if (getContext().getFilter() != null && !getContext().getFilter().isOk(errandId)) {
                        if (logger.isDebugEnabled())
                            logger.debug(String.format("{%s} filtered out", errandId));
                        continue;
                    }

                    // Исполнители Задачи
                    final List<AssociationRef> employees = nodeSrv.getTargetAssocs(errandId, qnames().QN_ASSOC_REF);
                    if (employees == null || employees.isEmpty()) // (!?) с Задачей не связан никакой Сотрудник-Исполнитель ...
                    {
                        logger.warn(String.format("No execution employee found for errand %s", errandId));
                        continue;
                    }

                    // (!) прогружаем атрибуты Задачи ...
                    final Map<QName, Serializable> props = nodeSrv.getProperties(errandId);

					/* проверка статуса поручения ... */
                    if (!paramsFilter.isStatusOk((String) props.get(qnames().QN_FLD_DOCSTATUS))) {
                        continue;
                    }

                    // формирование списка задач для каждого исполнителя ...
                    for (AssociationRef employee : employees) {
                        final NodeRef executorId = employee.getTargetRef(); // id Сотрудника-Исполнителя

                        final ExecutorGroupInfo executor;
                        if (executors.containsKey(executorId)) {
                            executor = executors.get(executorId);
                        } else {
                            final BasicEmployeeInfo execEmployee = new BasicEmployeeInfo(executorId);
                            execEmployee.loadProps(nodeSrv, beanOU);

                            executor = new ExecutorGroupInfo(execEmployee);
                            executors.put(executorId, executor);
                        }

                        // фильтрование по ID Подразделения
                        if (orgUnitSelected != null && !orgUnitSelected.contains(executor.employee.unitId.getId())) {
                            // нет фильтра или id перечислен в фильтре
                            continue;
                        }

                        executor.registerTask(errandId);

                    } // for по Исполнителям
                } // while

                for (final ExecutorGroupInfo ginfo : executors.values()) {
                    if (ginfo.tasksTitles.isEmpty())
                        continue;
                    for (String taskTitle : ginfo.tasksTitles) {
                        result.add(new TaskInfo(ginfo.tag, taskTitle, ginfo.employee.unitName));
                    } // fr
                }

                // (!) перенос в основной блок
                this.setData(getSortedItemsList(result));
            } // if

            if (this.getData() != null) {
                this.setIterData(this.getData().iterator());
            }

            logger.info(String.format("found %s row(s)", result.size()));

            return result.size();
        }

        private List<TaskInfo> getSortedItemsList(List<TaskInfo> result) {
            if (result != null) {
                // сортировка по Алфавиту Сотрудников и Названий ...
                Collections.sort(result, new TaskComparator());
            }
            return result;
        }

    }

    /**
     * Для Сравнения задач по алфавиту Сотрудников и названий задач
     */
    final private static class TaskComparator implements Comparator<TaskInfo> {
        // сравнение строк, Null в конце ("тяжёлые")
        public static int safeStrCmp(String s1, String s2) {
            if (s1 == null)
                return (s2 == null) ? 0 : -1;

            return (s2 == null) ? 1 : s1.compareToIgnoreCase(s2);
        }

        @Override
        public int compare(TaskInfo o1, TaskInfo o2) {
            final int i = safeStrCmp(o1.tagEmployee, o2.tagEmployee);
            return (i != 0) ? i : safeStrCmp(o1.tagTaskTitle, o2.tagTaskTitle);
        }
    }
}
