package ru.it.lecm.regnumbers.template;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 *
 * @author vlevin
 */
public interface Document {

	/**
	 * Получить аттрибут документа.
	 *
	 * @param attributeName название атрибута из модели данных.
	 * @return атрибут документа.
	 */
	Object getAttribute(String attributeName);

	/**
	 * Получить атрибут объекта по названию ассоциации с документом и названию
	 * атрибута целевого объекта.
	 *
	 * @param assocName название ассоциации, указанное в модели данных.
	 * @param attributeName название атрибута целевого объекта, указанное в
	 * модели данных.
	 * @return значение атрибута.
	 */
	Object getAssosiatedAttribute(String assocName, String attributeName);

	/**
	 * Колучить код типа документа.
	 *
	 * @return код типа документа.
	 */
	int getTypeCode();

	/**
	 * Получить название типа документа.
	 *
	 * @return название типа документа.
	 */
	String getTypeName();

	/**
	 * Получить участника документа.
	 *
	 * @param memberType Какого участника надо получить
	 * @return ссылка на участника.
	 */
	NodeRef getMember(String memberType);
}
