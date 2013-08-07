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
            String nodeRefStr = req.getParameter("reportRef");
            NodeRef nodeRef = new NodeRef(nodeRefStr);

            List<AssociationRef> assocs = nodeService.getTargetAssocs(nodeRef, ReportsEditorModel.ASSOC_REPORT_DESCRIPTOR_TEMPLATE);
            NodeRef templateRef = null;
            if (assocs != null && !assocs.isEmpty()){
                templateRef = assocs.get(0).getTargetRef();
            }
            if (templateRef != null) {
                List<AssociationRef> files = nodeService.getTargetAssocs(templateRef, ReportsEditorModel.ASSOC_REPORT_TEMPLATE_FILE);
                NodeRef fileRef = null;
                if (files != null && !files.isEmpty()){
                    fileRef = files.get(0).getTargetRef();
                }
                if (fileRef != null) {
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
