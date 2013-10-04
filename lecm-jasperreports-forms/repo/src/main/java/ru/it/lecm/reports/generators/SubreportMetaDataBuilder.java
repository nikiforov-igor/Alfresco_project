package ru.it.lecm.reports.generators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.reports.api.model.ColumnDescriptor;
import ru.it.lecm.reports.api.model.DataSourceDescriptor;
import ru.it.lecm.reports.api.model.SubReportDescriptor;
import ru.it.lecm.reports.model.impl.ColumnDescriptorImpl;
import ru.it.lecm.reports.model.impl.JavaDataTypeImpl.SupportedTypes;
import ru.it.lecm.reports.model.impl.SubReportDescriptorImpl;
import ru.it.lecm.reports.utils.Utils;

/**
 * Построение описателей подотчётов из исходного линейного raw-списка колонок,
 * который редактируется модулем lecm-report-editor.
 * <br/> {@link #SubreportMetaDataBuilder} нужен только в силу ограниченных возможностей report-editor.
 * <br/> Предполагается, что подотчёты будут представлены в списке колонок 
 * <li> основным полем с описанием подотчёта (сейчас это одно значение - атрибут-
 * ассоциация для получения вложенного списка)
 * <li> и всеми его колонками.
 * <br/> см {@link #scanSubreports(Collection)}
 * @author rabdullin
 * @created 2013/09
 */
public class SubreportMetaDataBuilder {

	static final transient Logger logger = LoggerFactory.getLogger(SubreportMetaDataBuilder.class);

	/** часть имени составного колонки, которая является частью отчёта */
	final static public String SUFFIX_SUBREPORT_COLUMN = ".sub.";

	/** 
	 * Регулярное выражение для выборки Текста из конструкций вида:
	 *    "subreport::Текст"
	 *    "{subreport::Текст}"
	 *    "{{subreport::Текст}}"
	 * т.е. тег "subreport::", нужный текст и допустимы необязательные одна-две 
	 * скобки "{" в начале и одна-две "}" в конце.
	 * достаточная группировка: самого Текста и после Текста.
	 * <hr/>
	 * <br/> Пример разбора <b>'subreport::ссылка'</b>
	 * <br/>    выражением regexp '[{]{0,2}subreport[:][:]([^}]+)([}]{0,2})' 
	 * <br/>    matches as:
	 * <b>
	 * <li> group[0]		'subreport::ссылка'
	 * <li>	group[1]		'ссылка:a'
	 * </b> 
	 */
	final static public String REGEXP_SUBREPORTLINK = "[{]{0,2}subreport[:][:]([^}]+)([}]{0,2})";

	/** группа для ссылки в выражении {@link #REGEXP_SUBREPORTLINK} */
	final static public int GRPINDEX__SUBREPORTLINK = 1;

	/**
	 * Выполнить разбор строки вида: 
	 *    "subreport::ссылка"
	 *    "{subreport::ссылка}"
	 *    "{{subreport::ссылка}}"
	 * т.е. тег "subreport::" и нужная ссылка (из букв, цифр, минуса, 
	 * подчёркивания, двоеточия, возможно запятых, точки-с-запятой и пр, 
	 * кроме '{'/'}'), и допустимы необязательные одна-две скобки "{" в начале 
	 * и одна-две "}" в конце.  
	 * @param sublink
	 * @return ссылка, если sublink удовлетворяет синтаксису, или null иначе.
	 */
	public final static String parseSubreportLink(String sublink) {
		if (sublink == null || sublink.length() == 0)
			return null;
 		final Pattern REG_EXP = Pattern.compile( REGEXP_SUBREPORTLINK);
		final Matcher m = REG_EXP.matcher(sublink);

		return m.matches() ? m.group(GRPINDEX__SUBREPORTLINK) : null;
	}

//	/**
//	 * Выделить ссылку из строки вида "{{subreport::ссылка}}"
//	 * @param expression
//	 * @return
//	 */
//	final private static String PREFIX_SUBREPORT_LINK = "{{subreport::";
//	private static String extractSourceLink(String expression) {
//		if (expression == null)
//			return null;
//		if (!expression.startsWith(PREFIX_SUBREPORT_LINK)) // нет префикса - принимаем всё за ссылку
//			return expression;
//		return expression.substring(PREFIX_SUBREPORT_LINK.length(), expression.length() - 2);
//	}


