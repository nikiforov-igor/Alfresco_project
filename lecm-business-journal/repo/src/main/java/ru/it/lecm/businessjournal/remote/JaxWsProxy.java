package ru.it.lecm.businessjournal.remote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.remoting.jaxws.JaxWsPortProxyFactoryBean;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * User: pmelnikov Date: 10.12.13 Time: 17:10
 */
public class JaxWsProxy extends JaxWsPortProxyFactoryBean {

	private final static Logger logger = LoggerFactory.getLogger(JaxWsProxy.class);

    private String useRemoteService;
    private URL wsdlUrl;

    @Override
    public void afterPropertiesSet() {
        if (Boolean.valueOf(useRemoteService) && wsdlUrl != null) {
            super.afterPropertiesSet();
        }
    }

    public void setUseRemoteService(String useRemoteService) {
        this.useRemoteService = useRemoteService;
    }

    public void setWsdlUrl(String wsdlUrl) {
        try {
            this.wsdlUrl = new URL(wsdlUrl);
            super.setWsdlDocumentUrl(this.wsdlUrl);
		} catch (MalformedURLException ex) {
			logger.warn("WSDL URL was not set for Remote BJ Service ({})", wsdlUrl);
			logger.trace(ex.getMessage(), ex);
        }
    }
}
