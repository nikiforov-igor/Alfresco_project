package ru.it.lecm.forms.pentahoforms;

import org.alfresco.repo.model.Repository;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: AZinovin
 * Date: 05.09.12
 * Time: 16:25
 */
public class PentahoFormProducer extends AbstractWebScript {

	private Repository repository;
	private NodeService nodeService;
	private ContentService contentService;

	public void setRepository(Repository repository)
	{
		this.repository = repository;
	}

	@Override
	public void execute(WebScriptRequest webScriptRequest, WebScriptResponse webScriptResponse) throws IOException {
		System.out.println("!!!!!!!!!");
//		String nodeRef = webScriptRequest.getParameter("nodeRef");
		String nodeRef = webScriptRequest.getServiceMatch().getTemplateVars().get("nodeRef");

		if (!NodeRef.isNodeRef(nodeRef)) {
			nodeRef = nodeRef.replace(":/","://");
		}

		NodeRef nodeRef1 = NodeRef.getNodeRefs(nodeRef).get(0);
		Map<QName, Serializable> properties = nodeService.getProperties(nodeRef1);

		String[][] data = new String[properties.size()][2];
		int i = 0;
		for (Map.Entry<QName, Serializable> entry : properties.entrySet()) {
			data[i][0] = entry.getKey().getLocalName();
			data[i][1] = entry.getValue().toString();
			i++;
		}
		OutputStream outputStream = webScriptResponse.getOutputStream();
		try {
			FormsReportGenerator formsReportGenerator = new FormsReportGenerator(data);
			formsReportGenerator.generateReport(AbstractReportGenerator.OutputType.PDF, outputStream);
		} catch (ReportProcessingException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		outputStream.flush();
		outputStream.close();
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}
}
