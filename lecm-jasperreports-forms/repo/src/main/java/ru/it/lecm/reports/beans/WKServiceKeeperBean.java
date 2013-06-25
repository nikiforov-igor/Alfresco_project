package ru.it.lecm.reports.beans;

import org.alfresco.service.ServiceRegistry;

import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.documents.beans.DocumentConnectionService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 * Внутренний бин для получения доступа к набору "часто исопльзуемых служб"
 * @author rabdullin
 */
public class WKServiceKeeperBean implements WKServiceKeeper {

	private ServiceRegistry serviceRegistry;
	private SubstitudeBean substitudeService;
	private OrgstructureBean orgstructureService;
	private DocumentService documentService;
	private DocumentConnectionService documentConnectionService;

	/* (non-Javadoc)
	 * @see ru.it.lecm.reports.beans.WKServiceKeeper#getServiceRegistry()
	 */
	@Override
	public ServiceRegistry getServiceRegistry() {
		return serviceRegistry;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	/* (non-Javadoc)
	 * @see ru.it.lecm.reports.beans.WKServiceKeeper#getSubstitudeService()
	 */
	@Override
	public SubstitudeBean getSubstitudeService() {
		return substitudeService;
	}

	public void setSubstitudeService(SubstitudeBean substitudeService) {
		this.substitudeService = substitudeService;
	}

	/* (non-Javadoc)
	 * @see ru.it.lecm.reports.beans.WKServiceKeeper#getOrgstructureService()
	 */
	@Override
	public OrgstructureBean getOrgstructureService() {
		return this.orgstructureService;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	/* (non-Javadoc)
	 * @see ru.it.lecm.reports.beans.WKServiceKeeper#getDocumentService()
	 */
	@Override
	public DocumentService getDocumentService() {
		return documentService;
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	/* (non-Javadoc)
	 * @see ru.it.lecm.reports.beans.WKServiceKeeper#getDocumentConnectionService()
	 */
	@Override
	public DocumentConnectionService getDocumentConnectionService() {
		return documentConnectionService;
	}

	public void setDocumentConnectionService(
			DocumentConnectionService documentConnectionService) {
		this.documentConnectionService = documentConnectionService;
	}
}
