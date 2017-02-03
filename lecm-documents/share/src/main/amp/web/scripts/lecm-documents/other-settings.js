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

(function()
{
	var Dom = YAHOO.util.Dom;

	LogicECM.module.PlatformOtherSettings = function(htmlId)
	{
		LogicECM.module.PlatformOtherSettings.superclass.constructor.call(this, "LogicECM.module.PlatformOtherSettings", htmlId, ["container", "json"]);
		return this;
	};

	YAHOO.extend(LogicECM.module.PlatformOtherSettings, Alfresco.component.Base,
		{
			onReady: function ()
			{
				this.loadSettings();
			},

			loadSettings: function() {
				var me = this;
				Alfresco.util.Ajax.request(
					{
						url: Alfresco.constants.PROXY_URI + "lecm/documents/global-settings/api/getSettingsNode",
						successCallback: {
							fn: function (response) {
								var oResults = eval("(" + response.serverResponse.responseText + ")");
								if (oResults && oResults.nodeRef) {
									me.loadForm(oResults.nodeRef);
								}
							}
						},
						failureMessage: "message.failure"
					});
			},

			loadForm: function(settingsNode) {
				var me = this;
				var successCallback = function(response) {
					var container = Dom.get(me.id + "-settings");
					container.innerHTML = response.serverResponse.responseText;

					Dom.get("documents-global-settings-edit-form-form-submit").value = me.msg("label.save");

					var form = new Alfresco.forms.Form("documents-global-settings-edit-form-form");
					form.setSubmitAsJSON(true);
					form.setAJAXSubmit(true,
						{
							successCallback:
							{
								fn: me.onSuccess,
								scope: this
							}
						});
					form.init();
				}
				Alfresco.util.Ajax.request(
					{
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
							fn: successCallback
						},
						failureMessage: "message.failure",
						execScripts: true
					});
			},

			onSuccess: function (response)
			{
				YAHOO.Bubbling.fire("formSubmit", this);

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