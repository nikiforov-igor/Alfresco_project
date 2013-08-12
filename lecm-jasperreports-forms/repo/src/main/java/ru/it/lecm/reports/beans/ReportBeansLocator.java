package ru.it.lecm.reports.beans;

import ru.it.lecm.reports.api.ReportsManager;

public final class ReportBeansLocator {

	private ReportBeansLocator() {}

	private static ReportsManager reportsManager;

	public static ReportsManager getReportsManager() {
		return reportsManager;
	}

	public static void setReportsManager(ReportsManager reportsMgr) {
		reportsManager = reportsMgr;
	}
}
