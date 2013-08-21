package ru.it.lecm.reports.api.model;

import java.util.Comparator;

import ru.it.lecm.reports.utils.Utils;

public interface ReportDescriptor extends Mnemonicable, L18able {

	DataSourceDescriptor getDsDescriptor();

	ReportType getReportType();

	ReportTemplate getReportTemplate();

	ReportProviderDescriptor getProviderDescriptor();

	ReportFlags getFlags();


	static class Comparator_ПоАлфавиту 
			implements Comparator<ReportDescriptor>
	{
		@Override
		public int compare(ReportDescriptor rd1, ReportDescriptor rd2) {
			if (rd1 == rd2)
				return 0;
			final String name1 = Utils.coalesce( rd1.getDefault(), rd1.getMnem());
			final String name2 = Utils.coalesce( rd2.getDefault(), rd2.getMnem());
			// null == null, null > any other
			if (name1 == null)
				return (name2 == null) ? 0 : 1;
			if (name2 == null) 
				return -1; // x < null

			// here name1 <> null, name2 <> null ...
			return name1.compareToIgnoreCase(name2);
		}
	}
}
