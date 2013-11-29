package ru.it.lecm.reports.api.model;

import java.util.Comparator;
import java.util.List;

import ru.it.lecm.reports.utils.Utils;

public interface ReportDescriptor extends Mnemonicable, L18able {

	/**
	 * Описатель данных.
	 */
	DataSourceDescriptor getDsDescriptor();

	/**
	 * Тип отчёта (и провайдера). Ключ уникальности - обычная мнемоника.
	 * Список доуступных отчётов расширяем, но требует разворачивания соот-щих 
	 * jar-ок дл яновых провайдеров.
	 */
	ReportType getReportType();

	/**
	 * Шаблон для построения отчёта (файл) в терминах провайдера.
	 * Например jrxml-файл для Jasper-отчёта.
	 */
	ReportTemplate getReportTemplate();

	ReportProviderDescriptor getProviderDescriptor();

	ReportFlags getFlags();

	/**
	 * @return список подотчётов или null если нет таковых
	 */
	List<SubReportDescriptor> getSubreports();
	void setSubreports(List<SubReportDescriptor> list);

	/**
	 * Сортировка по-умолчанию для списков Дескрипторов отчёта будет по алфавиту по названиям (кодам).
	 * Здесь null-значения будут ниже в списке ("тяжёлые").
	 */
	static class Comparator_ПоАлфавиту 
			implements Comparator<ReportDescriptor>
	{
		@Override
		public int compare(ReportDescriptor rd1, ReportDescriptor rd2) {
			if (rd1 == rd2)
				return 0;
			final String name1 = Utils.nonblank( rd1.getDefault(), rd1.getMnem());
			final String name2 = Utils.nonblank( rd2.getDefault(), rd2.getMnem());
			// null == null, null > any other
			if (name1 == null)
				return (name2 == null) ? 0 : 1;
			if (name2 == null) 
				return -1; // x < null

			// here name1 <> null, name2 <> null ...
			return name1.compareToIgnoreCase(name2);
		}
	}

    boolean isSubReport();

    boolean isSQLDataSource();
}
