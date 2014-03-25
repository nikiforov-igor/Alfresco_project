if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Signing = LogicECM.module.Signing || {};

(function() {

	LogicECM.module.Signing.SigningListDatagridControl = function(containerId, documentNodeRef) {

		this.documentNodeRef = documentNodeRef;

		YAHOO.util.Event.onContentReady(containerId, this.renewDatagrid, this, true);

		return LogicECM.module.Signing.SigningListDatagridControl.superclass.constructor.call(this, containerId);
	};

	YAHOO.lang.extend(LogicECM.module.Signing.SigningListDatagridControl, LogicECM.module.Base.DataGrid);

	YAHOO.lang.augmentObject(LogicECM.module.Signing.SigningListDatagridControl.prototype, {
		signingItemType: null,
		signingListRef: null,
		renewDatagrid: function() {
			Alfresco.util.Ajax.request({
				method: "GET",
				url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/workflow/signing/GetSigningListDataForDocument',
				dataObj: {
					documentNodeRef: this.documentNodeRef
				},
				successCallback: {
					scope: this,
					fn: function(response) {
						if (response) {
							this.signingItemType = response.json.signingItemType;
							this.signingListRef = response.json.signingListRef;
							if (this.signingListRef) {
								YAHOO.Bubbling.fire("activeGridChanged", {
									datagridMeta: {
										itemType: this.signingItemType,
										nodeRef: this.signingListRef,
										datagridFormId: this.options.datagridFormId,
										sort: 'lecm-workflow:assignee-order|true'
									},
									bubblingLabel: "SigningListDatagridControl"
								});
							}
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
