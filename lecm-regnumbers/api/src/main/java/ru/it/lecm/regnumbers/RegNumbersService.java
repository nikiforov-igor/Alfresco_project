package ru.it.lecm.regnumbers;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.regnumbers.template.TemplateParseException;
import ru.it.lecm.regnumbers.template.TemplateRunException;

/**
 *
 * @author vlevin
 */
public interface RegNumbersService {

	/**
	 * Namespace для модели данных
	 */
	String REGNUMBERS_NAMESPACE = "http://www.it.ru/lecm/model/regnumbers/1.0";
	/**
	 * Объект глобального сквозного счетчика, lecm-regnum:plain-counter.
	 */
	QName TYPE_PLAIN_COUNTER = QName.createQName(REGNUMBERS_NAMESPACE, "plain-counter");
	/**
	 * Объект глобального счетчика в рамках года, lecm-regnum:year-counter.
	 */
	QName TYPE_YEAR_COUNTER = QName.createQName(REGNUMBERS_NAMESPACE, "year-counter");
	/**
	 * Объект сквозного счетчика для одного типа документов,
	 * lecm-regnum:doctype-plain-counter.
	 */
	QName TYPE_DOCTYPE_PLAIN_COUNTER = QName.createQName(REGNUMBERS_NAMESPACE, "doctype-plain-counter");
	/**
	 * Объект годового счетчика для одного типа документов,
	 * lecm-regnum:doctype-year-counter.
	 */
	QName TYPE_DOCTYPE_YEAR_COUNTER = QName.createQName(REGNUMBERS_NAMESPACE, "doctype-year-counter");
	/**
	 * Значение счетчика, lecm-regnum:value.
	 */
	QName PROP_VALUE = QName.createQName(REGNUMBERS_NAMESPACE, "value");
	/**
	 * Год для годового счетчика, lecm-regnum:year.
	 */
	QName PROP_YEAR = QName.createQName(REGNUMBERS_NAMESPACE, "year");
	/**
	 * Тип документа, к которому привязан счетчик, lecm-regnum:doctype
	 */
	QName PROP_DOCTYPE = QName.createQName(REGNUMBERS_NAMESPACE, "doctype");

	/**
	 * Сгенерировать номер документа до данному шаблону номера.
	 *
	 * @param documentNode ссылка на экземпляр документа, для которого
	 * необходимо
	 * сгенерировать номер.
	 * @param templateStr шаблон номера документа в виде строки.
	 * @return сгененриванный номер документа.
	 * @throws TemplateParseException В шаблоне есть синтаксическа ошибка:
	 * незакрытые одинарные скобки, пропушен плюс, неверные символы в названии
	 * функций. Детали см. в эксепшене.
	 * @throws TemplateRunException Ошибка на этапе выполнения шаблона:
	 * неверное имя метода, функции или объекта, неверные параметры функции или
	 * метода. Детали см. в эксепшене.
	 */
	String getNumber(NodeRef documentNode, String templateStr) throws TemplateParseException, TemplateRunException;

	/**
	 * Сгенерировать номер документа до данному шаблону номера.
	 *
	 * @param documentNode ссылка на экземпляр документа, для которого
	 * необходимо
	 * сгенерировать номер.
	 * @param templateNode ссылка на шаблон номера (объект типа
	 * lecm-regnum:template)
	 * @return сгененриванный номер документа.
	 * @throws TemplateParseException В шаблоне есть синтаксическа ошибка:
	 * незакрытые одинарные скобки, пропушен плюс, неверные символы в названии
	 * функций. Детали см. в эксепшене.
	 * @throws TemplateRunException Ошибка на этапе выполнения шаблона:
	 * неверное имя метода, функции или объекта, неверные параметры функции или
	 * метода. Детали см. в эксепшене.
	 */
	String getNumber(NodeRef documentNode, NodeRef templateNode) throws TemplateParseException, TemplateRunException;

	/**
	 * Проверить, является ли номер документа уникальным
	 *
	 * @param number номер документа, который необходимо проверить на
	 * уникальность.
	 * @return уникальный/не уникальный
	 */
	boolean isNumberUnique(String number);

	/**
	 * Проверить, является ли шаблон номера синтаксический верным с точки зрения
	 * SpEL и если нет, то почему.
	 *
	 * @param templateStr строка-шаблон.
	 * @param verbose нужно ли возвращать стек-трейс.
	 * @return Пустая строка, если шаблон проходит валидацию. В противном случае
	 * - сообщение из TemplateParseException и, если нужно, стек-трейс.
	 */
	String validateTemplate(String templateStr, boolean verbose);
}
