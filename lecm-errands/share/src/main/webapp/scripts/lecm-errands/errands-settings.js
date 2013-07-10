(function()
{
	/**
	 * YUI Library aliases
	 */
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event,
		Element = YAHOO.util.Element;

	/**
	 * Alfresco Slingshot aliases
	 */
	var $html = Alfresco.util.encodeHTML;

	/**
	 * ConsoleTrashcan constructor.
	 *
	 * @param {String} htmlId The HTML id �of the parent element
	 * @return {Alfresco.ConsoleTrashcan} The new ConsoleTrashcan instance
	 * @constructor
	 */
	LogicECM.ErrandsSettings = function(htmlId)
	{
		this.name = "LogicECM.ErrandsSettings";
		LogicECM.ErrandsSettings.superclass.constructor.call(this, htmlId);

		/* Register this component */
		Alfresco.util.ComponentManager.register(this);

		/* Load YUI Components */
		Alfresco.util.YUILoaderHelper.require(["button", "container", "json"], this.onComponentsLoaded, this);

		ErrandsSettingsPanelHandler = function ErrandsSettingsPanelHandler()
		{
			ErrandsSettingsPanelHandler.superclass.constructor.call(this, "errands-settings");
		};

		YAHOO.extend(ErrandsSettingsPanelHandler, Alfresco.ConsolePanelHandler, {});
		new ErrandsSettingsPanelHandler();

		return this;
	};

	YAHOO.extend(LogicECM.ErrandsSettings, Alfresco.ConsoleTool,
		{
			onReady: function ConsoleErrandsSettings_onReady()
			{
				// Call super-class onReady() method
				LogicECM.ErrandsSettings.superclass.onReady.call(this);

				this.loadSettings();
			},

			loadSettings: function() {
				var me = this;
				Alfresco.util.Ajax.request(
					{
						url: Alfresco.constants.PROXY_URI + "lecm/errands/getGlobalSettings",
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
							htmlid: "errands-settings-edit-form",
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

								Dom.get("errands-settings-edit-form-form-submit").value = me.msg("label.save");

								var form = new Alfresco.forms.Form("errands-settings-edit-form-form");
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