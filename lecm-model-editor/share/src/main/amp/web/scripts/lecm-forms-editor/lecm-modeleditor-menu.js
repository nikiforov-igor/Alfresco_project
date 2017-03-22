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
			modelItem: {
				nodeRef: null,
				isDocumentModel: null,
				isModelActive: null,
				typeName: null,
			},
			nodeRef: null,
			nodeType: null,
			fileName: null
		},

		_getLocalParam: function() {
			var docType, statemachineId;

			if (this.options.modelItem.typeName) {
				return this.options.modelItem.typeName;
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

//		_getRemoteParam: function(successCallbackFn) {
//			var localParam = this._getLocalParam().replace(':', '_'),
//				template = '{proxyUri}lecm/docmodels/details?fileName={fileName}',
//				url = YAHOO.lang.substitute(template, {
//					proxyUri: Alfresco.constants.PROXY_URI,
//					fileName: localParam
//				}),
//				successCallback = {
//					scope: this,
//					fn: successCallbackFn
//				};
//			Alfresco.util.Ajax.jsonGet({
//				url: url,
//				successCallback: successCallback,
//				failureMessage: this.msg('message.failure')
//			});
//		},

		onModelEditorHome: function(event, obj) {
			var urlTemplate = 'doc-model-edit?doctype='+this.options.modelItem.typeName+'&formId=edit-model&redirect=/share/page/doc-model-list&nodeRef=';
			if (this.options.nodeRef) {
				window.location.href = Alfresco.constants.URL_PAGECONTEXT + urlTemplate + this.options.nodeRef;
			} else if (this.options.modelItem.nodeRef) {
				window.location.href = Alfresco.constants.URL_PAGECONTEXT + urlTemplate + this.options.modelItem.nodeRef;
			} else {
				Alfresco.util.PopupManager.displayMessage({
					text: Alfresco.util.message('lecm.meditor.msg.incorrect.editing.mode')
				});
			}
		},

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
			this.widgets.modelEditorHomeBtn = Alfresco.util.createYUIButton(this, 'modelEditorHomeBtn', this.onModelEditorHome, {
				disabled: !(this.options.modelItem.nodeRef && this.options.modelItem.isDocumentModel)
			});
			this.widgets.stateMachineHomeBtn = Alfresco.util.createYUIButton(this, 'stateMachineHomeBtn', this.onStateMachineHome, {
				disabled: !(this.options.modelItem.isModelActive && this.options.modelItem.isDocumentModel && this.options.modelItem.typeName && this.options.modelItem.typeName != 'fake')
			});
			this.widgets.formsEditorHomeBtn = Alfresco.util.createYUIButton(this, 'formsEditorHomeBtn', this.onFormsEditorHome, {
				disabled: !(this.options.modelItem.isModelActive && this.options.modelItem.typeName && this.options.modelItem.typeName != 'fake')
			});
			this.widgets.controlsEditorHomeBtn = Alfresco.util.createYUIButton(this, 'controlsEditorHomeBtn', this.onControlsEditorHome, {
				disabled: !(this.options.modelItem.isModelActive && this.options.modelItem.typeName)
			});
			this.widgets.modelListHomeBtn = Alfresco.util.createYUIButton(this, 'modelListHomeBtn', this.onModelListHome);
		}
	});
})();
