package ru.it.lecm.reports.model;

import ru.it.lecm.reports.api.model.ReportType;

/**
 * Тип отчёта. Файктически на 07/2013 их два: Jasper и OOffice.
 * @author rabdullin
 *
 */
public class ReportTypeImpl
		extends MnemonicNamedItem
		implements ReportType {

	public ReportTypeImpl() {
		super();
	}

	public ReportTypeImpl(String mnem, L18Value name) {
		super(mnem, name);
	}

	public ReportTypeImpl(String mnem) {
		this(mnem, null);
	}

}
