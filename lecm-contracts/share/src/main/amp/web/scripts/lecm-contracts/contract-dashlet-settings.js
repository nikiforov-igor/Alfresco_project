if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

(function () {
	LogicECM.ContractDashletSettings = function (htmlId) {
		LogicECM.ContractDashletSettings.superclass.constructor.call(this, "LogicECM.ContractDashletSettings", htmlId, ["container", "json"]);
		return this;
	};

	YAHOO.extend(LogicECM.ContractDashletSettings, Alfresco.component.Base,
		{
			onReady: function () {
				this.loadSettings();
			},

			loadSettings: function () {
				var me = this;
				Alfresco.util.Ajax.request(
					{
						url: Alfresco.constants.PROXY_URI + "/lecm/contracts/dashlet/settings",
						successCallback: {
							fn: function (response) {
								var oResults = response.serverResponse.responseText;
								if (oResults != null && oResults != "") {
									me.loadForm(oResults);
								}
							}
						},
						failureMessage: "message.failure"
					});
			},

			loadForm: function (settingsNode) {
				var me = this;
				Alfresco.util.Ajax.request(
					{
						url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form",
						dataObj: {
							htmlid: "contract-dashlet-settings-edit-form",
							itemKind: "node",
							itemId: settingsNode,
							mode: "edit",
							formUI: true,
							submitType: "json",
							showSubmitButton: "true",
							showCaption: false
						},
						successCallback: {
							fn: function (response) {
								var container = Dom.get(me.id + "-settings");
								container.innerHTML = response.serverResponse.responseText;

								Dom.get("contract-dashlet-settings-edit-form-form-submit").value = me.msg("label.save");

								var form = new Alfresco.forms.Form("contract-dashlet-settings-edit-form-form");
								form.setSubmitAsJSON(true);
								form.setAJAXSubmit(true,
									{
										successCallback: {
											fn: me.onSuccess,
											scope: this
										}
									});
								form.init();
							}
						},
						failureMessage: "message.failure",
						execScripts: true
					});
			},

			onSuccess: function (response) {
				if (response && response.json) {
					window.location.reload(true);
				} else {
					Alfresco.util.PopupManager.displayPrompt(
						{
							text: Alfresco.util.message("message.failure")
						});
				}
			}
		});
})();