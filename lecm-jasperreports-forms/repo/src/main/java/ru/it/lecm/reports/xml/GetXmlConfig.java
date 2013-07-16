package ru.it.lecm.reports.xml;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import ru.it.lecm.reports.api.ReportsManager;
import ru.it.lecm.reports.beans.ReportsManagerImpl;

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
            log.error("No Report to get DSXmlConfig bytes!");
            return;
        }

        OutputStream resOutputStream = null;
        InputStream is = null;
        try {
            is = new ByteArrayInputStream(reportsManager.loadDsXmlBytes(reportCode));

            res.setContentEncoding("UTF-8");
            res.setContentType("text/xml");
            resOutputStream = res.getOutputStream();

            byte[] buf = new byte[8 * 1024];
            int c;
            int len = 0;
            while ((c = is.read(buf)) != -1) {
                resOutputStream.write(buf, 0, c);
                len += c;
            }
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

    public void setReportsManager(ReportsManagerImpl reportsManager) {
        this.reportsManager = reportsManager;
    }
}
