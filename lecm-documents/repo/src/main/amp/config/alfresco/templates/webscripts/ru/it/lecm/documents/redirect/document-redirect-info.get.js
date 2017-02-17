(function() {

	function getDocumentAncestor(node, type) {
		var parent = node.getParent();
		if (parent) {
			if (parent.isSubType(type)) {
				return parent;
			} else {
				return getDocumentAncestor(parent, type);
			}
		}
		return null;
	}

	var nodeRef = args['nodeRef'];
	var node = search.findNode(nodeRef);
	var path = '' + node.qnamePath;
	var ancestor;
	//Проверить, входит ли отображаемый контент в папку \Business Platform\LECM
	if (path.indexOf('/app:company_home/cm:Business_x0020_platform/cm:LECM') >= 0) {
		model.redirect = true;
		model.type = 'SYSTEM_DOCUMENT';
		model.node = node;
		return;
	}
	//Проверить, является ли отображаемый контент аттачментом (или его рабочей копией) базового документа (где бы он ни находился).
	if (documentAttachments.isInnerAttachment(node) ||
		(node.hasAspect("cm:workingcopy") && node.assocs['cm:original'].length > 0 && documentAttachments.isInnerAttachment(node.assocs['cm:original'][0]))) {
		model.redirect = true;
		model.type = 'DOCUMENT_ATTACHMENT';
		model.node = node;
		return;
	}
	//Проверить, является ли отображаемый контент базовым документом
	if (node.isSubType('lecm-document:base')) {
		model.redirect = true;
		model.type = 'BASE_DOCUMENT';
		model.node = node;
		return;
	}
	//Проверить, является ли отображаемый контент любым его child-ом любого уровня
	ancestor = getDocumentAncestor(node, 'lecm-document:base');
	if (ancestor) {
		model.redirect = true;
		model.type = 'BASE_DOCUMENT';
		model.node = ancestor;
		return;
	}
	//для всего остального редирект не делаем
	model.redirect = false;
})();
