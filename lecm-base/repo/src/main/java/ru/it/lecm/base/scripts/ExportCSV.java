package ru.it.lecm.base.scripts;

import com.csvreader.CsvWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.surf.util.URLEncoder;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import ru.it.lecm.base.beans.SubstitudeBean;

import javax.mail.internet.MimeUtility;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
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
	private SubstitudeBean substituteService;

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

	public void setSubstituteService(SubstitudeBean substitudeService) {
		this.substituteService = substitudeService;
	}

	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {


		OutputStream resOutputStream = null;
		try {
			String fileName = req.getParameter("fileName");
			Integer timeZoneOffset = null;
			try {
				timeZoneOffset = - Integer.parseInt(req.getParameter("timeZoneOffset")) * 1000 * 60;
			} catch (NumberFormatException ex){
				log.warn("Неправльнай параметр timeZoneOffset", req.getParameter("timeZoneOffset"));
			}
			String[] nodeRefs = req.getParameterValues("nodeRef");
			String[] fields = req.getParameterValues("field");
			String[] fieldLabels = req.getParameterValues("fieldLabel");

			res.setContentEncoding("UTF-8");
			res.setContentType("text/csv");
			String fileNameFinal = ((fileName != null && !fileName.isEmpty()) ? URLDecoder.decode(fileName, "UTF-8") : "dictionary") + ".csv";
			res.addHeader("Content-Disposition", "filename=\"" + MimeUtility.encodeWord(fileNameFinal, "utf-8", "Q") + "\"");
			// Create an XML stream writer
			resOutputStream = res.getOutputStream();

			// По умолчанию charset в UTF-8
			Charset charset = Charset.defaultCharset();
			CsvWriter wr = new CsvWriter(resOutputStream, ';', charset);
			if (fieldLabels != null) {
				for (int i=0; i< fieldLabels.length; i++){
					if (i == 0) {
						wr.write("\ufeff" + fieldLabels[i]); //UTF c BOM идентификатором
					} else {
						wr.write(fieldLabels[i]);
					}
				}
			}

			wr.endRecord();
			if (nodeRefs != null && fields != null && fieldLabels != null && fields.length == fieldLabels.length) {
				for (String item : nodeRefs) {
					NodeRef nodeRef = new NodeRef(item);
					if (nodeService.exists(nodeRef)) {
						for (String field: fields) {
							if (field.startsWith("$parent")) {
								field = field.replace("$parent", "");
							}
							if (field.startsWith("$includeRef")) {
								field = field.replace("$includeRef", "");
							}

							String fieldValue = substituteService.formatNodeTitle(nodeRef, field, DATE_FORMAT, timeZoneOffset);
							fieldValue = fieldValue.replaceAll("<a[^>]*>", "");
							fieldValue = fieldValue.replaceAll("</a>", "");
							wr.write(fieldValue);
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
