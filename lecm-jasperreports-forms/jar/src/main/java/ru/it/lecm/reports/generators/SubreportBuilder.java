package ru.it.lecm.reports.generators;

import org.alfresco.service.cmr.search.ResultSetRow;
import ru.it.lecm.reports.api.ReportDSContext;
import ru.it.lecm.reports.jasper.AlfrescoJRDataSource;
import ru.it.lecm.reports.model.impl.ColumnDescriptor;
import ru.it.lecm.reports.model.impl.SubReportDescriptorImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Построитель для подотчётов на основе {@link ru.it.lecm.reports.model.impl.SubReportDescriptorImpl}.
 * <br/> Выполняется сбор свойств по всем узлам из вложенного в основной документ списка.
 * <br/> Результатом будет:
 * <li> либо <b> ОДНА отформатированная СТРОКА </b>
 * <li> либо <b> СПИСОК бинов. </b>
 * <br/> Описатели подотчётов {@link ru.it.lecm.reports.model.impl.SubReportDescriptorImpl} являются частью
 * {@link ru.it.lecm.reports.api.model.ReportDescriptor} и отнаследованы от него.
 * <br/> имеют:
 * <li> класс бина или формат в случае единой строки,</li>
 * <li> список атрибутов для присвоения и источники данных для них
 * <br/>(списком атрибутов или ассоциаций Альфреско).</li>
 *
 * @author rabdullin
 */
public class SubreportBuilder {
    final static public String REGEXP_SUBREPORTLINK = "[{]{0,2}subreport[:][:]([^}]+)([}]{0,2})";

    final private SubReportDescriptorImpl subReport;
    final private GenericDSProviderBase provider;

    public SubreportBuilder(SubReportDescriptorImpl subReportDesc, GenericDSProviderBase provider) {
        this.provider = provider;
        this.subReport = subReportDesc;
    }

    @Override
    public String toString() {
        return String.format("%s [", this.getClass().getName()) + "\n\t subReport " + (getSubReport() == null ? "NULL" : getSubReport()) + "\n]";
    }

    public SubReportDescriptorImpl getSubReport() {
        return subReport;
    }

    /**
     * Получить сконфигурированные свойства вложенного объекта.
     *
     * @param dataSource AlfrescoJRDataSource
     * @return <b>ключ</b> = название колонки или свойства,
     *         <br/><b>значение</b> = соот-щий объект, полученный по ссылке для колонки согласно {subItemsSourceMap}
     */
    protected Map<String, Object> gatherSubItemInfo(AlfrescoJRDataSource dataSource) {
        final Map<String, Object> values = new HashMap<>();
        List<ColumnDescriptor> columns = dataSource.getReportDescriptor().getDsDescriptor().getColumns();
        for (ColumnDescriptor column : columns) {
            final Object value = dataSource.getContext().getPropertyValueByJRField(column.getColumnName());
            values.put(column.getColumnName(), value);
        }
        return values;
    }

    /**
     * Выполнить построение бина подотчёта согласно текущего описателя и
     * указанного базового документа.
     *
     * @return получить вложенный список у основного объекта и построить для
     *         него результат согласно текущему описателю {@link #subReport} в виде:
     *         <li>   строки, когда используется форматирование,
     *         <li>   или List-а объектов типа #subReport.beanClassName
     */
    public Object buildSubReport(ReportDSContext parentContext) {
        /* получение списка ... */
        AlfrescoJRDataSource dataSource = provider.createDS(subReport, parentContext);

        if (dataSource == null || dataSource.getContext().getRsIter() == null) { // нет вложенных ...
            // если исопльзуется форматирование - вернуть его "пустую строку" ...
            return null;
        }

        final List<Object> result = new ArrayList<>();

        int i = 0;
        while (dataSource.getContext().getRsIter().hasNext()) {
            ResultSetRow next = dataSource.getContext().getRsIter().next();
            dataSource.getContext().setRsRow(next);
            dataSource.getContext().setCurNodeRef(next.getNodeRef());
            i++; // нумерация от единицы
            if (dataSource.loadAlfNodeProps(next.getNodeRef())) {
                // загрузка данных по строке
                final Map<String, Object> item = gatherSubItemInfo(dataSource);
                if (item != null) {
                    item.put(SubReportDescriptorImpl.BEAN_PROPNAME_COL_ROWNUM, String.valueOf(i));
                    result.add(item);
                }
            }
        }
        return result;
    }
}

