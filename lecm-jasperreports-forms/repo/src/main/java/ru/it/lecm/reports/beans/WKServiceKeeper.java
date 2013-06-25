package ru.it.lecm.reports.beans;

import org.alfresco.service.ServiceRegistry;

import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.documents.beans.DocumentConnectionService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

public interface WKServiceKeeper {

	public ServiceRegistry getServiceRegistry();

	public SubstitudeBean getSubstitudeService();

	public OrgstructureBean getOrgstructureService();

	public DocumentService getDocumentService();

	public DocumentConnectionService getDocumentConnectionService();

}