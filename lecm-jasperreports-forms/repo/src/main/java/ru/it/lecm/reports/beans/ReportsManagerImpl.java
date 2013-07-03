package ru.it.lecm.reports.beans;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.reports.api.ReportsManager;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.api.model.DAO.ReportDAO;
import ru.it.lecm.reports.jasper.utils.Utils;

public class ReportsManagerImpl implements ReportsManager {

	static final transient Logger log = LoggerFactory.getLogger(ReportsManagerImpl.class);

	/**
	 * Список зарегистрирванных отчётов
	 */
	private List<ReportDescriptor> descriptors;

	private ReportDAO reportDAO;

	public ReportDAO getReportDAO() {
		return reportDAO;
	}

	public void setReportDAO(ReportDAO reportDAO) {
		this.reportDAO = reportDAO;
	}

	public List<ReportDescriptor> getDescriptors() {
		if (this.descriptors == null)
			this.descriptors = new ArrayList<ReportDescriptor>();
		return this.descriptors;
	}

	public void setDescriptors(List<ReportDescriptor> list) {
		this.descriptors = list;
	}

	public void regReportDescriptor(ReportDescriptor desc) {
		if (desc != null)
			getDescriptors().add(desc);
	}

	/**
	 * Получить дексриптор отчёта по его мнемонике или вернуть null.
	 * Поиск ведётся по зарегистрированным отчётам в this.descriptors и в БД.
	 * @param reportMnemoName дескриптор
	 * @return описатеть отчёта или null, если не найден
	 */
	@Override
	public ReportDescriptor getReportDescriptor(String reportMnemoName) {

		for(ReportDescriptor d: getDescriptors()) {
			if (Utils.isSafelyEquals(reportMnemoName, d.getMnem()))
				return d; // FOUND by Mnemonic
		}

		// попытка загрузить DAO-объект
		if (reportDAO != null) {
			final ReportDescriptor d = reportDAO.getReportDescriptor(reportMnemoName);
			if (d != null)
				return d; // FOUND by DAO mnemonic
		}

		log.warn(String.format( "Report '%s' has no descriptor", reportMnemoName));
		return null; // NOT FOUND
	}

	public void init() {
		log.info( String.format( " initialized templates count %s\n%s",
					getDescriptors().size(), Utils.getAsString(getDescriptors()) 
		));
	}
}
