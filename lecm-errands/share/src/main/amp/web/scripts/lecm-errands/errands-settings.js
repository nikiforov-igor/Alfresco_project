/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

/**
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module
 */
LogicECM.module = LogicECM.module || {};

(function() {
	var Dom = YAHOO.util.Dom;

	LogicECM.module.ErrandsSettings = function(htmlId) {
		LogicECM.module.ErrandsSettings.superclass.constructor.call(this, "LogicECM.module.ErrandsSettings", htmlId, ["container", "json"]);

		YAHOO.Bubbling.on("beforeFormRuntimeInit", this.beforeFormInit, this);
		return this;
	};

	YAHOO.extend(LogicECM.module.ErrandsSettings, Alfresco.component.Base, {
		onReady: function () {
			this.loadSettings();
		},

		loadSettings: function() {
			Alfresco.util.Ajax.jsonGet({
				url: Alfresco.constants.PROXY_URI + "lecm/errands/getGlobalSettings",
				successCallback: {
					fn: function (response) {
						if (response.json && response.json.nodeRef) {
							this.loadForm(response.json.nodeRef);
						}
					},
					scope: this
				},
				failureMessage: this.msg("message.failure")
			});
		},

		loadForm: function(settingsNode) {
			Alfresco.util.Ajax.request({
				url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form",
				dataObj: {
					htmlid: "errands-settings-edit-form",
					itemKind:"node",
					itemId: settingsNode,
					mode: "edit",
					formUI: true,
					submitType:"json",
					showSubmitButton:"true",
					showCaption: false
				},
				successCallback: {
					scope: this,
					fn: function (response) {
						Dom.get(this.id + "-settings").innerHTML = response.serverResponse.responseText;
						Dom.get("errands-settings-edit-form-form-submit").value = this.msg("label.save");
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