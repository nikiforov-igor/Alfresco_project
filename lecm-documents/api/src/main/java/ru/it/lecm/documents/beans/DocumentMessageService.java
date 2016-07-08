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
	List<Locale> getAvailableLocales();
	List<Locale> getFallbackLocales();
}
