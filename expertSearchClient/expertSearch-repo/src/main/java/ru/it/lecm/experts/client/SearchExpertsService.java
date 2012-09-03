
package ru.it.lecm.experts.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebServiceClient(name = "SearchExpertsService", targetNamespace = "http://tempuri.org/", wsdlLocation = "http://172.30.0.73:8080/SearchExpertsService.asmx?wsdl")
public class SearchExpertsService
    extends Service
{

    private final static URL SEARCHEXPERTSSERVICE_WSDL_LOCATION;
    private final static Logger logger = Logger.getLogger(SearchExpertsService.class.getName());

    static {
        URL url = null;
        try {
            URL baseUrl;
            baseUrl = SearchExpertsService.class.getResource(".");
            url = new URL(baseUrl, "http://172.30.0.73:8080/SearchExpertsService.asmx?wsdl");
        } catch (MalformedURLException e) {
            logger.warning("Failed to create URL for the wsdl Location: 'http://172.30.0.73:8080/SearchExpertsService.asmx?wsdl', retrying as a local file");
            logger.warning(e.getMessage());
        }
        SEARCHEXPERTSSERVICE_WSDL_LOCATION = url;
    }

    public SearchExpertsService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public SearchExpertsService() {
        super(SEARCHEXPERTSSERVICE_WSDL_LOCATION, new QName("http://tempuri.org/", "SearchExpertsService"));
    }

    /**
     * 
     * @return
     *     returns SearchExpertsServiceSoap
     */
    @WebEndpoint(name = "SearchExpertsServiceSoap")
    public SearchExpertsServiceSoap getSearchExpertsServiceSoap() {
        return super.getPort(new QName("http://tempuri.org/", "SearchExpertsServiceSoap"), SearchExpertsServiceSoap.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns SearchExpertsServiceSoap
     */
    @WebEndpoint(name = "SearchExpertsServiceSoap")
    public SearchExpertsServiceSoap getSearchExpertsServiceSoap(WebServiceFeature... features) {
        return super.getPort(new QName("http://tempuri.org/", "SearchExpertsServiceSoap"), SearchExpertsServiceSoap.class, features);
    }

    /**
     * 
     * @return
     *     returns SearchExpertsServiceSoap
     */
    @WebEndpoint(name = "SearchExpertsServiceSoap12")
    public SearchExpertsServiceSoap getSearchExpertsServiceSoap12() {
        return super.getPort(new QName("http://tempuri.org/", "SearchExpertsServiceSoap12"), SearchExpertsServiceSoap.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns SearchExpertsServiceSoap
     */
    @WebEndpoint(name = "SearchExpertsServiceSoap12")
    public SearchExpertsServiceSoap getSearchExpertsServiceSoap12(WebServiceFeature... features) {
        return super.getPort(new QName("http://tempuri.org/", "SearchExpertsServiceSoap12"), SearchExpertsServiceSoap.class, features);
    }

}
