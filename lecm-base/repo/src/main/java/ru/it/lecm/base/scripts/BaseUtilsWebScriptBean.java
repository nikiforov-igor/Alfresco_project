package ru.it.lecm.base.scripts;

import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.base.utils.LecmBaseUtilsServiceImpl;

/**
 *
 * @author dbayandin
 */
public class BaseUtilsWebScriptBean extends BaseWebScript {
	private LecmBaseUtilsServiceImpl baseUtilsService;
	
	public void setBaseUtilsService(LecmBaseUtilsServiceImpl baseUtilsService) {
		this.baseUtilsService = baseUtilsService;
	}
	
	public Boolean checkProperties() {
		return baseUtilsService.checkProperties(null, null);
	}
}
