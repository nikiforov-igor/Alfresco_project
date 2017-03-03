package ru.it.lecm.events.ews;

import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import ru.it.lecm.events.beans.EWSService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 *
 * @author vmalygin
 */
public class EWSFactoryBean implements FactoryBean<EWSService> {

	private final static Logger logger = LoggerFactory.getLogger(EWSFactoryBean.class);

	private boolean enabled;
	private String exchangeVersion;
	private String url;
	private String username;
	private String password;
	private String domain;
	private OrgstructureBean orgstructureService;
	private NodeService nodeService;
	private EWSService ewsService;

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

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

	public void init() {
		if (enabled) {
			PropertyCheck.mandatory(this, "exchangeVersion", this.exchangeVersion);
			PropertyCheck.mandatory(this, "url", this.url);
			PropertyCheck.mandatory(this, "username", this.username);
			PropertyCheck.mandatory(this, "password", this.password);
			PropertyCheck.mandatory(this, "domain", this.domain);
			PropertyCheck.mandatory(this, "orgstructureService", this.orgstructureService);
			PropertyCheck.mandatory(this, "nodeService", this.nodeService);
		} else {
			logger.warn("Receiving availability information from MS Exchange server is disabled. Please activate \"lecm.events.ews.enabled\" option and provide proper configuration");
		}
	}

	@Override
	public EWSService getObject() throws Exception {
		if (ewsService == null) {
			ewsService = new EWSDummyServiceImpl();
			if (enabled) {
				EWSServiceImpl realService = new EWSServiceImpl();
				realService.setExchangeVersion(exchangeVersion);
				realService.setUrl(url);
				realService.setUsername(username);
				realService.setPassword(password);
				realService.setDomain(domain);
				realService.setOrgstructureService(orgstructureService);
				realService.setNodeService(nodeService);
				realService.init();
				ewsService = realService;
			}
		}
		return ewsService;
	}

	@Override
	public Class<?> getObjectType() {
		return EWSService.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}
