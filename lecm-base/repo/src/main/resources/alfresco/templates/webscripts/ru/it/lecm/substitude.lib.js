/**
 * Символ ссылки на родителя
 * @type {String}
 */
const PARENT_SYMBOL = "..";

/**
 * Разделитель элементов в выражении
 * @type {String}
 */
const SPLIT_TRANSITIONS_SYMBOL = "/";

/**
 * Символ эквивалентности в условиях
 * @type {String}
 */
const EQUALS_SYMBOL = "=";

/**
 * Разделитель условий
 * @type {String}
 */
const SPLIT_EXPRESSION_SYMBOL = ",";

/**
 * Символ открытия условия
 * @type {String}
 */
const OPEN_EXPRESSIONS_SYMBOL = "(";

/**
 * Символ закрытия условий
 * @type {String}
 */
const CLOSE_EXPRESSIONS_SYMBOL = ")";

/**
 * Символ открытия выражения
 * @type {String}
 */
const OPEN_SUBSTITUDE_SYMBOL = "{";

/**
 * Символ закрытия выражения
 * @type {String}
 */
const CLOSE_SUBSTITUDE_SYMBOL = "}";

/**
 * Получение заголовка элемента в соответствии с форматной строкой.
 * Выражения в форматной строке должны быть заключены в символы открытия (@see OPEN_SUBSTITUDE_SYMBOL) и закрытия (@see CLOSE_SUBSTITUDE_SYMBOL)
 *
 * @param node ScriptNode элемент
 * @param formatString форматная строка
 * @return Заголовок элемента
 */
function formatNodeTitle(node, formatString) {
	var result = formatString;
	var nameParams = splitSubstitudeFieldsString(formatString, OPEN_SUBSTITUDE_SYMBOL, CLOSE_SUBSTITUDE_SYMBOL);
	for each(var field in nameParams) {
		result = result.replace(OPEN_SUBSTITUDE_SYMBOL + field + CLOSE_SUBSTITUDE_SYMBOL, getSubstitudeField(node, field));
	}
	return result;
}

/**
 * Получение значения выражения для элемента.
 * Элементы в выражениях разделяются специальными символами (@see SPLIT_TRANSITIONS_SYMBOL)
 * Элементами выражения могут быть:
 *      - Ссылка на родителя (@see SPLIT_TRANSITIONS_SYMBOL)
 *      - Source ассоциация (..<Название ассоциации>)
 *      - Target ассоциация (<Название ассоциации>)
 *      -Child ассоциация (<Название ассоциации>)
 * Последним элементов выражения обязательно должен быть атрибут элемента
 *
 * Для ассоциаций можно указать условия.
 * Условия должно быть написано сразу после ассоциации, начиная с символа открытия (@see OPEN_EXPRESSIONS_SYMBOL) и заканчивая символом закрытия(@see OPEN_EXPRESSIONS_SYMBOL).
 * Условия должно содержать название атрибута и его значения, через знак равенства (@see EQUALS_SYMBOL).
 * Условий может быть несколько, в этом случае они должны разделяться специальным символом (@see SPLIT_EXPRESSION_SYMBOL).
 *
 * @param node ScriptNode элемент
 * @param field выражение для элемента (ассоциации, условия и атрибуты)
 * @return {String}
 */
