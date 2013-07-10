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
			destination: null,

			onReady: function ConsoleErrandsSettings_onReady()
			{
				// Call super-class onReady() method
				LogicECM.ErrandsSettings.superclass.onReady.call(this);

				this.getDestination();
			},

			getDestination: function() {
				var me = this;
				Alfresco.util.Ajax.request(
					{
						url: Alfresco.constants.PROXY_URI + "lecm/errands/getDraftRoot",
						successCallback: {
							fn: function (response) {
								var oResults = eval("(" + response.serverResponse.responseText + ")");
								if (oResults != null && oResults.nodeRef != null ) {
									me.destination = oResults.nodeRef;

									me.showEditForn();
								}
							}
						},
						failureMessage: "message.failure"
					});
			},

			showEditForn: function() {
				var me = this;
				Alfresco.util.Ajax.request(
					{
						url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form",
						dataObj: {
							htmlid: "errands-settings-edit-form",
							itemKind:"type",
							itemId: "lecm-errands:settings",
							destination: "123",
							mode: "create",
							formId: me.id,
							submitType:"json",
							showSubmitButton:"true"
						},
						successCallback: {
							fn: function (response) {
								var container = Dom.get(me.id + "-settings");
//								container.innerHTML = response.serverResponse.responseText;
								container.innerHTML = me.destination;
							}
						},
						failureMessage: "message.failure",
						execScripts: true
					});
			}
		});
})();