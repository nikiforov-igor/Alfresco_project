package ru.it.lecm.reports.generators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.reports.api.model.ColumnDescriptor;
import ru.it.lecm.reports.api.model.DataSourceDescriptor;
import ru.it.lecm.reports.api.model.SubReportDescriptor;
import ru.it.lecm.reports.model.impl.ColumnDescriptorImpl;
import ru.it.lecm.reports.model.impl.JavaDataTypeImpl.SupportedTypes;
import ru.it.lecm.reports.model.impl.SubReportDescriptorImpl;
import ru.it.lecm.reports.utils.Utils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Построение описателей подотчётов из исходного линейного raw-списка колонок,
 * который редактируется модулем lecm-report-editor.
 * <br/> {@link #SubreportMetaDataBuilder} нужен только в силу ограниченных возможностей report-editor.
 * <br/> Предполагается, что подотчёты будут представлены в списке колонок
 * <li> основным полем с описанием подотчёта (сейчас это одно значение - атрибут-
 * ассоциация для получения вложенного списка)
 * <li> и всеми его колонками.
 * <br/> см {@link #SubreportMetaDataBuilder.scanSubreports(Collection)}
 * @author rabdullin
 * @created 2013/09
 */
public class SubreportMetaDataBuilder {

	static final transient Logger logger = LoggerFactory.getLogger(SubreportMetaDataBuilder.class);

	/**
	 * часть имени составного колонки, которая является частью отчёта
	 */
	final static public String SUFFIX_SUBREPORT_COLUMN = ".sub.";

	/**
	 * Разборать имя колонки, если она относится к подотчёту.
	 * <br/> Имя колонки подотчёта имеет вид:
	 * <br/> <b> "ИмяПодотчёта .sub. ИмяКолонки" </b>
	 *
	 * @param colname имя проверяемой и разбираемой колонки
	 * @return массив из двух элементов - название подтчёта и название колонки,
	 *         <br/> или null, если имя не относится к какому-либо подотчёту
	 */
	public static String[] parseSubreportColName(String colname) {
		if (Utils.isStringEmpty(colname)) {
			// это точно не колонка подотчёта
			return null;
		}
		final int pos = colname.indexOf(SUFFIX_SUBREPORT_COLUMN);
		if (pos < 0) {
			// имя не является шаблонным для колонки подотчёта
			return null;
		}

		// выделение имени отчёта и названия колонки
		final String reportName = colname.substring(0, pos);
		final String subfieldName = colname.substring(pos + SUFFIX_SUBREPORT_COLUMN.length());
		if (Utils.isStringEmpty(reportName) || Utils.isStringEmpty(subfieldName))
			// не указано название отчёта или "под-колонки"
			return null;
		return new String[]{reportName, subfieldName};
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
        if (desc == null || desc.getColumns() == null || desc.getColumns().isEmpty()) {
            return null;
        }

        final Map<String, SubReportDescriptorImpl> newSubreports = new LinkedHashMap<String, SubReportDescriptorImpl>();

		/*
         *  выбираем все колонки, которые содержат ".sub." в именах, считая
		 *  что это части "SubReportName.sub.ColName" ...
		 */
        for (ColumnDescriptor srcCol : desc.getColumns()) {
			/* выделение имени отчёта и названия колонки */
            final String reportName, subfieldName;

            final String[] parts = parseSubreportColName(srcCol.getColumnName());
            if (parts == null) {
                // не является колонкой подотчёта
                continue;
            }

            reportName = parts[0];
            subfieldName = parts[1];

			/* получение описателя подотчёта ... */
            final SubReportDescriptorImpl sr;

            if (newSubreports.containsKey(reportName)) { // уже был ...
                sr = newSubreports.get(reportName);
            } else { // такой подотчёт ещё не встречался - создать ...
                // колонка подотчёта должна иметься в основном отчёте отдельно ...
                final ColumnDescriptor reportMainCol = desc.findColumnByName(reportName);
                if (reportMainCol == null) {
                    throw new RuntimeException(String.format("Column '%s' defines subreport. But there is no main column '%s' for the subreport itself", srcCol.getColumnName(), reportName));
                }
                final ColumnDescriptor sourceColumn = desc.findColumnByName(reportName + SUFFIX_SUBREPORT_COLUMN + "source");
                final ColumnDescriptor typeColumn = desc.findColumnByName(reportName + SUFFIX_SUBREPORT_COLUMN + "type");
                final ColumnDescriptor beanClassColumn = desc.findColumnByName(reportName + SUFFIX_SUBREPORT_COLUMN + "beanClass");
                sr = createSubreportDesc(reportMainCol, sourceColumn, typeColumn, beanClassColumn, srcCol);

                newSubreports.put(reportName, sr);
            }

			/* создание новой колонки ... */
            if (sr.getDsDescriptor().findColumnByName(subfieldName) != null) {
                // повтор определения колонки подотчёта ...
                logger.warn(String.format("Column '%s' of subreport '%s' is defined several times -> only first one applied", subfieldName, reportName));
                continue;
            }

            if (!subfieldName.equals("source") && !subfieldName.equals("type") && !subfieldName.equals("beanClass")) {
                final ColumnDescriptorImpl newSubrepCol = new ColumnDescriptorImpl(subfieldName, SupportedTypes.STRING);
                newSubrepCol.assign(srcCol);

                sr.getDsDescriptor().getColumns().add(newSubrepCol); // добавление в описатель

			/* обновление/формирование sourceMap для subreport */
                if (sr.getSubItemsSourceMap() == null) {
                    sr.setSubItemsSourceMap(new HashMap<String, String>());
                }
                // NOTE: (!) здесь используем "усечённое" название колонки subfieldName,
                // которое не содержит ".sub.", вместо полного srcCol.getColumnName()
                sr.getSubItemsSourceMap().put(newSubrepCol.getColumnName(), srcCol.getExpression());
            }
        } // for

        return (newSubreports.isEmpty()) ? null : new ArrayList<SubReportDescriptor>(newSubreports.values());
    }

	/**
	 * Создание подотчёта на основании колонки НД (колонки отчёта с его же мнемоническим названием)
	 *
	 * @param reportMainCol
	 * @param subSrcCol     одна из колонок подотчёта (на её основании и создаётся подотчёт)
	 *                      информационный характер
	 * @return
	 */
    private static SubReportDescriptorImpl createSubreportDesc(final ColumnDescriptor reportMainCol,
                                                               final ColumnDescriptor sourceColumn,
                                                               final ColumnDescriptor typeColumn,
                                                               final ColumnDescriptor beanClassColumn,
                                                               final ColumnDescriptor subSrcCol) {
        // колонка подотчёта должна иметься в основном отчёте явно и отдельно ...
        // подотчёт называем также что и его колонка ...
        final String reportName = reportMainCol.getColumnName();

        // !) Создание ообъекта подотчёта
        final SubReportDescriptorImpl srResult = new SubReportDescriptorImpl(reportName);
        srResult.setDestColumnName(reportName); // целевая колонка - это главная колонка отчёта

        // источник данных для вложенного списка полей должен быть указан как дополнительна колонка в основном отчете
        // expression в колонке описания подотчёта ...
        String sourceLink = reportMainCol.getExpression();
        if (sourceColumn != null) {
            sourceLink = sourceColumn.getExpression();
        }
        if (Utils.isStringEmpty(sourceLink)) {
            // если ссылки не указано - поругаемся ...
            throw new RuntimeException(String.format(
                    "Column '%s' defines subreport. But main subreport definition at column '%s'\n did not define expression '{{subreport::child-assoc}}' for subreport list"
                    , (subSrcCol != null ? subSrcCol.getColumnName() : "NULL")
                    , reportMainCol.getColumnName()));
        }
        srResult.setSourceListExpression(sourceLink);

        // тип данных для вложенного списка полей должен быть указан как доп колонка в основном отчете
        String sourceType = null;
        if (typeColumn != null) {
            sourceType = typeColumn.getExpression();
        }
        srResult.setSourceListType(sourceType);

        // TODO: + beanClass, format, ifEmpty, delimiter
        String beanClass = null;
        if (beanClassColumn != null) {
            beanClass = beanClassColumn.getExpression();
            srResult.setBeanClassName(beanClass);
        }

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
