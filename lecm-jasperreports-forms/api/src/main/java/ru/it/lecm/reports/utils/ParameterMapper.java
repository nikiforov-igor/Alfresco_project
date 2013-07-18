package ru.it.lecm.reports.utils;

import org.alfresco.service.cmr.repository.NodeRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.reports.api.model.ColumnDescriptor;
import ru.it.lecm.reports.api.model.ParameterType.Type;
import ru.it.lecm.reports.api.model.ReportDescriptor;

import java.util.Map;

/**
 * Распределитель request-аргументов по параметрам описателя отчёта.
 * Название аргумента для колонки данных выбирается в getArgRootName.
 */
public class ParameterMapper {
    static final transient Logger log = LoggerFactory.getLogger(ParameterMapper.class);
    public static final String DATE_RANGE = "-date-range";
    public static final String NUMBER_RANGE = "-range";

    /**
	 * Задать параметры из списка. Подразумевается, что параметры имеют названия 
	 * совпадающие с названиями параметра колонки данных из описателя reportDesc.getDsDescriptor.
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
                        if ((colDesc.getParameterValue().getType() == Type.VALUE) && (paramValue != null) && (paramValue.length > 0)){
                            // для простых значений явно зададим один элемент
                            colDesc.getParameterValue().setBound1(paramValue[0]);
                        } else {
                            colDesc.getParameterValue().setBound1(paramValue);
                        }
                    }
                    break;
                case RANGE:
                    // проверяем диапозон дат
                    String dateRangeParam = argRootName + DATE_RANGE;
                    if (args.containsKey(dateRangeParam)) {
                        argRootName = dateRangeParam;
                    } else {
                        // не нашли параметра - пробуем получить диапозон для чисел
                        String numberRangeParam = argRootName + NUMBER_RANGE;
                        if (args.containsKey(numberRangeParam)) {
                            argRootName = numberRangeParam;
                        }
                    }
                    if (args.containsKey(argRootName)) {
                        final String[] paramValue = args.get(argRootName)[0].split("\\|");
                        colDesc.getParameterValue().setBound1((paramValue[0] != null && paramValue[0].length() > 0) ? paramValue[0] : null);
                        if (paramValue.length == 2) {
                            colDesc.getParameterValue().setBound2((paramValue[1] != null && paramValue[1].length() > 0) ? paramValue[1] : null);
                        }
                    }

                    break;
                default: // непонятный тип - сообщение об ошибке и игнор ...
                    log.error(String.format("Unsupported parameter type '%s' skipped", Utils.coalesce(colDesc.getParameterValue().getType(), "NULL")));
                    break;
            }
        }
    }


	/**
	 * Получение названия аргумента, который соот-ет параметризации колонки.
	 * Для параметров списков и значений будет получено конечное название,
	 * для интервалов - корневое, а реальные значения образуются из него 
	 * добавлением окончаний "_lo" и "_hi".
	 * @param colDesc колонка, для которой получить название "её" аргумента 
	 * @return NULL, если колонка не является параметризуемой,
	 * иначе мнемоника параметра, а если она не задана - название колонки (columnName).
	 */
	public static String getArgRootName(final ColumnDescriptor colDesc) {
		if (colDesc == null || colDesc.getParameterValue() == null) // не параметризуется ...
			return null;
		final String paramRootName = Utils.coalesce(
					colDesc.getParameterValue().getMnem()
					, colDesc.getColumnName());
		return paramRootName;
	}

	/**
	 * Получить значение параметра в виде ссылки на узел
	 * @param colDesc
	 * @return
	 */
	public static NodeRef getArgAsNodeRef(final ColumnDescriptor colDesc) {
		if (colDesc == null || colDesc.getParameterValue() == null) // не параметризуется ...
			return null;
		final Object argValue = colDesc.getParameterValue().getBound1();
		if (argValue == null)
			return null;
		if (argValue instanceof NodeRef)
			return (NodeRef) argValue;
		return new NodeRef( argValue.toString() );
	}
}
