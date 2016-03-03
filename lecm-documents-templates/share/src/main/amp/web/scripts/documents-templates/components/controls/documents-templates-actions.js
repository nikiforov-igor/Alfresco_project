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
		return this;
	};

	YAHOO.lang.extend(LogicECM.module.DocumentsTemplates.Actions, Alfresco.component.Base, {

		actionsId: null,

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

		onReady: function () {
			var actionAdd = Dom.get(this.id + '-action-add');
			var actionSubmit = Dom.get(this.id + '-action-submit');
			// var actionClear = Dom.get(this.id + '-action-clear');

			Event.on(actionAdd, 'click', this.onActionAddClick, actionAdd, this);
			Event.on(actionSubmit, 'click', this.onActionSubmitClick, actionSubmit, this);
			// Event.on(actionClear, 'click', this.onActionClearClick, actionClear, this);

			console.log(this.name + '[' + this.id + '] is ready');
		}
	}, true);
})();
