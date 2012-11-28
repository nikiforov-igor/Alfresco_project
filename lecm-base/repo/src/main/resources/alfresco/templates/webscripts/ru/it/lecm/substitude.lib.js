const PARENT_SYMBOL = "..";
const SPLIT_TRANSITIONS_SYMBOL = "/";
const EQUALS_SYMBOL = "=";
const SPLIT_EXPRESSION_SYMBOL = ",";
const OPEN_EXPRESSIONS_SYMBOL = "(";
const CLOSE_EXPRESSIONS_SYMBOL = ")";
const OPEN_SUBSTITUDE_SYMBOL = "{";
const CLOSE_SUBSTITUDE_SYMBOL = "}";

function formatNodeTitle(node, formatString) {
	var result = formatString;
	var nameParams = splitSubstitudeFieldsString(formatString, OPEN_SUBSTITUDE_SYMBOL, CLOSE_SUBSTITUDE_SYMBOL);
	for each(var field in nameParams) {
		result = result.replace(OPEN_SUBSTITUDE_SYMBOL + field + CLOSE_SUBSTITUDE_SYMBOL, getSubstitudeField(node, field));
	}
	return result;
}

function getSubstitudeField(node, field) {
	var showNode = node;
	var fieldName = null;
	var transitions = [];

	if (field.indexOf(SPLIT_TRANSITIONS_SYMBOL) != -1) {
		var firstIndex = field.indexOf(SPLIT_TRANSITIONS_SYMBOL);
		transitions.push(field.substring(0, firstIndex));
		var lastIndex = field.lastIndexOf(SPLIT_TRANSITIONS_SYMBOL);
		while (firstIndex != lastIndex) {
			var oldFirstIndex = firstIndex;
			firstIndex = field.indexOf(SPLIT_TRANSITIONS_SYMBOL, firstIndex + 1);
			transitions.push(field.substring(oldFirstIndex + 1, firstIndex));
		}
		fieldName = field.substring(lastIndex + 1, ("" + field).length);
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
			if (("" + assocType).length > 0) {
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

function trim(str) {
	return str.replace(/^\s+|\s+$/g, "");
}