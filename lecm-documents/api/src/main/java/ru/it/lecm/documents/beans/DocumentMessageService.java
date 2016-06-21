package ru.it.lecm.documents.beans;

import java.util.List;
import java.util.Locale;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 *
 * @author vmalygin
 */
public interface DocumentMessageService {
	NodeRef getDocumentMessageFolder();
	boolean registerResourceBundle(NodeRef messageResource);
	boolean unregisterResourceBundle(NodeRef messageResource);
	void loadMessagesFromLocation(String messageLocation, boolean useDefault);
	List<Locale> getAvailableLocales();
}
