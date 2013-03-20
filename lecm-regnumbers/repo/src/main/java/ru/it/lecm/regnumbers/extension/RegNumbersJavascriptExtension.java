package ru.it.lecm.regnumbers.extension;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.regnumbers.RegNumbersService;

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
		return regNumbersService.getNumber(documentNode.getNodeRef(), templateNode.getNodeRef());
	}

	public String getNumber(ScriptNode documentNode, String templateStr) {
		String result;

		if (NodeRef.isNodeRef(templateStr)) {
			result = regNumbersService.getNumber(documentNode.getNodeRef(), new NodeRef(templateStr));
		} else {
			result = regNumbersService.getNumber(documentNode.getNodeRef(), templateStr);
		}

		return result;
	}

	public boolean isNumberUnique(String number) {
		return regNumbersService.isNumberUnique(number);
	}
}
