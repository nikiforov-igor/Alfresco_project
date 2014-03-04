if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Signing = LogicECM.module.Signing || {};

(function() {

	LogicECM.module.Signing.SigningListDatagridControl = function(containerId, documentNodeRef) {
		var me = this;

		Alfresco.util.Ajax.request({
			method: "GET",
			url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/workflow/signing/GetSigningListDataForDocument',
			dataObj: {
				documentNodeRef: documentNodeRef
			},
			successCallback: {
				fn: function(response) {
					if (response) {
						me.signingItemType = response.json.signingItemType;
						me.signingListRef = response.json.signingListRef;
						if (me.signingListRef) {
							YAHOO.util.Event.onContentReady(containerId, function() {
								YAHOO.Bubbling.fire("activeGridChanged", {
									datagridMeta: {
										itemType: me.signingItemType,
										nodeRef: me.signingListRef,
										sort: 'lecm-workflow-result:workflow-result-list-complete-date|true',
										searchConfig: {
											filter: ''
										}
									},
									bubblingLabel: "SigningListDatagridControl"
								});
							});
						}
					}
				}
			},
			failureMessage: "message.failure",
			execScripts: true,
			scope: this
		});

		return LogicECM.module.Signing.SigningListDatagridControl.superclass.constructor.call(this, containerId);
	};

	YAHOO.lang.extend(LogicECM.module.Signing.SigningListDatagridControl, LogicECM.module.Base.DataGrid);

	YAHOO.lang.augmentObject(LogicECM.module.Signing.SigningListDatagridControl.prototype, {});
})();