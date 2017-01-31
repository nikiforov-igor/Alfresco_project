if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};


LogicECM.module.ControlsEditor = LogicECM.module.ControlsEditor || {};

(function () {
	var Bubbling = YAHOO.Bubbling;

	LogicECM.module.ControlsEditor.Toolbar = function (containerId) {
		return LogicECM.module.ControlsEditor.Toolbar.superclass.constructor.call(this, 'LogicECM.module.ControlsEditor.Toolbar', containerId);
	};

	YAHOO.lang.extend(LogicECM.module.ControlsEditor.Toolbar, LogicECM.module.Base.Toolbar, {

		onCreateNewControl: function() {
			var newControlDialog,
				templateUrl = Alfresco.constants.URL_SERVICECONTEXT + 'components/form',
				templateRequestParams = {
					itemKind: 'type',
					itemId: 'lecm-controls-editor:control',
					destination: this.options.destination,
					mode: 'create',
					submitType: 'json',
					showCancelButton: true,
					showCaption: false
				};

			if (this.createNewControlClicked) {
				return;
			}

			this.createNewControlClicked = true;

			function doBeforeDialogShow(p_form, p_dialog) {
				p_dialog.dialog.setHeader(Alfresco.util.message('lecm.meditor.ttl.new.control'));
				p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
				this.createNewControlClicked = false;
			}

			function onSuccess(serverResponse) {
				Bubbling.fire('nodeCreated', {
					nodeRef: serverResponse.json.persistedObject,
					bubblingLabel: this.options.bubblingLabel
				});
				Bubbling.fire('dataItemCreated', {
					nodeRef: serverResponse.json.persistedObject,
					bubblingLabel: this.options.bubblingLabel
				});
				Alfresco.util.PopupManager.displayMessage({
					text: this.msg('message.save.success')
				});
				this.createNewControlClicked = false;
			}

			function onFailure(serverResponse) {
				Alfresco.util.PopupManager.displayMessage({
					text: this.msg('message.save.failure')
				});
				this.createNewControlClicked = false;
			}

			newControlDialog = new Alfresco.module.SimpleDialog(this.id + '-newControlDialog');
			newControlDialog.setOptions({
				width: '50em',
				//actionUrl: '',
				templateUrl: templateUrl,
				templateRequestParams: templateRequestParams,
				destroyOnHide: true,
				doBeforeDialogShow: {
					scope: this,
					fn: doBeforeDialogShow
				},
				onSuccess: {
					scope: this,
					fn: onSuccess
				},
				onFailure: {
					scope: this,
					fn: onFailure
				}
			});
			newControlDialog.show();
		},

		onGenerateControls: function() {
			Alfresco.util.Ajax.jsonGet({
				url: Alfresco.constants.PROXY_URI + 'lecm/controls/generate?typename=' + this.options.typename,
				successCallback: {
					scope: this,
					fn: function(serverResponse) {
						var json = serverResponse.json;
						if (json && json.success) {
							Alfresco.util.PopupManager.displayMessage({
								text: this.msg('message.generate.success')
							});
						} else {
							Alfresco.util.PopupManager.displayMessage({
								text: this.msg('message.failure')
							});
						}
					}
				},
				failureMessage: this.msg('message.failure')
			});
		},

		onDeployControls: function() {
			Alfresco.util.Ajax.request({
				url: Alfresco.constants.URL_SERVICECONTEXT + 'lecm/config/init',
				dataObj: {
					reset: true
				},
				successMessage: this.msg('message.deploy.success'),
				failureMessage: this.msg('message.failure')
			});
		},


		_initButtons: function () {
			Alfresco.util.createYUIButton(this, 'btnCreateNewControl', this.onCreateNewControl, {label: this.msg('toolbar.button.createnew')});
			Alfresco.util.createYUIButton(this, 'btnGenerateControls', this.onGenerateControls, {label: this.msg('toolbar.button.generate')});
			Alfresco.util.createYUIButton(this, 'btnDeployControls', this.onDeployControls, {label: this.msg('toolbar.button.deploy')});
		}
	});
})();
