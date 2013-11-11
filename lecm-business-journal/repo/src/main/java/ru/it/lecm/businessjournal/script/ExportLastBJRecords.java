package ru.it.lecm.businessjournal.script;

import com.csvreader.CsvWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * User: dbashmakov
 * Date: 31.10.13
 * Time: 14:38
 */
public class ExportLastBJRecords extends AbstractWebScript {
    private static final transient Logger log = LoggerFactory.getLogger(ExportLastBJRecords.class);

    protected NodeService nodeService;
    private BusinessJournalService businessJournalService;

    private final int DEFAULT_RECORDS_COUNT = 100;
    private boolean includeArchiveRecords = false;

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setBusinessJournalService(BusinessJournalService businessJournalService) {
        this.businessJournalService = businessJournalService;
    }

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
        OutputStream resOutputStream = null;
        try {
            int recordsCount = DEFAULT_RECORDS_COUNT;
            try {
                recordsCount = Integer.parseInt(req.getParameter("count"));
            } catch (Exception ex) {
                log.error("Не удалось распарсить параметр " + req.getParameter("count"), ex);
            }

            try {
                includeArchiveRecords = Boolean.parseBoolean(req.getParameter("includeArchive"));
            } catch (Exception ex) {
                log.error("Не удалось распарсить параметр " + req.getParameter("includeArchive"), ex);
            }

            String fileName = recordsCount + "-last-bj-records.csv";
            res.setContentEncoding("UTF-8");
            res.setContentType("text/csv");
            res.addHeader("Content-Disposition", "attachment; filename=" + fileName);

            resOutputStream = res.getOutputStream();

            // По умолчанию charset в UTF-8
            Charset charset = Charset.defaultCharset();
            CsvWriter wr = new CsvWriter(resOutputStream, ';', charset);
            wr.write("\ufeffДата");
            wr.write("Описание");
            wr.write("Категория");
            wr.write("Тип");
            wr.write("Осн. объект");
            wr.write("Доп. объект 1");
            wr.write("Доп. объект 2");
            wr.write("Доп. объект 3");
            wr.write("Доп. объект 4");
            wr.write("Доп. объект 5");
            wr.endRecord();
            List<NodeRef> bjRecordbjRecords = businessJournalService.getLastRecords(recordsCount, includeArchiveRecords);
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            for (NodeRef bjRecord : bjRecordbjRecords) {
                Date reportDate = (Date) nodeService.getProperty(bjRecord, BusinessJournalService.PROP_BR_RECORD_DATE);
                wr.write(format.format(reportDate));

                String reportDescription = (String) nodeService.getProperty(bjRecord, BusinessJournalService.PROP_BR_RECORD_DESC);
                reportDescription = reportDescription.replaceAll("<a[^>]*>", "");
                reportDescription = reportDescription.replaceAll("</a>", "");
                wr.write(reportDescription);

                String category = (String) nodeService.getProperty(bjRecord, QName.createQName(BusinessJournalService.BJ_NAMESPACE_URI, "bjRecord-evCategory-assoc-text-content"));
                wr.write(category);

                String type = (String) nodeService.getProperty(bjRecord, QName.createQName(BusinessJournalService.BJ_NAMESPACE_URI, "bjRecord-objType-assoc-text-content"));
                wr.write(type);

                String mainObject = (String) nodeService.getProperty(bjRecord, QName.createQName(BusinessJournalService.BJ_NAMESPACE_URI, "bjRecord-mainObject-assoc-ref"));
                wr.write(mainObject);

                String obj1 = (String) nodeService.getProperty(bjRecord, QName.createQName(BusinessJournalService.BJ_NAMESPACE_URI, "bjRecord-secondaryObj1-assoc-ref"));
                wr.write(obj1);

                String obj2 = (String) nodeService.getProperty(bjRecord, QName.createQName(BusinessJournalService.BJ_NAMESPACE_URI, "bjRecord-secondaryObj2-assoc-ref"));
                wr.write(obj2);

                String obj3 = (String) nodeService.getProperty(bjRecord, QName.createQName(BusinessJournalService.BJ_NAMESPACE_URI, "bjRecord-secondaryObj3-assoc-ref"));
                wr.write(obj3);

                String obj4 = (String) nodeService.getProperty(bjRecord, QName.createQName(BusinessJournalService.BJ_NAMESPACE_URI, "bjRecord-secondaryObj4-assoc-ref"));
                wr.write(obj4);

                String obj5 = (String) nodeService.getProperty(bjRecord, QName.createQName(BusinessJournalService.BJ_NAMESPACE_URI, "bjRecord-secondaryObj5-assoc-ref"));
                wr.write(obj5);

                wr.endRecord();
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
        log.info("Export Business journal Records CSV complete");
    }
}