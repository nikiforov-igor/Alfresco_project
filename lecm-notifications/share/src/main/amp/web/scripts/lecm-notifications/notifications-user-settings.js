/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

(function()
{
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event,
		Element = YAHOO.util.Element;


	LogicECM.NotificationsUserSettings = function(htmlId)
	{
		LogicECM.NotificationsUserSettings.superclass.constructor.call(this, "LogicECM.NotificationsUserSettings", htmlId, ["container", "json"]);
		return this;
	};

	YAHOO.extend(LogicECM.NotificationsUserSettings, Alfresco.component.Base,
		{
			onReady: function () {
				this.loadSettings();
			},

			loadSettings: function() {
				Alfresco.util.Ajax.jsonGet({
					url: Alfresco.constants.PROXY_URI + "lecm/notifications/getUserSettings",
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
							htmlid: "NotificationsUserSettings-user-settings-edit-form",
							itemKind:"node",
							itemId: settingsNode,
							mode: "edit",
							formUI: true,
							submitType:"json",
							showSubmitButton:"true",
							showCaption: false
						},
						successCallback: {
							fn: function (response) {
								var container = Dom.get(me.id + "-settings");
								container.innerHTML = response.serverResponse.responseText;

								Dom.get("NotificationsUserSettings-user-settings-edit-form-form-submit").value = me.msg("label.save");

								var form = new Alfresco.forms.Form("NotificationsUserSettings-user-settings-edit-form-form");
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
                }			}
		});
})();