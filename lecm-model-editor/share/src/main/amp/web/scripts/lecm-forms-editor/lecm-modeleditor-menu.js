/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

/**
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module
 */
LogicECM.module = LogicECM.module || {};


/**
 * LogicECM Dictionary module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.AllDictionary.AllDictionary
 */
LogicECM.module.ModelEditor = LogicECM.module.ModelEditor || {};

/**
 * AllDictionary module.
 *
 * @namespace LogicECM.module
 * @class LogicECM.module.AllDictionary
 */
(function () {

	LogicECM.module.ModelEditor.Menu = function (htmlId) {
		return LogicECM.module.ModelEditor.Menu.superclass.constructor.call(
			this,
			'LogicECM.module.AllDictionary.Menu',
			htmlId,
			['button', 'history']);
	};

	YAHOO.extend(LogicECM.module.ModelEditor.Menu, Alfresco.component.Base, {
		onReady:function () {
			this.widgets.modelEditorHomeBtn = Alfresco.util.createYUIButton(this, 'modelEditorHomeBtn', function() {
				window.location.href = Alfresco.constants.URL_PAGECONTEXT + 'doc-model-list';
			});

			this.widgets.formsEditorHomeBtn = Alfresco.util.createYUIButton(this, 'formsEditorHomeBtn', function() {
				var docType, statemachineId, param;
				docType = YAHOO.util.History.getQueryStringParameter('doctype');
				if (docType) {
					param = docType;
				} else {
					statemachineId = YAHOO.util.History.getQueryStringParameter('statemachineId');
					param = statemachineId.replace('_', ':');
				}
				if (param) {
					window.location.href = Alfresco.constants.URL_PAGECONTEXT + 'doc-forms-list?doctype='+param;
				} else {
					window.location.href = Alfresco.constants.URL_PAGECONTEXT + 'doc-forms-list';
				}
			});

			this.widgets.controlsEditorHomeBtn = Alfresco.util.createYUIButton(this, 'controlsEditorHomeBtn', function() {
				var docType, statemachineId, param;
				docType = YAHOO.util.History.getQueryStringParameter('doctype');
				if (docType) {
					param = docType;
				} else {
					statemachineId = YAHOO.util.History.getQueryStringParameter('statemachineId');
					param = statemachineId.replace('_', ':');
				}
				if (param) {
					window.location.href = Alfresco.constants.URL_PAGECONTEXT + 'doc-controls-list?doctype='+param;
				} else {
					window.location.href = Alfresco.constants.URL_PAGECONTEXT + 'doc-controls-list';
				}
			});
		}
	});
})();
