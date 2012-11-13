package ru.it.lecm.dictionary.export;

import com.csvreader.CsvWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: mShafeev
 * Date: 01.11.12
 * Time: 17:44
 * To change this template use File | Settings | File Templates.
 */

public class ExportCSV extends AbstractWebScript {

	private static final Log log = LogFactory.getLog(Export.class);

	protected NodeService nodeService;

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {

		OutputStream resOutputStream = null;
		try {
			ArrayList<String> namespace = new ArrayList<String>();
			String[] fields = req.getParameterValues("field");
			String[] selectItems = req.getParameterValues("selectedItems");
			String nodeRefStr = req.getParameter("nodeRef");
			NodeRef nodeRef;

			res.setContentEncoding("UTF-8");
			res.setContentType("text/csv");
			res.addHeader("Content-Disposition", "attachment; filename=dictionary.csv");
			// Create an XML stream writer
			resOutputStream = res.getOutputStream();

			Charset charset = Charset.defaultCharset();
			CsvWriter wr = new CsvWriter(resOutputStream, ';', charset);

			for (String field : fields) {
				namespace.add(field.split(":")[1]);
				wr.write(field);
			}
			wr.endRecord();
			if (selectItems != null) {
				Boolean noProperties = true;
				for (String item : selectItems ) {
					nodeRef = new NodeRef(item);
					Set set = nodeService.getProperties(nodeRef).entrySet();
					for (int i = 0; i < namespace.size(); i++) {
						noProperties = true;
						for (Object aSet : set) {
							Map.Entry m = (Map.Entry) aSet;
							QName key = (QName) m.getKey();
							String value = m.getValue().toString();
							if (key.getLocalName().equals(namespace.get(i))){
								wr.write(value);
								noProperties = false;
							}
						}
						if (noProperties) {
							wr.write("");
						}
					}
					wr.endRecord();
				}
			}
			wr.close();
			resOutputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (resOutputStream != null) {
				resOutputStream.close();
			}
		}
		log.info("Export CSV complete");
	}

}
