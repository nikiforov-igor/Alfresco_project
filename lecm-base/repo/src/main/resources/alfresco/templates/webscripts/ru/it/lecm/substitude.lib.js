var parentSymbol = "..";
var splitTransitionsSymbol = "/";
var equalsSymbol = "=";
var splitExpressionSymbol = ",";
var openExpressionsSymbol = "(";
var closeExpressionsSymbol = ")";

function getSubstitudeField(node, field) {
	var showNode = node;
	var fieldName = null;
	var transitions = [];

	if (field.indexOf(splitTransitionsSymbol) != -1) {
		var firstIndex = field.indexOf(splitTransitionsSymbol);
		transitions.push(field.substring(0, firstIndex));
		var lastIndex = field.lastIndexOf(splitTransitionsSymbol);
		while (firstIndex != lastIndex) {
			var oldFirstIndex = firstIndex;
			firstIndex = field.indexOf(splitTransitionsSymbol, firstIndex + 1);
			transitions.push(field.substring(oldFirstIndex + 1, firstIndex));
		}
		fieldName = field.substring(lastIndex + 1, ("" + field).length);
	} else {
		fieldName = field;
	}

	for (var i = 0; i < transitions.length; i++) {
		var el = transitions[i];
		var expressions = getExpression(el);
		if (expressions.length > 0) {
			el = el.substring(0, el.indexOf(openExpressionsSymbol));
		}
		if (el.indexOf(parentSymbol) == 0) {
			var assocType = el.replace(parentSymbol, "");
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
				for (var i = 0; i < showNode.length; i++) {
					var node = showNode[i];
					var expressionsFalse = false;
					for (var j = 0; j < expressions.length; j++) {
						var expression = expressions[j];
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

	var openIndex = str.indexOf(openExpressionsSymbol);
	var closeIndex = str.indexOf(closeExpressionsSymbol);
	if (openIndex > -1 && closeIndex > -1) {
		var expressionsStr = str.substring(openIndex + 1, closeIndex);
		if (expressionsStr.length > 0 && expressionsStr.indexOf(equalsSymbol) > -1) {
			var equalsIndex = -1;
			var endIndex = -1;
			var lastEqualsIndex = expressionsStr.lastIndexOf(equalsSymbol);
			while (equalsIndex != lastEqualsIndex) {
				var oldEndIndex = endIndex;
				equalsIndex = expressionsStr.indexOf(equalsSymbol, equalsIndex + 1);
				endIndex = expressionsStr.indexOf(splitExpressionSymbol, endIndex + 1);
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