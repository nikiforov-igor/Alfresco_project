if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.ControlsEditor = LogicECM.module.ControlsEditor || {};

(function() {
	LogicECM.module.ControlsEditor.ControlTemplateControl = function(containerId) {
		return LogicECM.module.ControlsEditor.ControlTemplateControl.superclass.constructor.call(this, 'LogicECM.module.ControlsEditor.ControlTemplateControl', containerId);
	};

	YAHOO.lang.extend(LogicECM.module.ControlsEditor.ControlTemplateControl, Alfresco.component.Base, {
		onReady: function() {
			console.log('LogicECM.module.ControlsEditor.ControlTemplateControl ready!');
		}
	}, true);
})();
