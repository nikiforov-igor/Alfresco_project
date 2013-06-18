package ru.it.lecm.dictionary.export;

import org.alfresco.service.cmr.repository.NodeRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import ru.it.lecm.dictionary.beans.XMLExportBean;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: AIvkin
 * Date: 13.11.12
 * Time: 10:15
 */
public class ExportAPI extends AbstractWebScript {
	private static final transient Logger log = LoggerFactory.getLogger(ExportAPI.class);
    private XMLExportBean xmlExportBean;

    public void setXmlExportBean(XMLExportBean xmlExportBean) {
        this.xmlExportBean = xmlExportBean;
    }

	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {

		OutputStream resOutputStream = null;
		try {
			String nodeRefStr = req.getParameter("nodeRef");
			NodeRef nodeRef = new NodeRef(nodeRefStr);

			res.setContentEncoding("utf-8");

			// Create an XML stream writer
			resOutputStream = res.getOutputStream();

			XMLExportBean.XMLExporter xmlDictionaryExporter = xmlExportBean.getXMLExporter(resOutputStream);
			xmlDictionaryExporter.writeItems(nodeRef);
			xmlDictionaryExporter.close();
			resOutputStream.flush();
		} catch (XMLStreamException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			if (resOutputStream != null) {
				resOutputStream.close();
			}
		}
		log.info("Export complete");
	}
}
