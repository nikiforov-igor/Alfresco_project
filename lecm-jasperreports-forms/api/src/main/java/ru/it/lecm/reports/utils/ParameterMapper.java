package ru.it.lecm.reports.utils;

import org.alfresco.service.cmr.repository.NodeRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.reports.api.model.ColumnDescriptor;
import ru.it.lecm.reports.api.model.DataSourceDescriptor;
import ru.it.lecm.reports.api.model.ParameterType.Type;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.model.impl.ColumnDescriptorImpl;
import ru.it.lecm.reports.model.impl.ParameterTypedValueImpl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Распределитель request-аргументов по параметрам описателя отчёта.
 * Название аргумента для колонки данных выбирается в getArgRootName.
 */
public class ParameterMapper {
    static final transient Logger log = LoggerFactory.getLogger(ParameterMapper.class);
    public static final String DATE_RANGE = "-date-range";
    public static final String NUMBER_RANGE = "-range";
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * Задать параметры из списка. Подразумевается, что параметры имеют названия
     * совпадающие с названиями параметра колонки данных из описателя reportDesc.getDsDescriptor.
     *
     * @param reportDesc
     * @param args
     */
    public static void assignParameters(ReportDescriptor reportDesc, Map<String, String[]> args) {
        if (args == null || reportDesc == null || reportDesc.getDsDescriptor() == null)
            return;

        for (ColumnDescriptor colDesc : reportDesc.getDsDescriptor().getColumns()) {
            if (colDesc.getParameterValue() == null) // колонка не описана как параметр ...
                continue;

            // если колонка параметризована ...
            String argRootName = colDesc.getColumnName();
            switch (colDesc.getParameterValue().getType()) {
                case VALUE:
                case LIST:
                    if (args.containsKey(argRootName)) {
                        final String[] paramValue = args.get(argRootName);
                        if ((colDesc.getParameterValue().getType() == Type.VALUE) && (paramValue != null) && (paramValue.length > 0)) {
                            // для простых значений явно зададим один элемент
                            colDesc.getParameterValue().setBound1(paramValue[0]);
                        } else {
                            String[] paramValues = paramValue[0].split(",");
                            colDesc.getParameterValue().setBound1(paramValues);
                        }
                    }
                    break;
                case RANGE:
                    // проверяем диапозон дат
                    String dateRangeParam = argRootName + DATE_RANGE;
                    boolean isDateRange = false;
                    if (args.containsKey(dateRangeParam)) {
                        argRootName = dateRangeParam;
                        isDateRange = true;
                    } else {
                        // не нашли параметра - пробуем получить диапозон для чисел
                        String numberRangeParam = argRootName + NUMBER_RANGE;
                        if (args.containsKey(numberRangeParam)) {
                            argRootName = numberRangeParam;
                            isDateRange = false;
                        }
                    }
                    if (args.containsKey(argRootName)) {
                        final String[] paramValue = args.get(argRootName)[0].split("\\|");
                        Object bound1 = null;
                        Object bound2 = null;
                        if (isDateRange) {
                            try {
                                bound1 = (paramValue[0] != null && paramValue[0].length() > 0) ? DATE_FORMAT.parse(paramValue[0]) : null;
                                if (paramValue.length == 2) {
                                    bound2 = (paramValue[1] != null && paramValue[1].length() > 0) ? DATE_FORMAT.parse(paramValue[1]) : null;
                                }
                            } catch (ParseException ignored) {
                            }
                        } else {
                            bound1 = (paramValue[0] != null && paramValue[0].length() > 0) ? paramValue[0] : null;
                            if (bound1 != null) {
                                bound1 = bound1.toString().indexOf(".") > 0 ? Double.valueOf((String) bound1) : Long.valueOf((String) bound1);
                            }
                            if (paramValue.length == 2) {
                                bound2 = (paramValue[1] != null && paramValue[1].length() > 0) ? paramValue[1] : null;
                                if (bound2 != null) {
                                    bound2 = bound2.toString().indexOf(".") > 0 ? Double.valueOf((String) bound2) : Long.valueOf((String) bound2);
                                }
                            }
                        }
                        colDesc.getParameterValue().setBound1(bound1);
                        colDesc.getParameterValue().setBound2(bound2);
                    }

                    break;
                default: // непонятный тип - сообщение об ошибке и игнор ...
                    log.error(String.format("Unsupported parameter type '%s' skipped", Utils.coalesce(colDesc.getParameterValue().getType(), "NULL")));
                    break;
            }
        }
        if (args.containsKey(DataSourceDescriptor.COLNAME_ID)){ // нужно добавить колонку с ID
            ColumnDescriptor idColumn = new ColumnDescriptorImpl();
            idColumn.setColumnName(DataSourceDescriptor.COLNAME_ID);

            ParameterTypedValueImpl result = new ParameterTypedValueImpl("VALUE");
            result.setBound1(args.get(DataSourceDescriptor.COLNAME_ID));
            idColumn.setParameterValue(result);
            reportDesc.getDsDescriptor().getColumns().add(idColumn);
        }
    }


    /**
     * Получение названия аргумента, который соот-ет параметризации колонки.
     * Для параметров списков и значений будет получено конечное название,
     * для интервалов - корневое, а реальные значения образуются из него
     * добавлением окончаний "_lo" и "_hi".
     *
     * @param colDesc колонка, для которой получить название "её" аргумента
     * @return NULL, если колонка не является параметризуемой,
     *         иначе мнемоника параметра, а если она не задана - название колонки (columnName).
     */
    public static String getArgRootName(final ColumnDescriptor colDesc) {
        if (colDesc == null || colDesc.getParameterValue() == null) // не параметризуется ...
            return null;
        return colDesc.getColumnName();
    }

    /**
     * Получить значение параметра в виде ссылки на узел
     *
     * @param colDesc
     * @return
     */
    public static List<NodeRef> getArgAsNodeRef(final ColumnDescriptor colDesc) {
        List<NodeRef> result = new ArrayList<NodeRef>();

        if (colDesc == null || colDesc.getParameterValue() == null) {
            return result;
        }

        final Object argValue = colDesc.getParameterValue().getBound1();
        if (argValue == null) {
            return result;
        }

        String[] nodeRefs = argValue.toString().split(",");
        for (String nodeRef : nodeRefs) {
            if (NodeRef.isNodeRef(nodeRef)) {
                result.add(new NodeRef(argValue.toString()));
            }
        }
        return result;
    }
}
