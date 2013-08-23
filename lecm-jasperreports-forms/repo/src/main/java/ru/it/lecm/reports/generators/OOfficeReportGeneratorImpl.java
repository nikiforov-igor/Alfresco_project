package ru.it.lecm.reports.generators;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.WebScriptResponse;

import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.api.model.DAO.ReportContentDAO;
import ru.it.lecm.reports.api.model.DAO.ReportContentDAO.IdRContent;
import ru.it.lecm.reports.model.DAO.FileReportContentDAOBean;
import ru.it.lecm.reports.utils.Utils;

import com.sun.star.beans.PropertyValue;
import com.sun.star.comp.helper.Bootstrap;
import com.sun.star.comp.helper.BootstrapException;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;

public class OOfficeReportGeneratorImpl extends ReportGeneratorBase {

	private static final transient Logger logger = LoggerFactory.getLogger(OOfficeReportGeneratorImpl.class);

	private ReportContentDAO templatesDAO;

	public ReportContentDAO getTemplatesDAO() {
		return templatesDAO;
	}

	public void setTemplatesDAO(ReportContentDAO templatesDAO) {
		this.templatesDAO = templatesDAO;
	}

	@Override
	public void onRegister(ReportDescriptor desc, byte[] templateData,
			ReportContentDAO storage)
	{
		logger.info( String.format( "decriptor deploy /'%s'/  \"%s\"", desc.getMnem(), desc.getDefault()));
		openOfficeExecute(desc, storage);
	}

	@Override
	public void produceReport(
			WebScriptResponse webScriptResponse,
			ReportDescriptor desc, Map<String, String[]> parameters,
			ReportContentDAO rptContent
	) throws IOException
	{
		logger.info( String.format( "producing report /'%s'/ \"%s\"", desc.getMnem(), desc.getDefault()));
		openOfficeExecute(desc, rptContent);
	}

	private void openOfficeExecute(ReportDescriptor desc, ReportContentDAO storage) {
		// "/reportdefinitions/templates/ooffice/ExampleArgsOfTheDoc.odt"
		// "/reportdefinitions/oo-templates/ExampleArgsOfTheDoc.odt";
		final String ooFileNameTemplate = "ExampleArgsOfTheDoc.odt";

		// "/reportdefinitions/oo-templates/generated.odt";
		final String ooFileNameResult = Utils.nonblank(desc.getMnem(), "generated") + ".odt";

		try {
			ooConvert( desc, storage, ooFileNameTemplate, ooFileNameResult);
		} catch(Throwable tx) {
			logger.error(String.format( "Error generating ooffice new file\n\t '{%s}'\n\t from '{%s}'\n\t error %s", ooFileNameResult, ooFileNameTemplate, tx.getMessage()), tx);
		}
	}

	private void ooConvert(ReportDescriptor desc, ReportContentDAO storage, String namein, String nameout)
			throws BootstrapException, com.sun.star.io.IOException, Exception, MalformedURLException
	{

		// final File sourceFile = new java.io.File(args[1]);
		// String sSaveUrl = "file:///"+ sourceFile.getCanonicalPath().replace('\\', '/');
		FileReportContentDAOBean fbean = new FileReportContentDAOBean();
		{
			fbean.setRootDir("/build/reports");
		}

		// final String sLoadUrl = "file:///"+ namein.replace('\\', '/');
		final IdRContent idIn = IdRContent.createId(desc, namein);
		if (!templatesDAO.exists(idIn)) {
			logger.warn(String.format( "file not found by id {%s}", idIn));
			return;
		}
		final String sLoadUrl = (new File( templatesDAO.getRoot()+ "/"+ namein)).toURI().toURL().toExternalForm();

		// final String sSaveUrl = "file:///"+ nameout.replace('\\', '/');
		final String sSaveUrl = (new File( fbean.getRoot()+ "/"+ nameout)).toURI().toURL().toExternalForm();

		// ensureOfficeLocally();

		// Get the remote office component context
		XComponentContext xContext = Bootstrap.bootstrap();
		logger.info("Connected to a running office ...");

		// Get the remote office service manager
		XMultiComponentFactory xMCF = xContext.getServiceManager();

		// Get the root frame (i.e. desktop) of openoffice framework.
		Object oDesktop = xMCF.createInstanceWithContext("com.sun.star.frame.Desktop", xContext);

		// Desktop has 3 interfaces. The XComponentLoader interface provides ability to load components.
		XComponentLoader xCLoader =  ( XComponentLoader ) UnoRuntime.queryInterface(XComponentLoader.class, oDesktop);

		XComponent document =  null;
		{ // OPEN FILE
			final PropertyValue[] props = new PropertyValue[4];
			props[0] = newPropertyValue("МоёПолеТекст", "абвгдеёжзик");
			props[1] = newPropertyValue("МояДата", (new SimpleDateFormat("yyyy/MM/dd")).parse("2012/03/22") );
			props[2] = newPropertyValue("МyFieldText", "abcdefghijk");
			props[3] = newPropertyValue("MyFieldNumber", 123);

			// Create a document
			document = xCLoader.loadComponentFromURL( sLoadUrl, "_blank", 0, props);
			// Object oDocToStore = xCLoader.loadComponentFromURL( sLoadUrl.toString(), "_blank", 0, propertyValue );
			logger.info("\nDocument \"" + sLoadUrl + "\" saved under \"" + sSaveUrl + "\"\n");
		}


		// Saving a document
		com.sun.star.frame.XStorable xStorable = null;
		if (document != null) {
			xStorable = (com.sun.star.frame.XStorable)
					UnoRuntime.queryInterface(com.sun.star.frame.XStorable.class, document );

			/*
			storeProps = new com.sun.star.beans.PropertyValue[ 2 ];
			storeProps[0] = new com.sun.star.beans.PropertyValue();
			storeProps[0].Name = "Overwrite";
			storeProps[0].Value = new Boolean(true);
			storeProps[1] = new com.sun.star.beans.PropertyValue();
			storePropse[1].Name = "FilterName";
			storeProps[1].Value = "StarOffice XML (Writer)";
			*/
			final PropertyValue[] storeProps = new PropertyValue[1];
			storeProps[0].Name = "FilterName";
			storeProps[0].Value = "Rich Text Format";
			xStorable.storeAsURL( sSaveUrl.toString(), storeProps);
			logger.info("\nDocument \"" + sLoadUrl + "\" saved under \"" +sSaveUrl + "\"\n");
		}

		// Get the textdocument
		// XTextDocument aTextDocument = ( XTextDocument )UnoRuntime.queryInterface(com.sun.star.text.XTextDocument.class, document);

		{
			// Closing the converted document. Use XCloseable.close if the
			// interface is supported, otherwise use XComponent.dispose
			com.sun.star.util.XCloseable xCloseable =
					(com.sun.star.util.XCloseable)UnoRuntime.queryInterface(
							com.sun.star.util.XCloseable.class, xStorable);

			if ( xCloseable != null ) {
				xCloseable.close(false);
			} else {
				com.sun.star.lang.XComponent xComp =
						(com.sun.star.lang.XComponent)UnoRuntime.queryInterface(
								com.sun.star.lang.XComponent.class, xStorable);
				xComp.dispose();
			}
		}
	}

	public static PropertyValue newPropertyValue(String propName, Object propVal) {
		final PropertyValue result = new PropertyValue();
		result.Name = propName;
		result.Value = propVal;
		return result;
	}
}
