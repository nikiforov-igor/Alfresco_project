package ru.it.lecm.reports.generators;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.reports.api.JasperReportTargetFileType;
import ru.it.lecm.reports.api.ReportsManager;
import ru.it.lecm.reports.api.model.DAO.ReportContentDAO;
import ru.it.lecm.reports.api.model.DAO.ReportContentDAO.IdRContent;
import ru.it.lecm.reports.api.model.DataSourceDescriptor;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.api.model.ReportFileData;
import ru.it.lecm.reports.model.impl.ColumnDescriptor;
import ru.it.lecm.reports.model.impl.ReportTemplate;
import ru.it.lecm.reports.utils.ArgsHelper;
import ru.it.lecm.reports.utils.Utils;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Генератор Jasper-отчётов.
 *
 * @author rabdullin
 */
public class JasperReportGeneratorImpl extends ReportGeneratorBase {

    static final String DEFAULT_FILENAME_DATE_SUFFIX = "dd-MM-yy-HH-mm-ss";

    private static final transient Logger log = LoggerFactory.getLogger(JasperReportGeneratorImpl.class);

    @Override
    public ReportFileData produceReport(ReportsManager reportsManager, ReportDescriptor reportDesc, ReportTemplate templateDescriptor, Map<String, Object> parameters) throws IOException {
        PropertyCheck.mandatory(this, "reportDesc", reportDesc);
        PropertyCheck.mandatory(this, "reportsManager", reportsManager);

        final ReportFileData result = new ReportFileData();

        log.debug(String.format("producing report /'%s'/ \"%s\"", reportDesc.getMnem(), reportDesc.getDefault()));

        final ReportContentDAO rptContent = reportsManager.findContentDAO(reportDesc);
        if (rptContent == null) {
            throw new RuntimeException(String.format("Report '%s' storage point is unknown (possibly report is not registered !?)", reportDesc.getMnem()));
        }

        final String reportFileName = getTemplateFileName(reportDesc, templateDescriptor, ".jasper");
        ContentReader reader = rptContent.loadContent(IdRContent.createId(reportDesc, reportFileName));
        if (reader == null) {
            throw new IOException(String.format("Report is missed - file '%s' not found", reportFileName));
        }

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        InputStream stm = reader.getContentInputStream();
        try {
            // DONE: параметризовать выходной формат
            final JasperReportTargetFileType target = findTargetArg(parameters, reportDesc);

            final JasperReport jasperReport = (JasperReport) JRLoader.loadObject(stm);
            IOUtils.closeQuietly(stm); // сразу закроем поток отчёта
            stm = null;

			/* Создание Провайдера */
            final String dataSourceClass = reportDesc.getProviderDescriptor() != null ?
                    reportDesc.getProviderDescriptor().getClassName() : jasperReport.getProperty("dataSource");
            final JRDataSourceProvider dsProvider = createDsProvider(reportsManager, reportDesc, dataSourceClass, parameters);

			/* построение отчёта */
            generateReport(target, outputStream, jasperReport, dsProvider, parameters);

            result.setMimeType(target.getMimeType());
            result.setFilename(generateReportResultFileName(reportDesc.getDefault(), target.getExtension()));
            result.setEncoding("UTF-8");

            outputStream.flush();
            result.setData(outputStream.toByteArray());
        } catch (Throwable e) { // (JRException e) {
            final String msg = String.format("Fail to build Jasper report '%s':\n\t%s", reportDesc.getMnem(), e);
            log.error(msg, e);
            throw new IOException(msg, e);
        } finally {
            IOUtils.closeQuietly(stm);
            IOUtils.closeQuietly(outputStream);
        }

        return result;
    }

    /**
     * Целевой формат отчёта по-умолчанию
     */
    private static final JasperReportTargetFileType DEFAULT_TARGET = JasperReportTargetFileType.PDF;

    /**
     * "Что сгенерировать" = название колонки (типа строка) с целевым форматом файла после генератора
     */
    private static final String COLNAME_TARGETFORMAT = DataSourceDescriptor.COLNAME_REPORT_TARGETFORMAT;

