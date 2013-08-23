package ru.it.lecm.reports.generators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import ru.it.lecm.reports.api.ReportGenerator;
import ru.it.lecm.reports.api.ReportsManager;
import ru.it.lecm.reports.beans.WKServiceKeeper;
import ru.it.lecm.reports.utils.Utils;

/**
 * Базовый класс для построителей отчётов в runtime.
 */
public abstract class ReportGeneratorBase
		implements ReportGenerator, ApplicationContextAware
{
	private static final transient Logger log = LoggerFactory.getLogger(ReportGeneratorBase.class);

	private WKServiceKeeper services; 
	private ReportsManager reportsMgr;
	private String reportsManagerBeanName;
	private ApplicationContext context;

	public void init() {
		log.info( String.format( "bean %s initialized", this.getClass().getName()));
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
		this.context = ctx;
	}

	public WKServiceKeeper getServices() {
		return services;
	}

	public void setServices(WKServiceKeeper services) {
		this.services = services;
	}

	public String getReportsManagerBeanName() {
		return reportsManagerBeanName;
	}

	public void setReportsManagerBeanName(String beanName) {
		if ( Utils.isSafelyEquals(beanName, reportsManagerBeanName) )
			return;
		log.debug(String.format("ReportsManagerBeanName assigned: %s", beanName));
		this.reportsManagerBeanName = beanName;
		this.reportsMgr = null; // очистка
	}

	public ReportsManager getReportsManager() {
		if (this.reportsMgr == null && this.reportsManagerBeanName != null) {
			this.reportsMgr = (ReportsManager) this.context.getBean(this.reportsManagerBeanName);
		}
		return this.reportsMgr;
	}


}
