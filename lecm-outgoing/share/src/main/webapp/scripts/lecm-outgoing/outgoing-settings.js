(function()
{
	var Dom = YAHOO.util.Dom;

	LogicECM.OutgoingDocsSettings = function(htmlId)
	{
		LogicECM.OutgoingDocsSettings.superclass.constructor.call(this, "LogicECM.OutgoingDocsSettings", htmlId, ["container", "json"]);
		return this;
	};

	YAHOO.extend(LogicECM.OutgoingDocsSettings, Alfresco.component.Base,
		{
			onReady: function ()
			{
				this.loadSettings();
			},

			loadSettings: function() {
				var me = this;
				Alfresco.util.Ajax.request(
					{
						url: Alfresco.constants.PROXY_URI + "lecm/outgoing/getSettingsNode",
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
							htmlid: "outgoing-docs-settings-edit-form",
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

								Dom.get("outgoing-docs-settings-edit-form-form-submit").value = me.msg("label.save");

								var form = new Alfresco.forms.Form("outgoing-docs-settings-edit-form-form");
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