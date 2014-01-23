(function()
{
	var Dom = YAHOO.util.Dom;

	LogicECM.NotificationsGlobalSettings = function(htmlId)
	{
		this.name = "LogicECM.NotificationsGlobalSettings";
		LogicECM.NotificationsGlobalSettings.superclass.constructor.call(this, htmlId);

		/* Register this component */
		Alfresco.util.ComponentManager.register(this);

		/* Load YUI Components */
		Alfresco.util.YUILoaderHelper.require(["button", "container", "json"], this.onComponentsLoaded, this);

		NotificationsGlobalSettingsPanelHandler = function NotificationsGlobalSettingsPanelHandler()
		{
			NotificationsGlobalSettingsPanelHandler.superclass.constructor.call(this, "notifications-settings");
		};

		YAHOO.extend(NotificationsGlobalSettingsPanelHandler, Alfresco.ConsolePanelHandler, {});
		new NotificationsGlobalSettingsPanelHandler();

		return this;
	};

	YAHOO.extend(LogicECM.NotificationsGlobalSettings, Alfresco.ConsoleTool,
		{
			onReady: function ()
			{
				// Call super-class onReady() method
				LogicECM.NotificationsGlobalSettings.superclass.onReady.call(this);

				this.loadSettings();
			},

			loadSettings: function() {
				var me = this;
				Alfresco.util.Ajax.request(
					{
						url: Alfresco.constants.PROXY_URI + "lecm/notifications/getGlobalSettings",
						successCallback: {
							fn: function (response) {
								var oResults = eval("(" + response.serverResponse.responseText + ")");
								if (oResults != null && oResults.nodeRef != null ) {
									me.loadForm(oResults.nodeRef);
								}
							}
						},
						failureMessage: "message.failure"
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