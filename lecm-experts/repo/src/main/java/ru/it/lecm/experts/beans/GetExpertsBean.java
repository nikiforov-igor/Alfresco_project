package ru.it.lecm.experts.beans;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.processor.BaseProcessorExtension;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.dom.ElementNSImpl;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.it.lecm.experts.client.RequestExpertsExResponse;
import ru.it.lecm.experts.client.SearchExpertsService;
import ru.it.lecm.experts.client.SearchExpertsServiceSoap;

/**
 * @author DBashmakov
 *         Date: 31.07.12
 *         Time: 16:01
 */
public class GetExpertsBean extends BaseProcessorExtension {
	private static boolean byUri = false;
	private static ServiceRegistry serviceRegistry;
	public static final String EXPRT = "exprt";
	public static final String USERNAME = "username";
	public static final String NAME = "name";

	public static final String ATTR_LNAME = "lname";
	public static final String ATTR_FNAME = "fname";

	private NodeService get_nodeService;
	private ContentService get_contentService;

	private static Log logger = LogFactory.getLog(GetExpertsBean.class);

	public String get(String ref) {
		String expertsArray = null;
		if (byUri) {
			/*//TODO need change this code block(check and change ref pass into service)
			SearchExpertsServiceSoap service = new SearchExpertsService().getSearchExpertsServiceSoap();
			RequestExpertsByURIResponse.RequestExpertsByURIResult res = service.requestExpertsByURI(ref);

			if (res.getContent().get(0) instanceof ElementNSImpl) {
				ElementNSImpl result = (ElementNSImpl) res.getContent().get(0);
				expertsArray = parseResult(result);
			}*/
		} else {
			ContentService contentService = serviceRegistry.getContentService();
			NodeRef nodeRef = new NodeRef(ref);
			NodeService nodeService = serviceRegistry.getNodeService();
			String fileName = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);

			ContentReader reader = contentService.getReader(nodeRef, ContentModel.PROP_CONTENT);
			InputStream originalInputStream = reader.getContentInputStream();
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			final int BUF_SIZE = 1 << 8; //1KiB buffer
			byte[] buffer = new byte[BUF_SIZE];
			int bytesRead = -1;

			try {
				while ((bytesRead = originalInputStream.read(buffer)) > -1) {
					outputStream.write(buffer, 0, bytesRead);
				}

				byte[] binaryData = outputStream.toByteArray();

				if (binaryData.length > 0) {
					expertsArray = get(binaryData, fileName);
				}
			} catch (IOException e) {
				logger.error(e);
			} finally {
				IOUtils.closeQuietly(originalInputStream);
				IOUtils.closeQuietly(outputStream);
			}
		}
		return expertsArray;
	}

	public String get (byte[] content, String fileName) {
		JSONArray expertsArray = new JSONArray();
		SearchExpertsServiceSoap service = new SearchExpertsService().getSearchExpertsServiceSoap();
		RequestExpertsExResponse.RequestExpertsExResult res = service.requestExpertsEx(content, fileName, true);

		if (res.getContent().get(0) instanceof ElementNSImpl) {
			ElementNSImpl result = (ElementNSImpl) res.getContent().get(0);
			expertsArray = parseResult(result);
		}
		return expertsArray.toString();
	}

	private JSONArray parseResult(ElementNSImpl result) {
		JSONArray expertsArray = new JSONArray();
		NodeList experts = result.getElementsByTagName(EXPRT);
		for (int i = 0; i < experts.getLength(); i++) {
			Node expert = experts.item(i);
			JSONObject exp = new JSONObject();
			NodeList attrs = expert.getChildNodes();
			for (int index = 0; index < attrs.getLength(); index++) {
				Node attr = attrs.item(index);
				try {
					if (attr.getNodeName().equals(USERNAME)) {
						exp.put(ATTR_LNAME, attr.getTextContent());
					} else if (attr.getNodeName().equals(NAME)) {
						exp.put(ATTR_FNAME, attr.getTextContent());
					}
				} catch (JSONException e) {
					logger.error(e);
				}
			}
			expertsArray.put(exp);
		}
		return expertsArray;
	}


	public void setByUri(boolean byUri) {
		this.byUri = byUri;
	}

	public ServiceRegistry getServiceRegistry() {
		return serviceRegistry;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public NodeService getGet_nodeService() {
		return get_nodeService;
	}

	public void setGet_nodeService(NodeService get_nodeService) {
		this.get_nodeService = get_nodeService;
	}

	public ContentService getGet_contentService() {
		return get_contentService;
	}

	public void setGet_contentService(ContentService get_contentService) {
		this.get_contentService = get_contentService;
	}
}
