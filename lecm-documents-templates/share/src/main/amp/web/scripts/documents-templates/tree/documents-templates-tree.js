/* global YAHOO, Alfresco */

if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.DocumentsTemplates = LogicECM.module.DocumentsTemplates || {};

(function () {
	LogicECM.module.DocumentsTemplates.TreeView = function(containerId, options, messages) {
		LogicECM.module.DocumentsTemplates.TreeView.superclass.constructor.call(this, 'LogicECM.module.DocumentsTemplates.TreeView', containerId);
		this.setOptions(options);
		this.setMesages(messages);
		return this;
	};

	YAHOO.lang.extend(LogicECM.module.DocumentsTemplates.TreeView, Alfresco.component.Base, {

		onReady: function() {
			console.log(this.name + '[' + this.id + '] is ready');
		}
	}, true);
})();
