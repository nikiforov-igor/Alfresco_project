package ru.it.lecm.reports.ooffice;

import com.sun.star.beans.PropertyValue;
import com.sun.star.comp.helper.Bootstrap;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.reports.generators.GenericDSProviderBase;

import java.text.SimpleDateFormat;

public class DSProviderOOfficeBase extends GenericDSProviderBase {

    private static final Logger logger = LoggerFactory.getLogger(DSProviderOOfficeBase.class);

    public void openOfficeExecute() {
        final String ooFileNameTemplate = "ExampleArgsOfTheDoc.odt";
        final String ooFileNameResult = "/reportdefinitions/oo-templates/generated.odt";

        try {
            ooConvert(ooFileNameTemplate, ooFileNameResult);
        } catch (Throwable tx) {
            logger.error(String.format("Error generating ooffice new file\n\t '{%s}'\n\t from '{%s}'\n\t error %s", ooFileNameResult, ooFileNameTemplate, tx.getMessage()), tx);
        }
    }

    private static void ooConvert(String namein, String nameout) throws Exception {
        final String sLoadUrl = "file:///" + namein.replace('\\', '/');
        final String sSaveUrl = "file:///" + nameout.replace('\\', '/');

        // Get the remote office component context
        XComponentContext xContext = Bootstrap.bootstrap();
        logger.info("Connected to a running office ...");

        // Get the remote office service manager
        XMultiComponentFactory xMCF = xContext.getServiceManager();

        // Get the root frame (i.e. desktop) of openoffice framework.
        Object oDesktop = xMCF.createInstanceWithContext("com.sun.star.frame.Desktop", xContext);

        // Desktop has 3 interfaces. The XComponentLoader interface provides ability to load components.
        XComponentLoader xCLoader = UnoRuntime.queryInterface(XComponentLoader.class, oDesktop);

        XComponent document;
        { // OPEN FILE
            final PropertyValue[] props = new PropertyValue[4];
            props[0] = newPropertyValue("МоёПолеТекст", "абвгдеёжзик");
            props[1] = newPropertyValue("МояДата", (new SimpleDateFormat("yyyy/MM/dd")).parse("2012/03/22"));
            props[2] = newPropertyValue("МyFieldText", "abcdefghijk");
            props[3] = newPropertyValue("MyFieldNumber", 123);

            // Create a document
            document = xCLoader.loadComponentFromURL(sLoadUrl, "_blank", 0, props);
            logger.info("\nDocument \"" + sLoadUrl + "\" saved under \"" + sSaveUrl + "\"\n");
        }


        // Saving a document
        com.sun.star.frame.XStorable xStorable = null;
        if (document != null) {
            xStorable = UnoRuntime.queryInterface(com.sun.star.frame.XStorable.class, document);

            final PropertyValue[] storeProps = new PropertyValue[1];
            storeProps[0].Name = "FilterName";
            storeProps[0].Value = "Rich Text Format";
            xStorable.storeAsURL(sSaveUrl, storeProps);
            logger.info("\nDocument \"" + sLoadUrl + "\" saved under \"" + sSaveUrl + "\"\n");
        }

        // Get the textdocument

        // Closing the converted document. Use XCloseable.close if the
        // interface is supported, otherwise use XComponent.dispose
        com.sun.star.util.XCloseable xCloseable = UnoRuntime.queryInterface(com.sun.star.util.XCloseable.class, xStorable);

        if (xCloseable != null) {
            xCloseable.close(false);
        } else {
            com.sun.star.lang.XComponent xComp = UnoRuntime.queryInterface(com.sun.star.lang.XComponent.class, xStorable);
            xComp.dispose();
        }
    }

    public static PropertyValue newPropertyValue(String propName, Object propVal) {
        final PropertyValue result = new PropertyValue();
        result.Name = propName;
        result.Value = propVal;
        return result;
    }
}
