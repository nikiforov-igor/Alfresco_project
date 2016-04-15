/* global YAHOO, Alfresco */

if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.ModelEditor = LogicECM.module.ModelEditor || {};

(function () {
	LogicECM.module.ModelEditor.FormManager = function (containerId, options, messages) {
		LogicECM.module.ModelEditor.FormManager.superclass.constructor.call(this, containerId);
		this.name = 'LogicECM.module.ModelEditor.FormManager';
		Alfresco.util.ComponentManager.reregister(this);
		this.options = YAHOO.lang.merge(this.options, LogicECM.module.ModelEditor.FormManager.superclass.options);
		this.setOptions(options);
		this.setMessages(messages);

		YAHOO.Bubbling.on('afterFormRuntimeInit', this.onAfterFormRuntimeInit, this);

		return this;
	};

	YAHOO.extend(LogicECM.module.ModelEditor.FormManager, Alfresco.component.FormManager, {
		onBeforeFormRuntimeInit: function (layer, args) {
			LogicECM.module.ModelEditor.FormManager.superclass.onBeforeFormRuntimeInit.call(this, layer, args);
			args[1].runtime.doBeforeFormSubmit = {
				scope: this,
				obj: null,
				fn: this.onBeforeFormSubmit
			};
		},

		onAfterFormRuntimeInit: function (layer, args) {
			// здесь мы получим XML, или поднимем из него json, или куда-нибудь передадим

			debugger;
		},

		onBeforeFormSubmit: function (form, obj) {
			debugger;
		}
	}, true);
})();
