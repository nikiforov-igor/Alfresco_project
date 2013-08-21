package ru.it.lecm.reports.model.impl;

import ru.it.lecm.reports.api.model.ReportType;

/**
 * Тип отчёта. Фактически на 07/2013 их два: Jasper и OOffice.
 * @author rabdullin
 *
 */
public class ReportTypeImpl
		extends MnemonicNamedItem
		implements ReportType 
{
	private static final long serialVersionUID = 1L;

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
