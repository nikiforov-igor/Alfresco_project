package ru.it.lecm.notifications.template;

import java.io.Serializable;
import java.util.List;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 *
 * @author vkuprin
 */
public interface CMObject {
	
	String getPresentString();
	
	String getViewUrl();
	
	String wrapAsLink();
	
	/**
	 * Получить аттрибут объекта.
	 *
	 * @param attributeName название атрибута из модели данных.
	 * @return атрибут документа.
	 */
	Serializable attribute(String attributeName);
	
	/**
	 * Получить ссылку на ассоциированный с документом объект по
	 * названию ассоциации. Будет возвращен только один объект,
	 * независимо от того, множественная ассоциация или нет.
	 * Поддерживаются только target-ассоциации.
	 *
	 * @param assocName название ассоциации из модели данных в префиксальной форме.
	 * @return ссылка на ассоциированный объект.
	 */
	CMObject getAssoc(String assocName);
	
	NodeRef getNodeRef();
	
	List<CMObject> getAssocs(String assocName);
	
	String getType();

	String wrapAsLink(String description);

	String getFormatted(String substitudeString);
}