	/**
	 * Разборать имя колонки, если она относится к подотчёту. 
	 * <br/> Имя колонки подотчёта имеет вид: 
	 * <br/> <b> "ИмяПодотчёта .sub. ИмяКолонки" </b> 
	 * @param colname имя проверяемой и разбираемой колонки
	 * @return массив из двух элементов - название подтчёта и название колонки,
	 * <br/> или null, если имя не относится к какому-либо подотчёту 
	 */
	public final static String[] parseSubreportColName(String colname) {
		if (Utils.isStringEmpty(colname) ) // это точно не колонка подотчёта 
			return null; 
		final int pos = colname.indexOf(SUFFIX_SUBREPORT_COLUMN);
		if (pos < 0) // имя не является шаблонным для колонки подотчёта
			return null;

		// выделение имени отчёта и названия колонки
		final String reportName = colname.substring(0, pos);
		final String subfieldName = colname.substring( pos + SUFFIX_SUBREPORT_COLUMN.length());
		if (Utils.isStringEmpty(reportName) || Utils.isStringEmpty(subfieldName) )
			// не указано название отчёта или "под-колонки"
			return null;
		return new String[] { reportName, subfieldName};
	}

	/**
	 * Формирование списка дексриптовров подотчётов из линейного списка колонок
	 * указанного НД (если они там есть, конечно).
	 * <br/>Предполагается следующая структура описания подотчёта в общем 
	 * линейном списке колонок:
	 * <li> есть колонка <b>описания отчёта</b> - это колонка название которой 
	 * совпадает с названием подотчёта,
	 * <br>а в её expression указана ссылка на вложенный список данных Альфреско 
	 * (список подотчёта), в виде указания qname-ассоциации, отн-но объекта
	 * основного НД:
	 * <br/> <b> "{{subreport::ссылка-на-вложенный-список}}" </b>
	 * </li>
	 * <li> есть все колонки подотчёта поотдельности, их имена имеют составной вид:
	 * <br/> <b>"НазПодотчёта .sub. КолонкаПодотчёта"</b>
	 * 
	 * @param desc описатель НД
	 * @return null, если нет подотчётов или непустой список описаний подотчётов.
	 */
	public static List<SubReportDescriptor> scanSubreports(DataSourceDescriptor desc) {

		if (desc == null || desc.getColumns() == null || desc.getColumns().isEmpty())
			return null;

		final Map<String, SubReportDescriptorImpl> newSubreports = new LinkedHashMap<String, SubReportDescriptorImpl>();

		/*
		 *  выбираем все колонки, которые содержат ".sub." в именах, считая 
		 *  что это части "SubReportName.sub.ColName" ...
		 */
		for ( ColumnDescriptor srcCol: desc.getColumns()) {

			/* выделение имени отчёта и названия колонки */
			final String reportName, subfieldName;
			{
				final String[] parts = parseSubreportColName(srcCol.getColumnName());
				if (parts == null) // не является колонкой подотчёта
					continue;

				reportName = parts[0];
				subfieldName = parts[1];
			}

			/* получение описателя подотчёта ... */
			final SubReportDescriptorImpl sr;
			{
				if (newSubreports.containsKey(reportName)) { // уже был ...
					sr = newSubreports.get(reportName);
				} else { // такой подотчёт ещё не встречался - создать ...

					// колонка подотчёта должна иметься в основном отчёте отдельно ... 
					final ColumnDescriptor reportMainCol = desc.findColumnByName(reportName);
					if (reportMainCol == null)
						throw new RuntimeException( String.format("Column '%s' defines subreport. But there is no main column '%s' for the subreport itself", srcCol.getColumnName(), reportName));
					sr = createSubreportDesc(reportMainCol, srcCol);

					// (!) Создание ообъекта подотчёта
					// подотчёт называем также что и его колонка ...
					newSubreports.put(reportName, sr);
					sr.setDestColumnName( reportName); // целевая колонка

					// источник данных для вложенного списка полей должен быть 
					// указан как expression в колонке описания подотчёта
					final String sourceLink = parseSubreportLink(reportMainCol.getExpression());
					if (Utils.isStringEmpty(sourceLink)) // если ссылки не указано - поругаемся ...
						throw new RuntimeException( String.format(
								"Column '%s' defines subreport. But main subreport definition at column '%s'\n did not define expression '{{subreport::child-assoc}}' for subreport list"
								, srcCol.getColumnName(), reportMainCol.getColumnName()));
					sr.setSourceListExpression( sourceLink);

					// TODO: + beanClass, format, ifEmpty, delimiter

					// тип колонки в основном отчёте, которая соот-вет подотчёту:
					//    String, если используется форматирование
					//    List, иначе
					final Class<?> classOfMainReportColumn = (sr.isUsingFormat())
								? String.class
								: List.class;
					reportMainCol.setClassName(classOfMainReportColumn.getName());
				}
			}

			/* создание новой колонки ... */
			if (sr.getDsDescriptor().findColumnByName(subfieldName) != null) {
				// повтор определения колонки подотчёта ...
				logger.warn( String.format( "Column '%s' of subreport '%s' is defined several times -> only first one applied",  subfieldName, reportName));
				continue;
			}

			final ColumnDescriptorImpl newSubrepCol = new ColumnDescriptorImpl( subfieldName,  SupportedTypes.STRING);
			newSubrepCol.assign( srcCol);

			sr.getDsDescriptor().getColumns().add(newSubrepCol); // добавление в описатель

			/* обновление/формирование sourceMap для subreport */
			if (sr.getSubItemsSourceMap() == null)
				sr.setSubItemsSourceMap( new HashMap<String, String>());
			// NOTE: (!) здесь используем "усечённое" название колонки subfieldName,
			// которое не содержит ".sub.", вместо полного srcCol.getColumnName()
			sr.getSubItemsSourceMap().put( newSubrepCol.getColumnName(), srcCol.getExpression()); 
		} // for

		return (newSubreports.isEmpty()) ? null : new ArrayList<SubReportDescriptor>(newSubreports.values());
	}

