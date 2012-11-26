var parentSymbol = "..";
var splitSymbol = "/";

function getSubstitudeField(node, field) {
	var showNode = node;
	var fieldName = null;
	var transitions = [];

	if (field.indexOf(splitSymbol) != -1) {
		var firstIndex = field.indexOf(splitSymbol);
		transitions.push(field.substring(0, firstIndex));
		var lastIndex = field.lastIndexOf(splitSymbol);
		while (firstIndex != lastIndex) {
			var oldFirstIndex = firstIndex;
			var firstIndex = field.indexOf(splitSymbol, firstIndex + 1);
			transitions.push(field.substring(oldFirstIndex + 1, firstIndex));
		}
		fieldName = field.substring(lastIndex + 1, ("" + field).length);
	} else {
		fieldName = field;
	}

	for (var i = 0; i < transitions.length; i++) {
		var el = transitions[i];
		if (el.indexOf(parentSymbol) == 0) {
			var assocType = el.replace(parentSymbol, "");
			if (("" + assocType).length > 0) {
				showNode = showNode.sourceAssocs[assocType];
				if (showNode != null) {
					showNode = showNode[0];
				} else {
					break;
				}
			} else {
				showNode = showNode.parent;
			}
		} else {
			var temp = showNode.getAssocs()[el];
			if (temp == null) {
				showNode = showNode.getChildAssocs()[el];
				if (showNode != null) {
					showNode = showNode[0];
				} else {
					break;
				}
			} else {
				showNode = temp[0];
			}
		}
	}

	var result = "";
	if (showNode != null) {
		result = showNode.properties[fieldName];
	}

	return result;
}

function splitSubstitudeFieldsString(string, openSymbol, closeSymbol) {
	var result = [];
	if (string.indexOf(openSymbol) != -1 && string.indexOf(closeSymbol) != -1) {
		var openIndex = string.indexOf(openSymbol);
		var closeIndex = string.indexOf(closeSymbol);
		result.push(string.substring(openIndex + 1, closeIndex));
		var lastOpenIndex = string.lastIndexOf(openSymbol);
		var lastCloseIndex = string.lastIndexOf(closeSymbol);
		while (openIndex != lastOpenIndex && closeIndex != lastCloseIndex) {
			var openIndex = string.indexOf(openSymbol, openIndex + 1);
			var closeIndex = string.indexOf(closeSymbol, closeIndex + 1);
			result.push(string.substring(openIndex + 1, closeIndex));
		}
	}
	return result;
}