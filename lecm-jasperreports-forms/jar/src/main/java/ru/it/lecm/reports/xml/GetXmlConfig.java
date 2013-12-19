package ru.it.lecm.reports.xml;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import ru.it.lecm.reports.api.ReportsManager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * User: dbashmakov
 * Date: 16.07.13
 * Time: 11:36
 */
public class GetXmlConfig extends AbstractWebScript {
    private static final transient Logger log = LoggerFactory.getLogger(GetXmlConfig.class);
    private static final String REPORT_CODE = "reportCode";

    private ReportsManager reportsManager;

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
        String reportCode = req.getParameter(REPORT_CODE);
        if (reportCode == null) {
            log.error("No Report Code to get DSXmlConfig bytes");
            return;
        }

        OutputStream resOutputStream = null;
        InputStream is = null;
        try {
            is = new ByteArrayInputStream(reportsManager.loadDsXmlBytes(reportCode));

            res.setContentEncoding("UTF-8");
            res.setContentType("text/xml");
            resOutputStream = res.getOutputStream();
            final int len = org.apache.commons.io.IOUtils.copy(is, resOutputStream);
            res.setHeader("Content-length", "" + len);

            resOutputStream.flush();
        }
        catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(resOutputStream);
        }
    }

    public void setReportsManager(ReportsManager reportsManager) {
        this.reportsManager = reportsManager;
    }
}
