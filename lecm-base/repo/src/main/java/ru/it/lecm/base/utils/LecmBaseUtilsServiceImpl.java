package ru.it.lecm.base.utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.BaseBean;

/**
 *
 * @author dbayandin
 */
public class LecmBaseUtilsServiceImpl extends BaseBean implements LecmBaseUtilsService {
	
	private Map<QName, Serializable> propertiesMap;
	
	@Override
	public NodeRef getServiceRootFolder() {
		return getFolder(LECM_SECRET_FOLDER_ID);
	}
	
	public void init() {
		this.propertiesMap = new HashMap<QName, Serializable>();
		
		NodeRef folderRef = getServiceRootFolder();
	}
	
	public Boolean checkProperties(NodeRef nodeRef, Map<QName, Serializable> properties) {
		Boolean result = false;
		
		return result;
	}
}