	/**
	 * Создание подотчёта на основании колонки НД (колонки отчёта с его же мнемоническим названием) 
	 * @param reportMainCol
	 * @param subSrcCol одна из колонок подотчёта (на её основании и создаётся подотчёт)
	 * информационный характер
	 * @return
	 */
	private final static SubReportDescriptorImpl createSubreportDesc(final ColumnDescriptor reportMainCol
				, final ColumnDescriptor subSrcCol)
	{
		// колонка подотчёта должна иметься в основном отчёте явно и отдельно ... 

		// подотчёт называем также что и его колонка ...
		final String reportName = reportMainCol.getColumnName();

		// !) Создание ообъекта подотчёта
		final SubReportDescriptorImpl srResult = new SubReportDescriptorImpl( reportName);
		srResult.setDestColumnName( reportName); // целевая колонка - это главная колонка отчёта

		// источник данных для вложенного списка полей должен быть указан как 
		// expression в колонке описания подотчёта ...
		final String sourceLink = parseSubreportLink(reportMainCol.getExpression());
		if (Utils.isStringEmpty(sourceLink)) // если ссылки не указано - поругаемся ...
			throw new RuntimeException( String.format(
					"Column '%s' defines subreport. But main subreport definition at column '%s'\n did not define expression '{{subreport::child-assoc}}' for subreport list"
					, ( subSrcCol != null ? subSrcCol.getColumnName() : "NULL")
					, reportMainCol.getColumnName()));
		srResult.setSourceListExpression( sourceLink);

		// TODO: + beanClass, format, ifEmpty, delimiter

		// тип колонки в основном отчёте, которая соот-вет подотчёту:
		//    String, если используется форматирование
		//    List, иначе
		final Class<?> classOfMainReportColumn = (srResult.isUsingFormat())
					? String.class
					: List.class;
		reportMainCol.setClassName(classOfMainReportColumn.getName());

		return srResult;
	}
}
