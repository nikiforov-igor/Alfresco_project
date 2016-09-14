/* global YAHOO, Alfresco */

if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.DocumentsTemplates = LogicECM.module.DocumentsTemplates || {};

(function () {
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event,
		Bubbling = YAHOO.Bubbling;

	LogicECM.module.DocumentsTemplates.Actions = function (containerId, options, messages) {
		LogicECM.module.DocumentsTemplates.Actions.superclass.constructor.call(this, 'LogicECM.module.DocumentsTemplates.Actions', containerId);
		this.setOptions(options);
		this.setMessages(messages);
		this.actionsId = containerId + '-actions';
		Bubbling.on('updateButtonState', this.onUpdateButtonState, this);
		return this;
	};

	YAHOO.lang.extend(LogicECM.module.DocumentsTemplates.Actions, Alfresco.component.Base, {

		actionsId: null,
		buttonsHide: true, /*default - disabled ADD button*/

		onActionAddClick: function (event, actionEl) {
			Bubbling.fire('addTemplateAttribute', {
				bubblingLabel: 'documentsTemplatesAttributes',
				event: event,
				actionEl: actionEl
			});
		},

		onActionSubmitClick: function (event, actionEl) {
			Bubbling.fire('beforeSubmitTemplate', {
				event: event,
				actionEl: actionEl
			});
			Bubbling.fire('submitTemplate', {
				bubblingLabel: 'documentsTemplatesDetailsView',
				event: event,
				actionEl: actionEl
			});
		},

		onActionClearClick: function (event, actionEl) {
			Bubbling.fire('clearTemplateAttributes', {
				bubblingLabel: 'documentsTemplatesAttributes',
				event: event,
				actionEl: actionEl
			});
		},

		onUpdateButtonState: function (layer, args) {
			this.buttonsHide = args[1].disabledState ? args[1].disabledState : false;
			this._init(false);
		},

		onReady: function () {
			this._init(true);
		},

		_init: function(firstTime) {
			var actionAdd = Dom.get(this.id + '-action-add');
			var actionSubmit = Dom.get(this.id + '-action-submit');
			var actionClear = Dom.get(this.id + '-action-clear');

			if (firstTime) {
				Event.on(actionAdd, 'click', this.onActionAddClick, actionAdd, this);
				Event.on(actionSubmit, 'click', this.onActionSubmitClick, actionSubmit, this);
				Event.on(actionClear, 'click', this.onActionClearClick, actionClear, this);
			}

			/*состояния кнопок*/
			if (actionAdd) {
				YAHOO.util.Dom.setStyle(actionAdd, 'display', this.buttonsHide ? 'none' : 'block');
			}
		}
	}, true);
})();
