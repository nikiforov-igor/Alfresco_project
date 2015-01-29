package ru.it.lecm.barcode.beans;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import javax.ws.rs.core.MediaType;
import ru.it.lecm.barcode.entity.PrintJob;
import ru.it.lecm.barcode.entity.PrintResult;

/**
 *
 * @author vlevin
 */
public class PrintRESTClient {

	private final Client client;
	private final String serviceURL;

	public PrintRESTClient(String serviceURL) {
		this.serviceURL = serviceURL;

		ClientConfig clientConfig = new DefaultClientConfig();
		clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
		client = Client.create(clientConfig);
	}

	public PrintResult print(PrintJob job) {
		PrintResult result;

		try {
			WebResource webResource = client.resource(serviceURL + "/print");
			result = webResource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON).post(PrintResult.class, job);
		} catch (RuntimeException ex) {
			result = new PrintResult();
			result.setSuccess(false);
			result.setErrorMessage(ex.getMessage());
		}

		return result;
	}

}
