package ru.it.lecm.barcode.extensions;

import java.util.List;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.jscript.ValueConverter;
import org.mozilla.javascript.Scriptable;
import ru.it.lecm.barcode.beans.BarcodePrintService;
import ru.it.lecm.barcode.beans.BarcodePrintServiceImpl;
import ru.it.lecm.base.beans.BaseWebScript;

/**
 *
 * @author vlevin
 */
public class BarcodePrintExtension extends BaseWebScript {

	private BarcodePrintService barcodePrintService;

	public void setBarcodePrintService(BarcodePrintServiceImpl barcodePrintService) {
		this.barcodePrintService = barcodePrintService;
	}

	public void print(ScriptNode documentNode, Scriptable additionalString) {
		ValueConverter converter = new ValueConverter();
		List<String> additionalStringList = (List<String>) converter.convertValueForJava(additionalString);
		barcodePrintService.print(documentNode.getNodeRef(), additionalStringList);
	}

	public String getPrinterNameByEmployee(ScriptNode employeeRef) {
		return barcodePrintService.getPrinterNameByEmployee(employeeRef.getNodeRef());
	}

	public boolean isBarcodeEnabled() {
		return barcodePrintService.isBarcodeEnabled();
	}
}
