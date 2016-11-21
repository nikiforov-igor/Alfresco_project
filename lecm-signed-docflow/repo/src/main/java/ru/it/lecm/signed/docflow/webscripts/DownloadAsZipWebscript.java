package ru.it.lecm.signed.docflow.webscripts;

import java.io.IOException;
import java.net.URLEncoder;
import org.alfresco.repo.web.scripts.content.StreamContent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.FileNameValidator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import ru.it.lecm.signed.docflow.ZipSignedContentService;

/**
 *
 * @author ikhalikov
 */
public class DownloadAsZipWebscript extends StreamContent {

	private ZipSignedContentService zipSignedContentService;
	private final static Log logger = LogFactory.getLog(DownloadAsZipWebscript.class);
	private static final String MIMETYPE_APPLICATION_ZIP = "application/zip";
	private static final String NODEREF_PARAM = "nodeRef";

	public void setZipSignedContentService(ZipSignedContentService zipSignedContentService) {
		this.zipSignedContentService = zipSignedContentService;
	}

	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
		NodeRef nodeRef = new NodeRef(req.getParameter(NODEREF_PARAM));
		String zipName = zipSignedContentService.writeZipToStream(res.getOutputStream(), nodeRef, false);

		zipName = FileNameValidator.getValidFileName(zipName);
		zipName = URLEncoder.encode(zipName, "UTF8").replaceAll("\\+", "%20");
		res.setContentType(MIMETYPE_APPLICATION_ZIP);
		String headerValue = "attachment; filename*=UTF-8''" + zipName + ".zip; charset=UTF-8";
		res.setHeader("Content-Disposition", headerValue);
	}
}
