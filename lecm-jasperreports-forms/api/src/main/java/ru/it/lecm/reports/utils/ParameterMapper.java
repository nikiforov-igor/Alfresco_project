package ru.it.lecm.reports.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.alfresco.service.cmr.repository.NodeRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import ru.it.lecm.reports.api.model.ColumnDescriptor;
import ru.it.lecm.reports.api.model.DataSourceDescriptor;
import ru.it.lecm.reports.api.model.ParameterType.Type;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.model.impl.ColumnDescriptorImpl;
import ru.it.lecm.reports.model.impl.JavaDataTypeImpl.SupportedTypes;
import ru.it.lecm.reports.model.impl.ParameterTypedValueImpl;

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
     * @param reportDesc ReportDescriptor
     * @param args       Map<String, String[]>
     */
    public static void assignParameters(ReportDescriptor reportDesc, Map<String, String[]> args) {
        if (args == null || reportDesc == null || reportDesc.getDsDescriptor() == null) {
            return;
        }

        for (ColumnDescriptor colDesc : reportDesc.getDsDescriptor().getColumns()) {
            String argRootName = getArgRootName(colDesc);// colDesc.getColumnName();
            /*
             * Если колонка НЕ описана как параметр, но значение ей передаётся в
			 * аргументах, считаем что она задана как константа.
			 */
            if (colDesc.getParameterValue() == null) { // колонка не описана как параметр ...
                // проверка на наличие данных в строке параметров - CONSTANT VALUE
                if (args.containsKey(colDesc.getColumnName())) {
                    final String[] argv = args.get(colDesc.getColumnName());
                    final String value = (argv != null && argv.length > 0) ? argv[0] : null;
                    log.info(String.format(
                            "Arguments list contains data for report column '%s' that is not marked as parameter -> column set as constant:"
                                    + "\n\t default column expression: '%s'"
                                    + "\n\t new argument constant: '%s'"
                            , colDesc.getColumnName()
                            , colDesc.getExpression()
                            , value
                    ));
                    colDesc.setExpression(value);
                }
                continue;
            }

            // если колонка параметризована ...
            switch (colDesc.getParameterValue().getType()) {
                case RANGE: {
                    // проверяем диапозон дат
                    final String dateRangeParam = argRootName + DATE_RANGE;
                    boolean isDateRange = false;
                    boolean isNumberRange = false;
                    if (args.containsKey(dateRangeParam)) {
                        //добавляем в список параметр обработанный ключ
                        args.put(argRootName, args.get(dateRangeParam));
                        args.remove(dateRangeParam);
                        isDateRange = true;
                    } else {
                        // не нашли параметра - пробуем получить диапозон для чисел
                        final String numberRangeParam = argRootName + NUMBER_RANGE;
                        if (args.containsKey(numberRangeParam)) {
                            //добавляем в список параметр обработанный ключ
                            args.put(argRootName, args.get(numberRangeParam));
                            args.remove(numberRangeParam);
                            isNumberRange = true;
                        }
                    }
                    Object bound1 = null;
                    Object bound2 = null;
                    if ((isDateRange || isNumberRange)) {
                        if (args.containsKey(argRootName)) {
                            final String[] paramValue = args.get(argRootName)[0].split("\\|");
                            if (isDateRange) {
                                bound1 = getDateValue(paramValue[0]);
                                if (paramValue.length >= 2)
                                    bound2 = getDateValue(paramValue[1]);
                            } else {
                                bound1 = getNumericAutoTypedValue(paramValue[0]);
                                if (paramValue.length >= 2)
                                    bound2 = getNumericAutoTypedValue(paramValue[1]);
                            }
                        }
                    }
                    colDesc.getParameterValue().setBound1(bound1);
                    colDesc.getParameterValue().setBound2(bound2);
                    break;
                }
                case VALUE:
                case LIST:
                    Object bound = null;
                    if (args.containsKey(argRootName)) {
                        final String[] paramValue = args.get(argRootName);
                        if (paramValue != null && (paramValue.length > 0)) {
                            if (colDesc.getParameterValue().getType() == Type.VALUE) {
                                // для простых значений явно зададим один элемент
                                bound = paramValue[0];
                            } else {
                                bound = paramValue[0].split(",");
                            }
                        }
                    }
                    colDesc.getParameterValue().setBound1(bound);
                    break;
                default: // непонятный тип - сообщение об ошибке и игнор ...
                    log.error(String.format("Unsupported parameter type '%s' skipped", Utils.coalesce(colDesc.getParameterValue().getType(), "NULL")));
                    break;
            }
        }

        if (args.containsKey(DataSourceDescriptor.COLNAME_ID)) {
            // нужно гарантировать колонку с ID, когда есть такой параметр ...
            ensureDataColumn(reportDesc.getDsDescriptor(), args.get(DataSourceDescriptor.COLNAME_ID), DataSourceDescriptor.COLNAME_ID, SupportedTypes.STRING);
        }

        // аналогично нужно гарантировать колонку с TYPE, когда есть такой параметр или тип задан явно ...
        if (args.containsKey(DataSourceDescriptor.COLNAME_TYPE)) {
            ensureDataColumn(reportDesc.getDsDescriptor(), args.get(DataSourceDescriptor.COLNAME_TYPE), DataSourceDescriptor.COLNAME_TYPE, SupportedTypes.STRING);
        } else if (reportDesc.getFlags() != null && !reportDesc.getFlags().getSupportedNodeTypes().isEmpty()) {
            ensureDataColumn(reportDesc.getDsDescriptor()
                    , StringUtils.collectionToCommaDelimitedString(reportDesc.getFlags().getSupportedNodeTypes())
                    , DataSourceDescriptor.COLNAME_TYPE
                    , SupportedTypes.STRING);
        }
    }


    static public Object getNumericAutoTypedValue(String value) {
        return ArgsHelper.tryMakeNumber(value);
    }

    static public Date getDateValue(String paramValue) {
        return ArgsHelper.tryMakeDate(paramValue, null);
    }

    /**
     * Гарантировать наличие колонки с указанным названием и присвоить ей указанное значение.
     * Если уже колонка есть - просто присвоить её значение, иначе создать новую с указанным типом
     * и потом присвоить значение.
     *
     * @param dsDesc  описатель НД
     * @param value   значение
     * @param colName название колонки
     * @param colType тип колонки
     * @return ColumnDescriptor
     */
    private static ColumnDescriptor ensureDataColumn(DataSourceDescriptor dsDesc, Object value, String colName, SupportedTypes colType) {
        ColumnDescriptor result = dsDesc.findColumnByName(colName);
        Object destValue = null;
        if (result == null) { // создание новой колонки ...
            result = new ColumnDescriptorImpl(colName, colType);

            dsDesc.getColumns().add(result);
            destValue = value;
        } else {
            if (result.getExpression() != null && !result.getExpression().isEmpty()) {
                destValue = result.getExpression();
            } else {
                if (value instanceof String) {
                    if (!Utils.isStringEmpty((String) value)) {
                        destValue = value;
                    }
                } else if (value instanceof String[]) {
                    final String[] arr = (String[]) value;
                    if (arr.length > 0 && !Utils.isStringEmpty(arr[0])) {
                        destValue = arr;
                    }
                }
            }
        }
        if (destValue != null) { // задать значение ...
            if (result.getParameterValue() == null) {
                final ParameterTypedValueImpl ptv = new ParameterTypedValueImpl(colName);
                result.setParameterValue(ptv);
            }
            result.getParameterValue().setBound1(destValue);
        }
        return result;
    }


    /**
     * Получение названия аргумента, который соот-ет параметризации колонки.
     * @param colDesc колонка, для которой получить название "её" аргумента
     * @return NULL, если колонка не является параметризуемой,
     *         иначе мнемоника параметра, а если она не задана - название колонки (columnName).
     */
    public static String getArgRootName(final ColumnDescriptor colDesc) {
        if (colDesc == null || colDesc.getParameterValue() == null) {
            // не параметризуется ...
            return null;
        }
        return colDesc.getColumnName();
    }

    /**
     * Получить значение параметра в виде ссылки на узел
     *
     * @param colDesc ColumnDescriptor
     * @return List<NodeRef>
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

        final String[] nodeRefs = (argValue instanceof String[]) ? (String[]) argValue : argValue.toString().split(",;");
        for (String item : nodeRefs) {
            if (NodeRef.isNodeRef(item)) {
                result.add(new NodeRef(item));
            }
        }
        return result;
    }
}
