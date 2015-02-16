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
import ru.it.lecm.reports.calc.AvgValue;
import ru.it.lecm.reports.generators.GenericDSProviderBase;
import ru.it.lecm.reports.jasper.AlfrescoJRDataSource;
import ru.it.lecm.reports.jasper.TypedJoinDS;
import ru.it.lecm.reports.jasper.containers.BasicEmployeeInfo;
import ru.it.lecm.reports.jasper.utils.JRUtils;
import ru.it.lecm.reports.utils.Utils;
import ru.it.lecm.reports.xml.DSXMLProducer;
import ru.it.lecm.reports.utils.LuceneSearchWrapper;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;

/**
 * Продуктивность по исполнителям и (или) по подразделениям
 * Фильтры отчета:
 * •	За Период
 * •	Исполнители
 * o	Сотрудники (шаблон отчёта первый)
 * o	Подразделения (шаблон отчёта второй/другой)
 * Выводимые показатели:
 * •	Среднее время исполнения поручения
 * (время нахождения поручения между статусами "В работе" до любого финального статуса)
 * в зависимости от даты
 *
 * @author rabdullin
 */
public class ErrandsProductionsGraphDSProvider extends GenericDSProviderBase {

    private static final Logger logger = LoggerFactory.getLogger(ErrandsProductionsGraphDSProvider.class);
    public static final int REPORT_DEFAULT_PERIOD = 7;

    /**
     * Способ группировки элементов определяется параметрами отчёта
     * Конфигурируется:
     * 1) формат ссылки
     * 2) атрибут-источник для группирования
     */
    final private ErrandsReportFilterParams paramsFilter = new ErrandsReportFilterParams(
            DsProductionsColumnNames.COL_PARAM_PERIOD
            , DsProductionsColumnNames.COL_PARAM_GROUP_BY
            , DsProductionsColumnNames.CONTAINS_GROUP_BY_OU
            , DsProductionsColumnNames.COL_PARAM_EXEC_ORGUNIT
    );

