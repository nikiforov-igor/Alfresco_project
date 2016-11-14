package ru.it.lecm.base.beans;

import java.util.List;
import java.util.Locale;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 *
 * @author vmalygin
 */
public interface LecmMessageService {
	List<Locale> getMlLocales();
	List<Locale> getFallbackLocales();
	boolean isMlSupported();
}
