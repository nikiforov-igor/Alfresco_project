package ru.it.lecm.reports.utils;

import org.alfresco.service.cmr.repository.NodeRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.reports.api.model.ColumnDescriptor;
import ru.it.lecm.reports.api.model.DataSourceDescriptor;
import ru.it.lecm.reports.api.model.ParameterType.Type;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.model.impl.ColumnDescriptorImpl;
import ru.it.lecm.reports.model.impl.JavaDataTypeImpl.SupportedTypes;
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
			String argRootName = getArgRootName(colDesc);// colDesc.getColumnName();
			switch (colDesc.getParameterValue().getType()) {
			case RANGE: {
				// проверяем диапозон дат
				final String dateRangeParam = argRootName + DATE_RANGE;
				boolean isDateRange = false;
				boolean isNumberRange = false;
				if (args.containsKey(dateRangeParam)) {
					argRootName = dateRangeParam;
					isDateRange = true;
				} else {
					// не нашли параметра - пробуем получить диапозон для чисел
					final String numberRangeParam = argRootName + NUMBER_RANGE;
					if (args.containsKey(numberRangeParam)) {
						argRootName = numberRangeParam;
						isNumberRange = true;
					}
				}
				Object bound1 = null;
				Object bound2 = null;
				if ((isDateRange || isNumberRange)){
					if (args.containsKey(argRootName)) {
						final String[] paramValue = args.get(argRootName)[0].split("\\|");
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
							final String[] paramValues = paramValue[0].split(",");
							bound = paramValues;
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

		if (args.containsKey(DataSourceDescriptor.COLNAME_ID)){
			// нужно гарантировать колонку с ID, когда есть такой параметр ...
			ensureDataColumn( reportDesc.getDsDescriptor(), args.get(DataSourceDescriptor.COLNAME_ID), DataSourceDescriptor.COLNAME_ID, SupportedTypes.STRING);
		}

		// аналогично нужно гарантировать колонку с TYPE, когда есть такой параметр или тип задан явно ...
		if (args.containsKey(DataSourceDescriptor.COLNAME_TYPE)){
			ensureDataColumn( reportDesc.getDsDescriptor(), args.get(DataSourceDescriptor.COLNAME_TYPE), DataSourceDescriptor.COLNAME_TYPE, SupportedTypes.STRING);
		} else if (reportDesc.getFlags() != null && !Utils.isStringEmpty(reportDesc.getFlags().getPreferedNodeType()) ) {
			ensureDataColumn( reportDesc.getDsDescriptor(), reportDesc.getFlags().getPreferedNodeType(), DataSourceDescriptor.COLNAME_TYPE, SupportedTypes.STRING);
		}
	}


	/**
	 * Гарантировать наличие колонки с указанным названием и присвоить ей указанное значение.
	 * Если уже колонка есть - просто присвоить её значение, иначе создать новую с указанным типом
	 * и потом присвоить значение. 
	 * @param dsDesc описатель НД
	 * @param value значение
	 * @param colName название колонки
	 * @param colType тип колонки 
	 * @return
	 */
	private static ColumnDescriptor ensureDataColumn( DataSourceDescriptor dsDesc
			, Object value, String colName, SupportedTypes colType) {
		ColumnDescriptor result = dsDesc.findColumnByName(colName);
		if ( result == null) { // создание новой колонки ...
			result = new ColumnDescriptorImpl( colName, colType);

			{ // задание типа параметра этой колонки ...
				final ParameterTypedValueImpl ptv = new ParameterTypedValueImpl(colName);
				// by default: ptv.setType( Type.VALUE);
				result.setParameterValue(ptv);
			}

			dsDesc.getColumns().add(result);
			result.getParameterValue().setBound1(value);
		}
		return result;
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
		// example: colDesc.getParameterValue().getMnem() == "PARAM_TYPE_VALUE"
		return colDesc.getColumnName();
	}

	/**
	 * Получить значение параметра в виде ссылки на узел
	 *
	 * @param colDesc
	 * @return
	 */
	public static final List<NodeRef> getArgAsNodeRef(final ColumnDescriptor colDesc) {
		List<NodeRef> result = new ArrayList<NodeRef>();

		if (colDesc == null || colDesc.getParameterValue() == null) {
			return result;
		}

		final Object argValue = colDesc.getParameterValue().getBound1();
		if (argValue == null) {
			return result;
		}

		final String[] nodeRefs = (argValue instanceof String[])
						? (String[]) argValue
						: argValue.toString().split(",;");
		for (String item : nodeRefs) {
			if (NodeRef.isNodeRef(item)) {
				result.add(new NodeRef(item.toString()));
			}
		}
		return result;
	}
}