    private Date periodStart, periodEnd;

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
        defaults.put(ErrandsReportFilterParams.XMLGROUPBY_FORMATS_MAP, null);
        defaults.put(ErrandsReportFilterParams.XMLGROUPBY_SOURCE_MAP, null);
    }


    private void loadConfig() {
        try {
            getConfigXML().setConfigName(DSXMLProducer.makeDsConfigFileName(this.getReportDescriptor().getMnem()));
            getConfigXML().loadConfig();
            this.paramsFilter.scanGroupByInfo(getConfigXML());
        } catch (JRException e) {
            logger.error(e.getMessage(), e);
        }
    }
    @Override
    protected AlfrescoJRDataSource createDS(ReportDescriptor descriptor, ReportDSContext parentContext) {
        this.setReportDescriptor(descriptor);
        LuceneSearchWrapper alfrescoQuery = execQuery(descriptor, parentContext);
        if (alfrescoQuery == null || alfrescoQuery.getSearchResults() == null) {
            return null;
        }

        ExecProductionsJRDataSource result = new ExecProductionsJRDataSource(this);
        result.getContext().setRegistryService(getServices().getServiceRegistry());
        result.getContext().setJrSimpleProps(getSimpleColumnNames(this.getReportDescriptor()));
        result.getContext().setMetaFields(JRUtils.getDataFields(this.getReportDescriptor()));
        result.getContext().setResolver(this.getResolver());
        result.getContext().setRsIter(alfrescoQuery.getSearchResults().iterator());

        // фильтр данных ...
        result.getContext().setFilter(getDataFilter(alfrescoQuery));
        return result;
    }

    @Override
    protected LuceneSearchWrapper execQuery(ReportDescriptor descriptor, ReportDSContext parentContext) {
        loadConfig();
        return super.execQuery(descriptor, parentContext);
    }

    @Override
    protected LuceneSearchWrapper buildQuery(ReportDescriptor descriptor, ReportDSContext parentContext) {
        final LuceneSearchWrapper builder = super.buildQuery(descriptor, parentContext);

        // hasData: становится true после внесения первого любого условия в builder
        boolean hasData = !builder.isEmpty();

		/*
         * Критерий : Время Окончания заданы внутри указанного интервала
		 */
        final DataSourceDescriptor ds = getReportDescriptor().getDsDescriptor();

        this.periodEnd = paramsFilter.getParamPeriodEnd(ds);
        if (periodEnd == null) {
            // если не указан конец -  берём текущий момент ...
            periodEnd = new Date();
        }
        // выравнивание periodEnd на конец суток ...
        periodEnd = Utils.adjustDayTime(periodEnd, 23, 59, 59, 999);

        this.periodStart = paramsFilter.getParamPeriodStart(ds);
        if (periodStart == null) {
            // если не указано начало - одно-недельный период от конца ...
            periodStart = new Date(periodEnd.getTime() - REPORT_DEFAULT_PERIOD * Utils.MILLIS_PER_DAY);
        }

        // выравнивание periodStart на начало суток ...
        periodStart = Utils.adjustDayTime(periodStart, 0, 0, 0, 0); // начало суток

        final String condEnd =
                getQueryHelper().emmitDateIntervalCheck(LocalQNamesHelper.QNFLD_END_DATE.toPrefixString(getServices().getServiceRegistry().getNamespaceService()), periodStart, periodEnd, false);

        if (!Utils.isStringEmpty(condEnd)) {
            final String condBoth = String.format("\n\t(%s)\n\t", condEnd);
            builder.emmit(hasData ? " AND " : "").emmit(condBoth);
        }

        return builder;
    }

    /**
     * Названия колонок в наборе данных для отчёта.
     */
    final class DsProductionsColumnNames {
        /**
         * Колонка "Группировка" - по часам, дням и  т.д.
         */
        final static String COL_PARAM_GROUP_BY = "Col_GroupBy"; // String значение долно быть в секцииях конфы "groupBy.xxx"
        final static String CONTAINS_GROUP_BY_OU = "OrgUnit"; // подстрока, которая означает группировку по организации

        /**
         * Период с ... по ...
         */
        final static String COL_PARAM_PERIOD = "Col_Period"; // date, PARAM_DELTA

        /**
         * Колонка "Исполняющее подразделение"
         */
        final static String COL_PARAM_EXEC_ORGUNIT = "Col_Param_Exec_OrgUnit"; // alfrescoType="lecm-links:link", PARAM_TYPE_LIST

        /**
         * Колонка результата "Название" в строке данных
         */
        final static String COL_NAMEATAG = "Col_NameTag"; // String

        /**
         * Колонка "даты" в строке данных
         */
        final static String COL_DATETIME = "Col_DateTime"; // Date/Timestamp

        /**
         * Колонка "Показатель: среднее время исполнения поручения"
         * (его единицы измерения - см колонку COL_AVG_EXECUTION_UNITS)
         */
        final static String COL_AVG_EXECUTION = "Col_Avg_Execution.Value"; // java.lang.Float

        /**
         * Колонка "Показатель: название единицы измерения среднего времени исполнения"
         */
        final static String COL_AVG_EXECUTION_UNITS = "Col_Avg_Execution.Units"; // String
    }

    /**
     * QName-ссылки на данные Альфреско
     */
    private class LocalQNamesHelper extends ErrandsQNamesHelper {
        /**
         * Параметр отчёта в НД: Исполнитель или Подразделение, по которому
         * фактически будет выполняться группировка ...
         */
        QName QN_ASSOC_REF;

        LocalQNamesHelper(NamespaceService ns) {
            super(ns);
            this.QN_ASSOC_REF = ErrandsService.ASSOC_ERRANDS_EXECUTOR;
        }

        public void setQN_ASSOC_REF(String assocQName) {
            this.QN_ASSOC_REF = super.makeQN(assocQName);
        }
    }

    /**
     * Структура для хранения данных о статистике по Сотруднику:
     * Средние значения накапливаются в обычном индексированном списке.
     */
    class ProductGroupInfo {

        final private BasicEmployeeInfo employee;

        /**
         * Список из элементов типа "Среднее время исполнения, часов"
         * (индексы: по дням, неделям, месяцам и т.п.)
         */
        final List<AvgValue> avgExecTimeInHours; // new AvgValue("Avg exec time, h");

        /**
         * Зарегистрировать
         *
         * @param maxListSize кол-во элементов в списке накопления
         */
        public ProductGroupInfo(BasicEmployeeInfo employeeExec, int maxListSize) {
            super(); // super(employeeId);

            this.employee = employeeExec;
            this.avgExecTimeInHours = new ArrayList<AvgValue>(maxListSize);
            for (int i = 0; i < maxListSize; i++) {
                this.avgExecTimeInHours.add(new AvgValue(String.format("Avg exec time [%s], h", i)));
            }
        }

        final static long MILLIS_PER_HOUR = 1000 * 60 * 60;

        public void registerDuration(int index, long duration_ms) {
            if (duration_ms <= 0) {
                // нельзя определить
                return;
            }
            final float fact = (float) duration_ms / MILLIS_PER_HOUR;
            this.avgExecTimeInHours.get(index).adjust(fact);
        }
    }

    /**
     * Точка на графике.
     */
    class GraphPoint {
        private String Col_NameTag;
        private java.sql.Timestamp Col_DateTime;

        /**
         * Показатель: среднее время исполнения поручения
         * (его единицы измерения - см колонку COL_AVG_EXECUTION_UNITS)
         */
        private Float Col_Avg_Execution_Value;

        /**
         * Колонка "Показатель: название единицы измерения среднего времени исполнения"
         */
        private String Col_Avg_Execution_Units;

        private GraphPoint(String col_NameTag, Timestamp col_DateTime,
                           Float col_Avg_Execution_Value, String col_Avg_Execution_Units) {
            super();
            Col_NameTag = col_NameTag;
            Col_DateTime = col_DateTime;
            Col_Avg_Execution_Value = col_Avg_Execution_Value;
            Col_Avg_Execution_Units = col_Avg_Execution_Units;
        }

        public String getCol_NameTag() {
            return Col_NameTag;
        }

        public void setCol_NameTag(String col_NameTag) {
            Col_NameTag = col_NameTag;
        }

        public java.sql.Timestamp getCol_DateTime() {
            return Col_DateTime;
        }

        public void setCol_DateTime(java.sql.Timestamp col_DateTime) {
            Col_DateTime = col_DateTime;
        }

        public Float getCol_Avg_Execution_Value() {
            return Col_Avg_Execution_Value;
        }

        public void setCol_Avg_Execution_Value(Float col_Avg_Execution_Value) {
            Col_Avg_Execution_Value = col_Avg_Execution_Value;
        }

        public String getCol_Avg_Execution_Units() {
            return Col_Avg_Execution_Units;
        }

        public void setCol_Avg_Execution_Units(String col_Avg_Execution_Units) {
            Col_Avg_Execution_Units = col_Avg_Execution_Units;
        }

    }


    /**
     * Jasper-НД для вычисления статистики
     */
    private class ExecProductionsJRDataSource extends TypedJoinDS<GraphPoint> {

        private ErrandsReportFilterParams.DSGroupByInfo groupBy; // способ группировки, заполняется внутри buildJoin

        public ExecProductionsJRDataSource(GenericDSProviderBase provider) {
            super(provider);
        }

        /**
         * Прогрузить строку отчёта
         * Time Series Dataset:
         * seriesExpression: java.lang.Comparable = название Сотрудника/Подразделения
         * timePeriodExpression: java.util.Date = время
         * valueExpression: java.lang.Number = значение
         * labelExpression: String = (необ) метка точки, если не указано - используется метка по-умолчанию
         * itemHyperlink: sets hyperlinks associated with chart items.
         */
        @Override
        protected Map<String, Serializable> getReportContextProps(GraphPoint item) {
            final Map<String, Serializable> result = new LinkedHashMap<String, Serializable>();

			/* Название подразделения или имя пользователя ... */
            result.put(DsProductionsColumnNames.COL_NAMEATAG, Utils.nonblank(item.getCol_NameTag(), "?"));

			/* Время ... */
            result.put(DsProductionsColumnNames.COL_DATETIME, item.getCol_DateTime());

			/* Значение ... */
            result.put(DsProductionsColumnNames.COL_AVG_EXECUTION, item.getCol_Avg_Execution_Value());

			/* Ед измерения ... */
            result.put(DsProductionsColumnNames.COL_AVG_EXECUTION_UNITS, item.getCol_Avg_Execution_Units());

            return result;
        }

        /**
         * Найти полное кол-во суток между указанными датами
         *
         * @return разницу в днях ("больше или равно")
         */
        private int countDeltaInDays(Date dstart, Date dend) {
            if (dstart == null || dend == null) {
                // одной из дат нет ...
                return 0;
            }

            if (dstart.getTime() > dend.getTime()) {
                final Date buf = dstart;
                dstart = dend;
                dend = buf;
            }
            // (!) первую дату выравниваем на начало дня,
            // вторую - не трогаем, т.к. будем ровнять delta_h сверху на 24ч
            final Date nstart = Utils.adjustDayTime(dstart, 0, 0, 0, 0);

            // вычисление разницы в часах ...
            final float delta_h = Utils.getDurationInHours(dend.getTime() - nstart.getTime());
            return (int) Math.ceil(delta_h / 24); // "с округлением вверх" на 24ч границу
        }

        @Override
        /** построить  в groups список объектов сгруппированных по названиям Измерения (ключ в groups) */
        public int buildJoin() {
            final List<GraphPoint> result = new ArrayList<GraphPoint>();

            final int countDays = countDeltaInDays(periodStart, periodEnd);
            final int maxDaysCounter = 1 + (countDays >= 0 ? countDays : 0); // кол-во отметок времени

            // проход по данным ...
            if (getContext().getRsIter() != null && result.isEmpty()) {
                // series: собранные данные по объекта (Сотрудникам или Подразделениям)
                // Ключ здесь это название измерения (tag)
                final Map<NodeRef, ProductGroupInfo> series = new HashMap<NodeRef, ProductGroupInfo>();

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
                while (getContext().getRsIter().hasNext()) {
                    final ResultSetRow rs = getContext().getRsIter().next();

                    final NodeRef errandId = rs.getNodeRef(); // id Поручения
                    if (getContext().getFilter() != null && !getContext().getFilter().isOk(errandId)) {
                        if (logger.isDebugEnabled()) {
                            logger.debug(String.format("{%s} filtered out", errandId));
                        }
                        continue;
                    }

                    // Исполнители
                    final List<AssociationRef> executors = nodeSrv.getTargetAssocs(errandId, qnames().QN_ASSOC_REF);
                    if (executors == null || executors.isEmpty()) {// (!?) с Поручением не связан никакой Сотрудник-Исполнитель ...
                        logger.warn(String.format("No execution employee found for errand item %s", errandId));
                        continue;
                    }

                    // прогружаем атрибуты Поручения и корректируем данные ...
                    final Map<QName, Serializable> props = nodeSrv.getProperties(errandId);

                    for (AssociationRef execEmployeeAssoc : executors) {
                        final NodeRef executorId = execEmployeeAssoc.getTargetRef(); // id Сотрудника-Исполнителя

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

                        final ProductGroupInfo executor;
                        if (series.containsKey(keyId)) {
                            executor = series.get(keyId); // уже такой был ...
                        } else { // создание нового Сотрудника-Исполнителя
                            executor = new ProductGroupInfo(execEmployee, maxDaysCounter);
                            series.put(keyId, executor);
                        }

                        // точка X: "дата завершения" отчёта ...
                        final Date endErrand = (Date) props.get(ErrandsQNamesHelper.QNFLD_END_DATE);
                        if (endErrand == null) {
                            // пропускаем не завершённые поручения
                            continue;
                        }

                        // точка Y: "среднее время исполнения на (!) дату закрытия" ...
                        final int index = countDeltaInDays(periodStart, endErrand) - 1;
                        executor.registerDuration(index, qnames().getErrandExecutionTime(props));
                    }

                } // while по НД

                // DONE (см fix [ALF-1524]): подумать над тем, чтобы гарантировать наличие выбранных для отчёта Сотрудников в легенде всегда (даже если по ним не было данных)

                // (!) перенос в основной блок с разбивкой по датам ...
                fillGraphData(result, series, countDays);

            } // if hasData

			/* fix [ALF-1524] Чтобы при пустом списке не было даты 01/01/1970, можно добавить точки из "заказанного диапазона"
            if (result.isEmpty()) {
				final String emptyTag = "?";
				result.add( new GraphPoint( emptyTag,  new Timestamp( periodStart.getTime()), 0f, "h")); // одна точка в начале
				result.add( new GraphPoint( emptyTag,  new Timestamp( periodEnd.getTime()), 0f, "h")); // вторая точка в конце
			}
			*/

            this.setData(result);
            if (this.getData() != null) {
                this.setIterData(this.getData().iterator());
            }

            logger.info(String.format("found %s data items", result.size()));

            return result.size();
        }


        /**
         * Заполнение массива графических xy-данных согласно вычисленным
         * значениям для Сотрудников.
         * Время начала см. periodStart
         *
         * @param result    целевой массив точек
         * @param series    отметки по Сотрудникам
         * @param countDays кол-во дней
         */
        protected void fillGraphData(final List<GraphPoint> result, final Map<NodeRef, ProductGroupInfo> series, final int countDays) {
            final Calendar x_curDay = Calendar.getInstance();
            x_curDay.setTime(periodStart);
            for (int i = 0; i < countDays; i++) { // цикл по дням
                final Timestamp x_curStamp = new Timestamp(x_curDay.getTimeInMillis());
                for (Map.Entry<NodeRef, ProductGroupInfo> e : series.entrySet()) { // цикл по объектам
                    final ProductGroupInfo item = e.getValue();

                    final float y_value;

                    final AvgValue avg = item.avgExecTimeInHours.get(i);
                    // вместо отсутствующих значений выводим ноль - чтобы
                    // график не "схлопывался до точки" ...
                    y_value = (avg != null && avg.getCount() > 0) ? avg.getAvg() : 0;

                    final String tag = (groupBy.isUseOUFilter() ? item.employee.unitName : item.employee.ФамилияИО());
                    result.add(new GraphPoint(tag, x_curStamp, y_value, "h"));
                }
                x_curDay.add(Calendar.HOUR, 24); // (++) = добавляем ровно сутки
            }
        }
    }
}
