package ru.it.lecm.base.extensions;

import org.alfresco.repo.jscript.ScriptNode;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.base.beans.LecmMessageService;

/**
 *
 * @author vmalygin
 */
public class LecmMessageJavascriptExtension extends BaseWebScript {

	private LecmMessageService lecmMessageService;

	public void setLecmMessageService(LecmMessageService lecmMessageService) {
		this.lecmMessageService = lecmMessageService;
	}

	public boolean isMlSupported() {
		return lecmMessageService.isMlSupported();
	}
}
