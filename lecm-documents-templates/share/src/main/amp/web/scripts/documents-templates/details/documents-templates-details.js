/* global YAHOO, Alfresco */

if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.DocumentsTemplates = LogicECM.module.DocumentsTemplates || {};

(function () {
	LogicECM.module.DocumentsTemplates.DetailsView = function(containerId, options, messages) {
		LogicECM.module.DocumentsTemplates.DetailsView.superclass.constructor.call(this, 'LogicECM.module.DocumentsTemplates.DetailsView', containerId);
		this.setOptions(options);
		this.setMessages(messages);
		return this;
	};

	YAHOO.lang.extend(LogicECM.module.DocumentsTemplates.DetailsView, Alfresco.component.Base, {

		//TODO: обработчик события "создать шаблон"
		//TODO: обработчик события "редактировать шаблон"
		/* получаем NodeRef или itemType, загружаем форму и отображаем ее в себе */
		onReady: function() {
			console.log(this.name + '[' + this.id + '] is ready');
		}
	}, true);
})();
