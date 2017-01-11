if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Deputy = LogicECM.module.Deputy || {};

(function() {
	var Dom = YAHOO.util.Dom;

	LogicECM.module.Deputy.Settings = function(htmlId) {
		LogicECM.module.Deputy.Settings.superclass.constructor.call(this, 'LogicECM.module.Deputy.Settings', htmlId, ['container', 'json']);
		return this;
	};

	YAHOO.extend(LogicECM.module.Deputy.Settings, Alfresco.component.Base, {
		onReady: function() {
			this.loadSettings();
		},
		loadSettings: function() {
			Alfresco.util.Ajax.request({
				url: Alfresco.constants.PROXY_URI + 'lecm/deputy/getSettingsNode',
				successCallback: {
					scope: this,
					fn: function(response) {
						var oResults = JSON.parse(response.serverResponse.responseText);

						if (oResults && oResults.settingsNodeRef) {
							this.loadForm(oResults.settingsNodeRef);
						}
					}
				},
				failureMessage: 'message.failure'
			});
		},
		loadForm: function(settingsNode) {
			Alfresco.util.Ajax.request({
				url: Alfresco.constants.URL_SERVICECONTEXT + 'components/form',
				dataObj: {
					htmlid: 'lecm-deputy-settings-edit-form',
					itemKind: 'node',
					itemId: settingsNode,
					formId: 'lecm-deputy-settings',
					mode: 'edit',
					formUI: true,
					submitType: 'json',
					showSubmitButton: 'true',
					showCaption: false
				},
				successCallback: {
					scope: this,
					fn: function(response) {
						var container = Dom.get(this.id + '-settings');
						container.innerHTML = response.serverResponse.responseText;

						Dom.get('lecm-deputy-settings-edit-form-form-submit').value = this.msg('label.save');

						var form = new Alfresco.forms.Form('lecm-deputy-settings-edit-form-form');
						form.setSubmitAsJSON(true);
						form.setAJAXSubmit(true, {
							successCallback: {
								fn: this.onSuccess,
								scope: this
							}
						});
						form.init();
					}
				},
				execScripts: true
			});
		},
		onSuccess: function(response) {
			YAHOO.Bubbling.fire('formSubmit', this);

			if (response && response.json) {
				Alfresco.util.PopupManager.displayMessage({
						text: Alfresco.util.message("message.save.success")
					});
			} else {
				Alfresco.util.PopupManager.displayPrompt({
					text: Alfresco.util.message("message.failure")
				});
			}
		}
	});
})();
