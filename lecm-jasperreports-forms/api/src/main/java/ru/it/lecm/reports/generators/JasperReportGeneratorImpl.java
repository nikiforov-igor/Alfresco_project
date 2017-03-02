package ru.it.lecm.reports.generators;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.alfresco.repo.content.filestore.FileContentReader;
import org.alfresco.repo.content.filestore.FileContentWriter;
import org.alfresco.repo.content.transform.ContentTransformer;
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
    static final String SQL_IN_SAME_ORGANIZATION = "SQL_IN_SAME_ORGANIZATION";

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

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        InputStream stm = null;
        try {
            // DONE: параметризовать выходной формат
            final JasperReportTargetFileType target = findTargetArg(parameters, reportDesc);

			/* Создание Провайдера */
            final String dataSourceClass = reportDesc.getProviderDescriptor().getClassName();
            final JRDataSourceProvider dsProvider = createDsProvider(reportsManager, reportDesc, dataSourceClass, parameters);

            String reportFileName;
            ContentReader reader ;
            JasperReport jasperReport;
            /* построение отчёта */
            if (!(dsProvider instanceof SQLProvider)) {
                reportFileName = getTemplateFileName(reportDesc, templateDescriptor, ".jasper");
                reader = rptContent.loadContent(IdRContent.createId(reportDesc, reportFileName));
                if (reader == null) {
                    throw new IOException(String.format("Report is missed - file '%s' not found", reportFileName));
                }
                stm = reader.getContentInputStream();

                jasperReport = (JasperReport) JRLoader.loadObject(stm);
            } else {
                reportFileName = getTemplateFileName(reportDesc, templateDescriptor, ".jrxml");
                reader = rptContent.loadContent(IdRContent.createId(reportDesc, reportFileName));
                if (reader == null) {
                    throw new IOException(String.format("Report template is missed - file '%s' not found", reportFileName));
                }

                stm = reader.getContentInputStream();

                final JasperDesign jasperDesign = JRXmlLoader.load(stm);

                //Build a new query
                String theQuery = jasperDesign.getQuery().getText();

                if (theQuery != null && theQuery.contains(SQL_IN_SAME_ORGANIZATION)) {
                    if (!reportDesc.getFlags().isRunAsSystem()) {
                        if (!reportDesc.getFlags().isIncludeAllOrganizations()) {
                            theQuery = getQueryHelper().getProcessorService().processQuery(theQuery);//выполним подстановку
                        } else {
                            theQuery = getQueryHelper().getProcessorService().processQuery(theQuery, "1=1");//выполним подстановку
                        }
                    } else {
                        theQuery = getQueryHelper().getProcessorService().processQuery(theQuery, "1=1");//выполним подстановку
                    }
                }

                //обновляем запрос в отчете
                JRDesignQuery newQuery = new JRDesignQuery();
                newQuery.setText(getQueryHelper().getProcessorService().processQuery(theQuery));

                jasperDesign.setQuery(newQuery);

                jasperReport = JasperCompileManager.compileReport(jasperDesign);
            }

            IOUtils.closeQuietly(stm); // сразу закроем поток отчёта

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

    @Override
    public byte[] generateReportTemplateByMaket(byte[] maketData, ReportDescriptor desc, ReportTemplate template) {
        final JRDataSourceProvider dsProvider = createDsProvider(null, desc, desc.getProviderDescriptor().getClassName(), null);
        final XMLMacroGenerator xmlGenerator = new XMLMacroGenerator(desc, template, dsProvider);
        final ByteArrayOutputStream result = xmlGenerator.xmlGenerateByTemplate(
                new ByteArrayInputStream(maketData), "Template For Report - " + desc.getMnem());
        return (result != null) ? result.toByteArray() : null;
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
                conn = ((SQLProvider) dataSourceProvider).getConnection();
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
                    try {
                        generateWithConversion(outputStream, jPrint, "doc");
                    } catch (IOException e) {
                        log.error("Cannot create doc", e);
                    }
                    break;
                case DOCX:
                    try {
                        generateWithConversion(outputStream, jPrint, "docx");
                    } catch (IOException e) {
                        log.error("Cannot create docx", e);
                    }
                    break;
                case XLS:
                    exportReportToStream(new JRXlsExporter(), jPrint, outputStream);
                    break;
                case XLSX:
                    exportReportToStream(new JRXlsxExporter(), jPrint, outputStream);
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

    private void generateWithConversion(OutputStream outputStream, JasperPrint jPrint, String outputExtention) throws IOException, JRException {
        File odt = File.createTempFile("report", ".odt");
        try (FileOutputStream odtOut = new FileOutputStream(odt)) {
            exportReportToStream(new JROdtExporter(), jPrint, odtOut);
            odtOut.flush();
        }
        File doc = File.createTempFile("report", "." + outputExtention);
        String odtMimetype = getServices().getServiceRegistry().getMimetypeService().getMimetype("odt");
        String docMimetype = getServices().getServiceRegistry().getMimetypeService().getMimetype(outputExtention);
        ContentTransformer transformer = getServices().getServiceRegistry().getContentService().getTransformer(odtMimetype, docMimetype);
        FileContentReader reader = new FileContentReader(odt);
        reader.setMimetype(odtMimetype);
        FileContentWriter writer =  new FileContentWriter(doc);
        writer.setMimetype(docMimetype);
        transformer.transform(reader, writer);
        odt.delete();
        try (FileInputStream docIn = new FileInputStream(doc)) {
            IOUtils.copy(docIn, outputStream);
        } catch (IOException e) {
            log.error("Cannot copy streams", e);
        } finally {
            doc.delete();
        }
    }

    private void exportReportToStream(final JRAbstractExporter exporter, final JasperPrint jPrint, OutputStream outputStream)
            throws JRException {
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jPrint);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
        exporter.exportReport();
    }

    @Override
    public void onRegister(ReportDescriptor desc, ReportTemplate template, byte[] templateData, ReportContentDAO storage) throws Exception{
        compileJrxml(desc, template, templateData, storage);
    }

    private void compileJrxml(ReportDescriptor desc, ReportTemplate template, byte[] templateData, ReportContentDAO storage) throws Exception {
        if (templateData == null || template == null) {
            return;
        }

        log.info(String.format("compiling report '%s' ...", desc.getMnem()));

        final ByteArrayInputStream inData = new ByteArrayInputStream(templateData);
        final ByteArrayOutputStream outData = new ByteArrayOutputStream();

        JasperCompileManager.compileReportToStream(inData, outData);
        final IdRContent id = IdRContent.createId(desc, getTemplateFileName(desc, template, ".jasper"));
        storage.storeContent(id, new ByteArrayInputStream(outData.toByteArray()));

        log.info(String.format("Jasper report '%s' compiled SUCCESSFULLY into %s bytes", desc.getMnem(), outData.size()));
    }
}
