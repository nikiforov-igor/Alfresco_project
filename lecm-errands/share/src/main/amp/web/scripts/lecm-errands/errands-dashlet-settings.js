if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

(function () {
	LogicECM.ErrandsDashletSettings = function (htmlId) {
		LogicECM.ErrandsDashletSettings.superclass.constructor.call(this, "LogicECM.ErrandsDashletSettings", htmlId, ["container", "json"]);
		return this;
	};

	YAHOO.extend(LogicECM.ErrandsDashletSettings, Alfresco.component.Base,
		{
			onReady: function () {
				this.loadSettings();
			},

			loadSettings: function () {
				var me = this;
				Alfresco.util.Ajax.request(
					{
						url: Alfresco.constants.PROXY_URI + "lecm/errands/dashlet/settings",
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
							htmlid: "errands-dashlet-settings-edit-form",
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

								Dom.get("errands-dashlet-settings-edit-form-form-submit").value = me.msg("label.save");

								var form = new Alfresco.forms.Form("errands-dashlet-settings-edit-form-form");
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
                    Alfresco.util.PopupManager.displayMessage(
                        {
                            text: Alfresco.util.message("message.save.success")
                        });
				} else {
					Alfresco.util.PopupManager.displayPrompt(
						{
							text: Alfresco.util.message("message.failure")
						});
				}
			}
		});
})();