package ru.it.lecm.dictionary.export;

import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import ru.it.lecm.dictionary.beans.XMLExportBean;

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

			res.setContentEncoding("UTF-8");
			res.setContentType("text/xml");
			res.addHeader("Content-Disposition", "attachment; filename=dictionary.xml");
			// Create an XML stream writer
			resOutputStream = res.getOutputStream();

			XMLExportBean.XMLExporter xmlDictionaryExporter = xmlExportBean.getXMLExporter(resOutputStream);
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
