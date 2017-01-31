if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

(function () {
	LogicECM.DelegationGlobalSettings = function (htmlId) {
		LogicECM.DelegationGlobalSettings.superclass.constructor.call(this, "LogicECM.DelegationGlobalSettings", htmlId, ["container", "json"]);
		return this;
	};

	YAHOO.extend(LogicECM.DelegationGlobalSettings, Alfresco.component.Base,
		{
			onReady: function () {
				this.loadSettings();
			},

			loadSettings: function () {
				var me = this;
				Alfresco.util.Ajax.request(
					{
						url: Alfresco.constants.PROXY_URI + "lecm/delegation/getGlobalSettings",
						successCallback: {
							fn: function (response) {
								var oResults = eval("(" + response.serverResponse.responseText + ")");
								if (oResults != null && oResults.nodeRef != null) {
									me.loadForm(oResults.nodeRef);
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
							htmlid: "delegation-settings-edit-form",
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

								Dom.get("delegation-settings-edit-form-form-submit").value = me.msg("label.save");

								var form = new Alfresco.forms.Form("delegation-settings-edit-form-form");
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