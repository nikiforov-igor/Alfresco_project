package ru.it.lecm.reports.model.DAO;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.api.model.ReportTemplate;
import ru.it.lecm.reports.api.model.DAO.ReportDAO;
import ru.it.lecm.reports.beans.WKServiceKeeper;

// import ru.it.lecm.reports.api.model.DAO.ReportDAO;
// TODO: implements ReportDAO
public class ReportDAOImpl implements ReportDAO {

	private static final transient Logger log = LoggerFactory.getLogger(ReportDAOImpl.class);

	private WKServiceKeeper services; 

	public WKServiceKeeper getServices() {
		return services;
	}

	public void setServices(WKServiceKeeper services) {
		this.services = services;
	}

	@Override
	public ReportDescriptor getReportDescriptor(NodeRef id) {
		PropertyCheck.mandatory (this, "services", services);
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReportDescriptor getReportDescriptor(String mnemo) {
		PropertyCheck.mandatory (this, "services", services);
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReportTemplate getReportTemplate(NodeRef id) {
		/*

	String getFileName();
	void setFileName( String fileName);

	InputStream getData();
	void setData( InputStream stm);
		 * */
		return null;
	}

	@Override
	public ReportTemplate getReportTemplate(String fileName) {
		// TODO Auto-generated method stub
		return null;
	}

}
