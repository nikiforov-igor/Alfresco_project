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

	LogicECM.module.ModelEditor.Menu2 = function (htmlId) {
		return LogicECM.module.ModelEditor.Menu2.superclass.constructor.call(
			this,
			'LogicECM.module.AllDictionary.Menu2',
			htmlId,
			['button', 'history']);
	};

	YAHOO.extend(LogicECM.module.ModelEditor.Menu2, Alfresco.component.Base, {

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

		onModelEditorHome: function(event, obj) {
			var urlTemplate = 'dict-model-edit?doctype='+this.options.modelItem.typeName+'&formId=edit-dict-model&redirect=/share/page/dict-model-list&nodeRef=';
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

		onFormsEditorHome: function(event, obj) {
			var param = this._getLocalParam();
			if (param) {
				window.location.href = Alfresco.constants.URL_PAGECONTEXT + 'dict-forms-list?doctype=' + param.replace('_', ':');
			} else {
				window.location.href = Alfresco.constants.URL_PAGECONTEXT + 'dict-forms-list';
			}
		},

		onControlsEditorHome: function(event, obj) {
			var param = this._getLocalParam();
			if (param) {
				window.location.href = Alfresco.constants.URL_PAGECONTEXT + 'dict-controls-list?doctype=' + param.replace('_', ':');
			} else {
				window.location.href = Alfresco.constants.URL_PAGECONTEXT + 'dict-controls-list';
			}
		},

		onModelListHome: function(event, obj) {
			window.location.href = Alfresco.constants.URL_PAGECONTEXT + 'dict-model-list';
		},

		onReady:function () {
			this.widgets.modelEditorHomeBtn = Alfresco.util.createYUIButton(this, 'modelEditorHomeBtn', this.onModelEditorHome, {
				disabled: !(this.options.modelItem.nodeRef)
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