    /**
     * Найти целевой формат в параметрах ...
     */
    // DONE: (?) разрешить задавать формат в колонках данных (константой или выражением)
    private JasperReportTargetFileType findTargetArg(final Map<String, Object> requestParameters, ReportDescriptor reportDesc) {
        String value = ArgsHelper.findArg(requestParameters, COLNAME_TARGETFORMAT, null);
        if (Utils.isStringEmpty(value)) {
            if (reportDesc != null) {
                final ColumnDescriptor colDesc = reportDesc.getDsDescriptor().findColumnByName(COLNAME_TARGETFORMAT);
                if (colDesc != null) {
                    value = colDesc.getExpression();
                }
            }
        }
        return JasperReportTargetFileType.findByName(value, DEFAULT_TARGET);
    }

    /**
     * Сгенерировать имя файла.
     *
     * @param name      имя файла (без расширения и пути): "contracts"
     * @param extension расширения файла (с точкой): ".rtf"
     * @return уникальной имя файла (добавляется дата и время)
     */
    static String generateReportResultFileName(String name, String extension) {
        return String.format("%s-%s%s", name, new SimpleDateFormat(DEFAULT_FILENAME_DATE_SUFFIX).format(new Date()), extension);
    }

    private void generateReport(JasperReportTargetFileType target, OutputStream outputStream, JasperReport report,
                                JRDataSourceProvider dataSourceProvider, Map<String, Object> requestParameters)
            throws IllegalArgumentException, JRException, ClassNotFoundException, SQLException {
        log.debug("Generating report " + report.getName() + " ...");

        if (outputStream == null) {
            throw new IllegalArgumentException("The output stream was not specified");
        }

        JasperPrint jPrint;
        Connection conn = null;
        try {
            final Map<String, Object> reportParameters = new HashMap<String, Object>();
            reportParameters.putAll(requestParameters);

            final JasperFillManager fillManager = JasperFillManager.getInstance(DefaultJasperReportsContext.getInstance());

            if (!(dataSourceProvider instanceof SQLProvider)) {
                final JRDataSource dataSource = dataSourceProvider.create(report);
                jPrint = fillManager.fill(report, reportParameters, dataSource);
            } else {// SQL
                conn = getTargetDataSource().getConnection();
                jPrint = fillManager.fill(report, reportParameters, conn);
            }

		/* формирование результата в нужном формате */
            log.debug(String.format("Exporting report '%s' as %s ...", report.getName(), target));
            switch (target) {
                case PDF:
                    JasperExportManager.exportReportToPdfStream(jPrint, outputStream);
                    break;
                case XML:
                    JasperExportManager.exportReportToXmlStream(jPrint, outputStream);
                    break;
                case RTF:
                    exportReportToStream(new JRRtfExporter(), jPrint, outputStream);
                    break;
                case DOC:
                    exportReportToStream(new JRDocxExporter(), jPrint, outputStream);
                    break;
                case DOCX:
                    exportReportToStream(new JRDocxExporter(), jPrint, outputStream);
                    break;
                case XLS:
                    exportReportToStream(new JRXlsExporter(), jPrint, outputStream);
                    break;
                default:
                    final String msg = String.format("Unknown report target '%s' -> using PDF by default", target);
                    log.warn(msg);
                    JasperExportManager.exportReportToPdfStream(jPrint, outputStream);
                    break;
            }
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        log.info(String.format("Report '%s' as %s generated succefully", report.getName(), target));
    }

    private void exportReportToStream(final JRAbstractExporter exporter, final JasperPrint jPrint, OutputStream outputStream)
            throws JRException {
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jPrint);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
        exporter.exportReport();
    }

    @Override
    public void onRegister(ReportDescriptor desc, ReportTemplate template, byte[] templateData, ReportContentDAO storage) {
        compileJrxml(desc, template, templateData, storage);
    }

    private void compileJrxml(ReportDescriptor desc, ReportTemplate template, byte[] templateData, ReportContentDAO storage) {
        if (templateData == null || template == null) {
            return;
        }

        log.info(String.format("compiling report '%s' ...", desc.getMnem()));

        final ByteArrayInputStream inData = new ByteArrayInputStream(templateData);
        final ByteArrayOutputStream outData = new ByteArrayOutputStream();

        try {
            JasperCompileManager.compileReportToStream(inData, outData);
            final IdRContent id = IdRContent.createId(desc, getTemplateFileName(desc, template, ".jasper"));
            storage.storeContent(id, new ByteArrayInputStream(outData.toByteArray()));
        } catch (Exception ex) {
            final String msg = String.format("Error compiling report '%s':\n\t%s", desc.getMnem(), ex.getMessage());
            log.error(msg, ex);
        }
        log.info(String.format("Jasper report '%s' compiled SUCCESSFULLY into %s bytes", desc.getMnem(), outData.size()));
    }
}
