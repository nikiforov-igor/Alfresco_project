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
			console.log('Invoking onDeployControls using scope ' + this);
		},


		_initButtons: function () {
			Alfresco.util.createYUIButton(this, "btnCreateNewControl", this.onCreateNewControl, {label: 'Создать контрол'});
			Alfresco.util.createYUIButton(this, "btnGenerateControls", this.onGenerateControls, {label: 'Сгенерировать'});
			Alfresco.util.createYUIButton(this, "btnDeployControls", this.onDeployControls, {label: 'Развернуть'});
		}
	});
})();
