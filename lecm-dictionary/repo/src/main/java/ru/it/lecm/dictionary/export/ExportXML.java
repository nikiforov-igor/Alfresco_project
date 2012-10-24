package ru.it.lecm.dictionary.export;

import org.alfresco.repo.exporter.ACPExportPackageHandler;
import org.alfresco.repo.processor.BaseProcessorExtension;
import org.alfresco.service.cmr.action.ActionServiceException;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.view.ExporterCrawlerParameters;
import org.alfresco.service.cmr.view.ExporterService;
import org.alfresco.service.cmr.view.Location;
import org.alfresco.util.TempFileProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * User: mShafeev
 * Date: 23.10.12
 * Time: 12:01
 */
public class ExportXML extends BaseProcessorExtension {
    public static final String PARAM_PACKAGE_NAME = "package-name";
    private static final String TEMP_FILE_PREFIX = "alf";
    /**
     * The Mime type service
     */
    private MimetypeService mimetypeService;

    /**
     * The exporter service
     */
    private ExporterService exporterService;

    /**
     * Sets the ExporterService to use
     *
     * @param exporterService The ExporterService
     */
    public void setExporterService(ExporterService exporterService)
    {
        this.exporterService = exporterService;
    }

    /**
     * Sets the MimetypeService to use
     *
     * @param mimetypeService
     */
    public void setMimetypeService(MimetypeService mimetypeService)
    {
        this.mimetypeService = mimetypeService;
    }

    private static Log logger = LogFactory.getLog(ExportXML.class);
    public String getXML() throws FileNotFoundException {

//        Location location = new Location(new StoreRef(nodeRef));
//        location.setPath("/");
//        ExporterCrawlerParameters parameters = new ExporterCrawlerParameters();
//        parameters.setExportFrom(location);
//        ExportPackageHandler exportHandler = null;
//
//        ExporterComponent exporterComponent = new ExporterComponent();
//        exporterComponent.exportView(exportHandler, parameters, null);


//        JCRSystemXMLExporter exporter = new JCRSystemXMLExporter(this, createExportContentHandler(out));
        String responseXML = "This is response XML";
        return responseXML;
    }

    public String getXMLFile(String node){
        NodeRef nodeRef = new NodeRef(node);
        String fileName = "testfile";
        File zipFile = null;
        try {
            File dataFile = new File(fileName);
            File contentDir = new File(fileName);
            String type = ACPExportPackageHandler.ACP_EXTENSION;
            zipFile = TempFileProvider.createTempFile(TEMP_FILE_PREFIX, type);
            ACPExportPackageHandler zipHandler = new ACPExportPackageHandler(new FileOutputStream(zipFile),
                    dataFile, contentDir, mimetypeService);

            ExporterCrawlerParameters params = new ExporterCrawlerParameters();
            boolean includeChildren = true;
            params.setCrawlChildNodes(includeChildren);
            params.setExportFrom(new Location(nodeRef));
            this.exporterService.exportView(zipHandler, params, null);

        } catch (FileNotFoundException fnfe)
        {
            throw new ActionServiceException("export.package.error", fnfe);
        }
        finally
        {
            // try and delete the temporary file
            if (zipFile != null)
            {
               // zipFile.delete();
            }
        }


        return "File create";
//        JCRSystemXMLExporter exporter = new JCRSystemXMLExporter(this, createExportContentHandler(out));
    }

}
