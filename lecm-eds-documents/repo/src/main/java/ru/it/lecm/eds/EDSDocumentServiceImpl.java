package ru.it.lecm.eds;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.eds.api.EDSDocumentService;

/**
 * User: dbashmakov
 * Date: 24.01.14
 * Time: 12:48
 */
public class EDSDocumentServiceImpl extends BaseBean implements EDSDocumentService {

	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}

}
