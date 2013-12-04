package ru.it.lecm.reports.utils;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import ru.it.lecm.reports.api.model.DataSourceDescriptor;
import ru.it.lecm.reports.api.model.ParameterType.Type;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.model.impl.ColumnDescriptor;
import ru.it.lecm.reports.model.impl.JavaDataType;
import ru.it.lecm.reports.model.impl.JavaDataType.SupportedTypes;
import ru.it.lecm.reports.model.impl.ParameterTypedValueImpl;

import java.util.*;

/**
 * Распределитель request-аргументов по параметрам описателя отчёта.
 * Название аргумента для колонки данных выбирается в getArgRootName.
 */
public class ParameterMapper {
    static final transient Logger log = LoggerFactory.getLogger(ParameterMapper.class);

    public static final String DATE_RANGE = "-date-range";
    public static final String NUMBER_RANGE = "-range";
    public static final String RANGE_LO_POSTFIX = "_lo";
    public static final String RANGE_HI_POSTFIX = "_hi";
    public static final String IDS_POSTFIX = "_ids";

    /**
     * Задать параметры из списка. Подразумевается, что параметры имеют названия
     * совпадающие с названиями параметра колонки данных из описателя reportDesc.getDsDescriptor.
     *
     * @param reportDesc ReportDescriptor
     * @param args       Map<String, String>
     */
    public static Map<String, Object> assignParameters(ReportDescriptor reportDesc, Map<String, String> args, NodeService nodeService) {
        Map<String, Object> argsMap = new HashMap<String, Object>();
        if (args == null || reportDesc == null || reportDesc.getDsDescriptor() == null) {
            return null;
        }

        //добавляем значения по умолчанию
        argsMap.putAll(args);

        for (ColumnDescriptor colDesc : reportDesc.getDsDescriptor().getColumns()) {
            String argParamName = getArgRootName(colDesc);
            /*
             * Если колонка НЕ описана как параметр, но значение ей передаётся в
			 * аргументах, считаем что она задана как константа.
			 */
            if (argParamName == null) { // колонка не описана как параметр ...
                // проверка на наличие данных в строке параметров - CONSTANT VALUE
                if (args.containsKey(colDesc.getColumnName())) {
                    final String argv = args.get(colDesc.getColumnName());
                    final String value = (argv != null) ? argv : null;
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
                    final String dateRangeParam = argParamName + DATE_RANGE;
                    boolean isDateRange = false;
                    boolean isNumberRange = false;
                    if (args.containsKey(dateRangeParam)) {
                        //добавляем в список параметр - обработанный ключ
                        args.put(argParamName, args.get(dateRangeParam));
                        args.remove(dateRangeParam);

                        // удаляем обработанное строкове значение "range1|range2"
                        argsMap.remove(dateRangeParam);

                        isDateRange = true;
                    } else {
                        // не нашли параметра - пробуем получить диапозон для чисел
                        final String numberRangeParam = argParamName + NUMBER_RANGE;
                        if (args.containsKey(numberRangeParam)) {
                            //добавляем в список параметр обработанный ключ
                            args.put(argParamName, args.get(numberRangeParam));

                            // удаляем обработанное строкове значение "range1|range2"
                            argsMap.remove(numberRangeParam);

                            args.remove(numberRangeParam);
                            isNumberRange = true;
                        }
                    }
                    Object bound1 = null;
                    Object bound2 = null;
                    if ((isDateRange || isNumberRange)) {
                        if (args.containsKey(argParamName)) {
                            final String[] paramValue = args.get(argParamName).split("\\|");
                            if (isDateRange) {
                                bound1 = getDateValue(paramValue[0]);
                                if (paramValue.length >= 2) {
                                    bound2 = getDateValue(paramValue[1]);
                                }
                            } else {
                                bound1 = getNumericAutoTypedValue(paramValue[0]);
                                if (paramValue.length >= 2) {
                                    bound2 = getNumericAutoTypedValue(paramValue[1]);
                                }
                            }
                        }
                    }
                    colDesc.getParameterValue().setBound1(bound1);
                    colDesc.getParameterValue().setBound2(bound2);

                    // диапазонов добавляем  действительные значение границ!
                    argsMap.put(argParamName + RANGE_LO_POSTFIX, bound1);
                    argsMap.put(argParamName + RANGE_HI_POSTFIX, bound2);

                    break;
                }
                case VALUE:
                case LIST:
                    Object bound = null;
                    if (args.containsKey(argParamName)) {
                        final String paramValue = args.get(argParamName);
                        if (paramValue != null) {
                            if (colDesc.getParameterValue().getType() == Type.VALUE) {
                                // для простых значений явно зададим один элемент
                                bound = paramValue;

                                JavaDataType.SupportedTypes type = null;
                                String destClassName = colDesc.getDataType().getClassName();
                                if (!ru.it.lecm.reports.utils.Utils.isStringEmpty(destClassName)) {
                                    type = JavaDataType.SupportedTypes.findType(destClassName);
                                }

                                if (type != null) {
                                    argsMap.put(argParamName, type.getValueByRealType(!paramValue.isEmpty() ? paramValue : null));
                                }

                                if (NodeRef.isNodeRef(paramValue)) { // для REF добавляем еще и node_id
                                    Long node_id = (Long) nodeService.getProperty(new NodeRef(paramValue), ContentModel.PROP_NODE_DBID);
                                    argsMap.put(argParamName + IDS_POSTFIX, node_id);
                                }
                            } else {
                                bound = paramValue.split("[,;]");
                                argsMap.put(argParamName, SupportedTypes.LIST.getValueByRealType(!paramValue.isEmpty() ? bound : null));
                                argsMap.put(argParamName + IDS_POSTFIX, getIdsList((List<String>) SupportedTypes.LIST.getValueByRealType(!paramValue.isEmpty() ? bound : null), nodeService));
                            }
                        }
                    }
                    colDesc.getParameterValue().setBound1(bound); // единичное значение или String[]
                    break;
                default: // непонятный тип - сообщение об ошибке и игнор ...
                    log.error(String.format("Unsupported parameter type '%s' skipped", Utils.coalesce(colDesc.getParameterValue().getType(), "NULL")));
                    break;
            }
        }

        if (args.containsKey(DataSourceDescriptor.COLNAME_ID)) {
            // нужно гарантировать колонку с ID, когда есть такой параметр ...
            ensureDataColumn(reportDesc.getDsDescriptor(), args.get(DataSourceDescriptor.COLNAME_ID), DataSourceDescriptor.COLNAME_ID, SupportedTypes.STRING);

            Object refs = SupportedTypes.LIST.getValueByRealType(args.get(DataSourceDescriptor.COLNAME_ID));
            argsMap.put(DataSourceDescriptor.COLNAME_ID, refs);
            argsMap.put(DataSourceDescriptor.COLNAME_NODE_ID, getIdsList((List<String>)refs, nodeService));
        }

        // аналогично нужно гарантировать колонку с TYPE, когда есть такой параметр или тип задан явно ...
        if (args.containsKey(DataSourceDescriptor.COLNAME_TYPE)) {
            ensureDataColumn(reportDesc.getDsDescriptor(), args.get(DataSourceDescriptor.COLNAME_TYPE), DataSourceDescriptor.COLNAME_TYPE, SupportedTypes.STRING);
            argsMap.put(DataSourceDescriptor.COLNAME_TYPE, SupportedTypes.LIST.getValueByRealType(args.get(DataSourceDescriptor.COLNAME_TYPE)));
        } else if (reportDesc.getFlags() != null && !reportDesc.getFlags().getSupportedNodeTypes().isEmpty()) {
            String typesStr = StringUtils.collectionToCommaDelimitedString(reportDesc.getFlags().getSupportedNodeTypes());
            ensureDataColumn(reportDesc.getDsDescriptor(), typesStr, DataSourceDescriptor.COLNAME_TYPE, SupportedTypes.STRING);
            argsMap.put(DataSourceDescriptor.COLNAME_TYPE, SupportedTypes.LIST.getValueByRealType(typesStr));
        }

        return argsMap;
    }


    static private List<Long> getIdsList(List<String> refsValue, NodeService nodeService) {
        List<Long> nodeIds = null;
        if (refsValue != null) {
            nodeIds = new ArrayList<Long>();
            if (!((List) refsValue).isEmpty()) {
                for (String nodeRef : refsValue) {
                    if (NodeRef.isNodeRef(nodeRef)) {
                        nodeIds.add((Long) nodeService.getProperty(new NodeRef(nodeRef), ContentModel.PROP_NODE_DBID));
                    }
                }
            }
        }
        return nodeIds;
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
            result = new ColumnDescriptor(colName, colType);

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
