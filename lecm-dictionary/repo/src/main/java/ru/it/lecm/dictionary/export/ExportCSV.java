package ru.it.lecm.dictionary.export;

import com.csvreader.CsvWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: mShafeev
 * Date: 01.11.12
 */

public class ExportCSV extends AbstractWebScript {

	private static final transient Logger log = LoggerFactory.getLogger(ExportCSV.class);

	protected NodeService nodeService;

	/**
	 * Russian locale
	 */
	private static final Locale LOCALE_RU = new Locale("RU");

	/**
	 * Формат даты
	 */
	private static final String DATE_FORMAT = "dd MMM yyyy HH:mm:ss";

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {

		OutputStream resOutputStream = null;
		try {
			ArrayList<String> namespace = new ArrayList<String>();
			String[] fields = req.getParameter("fields").split(",");
			String[] selectItems = req.getParameter("selectedItems").split(",");
			String[] columnsName = req.getParameter("datagridColumns").split(",");
			String fileName = req.getParameter("fileName");
			NodeRef nodeRef;

			res.setContentEncoding("UTF-8");
			res.setContentType("text/csv");
			res.addHeader("Content-Disposition", "attachment; filename=" + ((fileName != null && !fileName.isEmpty()) ? fileName : "dictionary") + ".csv");
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
								if (m.getValue() instanceof Date) {
									Date date = (Date)m.getValue();
									value = new SimpleDateFormat(DATE_FORMAT, LOCALE_RU).format(date);
								}
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
			log.error(e.getMessage(), e);
		} finally {
			if (resOutputStream != null) {
				resOutputStream.close();
			}
		}
		log.info("Export CSV complete");
	}

}
