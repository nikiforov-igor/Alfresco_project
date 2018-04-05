/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

(function() {
	var Dom = YAHOO.util.Dom;

	LogicECM.ErrandsUserSettings = function(htmlId) {
		LogicECM.ErrandsUserSettings.superclass.constructor.call(this, "LogicECM.ErrandsUserSettings", htmlId, ["container", "json"]);

		YAHOO.Bubbling.on("beforeFormRuntimeInit", this.beforeFormInit, this);
		return this;
	};

	YAHOO.extend(LogicECM.ErrandsUserSettings, Alfresco.component.Base, {
		canChooseInititator: false,

		onReady: function () {
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
							me.loadSettings();
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
			Alfresco.util.Ajax.request({
				url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form",
				dataObj: {
					htmlid: "errands-user-settings-edit-form",
					itemKind: "node",
					itemId: settingsNode,
					mode: "edit",
					formUI: true,
					submitType: "json",
					showSubmitButton: "true",
					showCaption: false
				},
				successCallback: {
					scope: this,
					fn: function (response) {
						Dom.get(this.id + "-settings").innerHTML = response.serverResponse.responseText;
						Dom.get("errands-user-settings-edit-form-form-submit").value = this.msg("label.save");

						var defaultInitiatorControl = Dom.get("errands-user-settings-edit-form_assoc_lecm-errands_user-settings-default-initiator-assoc-cntrl");
						if (defaultInitiatorControl) {
							defaultInitiatorControl.title = Alfresco.util.message("lecm.errands.user-settings.default-initiator.description");
							if (!this.canChooseInititator) {
								LogicECM.module.Base.Util.disableControl("errands-user-settings-edit-form", "lecm-errands:user-settings-default-initiator-assoc");
							}
						}
					}
				},
				failureMessage: "message.failure",
				execScripts: true
			});
		},

		beforeFormInit: function (layer, args) {
			YAHOO.Bubbling.unsubscribe("beforeFormRuntimeInit", this.beforeFormInit);
			var form = args[1].runtime;
			form.setSubmitAsJSON(true);
			form.setAJAXSubmit(true, {
				successCallback: {
					scope: this,
					fn: this.onSuccess
				}
			});
		},

		onSuccess: function (response) {
			if (response && response.json) {
				Alfresco.util.PopupManager.displayMessage({
					text: Alfresco.util.message("message.save.success")
				});
				window.location.reload();
			} else {
				Alfresco.util.PopupManager.displayPrompt({
					text: Alfresco.util.message("message.failure")
				});
			}
		}
	});
})();