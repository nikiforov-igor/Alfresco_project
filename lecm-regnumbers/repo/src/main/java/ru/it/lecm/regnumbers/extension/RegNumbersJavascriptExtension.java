package ru.it.lecm.regnumbers.extension;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.springframework.extensions.webscripts.WebScriptException;
import ru.it.lecm.regnumbers.RegNumbersService;
import ru.it.lecm.regnumbers.template.TemplateParseException;
import ru.it.lecm.regnumbers.template.TemplateRunException;

/**
 *
 * @author vlevin
 */
public class RegNumbersJavascriptExtension extends BaseScopableProcessorExtension {

	private RegNumbersService regNumbersService;

	public void setRegNumbersService(RegNumbersService regNumbersService) {
		this.regNumbersService = regNumbersService;
	}

	public String getNumber(ScriptNode documentNode, ScriptNode templateNode) {
		try {
			return regNumbersService.getNumber(documentNode.getNodeRef(), templateNode.getNodeRef());
		} catch (TemplateParseException ex) {
			throw new WebScriptException(String.format("Error parsing registration number template node [%s]", templateNode.toString()), ex);
		} catch (TemplateRunException ex) {
			throw new WebScriptException(String.format("Error running registration number template node [%s]", templateNode.toString()), ex);
		}
	}

	public String getNumber(ScriptNode documentNode, String templateStr) {
		String result;

		try {
			if (NodeRef.isNodeRef(templateStr)) {
				result = regNumbersService.getNumber(documentNode.getNodeRef(), new NodeRef(templateStr));
			} else {
				result = regNumbersService.getNumber(documentNode.getNodeRef(), templateStr);
			}
		} catch (TemplateParseException ex) {
			throw new WebScriptException(String.format("Error parsing registration number template '%s'", templateStr), ex);
		} catch (TemplateRunException ex) {
			throw new WebScriptException(String.format("Error running registration number template '%s'", templateStr), ex);
		}

		return result;
	}

	public String validateTemplate(String templateStr, boolean verbose) {
		return regNumbersService.validateTemplate(templateStr, verbose);
	}

	public boolean isNumberUnique(String number) {
		return regNumbersService.isNumberUnique(number);
	}
}
