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

		options: {
			nodeRef: null,
			nodeType: null,
			fileName: null
		},

		_getLocalParam: function() {
			var docType, statemachineId;

			if (this.options.fileName) {
				return this.options.fileName;
			}

			docType = YAHOO.util.History.getQueryStringParameter('doctype');
			if (docType) {
				return docType;
			}

			statemachineId = statemachineId = YAHOO.util.History.getQueryStringParameter('statemachineId');
			if (statemachineId) {
				return statemachineId;
			}

			return null;
		},

		_getRemoteParam: function(successCallbackFn) {
			var localParam = this._getLocalParam().replace(':', '_'),
				template = '{proxyUri}lecm/docmodels/details?fileName={fileName}',
				url = YAHOO.lang.substitute(template, {
					proxyUri: Alfresco.constants.PROXY_URI,
					fileName: localParam
				}),
				successCallback = {
					scope: this,
					fn: successCallbackFn
				};
			Alfresco.util.Ajax.jsonGet({
				url: url,
				successCallback: successCallback,
				failureMessage: this.msg('message.failure')
			});
		},

//		onModelEditorHome: function(event, obj) {
//			var urlTemplate = 'doc-model-edit?formId=edit-model&redirect=/share/page/doc-model-list&nodeRef=';
//			if (this.options.nodeRef) {
//				window.location.href = Alfresco.constants.URL_PAGECONTEXT + urlTemplate + this.options.nodeRef;
//			} else {
//				this._getRemoteParam(function(serverResponse) {
//					var json = serverResponse.json;
//					window.location.href = Alfresco.constants.URL_PAGECONTEXT + urlTemplate + json.nodeRef;
//				});
//			}
//		},

		onStateMachineHome: function(event, obj) {
			var param = this._getLocalParam();
			if (param) {
				window.location.href = Alfresco.constants.URL_PAGECONTEXT + 'statemachine?statemachineId=' + param.replace(':', '_');
			} else {
				window.location.href = Alfresco.constants.URL_PAGECONTEXT + 'statemachine';
			}
		},

		onFormsEditorHome: function(event, obj) {
			var param = this._getLocalParam();
			if (param) {
				window.location.href = Alfresco.constants.URL_PAGECONTEXT + 'doc-forms-list?doctype=' + param.replace('_', ':');
			} else {
				window.location.href = Alfresco.constants.URL_PAGECONTEXT + 'doc-forms-list';
			}
		},

		onControlsEditorHome: function(event, obj) {
			var param = this._getLocalParam();
			if (param) {
				window.location.href = Alfresco.constants.URL_PAGECONTEXT + 'doc-controls-list?doctype=' + param.replace('_', ':');
			} else {
				window.location.href = Alfresco.constants.URL_PAGECONTEXT + 'doc-controls-list';
			}
		},

		onModelListHome: function(event, obj) {
			window.location.href = Alfresco.constants.URL_PAGECONTEXT + 'doc-model-list';
		},

		onReady:function () {
//			this.widgets.modelEditorHomeBtn = Alfresco.util.createYUIButton(this, 'modelEditorHomeBtn', this.onModelEditorHome);
			this.widgets.stateMachineHomeBtn = Alfresco.util.createYUIButton(this, 'stateMachineHomeBtn', this.onStateMachineHome);
			this.widgets.formsEditorHomeBtn = Alfresco.util.createYUIButton(this, 'formsEditorHomeBtn', this.onFormsEditorHome);
			this.widgets.controlsEditorHomeBtn = Alfresco.util.createYUIButton(this, 'controlsEditorHomeBtn', this.onControlsEditorHome);
			this.widgets.modelListHomeBtn = Alfresco.util.createYUIButton(this, 'modelListHomeBtn', this.onModelListHome);
		}
	});
})();
