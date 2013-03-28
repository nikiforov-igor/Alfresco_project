package ru.it.lecm.regnumbers.extension;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.springframework.extensions.webscripts.WebScriptException;
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

	public void setRegNumbersService(RegNumbersService regNumbersService) {
		this.regNumbersService = regNumbersService;
	}

	/**
	 * Сгенерировать номер документа до данному шаблону номера.
	 * При ошибке с шаблоне генерирует исключение WebScriptException
	 *
	 * @param documentNode ссылка на экземпляр документа, для которого
	 * необходимо сгенерировать номер.
	 * @param templateNode ссылка на шаблон номера (объект типа
	 * lecm-regnum:template)
	 * @return сгененриванный номер документа.
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
	 * @return сгененриванный номер документа.
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
	 * Проверить, является ли номер документа уникальным
	 *
	 * @param number номер документа, который необходимо проверить на
	 * уникальность.
	 * @return уникальный/не уникальный
	 */
	public boolean isNumberUnique(String number) {
		return regNumbersService.isNumberUnique(number);
	}
}
