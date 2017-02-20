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

	LogicECM.module.NotificationsGlobalSettings = function(htmlId)
	{
		LogicECM.module.NotificationsGlobalSettings.superclass.constructor.call(this, "LogicECM.module.NotificationsGlobalSettings", htmlId, ["container", "json"]);
		return this;
	};

	YAHOO.extend(LogicECM.module.NotificationsGlobalSettings, Alfresco.component.Base,
		{
			onReady: function () {
				this.loadSettings();
			},

			loadSettings: function() {
				Alfresco.util.Ajax.jsonGet({
					url: Alfresco.constants.PROXY_URI + "lecm/notifications/getGlobalSettings",
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
				var me = this;
				Alfresco.util.Ajax.request(
					{
						url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form",
						dataObj: {
							htmlid: "notifications-settings-edit-form",
							itemKind:"node",
							itemId: settingsNode,
							mode: "edit",
							formUI: true,
							submitType:"json",
							showSubmitButton:"true"
						},
						successCallback: {
							fn: function (response) {
								var container = Dom.get(me.id + "-settings");
								container.innerHTML = response.serverResponse.responseText;

								Dom.get("notifications-settings-edit-form-form-submit").value = me.msg("label.save");

								var form = new Alfresco.forms.Form("notifications-settings-edit-form-form");
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
						},
						failureMessage: "message.failure",
						execScripts: true
					});
			},

			onSuccess: function (response)
			{
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