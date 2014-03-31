package ru.it.lecm.businessjournal.script;

import com.csvreader.CsvWriter;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import ru.it.lecm.businessjournal.beans.BusinessJournalRecord;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: dbashmakov
 * Date: 31.10.13
 * Time: 14:38
 */
public class ExportBJRecords extends AbstractWebScript {
    private static final transient Logger log = LoggerFactory.getLogger(ExportBJRecords.class);

    protected NodeService nodeService;
    private BusinessJournalService businessJournalService;

    private static final int DEFAULT_RECORDS_COUNT = 100;
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
            int timeZoneOffset = TimeZone.getDefault().getRawOffset();
            try {
                timeZoneOffset = - Integer.parseInt(req.getParameter("timeZoneOffset")) * 1000 * 60;
            } catch (Exception ignored) {}
            List<BusinessJournalRecord> bjRecordbjRecords = null;
            String fileName = null;
            String[] recordIDs = req.getParameterValues("id");
            boolean full = true;
            if (recordIDs != null) {
                bjRecordbjRecords = new ArrayList<BusinessJournalRecord>();
                for (String recordID : recordIDs) {
                    try {
                        Long longId = Long.valueOf(recordID);
                        bjRecordbjRecords.add(businessJournalService.getNodeById(longId));
                    } catch (NumberFormatException ignored) {
                    }
                }
                fileName = "bj-records.csv";
                full = false;
            }

            if (bjRecordbjRecords == null) {
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
                bjRecordbjRecords = businessJournalService.getLastRecords(recordsCount, includeArchiveRecords);
                fileName = recordsCount + "-last-bj-records.csv";
            }

            res.setContentEncoding("UTF-8");
            res.setContentType("text/csv");
            res.addHeader("Content-Disposition", "attachment; filename=" + fileName);

            resOutputStream = res.getOutputStream();

            // По умолчанию charset в UTF-8
            Charset charset = Charset.defaultCharset();
            CsvWriter wr = new CsvWriter(resOutputStream, ';', charset);
            wr.write("\ufeffДата");
            wr.write("Тип");
            wr.write("Категория");
            wr.write("Описание");
            wr.write("Инициатор");
            if (full) {
                wr.write("Осн. объект");
                wr.write("Доп. объект 1");
                wr.write("Доп. объект 2");
                wr.write("Доп. объект 3");
                wr.write("Доп. объект 4");
                wr.write("Доп. объект 5");
            }
            wr.endRecord();

            int timeZoneDiff = timeZoneOffset - TimeZone.getDefault().getRawOffset();
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            if (bjRecordbjRecords != null) {
                for (BusinessJournalRecord bjRecord : bjRecordbjRecords) {
                    Date reportDate = bjRecord.getDate();

                    if (timeZoneDiff != 0) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(reportDate);

                        cal.add(Calendar.MILLISECOND, timeZoneDiff);
                        reportDate = cal.getTime();
                    }
                    wr.write(format.format(reportDate));

                    NodeRef objectType = bjRecord.getObjectType();
                    String type;
                    if (objectType != null) {
                        type = (String) nodeService.getProperty(objectType, ContentModel.PROP_NAME);
                    } else {
                        type = nodeService.getType(bjRecord.getMainObject()).getPrefixString().replace(":", "_");
                    }
                    wr.write(type);

                    String category;
                    if (bjRecord.getEventCategory() != null) {
                        category = (String) nodeService.getProperty(bjRecord.getEventCategory(), ContentModel.PROP_NAME);
                    } else {
                        category = "unknown";
                    }
                    wr.write(category);

                    String reportDescription = bjRecord.getRecordDescription();
                    reportDescription = reportDescription.replaceAll("<a[^>]*>", "");
                    reportDescription = reportDescription.replaceAll("</a>", "");
                    wr.write(reportDescription);

                    wr.write(bjRecord.getInitiatorText() != null ? bjRecord.getInitiatorText() : "");

                    if (full) {
                        String mainObject = bjRecord.getMainObject() != null ? bjRecord.getMainObject().toString() : "";
                        wr.write(mainObject);

                        String obj1 = bjRecord.getObject1();
                        wr.write(obj1);

                        String obj2 = bjRecord.getObject2();
                        wr.write(obj2);

                        String obj3 = bjRecord.getObject3();
                        wr.write(obj3);

                        String obj4 = bjRecord.getObject4();
                        wr.write(obj4);

                        String obj5 = bjRecord.getObject5();
                        wr.write(obj5);
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
        log.info("Export Business journal Records CSV complete");
    }
}