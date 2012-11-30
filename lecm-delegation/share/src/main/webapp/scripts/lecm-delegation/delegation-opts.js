if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.Delegation = LogicECM.module.Delegation || {};

LogicECM.module.Delegation.DelegationOpts = LogicECM.module.Delegation.DelegationOpts || {};

(function () {

	LogicECM.module.Delegation.DelegationOpts = function (containerId) {
		return LogicECM.module.Delegation.DelegationOpts.superclass.constructor.call(
			this,
			"LogicECM.module.Delegation.DelegationOpts",
			containerId,
			["button", "container", "connection", "json", "selector"]);
	};

	/**
	 * Extend from Alfresco.component.Base
	 */
	YAHOO.lang.extend (LogicECM.module.Delegation.DelegationOpts, Alfresco.component.Base);

    /**
     * Augment prototype with main class implementation, ensuring overwrite is enabled
     */
	YAHOO.lang.augmentObject (LogicECM.module.Delegation.DelegationOpts.prototype, {

		options: {
			delegator: null //nodeRef на делегирующее лицо
		},

		onDelegationOptsFormLoaded: function (result) {
			var formEl = Dom.get(this.id + "-content");
			formEl.innerHTML = result.serverResponse.responseText;
			LogicECM.module.Base.Util.setHeight();
		},

		onReady: function () {

			Alfresco.logger.info ("A new LogicECM.module.Delegation.DelegationOpts has been created");

			//аяксовым запросом дергаем нашу ftl-ку с формой и передаем ей noreRef для загрузки данных
			//если загрузить данные не вышло то вернется форма с ошибкой, иначе - форма с параметрами делегирования указанного пользователя
			var args = {
				htmlid: this.id,
				delegator: this.options.delegator
			};
			Alfresco.util.Ajax.request ({
				method: "GET",
				url: Alfresco.constants.URL_SERVICECONTEXT + "ru/it/lecm/delegation/delegation-opts/form",
				dataObj: args,
				successCallback: {
					fn: this.onDelegationOptsFormLoaded,
					scope: this
				},
				failureMessage: "не удалось выполнить запрос, попробуйте обновить страницу",
				execScripts: true

			});
			YAHOO.util.Dom.setStyle (this.id + "-body", "visibility", "visible");
		}

	}, true);

})();
