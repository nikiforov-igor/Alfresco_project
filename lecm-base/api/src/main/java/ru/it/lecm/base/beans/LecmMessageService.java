package ru.it.lecm.base.beans;

import java.util.List;
import java.util.Locale;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 *
 * @author vmalygin
 */
public interface LecmMessageService {
	NodeRef getDocumentMessageFolder();
	List<Locale> getAvailableLocales();
	List<Locale> getFallbackLocales();
}
