if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

(function() {
	var Dom = YAHOO.util.Dom;

	LogicECM.module.LecmOSSettings = function(htmlId) {
		LogicECM.module.LecmOSSettings.superclass.constructor.call(this, 'LogicECM.module.LecmOSSettings', htmlId, ['container', 'json']);
		return this;
	};

	YAHOO.extend(LogicECM.module.LecmOSSettings, Alfresco.component.Base, {
		onReady: function() {
			this.loadSettings();
		},
		loadSettings: function() {
			Alfresco.util.Ajax.request({
				url: Alfresco.constants.PROXY_URI + 'lecm/operative-storage/settings',
				successCallback: {
					scope: this,
					fn: function(response) {
						var oResults = JSON.parse(response.serverResponse.responseText);

						if (oResults && oResults.nodeRef) {
							this.loadForm(oResults.nodeRef);
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
					htmlid: 'lecm-os-settings-edit-form',
					itemKind: 'node',
					itemId: settingsNode,
					formId: 'lecm-os-settings',
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

						Dom.get('lecm-os-settings-edit-form-form-submit').value = this.msg('label.save');

						var form = new Alfresco.forms.Form('lecm-os-settings-edit-form-form');
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
				window.location.reload(true);
			} else {
				Alfresco.util.PopupManager.displayPrompt({
					text: Alfresco.util.message('message.failure')
				});
			}
		}
	});
})();
