package ru.it.lecm.events.ews;

import java.net.URISyntaxException;
import org.alfresco.service.cmr.repository.NodeService;
import ru.it.lecm.events.beans.EWSService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 *
 * @author vmalygin
 */
public abstract class AbstractEWSService implements EWSService {

	protected String exchangeVersion;
	protected String url;
	protected String username;
	protected String password;
	protected String domain;
	protected OrgstructureBean orgstructureService;
	protected NodeService nodeService;

	public void setExchangeVersion(String exchangeVersion) {
		this.exchangeVersion = exchangeVersion;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public abstract void init() throws URISyntaxException;
}
