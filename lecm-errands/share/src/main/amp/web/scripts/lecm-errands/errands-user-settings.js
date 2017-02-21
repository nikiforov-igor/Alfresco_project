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


	LogicECM.ErrandsUserSettings = function(htmlId)
	{
		LogicECM.ErrandsUserSettings.superclass.constructor.call(this, "LogicECM.ErrandsUserSettings", htmlId, ["container", "json"]);
		return this;
	};

	YAHOO.extend(LogicECM.ErrandsUserSettings, Alfresco.component.Base,
		{
			canChooseInititator: false,

			onReady: function ()
			{
				Alfresco.util.Ajax.jsonGet({
					url: Alfresco.constants.PROXY_URI + "lecm/orgstructure/api/getCurrentEmployeeRoles",
					successCallback: {
						fn: function (response) {
							var me = response.config.scope;
							if (response && response.json) {
								var roles = response.json;
								me.canChooseInititator = roles.some(function(role){
									return "CHOOSING_INITIATOR" == role.id
								});
                                this.loadSettings();
							}
						}
					},
					failureMessage: this.msg("message.details.failure"),
					scope: this
				});


			},

			loadSettings: function() {
				Alfresco.util.Ajax.jsonGet({
					url: Alfresco.constants.PROXY_URI + "lecm/errands/getUserSettings",
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
							htmlid: "errands-user-settings-edit-form",
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

								Dom.get("errands-user-settings-edit-form-form-submit").value = me.msg("label.save");

								var defaultInitiatorControl = Dom.get("errands-user-settings-edit-form_assoc_lecm-errands_user-settings-default-initiator-assoc-cntrl");
								if(defaultInitiatorControl) {
									defaultInitiatorControl.title = Alfresco.util.message("lecm.errands.user-settings.default-initiator.description");
									if (!me.canChooseInititator) {
										LogicECM.module.Base.Util.disableControl("errands-user-settings-edit-form", "lecm-errands:user-settings-default-initiator-assoc");
									}
								}

								var form = new Alfresco.forms.Form("errands-user-settings-edit-form-form");
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
	                window.location.reload();
                } else {
                    Alfresco.util.PopupManager.displayPrompt(
                        {
                            text: Alfresco.util.message("message.failure")
                        });
                }
			}
		});
})();