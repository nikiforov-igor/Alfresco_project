package ru.it.lecm.dictionary.export;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import ru.it.lecm.dictionary.ExportSettings;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.OutputStream;

/**
 * User: mShafeev
 * Date: 23.10.12
 * Time: 12:01
 */
public class Export extends AbstractWebScript {

	private static final Log log = LogFactory.getLog(Export.class);
	private NodeService nodeService;
	private NamespaceService namespaceService;
	private ExportSettings exportSettings;

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

    public void setExportSettings(ExportSettings exportSettings) {
        this.exportSettings = exportSettings;
    }

    @Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {

		OutputStream resOutputStream = null;
		try {
			String nodeRefStr = req.getParameter("nodeRef");
			NodeRef nodeRef = new NodeRef(nodeRefStr);

			res.setContentEncoding("UTF-8");
			res.setContentType("text/xml");
			res.addHeader("Content-Disposition", "attachment; filename=dictionary.xml");
			// Create an XML stream writer
			resOutputStream = res.getOutputStream();

			XmlDictionaryExporter xmlDictionaryExporter = new XmlDictionaryExporter(resOutputStream, exportSettings, nodeService, namespaceService);
			xmlDictionaryExporter.writeItems(nodeRef);
			xmlDictionaryExporter.close();
			resOutputStream.flush();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (resOutputStream != null) {
				resOutputStream.close();
			}
		}
		log.info("Export complete");
	}
}
