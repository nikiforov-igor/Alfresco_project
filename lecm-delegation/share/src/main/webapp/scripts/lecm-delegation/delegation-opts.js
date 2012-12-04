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
		},

		onDelegationOptsPart1: function (result) {
			var formEl = Dom.get(this.id + "-content-part1");
			formEl.innerHTML = result.serverResponse.responseText;
		},

		onDelegationOptsPart3: function (result) {
			var formEl = Dom.get(this.id + "-content-part3");
			formEl.innerHTML = result.serverResponse.responseText;
		},

		onReady: function () {

			Alfresco.logger.info ("A new LogicECM.module.Delegation.DelegationOpts has been created");

			if (this.options.delegator) {
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

				var argsPart1 = {
					htmlid:"delegation-opts-part1",
					itemKind:"node",
					itemId: this.options.delegator,
					formId: "delegation-opts-part1",
					mode: "edit",
					showCancelButton: false,
					showResetButton: false,
					showSubmitButton: false
				};
				Alfresco.util.Ajax.request ({
					method: "GET",
					url: Alfresco.constants.URL_SERVICECONTEXT + "components/form",
					dataObj: argsPart1,
					successCallback: {
						fn: this.onDelegationOptsPart1,
						scope: this
					},
					failureMessage: "не удалось выполнить запрос, попробуйте обновить страницу",
					execScripts: true
				});

				var argsPart3 = {
					htmlid:"delegation-opts-part3",
					itemKind:"node",
					itemId: this.options.delegator,
					formId: "delegation-opts-part3",
					mode: "edit",
					showCancelButton: false,
					showResetButton: false,
					showSubmitButton: false
				}
				Alfresco.util.Ajax.request ({
					method: "GET",
					url: Alfresco.constants.URL_SERVICECONTEXT + "components/form",
					dataObj: argsPart3,
					successCallback: {
						fn: this.onDelegationOptsPart3,
						scope: this
					},
					failureMessage: "не удалось выполнить запрос, попробуйте обновить страницу",
					execScripts: true
				});
			} else {
               Alfresco.util.PopupManager.displayPrompt ({
                  title: "Ошибка отображения параметров делегирования",
                  text: "текущий пользователь не привязан к сотруднику и у него нет параметров делегирования!"
               });
			}

			YAHOO.util.Dom.setStyle (this.id + "-body", "visibility", "visible");
		}

	}, true);

})();
