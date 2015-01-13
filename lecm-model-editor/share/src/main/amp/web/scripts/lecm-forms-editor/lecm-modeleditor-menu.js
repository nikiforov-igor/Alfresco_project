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

		onModelEditorHome: function(event, obj) {
			Alfresco.util.PopupManager.displayMessage({
				text: 'Not implementer yet'
			});
		},

		onStateMachineHome: function(event, obj) {
			var docType, statemachineId, param;
			docType = YAHOO.util.History.getQueryStringParameter('doctype');
			if (docType) {
				param = docType.replace(':', '_');
			} else {
				statemachineId = YAHOO.util.History.getQueryStringParameter('statemachineId');
				param = statemachineId;
			}
			if (param) {
				window.location.href = Alfresco.constants.URL_PAGECONTEXT + 'statemachine?statemachineId=' + param;
			} else {
				window.location.href = Alfresco.constants.URL_PAGECONTEXT + 'statemachine';
			}
		},

		onFormsEditorHome: function(event, obj) {
			var docType, statemachineId, param;
			docType = YAHOO.util.History.getQueryStringParameter('doctype');
			if (docType) {
				param = docType;
			} else {
				statemachineId = YAHOO.util.History.getQueryStringParameter('statemachineId');
				param = statemachineId.replace('_', ':');
			}
			if (param) {
				window.location.href = Alfresco.constants.URL_PAGECONTEXT + 'doc-forms-list?doctype=' + param;
			} else {
				window.location.href = Alfresco.constants.URL_PAGECONTEXT + 'doc-forms-list';
			}
		},

		onControlsEditorHome: function(event, obj) {
			var docType, statemachineId, param;
			docType = YAHOO.util.History.getQueryStringParameter('doctype');
			if (docType) {
				param = docType;
			} else {
				statemachineId = YAHOO.util.History.getQueryStringParameter('statemachineId');
				param = statemachineId.replace('_', ':');
			}
			if (param) {
				window.location.href = Alfresco.constants.URL_PAGECONTEXT + 'doc-controls-list?doctype=' + param;
			} else {
				window.location.href = Alfresco.constants.URL_PAGECONTEXT + 'doc-controls-list';
			}
		},

		onModelListHome: function(event, obj) {
			window.location.href = Alfresco.constants.URL_PAGECONTEXT + 'doc-model-list';
		},

		onReady:function () {
			this.widgets.modelEditorHomeBtn = Alfresco.util.createYUIButton(this, 'modelEditorHomeBtn', this.onModelEditorHome);
			this.widgets.stateMachineHomeBtn = Alfresco.util.createYUIButton(this, 'stateMachineHomeBtn', this.onStateMachineHome);
			this.widgets.formsEditorHomeBtn = Alfresco.util.createYUIButton(this, 'formsEditorHomeBtn', this.onFormsEditorHome);
			this.widgets.controlsEditorHomeBtn = Alfresco.util.createYUIButton(this, 'controlsEditorHomeBtn', this.onControlsEditorHome);
			this.widgets.modelListHomeBtn = Alfresco.util.createYUIButton(this, 'modelListHomeBtn', this.onModelListHome);
		}
	});
})();