function getSubstitudeField(node, field) {
	var showNode = node;
	var fieldName = null;
	var transitions = [];
	field = "" + field; //хак, приведение типа

	if (field.indexOf(SPLIT_TRANSITIONS_SYMBOL) != -1) {
		var firstIndex = field.indexOf(SPLIT_TRANSITIONS_SYMBOL);
		transitions.push(field.substring(0, firstIndex));
		var lastIndex = field.lastIndexOf(SPLIT_TRANSITIONS_SYMBOL);
		while (firstIndex != lastIndex) {
			var oldFirstIndex = firstIndex;
			firstIndex = field.indexOf(SPLIT_TRANSITIONS_SYMBOL, firstIndex + 1);
			transitions.push(field.substring(oldFirstIndex + 1, firstIndex));
		}
		fieldName = field.substring(lastIndex + 1, field.length);
	} else {
		fieldName = field;
	}

	for each(var el in transitions) {
		var expressions = getExpression(el);
		if (expressions.length > 0) {
			el = el.substring(0, el.indexOf(OPEN_EXPRESSIONS_SYMBOL));
		}
		if (el.indexOf(PARENT_SYMBOL) == 0) {
			var assocType = el.replace(PARENT_SYMBOL, "");
			if (assocType.length > 0) {
				showNode = showNode.sourceAssocs[assocType];
				if (showNode == null) {
					break;
				}
			} else {
				showNode = showNode.parent;
			}
		} else {
			var temp = showNode.getAssocs()[el];
			if (temp == null) {
				showNode = showNode.getChildAssocs()[el];
				if (showNode == null) {
					break;
				}
			} else {
				showNode = temp;
			}
		}
		if (showNode != null) {
			if (expressions.length > 0) {
				var exist = false;
				for each(var node in showNode) {
					var expressionsFalse = false;
					for each(var expression in expressions) {
						if (("" + node.properties[expression.field]) != expression.value) {
							expressionsFalse = true;
							break;
						}
					}
					if (!expressionsFalse) {
						showNode = node;
						exist = true;
						break;
					}
				}
				if (!exist) {
					showNode = null;
					break;
				}
			} else if (showNode.length > 0) {
				showNode = showNode[0];
			}
		}
	}

	var result = "";
	if (showNode != null ) {
		result = showNode.properties[fieldName];
		if (result != null) {
			result = trim(result);
		} else {
			result = "";
		}
	}

	return result;
}

/**
 * Получение выражений из форматной строки
 *
 * @param str форматная строка
 * @param openSymbol символ открытия выражения
 * @param closeSymbol символ закрытия выражения
 * @return {Array} список строк с выражениями
 */
function splitSubstitudeFieldsString(str, openSymbol, closeSymbol) {
	var result = [];
	if (str.indexOf(openSymbol) != -1 && str.indexOf(closeSymbol) != -1) {
		var openIndex = str.indexOf(openSymbol);
		var closeIndex = str.indexOf(closeSymbol);
		result.push(str.substring(openIndex + 1, closeIndex));
		var lastOpenIndex = str.lastIndexOf(openSymbol);
		var lastCloseIndex = str.lastIndexOf(closeSymbol);
		while (openIndex != lastOpenIndex && closeIndex != lastCloseIndex) {
			openIndex = str.indexOf(openSymbol, openIndex + 1);
			closeIndex = str.indexOf(closeSymbol, closeIndex + 1);
			result.push(str.substring(openIndex + 1, closeIndex));
		}
	}
	return result;
}

/**
 * Получение условий
 * @param str строка с условием
 * @return {Array} список строк с условиями
 */
function getExpression(str) {
	var expressions = [];

	var openIndex = str.indexOf(OPEN_EXPRESSIONS_SYMBOL);
	var closeIndex = str.indexOf(CLOSE_EXPRESSIONS_SYMBOL);
	if (openIndex > -1 && closeIndex > -1) {
		var expressionsStr = str.substring(openIndex + 1, closeIndex);
		if (expressionsStr.length > 0 && expressionsStr.indexOf(EQUALS_SYMBOL) > -1) {
			var equalsIndex = -1;
			var endIndex = -1;
			var lastEqualsIndex = expressionsStr.lastIndexOf(EQUALS_SYMBOL);
			while (equalsIndex != lastEqualsIndex) {
				var oldEndIndex = endIndex;
				equalsIndex = expressionsStr.indexOf(EQUALS_SYMBOL, equalsIndex + 1);
				endIndex = expressionsStr.indexOf(SPLIT_EXPRESSION_SYMBOL, endIndex + 1);
				if (endIndex == -1) {
					endIndex = expressionsStr.length;
				}
				var expression = {
					field: trim(expressionsStr.substring(oldEndIndex + 1, equalsIndex)),
					value: trim(expressionsStr.substring(equalsIndex + 1, endIndex))
				};
				expressions.push(expression);
			}
		}
	}
	return expressions;
}

/**
 * Обрезка пробелов на концах строки
 * @param str строка для обрезки пробелов
 * @return String строка без пробелов на концах
 */
function trim(str) {
	return str.replace(/^\s+|\s+$/g, "");
}
