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
 * User: mShafeev
 * Date: 01.11.12
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
			String[] columnsName = req.getParameterValues("datagridColumns");
			NodeRef nodeRef;

			res.setContentEncoding("UTF-8");
			res.setContentType("text/csv");
			res.addHeader("Content-Disposition", "attachment; filename=dictionary.csv");
			// Create an XML stream writer
			resOutputStream = res.getOutputStream();

			// По умолчанию charset в UTF-8
			Charset charset = Charset.defaultCharset();
			CsvWriter wr = new CsvWriter(resOutputStream, ';', charset);
			for (int i=0; i<fields.length; i++){
				namespace.add(fields[i].split(":")[1]);
				if (i == 0) {
					wr.write("\ufeff" + columnsName[i]); //UTF c BOM идентификатором
				} else {
					wr.write(columnsName[i]);
				}
			}

			wr.endRecord();
			if (selectItems != null) {
				Boolean noProperties;
				for (String item : selectItems ) {
					nodeRef = new NodeRef(item);
					Set set = nodeService.getProperties(nodeRef).entrySet();
					for (String aNamespace : namespace) {
						noProperties = true;
						for (Object aSet : set) {
							Map.Entry m = (Map.Entry) aSet;
							QName key = (QName) m.getKey();
							String value = m.getValue().toString();
							if (key.getLocalName().equals(aNamespace)) {
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
