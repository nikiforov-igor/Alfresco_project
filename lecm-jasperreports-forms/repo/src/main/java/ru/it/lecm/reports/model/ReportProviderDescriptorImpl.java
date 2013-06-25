package ru.it.lecm.reports.model;

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
