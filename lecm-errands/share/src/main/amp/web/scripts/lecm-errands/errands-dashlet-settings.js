if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

(function () {
	LogicECM.ErrandsDashletSettings = function (htmlId) {
		LogicECM.ErrandsDashletSettings.superclass.constructor.call(this, "LogicECM.ErrandsDashletSettings", htmlId, ["container", "json"]);

		YAHOO.Bubbling.on("beforeFormRuntimeInit", this.beforeFormInit, this);
		return this;
	};

	YAHOO.extend(LogicECM.ErrandsDashletSettings, Alfresco.component.Base,{
		onReady: function () {
			this.loadSettings();
		},

		loadSettings: function () {
			var me = this;
			Alfresco.util.Ajax.request({
				url: Alfresco.constants.PROXY_URI + "lecm/errands/dashlet/settings",
				successCallback: {
					fn: function (response) {
						var oResults = response.serverResponse.responseText;
						if ((oResults != null) && (oResults != "")) {
							me.loadForm(oResults);
						}
					}
				},
				failureMessage: "message.failure"
			});
		},

		loadForm: function (settingsNode) {
			Alfresco.util.Ajax.request({
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
					scope: this,
					fn: function (response) {
						Dom.get(this.id + "-settings").innerHTML = response.serverResponse.responseText;
						Dom.get("errands-dashlet-settings-edit-form-form-submit").value = this.msg("label.save");
					}
				},
				failureMessage: "message.failure",
				execScripts: true
			});
		},

		beforeFormInit: function (layer, args) {
			YAHOO.Bubbling.unsubscribe("beforeFormRuntimeInit", this.beforeFormInit);
			var form = args[1].runtime;
			form.setSubmitAsJSON(true);
			form.setAJAXSubmit(true, {
				successCallback: {
					scope: this,
					fn: this.onSuccess
				}
			});
		},

		onSuccess: function (response) {
			if (response && response.json) {
				Alfresco.util.PopupManager.displayMessage({
					text: Alfresco.util.message("message.save.success")
				});
			} else {
				Alfresco.util.PopupManager.displayPrompt({
					text: Alfresco.util.message("message.failure")
				});
			}
		}
	});
})();