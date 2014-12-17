if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};


LogicECM.module.ControlsEditor = LogicECM.module.ControlsEditor || {};

(function () {
	LogicECM.module.ControlsEditor.Toolbar = function (containerId) {
		return LogicECM.module.ControlsEditor.Toolbar.superclass.constructor.call(this, 'LogicECM.module.ControlsEditor.Toolbar', containerId);
	};

	YAHOO.lang.extend(LogicECM.module.ControlsEditor.Toolbar, LogicECM.module.Base.Toolbar, {

		onCreateNewControl: function() {
			console.log('Invoking onCreateNewControl using scope ' + this);
		},

		onGenerateControls: function() {
			console.log('Invoking onGenerateControls using scope ' + this);
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
