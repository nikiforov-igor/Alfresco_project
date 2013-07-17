package ru.it.lecm.br5.semantic.api;

import org.alfresco.service.namespace.QName;
import org.alfresco.model.ContentModel;

/**
 *
 * @author snovikov
 */
public interface ConstantsBean {
	public static final QName ASPECT_BR5_INTEGRATION = QName.createQName("http://www.it.ru/lecm/br5/semantic/aspects/1.0","br5");
	public static final QName PROP_BR5_INTEGRATION_LOADED = QName.createQName("http://www.it.ru/lecm/br5/semantic/aspects/1.0","loaded");
}
