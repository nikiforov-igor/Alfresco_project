if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.Delegation = LogicECM.module.Delegation || {};

LogicECM.module.Delegation.DelegationList = LogicECM.module.Delegation.DelegationList || {};

(function () {
	"use strict";
	LogicECM.module.Delegation.DelegationList.Grid = function (containerId) {
		return LogicECM.module.Delegation.DelegationList.Grid.superclass.constructor.call(this, containerId);
	};

	/**
	 * Extend from LogicECM.module.Base.DataGrid
	 */
	YAHOO.lang.extend (LogicECM.module.Delegation.DelegationList.Grid, LogicECM.module.Base.DataGrid);

    /**
     * Augment prototype with main class implementation, ensuring overwrite is enabled
     */
	YAHOO.lang.augmentObject (LogicECM.module.Delegation.DelegationList.Grid.prototype, {

		onActionEdit: function (item) {
			var baseUrl = window.location.protocol + "//" + window.location.host;
			//delegator - доверенное лицо, тот кто создает доверенность
			var template = "delegation-opts?delegator={delegator}";
			var url = YAHOO.lang.substitute (baseUrl + Alfresco.constants.URL_PAGECONTEXT + template, {
				delegator: item.nodeRef // доверенное лицо, тот кто создает доверенность
			});
			window.location.href = url;
		},

		onExpand: function(record) {
			var nodeRef = record.getData("nodeRef");
			Alfresco.util.Ajax.request({
				method: "GET",
				url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/delegation/procuraciesDatagrid",
				dataObj: {
					nodeRef: nodeRef,
					itemType: this.options.procuracyItemType,
					filter: ' AND @lecm\-dic\:active:true'
				},
				successCallback: {
					scope: this,
					fn: function(response) {
						if (response.serverResponse) {
							this.addExpandedRow(record, response.serverResponse.responseText);
						}
					}
				},
				failureMessage: "message.failure",
				execScripts: true,
				scope: this
			});
		}
	}, true);
})();
