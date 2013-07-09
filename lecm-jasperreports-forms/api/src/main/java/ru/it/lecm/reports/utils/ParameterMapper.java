package ru.it.lecm.reports.utils;

import java.util.Arrays;
import java.util.Map;

import org.alfresco.service.cmr.repository.NodeRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.reports.api.model.ColumnDescriptor;
import ru.it.lecm.reports.api.model.ParameterType.Type;
import ru.it.lecm.reports.api.model.ReportDescriptor;

/**
 * Распределитель request-аргументов по параметрам описателя отчёта.
 * Название аргумента для колонки данных выбирается в getArgRootName.
 */
public class ParameterMapper {

	static final transient Logger log = LoggerFactory.getLogger(ParameterMapper.class);

	/**
	 * Задать параметры из списка. Подразумевается, что параметры имеют названия 
	 * совпадающие с названиями параметра колонки данных из описателя reportDesc.getDsDescriptor.
	 * @param reportDesc
	 * @param args
	 */
	public static void assignParameters( ReportDescriptor reportDesc, Map<String, String[]> args) {

		if (args == null || reportDesc == null || reportDesc.getDsDescriptor() == null)
			return;

		for ( ColumnDescriptor colDesc: reportDesc.getDsDescriptor().getColumns()) {

			if ( colDesc.getParameterValue() == null) // колонка не описана как параметр ...
				continue;

			// если колонка параметризована ...
			final String argRootName = getArgRootName(colDesc);
			switch (colDesc.getParameterValue().getType()) {
				case VALUE:
				case LIST:
					if (args.containsKey(argRootName)) {
						final String[] paramValue = args.get(argRootName);
						log.debug(String.format( "assigning for column '%s' parameter '%s': value '%s'"
								, colDesc.getColumnName(), argRootName, Arrays.toString(paramValue)) );
						if ( (colDesc.getParameterValue().getType() == Type.VALUE)
								&& (paramValue != null) && (paramValue.length > 0)
								) // для простых значений явно зададим один элемент
							colDesc.getParameterValue().setBound1(paramValue[0]);
						else
							colDesc.getParameterValue().setBound1(paramValue);
					}
					break;
				case RANGE:
					// диапазон задаётся параметрами "{mnem}_lo" и "{mnem}_hi" ...
					final String argNameLo = argRootName + "_lo";
					final String argNameHi = argRootName + "_hi";

					if (args.containsKey(argNameLo)) {
						final String[] paramValue = args.get(argNameLo);
						log.debug(String.format( "assigning for column '%s' low-bound from arg '%s' with value '%s'"
								, colDesc.getColumnName(), argNameLo, Arrays.toString(paramValue)) );
						colDesc.getParameterValue().setBound1( (paramValue != null && paramValue.length > 0) ? paramValue[0] : null);
					}

					if (args.containsKey(argNameHi)) {
						final String[] paramValue = args.get(argNameHi);
						log.debug(String.format( "assigning for column '%s' high-bound from arg '%s' with value '%s'"
								, colDesc.getColumnName(), argNameHi, Arrays.toString(paramValue)) );
						colDesc.getParameterValue().setBound2( (paramValue != null && paramValue.length > 0) ? paramValue[0] : null);
					}
					break;

				default: // непонятный тип - сообщение об ошибке и игнор ...
					log.error( String.format("Unsupported parameter type '%s' skipped", Utils.coalesce(colDesc.getParameterValue().getType(), "NULL") ));
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
