package ru.it.lecm.regnumbers.template;

import java.util.Date;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Обертка вокруг документа. Объект данного класса играет роль объекта 'doc' в
 * шаблоне номера документа. Соответственно, геттеры объекта можно дергать из
 * шаблона номера (читай, SpEL'а) таким образом: Document.getFoo() -> doc.foo.
 * Необходимо помнить, что параметры геттеру передать нельзя. Поэтому, если
 * нужен метод с параметрами, то он выглядит таким образом: Document.bar(arg) ->
 * doc.bar(arg).
 *
 * @author vlevin
 */
interface Document {

	/**
	 * Получить аттрибут документа.
	 *
	 * @param attributeName название атрибута из модели данных.
	 * @return атрибут документа.
	 */
	Object attribute(String attributeName);

	/**
	 * Получить атрибут объекта по названию ассоциации с документом и названию
	 * атрибута целевого объекта. Если документ имеет несколько ассоциаций с
	 * таким названием, то выводятся аттрибуты всех связанных объектов через
	 * запятую и пробел (", "). Поддерживаются только target-ассоциации.
	 *
	 * @param assocName название ассоциации из модели данных в префиксальной
	 * форме.
	 * @param attributeName название атрибута целевого объекта из
	 * модели данных в префиксальной форме.
	 * @return значение атрибута. Если атрибутов несколько, что значения
	 * формируются в виде строки с разделителем в виде ", ". Если ассоциации или
	 * атирибута не существует, то пустая строка.
	 */
	Object associatedAttribute(String assocName, String attributeName);

	/**
	 * Получить атрибут по пути из ассоциаций, ведущему свое начала от
	 * документа. Метод не поддерживает множественные ассоциации и будет идти
	 * только по первой встретившейся. Путь из ассоциаций к атрибуту выглядит
	 * следующим образом: "prefix1:assoc1/prefix2:assoc2/prefix3:property".
	 * Поддерживаются только target-ассоциации.
	 *
	 * @param attributePath путь к атрибуту из ассоциаций. Все элементы пути
	 * должны быть указаны в префиксальной форме.
	 * @return значение указанного атрибута. Если путь из ассоциаций неверный,
	 * или атрибут отсутствует, то пустая строка.
	 */
	Object associatedAttributePath(String attributePath);

	/**
	 * Получить ссылку на ассоциированный с документом объект по
	 * названию ассоциации. Будет возвращен только один объект,
	 * независимо от того, множественная ассоциация или нет.
	 * Поддерживаются только target-ассоциации.
	 *
	 * @param assocName название ассоциации из модели данных в префиксальной форме.
	 * @return ссылка на ассоциированный объект.
	 */
	NodeRef getAssoc(String assocName);

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
	NodeRef member(String memberType);

	/**
	 * Получить создателя документа.
	 *
	 * @return ссылка на сотрудника, создавшего документ.
	 */
	NodeRef getCreator();

	/**
	 * Получить сотрудника, который изменял документ последним.
	 *
	 * @return ссылка на последнего изменявшего документ сотрудника.
	 */
	NodeRef getModifier();

	/**
	 * Получить значение сквозного счетчика, единого для всей системы.
	 *
	 * @return значение счетчика.
	 */
	long getCounterPlain();

	/**
	 * Получить значение счетчика в пределах года, единого для всей системы.
	 * Каждый год нумерация начинается заново.
	 *
	 * @return значение счетчика.
	 */
	long getCounterYear();

	/**
	 * Получить значение сквозного счетчика, отдельного для каждого из типов
	 * документов.
	 *
	 * @return значение счетчика.
	 */
	long getCounterPlainDoctype();

	/**
	 * Получить значение счетчика в пределах года, отдельного для каждого из
	 * типов документов.
	 * Каждый год нумерация начинается заново.
	 *
	 * @return значение счетчика.
	 */
	long getCounterYearDoctype();

	/**
	 * Получить значение сквозного счетчика, отдельного для каждой пары "тип
	 * документа - тэг"
	 *
	 * @param tag метка счетчика
	 * @return значение счетчика.
	 */
	long counterPlainDoctype(String tag);

	/**
	 * Получить значение счетчика в пределах года, отдельного для каждой пары
	 * "тип документа - тэг"
	 * Каждый год нумерация начинается заново.
	 *
	 * @param tag метка счетчика
	 * @return значение счетчика.
	 */
	long counterYearDoctype(String tag);
	/**
	 * Получить значение счетчика, используемого в документообороте.
	 *
	 * @return значение счетчика.
	 */
	long getCounterSignedDocflow();

	/**
	 * @return Дата содания документа.
	 */
	Date getCreationDate();

}
