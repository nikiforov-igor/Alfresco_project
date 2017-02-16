if (typeof LogicECM === "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Nomenclature = LogicECM.module.Nomenclature || {};

(function() {

	LogicECM.module.Nomenclature.Tree = function(htmlId) {
		return LogicECM.module.Nomenclature.Tree.superclass.constructor.call(this, htmlId);
	};

	YAHOO.lang.extend(LogicECM.module.Nomenclature.Tree, LogicECM.module.Dictionary.Tree);

	YAHOO.lang.augmentObject(LogicECM.module.Nomenclature.Tree.prototype, {
		_loadRootNode: function() {
			Alfresco.util.Ajax.jsonGet({
				url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/os/nomenclature/getNomenclatureFolder',
				successCallback: {
					scope: this,
					fn: function(response) {
						if (response) {
							this.rootNode = response.json;
							this.draw();
						}
					}
				},
				failureMessage: this.msg("message.failure"),
				execScripts: true
			});
		},

		_treeNodeSelected:function (node) {
            this.selectedNode = node;
			YAHOO.Bubbling.fire("activeGridChanged",
				{
					datagridMeta: {
						itemType: node.data.childType,
						currentItemType: node.data.type,
						recreate: true,
						sort: "os-aspects:sort-value",
						nodeRef: node.data.nodeRef
					},
				scrollTo: true
				});
			YAHOO.Bubbling.fire("hideFilteredLabel");
			},
	}, true);
})();
