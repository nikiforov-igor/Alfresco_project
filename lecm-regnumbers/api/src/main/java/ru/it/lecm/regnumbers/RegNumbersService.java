package ru.it.lecm.regnumbers;

import java.util.Date;
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
	 * Название справочника с шаблонами регистрационных номеров
	 */
	public static final String REGNUMBERS_TEMPLATE_DICTIONARY_NAME = "Шаблоны регистрационных номеров";

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
	 * Объект для хранения шаблона номеров, lecm-regnum:template
	 */
	QName TYPE_REGNUMBERS_TEMPLATE = QName.createQName(REGNUMBERS_NAMESPACE, "template");
	/**
	 * Непосредственно строка шаблона, lecm-regnum:template-string
	 */
	QName PROP_TEMPLATE_STRING = QName.createQName(REGNUMBERS_NAMESPACE, "template-string");
	/**
	 * Комментарий к шаблону, lecm-regnum:template-comment
	 */
	QName PROP_TEMPLATE_COMMENT = QName.createQName(REGNUMBERS_NAMESPACE, "template-comment");
	/**
	 * Служебный идентификатор шаблона номера, lecm-regnum:template-service-id
	 */
	QName PROP_TEMPLATE_SERVICE_ID = QName.createQName(REGNUMBERS_NAMESPACE, "template-service-id");

	/**
	 * Сгенерировать номер документа до данному шаблону номера.
	 *
	 * @param documentNode ссылка на экземпляр документа, для которого
	 * необходимо сгенерировать номер.
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
	 * необходимо сгенерировать номер.
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
	 * Проверить, является ли номер документа уникальным для всех документов в системе
	 *
	 * @param number номер документа, который необходимо проверить на
	 * уникальность.
	 * @return уникальный/не уникальный
	 */
	boolean isNumberUnique(String number);

	/**
	 * Проверить, является ли номер документа уникальным в рамках указанного типа документа
	 *
	 * @param number номер документа, который необходимо проверить на
	 * уникальность.
	 * @param documentType тип документов, в рамках которого проверять уникальность номера
	 * @return уникальный/не уникальный
	 */
	boolean isNumberUnique(String number, QName documentType);

	/**
	 * Проверить, является ли номер документа уникальным в рамках указанного типа документа и года
	 *
	 * @param number номер документа, который необходимо проверить на
	 * уникальность.
	 * @param documentType тип документов, в рамках которого проверять уникальность номера
	 * @param regDate в рамках какого года проверять уникальность регистрационного номера. в качестве атрибута для сравнения используется дата
	 * регистрации документа
	 * @return уникальный/не уникальный
	 */
	boolean isNumberUnique(String number, QName documentType, Date regDate);

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

	/**
	 * Получить строку шаблона регистрационного номера из объекта, в котором
	 * этот шаблон хранится.
	 *
	 * @param templateNode ссылка на объект типа lecm-regnum:template.
	 * @return строка шаблона рег. номера
	 */
	String getTemplateString(NodeRef templateNode);

	/**
	 * Получить регистрационный номер для документа по указанному шаблону и
	 * записать его в указанноый атрибут документа.
	 *
	 * @param documentNode ссылка на экземпляр документа, которому необходимо
	 * присвоить номер.
	 * @param documentProperty в какой атрибут документа необходимо записать
	 * сгенерированный номер.
	 * @param templateStr шаблон номера документа в виде строки.
	 * @throws TemplateParseException В шаблоне есть синтаксическа ошибка:
	 * незакрытые одинарные скобки, пропушен плюс, неверные символы в названии
	 * функций. Детали см. в эксепшене.
	 * @throws TemplateRunException Ошибка на этапе выполнения шаблона:
	 * неверное имя метода, функции или объекта, неверные параметры функции или
	 * метода. Детали см. в эксепшене.
	 * @deprecated use registerProject|registerDocument instead
	 */
	@Deprecated
	void setDocumentNumber(NodeRef documentNode, QName documentProperty, String templateStr) throws TemplateParseException, TemplateRunException;

	/**
	 * Получить регистрационный номер для документа по указанному шаблону и
	 * записать его в указанноый атрибут документа.
	 *
	 * @param documentNode ссылка на экземпляр документа, которому необходимо
	 * присвоить номер.
	 * @param documentProperty в какой атрибут документа необходимо записать
	 * сгенерированный номер.
	 * @param templateNode ссылка на объект шаблона номера документа.
	 * @throws TemplateParseException В шаблоне есть синтаксическа ошибка:
	 * незакрытые одинарные скобки, пропушен плюс, неверные символы в названии
	 * функций. Детали см. в эксепшене.
	 * @throws TemplateRunException Ошибка на этапе выполнения шаблона:
	 * неверное имя метода, функции или объекта, неверные параметры функции или
	 * метода. Детали см. в эксепшене.
	 * @deprecated use registerProject|registerDocument instead
	 */
	@Deprecated
	void setDocumentNumber(NodeRef documentNode, QName documentProperty, NodeRef templateNode) throws TemplateParseException, TemplateRunException;

	/**
	 * Получить регистрационный номер для документа по указанному шаблону и
	 * записать его в указанноый атрибут документа.
	 *
	 * @param documentNode ссылка на экземпляр документа, которому необходимо
	 * присвоить номер.
	 * @param documentPropertyPrefix атрибут документа в префиксальной форме
	 * (prefix:property), в котороый необходимо записать сгенерированный номер.
	 * @param templateStr шаблон номера документа в виде строки.
	 * @throws TemplateParseException В шаблоне есть синтаксическа ошибка:
	 * незакрытые одинарные скобки, пропушен плюс, неверные символы в названии
	 * функций. Детали см. в эксепшене.
	 * @throws TemplateRunException Ошибка на этапе выполнения шаблона:
	 * неверное имя метода, функции или объекта, неверные параметры функции или
	 * метода. Детали см. в эксепшене.
	 * @deprecated use registerProject|registerDocument instead
	 */
	@Deprecated
	void setDocumentNumber(NodeRef documentNode, String documentPropertyPrefix, String templateStr) throws TemplateParseException, TemplateRunException;

	/**
	 * Получить регистрационный номер для документа по указанному шаблону и
	 * записать его в указанноый атрибут документа.
	 *
	 * @param documentNode ссылка на экземпляр документа, которому необходимо
	 * присвоить номер.
	 * @param documentPropertyPrefix атрибут документа в префиксальной форме
	 * (prefix:property), в котороый необходимо записать сгенерированный номер.
	 * @param templateNode ссылка на объект шаблона номера документа.
	 * @throws TemplateParseException В шаблоне есть синтаксическа ошибка:
	 * незакрытые одинарные скобки, пропушен плюс, неверные символы в названии
	 * функций. Детали см. в эксепшене.
	 * @throws TemplateRunException Ошибка на этапе выполнения шаблона:
	 * неверное имя метода, функции или объекта, неверные параметры функции или
	 * метода. Детали см. в эксепшене.
	 * @deprecated use registerProject|registerDocument instead
	 */
	@Deprecated
	void setDocumentNumber(NodeRef documentNode, String documentPropertyPrefix, NodeRef templateNode) throws TemplateParseException, TemplateRunException;

	/**
	 * Получить регистрационный номер для документа по указанному шаблону и
	 * записать его в указанноый атрибут документа.
	 *
	 * @param documentNode ссылка на экземпляр документа, которому необходимо
	 * присвоить номер.
	 * @param documentPropertyPrefix атрибут документа в префиксальной форме
	 * (prefix:property), в котороый необходимо записать сгенерированный номер.
	 * @param dictionaryTemplateCode код элемента справочника с шаблоном номера.
	 * @throws TemplateParseException В шаблоне есть синтаксическа ошибка:
	 * незакрытые одинарные скобки, пропушен плюс, неверные символы в названии
	 * функций. Детали см. в эксепшене.
	 * @throws TemplateRunException Ошибка на этапе выполнения шаблона:
	 * неверное имя метода, функции или объекта, неверные параметры функции или
	 * метода. Детали см. в эксепшене.
	 * @deprecated use registerProject|registerDocument instead
	 */
	@Deprecated
	void setDocumentNumber(String dictionaryTemplateCode, NodeRef documentNode, String documentPropertyPrefix) throws TemplateParseException, TemplateRunException;

	/**
	 * Получить NodeRef шаблона по его коду.
	 *
	 * @param dictionaryTemplateCode код шаблона в справочнике.
	 * @return ссылка на объект шаблона.
	 */
	NodeRef getTemplateNodeByCode(String dictionaryTemplateCode) throws TemplateParseException, TemplateRunException;

	void registerProject(NodeRef documentNode, String dictionaryTemplateCode) throws TemplateParseException, TemplateRunException;

	void registerDocument(NodeRef documentNode, String dictionaryTemplateCode) throws TemplateParseException, TemplateRunException;

	void registerProject(NodeRef documentNode, String dictionaryTemplateCode, boolean onlyReserve) throws TemplateParseException, TemplateRunException;

	void registerDocument(NodeRef documentNode, String dictionaryTemplateCode, boolean onlyReserve) throws TemplateParseException, TemplateRunException;

	void registerProject(NodeRef templateRef, NodeRef documentNode) throws TemplateParseException, TemplateRunException;

	void registerDocument(NodeRef documentNode, NodeRef templateRef) throws TemplateParseException, TemplateRunException;

	/**
	 * Проверить, зарегистрирован ли документ
	 *
	 * @param documentNode ссылка на экземпляр документа, который необходимо проверить на регистрацию.
	 * @return зарегистрирован/не зарегистрирован
	 */
	boolean isRegistered(NodeRef documentNode, boolean isProject);
}
