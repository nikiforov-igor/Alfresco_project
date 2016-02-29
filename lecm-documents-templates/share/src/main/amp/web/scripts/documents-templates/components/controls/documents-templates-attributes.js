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

	LogicECM.module.DocumentsTemplates.Attributes = function (containerId, options, messages) {
		LogicECM.module.DocumentsTemplates.Attributes.superclass.constructor.call(this, 'LogicECM.module.DocumentsTemplates.Attributes', containerId);
		this.setOptions(options);
		this.setMessages(messages);
		Bubbling.on('addTemplateAttribute', this.onAddTemplateAttribute, this);
		Bubbling.on('clearTemplateAttributes', this.onClearTemplateAttributes, this);
		return this;
	};

	YAHOO.lang.extend(LogicECM.module.DocumentsTemplates.Attributes, Alfresco.component.Base, {

		options: {
			bubblingLabel: null
		},

		onAddTemplateAttribute: function (layer, args) {
			//добавление строки в датагрид
			//получение списка неиспользованных атрибутов
			//построение контрола по выбранному атрибуту

		},

		onClearTemplateAttributes: function (layer, args) {

		},

		onReady: function () {
			/*
			 * скорее всего контрол будет предствлять собой YAHOO.widget.Datatable
			 * каждая строка будет состоять из 3х колонок: действие "удалить", "выпадашка с атрибутами", "поле для контрола"
			*/
			console.log(this.name + '[' + this.id + '] is ready');
		}
}, true);
})();
