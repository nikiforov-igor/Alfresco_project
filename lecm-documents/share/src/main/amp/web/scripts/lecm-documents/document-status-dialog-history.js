if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.DocumentStatusHistory = LogicECM.module.DocumentStatusHistory || {};

(function () {
	var showStatusDialog = false;

	/**
	 * YUI Library aliases
	 */
	var Dom = YAHOO.util.Dom,
		Bubbling = YAHOO.Bubbling;

	LogicECM.module.DocumentStatusHistory = function (fieldHtmlId) {
		LogicECM.module.DocumentStatusHistory.superclass.constructor.call(this, "LogicECM.module.DocumentStatusHistory", fieldHtmlId);
		Bubbling.on("onSearchSuccess", this.onSearchSuccess, this);
		return this;
	};

	LogicECM.module.DocumentStatusHistory.showDialog = function (formId, nodeRef) {
		var id = Alfresco.util.generateDomId();
		var htmlid = formId + id;
		Alfresco.util.Ajax.request({
			url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/document/history-datagrid",
			dataObj: {
				nodeRef: nodeRef,
				htmlid: htmlid,
				dataSource: "lecm/business-journal/ds/getStatusHistory"
			},
			successCallback: {
				fn: function (response) {
					showStatusDialog = true;
					var text = response.serverResponse.responseText;
					var formEl = Dom.get(formId + "-panel-content");
					formEl.innerHTML = text;
				},
				scope: this
			},
			failureMessage: function () {
				alert("Данные не загружены");
			},
			scope: this,
			execScripts: true
		});
	};

	YAHOO.extend(LogicECM.module.DocumentStatusHistory, Alfresco.component.Base,
		{
			viewStatusDialog: null,
			panelId: null,

			onReady: function () {
				this.panelId = this.id + "-panel";
				this.viewStatusDialog = Alfresco.util.createYUIPanel(this.panelId,
					{
						width: "50em"
					});
				this.viewStatusDialog.hideEvent.subscribe(this.hideViewStatusDialog, null, this);
				Alfresco.util.createYUIButton(this, "panel-cancel", this.hideViewStatusDialog);
				Dom.setStyle(this.panelId, "display", "none");
			},

			onSearchSuccess: function () {
				if (showStatusDialog) {
					showStatusDialog = false;
					if (this.viewStatusDialog != null) {
						Dom.setStyle(this.panelId, "display", "block");
						this.viewStatusDialog.show();
					}
				}
			},

			hideViewStatusDialog: function () {
				if (this.viewStatusDialog != null) {
					this.viewStatusDialog.hide();
					Dom.setStyle(this.panelId, "display", "none");
				}
			}
		});
})();
