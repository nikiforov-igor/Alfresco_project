package ru.it.lecm.reports.model.impl;

import ru.it.lecm.reports.api.model.L18able;
import ru.it.lecm.reports.api.model.Mnemonicable;

import java.io.Serializable;

/**
 * Тип отчёта. Фактически на 07/2013 их два: Jasper и OOffice.
 * @author rabdullin
 *
 */
public class ReportType extends MnemonicNamedItem implements L18able, Mnemonicable, Serializable {
	private static final long serialVersionUID = 1L;

    /**
     * Мнемоника для Jasper-отчётов
     */
    final public static String RTYPE_MNEMO_JASPER = "JASPER";

    /**
     * Мнемоника для OpenOffice-отчётов
     */
    final public static String RTYPE_MNEMO_OOFFICE = "OOFFICE";

	/**
     * Мнемоника для OpenOffice Calc отчётов
     */
    final public static String RTYPE_MNEMO_OOCALC = "OOCALC";

	public ReportType() {
		super();
	}

	public ReportType(String mnem, L18Value name) {
		super(mnem, name);
	}

	public ReportType(String mnem) {
		this(mnem, null);
	}
}
