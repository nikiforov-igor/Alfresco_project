package ru.it.lecm.reports.model.impl;

import ru.it.lecm.reports.api.model.L18able;
import ru.it.lecm.reports.api.model.ReportProviderDescriptor;


/**
 * Провайдер НД.
 * @author rabdullin
 */
public class ReportProviderDescriptorImpl
		extends JavaClassableImpl
		implements ReportProviderDescriptor
{
	private static final long serialVersionUID = 1L;


	public ReportProviderDescriptorImpl() {
		super();
	}

	public ReportProviderDescriptorImpl(String className, String mnem,
			L18able name) {
		super(className, mnem, name);
	}

	public ReportProviderDescriptorImpl(String className, String mnem) {
		super(className, mnem);
	}

	public ReportProviderDescriptorImpl(String className) {
		super(className);
	}

}
