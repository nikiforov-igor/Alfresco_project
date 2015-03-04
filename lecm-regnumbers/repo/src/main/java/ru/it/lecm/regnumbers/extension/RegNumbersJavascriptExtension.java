package ru.it.lecm.regnumbers.extension;

import java.util.Date;
import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.springframework.extensions.webscripts.WebScriptException;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.regnumbers.RegNumbersService;
import ru.it.lecm.regnumbers.template.TemplateParseException;
import ru.it.lecm.regnumbers.template.TemplateRunException;

/**
 * JavaScript root-object под названием regnumbers. Предоставляет доступ к
 * методам RegNumbersService из JS web-скриптов.
 *
 * @author vlevin
 */
public class RegNumbersJavascriptExtension extends BaseScopableProcessorExtension {

	private RegNumbersService regNumbersService;
	private NodeService nodeService;

	public void setRegNumbersService(RegNumbersService regNumbersService) {
		this.regNumbersService = regNumbersService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	/**
	 * Сгенерировать номер документа до данному шаблону номера.
	 * При ошибке с шаблоне генерирует исключение WebScriptException
	 *
	 * @param documentNode ссылка на экземпляр документа, для которого
	 * необходимо сгенерировать номер.
	 * @param templateNode ссылка на шаблон номера (объект типа
	 * lecm-regnum:template)
	 * @return сгенерированный номер документа.
	 * @throws WebScriptException в случае неудачной генерации номера.
	 */
	public String getNumber(ScriptNode documentNode, ScriptNode templateNode) {
		try {
			return regNumbersService.getNumber(documentNode.getNodeRef(), templateNode.getNodeRef());
		} catch (TemplateParseException ex) {
			throw new WebScriptException(String.format("Error parsing registration number template node [%s]", templateNode.toString()), ex);
		} catch (TemplateRunException ex) {
			throw new WebScriptException(String.format("Error running registration number template node [%s]", templateNode.toString()), ex);
		}
	}

	/**
	 * Сгенерировать номер документа до данному шаблону номера.
	 * При ошибке с шаблоне генерирует исключение WebScriptException
	 *
	 * @param documentNode ссылка на экземпляр документа, для которого
	 * необходимо сгенерировать номер.
	 * @param templateStr шаблон номера документа в виде строки либо NodeRef
	 * шаблон номера (объект типа lecm-regnum:template) в виде строки
	 * @return сгенерированный номер документа.
	 * @throws WebScriptException в случае неудачной генерации номера.
	 */
	public String getNumber(ScriptNode documentNode, String templateStr) {
		String result;

		try {
			if (NodeRef.isNodeRef(templateStr)) {
				result = regNumbersService.getNumber(documentNode.getNodeRef(), new NodeRef(templateStr));
			} else {
				result = regNumbersService.getNumber(documentNode.getNodeRef(), templateStr);
			}
		} catch (TemplateParseException ex) {
			throw new WebScriptException(String.format("Error parsing registration number template '%s'", templateStr), ex);
		} catch (TemplateRunException ex) {
			throw new WebScriptException(String.format("Error running registration number template '%s'", templateStr), ex);
		}

		return result;
	}

	/**
	 * Проверить, является ли шаблон номера синтаксический верным с точки зрения
	 * SpEL и если нет, то почему.
	 *
	 * @param templateStr строка-шаблон.
	 * @param verbose нужно ли возвращать стек-трейс.
	 * @return Пустая строка, если шаблон проходит валидацию. В противном случае
	 * - сообщение из TemplateParseException и, если нужно, стек-трейс.
	 */
	public String validateTemplate(String templateStr, boolean verbose) {
		return regNumbersService.validateTemplate(templateStr, verbose);
	}

	/**
	 * Проверить, является ли номер документа уникальным для всех документов в системе
	 *
	 * @param number номер документа, который необходимо проверить на
	 * уникальность.
	 * @return уникальный/не уникальный
	 */
	public boolean isNumberUnique(String number) {
		return regNumbersService.isNumberUnique(number);
	}

	/**
	 * Проверить, является ли номер документа уникальным в рамках определенного типа документа и года.
	 * Год определяется по дате регистрации документа.
	 *
	 * @param number номер документа, который необходимо проверить на
	 * уникальность.
	 * @param documentNodeRefStr NodeRef документа, в рамках типа которого проверять уникальность номера
	 * @return уникальный/не уникальный
	 */
	public boolean isNumberUnique(String number, String documentNodeRefStr) {
		return isNumberUnique(number, documentNodeRefStr, true);
	}

	/**
	 * Проверить, является ли номер документа уникальным в рамках определенного типа документа и года.
	 * Год определяется по дате регистрации документа.
	 *
	 * @param number номер документа, который необходимо проверить на
	 * уникальность.
	 * @param documentNodeRef NodeRef документа, в рамках типа которого проверять уникальность номера
	 * @return уникальный/не уникальный
	 */
	public boolean isNumberUnique(String number, ScriptNode documentNodeRef) {
		return isNumberUnique(number, documentNodeRef.getNodeRef().toString(), true);
	}

	/**
	 * Проверить, является ли номер документа уникальным в рамках определенного типа документа и, опционально, года.
	 * Год определяется по дате регистрации документа.
	 *
	 * @param number номер документа, который необходимо проверить на
	 * уникальность.
	 * @param documentNodeRefStr NodeRef документа, в рамках типа которого проверять уникальность номера
	 * @param registrationYearOnly искать ли номер документа только в рамках одного года
	 * @return уникальный/не уникальный
	 */
	public boolean isNumberUnique(String number, String documentNodeRefStr, boolean registrationYearOnly) {
		boolean result;
		NodeRef documentNodeRef = new NodeRef(documentNodeRefStr);
		QName documentType = nodeService.getType(documentNodeRef);
		if (registrationYearOnly) {
			Date regDate = (Date) nodeService.getProperty(documentNodeRef, DocumentService.PROP_REG_DATA_DOC_DATE);
			if (regDate == null) {
				regDate = new Date();
			}
			result = regNumbersService.isNumberUnique(number, documentType, regDate);
		} else {
			result = regNumbersService.isNumberUnique(number, documentType);
		}

		return result;
	}

	/**
	 * Проверить, является ли номер документа уникальным в рамках определенного типа документа и, опционально, года.
	 * Год определяется по дате регистрации документа.
	 *
	 * @param number номер документа, который необходимо проверить на
	 * уникальность.
	 * @param documentNodeRef NodeRef документа, в рамках типа которого проверять уникальность номера
	 * @param registrationYearOnly искать ли номер документа только в рамках одного года
	 * @return уникальный/не уникальный
	 */
	public boolean isNumberUnique(String number, ScriptNode documentNodeRef, boolean registrationYearOnly) {
		return isNumberUnique(number, documentNodeRef.getNodeRef().toString(), registrationYearOnly);
	}

	/**
	 * Получить регистрационный номер для документа по указанному шаблону и
	 * записать его в указанный атрибут документа.
	 *
	 * @param documentNode ссылка на экземпляр документа, которому необходимо
	 * присвоить номер.
	 * @param documentProperty атрибут документа в префиксальной форме
	 * (prefix:property), в который необходимо записать сгенерированный номер.
	 * @param templateStr шаблон номера документа в виде строки либо NodeRef
	 * шаблон номера (объект типа lecm-regnum:template) в виде строки
	 * @throws WebScriptException в случае неудачной генерации номера.
	 * @deprecated use registerProject, registerDocument instead
	 */
	@Deprecated
	public void setDocumentNumber(ScriptNode documentNode, String documentProperty, String templateStr) {
		try {
			if (NodeRef.isNodeRef(templateStr)) {
				regNumbersService.setDocumentNumber(documentNode.getNodeRef(), documentProperty, new NodeRef(templateStr));
			} else {
				regNumbersService.setDocumentNumber(documentNode.getNodeRef(), documentProperty, templateStr);
			}
		} catch (TemplateParseException ex) {
			throw new WebScriptException(String.format("Error parsing registration number template '%s'", templateStr), ex);
		} catch (TemplateRunException ex) {
			throw new WebScriptException(String.format("Error running registration number template '%s'", templateStr), ex);
		}
	}

	/**
	 * Получить регистрационный номер для документа по указанному шаблону и
	 * записать его в указанный атрибут документа.
	 *
	 * @param documentNode ссылка на экземпляр документа, которому необходимо
	 * присвоить номер.
	 * @param documentProperty атрибут документа в префиксальной форме
	 * (prefix:property), в который необходимо записать сгенерированный номер.
	 * @param templateNode ссылка на шаблон номера (объект типа
	 * lecm-regnum:template)
	 * @throws WebScriptException в случае неудачной генерации номера.
	 * @deprecated use registerProject, registerDocument instead
	 */
	@Deprecated
	public void setDocumentNumber(ScriptNode documentNode, String documentProperty, ScriptNode templateNode) {
		try {
			regNumbersService.setDocumentNumber(documentNode.getNodeRef(), documentProperty, templateNode.getNodeRef());
		} catch (TemplateParseException ex) {
			throw new WebScriptException(String.format("Error parsing registration number template '%s'", templateNode), ex);
		} catch (TemplateRunException ex) {
			throw new WebScriptException(String.format("Error running registration number template '%s'", templateNode), ex);
		}
	}

	/**
	 * Получить регистрационный номер для документа по указанному шаблону и
	 * записать его в указанный атрибут документа.
	 *
	 * @param documentNode ссылка на экземпляр документа, которому необходимо
	 * присвоить номер.
	 * @param documentProperty атрибут документа в префиксальной форме
	 * (prefix:property), в который необходимо записать сгенерированный номер.
	 * @param dictionaryTemplateCode код элемента справочника с шаблоном номера
	 * @throws WebScriptException в случае неудачной генерации номера.
	 * @deprecated use registerProject, registerDocument instead
	 */
	@Deprecated
	public void setDocumentNumber(String dictionaryTemplateCode, ScriptNode documentNode, String documentProperty) {
		try {
			regNumbersService.setDocumentNumber(dictionaryTemplateCode, documentNode.getNodeRef(), documentProperty);
		} catch (TemplateParseException ex) {
			throw new WebScriptException(String.format("Error parsing registration number template code '%s'", dictionaryTemplateCode), ex);
		} catch (TemplateRunException ex) {
			throw new WebScriptException(String.format("Error running registration number template code '%s'", dictionaryTemplateCode), ex);
		}
	}

	/**
	 * Получить регистрационный номер для документа по указанному шаблону и
	 * записать его как номер проекта
	 *
	 *
	 * @param documentNode ссылка на экземпляр документа, которому необходимо
	 * присвоить номер.
	 * @param dictionaryTemplateCode код элемента справочника с шаблоном номера
	 * @param onlyReserve флаг нужно ли реально регистрировать документ
	 * @throws WebScriptException в случае неудачной генерации номера.
	 */
	public void registerProject(ScriptNode documentNode, String dictionaryTemplateCode, boolean onlyReserve) {
		try {
			regNumbersService.registerProject(documentNode.getNodeRef(), dictionaryTemplateCode, onlyReserve);
		} catch (TemplateParseException ex) {
			throw new WebScriptException(String.format("Error parsing registration number template code '%s'", dictionaryTemplateCode), ex);
		} catch (TemplateRunException ex) {
			throw new WebScriptException(String.format("Error running registration number template code '%s'", dictionaryTemplateCode), ex);
		}
	}

	/**
	 * Получить регистрационный номер для документа по указанному шаблону и
	 * записать его как номер документа
	 *
	 *
	 * @param documentNode ссылка на экземпляр документа, которому необходимо
	 * присвоить номер.
	 * @param dictionaryTemplateCode код элемента справочника с шаблоном номера
	 * @param onlyReserve флаг нужно ли реально регистрировать документ
	 * @throws WebScriptException в случае неудачной генерации номера.
	 */
	public void registerDocument(ScriptNode documentNode, String dictionaryTemplateCode, boolean onlyReserve) {
		try {
			regNumbersService.registerDocument(documentNode.getNodeRef(), dictionaryTemplateCode, onlyReserve);
		} catch (TemplateParseException ex) {
			throw new WebScriptException(String.format("Error parsing registration number template code '%s'", dictionaryTemplateCode), ex);
		} catch (TemplateRunException ex) {
			throw new WebScriptException(String.format("Error running registration number template code '%s'", dictionaryTemplateCode), ex);
		}
	}

	public void registerDocument(ScriptNode documentNode, String templateStr) {
		try {
			if (NodeRef.isNodeRef(templateStr)) {
				regNumbersService.registerDocument(documentNode.getNodeRef(), new NodeRef(templateStr));
			} else {
				regNumbersService.registerDocument(documentNode.getNodeRef(), templateStr);
			}
		} catch (TemplateParseException ex) {
			throw new WebScriptException(String.format("Error parsing registration number template '%s'", templateStr), ex);
		} catch (TemplateRunException ex) {
			throw new WebScriptException(String.format("Error running registration number template '%s'", templateStr), ex);
		}
	}

	public void registerProject(ScriptNode documentNode, String templateStr) {
		try {
			if (NodeRef.isNodeRef(templateStr)) {
				regNumbersService.registerProject(new NodeRef(templateStr), documentNode.getNodeRef());
			} else {
				regNumbersService.registerProject(documentNode.getNodeRef(), templateStr);
			}
		} catch (TemplateParseException ex) {
			throw new WebScriptException(String.format("Error parsing registration number template '%s'", templateStr), ex);
		} catch (TemplateRunException ex) {
			throw new WebScriptException(String.format("Error running registration number template '%s'", templateStr), ex);
		}
	}

	/**
	 * Проверить, зарегистрирован ли документ
	 *
	 * @param documentNode ссылка на экземпляр документа, который необходимо проверить на регистрацию.
	 * @return зарегистрирован/не зарегистрирован
	 */
	public boolean isDocumentRegistered(ScriptNode documentNode) {
		return regNumbersService.isRegistered(documentNode.getNodeRef(), false);
	}

	/**
	 * Проверить, зарегистрирован ли проект документа
	 *
	 * @param documentNode ссылка на экземпляр документа, который необходимо проверить на регистрацию.
	 * @return зарегистрирован/не зарегистрирован
	 */
	public boolean isProjectRegistered(ScriptNode documentNode) {
		return regNumbersService.isRegistered(documentNode.getNodeRef(), true);
	}
}
