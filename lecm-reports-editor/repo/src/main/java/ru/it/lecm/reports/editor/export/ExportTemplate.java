package ru.it.lecm.reports.editor.export;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.*;
import org.apache.commons.io.IOUtils;
import org.springframework.extensions.surf.util.URLEncoder;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import ru.it.lecm.reports.editor.ReportsEditorModel;

import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: dbashmakov
 * Date: 07.08.13
 * Time: 11:10
 */
public class ExportTemplate extends AbstractWebScript {
    private static final transient Logger log = LoggerFactory.getLogger(ExportTemplate.class);

    private NodeService nodeService = null;
    private ContentService contentService = null;

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
        OutputStream resOutputStream = null;
        InputStream is = null;
        try {
            String nodeRefStr = req.getParameter("templateRef");
            NodeRef templateRef = NodeRef.isNodeRef(nodeRefStr) ? new NodeRef(nodeRefStr) : null;

            if (templateRef != null) {
                List<AssociationRef> files = nodeService.getTargetAssocs(templateRef, ReportsEditorModel.ASSOC_REPORT_TEMPLATE_FILE);
                if (files != null && !files.isEmpty()) {
                    NodeRef fileRef = files.get(0).getTargetRef();

                    String fileName = (String) nodeService.getProperty(fileRef, ContentModel.PROP_NAME);
                    res.setContentEncoding("UTF-8");
                    res.setContentType("text/xml");
                    res.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName));

                    resOutputStream = res.getOutputStream();

                    ContentReader reader = contentService.getReader(fileRef, ContentModel.PROP_CONTENT);
                    is = reader.getContentInputStream();

                    final int len = IOUtils.copy(is, resOutputStream);

                    res.setHeader("Content-length", "" + len);
                    resOutputStream.flush();
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(resOutputStream);
        }
        log.info("Export complete");
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }
}
