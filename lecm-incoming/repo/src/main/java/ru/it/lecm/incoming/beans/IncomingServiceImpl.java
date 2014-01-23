package ru.it.lecm.incoming.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.BaseBean;

/**
 * User: AIvkin
 * Date: 17.01.14
 * Time: 11:59
 */
public class IncomingServiceImpl extends BaseBean {
	public static final String INCOMING_NAMESPACE_URI = "http://www.it.ru/logicECM/incoming/1.0";

	public static final QName TYPE_INCOMING = QName.createQName(INCOMING_NAMESPACE_URI, "document");

    public static final QName ASSOC_RECIPIENT = QName.createQName(INCOMING_NAMESPACE_URI, "recipient-assoc");

    public static final QName PROP_EXECUTION_DATE = QName.createQName(INCOMING_NAMESPACE_URI, "execution-date");

	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}
}
