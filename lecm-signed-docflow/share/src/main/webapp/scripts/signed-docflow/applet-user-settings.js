/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

(function()
{
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event,
		Element = YAHOO.util.Element;


	LogicECM.AppletUserSettings = function(htmlId)
	{
		LogicECM.AppletUserSettings.superclass.constructor.call(this, "LogicECM.AppletUserSettings", htmlId, ["container", "json"]);
		return this;
	};

	YAHOO.extend(LogicECM.AppletUserSettings, Alfresco.component.Base,
		{
			onReady: function ()
			{
				this.loadSettings();
			},

			checkAspect: function(nodeRef) {
				var me = this;
				Alfresco.util.Ajax.jsonPost(
					{
						url: Alfresco.constants.PROXY_URI + "/lecm/signed-docflow/config/aspect",
						dataObj: {
							action: "get",				            
							node: nodeRef,
							aspect: "{http://www.it.ru/lecm/model/signed-docflow/1.0}personal-data-attrs-aspect"
						},
						method: "POST",
						successCallback: {
							fn: function (response) {
								var oResults = eval("(" + response.serverResponse.responseText + ")");
								if (oResults != null && oResults.enabled) {
									me.loadForm(nodeRef);
								} else {
									me.setAspect(nodeRef);
								}
							}
						},
						failureMessage: "message.failure"
					});
			},

			setAspect: function(nodeRef) {
				var me = this;
				Alfresco.util.Ajax.jsonPost(
					{
						url: Alfresco.constants.PROXY_URI + "/lecm/signed-docflow/config/aspect",
						dataObj:{
							action: "set",				            
							enabled: "true",
							node: nodeRef,
							aspect: "{http://www.it.ru/lecm/model/signed-docflow/1.0}personal-data-attrs-aspect"
						},
						method: "POST",
						successCallback: {
							fn: function (response) {
								var oResults = eval("(" + response.serverResponse.responseText + ")");
								if (oResults != null && oResults.enabled) {
									me.loadForm(nodeRef);
								}
							}
						},
						failureMessage: "message.failure"
					});
			},

			loadSettings: function() {
				var me = this;
				Alfresco.util.Ajax.request(
					{
						url: Alfresco.constants.PROXY_URI + "/lecm/orgstructure/api/getCurrentEmployee",
						successCallback: {
							fn: function (response) {
								var oResults = eval("(" + response.serverResponse.responseText + ")");
								if (oResults != null && oResults.nodeRef != null ) {
									me.checkAspect(oResults.nodeRef);
								} else {
									Alfresco.util.PopupManager.displayPrompt(
									{
										text: Alfresco.util.message("message.failure")
									});
								}
							}
						},
						failureMessage: "message.failure"
					});
			},

			loadForm: function(employeeNode) {
				var me = this;
				Alfresco.util.Ajax.request(
					{
						url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form",
						dataObj: {
							htmlid: "applet-user-settings-edit-form",
							itemKind:"node",
							itemId: employeeNode,
							formId: "applet-user-settings-form",
							mode: "edit",
							formUI: true,
							submitType:"json",
							showSubmitButton:"true"
						},
						successCallback: {
							fn: function (response) {
								var container = Dom.get(me.id + "-settings");
								container.innerHTML = response.serverResponse.responseText;

								Dom.get("applet-user-settings-edit-form-form-submit").value = me.msg("label.save");

								var form = new Alfresco.forms.Form("applet-user-settings-edit-form-form");
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