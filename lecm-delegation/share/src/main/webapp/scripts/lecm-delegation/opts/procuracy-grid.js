if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.Delegation = LogicECM.module.Delegation || {};

LogicECM.module.Delegation.Procuracy = LogicECM.module.Delegation.Procuracy || {};

(function () {

	LogicECM.module.Delegation.Procuracy.Grid = function (containerId) {
		return LogicECM.module.Delegation.Procuracy.Grid.superclass.constructor.call(this, containerId);
	};

	/**
	 * Extend from LogicECM.module.Base.DataGrid
	 */
	YAHOO.lang.extend (LogicECM.module.Delegation.Procuracy.Grid, LogicECM.module.Base.DataGrid);

    /**
     * Augment prototype with main class implementation, ensuring overwrite is enabled
     */
	YAHOO.lang.augmentObject (LogicECM.module.Delegation.Procuracy.Grid.prototype, {

		onDelete: function (p_items, owner, actionsConfig, fnDeleteComplete, fnPrompt){
			var items = YAHOO.lang.isArray(p_items) ? p_items : [p_items];
			var nodeRefs = [];
			for (var i = 0; i < items.length; ++i) {
				nodeRefs.push ({"nodeRef": items[i].nodeRef});
			}
			var scope = this;
			Alfresco.util.Ajax.request ({
				method: "POST",
				url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/delegation/del/procuracies",
				dataObj: nodeRefs,
				requestContentType: "application/json",
				responseContentType: "application/json",
//				successMessage: "ololo!",
				successCallback: {
					fn: function (response) {
						YAHOO.Bubbling.fire("datagridRefresh", {
							bubblingLabel: scope.options.bubblingLabel
						});
					},
					scope: this
				},
				failureMessage: "не удалось удалить доверенность"
			});
		}
	}, true);

})();
