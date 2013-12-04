package ru.it.lecm.reports.model.impl;

import ru.it.lecm.reports.api.model.JavaClassable;
import ru.it.lecm.reports.api.model.L18able;
import ru.it.lecm.reports.api.model.Mnemonicable;


/**
 * Провайдер НД.
 * @author rabdullin
 */
public class ReportProviderDescriptor
		extends JavaClassableImpl
		implements JavaClassable, Mnemonicable, L18able
{
	private static final long serialVersionUID = 1L;


	public ReportProviderDescriptor() {
		super();
	}

	public ReportProviderDescriptor(String className, String mnem,
                                    L18able name) {
		super(className, mnem, name);
	}

	public ReportProviderDescriptor(String className, String mnem) {
		super(className, mnem);
	}

	public ReportProviderDescriptor(String className) {
		super(className);
	}

}
