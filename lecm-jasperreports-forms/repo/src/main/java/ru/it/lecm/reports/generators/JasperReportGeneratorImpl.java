package ru.it.lecm.reports.generators;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDataSourceProvider;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
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
import ru.it.lecm.reports.api.ReportFileData;
import ru.it.lecm.reports.api.model.ColumnDescriptor;
import ru.it.lecm.reports.api.model.DataSourceDescriptor;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.api.model.DAO.ReportContentDAO;
import ru.it.lecm.reports.api.model.DAO.ReportContentDAO.IdRContent;
import ru.it.lecm.reports.utils.ArgsHelper;
import ru.it.lecm.reports.utils.Utils;

/**
 * Генератор Jasper-отчётов.
 *
 * @author rabdullin
 */
public class JasperReportGeneratorImpl extends ReportGeneratorBase {

    private static final transient Logger log = LoggerFactory.getLogger(JasperReportGeneratorImpl.class);

    @Override
    public void produceReport(ReportFileData result, ReportDescriptor reportDesc, Map<String, String[]> parameters, ReportContentDAO rptContent)
            throws IOException {

        final String reportFileName = String.format("%s.jasper", reportDesc.getMnem());

        final ContentReader reader = rptContent.loadContent(IdRContent.createId(reportDesc, reportFileName));

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        InputStream stm = (reader != null) ? reader.getContentInputStream() : null;
        try {
            if (reader == null) {
                throw new IOException(String.format("Report is missed - file '%s' not found", reportFileName));
            }

            // DONE: параметризовать выходной формат
            final JasperReportTargetFileType target = findTargetArg(parameters, reportDesc);

            final JasperReport jasperReport = (JasperReport) JRLoader.loadObject(stm);
            IOUtils.closeQuietly(stm);
            stm = null; // сразу закроем поток отчёта

			/* Создание Провайдера */
            final String dataSourceClass = jasperReport.getProperty("dataSource");
            final JRDataSourceProvider dsProvider = super.createDsProvider(reportDesc, dataSourceClass, parameters);

			/* построение отчёта */
            generateReport(target, outputStream, jasperReport, dsProvider, parameters);

            result.setMimeType(target.getMimeType());
            result.setFilename(generateReportResultFileName(reportDesc.getMnem(), target.getExtension()));
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
     *
     * @param requestParameters
     * @param reportDesc
     * @return
     */
    // DONE: (?) разрешить задавать формат в колонках данных (константой или выражением)
    private JasperReportTargetFileType findTargetArg(final Map<String, String[]> requestParameters, ReportDescriptor reportDesc) {
        String value = ArgsHelper.findArg(requestParameters, COLNAME_TARGETFORMAT, null);
        if (Utils.isStringEmpty(value)) {
            if (reportDesc != null) {
                final ColumnDescriptor coldesc = reportDesc.getDsDescriptor().findColumnByName(COLNAME_TARGETFORMAT);
                if (coldesc != null) {
                    value = coldesc.getExpression();
                }
            }
        }
        return JasperReportTargetFileType.findByName(value, DEFAULT_TARGET);
    }

    static final String DEFAULT_FILENAME_DATE_SUFFIX = "dd-MM-yy-HH-mm-ss";

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
                                JRDataSourceProvider dataSourceProvider, Map<String, String[]> requestParameters)
            throws IllegalArgumentException, JRException {
        log.debug("Generating report " + report.getName() + " ...");

        if (outputStream == null) {
            throw new IllegalArgumentException("The output stream was not specified");
        }

        final JRDataSource dataSource = dataSourceProvider.create(report);

        final JasperFillManager fillManager = JasperFillManager.getInstance(DefaultJasperReportsContext.getInstance());

        final Map<String, Object> reportParameters = new HashMap<String, Object>();
        reportParameters.putAll(requestParameters);

        final JasperPrint jPrint = fillManager.fill(report, reportParameters, dataSource);

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
            case DOCX:
                exportReportToStream(new JRDocxExporter(), jPrint, outputStream);
                break;
            case XLS:
                exportReportToStream(new JRXlsExporter(), jPrint, outputStream);
                break;
            default:
                JasperExportManager.exportReportToPdfStream(jPrint, outputStream);
                break;
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
    public void onRegister(ReportDescriptor desc, byte[] templateData, ReportContentDAO storage) {
        compileJrxml(desc, templateData, storage);
    }

    private void compileJrxml(ReportDescriptor desc, byte[] templateData, ReportContentDAO storage) {
        if (templateData == null) {
            return;
        }

        log.info(String.format("compiling report '%s' ...", desc.getMnem()));

        final ByteArrayInputStream inData = new ByteArrayInputStream(templateData);
        final ByteArrayOutputStream outData = new ByteArrayOutputStream();

        try {
            JasperCompileManager.compileReportToStream(inData, outData);
            final IdRContent id = IdRContent.createId(desc, desc.getMnem() + ".jasper");
            storage.storeContent(id, new ByteArrayInputStream(outData.toByteArray()));
        } catch (JRException ex) {
            final String msg = String.format("Error compiling report '%s':\n\t%s", desc.getMnem(), ex.getMessage());
            log.error(msg, ex);
            throw new RuntimeException(msg, ex);
        }
        log.info(String.format("Jasper report '%s' compiled SUCCESSFULLY into %s bytes", desc.getMnem(), outData.size()));
    }
}
