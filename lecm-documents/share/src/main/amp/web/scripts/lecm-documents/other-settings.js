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

    LogicECM.module.MiscellaneousSettings = function(htmlId) {
        LogicECM.module.MiscellaneousSettings.superclass.constructor.call(this, "LogicECM.module.MiscellaneousSettings", htmlId, ["container", "json"]);
		return this;
	};

	YAHOO.extend(LogicECM.module.MiscellaneousSettings, Alfresco.component.Base, {
		onReady: function () {
			this.loadSettings();
		},

		loadSettings: function() {
			Alfresco.util.Ajax.request({
				method: "GET",
				url: Alfresco.constants.PROXY_URI + "lecm/documents/global-settings/api/getSettingsNode",
				successCallback: {
					scope: this,
					fn: function (response) {
						if (response && response.json && response.json.nodeRef) {
							this.loadForm(response.json.nodeRef);
						}
					}
				},
				failureMessage: this.msg("message.failure")
			});
		},

		loadForm: function(settingsNode) {
			var successCallback = function(response) {
				var container = Dom.get(this.id + "-settings"),
                    markupAndScripts = Alfresco.util.Ajax.sanitizeMarkup(response.serverResponse.responseText),
                    markup = markupAndScripts[0],
                    scripts = markupAndScripts[1];
                container.innerHTML = markup;
                // Run the js code from the webscript's <script> elements
                setTimeout(scripts, 0);
				
				Dom.get("documents-global-settings-edit-form-form-submit").value = this.msg("label.save");

				var form = new Alfresco.forms.Form("documents-global-settings-edit-form-form");
				form.setSubmitAsJSON(true);
				form.setAJAXSubmit(true, {
					successCallback: {
                        scope: this,
						fn: this.onSuccess
					}
				});
				form.init();
			};
			Alfresco.util.Ajax.request({
				method: "GET",
				url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form",
				dataObj: {
					htmlid: "documents-global-settings-edit-form",
					itemKind:"node",
					itemId: settingsNode,
					mode: "edit",
					formUI: true,
					submitType:"json",
					showSubmitButton:"true"
				},
				successCallback: {
					scope: this,
					fn: successCallback
				},
				failureMessage: this.msg("message.failure"),
				execScripts: true
			});
		},

		onSuccess: function (response) {
			YAHOO.Bubbling.fire("formSubmit", this);
			
			if (response && response.json) {
				Alfresco.util.PopupManager.displayMessage({
					text: this.msg("message.save.success")
				});
			} else {
				Alfresco.util.PopupManager.displayPrompt({
					text: this.msg("message.failure")
				});
			}
		}
	});
})();